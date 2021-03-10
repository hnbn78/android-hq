package com.ab.util;

import com.ab.util.base64.BackAES;

public class AbAes {
    
    public static final String TAG = "AbAes";
    
    public static String encrypt(String message, String password) {
        try {
            return Bytes.bytes2hexStr(BackAES.encrypt(message, password, 0), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    public static String decrypt(String message, String password) {
        try {
            return BackAES.decrypt(new String(Bytes.hexString2bytes(message)),
                    password, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    public static void main(String[] args) {
        String enc = encrypt("user123");
        String clear = decrypt(enc);
    }
    
    private static String encrypt(String str) {
        //6位随机
        String encrpied = AbAes.encrypt(str, "xdexshengx");
        return encrpied;
    }
    
    private static String decrypt(String str) {
        String encrpied = str;
        if (Strs.isEmpty(encrpied)) {
            return "";
        } else {
           
            String clear = AbAes.decrypt(encrpied, "xdexshengx");
            return clear;
        }
    }
}
