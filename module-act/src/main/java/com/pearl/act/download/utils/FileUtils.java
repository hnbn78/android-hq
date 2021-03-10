package com.pearl.act.download.utils;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * 
 * @author shuwoom
 * @email 294299195@qq.com
 * @date 2015-9-2
 * @update 2015-9-2
 * @des FileUtils
 */
public class FileUtils {
	
	public static long getStorageSize(){
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		long totalSize = totalBlocks * blockSize;
		
		return totalSize;
	}
	
	public static long getStorageAvailableSize(){
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		long availSize = availableBlocks * blockSize;
		
		return availSize;
	}
}
