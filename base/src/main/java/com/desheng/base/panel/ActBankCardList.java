package com.desheng.base.panel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.callback.AbCallback;
import com.ab.dialog.AbDialogOnClickListener;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.AbStrUtil;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.view.MaterialDialog;
import com.bumptech.glide.Glide;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.BankCardInfo;
import com.desheng.base.model.UserBindStatus;
import com.desheng.base.view.BottomInputFundPwdDialog;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.rmondjone.locktableview.DisplayUtil;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Request;

/**
 * Created by user on 2018/4/16.
 */

public class ActBankCardList extends AbAdvanceActivity {
    private boolean isFirstBind = true;
    private ListView lvCards;
    private ArrayList<BankCardInfo> listCard;


    public static void launch(Context act) {
        Intent itt = new Intent(act, ActBankCardList.class);
        act.startActivity(itt);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_bank_card_list;
    }

    @Override
    protected void init() {
        setStatusBarTranslucentAndLightContentWithPadding();
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "银行卡列表");
        lvCards = findViewById(R.id.lvCards);

        ImageView ivFooterView = new ImageView(this);
        ivFooterView.setImageResource(R.mipmap.ic_addcard);

        lvCards.addFooterView(ivFooterView);
    }

    private void initData() {
        HttpAction.getUserBankCardList(ActBankCardList.this, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<BankCardInfo>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getFieldObject(extra, "data", null) != null) {
                    listCard = (ArrayList<BankCardInfo>) getFieldObject(extra, "data", null);
                    isFirstBind = (listCard.size() == 0);
                    lvCards.setAdapter(new AdapterBankCard(ActBankCardList.this, listCard));
                    lvCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (position == listCard.size()) {
                                if (listCard != null && listCard.size() >= 5) {
                                    Toasts.show(ActBankCardList.this, "银行卡最多添加5张!", false);
                                    return;
                                } else {
                                    ActBindBankCard.launch(ActBankCardList.this, isFirstBind);
                                }

                            } else {
                                HttpAction.setDefaultCard(listCard.get(position).getId(), new AbHttpResult() {
                                    @Override
                                    public void setupEntity(AbHttpRespEntity entity) {

                                    }

                                    @Override
                                    public void onBefore(Request request, int id, String host, String funcName) {
                                        lvCards.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Dialogs.showProgressDialog(ActBankCardList.this, "");
                                            }
                                        });
                                    }

                                    @Override
                                    public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                        if (code == 0 && error == 0) {
                                            Toasts.show(ActBankCardList.this, "设置成功!", true);
                                            initData();
                                        } else {
                                            Toasts.show(ActBankCardList.this, msg, false);
                                        }
                                        return true;
                                    }

                                    @Override
                                    public void onAfter(int id) {
                                        lvCards.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Dialogs.hideProgressDialog(ActBankCardList.this);
                                            }
                                        }, 400);
                                    }
                                });
                            }

                        }
                    });
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserBankCard();
        initData();
    }

    private void checkUserBankCard() {
        UserManager.getIns().checkUserBindStatus(new UserManager.IUserBindCheckCallback() {
            @Override
            public void onBefore() {
                lvCards.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActBankCardList.this, "");
                    }
                });
            }

            @Override
            public void onUserBindChecked(UserBindStatus status) {
                isFirstBind = !status.isBindCard();
            }

            @Override
            public void onUserBindCheckFailed(String msg) {
                Toasts.show(ActBankCardList.this, msg, false);
            }

            @Override
            public void onAfter() {
                lvCards.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.hideProgressDialog(ActBankCardList.this);
                    }
                }, 400);
            }
        });
    }

    public static class AdapterBankCard extends BaseAdapter {
        private ArrayList<BankCardInfo> datas;
        private Context ctx;

        public AdapterBankCard(Context ctx, ArrayList<BankCardInfo> datas) {
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
            ViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(ctx).inflate(R.layout.item_bank_info, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String bankFace = UserManager.getIns().getBankFace(datas.get(position).getBankName());
            String bankBg = UserManager.getIns().getBankBG(datas.get(position).getBankName());

            if (Strs.isNotEmpty(bankFace)) {
                Glide.with(ctx).load(Uri.parse(bankFace)).
                        placeholder(R.mipmap.ic_bank_def).
                        into(holder.ivBankLogo);
            }

            if (Strs.isNotEmpty(bankBg)) {
                Glide.with(ctx).load(Uri.parse(bankBg)).
                        placeholder(R.mipmap.ic_bank_bg).
                        into(holder.iv_bank_bg);
            }

            holder.tvBankName.setText(datas.get(position).getBankName());
            holder.tv_card_code.setText("****    ****    ****    " + datas.get(position).getBankCardId());
//            ((TextView)convertView.findViewById(R.id.tvDefault)).setVisibility(
//                    datas.get(position).isIsDefault()? View.VISIBLE : View.INVISIBLE);

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            double device_width = (DisplayUtil.screenWidthPx) * 0.9;

            holder.tv_account_address.setText(datas.get(position).getBankCardName() + "**   " + datas.get(position).getBankBranch());

            holder.tvCardType.setText(datas.get(position).getCardType()==2?"信用卡":"借记卡");

            lp.width = (int) (device_width);
            lp.height = (int) (device_width / 3);
            holder.iv_bank_bg.setLayoutParams(lp);
            return convertView;
        }

        class ViewHolder {
            RelativeLayout layout_bank_bg;
            TextView tvBankName;
            TextView tvCardType;
            ImageView ivBankLogo;
            ImageView iv_bank_bg;
            TextView tv_account_address;
            TextView tv_card_code;

            public ViewHolder(View view) {
                tvBankName = view.findViewById(R.id.tvBankName);
                tvCardType = view.findViewById(R.id.tvCardType);
                ivBankLogo = view.findViewById(R.id.ivBankLogo);
                iv_bank_bg = view.findViewById(R.id.iv_bank_bg);
                tv_account_address = view.findViewById(R.id.tv_account_address);
                tv_card_code = view.findViewById(R.id.tv_card_code);
                layout_bank_bg = view.findViewById(R.id.layout_bank_bg);
            }
        }
    }
}
