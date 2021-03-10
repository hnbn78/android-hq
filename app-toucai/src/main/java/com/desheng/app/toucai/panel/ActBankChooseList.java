package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.bumptech.glide.Glide;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.RechargeInfo;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import java.util.ArrayList;

/**
 * 银行选择页面
 */
public class ActBankChooseList extends AbAdvanceActivity {
    private ListView lvCards;
    private RechargeInfo.BanklistBean curr = null;
    ArrayList<RechargeInfo.BanklistBean> bankList;
    boolean [] arrChoose;
    private AdapterBankCard adapter;
    private RechargeInfo.BanklistBean preChoosed;
    
    public static void launchForResult(Activity act, RechargeInfo.BanklistBean preChoosed, ArrayList<RechargeInfo.BanklistBean> bankList) {
        Intent itt = new Intent(act, ActBankChooseList.class);
        itt.putExtra("bankList", bankList);
        itt.putExtra("preChoosed", preChoosed);
        act.startActivityForResult(itt, 0);
    }
    
    @Override
    protected int getLayoutId() {
        return R.layout.act_bank_card_list;
    }
    
    @Override
    protected void init() {
        setStatusBarTranslucentAndLightContentWithPadding();
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "选择银行卡");
        setToolbarButtonRightText("确认");
        setToolbarButtonRightTextColor(R.color.white);
        setToolbarButtonRightTextSize(18);
        getToolbarLeftImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itt = new Intent();
                setResult(0, itt);
                finish();
            }
        });
        setToolbarRightButtonGroupClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curr == null) {
                    Toasts.show(ActBankChooseList.this, "请先选择银行", false);
                    return;
                }
                setResult();
                finish();
            }
        });
        
        bankList = (ArrayList<RechargeInfo.BanklistBean>) getIntent().getSerializableExtra("bankList");
        preChoosed = (RechargeInfo.BanklistBean) getIntent().getSerializableExtra("preChoosed");
        
        arrChoose = new boolean[bankList.size()];
        
        lvCards = (ListView)findViewById(R.id.lvCards);
       
        if (preChoosed != null) {
             curr = preChoosed;
             int index = 0;
            for (int i = 0; i < arrChoose.length; i++) {
                if (bankList.get(i).getCode().equals(preChoosed.code)) {
                    index = i;
                    break;
                }
            }
            arrChoose[index] = true;
        }else{
            curr = bankList.get(0);
            arrChoose[0] = true;
        }
    
        adapter = new AdapterBankCard(ActBankChooseList.this, bankList);
        lvCards.setAdapter(adapter);
        lvCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                arrChoose[position] = true;
                curr = bankList.get(position);
                for (int i = 0; i < arrChoose.length; i++) {
                    if (i != position) {
                        arrChoose[i] = false;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    
    }
    
    public void setResult() {
        Intent itt = new Intent();
        itt.putExtra("result", curr);
        setResult(0, itt);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
    }
    
    @Override
    public void onBackPressed() {
        if (curr == null) {
            Toasts.show(ActBankChooseList.this, "请先选择银行", false);
            return;
        }
        setResult();
        finish();
    }
    
    public  class AdapterBankCard extends BaseAdapter{
        private ArrayList<RechargeInfo.BanklistBean> datas;
        private Context ctx;
    
        public AdapterBankCard(Context ctx, ArrayList<RechargeInfo.BanklistBean> datas) {
            this.ctx = ctx;
            this.datas = datas;
        }
    
        @Override
        public int getCount() {
            return datas.size();
        }
    
        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }
    
        @Override
        public long getItemId(int position) {
            return 0;
        }
    
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(ctx).inflate(R.layout.item_bank_choose, null);
            }
            String bankFace = UserManager.getIns().getBankFace(datas.get(position).name);
            if(Strs.isNotEmpty(bankFace)) {
                Glide.with(ctx).load(Uri.parse(bankFace)).
                        placeholder(R.mipmap.ic_bank_def).
                        into((ImageView) convertView.findViewById(R.id.ivBankLogo));
            }
            ((TextView)convertView.findViewById(R.id.tvBankName)).setText(datas.get(position).name);
            ((TextView)convertView.findViewById(R.id.tvCardNum)).setText("");
            (convertView.findViewById(R.id.ivDefault)).setVisibility(
                    arrChoose[position] ? View.VISIBLE : View.INVISIBLE);
            return convertView;
        }
    }
}
