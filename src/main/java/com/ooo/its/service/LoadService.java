package com.ooo.its.service;

import com.ooo.its.entity.Order;
import com.ooo.its.entity.Passwd;
import com.ooo.its.util.SmbUtil;
import com.ooo.its.util.ZipUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoadService {

    private static final Set<String> RUNNING = ConcurrentHashMap.newKeySet();

    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private QqService qqService;

    @Autowired
    private PassWordService passWordService;

    @Value("${app.load.base-dir}")
    private String loadBaseDir;

    @Value("${smb.host}")
    private String smbHost;

    @Value("${smb.share}")
    private String smbShare;

    @Value("${smb.username}")
    private String smbUsername;

    @Value("${smb.password}")
    private String smbPassword;
    @Value("${app.load.remote-prefix:}")
    private String remotePrefix;
    @Async("loadTaskExecutor")
    public void loadAsync(String qqNumber, int batch) {
        String taskKey = qqNumber + "#" + batch;
        if (!RUNNING.add(taskKey)) {
            safeSendAdmin(String.format(
                    "备货任务重复触发，已忽略：QQ：%s，批次：%d", qqNumber, batch));
            return;
        }

        try {
            doLoad(qqNumber, batch);
        } catch (Exception e) {
            safeSendAdmin(String.format(
                    "装货发货失败：QQ：%s，批次：%d，原因：%s",
                    qqNumber, batch, e.getMessage()));
        } finally {
            RUNNING.remove(taskKey);
        }
    }

    private void doLoad(String qqNumber, int batch) throws Exception {

        List<Order> orders = orderService.FindHandleDataByBatch(qqNumber, 1, batch);
        if (orders == null || orders.isEmpty()) {
            safeSendAdmin(String.format(
                    "备货失败：QQ：%s，批次：%d，原因：没有需要装货的订单", qqNumber, batch));
            return;
        }

        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String baseDir = loadBaseDir + "/" + timestamp;
        File dir = new File(baseDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("创建文件夹失败: " + baseDir);
        }

        String password = generateRandomPassword(8);

        for (Order order : orders) {
            String remoteFilePath = goodsService.FileLocation(order.getGoodsId());
            if (remoteFilePath == null || remoteFilePath.trim().isEmpty()) {
                continue;
            }
            remoteFilePath = remoteFilePath.trim();
            remoteFilePath = remoteFilePath.replace('\\', '/');
            while (remoteFilePath.startsWith("/")) {
                remoteFilePath = remoteFilePath.substring(1);
            }
            if (remotePrefix != null && !remotePrefix.trim().isEmpty()) {
                String prefix = remotePrefix.trim().replace('\\', '/');
                while (prefix.endsWith("/")) {
                    prefix = prefix.substring(0, prefix.length() - 1);
                }
                if (!remoteFilePath.startsWith(prefix + "/")
                        && !remoteFilePath.equals(prefix)) {
                    remoteFilePath = prefix + "/" + remoteFilePath;
                }
            }
            String fileName = extractFileName(remoteFilePath);
            if (fileName.isEmpty()) {
                continue;
            }

            String localTempFile = baseDir + File.separator + fileName;
            boolean copied = SmbUtil.copyFile(
                    smbHost, smbShare, smbUsername, smbPassword,
                    remoteFilePath, localTempFile);
            if (!copied) {
                safeSendAdmin("文件下载失败：" + remoteFilePath);
                continue;
            }

            String zipFilePath = baseDir + File.separator + fileName + ".zip";
            ZipUtil.compressWithPassword(localTempFile, zipFilePath, password);
            //noinspection ResultOfMethodCallIgnored
            new File(localTempFile).delete();

            order.setIs_load(1);
            orderService.SaveOrder(order);
        }

        qqService.sendFolderToGroup(qqNumber, batch, baseDir);
        deleteDirectory(new File(baseDir));

        Passwd passwd = new Passwd();
        passwd.setQq(qqNumber);
        passwd.setBatch(batch);
        passwd.setPassword(password);
        passwd.setOk(0);
        passWordService.Save(passwd);
    }

    /**
     * 从远程路径中提取文件名（同时兼容 / 和 \）
     */
    private String extractFileName(String path) {
        if (path == null) {
            return "";
        }
        String p = path.replace('/', '\\');
        int idx = p.lastIndexOf('\\');
        return idx >= 0 ? p.substring(idx + 1) : p;
    }

    private String generateRandomPassword(int len) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void deleteDirectory(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteDirectory(child);
                }
            }
        }
        if (!file.delete()) {
            safeSendAdmin("本地临时文件删除失败：" + file.getAbsolutePath());
        }
    }

    private void safeSendAdmin(String message) {
        try {
            qqService.sendMessageToAdmin(message);
        } catch (Exception e) {
            // 忽略，避免阻塞主流程
        }
    }
}