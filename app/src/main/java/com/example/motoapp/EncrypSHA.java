package com.example.motoapp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by TOM on 2017/7/24.
 */

public class EncrypSHA {
    public static byte[] eccrypt() throws NoSuchAlgorithmException {
        String strKeyKEY = "Shinda999";//sha256 key
        // 根據 sha256 演算法生成 MessageDigest 物件(這邊選擇要雜湊的方式)
        MessageDigest sha = MessageDigest.getInstance("SHA256");
        byte[] srcBytes = strKeyKEY.getBytes();
        // 使用 srcBytes 更新摘要
        sha.update(srcBytes);
        // 完成哈希計算，得到 result
        byte[] resultBytes = sha.digest();
        return resultBytes;
    }
}
