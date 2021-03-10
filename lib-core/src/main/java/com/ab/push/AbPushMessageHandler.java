package com.ab.push;

import android.content.Context;

/**
 * Created by lee on 2017/10/21.
 */

public interface AbPushMessageHandler {
    void onMessageReceived(Context context, String title, String content);
}
