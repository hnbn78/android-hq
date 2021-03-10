package com.desheng.base.panel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.global.Config;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.model.BonusData;
import com.desheng.base.model.MemberManagementBean;
import com.desheng.base.model.PrepareEditUserPoint;
import com.desheng.base.model.UserPoints;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.SpaceTopDecoration;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ActBonusHETIANXIA extends AbAdvanceActivity {

    private RecyclerView lv_list;
    private TextView tv_name, tv_tips;
    private TextView tv_nick_name;
    private TextView tv_lottery_point;
    private TextView tv_lottery_balance;

    private Button btnSign;

    private PrepareEditUserPoint info;
    private String username;
    private String userid;
    private int userType;

    private AdapterBonus mAdapter;
    private List<UserPoints> userPointList;


    public static void launch(Activity ctx, MemberManagementBean bean) {
        Intent intent = new Intent(ctx, ActBonusHETIANXIA.class);
        intent.putExtra("username", bean.userName);
        intent.putExtra("userid", "" + bean.userId);
        intent.putExtra("userType", bean.userType);

        ctx.startActivity(intent);
    }

    @Override
    protected void init() {
        userPointList = new ArrayList<>();
        setToolbarLeftBtn(UIHelper.BACK_WHITE_ARROR);
        if (UIHelper.toolbarBgResId > 0)
            setToolbarBgImage(UIHelper.toolbarBgResId);
        else
            setToolbarBgColor(R.color.colorPrimary);
        setToolbarTitleCenter("分红设置");
        setToolbarTitleCenterColor(R.color.white);
        setStatusBarTranslucentAndLightContentWithPadding();
        setToolbarButtonRightText("增加");
        setToolbarButtonRightTextColor(R.color.textColorPrimaryInverse);

        lv_list = findViewById(R.id.lv_list);
        lv_list.setLayoutManager(Views.genLinearLayoutManagerV(ActBonusHETIANXIA.this));
        lv_list.addItemDecoration(new SpaceTopDecoration(3));
        mAdapter = new AdapterBonus(this, userPointList);

        tv_name = findViewById(R.id.tv_name);
        tv_tips = findViewById(R.id.tv_tips);
        tv_nick_name = findViewById(R.id.tv_nick_name);
        tv_lottery_point = findViewById(R.id.tv_lottery_point);
        tv_lottery_balance = findViewById(R.id.tv_lottery_balance);

        btnSign = findViewById(R.id.btnSign);

        username = getIntent().getStringExtra("username");
        userid = getIntent().getStringExtra("userid");
        userType = getIntent().getIntExtra("userType", 1);
        showEdit(username);

        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != userPointList && userPointList.size() > 0 && hasValue())
                    update_BonusCFG(userid, 0, userPointList);
                }
        });
    }

    @Override
    public void onRightButtonClick() {
        super.onRightButtonClick();
        UserPoints bonusBean = new UserPoints();
        bonusBean.setAdd(true);
        userPointList.add(0, bonusBean);
        mAdapter = new AdapterBonus(ActBonusHETIANXIA.this, userPointList);
        lv_list.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_edit_add_new_bonus_point_hetianxia;
    }

    private void showEdit(final String userName) {
        HttpAction.prepareEditPoint(userName, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", PrepareEditUserPoint.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {

            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    info = getField(extra, "data", null);
                    if (null != info) {
                        tv_name.setText(info.uAccount.username);
                        tv_nick_name.setText(info.uAccount.nickname);

                        if (Config.custom_flag.equals(BaseConfig.FLAG_HETIANXIA)) {
                            tv_lottery_point.setText("" + (info.uGameLotteryAccount.point + 5));
                        } else if(Config.custom_flag.equals(BaseConfig.FLAG_JINDU)){
                            tv_lottery_point.setText("" + (info.uGameLotteryAccount.point + 10));
                        } else {
                            tv_lottery_point.setText("" + info.uGameLotteryAccount.point);
                        }

                        tv_lottery_balance.setText("" + info.uGameLotteryAccount.availableBalance);
                        initXjBonus(info.uAccount.username);
                    }
                } else {
                    Toasts.show(ActBonusHETIANXIA.this, msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {

            }
        });
    }

    private void initXjBonus(String username) {
        HttpAction.initXjBonus(null, username, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", BonusData.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
                btnSign.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActBonusHETIANXIA.this, "查询中");
                    }
                });
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                BonusData info = getField(extra, "data", null);
                userPointList.clear();
                if (code == 0 && error == 0 && null != info) {
                    tv_tips.setText("您当前的分红最低为" + info.getMinPoint() + "%,最高为" + info.getMaxPoint() + "%");
                    String userPoint = info.getUserPoints();
                    if (Strs.isEmpty(userPoint)) {

                        UserPoints bonusBean = new UserPoints();
                        bonusBean.setAdd(true);
                        userPointList.add(bonusBean);
                        mAdapter = new AdapterBonus(ActBonusHETIANXIA.this, userPointList);
                        lv_list.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        btnSign.setVisibility(View.VISIBLE);
                        tv_tips.setVisibility(View.VISIBLE);
                        getToolbarRightTextButton().setVisibility(View.VISIBLE);

                    } else {
                        userPoint = userPoint.replace("\\", "");
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<UserPoints>>() {
                        }.getType();
                        List<UserPoints> userPoints = gson.fromJson(userPoint, listType);
                        userPointList.addAll(userPoints);
                        mAdapter = new AdapterBonus(ActBonusHETIANXIA.this, userPointList);
                        lv_list.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        btnSign.setVisibility(View.GONE);
                        tv_tips.setVisibility(View.GONE);
                        getToolbarRightTextButton().setVisibility(View.GONE);
                    }

                } else {
                    Toasts.show(ActBonusHETIANXIA.this, msg, false);
                }

                btnSign.setEnabled(true);
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onGetString(String str) {
                return super.onGetString(str);
            }

            @Override
            public boolean onError(int status, String content) {
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Dialogs.hideProgressDialog(ActBonusHETIANXIA.this);
            }
        });
    }

    public void onDelete(int position) {

        userPointList.remove(position);
        mAdapter = new AdapterBonus(ActBonusHETIANXIA.this, userPointList);
        lv_list.setAdapter(mAdapter);

    }

    private boolean hasValue() {
        for (int i = 0; i < userPointList.size(); i++) {

            if (Strs.isEmpty(userPointList.get(i).getProfit())
                    || Strs.isEmpty(userPointList.get(i).getPercent())
                    || Strs.isEmpty(userPointList.get(i).getAmount())) {

                Toasts.show(ActBonusHETIANXIA.this, "请输入正确的分红", false);
                return false;
            }
        }

        return true;
    }

    private void update_BonusCFG(String userid, int checklast, List<UserPoints> userPointsList) {
        HttpAction.update_BonusCFG(null, userid, checklast, userPointsList, new AbHttpResult() {
            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                Toasts.show(ActBonusHETIANXIA.this, msg);
                if (error == 0 && code == 0) {
                    finish();
                }

                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onGetString(String str) {
                return super.onGetString(str);
            }

            @Override
            public boolean onError(int status, String content) {

                Toasts.show(ActBonusHETIANXIA.this, content);
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }


    protected class AdapterBonus extends BaseQuickAdapter<UserPoints, AdapterBonus.ViewHolder> {

        private Context ctx;

        public AdapterBonus(Context ctx, List<UserPoints> data) {
            super(R.layout.item_new_bonus, data);
            this.ctx = ctx;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int positions) {
            super.onBindViewHolder(holder, positions);

            userPointList.get(positions).setProfit(holder.etLost.getText().toString());
            userPointList.get(positions).setPercent(holder.etPercent.getText().toString());
            userPointList.get(positions).setAmount(holder.etAmount.getText().toString());
            userPointList.get(positions).setActivity(holder.etActivity.getText().toString());

            holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ActBonusHETIANXIA) ctx).onDelete(positions);
                }
            });
        }

        @Override
        protected void convert(AdapterBonus.ViewHolder viewHolder, final UserPoints item) {

            if (Config.custom_flag.equals(BaseConfig.FLAG_LONGTENG)) {
                viewHolder.tv_time_length.setText("周销量(元)>=:");
                viewHolder.tv_profit_length.setText("周累计亏损:");
            } else {
                viewHolder.tv_time_length.setText("半月总销量(元)>=:");
                viewHolder.tv_profit_length.setText("半月累计亏损:");
            }

            if (item.isAdd()) {
                viewHolder.etLost.setEnabled(true);
                viewHolder.etPercent.setEnabled(true);
                viewHolder.etAmount.setEnabled(true);
                viewHolder.etActivity.setEnabled(true);

                if (!Strs.isEmpty(item.getAmount())) {
                    viewHolder.etAmount.setText("" + item.getAmount());
                }

                if (!Strs.isEmpty(item.getPercent())) {
                    viewHolder.etPercent.setText("" + item.getPercent());
                }

                if (!Strs.isEmpty(item.getProfit())) {
                    viewHolder.etLost.setText("" + item.getProfit());
                }

                if (!Strs.isEmpty(item.getActivity())) {
                    viewHolder.etActivity.setText(item.getActivity());
                }

                viewHolder.btn_delete.setVisibility(View.VISIBLE);

                viewHolder.etLost.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        item.setProfit(s.toString());

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                viewHolder.etPercent.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        item.setPercent(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                viewHolder.etAmount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        item.setAmount(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                viewHolder.etActivity.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        item.setActivity(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


            } else {
                viewHolder.btn_delete.setVisibility(View.INVISIBLE);
                viewHolder.etLost.setText("" + item.getProfit());
                viewHolder.etPercent.setText("" + item.getPercent());
                viewHolder.etAmount.setText("" + item.getAmount());
                viewHolder.etActivity.setText("" + item.getActivity());

                viewHolder.etLost.setEnabled(false);
                viewHolder.etPercent.setEnabled(false);
                viewHolder.etAmount.setEnabled(false);
                viewHolder.etActivity.setEnabled(false);
            }
        }

        class ViewHolder extends BaseViewHolder {
            EditText etAmount;
            EditText etLost;
            EditText etPercent;
            EditText etActivity;
            Button btn_delete;
            TextView tv_time_length;
            TextView tv_profit_length;


            public ViewHolder(View view) {
                super(view);
                tv_time_length = view.findViewById(R.id.tv_time_length);
                tv_profit_length = view.findViewById(R.id.tv_profit_length);
                etAmount = view.findViewById(R.id.etAllSale);
                etLost = view.findViewById(R.id.etLost);
                etPercent = view.findViewById(R.id.etBonus);
                btn_delete = view.findViewById(R.id.btn_delete);
                etActivity = view.findViewById(R.id.etActivity);
            }
        }
    }
}
