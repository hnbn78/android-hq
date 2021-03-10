package com.desheng.base.panel;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.ab.view.MaterialDialog;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.adapter.NameChainAdapter;
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

import okhttp3.Request;

/**
 * 会员管理
 * Created by user on 2018/3/15.
 */

public class ActMemberManagement extends AbAdvanceActivity implements NameChainAdapter.OnItemClickListener {

    public static void launch(Activity act) {
        if (Config.custom_flag.equals(BaseConfig.FLAG_HETIANXIA)||Config.custom_flag.equals(BaseConfig.FLAG_JINDU)) {
            simpleLaunch(act, ActMemberManagementHETIANXIA.class);
        } else {
            simpleLaunch(act, ActMemberManagement.class);
        }
    }

    private Button Search_bt;
    private LinearLayout mContentView;
    private RecyclerView recycleView_Chain;
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
    private NameChainAdapter mAdapter;

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

    private List<String> subnamelist;
    private List<String> temp_list;
    private boolean isEnabled = true; //请求监听，用于频繁按钮阻塞器，而避免重复请求数据

    @Override
    protected void init() {
        subnamelist = new ArrayList<>();
        temp_list = new ArrayList<>();
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
        temp_list.add(0, UserManager.getIns().getMainUserName());
        subnamelist.add(0, UserManager.getIns().getMainUserName());
        mContentView = findViewById(R.id.contentView);
        recycleView_Chain = findViewById(R.id.recycleView_Chain);
        recycleView_Chain.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycleView_Chain.setLayoutManager(linearLayoutManager);
        mAdapter = new NameChainAdapter(this, subnamelist, this);
        recycleView_Chain.setAdapter(mAdapter);

        etAgent = findViewById(R.id.etAgent);
        etUser = findViewById(R.id.etUser);

        Search_bt = findViewById(R.id.button);
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

    @Override
    public void onItemClick(int position) {

        subName = subnamelist.get(position);
        subName = subName.contains(">") ? subName.replace(">", "") : subName;

        for (int i = position + 1; i < subnamelist.size(); i++) {
            temp_list.remove(subnamelist.get(i));
        }

        subnamelist.clear();
        subnamelist.addAll(temp_list);

        if (subnamelist.size() == 1) {
            subName = "";
        }

        etUser.setText(subName);

        mAdapter.notifyDataSetChanged();
        currPage = -1;
        getQuerySalaryBonus();

    }

    public void updateTable() {
        currPage++;
        getMemberManagementList();
    }

    /**
     * 获得会员管理
     */
    public void getMemberManagementList() {
        if (!isEnabled) return;
        isEnabled = false;
        HttpAction.getMemberManagement(userName, subName, currPage, PAGE_SIZE, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                isEnabled = true;
                if (currPage == 0) {
                    Dialogs.showProgressDialog(ActMemberManagement.this, "搜索中...");
                }
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                isEnabled = true;
                entity.putField("data", HttpAction.RespSearchMember.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                isEnabled = true;
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
                        mRowDatas.add(memberManagementBean.point);
                        mRowDatas.add("" + memberManagementBean.lastLoginTime + memberManagementBean.onlineStatus);

                        StringBuilder actionsSB = new StringBuilder();

                        if (Strs.isEmpty(subName) && Strs.isEmpty(userName)) { // 第一级会员目录
                            if (!UserManager.getIns().isHideImprovePoint()) {
                                actionsSB.append(ACTION_LIFT_POINT);
                                actionsSB.append("@");
                            }
                            actionsSB.append(ACTION_TRANSFORM);

                            if (UserManager.getIns().showBonusAndOrderSalary( memberManagementBean)) {
                                if (bonusSalaryBean != null && bonusSalaryBean.getBonus() != 0 && UserManager.getIns().showBonus()) {
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
                            mRowDatas.add(actionsSB.toString());

                        } else {
                            actionsSB.append(ACTION_TRANSFORM);
                            mRowDatas.add(actionsSB.toString());
                        }
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
            public boolean onError(int status, String content) {
                isEnabled = true;
                return super.onError(status, content);
            }

            @Override
            public void onAfter(int id) {
                isEnabled = true;
                if (currPage == 0) {
                    Dialogs.hideProgressDialog(ActMemberManagement.this);
                }
                if (ActMemberManagement.this.mXRecyclerView != null) {
                    ActMemberManagement.this.mXRecyclerView.refreshComplete();
                    ActMemberManagement.this.mXRecyclerView.loadMoreComplete();
                }
            }
        });
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
    private void prepareEditPoint(final String userName, final String point, final String tag) {
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
                        Dialogs.showProgressDialog(ActMemberManagement.this, "查询中");
                    }
                });
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    info = getField(extra, "data", null);

                    if (tag.equals(ACTION_BONUS)) {

                    } else if (tag.equals(ACTION_LIFT_POINT)) {
                        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(ActMemberManagement.this).inflate(R.layout.dialog_promote_point, null);
                        TextView tvBackPoint = viewGroup.findViewById(R.id.tv_back_point);
                        TextView tvRange = viewGroup.findViewById(R.id.tvRange);
                        TextView tv_account = viewGroup.findViewById(R.id.tv_account);
                        final EditText etPoint = viewGroup.findViewById(R.id.etPoint);
                        Button btnConfirm = viewGroup.findViewById(R.id.btnConfirm);
                        Button btnCancel = viewGroup.findViewById(R.id.btnCancel);
                        Dialogs.showCustomDialog(ActMemberManagement.this, viewGroup, true);
                        tv_account.setText(info.uAccount.username);
                        tvBackPoint.setText(point);
                        if (UserManager.getIns().getMainUserLevel() > 1
                                && null != info
                                && info.lotteryCodeRange != null
                                && (info.lotteryCodeRange.minPoint < info.lotteryCodeRange.maxPoint)) {
                            etPoint.setEnabled(true);
                            tvRange.setText(info.lotteryCodeRange.minPoint + "~" + info.lotteryCodeRange.maxPoint);
                            btnConfirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String lotteryPoint = etPoint.getText().toString();
                                    if (Config.custom_flag.equals(BaseConfig.FLAG_ZHONGXIN)) {
                                        String[] numParts = lotteryPoint.split("\\.");
                                        if (numParts.length == 2 && numParts[1].length() > 1) {
                                            Toasts.show(ActMemberManagement.this, "返点只能输入一位小数", false);
                                            return;
                                        }
                                    }

                                    double point = Views.getValue(etPoint, 0.0);
                                    if (point <= 0 || point < info.lotteryCodeRange.minPoint || point > info.lotteryCodeRange.maxPoint) {
                                        Toasts.show(ActMemberManagement.this, "请输入正确的点数!", false);
                                        return;
                                    }
                                    commit(userName, point);
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
                            etPoint.setText("" + info.lotteryCodeRange.maxPoint);
                            tvRange.setText("无法调整该用户返点");
                            btnConfirm.setVisibility(View.GONE);
                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Dialogs.dissmisCustomDialog();
                                }
                            });
                        }


                    } else if (tag.equals(ACTION_ORDER_SALARY)) {//单单

                    }

                } else {
                    Toasts.show(ActMemberManagement.this, msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                Search_bt.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.hideProgressDialog(ActMemberManagement.this);
                    }
                });
            }
        });
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
                        Dialogs.showProgressDialog(ActMemberManagement.this, "查询中");
                    }
                });
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    Toasts.show(ActMemberManagement.this, "修改成功", true);
                } else {
                    Toasts.show(ActMemberManagement.this, msg, false);
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
                        Dialogs.hideProgressDialog(ActMemberManagement.this);
                    }
                });
            }
        });
    }

    private void initLockTable() {

        final ArrayList<String> mfristData = new ArrayList<String>();

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
                        ActMemberManagement.this.mXRecyclerView = mXRecyclerView;
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
                        ActMemberManagement.this.mXRecyclerView = mXRecyclerView;
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
                        if (memberManagementBeans.get(position - 1).userType >= 1 && isEnabled ) {
                            currPage = -1;
                            subnamelist.clear();
                            subName = memberManagementBeans.get(position - 1).userName;
                            temp_list.add(temp_list.size(), ">" + subName);
                            temp_list = Strs.removeDuplicateinOrder(temp_list);
                            subnamelist.addAll(temp_list);
                            mAdapter.notifyDataSetChanged();
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
                            prepareEditPoint(bean.userName, bean.point, ACTION_LIFT_POINT);
                        } else if (data.equals(ACTION_TRANSFORM)) {
                            prepareTransfer(bean.userName);
                        } else if (data.equals(ACTION_BONUS)) {
//
                            if (Config.custom_flag.equals(BaseConfig.FLAG_QINGFENG)) {
                                ActBonusQingfeng.launch(ActMemberManagement.this, bean);
                            } else {
                                ActBonus.launch(ActMemberManagement.this, bean);
                            }

                        } else if (data.equals(ACTION_ORDER_SALARY)) {
                            initOrderBonus(bean);
                        } else if (data.equals(ACTION_SALARY)) {
                            ActSalary.launch(ActMemberManagement.this, bean);
                        }

                    }
                }).setOnItemSeletor(R.color.selection)//设置Item被选中颜色
                .show(); //显示表格,此方法必须调用
        mLockTableView.getTableScrollView().setPullRefreshEnabled(true);
        mLockTableView.getTableScrollView().setLoadingMoreEnabled(true);
        mLockTableView.getTableScrollView().setRefreshProgressStyle(ProgressStyle.BallRotate);

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
                    ActTransfer.launch(ActMemberManagement.this, name);
                } else {
                    Toasts.show(ActMemberManagement.this, msg, false);
                }

                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onGetString(String str) {
                return super.onGetString(str);
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(ActMemberManagement.this, content);
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
                        Dialogs.showProgressDialog(ActMemberManagement.this, "查询中");
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

                    dialog = new MaterialDialog(ActMemberManagement.this);
                    View view = LayoutInflater.from(ActMemberManagement.this).inflate(R.layout.dialog_order_salary_point, null);
                    final TextView tv_username = (TextView) view.findViewById(R.id.tv_username);
                    final EditText etPoint = (EditText) view.findViewById(R.id.etPoint);
                    TextView tvHint = view.findViewById(R.id.tv_hint);
                    Button btnConfirm = (Button) view.findViewById(R.id.btnConfirm);
                    TextView tvClose = (TextView) view.findViewById(R.id.tv_close);
                    tv_username.setText(bean.userName);
                    tvHint.setText(String.format(getText(R.string.dandanrgz_range_hint).toString(), bonusBean.getSetMinPoint().floatValue() * 100, bonusBean.getSetMaxPoint().floatValue() * 100));
                    dialog.setView(view);
                    dialog.setBackgroundResource(0);
                    dialog.show();

                    if (!bonusBean.isCanModify()) {
                        tvHint.setVisibility(View.GONE);
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
                                if (bean.userType == 1
                                        || Config.custom_flag.equals(BaseConfig.FLAG_ZHONGXIN)) {
                                    try {
                                        Float point = Float.parseFloat(etPoint.getText().toString()) / 100;
                                        if (bonusBean.getSetMinPoint().compareTo(BigDecimal.valueOf(point)) > 0
                                                || bonusBean.getSetMaxPoint().compareTo(BigDecimal.valueOf(point)) < 0) {
                                            Toasts.show(ActMemberManagement.this, "请输入正确的范围");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    if (Strs.isEmpty(etPoint.getText().toString())) {
                                        Toasts.show(ActMemberManagement.this, "请输入正确的比例");
                                    } else {
                                        updateBonus("" + bean.userId, bonusBean.getType(), etPoint.getText().toString());
                                    }
                                } else {
                                    Toasts.show(ActMemberManagement.this, "下级不是代理，不能签订契约", false);
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
                    Toasts.show(ActMemberManagement.this, msg);
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

                Dialogs.hideProgressDialog(ActMemberManagement.this);
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
                Dialogs.showProgressDialog(ActMemberManagement.this, "加载中...");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (error == 0 && code == 0) {
                    if (null != dialog) {
                        dialog.dismiss();
                    }

                    Toasts.show(ActMemberManagement.this, msg, true);
                    currPage = -1;

                } else {
                    Toasts.show(ActMemberManagement.this, msg, false);
                }

                DeviceUtil.hideInputKeyboard(ActMemberManagement.this);

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
                Dialogs.hideProgressDialog(ActMemberManagement.this);
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
                    Toasts.show(ActMemberManagement.this, msg);
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
