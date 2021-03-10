package com.ab.util;

/**
 * Created by lee on 2017/4/7 0007.
 */

public class CashUtils {
    /**
     *
     * @param fen
     * @return ""表示数据非法,其它返回正常的元,如果是整数,则不显示后面的".00"
     */
    public static String fentoyuan(String fen){
        try{
            Integer.parseInt(fen);
        } catch (Exception e){
            return "10";
        }
        int ifen = Integer.parseInt(fen);
        String sfen = "" + ifen;
        if(sfen.length()==1){
            sfen = "0.0" + sfen;
        } else if(sfen.length()==2){
            sfen = "0." + sfen;
        } else {
            sfen = sfen.substring(0,sfen.length()-2) + "." + sfen.substring(sfen.length()-2,sfen.length());
        }
        String yuan = sfen;
        if(yuan.substring(yuan.length()-1,yuan.length()).equals("0")){
            yuan = yuan.substring(0,yuan.length()-1);
            if(yuan.substring(yuan.length()-1,yuan.length()).equals("0")) {
                yuan = yuan.substring(0, yuan.length() - 2);
            }
        }
        return yuan;
    }
    
    /**
     *
     * @param fen
     * @return ""表示数据非法,其它返回正常的元,如果是整数,则不显示后面的".00"
     */
    public static String fentoyuanOneDot(String fen){
        try{
            Integer.parseInt(fen);
        } catch (Exception e){
            return "10";
        }
        int ifen = Integer.parseInt(fen);
        String sfen = "" + ifen;
        if(sfen.length()==1){
            sfen = "0.0" + sfen;
        } else if(sfen.length()==2){
            sfen = "0." + sfen;
        } else {
            sfen = sfen.substring(0,sfen.length()-2) + "." + sfen.substring(sfen.length()-2,sfen.length());
        }
        String yuan = sfen;
        if(yuan.substring(yuan.length()-1,yuan.length()).equals("0")){
            yuan = yuan.substring(0,yuan.length()-1);
        }
        return yuan;
    }
    
    public static int yuantofen(String yuan){
        yuan = yuan.trim();
        if(yuan.length()==0){
            return 0;
        } else {
            if(yuan.indexOf(".") < 0){
                //表示没有小数点
                yuan =  yuan + ".0";
            }
            if(yuan.indexOf(".") == yuan.length() - 1){
                //最后一位是.
                yuan =  yuan + "0";
            }
            if(yuan.indexOf(".") == yuan.length() - 1){
                //最后一位是.
                yuan =  yuan + "0";
            }
            if(yuan.indexOf(".") == yuan.length() - 2){
                //最后一位是.
                yuan =  yuan + "0";
            }
            if(yuan.length() - yuan.indexOf(".") >= 3 ){
                //123.04:6-3=3   123.045:7-3=4
                yuan  = yuan.substring(0,yuan.indexOf(".")+3);  //保留最多2位小数而已
            }
            return Integer.parseInt(yuan.replace(".",""));
        }
    }
}
