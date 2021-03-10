package com.desheng.app.toucai.panel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.pearl.act.base.AbBaseFragment;
import com.shark.tc.R;

/**
 * Created by lee on 2018/3/7.
 */
@Deprecated
public class FragTabGameReal extends AbBaseFragment {
    
    public static Fragment newIns(Bundle bundle) {
        Fragment fragment = new FragTabGameReal();
        fragment.setArguments(bundle);
        return fragment;
    }
    
    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_game_other;
    }
    
    @Override
    public void init(View root) {
    
    }
}
