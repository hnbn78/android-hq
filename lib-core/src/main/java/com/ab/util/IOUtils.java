package com.ab.util;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;

public class IOUtils {

	public static void close(Closeable ios) {
		try {
			if(ios != null){
				ios.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	


}
