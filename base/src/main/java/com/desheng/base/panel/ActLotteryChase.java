package com.desheng.base.panel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.global.Config;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Maps;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.event.BuyedLotteryEvent;
import com.desheng.base.event.ChasedLotteryEvent;
import com.desheng.base.event.ClearLotteryPlaySelectedEvent;
import com.desheng.base.event.DeleteFromCartEvent;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LotteryChase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.PagerSlidingTabStrip;
import com.pearl.view.SpaceTopDecoration;
import com.pearl.view.ToggleImageButton;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * 彩票购物车
 * Created by lee on 2018/3/7.
 */
public class ActLotteryChase extends AbAdvanceActivity implements FragLotteryPursue.IBuildListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String TYPE_FanBei = "TYPE_FanBei";
    public static final String TYPE_TongBei = "TYPE_TongBei";
    public static final String TYPE_TongLiRen = "TYPE_TongLiRen";

    private ImageView ivListFoot;
    private PagerSlidingTabStrip tsTabs;
    private ViewPager vpContent;
    private View vgStopGroup;
    private ToggleImageButton cbStopBtn;

    private FrameLayout srlRefresh;
    private RecyclerView rvPusrsum;

    private ArrayList<LotteryChase> listSelected = new ArrayList<>();
    private AdapterPursum adapter;
    private TextView tvMoney;
    private TextView tvHit;
    private TextView tvBuy;

    private int lotteryId;
    private ILotteryKind lottery;
    private ArrayList<HashMap<String, Object>> listNumAddedCart;

    /**
     * @param activity
     * @param lotteryId        彩票id
     * @param listNumAddedCart
     */
    public static void launch(Activity activity, int lotteryId, ArrayList<HashMap<String, Object>> listNumAddedCart) {
        Intent itt = new Intent(activity, ActLotteryChase.class);
        itt.putExtra("lotteryId", lotteryId);
        itt.putExtra("listNumAddedCart", listNumAddedCart);
        activity.startActivity(itt);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_lottery_chase;
    }

    @Override
    protected void init() {
        lotteryId = getIntent().getIntExtra("lotteryId", 0);
        lottery = CtxLottery.getIns().findLotteryKind(lotteryId);
        listNumAddedCart = (ArrayList<HashMap<String, Object>>) getIntent().getSerializableExtra("listNumAddedCart");


        setToolbarLeftBtn(UIHelper.BACK_WHITE_CIRCLE_ARROW);
        setToolbarTitleCenter("追号");
        if (UIHelper.toolbarBgResId > 0)
            setToolbarBgImage(UIHelper.toolbarBgResId);
        else
            setToolbarBgColor(R.color.colorPrimary);
        setToolbarTitleCenterColor(com.desheng.base.R.color.white);
        setStatusBarTranslucentAndLightContentWithPadding();

        tsTabs = (PagerSlidingTabStrip) findViewById(R.id.tsTabs);
        vpContent = (ViewPager) findViewById(R.id.vpContent);
        vpContent.setAdapter(new PursueAdapter(getSupportFragmentManager()));
        tsTabs.setViewPager(vpContent);

        vgStopGroup = findViewById(R.id.vgStopGroup);
        cbStopBtn = (ToggleImageButton) findViewById(R.id.cbStopBtn);
        vgStopGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbStopBtn.setChecked(!cbStopBtn.isChecked());
            }
        });

        srlRefresh = findViewById(R.id.srlRefresh);
//        srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
//        srlRefresh.setOnRefreshListener(this);

        rvPusrsum = (RecyclerView) findViewById(R.id.rvPusrsum);
        rvPusrsum.setLayoutManager(Views.genLinearLayoutManagerV(this));
        adapter = new AdapterPursum(this);
        adapter.bindToRecyclerView(rvPusrsum);
        rvPusrsum.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LotteryChase pursue = (LotteryChase) adapter.getData().get(position);
                ToggleImageButton toggle = (ToggleImageButton) adapter.getViewByPosition(position, R.id.tibCheck);
                toggle.setChecked(!toggle.isChecked());
                if (toggle.isChecked()) {
                    listSelected.add(pursue);
                    if (pursue.getMulti() <= 0) {
                        pursue.setMulti(1);
                    }
                    EditText etMulti = (EditText) adapter.getViewByPosition(position, R.id.etMulti);
                    etMulti.setText(String.valueOf(pursue.getMulti()));
                    etMulti.setFocusable(true);
                    etMulti.setFocusableInTouchMode(true);
                    etMulti.setBackgroundResource(R.drawable.bg_et_chase_list_item_times_white);
                } else {
                    listSelected.remove(pursue);
                    if (pursue.getMulti() > 0) {
                        pursue.setMulti(0);
                    }
                    EditText etMulti = (EditText) adapter.getViewByPosition(position, R.id.etMulti);
                    etMulti.setText(String.valueOf(pursue.getMulti()));
                    etMulti.setFocusable(false);
                    etMulti.setFocusableInTouchMode(false);
                    etMulti.setBackgroundResource(R.drawable.bg_et_chase_list_item_times_black);
                }
            }
        });

        tvMoney = (TextView) findViewById(R.id.tvMoney);
        tvHit = (TextView) findViewById(R.id.tvHit);
        tvBuy = (TextView) findViewById(R.id.tvBuy);
        tvBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyChase();
            }
        });

        refresh();
    }

    private String playname;

    @Override
    public void onBuild(final String type, final int issueCount, final int initMulti, final int maxMulti, final double percent, final HashMap<String, Object> map, final BuildCallback callback) {
        HttpAction.getLotteryPursue(this, lottery.getCode(), new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
//                srlRefresh.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        srlRefresh.setRefreshing(true);
//                    }
//                });
                srlRefresh.setEnabled(false);
            }

            @Override
            public boolean onGetString(String str) {
                listSelected.clear();
                ArrayList<LotteryChase> listData = new Gson().fromJson(str.replace("\n", ""), new TypeToken<ArrayList<LotteryChase>>() {
                }.getType());

                double money = 0;
                double award = 0;

                for (int i = 0; i < listNumAddedCart.size(); i++) {
                    money = money + (Double) listNumAddedCart.get(i).get(CtxLottery.Order.MONEY);
                    award = award + (Double) listNumAddedCart.get(i).get(CtxLottery.Order.AWARD);
                    playname = (String) listNumAddedCart.get(i).get(CtxLottery.Order.PLAY_NAME);
                }

                if (type.equals(TYPE_FanBei)) {

                    int intevalCtn = 1;

                    int multiCtn = initMulti;
                    ArrayList<Integer> listDelete = new ArrayList<>();
                    for (int i = 0; i < listData.size(); i++) {
                        LotteryChase chase = listData.get(i);
                        if (i < issueCount) {
                            if (map.get(CtxLottery.Chase.SIGN).equals("plus")) {
                                if (intevalCtn > Maps.value(map, CtxLottery.Chase.INTEVAL, 0)) {
                                    multiCtn += Maps.value(map, CtxLottery.Chase.MULTI, 0);
                                    intevalCtn = 1;
                                }
                            } else if (map.get(CtxLottery.Chase.SIGN).equals("multi")) {
                                if (intevalCtn > Maps.value(map, CtxLottery.Chase.INTEVAL, 0)) {
                                    multiCtn *= Maps.value(map, CtxLottery.Chase.MULTI, 0);
                                    intevalCtn = 1;
                                }
                            }

                            if (multiCtn <= 0) {
                                Toasts.show(ActLotteryChase.this, "倍数溢出!！", false);
                                break;
                            }

                            chase.setMulti(multiCtn);
                            chase.setMoney(money * multiCtn * 1.0);
                            listSelected.add(chase);
                            intevalCtn++;
                        }
                    }
                } else if (TYPE_TongLiRen.equals(type)) {

                    listSelected.addAll(getLotteryChaseList(listData, issueCount, initMulti, maxMulti, percent / 100, money, award));

                } else if (TYPE_TongBei.equals(type)) {
                    for (int i = 0; i < listData.size(); i++) {
                        LotteryChase chase = listData.get(i);
                        if (i < issueCount) {
                            chase.setMulti(initMulti);
                            chase.setMoney(money * initMulti * 1.0);

                            listSelected.add(chase);
                        }
                    }
                }

                adapter.setNewData(new ArrayList<>(listSelected));
                updateBottomText();
                callback.onResult(listSelected.size());
                return true;
            }

            @Override
            public void onAfter(int id) {
                srlRefresh.setEnabled(true);
//                srlRefresh.setRefreshing(false);
            }
        });


    }

    private void buyChase() {
        ArrayList<HashMap<String, Object>> orderList = new ArrayList<>();
        for (int i = 0; i < listNumAddedCart.size(); i++) {
            HashMap<String, Object> map = listNumAddedCart.get(i);
            HashMap<String, Object> orderMap = new HashMap<>();
            orderMap.put(CtxLottery.Chase.LOTTERY, map.get(CtxLottery.Order.LOTTERY));
            orderMap.put(CtxLottery.Chase.METHOD, map.get(CtxLottery.Order.METHOD));
            orderMap.put(CtxLottery.Chase.CONTENT, map.get(CtxLottery.Order.CONTENT));
            orderMap.put(CtxLottery.Chase.MODEL, map.get(CtxLottery.Order.MODEL));
            orderMap.put(CtxLottery.Chase.CODE, map.get(CtxLottery.Order.CODE));
            orderMap.put(CtxLottery.Chase.COMPRESS, map.get(CtxLottery.Order.COMPRESS));
            orderList.add(orderMap);
        }

        ArrayList<HashMap<String, Object>> planList = new ArrayList<>();
        if (listSelected.size() <= 0) {
            Toasts.show(ActLotteryChase.this, "请选择期号!", false);
            return;
        }
        for (int i = 0; i < listSelected.size(); i++) {
            HashMap<String, Object> planMap = new HashMap<>();
            planMap.put(CtxLottery.Chase.ISSUE, listSelected.get(i).getIssue());
            planMap.put(CtxLottery.Chase.MULTIPLE, listSelected.get(i).getMulti());
            planList.add(planMap);
        }

        HttpAction.addChase(this, orderList, planList, cbStopBtn.isChecked(), new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActLotteryChase.this, "提交中...");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    UserManager.getIns().sendBroadcaset(ActLotteryChase.this, UserManager.EVENT_USER_INFO_UNPDATED, null);

                    //清除选球数据
                    EventBus.getDefault().post(new ClearLotteryPlaySelectedEvent());

                    //刷新玩法页面记录
                    EventBus.getDefault().post(new BuyedLotteryEvent());

                    //删除所有添加的购物车注数
                    EventBus.getDefault().post(new DeleteFromCartEvent(true, null));

                    //刷新购物车页面记录
                    EventBus.getDefault().post(new ChasedLotteryEvent());

                    showLotteryBuyOkDialog(ActLotteryChase.this);
                    return true;
                }

                Toasts.show(ActLotteryChase.this, msg, code == 0);
                return true;
            }

            @Override
            public void onAfter(int id) {
                Dialogs.hideProgressDialog(ActLotteryChase.this);
            }
        });
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        refresh();
    }

    public void refresh() {
        HttpAction.getLotteryPursue(this, lottery.getCode(), new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
//                srlRefresh.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        srlRefresh.setRefreshing(true);
//                    }
//                });
                srlRefresh.setEnabled(false);
            }

            @Override
            public boolean onGetString(String str) {
                listSelected.clear();
                ArrayList<LotteryChase> listData = new Gson().fromJson(str.replace("\n", ""), new TypeToken<ArrayList<LotteryChase>>() {
                }.getType());
                adapter.setNewData(listData);
                return true;
            }

            @Override
            public void onAfter(int id) {
                srlRefresh.setEnabled(true);
//                srlRefresh.setRefreshing(false);
            }
        });
    }

    private void updateBottomText() {
        float totalMoney = 0.0f;
        int totalIssue = 0;
        for (int i = 0; i < listSelected.size(); i++) {
            totalMoney += listSelected.get(i).getMoney();
            totalIssue++;
        }
        tvMoney.setText("共" + Nums.formatDecimal(totalMoney, 3) + "元");
        tvHit.setText("选中 " + totalIssue + " 期");
    }


    private class PursueAdapter extends FragmentPagerAdapter {

        String[] TITLES = {"翻倍追号", "同倍追号", "利润率追号"};
        Fragment[] FRAGMENTS = new Fragment[]{FragLotteryPursue.newIns(FragLotteryPursue.TYPE_FanBei, lottery.getCode()),
                FragLotteryPursue.newIns(FragLotteryPursue.TYPE_TongBei, lottery.getCode()),
                FragLotteryPursue.newIns(FragLotteryPursue.TYPE_TongLiRen, lottery.getCode())};

        public PursueAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FRAGMENTS[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return FRAGMENTS.length;
        }
    }

    protected class AdapterPursum extends BaseQuickAdapter<LotteryChase, AdapterPursum.PursumHolder> {

        private Context ctx;

        public AdapterPursum(Context ctx) {
            super(R.layout.item_lottery_pursue_list, new ArrayList<LotteryChase>());
            this.ctx = ctx;
        }

        @Override
        protected void convert(final PursumHolder holder, LotteryChase item) {
            holder.tvIssueNo.setText(item.getIssue());
//            holder.tvSuspendDate.setText(item.getStopTime());
            holder.tibCheck.setChecked(listSelected.contains(item));
            holder.tvMoney.setText(Nums.formatDecimal(item.getMoney(), 3));
            holder.etMulti.removeTextChangedListener(holder.watcher);
            holder.etMulti.setText(String.valueOf(item.getMulti()));
            holder.etMulti.setTag(item);
            if (item.getMulti() > 0) {
                holder.etMulti.setBackgroundResource(R.drawable.bg_et_chase_list_item_times_white);
                holder.etMulti.setFocusable(true);
                holder.etMulti.setFocusableInTouchMode(true);
            } else {
                holder.etMulti.setBackgroundResource(R.drawable.bg_et_chase_list_item_times_black);
                holder.etMulti.setFocusable(false);
                holder.etMulti.setFocusableInTouchMode(false);
            }

            holder.watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    LotteryChase item = (LotteryChase) holder.etMulti.getTag();
                    if (Strs.parse(s.toString(), 0) > 10000) {
                        s.clear();
                        s.append("10000");
                    }
                    item.setMulti(Strs.parse(s.toString(), 0));
                    item.setMoney(1.0 * item.getMulti());
                    holder.tvMoney.setText(Nums.formatDecimal(item.getMoney(), 3));
                    updateBottomText();
                }
            };

            holder.etMulti.addTextChangedListener(holder.watcher);
        }


        public class PursumHolder extends BaseViewHolder {
            public ToggleImageButton tibCheck;
            //            public TextView tvIssueNoLab;
            public TextView tvIssueNo;
            //            public TextView tvSuspendDateLab;
//            public TextView tvSuspendDate;
//            public TextView tvMultiLab1;
            public EditText etMulti;
            public TextView tvMultiLab2;
            public TextView tvMoney;
            public TextWatcher watcher;

            public PursumHolder(View view) {
                super(view);
                tibCheck = (ToggleImageButton) view.findViewById(R.id.tibCheck);
//                tvIssueNoLab = (TextView) view.findViewById(R.id.tvIssueNoLab);
                tvIssueNo = (TextView) view.findViewById(R.id.tvIssueNo);
//                tvSuspendDateLab = (TextView) view.findViewById(R.id.tvSuspendDateLab);
//                tvSuspendDate = (TextView) view.findViewById(R.id.tvSuspendDate);
//                tvMultiLab1 = (TextView) view.findViewById(R.id.tvMultiLab1);
                etMulti = (EditText) view.findViewById(R.id.etMulti);
                tvMultiLab2 = (TextView) view.findViewById(R.id.tvMultiLab2);
                tvMoney = (TextView) view.findViewById(R.id.tvMoney);
            }
        }

    }

    /**
     * 计算利润率
     * count 追号期数
     * sMultiple 开始倍数
     * maxMultiple 最大倍投
     * minProfit 最低利润率（百分比）
     * money 单倍金额
     * prize 单倍奖金
     */
    private List<LotteryChase> getLotteryChaseList(List<LotteryChase> lotteryChases, int count, int sMultiple, int maxMultiple, double minProfit, double money, double prize) {
        List<LotteryChase> chaseList = new ArrayList<>();

        double totalMoney = 0;
        int multiple = sMultiple;
        for (int i = 0; i < count; i++) {
            if (lotteryChases.size() > count) {
                LotteryChase chase = lotteryChases.get(i);
                double thisMoney = 0;
                double thisPrize = 0;
                double thisProfit = 0;
                while (true) {
                    thisMoney = money * multiple;
                    thisPrize = prize * multiple;
                    double tempTotal = totalMoney + thisMoney;
                    thisProfit = (thisPrize - tempTotal) / tempTotal;
                    if (thisProfit >= minProfit) break;
                    if (multiple > maxMultiple) {
                        return chaseList;
                    }
                    multiple++;
                }
                totalMoney += thisMoney; // 累计投入
                chase.setMulti(multiple);
                chase.setMoney(thisMoney);
                chaseList.add(chase);
            }
        }
        return chaseList;
    }

    public void showLotteryBuyOkDialog(Context context) {
        final Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_lottery_play_buy_ok, null);
        root.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.setContentView(root);
        dialog.show();
    }

}
