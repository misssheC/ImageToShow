package com.ooo.its.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ooo.its.entity.Order;
import com.ooo.its.entity.UserInfo;
import com.ooo.its.repository.GoodsRep;
import com.ooo.its.repository.OrderRep;
import com.ooo.its.repository.UserInfoRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


@Service
public class QqService {
    @Autowired
    private UserInfoRep userInfoRep;
    @Autowired
    private OrderRep orderRep;
    @Autowired
    private GoodsRep goodsRep;
    @Autowired
    private OrderService orderService;
    @Autowired
    private QqSendService qqSendService;

    @Value("${napcat.ws.url}")
    private String wsUrl;

    @Value("${napcat.admin-qq}")
    private String adminQQ;
    @Value("${napcat.group-id}")
    private String defaultGroupId;
    private WebSocket webSocket;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ScheduledExecutorService reconnectExecutor = Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean reconnecting = new AtomicBoolean(false);
    private static final long RECONNECT_DELAY_SECONDS = 5 * 60L;

    private final Map<String, CompletableFuture<JsonNode>> pending = new ConcurrentHashMap<>();
    private static final long TEXT_TIMEOUT_SEC = 15;
    private static final long UPLOAD_TIMEOUT_SEC = 700;
    private static final long STEP_INTERVAL_MS = 300;


    @PostConstruct
    public void connect() {
        doConnect();
    }

    private void doConnect() {
        HttpClient client = HttpClient.newHttpClient();
        CompletableFuture<WebSocket> wsFuture = client.newWebSocketBuilder()
                .buildAsync(URI.create(wsUrl), new WebSocket.Listener() {
                    @Override
                    public void onOpen(WebSocket webSocket) {
                        System.out.println("已连接 napcat WebSocket");
                        QqService.this.webSocket = webSocket;
                        reconnecting.set(false);
                        WebSocket.Listener.super.onOpen(webSocket);
                    }

                    @Override
                    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                        try {
                            JsonNode node = objectMapper.readTree(data.toString());
                            if (node.hasNonNull("echo")) {
                                String echo = node.get("echo").asText();
                                CompletableFuture<JsonNode> f = pending.remove(echo);
                                if (f != null) {
                                    f.complete(node);
                                }
                            }
                        } catch (Exception e) {
                            // 非 JSON 或事件，忽略
                        }
                        return WebSocket.Listener.super.onText(webSocket, data, last);
                    }

                    @Override
                    public void onError(WebSocket webSocket, Throwable error) {
                        System.err.println("WebSocket 异常: " + error.getMessage());
                        triggerReconnect();
                        WebSocket.Listener.super.onError(webSocket, error);
                    }

                    @Override
                    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                        System.out.println("WebSocket 已关闭: " + reason + " code:" + statusCode);
                        QqService.this.webSocket = null;
                        triggerReconnect();
                        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
                    }
                });

        try {
            this.webSocket = wsFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("WebSocket 初次连接失败，将触发重连");
            e.printStackTrace();
            triggerReconnect();
        }
    }

    private void triggerReconnect() {
        if (reconnecting.compareAndSet(false, true)) {
            System.out.println("计划 " + RECONNECT_DELAY_SECONDS / 60 + " 分钟后尝试重连 napcat ws");
            reconnectExecutor.schedule(() -> {
                System.out.println("开始执行 napcat WebSocket 重连...");
                doConnect();
            }, RECONNECT_DELAY_SECONDS, TimeUnit.SECONDS);
        } else {
            System.out.println("已有重连任务在排队，跳过本次重连");
        }
    }

    @PreDestroy
    public void disconnect() {
        reconnectExecutor.shutdownNow();
        if (webSocket != null) {
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "应用关闭");
        }
        System.out.println("QqService 资源释放完成");
    }

    public void sendMessageToAdmin(String text) {
        if (webSocket == null) {
            System.err.println("WebSocket 未连接，消息发送失败");
            return;
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("action", "send_private_msg");
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", Long.parseLong(adminQQ));
        params.put("message", text);
        payload.put("params", params);
        try {
            String json = objectMapper.writeValueAsString(payload);
            webSocket.sendText(json, true);
            System.out.println("已发送消息至 " + adminQQ);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendLoginMessage(String qqNumber, boolean success, String region, String device, String extraInfo) {
        String consumptionInfo = getConsumptionSummary(qqNumber);
        StringBuilder message = new StringBuilder();
        message.append("【登录通知】\n");
        message.append("QQ: ").append(qqNumber).append("\n");
        message.append("状态: ").append(success ? "✅ 登录成功" : "❌ 登录失败").append("\n");
        message.append("地区: ").append(region).append("\n");
        message.append("设备: ").append(device).append("\n");
        if (extraInfo != null && !extraInfo.isEmpty()) {
            message.append("登录类型: ").append(extraInfo).append("\n");
        }
        message.append(consumptionInfo);
        sendMessageToAdmin(message.toString());
    }

    @Async
    public void sendOrderMessage(String qqNumber, int batch, int orderCount) {
        String message = String.format(
                "【订单通知】\nQQ: %s\n批次: %d\n数量: %d",
                qqNumber, batch, orderCount
        );
        sendMessageToAdmin(message);
    }

    private String getConsumptionSummary(String qqNumber) {
        Optional<UserInfo> userInfoOpt = userInfoRep.findByQqNumber(qqNumber);
        if (userInfoOpt.isPresent()) {
            UserInfo user = userInfoOpt.get();
            double totalAmount = user.getLumpSum();
            Date lastTime = user.getLastTime();
            String lastTimeStr = (lastTime != null)
                    ? new SimpleDateFormat("yyyy-MM-dd").format(lastTime)
                    : "";
            int quantity = user.getPurchaseQuantity();
            return String.format("总消费：%.2f 元\n最近登录：%s\n购买数量：%d",
                    totalAmount, lastTimeStr, quantity);
        } else {
            return "未注册";
        }
    }

    /**
     * 上传结果。
     * 当 upload_file=false 时，NapCat 仅发送文件信息而不实际上传内容，
     * 返回的 data 中通常只有 file_id（可能为 null），不会有 message_id。
     * 真实的 message_id 由 QqSendService 从群消息事件中捕获。
     */
    private static class UploadResult {
        final boolean success;
        final String messageId;   // 一般为 null
        final String fileId;      // NapCat 返回的群文件标识（可能为 null）
        final JsonNode rawResponse;

        UploadResult(boolean success, String messageId, String fileId, JsonNode rawResponse) {
            this.success = success;
            this.messageId = messageId;
            this.fileId = fileId;
            this.rawResponse = rawResponse;
        }
    }

    @Async
    public void sendFolderToGroup(String qqNumber, int batch, String folderPath) {

        // 通知 QqSendService 开始记录（防止 start 事件乱序/丢失）
        qqSendService.startRecording(qqNumber, batch);

        boolean headerOk = sendGroupText(defaultGroupId, "start"+"["+qqNumber+"]" + " [" + batch+"]");
        if (!headerOk) {
            qqSendService.stopRecording();
            sendMessageToAdmin(String.format(
                    "备货失败：QQ：%s，批次：%d，原因：头部消息发送失败", qqNumber, batch));
            return;
        }
        sleep(STEP_INTERVAL_MS);

        File folder = resolveFolder(folderPath);
        if (folder == null || !folder.isDirectory()) {
            sendGroupText(defaultGroupId, "end");
            qqSendService.stopRecording();
            sendMessageToAdmin(String.format(
                    "备货失败：QQ：%s，批次：%d，原因：文件夹不存在或不是目录，目录：%s",
                    qqNumber, batch, folderPath));
            return;
        }

        File[] files = folder.listFiles(File::isFile);
        if (files == null || files.length == 0) {
            sendGroupText(defaultGroupId, "end");
            qqSendService.stopRecording();
            sendMessageToAdmin(String.format(
                    "备货失败：QQ：%s，批次：%d，原因：文件夹为空，目录：%s",
                    qqNumber, batch, folder.getAbsolutePath()));
            return;
        }

        int success = 0, failed = 0;
        List<String> failedFiles = new ArrayList<>();

        for (File f : files) {
            String fileRef = buildFileRef(f);
            UploadResult result = uploadGroupFile(defaultGroupId, fileRef, f.getName());

            if (result.success) {
                try {
                    String goodsName = stripZipSuffix(f.getName());
                    var goods = goodsRep.findByName(goodsName);
                    if (goods == null) {
                        goods = goodsRep.findByName(f.getName());
                    }
                    if (goods != null) {
                        Order order = orderRep.findByQqNumberAndBatchAndGoodsId(
                                qqNumber, batch, goods.getId());
                        if (order != null) {
                            order.setIs_load(1);
                            orderService.SaveOrder(order);
                        } else {
                            System.out.println("未找到匹配订单: qq=" + qqNumber
                                    + ", batch=" + batch + ", goodsId=" + goods.getId());
                        }
                    } else {
                        System.err.println("goodsRep.findByName 返回 null，goodsName=" + goodsName
                                + "（原始文件名=" + f.getName() + "）");
                    }
                } catch (Exception e) {
                    System.err.println("更新订单状态失败: file=" + f.getName() + ", msg=" + e.getMessage());
                }

                // GoodsKey 的 messageId 由 QqSendService 从群消息事件中写入，
                // 这里不再使用 upload 接口返回的 file_id 作为 message_id。
                // 若 upload_file=false 且 NapCat 仍返回了 file_id，可选择性记录日志。
                if (result.fileId != null) {
                    System.out.println("上传完成（未落盘），NapCat 返回 file_id=" + result.fileId
                            + "，file=" + f.getName());
                } else {
                    System.out.println("上传完成（未落盘），NapCat 未返回 file_id，file=" + f.getName());
                }

                success++;
            } else {
                failed++;
                failedFiles.add(f.getName());
            }
            sleep(STEP_INTERVAL_MS);
        }

        sendGroupText(defaultGroupId, "end");
        qqSendService.stopRecording();

        String failDetail = failedFiles.isEmpty() ? "" :
                "，失败文件：" + String.join("、", failedFiles);
        sendMessageToAdmin(String.format(
                "备货完成：QQ：%s，批次：%d，成功：%d，失败：%d%s",
                qqNumber, batch, success, failed, failDetail));
    }

    /** 去掉 .zip 后缀，用于匹配数据库 goods_name */
    private String stripZipSuffix(String fileName) {
        if (fileName == null) return null;
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".zip")) {
            return fileName.substring(0, fileName.length() - 4);
        }
        return fileName;
    }

    private String buildFileRef(File f) {
        String p = f.getAbsolutePath().replace('\\', '/');
        if (!p.startsWith("/")) {
            p = "/" + p;
        }
        return "file://" + p;
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    /**
     * 上传群文件，使用 upload_file=false 避免 Linux 下产生 Ori 缓存副本。
     *
     * NapCat v4.18.29+ 新增 upload_file 参数（默认 true）：
     * - upload_file=true  （旧行为）：NapCat 会将文件复制到本地缓存后上传，产生副本。
     * - upload_file=false （新行为）：仅发送文件信息，不执行本地上传，避免磁盘占用。
     *
     * 注意：upload_file=false 时，NapCat 可能仍返回 file_id（取决于版本），
     * 但真实的 message_id 必须从群消息事件中获取，由 QqSendService 负责记录。
     */
    private UploadResult uploadGroupFile(String groupId, String fileRef, String fileName) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("action", "upload_group_file");

        Map<String, Object> params = new HashMap<>();
        params.put("group_id", Long.parseLong(groupId));
        params.put("file", fileRef);
        params.put("name", fileName);
        params.put("folder", "");
        params.put("upload_file", false);   // ★ 关键：不落盘，避免 Ori 缓存副本
        payload.put("params", params);

        JsonNode resp = sendAndWait(payload, UPLOAD_TIMEOUT_SEC);

        if (resp == null) {
            System.out.println("[群 " + groupId + "] upload_group_file 响应: null（超时或 WebSocket 未连接），file=" + fileName);
        } else {
            System.out.println("[群 " + groupId + "] upload_group_file 响应: " + resp.toString());
        }

        if (isOk(resp)) {
            System.out.println("[群 " + groupId + "] 文件发送成功（upload_file=false，不落盘）: " + fileName);

            String messageId = null;
            String fileId = null;
            JsonNode data = resp.path("data");
            if (data != null && !data.isNull() && !data.isMissingNode()) {
                if (data.hasNonNull("message_id")) {
                    messageId = data.get("message_id").asText();
                }
                if (data.hasNonNull("file_id")) {
                    fileId = data.get("file_id").asText();
                }
            }
            return new UploadResult(true, messageId, fileId, resp);
        } else {
            String ret = resp == null ? "超时/无响应"
                    : ("retcode=" + resp.path("retcode").asInt()
                    + ", msg=" + resp.path("message").asText()
                    + ", wording=" + resp.path("wording").asText());
            System.err.println("[群 " + groupId + "] 文件发送失败: " + fileName + " -> " + ret);
            return new UploadResult(false, null, null, resp);
        }
    }

    private File resolveFolder(String path) {
        if (path == null || path.isBlank()) return null;
        try {
            if (path.startsWith("file:")) {
                return new File(URI.create(path));
            }
            if (path.startsWith("http://") || path.startsWith("https://")) {
                File tmp = Files.createTempDirectory("ship-").toFile();
                System.err.println("暂不自动下载 http 目录: " + path);
                return null;
            }
            return new File(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JsonNode sendAndWait(Map<String, Object> payload, long timeoutSec) {
        if (webSocket == null) {
            System.err.println("WebSocket 未连接，无法发送: " + payload.get("action"));
            return null;
        }
        String echo = UUID.randomUUID().toString();
        payload.put("echo", echo);

        CompletableFuture<JsonNode> future = new CompletableFuture<>();
        pending.put(echo, future);

        try {
            String json = objectMapper.writeValueAsString(payload);
            webSocket.sendText(json, true);
            return future.get(timeoutSec, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            System.err.println("等待回执超时: action=" + payload.get("action") + " echo=" + echo);
            pending.remove(echo);
            return null;
        } catch (Exception e) {
            System.err.println("发送异常: action=" + payload.get("action"));
            e.printStackTrace();
            pending.remove(echo);
            return null;
        }
    }

    private boolean isOk(JsonNode resp) {
        return resp != null
                && resp.has("retcode")
                && resp.get("retcode").asInt(-1) == 0;
    }

    private boolean sendGroupText(String groupId, String text) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("action", "send_group_msg");

        Map<String, Object> params = new HashMap<>();
        params.put("group_id", Long.parseLong(groupId));
        params.put("message", text);
        payload.put("params", params);

        JsonNode resp = sendAndWait(payload, TEXT_TIMEOUT_SEC);
        boolean ok = isOk(resp);
        System.out.println("[群 " + groupId + "] 文本发送" + (ok ? "成功" : "失败") + ": " + text);
        return ok;
    }
}