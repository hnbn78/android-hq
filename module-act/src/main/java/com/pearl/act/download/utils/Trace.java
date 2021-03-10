package com.pearl.act.download.utils;

import com.ab.debug.AbDebug;

/**
 * @author shuwoom
 * @email 294299195@qq.com
 * @date 2015-9-2
 * @update 2015-9-2
 * @des Debug tools
 */
public class Trace {
    
    public static void d(String msg) {
        AbDebug.log(AbDebug.TAG_APP, msg);
    }
    
    public static void e(String msg) {
        AbDebug.log(AbDebug.TAG_APP, msg);
    }
}