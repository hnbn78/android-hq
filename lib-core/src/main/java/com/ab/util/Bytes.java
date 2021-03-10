package com.ab.util;

/**
 * @author Leo
 */

import java.util.Locale;

/**
 * @author Leo
 *
 */
public class Bytes {
    /**
     * 16进制字符串转字节
     */
    public static byte[] hexString2bytes(String str) {
        
        byte[] b = new byte[0];
        
        str = str.replaceAll("0[xX]", "").replaceAll("\\s+", "");
        int len = str.length();
        if (len == 0 || len % 2 == 1) {
            return b;
        }
        b = new byte[len / 2];
        
        for (int i = 0; i < str.length(); i += 2) {
            b[i / 2] = (byte) Integer.decode("0X" + str.substring(i, i + 2))
                    .intValue();
        }
        return b;
    }
    
    /**
     *
     * @param b
     * @param is2step 每8位十六进制字符串加空格
     * @return
     */
    public static String bytes2hexStr(byte[] b, boolean is2step) {
        StringBuffer sb = new StringBuffer();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                sb.append("0" + stmp);
            } else {
                sb.append(stmp);
            }
        }
        int step = sb.length() / 2;
        if (step >= 1 && is2step) {
            for (int i = 0; i < step; i++) {
                sb.insert(2 * (i + 1) + i, " ");
            }
            if (sb.toString().substring(sb.length() - 1).equals(" ")) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        return sb.toString();
    }
    
    /**
     *
     * @param b
     * @return
     */
    public static String byte2hex(byte b) {
        byte[] arrBit = new byte[]{b};
        StringBuffer sb = new StringBuffer();
        String stmp = "";
        for (int n = 0; n < arrBit.length; n++) {
            stmp = Integer.toHexString(arrBit[n] & 0XFF);
            if (stmp.length() == 1) {
                sb.append("0" + stmp);
            } else {
                sb.append(stmp);
            }
        }
        return sb.toString();
    }
    
    /**
     * 数字字符串转ASCII码字符串
     *            字符串
     * @return ASCII字符串
     */
    public static String StringToAsciiString(String content) {
        String result = "";
        int max = content.length();
        for (int i = 0; i < max; i++) {
            char c = content.charAt(i);
            String b = Integer.toHexString(c);
            result = result + b;
        }
        return result;
    }
    
    /**
     * byte数组转ASCII码字符串
     *  0x32 0x30 0x31 0x37 到 ”2017“
     * @return ASCII字符串
     */
    public static String bytesToAsciiString(byte[] content) {
        StringBuffer result = new StringBuffer();
        int max = content.length;
        for (int i = 0; i < max; i++) {
            char c = (char)content[i];
            result.append(c);
        }
        return result.toString();
    }
    
    /**
     * 转化字符串的二进制数组到ascii的二进制数组
     * 如"2017".getBytes 到  0x32 0x30 0x31 0x37
     * @return ASCII字符串
     */
    public static byte [] strBytesToAsciiBytes(byte[] content) {
        byte [] result = new byte [content.length];
        int max = content.length;
        for (int i = 0; i < max; i++) {
            char c = (char)content[i];
            String s = String.valueOf(c);
            int t = Integer.parseInt(s);
            result[i] = byteToAsciiByte(t);
        }
        return result;
    }
    
    /**
     * 把数字变成字节的形式
     * @param num 1-99之间的数字
     * @return  15 -> byte(0x15)  18 -> byte(0x18)
     */
    public static byte byteToAsciiByte(int num){
        int asciinum = 30;
        switch(num){
            case 0:
                asciinum = 30;
                break;
            case 1:
                asciinum = 31;
                break;
            case 2:
                asciinum = 32;
                break;
            case 3:
                asciinum = 33;
                break;
            case 4:
                asciinum = 34;
                break;
            case 5:
                asciinum = 35;
                break;
            case 6:
                asciinum = 36;
                break;
            case 7:
                asciinum = 37;
                break;
            case 8:
                asciinum = 38;
                break;
            case 9:
                asciinum = 39;
                break;
            default:
                asciinum = 30;
                break;
        }
        
        int first = Integer.parseInt(("" + asciinum).substring(0, 1));
        int second = Integer.parseInt(("" + asciinum).substring(1, 2));
        
        return (byte)(first*16 + second);
        
    }
    
    /**
     * 十六进制转字符串
     *
     * @param hexString
     *            十六进制字符串
     * @param encodeType
     *            编码类型4：Unicode，2：普通编码
     * @return 字符串
     */
    public static String hexStringToString(String hexString, int encodeType) {
        String result = "";
        int max = hexString.length() / encodeType;
        for (int i = 0; i < max; i++) {
            char c = (char) hexStringToAlgorism(hexString
                    .substring(i * encodeType, (i + 1) * encodeType));
            result += c;
        }
        return result;
    }
    
    /**
     * 十六进制字符串装十进制
     *
     * @param hex
     *            十六进制字符串
     * @return 十进制数值
     */
    public static int hexStringToAlgorism(String hex) {
        hex = hex.toUpperCase();
        int max = hex.length();
        int result = 0;
        for (int i = max; i > 0; i--) {
            char c = hex.charAt(i - 1);
            int algorism = 0;
            if (c >= '0' && c <= '9') {
                algorism = c - '0';
            } else {
                algorism = c - 55;
            }
            result += Math.pow(16, max - i) * algorism;
        }
        return result;
    }
    
    /**
     * 十六转二进制
     *
     * @param hex
     *            十六进制字符串
     * @return 二进制字符串
     */
    public static String hexToBinary(String hex) {
        hex = hex.toUpperCase();
        String result = "";
        int max = hex.length();
        for (int i = 0; i < max; i++) {
            char c = hex.charAt(i);
            switch (c) {
                case '0':
                    result += "0000";
                    break;
                case '1':
                    result += "0001";
                    break;
                case '2':
                    result += "0010";
                    break;
                case '3':
                    result += "0011";
                    break;
                case '4':
                    result += "0100";
                    break;
                case '5':
                    result += "0101";
                    break;
                case '6':
                    result += "0110";
                    break;
                case '7':
                    result += "0111";
                    break;
                case '8':
                    result += "1000";
                    break;
                case '9':
                    result += "1001";
                    break;
                case 'A':
                    result += "1010";
                    break;
                case 'B':
                    result += "1011";
                    break;
                case 'C':
                    result += "1100";
                    break;
                case 'D':
                    result += "1101";
                    break;
                case 'E':
                    result += "1110";
                    break;
                case 'F':
                    result += "1111";
                    break;
            }
        }
        return result;
    }
    
    /**
     * ASCII码字符串转数字字符串
     *            ASCII字符串
     * @return 字符串
     */
    public static String AsciiStringToString(String content) {
        String result = "";
        int length = content.length() / 2;
        for (int i = 0; i < length; i++) {
            String c = content.substring(i * 2, i * 2 + 2);
            int a = hexStringToAlgorism(c);
            char b = (char) a;
            String d = String.valueOf(b);
            result += d;
        }
        return result;
    }
    
    /**
     * 将十进制转换为指定长度的十六进制字符串
     *
     * @param algorism
     *            int 十进制数字
     * @param maxLength
     *            int 转换后的十六进制字符串长度
     * @return String 转换后的十六进制字符串
     */
    public static String algorismToHEXString(int algorism, int maxLength) {
        String result = "";
        result = Integer.toHexString(algorism);
        
        if (result.length() % 2 == 1) {
            result = "0" + result;
        }
        return patchHexString(result.toUpperCase(), maxLength);
    }
    
    /**
     * 字节数组转为普通字符串（ASCII对应的字符）
     *
     * @param bytearray
     *            byte[]
     * @return String
     */
    public static String bytetoString(byte[] bytearray) {
        String result = "";
        char temp;
        
        int length = bytearray.length;
        for (int i = 0; i < length; i++) {
            temp = (char) bytearray[i];
            result += temp;
        }
        return result;
    }
    
    /**
     * 二进制字符串转十进制
     *
     * @param binary
     *            二进制字符串
     * @return 十进制数值
     */
    public static int binaryToAlgorism(String binary) {
        int max = binary.length();
        int result = 0;
        for (int i = max; i > 0; i--) {
            char c = binary.charAt(i - 1);
            int algorism = c - '0';
            result += Math.pow(2, max - i) * algorism;
        }
        return result;
    }
    
    /**
     * 十进制转换为十六进制字符串
     *
     * @param algorism
     *            int 十进制的数字
     * @return String 对应的十六进制字符串
     */
    public static String algorismToHEXString(int algorism) {
        String result = "";
        result = Integer.toHexString(algorism);
        
        if (result.length() % 2 == 1) {
            result = "0" + result;
            
        }
        result = result.toUpperCase();
        
        return result;
    }
    
    /**
     * HEX字符串前补0，主要用于长度位数不足。
     *
     * @param str
     *            String 需要补充长度的十六进制字符串
     * @param maxLength
     *            int 补充后十六进制字符串的长度
     * @return 补充结果
     */
    static public String patchHexString(String str, int maxLength) {
        String temp = "";
        for (int i = 0; i < maxLength - str.length(); i++) {
            temp = "0" + temp;
        }
        str = (temp + str).substring(0, maxLength);
        return str;
    }
    
    /**
     * 将一个字符串转换为int
     *
     * @param s
     *            String 要转换的字符串
     * @param defaultInt
     *            int 如果出现异常,默认返回的数字
     * @param radix
     *            int 要转换的字符串是什么进制的,如16 8 10.
     * @return int 转换后的数字
     */
    public static int parseToInt(String s, int defaultInt, int radix) {
        int i = 0;
        try {
            i = Integer.parseInt(s, radix);
        } catch (NumberFormatException ex) {
            i = defaultInt;
        }
        return i;
    }
    
    /**
     * 将一个十进制形式的数字字符串转换为int
     *
     * @param s
     *            String 要转换的字符串
     * @param defaultInt
     *            int 如果出现异常,默认返回的数字
     * @return int 转换后的数字
     */
    public static int parseToInt(String s, int defaultInt) {
        int i = 0;
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            i = defaultInt;
        }
        return i;
    }
    
    /**
     * 十六进制字符串转为Byte数组,每两个十六进制字符转为一个Byte
     *
     * @param hex
     *            十六进制字符串
     * @return byte 转换结果
     */
    public static byte[] hexToBytes(String hex) {
        int max = hex.length() / 2;
        byte[] bytes = new byte[max];
        String binarys = hexToBinary(hex);
        for (int i = 0; i < max; i++) {
            bytes[i] = (byte) binaryToAlgorism(binarys.substring(
                    i * 8 + 1, (i + 1) * 8));
            if (binarys.charAt(8 * i) == '1') {
                bytes[i] = (byte) (0 - bytes[i]);
            }
        }
        return bytes;
    }
//    /**
//     * 十六进制串转化为byte数组
//     * 
//     * @return the array of byte
//     */
//    public static final byte[] hex2byte(String hex)
//            throws IllegalArgumentException {
//        if (hex.length() % 2 != 0) {
//            throw new IllegalArgumentException();
//        }
//        char[] arr = hex.toCharArray();
//        byte[] b = new byte[hex.length() / 2];
//        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
//            String swap = "" + arr[i++] + arr[i];
//            int byteint = Integer.parseInt(swap, 16) & 0xFF;
//            b[j] = new Integer(byteint).byteValue();
//        }
//        return b;
//    }
    
    /**
     * 字节数组转换为十六进制字符串
     *
     * @param b
     *            byte[] 需要转换的字节数组
     * @return String 十六进制字符串
     */
    public static final String byte2hex(byte b[]) {
        if (b == null) {
            throw new IllegalArgumentException(
                    "Argument b ( byte array ) is null! ");
        }
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xff);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }
    
    /**
     * 转换字节为十六进制
     * @param src 字符串数据
     * @param size 长度
     * @return String 16进制字符串
     */
    public static String bytesToHexString(byte[] src, int size) {
        String ret = "";
        if (src == null || size <= 0) {
            return null;
        }
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(src[i] & 0xFF);
            if (hex.length() < 2) {
                hex = "0" + hex;
            }
            ret += hex;
        }
        return ret.toUpperCase(Locale.US);
    }
    
    /**
     * 将short转成byte[2]， 高位为索引0
     * @param a
     * @return
     */
    public static byte[] short2Bytes(short a) {
        byte[] b = new byte[2];
        
        b[0] = (byte) (a >> 8);
        b[1] = (byte) (a);
        
        return b;
    }
    
    /**
     * 将short转成byte[2]
     * @param a
     * @param b
     * @param offset b中的偏移量
     */
    public static void short2Byte(short a, byte[] b, int offset) {
        b[offset] = (byte) (a >> 8);
        b[offset + 1] = (byte) (a);
    }
    
    
    /**
     * 将byte[2]转换成short
     * @param bs
     * @return
     */
    public static short bytes2Short(byte ... bs) {
        return (short) (((bs[0] & 0xff) << 8) | (bs[1] & 0xff));
    }
    
    /**
     * 将byte[2]转换成short
     * @param b
     * @param offset
     * @return
     */
    public static short bytes2Short(byte[] b, int offset) {
        return (short) (((b[offset] & 0xff) << 8) | (b[offset + 1] & 0xff));
    }
    
    /**
     * long转byte[8]
     *
     * @param a
     * @param b
     * @param offset
     *            b的偏移量
     */
    public static void long2Byte(long a, byte[] b, int offset) {
        b[offset + 0] = (byte) (a >> 56);
        b[offset + 1] = (byte) (a >> 48);
        b[offset + 2] = (byte) (a >> 40);
        b[offset + 3] = (byte) (a >> 32);
        
        b[offset + 4] = (byte) (a >> 24);
        b[offset + 5] = (byte) (a >> 16);
        b[offset + 6] = (byte) (a >> 8);
        b[offset + 7] = (byte) (a);
    }
    
    /**
     * byte[8]转long
     *
     * @param b
     * @param offset
     *            b的偏移量
     * @return
     */
    public static long byte2Long(byte[] b, int offset) {
        return ((((long) b[offset + 0] & 0xff) << 56)
                | (((long) b[offset + 1] & 0xff) << 48)
                | (((long) b[offset + 2] & 0xff) << 40)
                | (((long) b[offset + 3] & 0xff) << 32)
                
                | (((long) b[offset + 4] & 0xff) << 24)
                | (((long) b[offset + 5] & 0xff) << 16)
                | (((long) b[offset + 6] & 0xff) << 8)
                | (((long) b[offset + 7] & 0xff) << 0));
    }
    
    /**
     * byte[8]转long
     *
     * @param b
     * @return
     */
    public static long byte2Long(byte[] b) {
        return
                ((b[0] & 0xff) << 56) |
                        ((b[1] & 0xff) << 48) |
                        ((b[2] & 0xff) << 40) |
                        ((b[3] & 0xff) << 32) |
                        
                        ((b[4] & 0xff) << 24) |
                        ((b[5] & 0xff) << 16) |
                        ((b[6] & 0xff) << 8) |
                        (b[7] & 0xff);
    }
    
    /**
     * long转byte[8]
     *
     * @param a
     * @return
     */
    public static byte[] long2Byte(long a) {
        byte[] b = new byte[4 * 2];
        
        b[0] = (byte) (a >> 56);
        b[1] = (byte) (a >> 48);
        b[2] = (byte) (a >> 40);
        b[3] = (byte) (a >> 32);
        
        b[4] = (byte) (a >> 24);
        b[5] = (byte) (a >> 16);
        b[6] = (byte) (a >> 8);
        b[7] = (byte) (a >> 0);
        
        return b;
    }
    
    /**
     * 无符号byte数组转int b
     *
     * @param bs
     * @return
     */
    public static int ubytes2Int(byte ... bs) {
        /*int i = 0;
        for (int j = bs.length - 1; j >= 0; j--) {
            i = i | ((bs[j] & 0xff)<<(bs.length - 1 -j)*8);
        }
        return i;*/
        int mask=0xff;
        int temp=0;
        int n=0;
        for(int i=0;i<bs.length;i++){
            n<<=8;
            temp=bs[i]&mask;
            n|=temp;
        }
        return n;
    }
    
    /**
     * byte数组转int
     *
     * @param b
     * @return
     */
    public static int ubyte2Int(byte b) {
        return b & 0xff;
    }
    
    /**
     * byte数组转int
     *
     * @param bs
     * @param offset
     * @return
     */
    public static int bytes2Int(byte[] bs, int offset) {
        return ((bs[offset++] & 0xff) << 24) | ((bs[offset++] & 0xff) << 16)
                | ((bs[offset++] & 0xff) << 8) | (bs[offset++] & 0xff);
    }
    
    /**
     * int转byte数组
     *
     * @param a
     * @return
     */
    public static byte[] int2Bytes(int a) {
        byte[] b = new byte[4];
        b[0] = (byte) (a >> 24);
        b[1] = (byte) (a >> 16);
        b[2] = (byte) (a >> 8);
        b[3] = (byte) (a);
        
        return b;
    }
    
    /**
     * int转byte
     *
     * @param a
     * @return
     */
    public static byte int2Byte(int a) {
        return (byte)(a&0xff);
    }
    
    /**
     * int转byte数组
     *
     * @param a
     * @param b
     * @param offset
     * @return
     */
    public static void int2Byte(int a, byte[] b, int offset) {
        b[offset++] = (byte) (a >> 24);
        b[offset++] = (byte) (a >> 16);
        b[offset++] = (byte) (a >> 8);
        b[offset++] = (byte) (a);
    }
    
    public static int hex2byte(String hex2){
        if(hex2.length() != 2)
            return 0;
        
        String first = hex2.substring(0,1);
        if(first.equals("A")){
            first = "10";
        } else if(first.equals("B")){
            first = "11";
        } else if(first.equals("C")){
            first = "12";
        } else if(first.equals("D")){
            first = "13";
        } else if(first.equals("E")){
            first = "14";
        } else if(first.equals("F")) {
            first = "15";
        }
        String two = hex2.substring(1,2);
        if(two.equals("A")){
            two = "10";
        } else if(two.equals("B")){
            two = "11";
        } else if(two.equals("C")){
            two = "12";
        } else if(two.equals("D")){
            two = "13";
        } else if(two.equals("E")){
            two = "14";
        } else if(two.equals("F")) {
            two = "15";
        }
        return Integer.parseInt(first)*16 + Integer.parseInt(two);
    }
    
    
    public static void main(String [] args){
        System.out.println("0x00 0x71 0x70 0x00 0x01 0x00 0x71 0x70 0x00".replaceAll("0[xX]", ""));
        System.out.println("0x00 0x71 0x70 0x00 0x01 0x00 0x71 0x70 0x00".replaceAll("\\s+", ""));
        System.out.println(hexString2bytes("0x00 0x71 0x70 0x00 0x01 0x00 0x71 0x70 0x00"));
        System.out.println(ubytes2Int((byte)0x02, (byte)0x03));
    }
}
