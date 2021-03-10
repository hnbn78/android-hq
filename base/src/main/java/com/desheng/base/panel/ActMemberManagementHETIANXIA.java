package com.desheng.base.panel;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
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
import com.ab.view.MaterialDialog;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.event.TransferSuccessEvent;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.BonusBean;
import com.desheng.base.model.BonusSalaryBean;
import com.desheng.base.model.MemberManagementBean;
import com.desheng.base.model.PrepareEditUserPoint;
import com.desheng.base.util.DeviceUtil;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.rmondjone.locktableview.LockTableView;
import com.pearl.view.rmondjone.locktableview.ProgressStyle;
import com.pearl.view.rmondjone.locktableview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Request;

/**
 * 会员管理
 * Created by user on 2018/3/15.
 */

public class ActMemberManagementHETIANXIA extends AbAdvanceActivity {

    public static void launch(Activity act) {
        simpleLaunch(act, ActMemberManagementHETIANXIA.class);
    }

    private Button Search_bt;
    private LinearLayout mContentView;
    private static final int PAGE_SIZE = 20;
    private List<MemberManagementBean> memberManagementBeans = new ArrayList<>();
    private ArrayList<ArrayList<String>> mTableDatas = new ArrayList<>();
    private LockTableView mLockTableView;
    private int totalCount;
    private XRecyclerView mXRecyclerView;
    private String action, userName, subName;
    private int currPage = -1;
    int size;
    private EditText etAgent, etUser;
    private HttpAction.RespSearchMember respSearchMember;
    private PrepareEditUserPoint info;

    private static final String ACTION_LIFT_POINT = "升点";
    private static final String ACTION_TRANSFORM = "转账";
    private static final String ACTION_BONUS = "分红";
    private static final String ACTION_ORDER_SALARY = "单单工资";
    private static final String ACTION_SALARY = "工资管理";
    private static final String ACTION_QUOTA = "配额";

    private static final String COLOR_LIFT_POINT = "#2095F2";

    private BonusBean bonusBean;
    private BonusSalaryBean bonusSalaryBean;
    private MaterialDialog dialog;

    @Override
    protected void init() {
        setToolbarLeftBtn(UIHelper.BACK_WHITE_ARROR);
        if (UIHelper.toolbarBgResId > 0)
            setToolbarBgImage(UIHelper.toolbarBgResId);
        else
            setToolbarBgColor(R.color.colorPrimary);
        setToolbarTitleCenter("会员管理");
        setToolbarTitleCenterColor(R.color.white);
        setStatusBarTranslucentAndLightContentWithPadding();
        initView();
        initLockTable();
        getQuerySalaryBonus();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_member_managment;
    }

    private void initView() {
        mContentView = (LinearLayout) findViewById(R.id.contentView);
        etAgent = (EditText) findViewById(R.id.etAgent);
        etUser = (EditText) findViewById(R.id.etUser);

        Search_bt = (Button) findViewById(R.id.button);
        Search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currPage = -1;
                memberManagementBeans.clear();
                userName = Views.getText(etUser);
                subName = Views.getText(etAgent);
                updateTable();
            }
        });

    }

    public void updateTable() {

        currPage++;
        getMemberManagementList();
    }

    /**
     * 获得会员管理
     */
    public void getMemberManagementList() {
        HttpAction.getMemberManagement(userName, subName, currPage, PAGE_SIZE, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                if (currPage == 0) {
                    Dialogs.showProgressDialog(ActMemberManagementHETIANXIA.this, "搜索中...");
                }
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespSearchMember.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                respSearchMember = getField(extra, "data", null);
                if (respSearchMember != null) {
                    mTableDatas.clear();
                    addTableHeader();
                    if (currPage == 0) {
                        memberManagementBeans.clear();
                    }
                    totalCount = respSearchMember.totalCount;
                    memberManagementBeans.addAll(respSearchMember.list);

                    for (int i = 0; i < memberManagementBeans.size(); i++) {
                        ArrayList<String> mRowDatas = new ArrayList<String>();
                        MemberManagementBean memberManagementBean = memberManagementBeans.get(i);
                        mRowDatas.add(memberManagementBean.userName);
                        mRowDatas.add(CtxLottery.formatUserType(memberManagementBean.userType));
                        mRowDatas.add(memberManagementBean.balance);
                        mRowDatas.add(memberManagementBean.teamBalance);

                        if (Config.custom_flag.equals(BaseConfig.FLAG_HETIANXIA)) {
                            showPoint(mRowDatas, memberManagementBean, 5);
                        } else if (Config.custom_flag.equals(BaseConfig.FLAG_JINDU)) {
                            showPoint(mRowDatas, memberManagementBean, 10);
                        } else {
                            mRowDatas.add(memberManagementBean.point);
                        }

                        mRowDatas.add("" + memberManagementBean.lastLoginTime + memberManagementBean.onlineStatus);
//                        mRowDatas.add((UserManager.getIns().getMainUserLevel() > 4 && (info.lotteryCodeRange.minPoint < info.lotteryCodeRange.maxPoint)) ? "升点" : "");

                        StringBuilder actionsSB = new StringBuilder();

                        if (Strs.isEmpty(subName) && Strs.isEmpty(userName)) { // 第一级会员目录
                            if (UserManager.getIns().isAgent()) {

                                if(!UserManager.getIns().isHideImprovePoint()){
                                    actionsSB.append(ACTION_LIFT_POINT);
                                    actionsSB.append("@");
                                }

                                actionsSB.append(ACTION_TRANSFORM);

                                if(Config.custom_flag.equals(BaseConfig.FLAG_JINDU)){
                                    setBonusShow(actionsSB,4);
                                }else{
                                    setBonusShow(actionsSB,2);
                                }

                            }
                        } else {
                            actionsSB.append(ACTION_TRANSFORM);
                        }

                        mRowDatas.add(actionsSB.toString());
                        mTableDatas.add(mRowDatas);
                    }

                    mLockTableView.setTableDatas(mTableDatas);
                    if (memberManagementBeans.size() >= totalCount) {
                        mLockTableView.getTableScrollView().setLoadingMoreEnabled(false);
//                        Toasts.show(ActMemberManagement.this, "已经全部加载完毕!");
                    } else {
                        mLockTableView.getTableScrollView().setLoadingMoreEnabled(true);
                    }
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                if (currPage == 0) {
                    Dialogs.hideProgressDialog(ActMemberManagementHETIANXIA.this);
                }
                if (ActMemberManagementHETIANXIA.this.mXRecyclerView != null) {
                    ActMemberManagementHETIANXIA.this.mXRecyclerView.refreshComplete();
                    ActMemberManagementHETIANXIA.this.mXRecyclerView.loadMoreComplete();
                }
            }
        });


    }

    private void setBonusShow(StringBuilder actionsSB,int min_level) {
        if (UserManager.getIns().getMainUserLevel() > min_level) {

            if (bonusSalaryBean != null && bonusSalaryBean.getBonus() != 0) {
                actionsSB.append("@");
                actionsSB.append(ACTION_BONUS);
            }

            if (bonusSalaryBean != null && bonusSalaryBean.getDandanRgz() != 0
                    && !UserManager.getIns().isHideDandanGongzi()) {
                actionsSB.append("@");
                actionsSB.append(ACTION_ORDER_SALARY);
            }

            if (bonusSalaryBean != null && bonusSalaryBean.getSalary() != 0) {
                actionsSB.append("@");
                actionsSB.append(ACTION_SALARY);
            }
        }
    }

    private void showPoint(ArrayList<String> mRowDatas, MemberManagementBean memberManagementBean, int plus_point) {
        try {
            Matcher matcher = Pattern.compile("[\\d|\\.]+").matcher(memberManagementBean.point);
            matcher.find();
            float p = Float.parseFloat(matcher.group());
            matcher.find();
            int a = Integer.parseInt(matcher.group());
            mRowDatas.add(Nums.formatDecimal(p + plus_point, 1) + " / " + a);
        } catch (Exception e) {
            mRowDatas.add(memberManagementBean.point);
        }
    }

    public void addTableHeader() {
        ArrayList<String> mfristData = new ArrayList<String>();

        //标题
        mfristData.add("账号");
        mfristData.add("类型");
        mfristData.add("余额");
        mfristData.add("团队余额");
        mfristData.add("返点");
        mfristData.add("最后登录时间");
        mfristData.add("操作");

        mTableDatas.add(mfristData);
    }

    /**
     * 升点
     *
     * @param userName
     * @param tag
     */
    private void prepareEditPointHETIANXIA(final String userName, final String point, final String tag) {
        HttpAction.prepareEditPoint(userName, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", PrepareEditUserPoint.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Search_bt.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActMemberManagementHETIANXIA.this, "查询中");
                    }
                });
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    info = getField(extra, "data", null);

                    if (tag.equals(ACTION_BONUS)) {

                    } else if (tag.equals(ACTION_LIFT_POINT)) {
                        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(ActMemberManagementHETIANXIA.this).inflate(R.layout.dialog_promote_point, null);
                        TextView tvBackPoint = viewGroup.findViewById(R.id.tv_back_point);
                        TextView tvRange = viewGroup.findViewById(R.id.tvRange);
                        TextView tv_account = viewGroup.findViewById(R.id.tv_account);
                        final EditText etPoint = viewGroup.findViewById(R.id.etPoint);
                        Button btnConfirm = viewGroup.findViewById(R.id.btnConfirm);
                        Button btnCancel = viewGroup.findViewById(R.id.btnCancel);
                        Dialogs.showCustomDialog(ActMemberManagementHETIANXIA.this, viewGroup, true);
                        tv_account.setText(info.uAccount.username);

                        try {
                            Matcher matcher = Pattern.compile("[\\d|\\.]+").matcher(point);
                            matcher.find();
                            float p = Float.parseFloat(matcher.group());
                            matcher.find();
                            int a = Integer.parseInt(matcher.group());

                            if (Config.custom_flag.equals(BaseConfig.FLAG_JINDU)) {
                                if(UserManager.getIns().getMainUserLevel()==5){
                                    setLiftPoint(tvRange, etPoint, btnConfirm, btnCancel, userName, 4, 10,0.2d,p);
                                }else{
                                    setLiftPoint(tvRange, etPoint, btnConfirm, btnCancel, userName, 4, 10,0.1d,p);
                                }
                                tvBackPoint.setText(Nums.formatDecimal(p + 10, 1) + " / " + a);
                            } else if (Config.custom_flag.equals(BaseConfig.FLAG_HETIANXIA)) {
                                setLiftPoint(tvRange, etPoint, btnConfirm, btnCancel, userName, 1, 5,0,p);
                                tvBackPoint.setText(Nums.formatDecimal(p + 5, 1) + " / " + a);
                            }
                        } catch (Exception e) {
                            tvBackPoint.setText(point);
                        }



                    } else if (tag.equals(ACTION_ORDER_SALARY)) {//单单

                    }

                } else {
                    Toasts.show(ActMemberManagementHETIANXIA.this, msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                Search_bt.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.hideProgressDialog(ActMemberManagementHETIANXIA.this);
                    }
                });
            }
        });
    }

    private void setLiftPoint(TextView tvRange, final EditText etPoint, Button btnConfirm, Button btnCancel, final String userName, int minLevel, final int plus_point, final double sub_point,final float p) {
        if (UserManager.getIns().getMainUserLevel() > minLevel
                && null != info
                && info.lotteryCodeRange != null
                && (p < (info.lotteryCodeRange.maxPoint- sub_point))) {

            etPoint.setEnabled(true);
            tvRange.setText((p+ plus_point) + "~" + (info.lotteryCodeRange.maxPoint + plus_point - sub_point));

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    double point = Views.getValue(etPoint, 0.0);

                    if (point <= 0 || point < p + plus_point -sub_point|| point > info.lotteryCodeRange.maxPoint + plus_point-sub_point) {
                        Toasts.show(ActMemberManagementHETIANXIA.this, "请输入正确的点数!", false);
                        return;
                    }
                    commit(userName, point - plus_point);
                    Dialogs.dissmisCustomDialog();
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialogs.dissmisCustomDialog();
                }
            });
        } else {
            etPoint.setEnabled(false);
            etPoint.setText("" + (p + plus_point));
            tvRange.setText("无法调整该用户返点");
            btnConfirm.setVisibility(View.GONE);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialogs.dissmisCustomDialog();
                }
            });
        }
    }

    private void commit(String userName, double point) {
        HttpAction.editUserPoint(userName, point, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", PrepareEditUserPoint.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Search_bt.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActMemberManagementHETIANXIA.this, "查询中");
                    }
                });
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    Toasts.show(ActMemberManagementHETIANXIA.this, "修改成功", true);
                } else {
                    Toasts.show(ActMemberManagementHETIANXIA.this, msg, false);
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("现有表格数据", mTableDatas.toString());
                        currPage = -1;
                        updateTable();
                    }
                }, 200);
                return true;
            }

            @Override
            public void onAfter(int id) {
                Search_bt.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.hideProgressDialog(ActMemberManagementHETIANXIA.this);
                    }
                });
            }
        });
    }

    private void initLockTable() {

        ArrayList<String> mfristData = new ArrayList<String>();

        //标题
        mfristData.add("账号");
        mfristData.add("类型");
        mfristData.add("余额");
        mfristData.add("团队余额");
        mfristData.add("返点");
        mfristData.add("最后登录时间");
        mfristData.add("操作");

        mTableDatas.add(mfristData);
        mLockTableView = new LockTableView(this, mContentView, mTableDatas);
        final String[] colors = new String[mfristData.size()];
        for (int i = 0; i < colors.length; i++) {
            if (i == colors.length - 2) {
                colors[i] = COLOR_LIFT_POINT;
            } else {
                colors[i] = "";
            }
        }

        mLockTableView.setColumnWidth(6, 200);

        mLockTableView.setTableContentTextColumnColors(colors);

        Log.e("表格加载开始", "当前线程：" + Thread.currentThread());

        mLockTableView.setLockFristColumn(true) //是否锁定第一列
                .setLockFristRow(true) //是否锁定第一行
                .setMaxColumnWidth(90) //列最大宽度
                .setMinColumnWidth(90) //列最小宽度
                .setMinRowHeight(40)//行最小高度
                .setMaxRowHeight(40)//行最大高度
                .setTextViewSize(12) //单元格字体大小
                .setFristRowBackGroudColor(R.color.colorPrimaryInverse)//表头背景色
                .setTableHeadTextColor(R.color.black)//表头字体颜色
                .setFirstRowTextColor(R.color.blue)
                .setTableContentTextColor(R.color.black)//单元格字体颜色
                .setLockFristColumn(true)
                .setNullableString("") //空值替换值
                .setTableViewListener(new LockTableView.OnTableViewListener() {
                    @Override
                    public void onTableViewScrollChange(int x, int y) {
//                        Log.e("滚动值","["+x+"]"+"["+y+"]");
                    }
                })//设置横向滚动回调监听
                .setTableViewRangeListener(new LockTableView.OnTableViewRangeListener() {
                    @Override
                    public void onLeft(HorizontalScrollView view) {
//                        Log.e("滚动边界","滚动到最左边");
                    }

                    @Override
                    public void onRight(HorizontalScrollView view) {
//                        Log.e("滚动边界","滚动到最右边");
                    }
                })//设置横向滚动边界监听
                .setOnLoadingListener(new LockTableView.OnLoadingListener() {
                    @Override
                    public void onRefresh(XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
                        ActMemberManagementHETIANXIA.this.mXRecyclerView = mXRecyclerView;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("现有表格数据", mTableDatas.toString());
                                currPage = -1;
                                updateTable();
                            }
                        }, 1000);
                    }

                    @Override
                    public void onLoadMore(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
                        ActMemberManagementHETIANXIA.this.mXRecyclerView = mXRecyclerView;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateTable();
                            }
                        }, 1000);
                    }
                })
                .setOnItemClickListenter(new LockTableView.OnItemClickListenter() {
                    @Override
                    public void onItemClick(View item, int position) {
                        Log.e("点击事件", position + "");
                        if (memberManagementBeans.get(position - 1).userType >= 1) {
                            currPage = -1;
                            subName = memberManagementBeans.get(position - 1).userName;

                            getQuerySalaryBonus();
                        }
                    }
                })
                .setOnItemLongClickListenter(new LockTableView.OnItemLongClickListenter() {
                    @Override
                    public void onItemLongClick(View item, int position) {
                        Log.e("长按事件", position + "");
                    }
                })
                .setOnCellClickListenter(new LockTableView.OnCellClickListenter() {
                    @Override
                    public void onCellClick(View item, int row, int column, String data) {
                        MemberManagementBean bean = memberManagementBeans.get(row);
                        if (data.equals(ACTION_LIFT_POINT)) {
                            prepareEditPointHETIANXIA(bean.userName, bean.point, ACTION_LIFT_POINT);
                        } else if (data.equals(ACTION_TRANSFORM)) {
                            prepareTransfer(bean.userName);
                        } else if (data.equals(ACTION_BONUS)) {
//
                            if (Config.custom_flag.equals(BaseConfig.FLAG_QINGFENG)) {
                                ActBonusQingfeng.launch(ActMemberManagementHETIANXIA.this, bean);
                            } else {
                                ActBonusHETIANXIA.launch(ActMemberManagementHETIANXIA.this, bean);
                            }

                        } else if (data.equals(ACTION_ORDER_SALARY)) {
                            initOrderBonus(bean);
                        } else if (data.equals(ACTION_SALARY)) {
                            ActSalary.launch(ActMemberManagementHETIANXIA.this, bean);
                        }

                    }
                }).setOnItemSeletor(R.color.selection)//设置Item被选中颜色
                .show(); //显示表格,此方法必须调用
        mLockTableView.getTableScrollView().setPullRefreshEnabled(true);
        mLockTableView.getTableScrollView().setLoadingMoreEnabled(true);
        mLockTableView.getTableScrollView().setRefreshProgressStyle(ProgressStyle.BallRotate);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void prepareTransfer(final String name) {

        HttpAction.prepareTransfer(name, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                super.setupEntity(entity);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (code == 0 && error == 0) {
                    ActTransfer.launch(ActMemberManagementHETIANXIA.this, name);
                } else {
                    Toasts.show(ActMemberManagementHETIANXIA.this, msg, false);
                }

                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onGetString(String str) {
                return super.onGetString(str);
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(ActMemberManagementHETIANXIA.this, content);
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    /**
     * type 1亏损  2中奖  3日工资
     *
     * @param
     */
    private void initOrderBonus(final MemberManagementBean bean) {
        HttpAction.initOrderBonus(null, bean.userName, 3, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
                Search_bt.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActMemberManagementHETIANXIA.this, "查询中");
                    }
                });
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                super.setupEntity(entity);
                entity.putField("data", BonusBean.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (code == 0 && error == 0) {
                    bonusBean = getField(extra, "data", null);

                    dialog = new MaterialDialog(ActMemberManagementHETIANXIA.this);
                    View view = LayoutInflater.from(ActMemberManagementHETIANXIA.this).inflate(R.layout.dialog_order_salary_point, null);
                    final TextView tv_username = (TextView) view.findViewById(R.id.tv_username);
                    final EditText etPoint = (EditText) view.findViewById(R.id.etPoint);
                    TextView hint = view.findViewById(R.id.tv_hint);
                    Button btnConfirm = (Button) view.findViewById(R.id.btnConfirm);
                    TextView tvClose = (TextView) view.findViewById(R.id.tv_close);
                    tv_username.setText(bean.userName);
                    hint.setText(String.format(getText(R.string.dandanrgz_range_hint).toString(), bonusBean.getSetMinPoint().floatValue() * 100, bonusBean.getSetMaxPoint().floatValue() * 100));
                    dialog.setView(view);
                    dialog.setBackgroundResource(0);
                    dialog.show();

                    if (!bonusBean.isCanModify()) {
                        hint.setVisibility(View.GONE);
                        etPoint.setEnabled(false);
                        tvClose.setVisibility(View.INVISIBLE);
                        etPoint.setText("" + (bonusBean.getUserMinPoint().multiply(BigDecimal.valueOf(100))));
                        btnConfirm.setText("关闭");
                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                    } else {
                        tvClose.setVisibility(View.VISIBLE);
                        etPoint.setEnabled(true);
                        btnConfirm.setText("签订契约");
                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Float point = Float.parseFloat(etPoint.getText().toString()) / 100;
                                    if (bonusBean.getSetMinPoint().compareTo(BigDecimal.valueOf(point)) > 0
                                            || bonusBean.getSetMaxPoint().compareTo(BigDecimal.valueOf(point)) < 0) {
                                        Toasts.show(ActMemberManagementHETIANXIA.this, "请输入正确的范围");
                                        return;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (Strs.isEmpty(etPoint.getText().toString())) {
                                    Toasts.show(ActMemberManagementHETIANXIA.this, "请输入正确的比例");
                                } else {
                                    updateBonus("" + bean.userId, bonusBean.getType(), etPoint.getText().toString());
                                }
                            }
                        });
                        tvClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }

                } else {
                    Toasts.show(ActMemberManagementHETIANXIA.this, msg);
                }

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

                Dialogs.hideProgressDialog(ActMemberManagementHETIANXIA.this);
            }
        });
    }

    private void updateBonus(String userid, int type, String toPercent) {

        float value = Float.parseFloat(toPercent);

        HttpAction.updateBonus(null, userid, type, "" + (value / 100), new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                super.setupEntity(entity);

            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
                Dialogs.showProgressDialog(ActMemberManagementHETIANXIA.this, "加载中...");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (error == 0 && code == 0) {
                    if (null != dialog) {
                        dialog.dismiss();
                    }

                    Toasts.show(ActMemberManagementHETIANXIA.this, msg, true);
                    currPage = -1;

                } else {
                    Toasts.show(ActMemberManagementHETIANXIA.this, msg, false);
                }

                DeviceUtil.hideInputKeyboard(ActMemberManagementHETIANXIA.this);

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
                Dialogs.hideProgressDialog(ActMemberManagementHETIANXIA.this);
            }
        });
    }

    private void getQuerySalaryBonus() {
        HttpAction.getQuerySalaryBonus(new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", BonusSalaryBean.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (error == 0 && code == 0) {
                    bonusSalaryBean = getField(extra, "data", null);
                } else {
                    Toasts.show(ActMemberManagementHETIANXIA.this, msg);
                }
                updateTable();
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
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBuyedInCartEvent(TransferSuccessEvent event) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("现有表格数据", mTableDatas.toString());
                currPage = -1;
                updateTable();
            }
        }, 200);
    }
}
