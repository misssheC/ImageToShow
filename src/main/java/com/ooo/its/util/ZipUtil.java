package com.ooo.its.util;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;

public class ZipUtil {
    public static void compressWithPassword(String sourceFile, String zipFile, String password) throws Exception {
        ZipParameters params = new ZipParameters();
        params.setEncryptFiles(true);
        params.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
        ZipFile zip = new ZipFile(zipFile, password.toCharArray());
        zip.addFile(new File(sourceFile), params);
    }
}
