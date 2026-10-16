package com.ooo.its.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SmbUtil {
    private static final String SMBCLIENT_BIN = "smbclient";
    private static final String SMBCLIENT_PROTOCOL = "SMB3";

    private SmbUtil() {
    }
    public static boolean copyFile(String host,
                                   String shareName,
                                   String username,
                                   String password,
                                   String remoteFilePath,
                                   String localPath) {

        if (remoteFilePath == null || remoteFilePath.trim().isEmpty()) {
            System.err.println("smbclient 下载失败：remoteFilePath 为空");
            return false;
        }

        String p = remoteFilePath.trim().replace('/', '\\');
        while (p.startsWith("\\")) {
            p = p.substring(1);
        }

        int idx = p.lastIndexOf('\\');
        String remoteDir = idx > 0 ? p.substring(0, idx) : "";
        String remoteFileName = idx >= 0 ? p.substring(idx + 1) : p;

        remoteDir = remoteDir.replace('\\', '/');

        java.io.File local = new java.io.File(localPath);
        java.io.File parent = local.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        StringBuilder script = new StringBuilder();
        if (!remoteDir.isEmpty()) {
            script.append("cd \"").append(remoteDir).append("\"; ");
        }
        script.append("get \"")
                .append(remoteFileName)
                .append("\" \"")
                .append(local.getAbsolutePath().replace('\\', '/'))
                .append("\"");
        List<String> cmd = new ArrayList<>();
        cmd.add(SMBCLIENT_BIN);
        cmd.add("//" + host + "/" + shareName);
        cmd.add("-U");
        cmd.add(username + "%" + password);
        cmd.add("-m");
        cmd.add(SMBCLIENT_PROTOCOL);
        cmd.add("-c");
        cmd.add(script.toString());

        long start = System.nanoTime();

        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process proc = pb.start();

            // 消费输出，避免管道阻塞；同时保留日志
            StringBuilder out = new StringBuilder();
            try (BufferedReader r = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = r.readLine()) != null) {
                    out.append(line).append('\n');
                }
            }

            int code = proc.waitFor();

            if (code != 0 || !local.exists() || local.length() == 0) {
                System.err.println("smbclient 下载失败，exit=" + code
                        + "，remote=" + remoteFilePath
                        + "\n--- smbclient output ---\n" + out);
                if (local.exists()) {
                    local.delete();
                }
                return false;
            }

            long totalBytes = local.length();
            double seconds = (System.nanoTime() - start) / 1_000_000_000.0;
            double mbps = seconds > 0
                    ? totalBytes / 1024.0 / 1024.0 / seconds
                    : 0;

            System.out.printf("Copy Success (smbclient): %.2f MB/s, %d bytes -> %s%n",
                    mbps, totalBytes, localPath);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}