package com.example.motoapp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by TOM on 2017/7/24.
 */

public class EncrypMD5 {
    public static byte[] eccrypt() throws
            NoSuchAlgorithmException {
        String strKeyIV = "24225676";//md5 iv
        // 根據 MD5 演算法生成 MessageDigest 物件(這邊選擇雜湊的方式)
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] srcBytes = strKeyIV.getBytes();
        // 使用 srcBytes 更新摘要
        md5.update(srcBytes);
        // 完成哈希計算，得到 result
        byte[] resultBytes = md5.digest();
        return resultBytes;
    }
}
