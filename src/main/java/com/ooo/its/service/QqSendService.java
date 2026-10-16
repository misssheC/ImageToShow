package com.ooo.its.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ooo.its.entity.GoodsKey;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class QqSendService {

    private static final Pattern START_PATTERN =
            Pattern.compile("^start\\s*\\[(\\d+)\\]\\s*\\[(\\d+)\\]$");

    private static final String END_FLAG = "end";
    private static final long RECONNECT_DELAY_SECONDS = 60;
    private static final long TEXT_TIMEOUT_SEC = 15;
    private static final long FORWARD_TIMEOUT_SEC = 30;

    @Value("${napcat.sender.ws.url}")
    private String wsUrl;

    /** 如果配置了，只处理该群的事件；为空则不限制 */
    @Value("${napcat.group-id:}")
    private String defaultGroupId;

    @Autowired
    private GoodsKeyService goodsKeyService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ScheduledExecutorService reconnectExecutor =
            Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean reconnecting = new AtomicBoolean(false);

    private volatile WebSocket webSocket;

    /** 记录状态 */
    private final AtomicBoolean recording = new AtomicBoolean(false);
    private volatile String currentQq;
    private volatile int currentBatch;

    /** 等待 echo 回执 */
    private final Map<String, CompletableFuture<JsonNode>> pending = new ConcurrentHashMap<>();

    /** 累积 WebSocket 分片 */
    private final StringBuilder textBuffer = new StringBuilder();

    @PostConstruct
    public void connect() {
        doConnect();
    }

    private void doConnect() {
        HttpClient client = HttpClient.newHttpClient();
        CompletableFuture<WebSocket> future = client.newWebSocketBuilder()
                .buildAsync(URI.create(wsUrl), new WebSocket.Listener() {

                    @Override
                    public void onOpen(WebSocket ws) {
                        webSocket = ws;
                        reconnecting.set(false);
                        System.out.println("[QqSendService] 已连接 " + wsUrl);
                        WebSocket.Listener.super.onOpen(ws);
                    }

                    @Override
                    public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
                        handleFrame(data, last);
                        return WebSocket.Listener.super.onText(ws, data, last);
                    }

                    @Override
                    public CompletionStage<?> onBinary(WebSocket ws, ByteBuffer data, boolean last) {
                        byte[] bytes = new byte[data.remaining()];
                        data.get(bytes);
                        handleFrame(new String(bytes, StandardCharsets.UTF_8), last);
                        return WebSocket.Listener.super.onBinary(ws, data, last);
                    }

                    @Override
                    public void onError(WebSocket ws, Throwable error) {
                        System.err.println("[QqSendService] WebSocket 异常: " + error.getMessage());
                        webSocket = null;
                        // 清掉可能残留的半截分片，避免污染重连后的消息
                        synchronized (textBuffer) {
                            textBuffer.setLength(0);
                        }
                        triggerReconnect();
                        WebSocket.Listener.super.onError(ws, error);
                    }

                    @Override
                    public CompletionStage<?> onClose(WebSocket ws, int statusCode, String reason) {
                        System.out.println("[QqSendService] WebSocket 关闭: " + reason
                                + " code:" + statusCode);
                        webSocket = null;
                        // 清掉可能残留的半截分片
                        synchronized (textBuffer) {
                            textBuffer.setLength(0);
                        }
                        triggerReconnect();
                        return WebSocket.Listener.super.onClose(ws, statusCode, reason);
                    }
                });

        try {
            this.webSocket = future.get();
        } catch (Exception e) {
            System.err.println("[QqSendService] 初次连接失败，稍后重连: " + e.getMessage());
            triggerReconnect();
        }
    }

    /**
     * 处理单个 WebSocket 帧：
     * - 累积分片，直到 last == true
     * - 完整 JSON 后：先看 echo 回执，再看事件
     * - 非 JSON：静默忽略
     * - 出错：清空缓存，避免脏数据污染后续消息
     */
    private void handleFrame(CharSequence data, boolean last) {
        try {
            String fullText;
            synchronized (textBuffer) {
                textBuffer.append(data);
                if (!last) {
                    return;
                }
                fullText = textBuffer.toString();
                textBuffer.setLength(0);
            }

            JsonNode node;
            try {
                node = objectMapper.readTree(fullText);
            } catch (Exception parseEx) {
                // 非 JSON（心跳/日志等），静默忽略
                // 排查时临时打开下面这行：
                // System.err.println("[QqSendService] 非 JSON 帧，忽略: " + fullText);
                return;
            }

            // echo 回执优先处理
            if (node.hasNonNull("echo")) {
                String echo = node.get("echo").asText();
                CompletableFuture<JsonNode> f = pending.remove(echo);
                if (f != null) f.complete(node);
                return;
            }

            // 事件处理
            handleMessage(node);

        } catch (Exception e) {
            System.err.println("[QqSendService] 处理消息异常: " + e.getMessage() + " | last=" + last);
            synchronized (textBuffer) {
                textBuffer.setLength(0);
            }
        }
    }

    private void triggerReconnect() {
        if (reconnecting.compareAndSet(false, true)) {
            System.out.println("[QqSendService] " + RECONNECT_DELAY_SECONDS
                    + " 秒后尝试重连...");
            reconnectExecutor.schedule(() -> {
                System.out.println("[QqSendService] 开始执行重连");
                doConnect();
            }, RECONNECT_DELAY_SECONDS, TimeUnit.SECONDS);
        }
    }

    @PreDestroy
    public void disconnect() {
        reconnectExecutor.shutdownNow();
        WebSocket ws = this.webSocket;
        if (ws != null) {
            try {
                ws.sendClose(WebSocket.NORMAL_CLOSURE, "应用关闭");
            } catch (Exception ignored) {
            }
        }
        synchronized (textBuffer) {
            textBuffer.setLength(0);
        }
        System.out.println("[QqSendService] 资源释放完成");
    }

    // ---------------------------------------------------------------------
    // 对外方法：允许 QqService 显式触发/停止
    // ---------------------------------------------------------------------

    public void startRecording(String qqNumber, int batch) {
        this.currentQq = qqNumber;
        this.currentBatch = batch;
        this.recording.set(true);
        System.out.println("[QqSendService] 手动开始记录: qq=" + qqNumber + ", batch=" + batch);
    }

    public void stopRecording() {
        this.recording.set(false);
        System.out.println("[QqSendService] 手动停止记录");
    }

    public boolean isRecording() {
        return recording.get();
    }

    // ---------------------------------------------------------------------
    // 消息处理
    // ---------------------------------------------------------------------

    private void handleMessage(JsonNode node) {
        if (node == null) return;

        // 只处理群消息事件
        if (!"message".equals(node.path("post_type").asText())) return;
        if (!"group".equals(node.path("message_type").asText())) return;

        // 如果配置了群号，做一下过滤
        if (defaultGroupId != null && !defaultGroupId.isBlank()) {
            long gid = node.path("group_id").asLong(0);
            if (!String.valueOf(gid).equals(defaultGroupId)) return;
        }

        String raw = node.path("raw_message").asText("");
        String trimmed = raw == null ? "" : raw.trim();

        // 命中 start[qq] [batch]
        Matcher matcher = START_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            currentQq = matcher.group(1);
            currentBatch = Integer.parseInt(matcher.group(2));
            recording.set(true);
            System.out.println("[QqSendService] 收到 start，开始记录: qq="
                    + currentQq + ", batch=" + currentBatch);
            return;
        }

        // 命中 end
        if (END_FLAG.equalsIgnoreCase(trimmed)) {
            if (recording.get()) {
                System.out.println("[QqSendService] 收到 end，停止记录: qq="
                        + currentQq + ", batch=" + currentBatch);
            }
            recording.set(false);
            currentQq = null;
            return;
        }

        // 未处于记录状态，忽略
        if (!recording.get()) return;

        // 取真实 message_id（数字会 asText 成字符串）
        String messageId = node.path("message_id").asText(null);
        if (messageId == null || messageId.isBlank()) {
            System.err.println("[QqSendService] message_id 缺失，跳过本条消息");
            return;
        }

        // 遍历 message 段，找 type=file
        JsonNode messageArr = node.path("message");
        if (!messageArr.isArray()) return;

        for (JsonNode seg : messageArr) {
            if (!"file".equals(seg.path("type").asText())) continue;

            JsonNode data = seg.path("data");
            String fileName = data.path("file").asText(null);
            String fileId = data.path("file_id").asText(null);

            if (fileName == null || fileName.isBlank()) {
                System.err.println("[QqSendService] 文件名为空，跳过: messageId=" + messageId);
                continue;
            }

            try {
                GoodsKey key = new GoodsKey();
                key.setQqNumber(currentQq);
                key.setBatch(currentBatch);
                key.setMessageId(messageId);
                key.setFilename(fileName);
                goodsKeyService.save(key);

                System.out.println("[QqSendService] 保存 GoodsKey 成功: "
                        + "qq=" + currentQq
                        + ", batch=" + currentBatch
                        + ", messageId=" + messageId
                        + ", fileId=" + fileId
                        + ", filename=" + fileName);
            } catch (Exception e) {
                System.err.println("[QqSendService] 保存 GoodsKey 失败: file="
                        + fileName + ", msg=" + e.getMessage());
            }
        }
    }

    // ---------------------------------------------------------------------
    // 发送/回执
    // ---------------------------------------------------------------------

    /** 发送并等待 echo 回执 */
    private JsonNode sendAndWait(Map<String, Object> payload, long timeoutSec) {
        WebSocket ws = this.webSocket;
        if (ws == null) {
            System.err.println("[QqSendService] WebSocket 未连接，无法发送: " + payload.get("action"));
            return null;
        }
        String echo = UUID.randomUUID().toString();
        payload.put("echo", echo);

        CompletableFuture<JsonNode> future = new CompletableFuture<>();
        pending.put(echo, future);
        try {
            ws.sendText(objectMapper.writeValueAsString(payload), true);
            return future.get(timeoutSec, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.err.println("[QqSendService] 发送异常: action=" + payload.get("action")
                    + ", msg=" + e.getMessage());
            pending.remove(echo);
            return null;
        }
    }

    private boolean isOk(JsonNode resp) {
        return resp != null && resp.has("retcode") && resp.get("retcode").asInt(-1) == 0;
    }

    private boolean isNumeric(String s) {
        if (s == null || s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }

    // ---------------------------------------------------------------------
    // 转发
    // ---------------------------------------------------------------------

    /**
     * 把一条群消息（文件消息）转发给指定好友。
     * 使用 NapCat 的 forward_friend_single_msg，参数是 message_id 和 user_id。
     *
     * @param targetQq    接收方 QQ
     * @param messageId   群消息事件里的真实 message_id（必须是纯数字）
     * @param fileName    仅用于日志
     */
    public boolean forwardMessageToUser(String targetQq, String messageId, String fileName) {
        if (webSocket == null) {
            System.err.println("[QqSendService] WebSocket 未连接，无法转发: messageId=" + messageId);
            return false;
        }
        if (targetQq == null || targetQq.isBlank()) {
            System.err.println("[QqSendService] 目标 QQ 为空，跳过转发");
            return false;
        }
        if (messageId == null || messageId.isBlank()) {
            System.err.println("[QqSendService] messageId 为空，跳过转发，file=" + fileName);
            return false;
        }
        if (!isNumeric(messageId)) {
            // 非数字 messageId（可能是历史数据里存的 file_id），跳过并记录
            System.err.println("[QqSendService] messageId 非数字，跳过转发: "
                    + messageId + " (file=" + fileName + ")");
            return false;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("action", "forward_friend_single_msg");

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", Long.parseLong(targetQq));
        params.put("message_id", Long.parseLong(messageId));
        // 如果 NapCat 版本需要 group_id，也可以补上：
        // if (defaultGroupId != null && !defaultGroupId.isBlank()) {
        //     params.put("group_id", Long.parseLong(defaultGroupId));
        // }
        payload.put("params", params);

        JsonNode resp = sendAndWait(payload, FORWARD_TIMEOUT_SEC);
        boolean ok = isOk(resp);

        if (ok) {
            System.out.println("[QqSendService] 转发成功: " + fileName
                    + " -> QQ " + targetQq + " (messageId=" + messageId + ")");
        } else {
            String ret = resp == null ? "超时/无响应"
                    : ("retcode=" + resp.path("retcode").asInt()
                    + ", msg=" + resp.path("message").asText()
                    + ", wording=" + resp.path("wording").asText());
            System.err.println("[QqSendService] 转发失败: " + fileName
                    + " -> QQ " + targetQq + ", messageId=" + messageId + ", " + ret);
        }
        return ok;
    }
}