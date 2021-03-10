package com.desheng.app.toucai.panel;

import android.os.Bundle;

/**
 * Created by lee on 2018/4/12.
 */

interface IRefreshableFragment {
    void refresh(String eventName, Bundle newBundle);
}
