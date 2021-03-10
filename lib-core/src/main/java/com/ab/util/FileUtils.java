package com.ab.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class FileUtils {
	/**
	 * 二进制字符串转文件
	 * 
	 * @param hexImgStr
	 *            以空格分隔的 16进制字符串
	 * @param path
	 *            要保存的目录
	 * @param fileName
	 *            保存的文件
	 */
	public static void hexString2File(String hexImgStr, String path,
			String fileName) throws IOException {

		String[] data = hexImgStr.split(" ");

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path + fileName);
			for (String hexStr : data) {
				byte[] bytes = BitUtils.hex2byte(hexStr);
				fos.write(bytes, 0, bytes.length);
			}
			fos.flush();
		} finally {
			IOUtils.close(fos);
		}
	}
	
	public static String getStringFormAsset(Context ctx, String fileName){
		String str = "";
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(ctx.getAssets().open(fileName)));
			String line = "";
			while((line = br.readLine()) != null){
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			IOUtils.close(br);
		}
		str = sb.toString();
		return str;
	}
    
    
    public static void filterAllFiles(File dir, String partern, List<String> outContainer) {
        File[] fs = dir.listFiles();
        for(int i=0; i<fs.length; i++){
            if(fs[i].isDirectory()){
                try{
                    filterAllFiles(fs[i], partern, outContainer);
                }catch(Exception e){
                    //nothing
                }
            }else{
                if(fs[i].getName().matches(partern)){
                    outContainer.add(fs[i].getAbsolutePath());
                }
            }
        }
    }
}
