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
import com.desheng.base.model.MemberManagementBean;
import com.desheng.base.model.SalaryBean;
import com.desheng.base.model.UserSalary;
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

    public static void launch(Activity ctx, MemberManagementBean bean ) {

        Intent intent = new Intent(ctx, ActSalary.class);
        intent.putExtra("username", bean.userName);
        intent.putExtra("userid", "" + bean.userId);
        ctx.startActivity(intent);
    }

    @Override
    protected void init() {
        userSalaryList = new ArrayList<>();
        setToolbarLeftBtn(UIHelper.BACK_WHITE_ARROR);
        if (UIHelper.toolbarBgResId > 0)
            setToolbarBgImage(UIHelper.toolbarBgResId);
        else
            setToolbarBgColor(R.color.colorPrimary);
        setToolbarTitleCenter("工资管理");
        setToolbarTitleCenterColor(R.color.white);
        setStatusBarTranslucentAndLightContentWithPadding();
        setToolbarButtonRightText("签订契约");
        setToolbarButtonRightTextColor(R.color.textColorPrimaryInverse);

        lv_list = findViewById(R.id.lv_list);
        lv_list.setLayoutManager(Views.genLinearLayoutManagerV(ActSalary.this));
        lv_list.addItemDecoration(new SpaceTopDecoration(3));
        mAdapter = new AdapterAddEditSalary(this, userSalaryList);
        lv_list.setAdapter(mAdapter);

        tv_username = findViewById(R.id.tv_username);
        tv_private_point = findViewById(R.id.tv_private_point);
        tv_tips = findViewById(R.id.tv_tips);
        btnAdd = findViewById(R.id.btnAdd);

        username = getIntent().getStringExtra("username");
        userid = getIntent().getStringExtra("userid");
        tv_username.setText(username);
        initSalary(username);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserSalary bonusBean = new UserSalary();
                bonusBean.setAdd(true);
                userSalaryList.add(0,bonusBean);
                mAdapter = new AdapterAddEditSalary(ActSalary.this, userSalaryList);
                lv_list.setAdapter(mAdapter);
            }
        });
    }

    @Override
    public void onRightButtonClick() {
        super.onRightButtonClick();

        if (null != userSalaryList && userSalaryList.size() > 0&&hasValue()) {
            updateSalary(userid, userSalaryList);
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_edit_salary_point;
    }

    private void initSalary(String username) {
        HttpAction.initSalary(null, username, new AbHttpResult() {
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

                SalaryBean info = getField(extra, "data", null);
                userSalaryList.clear();
                if (code == 0 && error == 0 && null != info) {
                    tv_private_point.setText((info.getSetMinPoint() * 100) + "%-" + (info.getSetMaxPoint() * 100) + "%");
                    String infoMixPercent = info.getMixPercent();
                    tv_tips.setText("温馨提示：活跃人数若不需要则填0，下级日工资则是按照最低比例" + (info.getSetMinPoint() * 100) + "%");

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

    public void onDelete(int position) {
        userSalaryList.remove(position);
        mAdapter = new AdapterAddEditSalary(ActSalary.this, userSalaryList);
        lv_list.setAdapter(mAdapter);
    }

    private boolean hasValue() {
        for (int i = 0; i < userSalaryList.size(); i++) {

            if (Strs.isEmpty(userSalaryList.get(i).getPoint())
                    || Strs.isEmpty(userSalaryList.get(i).getAmount())) {
                Toasts.show(ActSalary.this, "请输入正确的最低团队流水和比例",false);
                return false;
            }

            userSalaryList.get(i).setPoint(""+(Double.parseDouble(userSalaryList.get(i).getPoint())/100));
        }

        return true;
    }


    private void updateSalary(String userid, List<UserSalary> userPointsList) {
        HttpAction.updateSalary(null, userid, userPointsList, new AbHttpResult() {
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

    protected class AdapterAddEditSalary extends BaseQuickAdapter<UserSalary , AdapterAddEditSalary.ViewHolder> {

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
        protected void convert(AdapterAddEditSalary.ViewHolder viewHolder, final UserSalary item) {

            if (item.isAdd()) {
                viewHolder.et_active_count.setEnabled(true);
                viewHolder.et_rate.setEnabled(true);
                viewHolder.et_min_count.setEnabled(true);

                if (!Strs.isEmpty(item.getAmount())) {
                    viewHolder.et_min_count.setText("" + item.getAmount());
                } else {
//                viewHolder.et_min_count.setText("");
                }

                if (!Strs.isEmpty(item.getPoint())) {
                    viewHolder.et_rate.setText("" + item.getPoint());
                } else {
//                viewHolder.et_rate.setText("");
                }

                if (!Strs.isEmpty(item.getActivity())) {
                    viewHolder.et_active_count.setText("" + item.getActivity());
                } else {
//                viewHolder.et_active_count.setText("");
                }

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

                viewHolder.btn_delete.setVisibility(View.VISIBLE);


            } else {
                viewHolder.btn_delete.setVisibility(View.INVISIBLE);
                viewHolder.et_active_count.setText("" + item.getActivity());
                viewHolder.et_rate.setText("" + (Double.parseDouble(item.getPoint()) * 100));
                viewHolder.et_min_count.setText("" + item.getAmount());
                viewHolder.et_active_count.setEnabled(false);
                viewHolder.et_rate.setEnabled(false);
                viewHolder.et_min_count.setEnabled(false);
            }
        }

        class ViewHolder extends BaseViewHolder {
            EditText et_min_count;
            EditText et_active_count;
            EditText et_rate;
            Button btn_delete;

            public ViewHolder(View view) {
                super(view);
                et_min_count = (EditText) view.findViewById(R.id.et_min_count);
                et_active_count = (EditText) view.findViewById(R.id.et_active_count);
                et_rate = (EditText) view.findViewById(R.id.et_rate);
                btn_delete = (Button) view.findViewById(R.id.btn_delete);
            }
        }
    }
}
