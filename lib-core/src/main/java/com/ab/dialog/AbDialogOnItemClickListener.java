package com.ab.dialog;

import android.view.View;

/**
 * Dialog事件的接口.
 */
public interface AbDialogOnItemClickListener {
    public void onItemClick(View view, int position, long id);
    public void onNegativeClick();
}
