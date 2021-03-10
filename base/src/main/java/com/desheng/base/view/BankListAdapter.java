package com.desheng.base.view;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.util.Strs;
import com.bumptech.glide.Glide;
import com.desheng.base.R;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.WithdrawInfo;

import java.util.List;


/**
 * Created by user on 2018/3/13.
 */

public class BankListAdapter extends BaseAdapter {
    private List<WithdrawInfo.AccountCardListBean> bankList;
    private Activity mActivity;

    public BankListAdapter( Activity activity, List<WithdrawInfo.AccountCardListBean> items){
        this.mActivity=activity;
        this.bankList =items;
    }

    @Override
    public int getCount() {
        return bankList.size();
    }
    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        //获取设置好的listener
        ViewHolder holder=null;
        if(convertView==null){
            convertView= View.inflate(mActivity, R.layout.item_bank_list, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_title.setText(bankList.get(position).getBankName());
        String bankFace = UserManager.getIns().getBankFace(bankList.get(position).getBankName());
        if(Strs.isNotEmpty(bankFace)) {
            Glide.with(mActivity).load(Uri.parse(bankFace)).
                    placeholder(R.mipmap.ic_bank_def).
                    into(holder.ivHead);
        }

        return convertView;
    }

    private class ViewHolder {
        private TextView tv_title;
        private ImageView ivHead;

        private ViewHolder(View view) {
         tv_title = view.findViewById(R.id.tv_text);
         ivHead = view.findViewById(R.id.iv_head);
        }
    }

}
