package com.desheng.app.toucai.panel;

import android.view.View;
import android.widget.EditText;

import com.pearl.act.base.AbBaseFragment;
import com.shark.tc.R;

/**
 * 投注记录
 */
public class FragSuperPartnerSubMemberSearch extends AbBaseFragment {
    private OnSearchListener onSearchListener;

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }

    @Override

    public int getLayoutId() {
        return R.layout.frag_super_partner_sub_member_search;
    }

    @Override
    public void init(View root) {
      root.findViewById(R.id.tv_search).setOnClickListener(v -> {
          EditText editText = root.findViewById(R.id.et_name);

          if (onSearchListener != null) {
              onSearchListener.searchSubUser(editText.getText().toString());
          }

      });
    }

    public interface OnSearchListener {
        void searchSubUser(String user);
    }
}