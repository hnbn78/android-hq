package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.global.Config;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.action.HttpAction;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.model.MemberManagementBean;
import com.desheng.base.model.SalaryBean;
import com.desheng.base.model.UserSalary;
import com.desheng.base.util.DeviceUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.SpaceTopDecoration;
import com.shark.tc.R;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ActSalary extends AbAdvanceActivity {

    private RecyclerView lv_list;
    private TextView tv_username;
    private TextView tv_private_point, tv_tips;
    private Button btnAdd;

    //    private PrepareEditUserPoint info;
    private String username;
    private String userid;

    private AdapterAddEditSalary mAdapter;
    private List<UserSalary> userSalaryList;
    private SalaryBean info;
    private String timeType;
    private String TAG = ActSalary.class.getSimpleName();

    public static void launch(Activity ctx, MemberManagementBean bean) {
        launch(ctx, bean, null);
    }

    public static void launch(Activity ctx, MemberManagementBean bean, String timeType) {

        Intent intent = new Intent(ctx, ActSalary.class);
        intent.putExtra("username", bean.userName);
        intent.putExtra("userid", "" + bean.userId);
        intent.putExtra("timeType", timeType);
        ctx.startActivity(intent);
    }

    @Override
    protected void init() {
        userSalaryList = new ArrayList<>();
        setToolbarLeftBtn(UIHelper.BACK_WHITE_ARROR);
        if (UIHelper.toolbarBgResId > 0)
            setToolbarBgImage(UIHelper.toolbarBgResId);
        else
            setToolbarBgColor(com.desheng.base.R.color.colorPrimary);


        timeType = getIntent().getStringExtra("timeType");
        if (Strs.isEmpty(timeType)) {
            setToolbarTitleCenter("工资管理");
        } else {
            setToolbarTitleCenter("时工资");
        }
        setToolbarTitleCenterColor(com.desheng.base.R.color.white);
        setStatusBarTranslucentAndLightContentWithPadding();
        setToolbarButtonRightText("签订契约");
        setToolbarButtonRightTextColor(com.desheng.base.R.color.textColorPrimaryInverse);

        lv_list = findViewById(com.desheng.base.R.id.lv_list);
        lv_list.setLayoutManager(Views.genLinearLayoutManagerV(ActSalary.this));
        lv_list.addItemDecoration(new SpaceTopDecoration(3));
        mAdapter = new AdapterAddEditSalary(this, userSalaryList);
        lv_list.setAdapter(mAdapter);

        tv_username = findViewById(com.desheng.base.R.id.tv_username);
        tv_private_point = findViewById(com.desheng.base.R.id.tv_private_point);
        tv_tips = findViewById(com.desheng.base.R.id.tv_tips);
        btnAdd = findViewById(com.desheng.base.R.id.btnAdd);

        username = getIntent().getStringExtra("username");
        userid = getIntent().getStringExtra("userid");
        timeType = getIntent().getStringExtra("timeType");
        tv_username.setText(username);
        initSalary(username);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserSalary bonusBean = new UserSalary();
                bonusBean.setAdd(true);
                userSalaryList.add(0, bonusBean);
                mAdapter = new AdapterAddEditSalary(ActSalary.this, userSalaryList);
                lv_list.setAdapter(mAdapter);
            }
        });


    }

    @Override
    public void onRightButtonClick() {
        super.onRightButtonClick();

        if (null != userSalaryList && userSalaryList.size() > 0 && hasValue()) {
            for (int i = 0; i < userSalaryList.size(); i++) {
                BigDecimal bigDecimal = new BigDecimal(userSalaryList.get(i).getPoint()).divide(new BigDecimal(100));
                userSalaryList.get(i).setPoint(bigDecimal.toPlainString());
            }
            updateSalary(userid, userSalaryList);
        }

    }

    @Override
    protected int getLayoutId() {
        return com.desheng.base.R.layout.act_edit_salary_point;
    }

    private void initSalary(String username) {
        HttpAction.initSalary(null, username, timeType, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", SalaryBean.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
                btnAdd.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActSalary.this, "查询中");
                    }
                });
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                info = getField(extra, "data", null);
                userSalaryList.clear();
                if (code == 0 && error == 0 && null != info) {
                    String tempStr = Strs.isEqual("1", timeType) ? "小时" : "日";
                    String infoMixPercent;
                    String minPoint = Nums.formatDecimal((info.getSetMinPoint() * 100), 2) + "%";
                    String maxPoint = Nums.formatDecimal((info.getSetMaxPoint() * 100), 2) + "%";
                    String pointStr;
                    String tipStr;
                    pointStr = minPoint + "-" + maxPoint;
                    infoMixPercent = info.getMixPercent();
                    tipStr = "温馨提示：活跃人数若不需要则填0，下级" + tempStr + "工资则是按照最低比例" + minPoint + "，最高为" + maxPoint;
                    tv_private_point.setText(pointStr);
                    tv_tips.setText(tipStr);

                    if (Strs.isEmpty(infoMixPercent)) {
                        UserSalary bonusBean = new UserSalary();
                        bonusBean.setAdd(true);
                        userSalaryList.add(bonusBean);
                        mAdapter = new AdapterAddEditSalary(ActSalary.this, userSalaryList);
                        lv_list.setAdapter(mAdapter);
                        btnAdd.setVisibility(View.VISIBLE);
                        getToolbarRightTextButton().setVisibility(View.VISIBLE);
                    } else {
                        infoMixPercent = infoMixPercent.replace("\\", "");
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<UserSalary>>() {
                        }.getType();
                        List<UserSalary> userSalaries = gson.fromJson(infoMixPercent, listType);
                        userSalaryList.addAll(userSalaries);

                        for (int i = 0; i < userSalaryList.size(); i++) {
                            userSalaryList.get(i).setAdd(false);
                        }

                        mAdapter = new AdapterAddEditSalary(ActSalary.this, userSalaryList);
                        lv_list.setAdapter(mAdapter);
                        btnAdd.setVisibility(View.GONE);
                        getToolbarRightTextButton().setVisibility(View.GONE);
                    }

                } else {
                    Toasts.show(ActSalary.this, msg, false);
                }

                btnAdd.setEnabled(true);
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
                Dialogs.hideProgressDialog(ActSalary.this);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        DeviceUtil.hideInputKeyboard(ActSalary.this);
    }

    public void onDelete(int position) {
        userSalaryList.remove(position);
        mAdapter = new AdapterAddEditSalary(ActSalary.this, userSalaryList);
        lv_list.setAdapter(mAdapter);
    }

    private boolean hasValue() {
        for (int i = 0; i < userSalaryList.size(); i++) {

            if (Strs.isEmpty(userSalaryList.get(i).getPoint())) {
                Toasts.show(ActSalary.this, "请输入工资比例", false);
                return false;
            }

            double setPoint = Double.parseDouble(userSalaryList.get(i).getPoint());
            double min = Nums.formatTriple(info.getSetMinPoint() * 100);
            double max = Nums.formatTriple(info.getSetMaxPoint() * 100);

            if (info != null && (setPoint < min || setPoint > max)) {
                Toasts.show(ActSalary.this, "请输入正确的工资比例", false);
                return false;
            }

            if (Strs.isEmpty(userSalaryList.get(i).getAmount())) {
                userSalaryList.get(i).setAmount("0");
            }

            if (Strs.isEmpty(userSalaryList.get(i).getActivity())) {
                userSalaryList.get(i).setActivity("0");
            }


        }

        return true;
    }


    private void updateSalary(String userid, List<UserSalary> userPointsList) {
        HttpAction.updateSalary(null, userid, userPointsList, timeType, new AbHttpResult() {
            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                Toasts.show(ActSalary.this, msg);
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
                Toasts.show(ActSalary.this, content);
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    protected class AdapterAddEditSalary extends BaseQuickAdapter<UserSalary, AdapterAddEditSalary.ViewHolder> {

        private Context ctx;

        public AdapterAddEditSalary(Context ctx, List<UserSalary> data) {
            super(R.layout.item_new_salary, data);
            this.ctx = ctx;
        }

        @Override
        public void onBindViewHolder(AdapterAddEditSalary.ViewHolder holder, final int positions) {
            super.onBindViewHolder(holder, positions);

            userSalaryList.get(positions).setPoint(holder.et_rate.getText().toString());
            userSalaryList.get(positions).setActivity(holder.et_active_count.getText().toString());
            userSalaryList.get(positions).setAmount(holder.et_min_count.getText().toString());

            holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ActSalary) ctx).onDelete(positions);
                }
            });
        }


        @Override
        protected void convert(final AdapterAddEditSalary.ViewHolder viewHolder, final UserSalary item) {
            if (item.isAdd()) {
                editPoint(viewHolder, item);
                viewHolder.btn_delete.setVisibility(View.VISIBLE);

            } else {
                viewHolder.btn_delete.setVisibility(View.INVISIBLE);
                viewHolder.et_active_count.setText("" + item.getActivity());
                viewHolder.et_rate.setText(Nums.formatDecimal(Double.parseDouble(item.getPoint()) * 100, 2));
                viewHolder.et_min_count.setText("" + item.getAmount());

                viewHolder.et_active_count.setEnabled(false);
                viewHolder.et_rate.setEnabled(false);
                viewHolder.et_min_count.setEnabled(false);
            }
        }

        private void editPoint(final ViewHolder viewHolder, final UserSalary item) {
            viewHolder.et_active_count.setEnabled(true);
            viewHolder.et_rate.setEnabled(false);
            viewHolder.et_min_count.setEnabled(true);

            if (!Strs.isEmpty(item.getAmount())) {
                viewHolder.et_min_count.setText("" + item.getAmount());
            }
            final int fractionDigits = Config.custom_flag.equals(BaseConfig.FLAG_TOUCAI) ? 1 : 2;
            if (!Strs.isEmpty(item.getPoint())) {
                viewHolder.et_rate.setText(Nums.formatDecimal(item.getPoint(), fractionDigits));
            }

            if (!Strs.isEmpty(item.getActivity())) {
                viewHolder.et_active_count.setText("" + item.getActivity());
            }

            //固定契约0.1%
            viewHolder.et_rate.setText("0.1");

            viewHolder.et_active_count.addTextChangedListener(new TextWatcher() {
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

            viewHolder.et_rate.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Strs.inputLimitCount(s, viewHolder.et_rate, fractionDigits);
                    item.setPoint(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            viewHolder.et_min_count.addTextChangedListener(new TextWatcher() {
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
        }

        class ViewHolder extends BaseViewHolder {
            EditText et_min_count;
            TextView et_min_count_unit;
            EditText et_active_count;
            EditText et_rate;
            Button btn_delete;
            TextView tv_has_bonus;

            LinearLayout layout_min_team;
            LinearLayout layout_active_count;

            public ViewHolder(View view) {
                super(view);
                et_min_count = view.findViewById(R.id.et_min_count);
                et_min_count_unit = view.findViewById(R.id.et_min_count_unit);
                et_active_count = view.findViewById(R.id.et_active_count);
                et_rate = view.findViewById(R.id.et_rate);
                btn_delete = view.findViewById(R.id.btn_delete);
                layout_min_team = view.findViewById(R.id.layout_min_team);
                layout_active_count = view.findViewById(R.id.layout_active_count);
                tv_has_bonus = view.findViewById(R.id.tv_has_bonus);
            }
        }
    }
}
