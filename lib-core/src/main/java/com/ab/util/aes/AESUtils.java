package com.ab.util.aes;

import com.ab.util.base64.AESType;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/*******************************************************************************
 * AES加解密算法
 *
 *
 */

public class AESUtils {

    public static final String bm = "utf-8";
    private static String WAYS = "AES";
    private static String MODE = "";
    private static String IV = "1234567890123456";
    private static int AES_SIZE = 128;
    private static int pwdLength = 16;
    private static String defaultPwd = null;

    private static String ModeCode = "PKCS5Padding";

    private static int type = 0;// 默认

    public static String selectMod(int type) {
        // ECB("ECB", "0"), CBC("CBC", "1"), CFB("CFB", "2"), OFB("OFB", "3");
        switch (type) {
            case 0:
                MODE = WAYS + "/" + AESType.ECB.key() + "/" + ModeCode;

                break;
            case 1:

                MODE = WAYS + "/" + AESType.CBC.key() + "/" + ModeCode;
                break;
            case 2:

                MODE = WAYS + "/" + AESType.CFB.key() + "/" + ModeCode;
                break;
            case 3:
                MODE = WAYS + "/" + AESType.OFB.key() + "/" + ModeCode;
                break;

        }

        return MODE;

    }

    /**
     * 字节数组转化为大写16进制字符串
     *
     * @param b
     * @return
     */
    private static String byte2HexStr(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            String s = Integer.toHexString(b[i] & 0xFF);
            if (s.length() == 1) {
                sb.append("0");
            }

            sb.append(s.toUpperCase());
        }

        return sb.toString();
    }

    /**
     * 16进制字符串转字节数组
     *
     * @param s
     * @return
     */
    private static byte[] str2ByteArray(String s) {
        int byteArrayLength = s.length() / 2;
        byte[] b = new byte[byteArrayLength];
        for (int i = 0; i < byteArrayLength; i++) {
            byte b0 = (byte) Integer.valueOf(s.substring(i * 2, i * 2 + 2), 16)
                    .intValue();
            b[i] = b0;
        }

        return b;
    }


    /**
     * AES 加密
     *
     * @param content  明文
     * @param password 生成秘钥的关键字
     * @return
     */

    public static String aesEncrypt(String content, String password) {
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(IV.getBytes());
            SecretKeySpec key = new SecretKeySpec(password.getBytes(), WAYS);
            Cipher cipher = Cipher.getInstance(selectMod(type));
            if (type == 0) {
                cipher.init(Cipher.ENCRYPT_MODE, key);
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, key,zeroIv);
            }

            byte[] encryptedData = cipher.doFinal(content.getBytes(bm));

            return byte2HexStr(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES 解密
     *
     * @param content  密文
     * @param password 生成秘钥的关键字
     * @return
     */
    public static String aesDecrypt(String content, String password) {
        try {
            byte[] byteMi = str2ByteArray(content);
            IvParameterSpec zeroIv = new IvParameterSpec(IV.getBytes());
            SecretKeySpec key = new SecretKeySpec(password.getBytes(), WAYS);
            Cipher cipher = Cipher.getInstance(selectMod(type));
            if (type == 0) {
                cipher.init(Cipher.DECRYPT_MODE, key);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, key,zeroIv);
            }

            byte[] decryptedData = cipher.doFinal(byteMi);
            return new String(decryptedData, StandardCharsets.UTF_8);
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }catch(InvalidKeyException e){
            e.printStackTrace();
        }catch(InvalidAlgorithmParameterException e){
            e.printStackTrace();
        }catch(NoSuchPaddingException e){
            e.printStackTrace();
        }catch(BadPaddingException e){
            e.printStackTrace();
        }catch(IllegalBlockSizeException e){
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        /** 数据初始化 **/
        String content = "加密内容";
        String password = "9876543210123456";
        /** 加密 **/
        System.out.println("加密前：" + content);

        String encryptResultStr = aesEncrypt(content, password);
        System.out.println("加密后：" + encryptResultStr);
        /** 解密 **/
        String decryptString = aesDecrypt(encryptResultStr,password );
        System.out.println("解密后：" + decryptString);
    }
}