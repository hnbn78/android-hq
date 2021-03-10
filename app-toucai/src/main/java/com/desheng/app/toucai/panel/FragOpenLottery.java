package com.desheng.app.toucai.panel;

import android.graphics.drawable.GradientDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ab.http.AbHttpResult;
import com.ab.util.Strs;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.model.LotteryCodeUpdateBean;
import com.desheng.app.toucai.util.Utils;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LotteryOpenHistory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbBaseFragment;
import com.shark.tc.R;
import com.zhy.view.flowlayout.FlowLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 开奖记录
 */
public class FragOpenLottery extends AbBaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mRecycleView;
    private SwipeRefreshLayout refreshLayout;

    private int lotteryId;
    private ILotteryKind lotteryKind;
    private RadioGroup trend_RadioGroup;

    private ArrayList<LotteryOpenHistory> mlistOpen = new ArrayList<>();
    private BaseQuickAdapter<LotteryOpenHistory, BaseViewHolder> mCodeAdapter;
    private BaseQuickAdapter<LotteryOpenHistory, BaseViewHolder> mDanShuangAdapter;
    private BaseQuickAdapter<LotteryOpenHistory, BaseViewHolder> mDaxiaoAdapter;

    Integer[] redbo = {1, 2, 7, 8, 12, 13, 18, 19, 23, 24, 29, 30, 34, 35, 40, 45, 46};//红波
    Integer[] bluebo = {3, 4, 9, 10, 14, 15, 20, 25, 26, 31, 36, 37, 41, 42, 47, 48};//蓝波
    Integer[] greenbo = {5, 6, 11, 16, 17, 21, 22, 27, 28, 32, 33, 38, 39, 43, 44, 49};//绿波

    List<Integer> blueboList = new ArrayList<>();
    List<Integer> redboList = new ArrayList<>();
    List<Integer> greenboList = new ArrayList<>();

    public static FragOpenLottery newInstance(int lotteryId) {
        FragOpenLottery fragOpenLottery = new FragOpenLottery();
        fragOpenLottery.lotteryId = lotteryId;
        return fragOpenLottery;
    }

    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_lhc_history;
    }

    @Override
    public void init(View root) {
        EventBus.getDefault().register(this);
        refreshLayout = root.findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        lotteryKind = CtxLottery.getIns().findLotteryKind(lotteryId);
        mRecycleView = root.findViewById(R.id.recycleView);
        trend_RadioGroup = root.findViewById(R.id.trend_RadioGroup);
        updateStaticOpen();
        mRecycleView.setLayoutManager(new LinearLayoutManager(context));

        blueboList.addAll(Arrays.asList(bluebo));
        redboList.addAll(Arrays.asList(redbo));
        greenboList.addAll(Arrays.asList(greenbo));

        //号码
        if (Strs.isEqual("LHC", lotteryKind.getPlayCategory())) {

            ((TextView) root.findViewById(R.id.rb_btn2)).setText("总和");
            ((TextView) root.findViewById(R.id.rb_btn3)).setText("特码");

            mCodeAdapter = new BaseQuickAdapter<LotteryOpenHistory, BaseViewHolder>(R.layout.item_lottery_play_code_lhc) {
                @Override
                protected void convert(BaseViewHolder helper, LotteryOpenHistory item) {
                    if (item == null) {
                        return;
                    }
                    helper.setText(R.id.tv_time, Utils.getDate(item.getTime() * 1000, "HH:mm"));
                    helper.setText(R.id.tv_issue, item.getIssue());

                    FlowLayout flowLayout = (FlowLayout) helper.getView(R.id.llBallsBlue);
                    flowLayout.removeAllViews();

                    if (Strs.isEmpty(item.getCode())) {
                        helper.setText(R.id.tv_time, "\t\t\t\t\t\t\t等待开奖...⏳ ");
                        flowLayout.setVisibility(View.GONE);
                        helper.getView(R.id.ll_tema).setVisibility(View.GONE);
                        helper.getView(R.id.add).setVisibility(View.GONE);
                        return;
                    }
                    helper.getView(R.id.ll_tema).setVisibility(View.VISIBLE);
                    helper.getView(R.id.add).setVisibility(View.VISIBLE);
                    flowLayout.setVisibility(View.VISIBLE);

                    //开奖号球
                    String[] arrCode = item.getCode().split(",");
                    String lastcode = arrCode[arrCode.length - 1];

                    helper.setText(R.id.tvball, lastcode);
                    helper.setText(R.id.tvname, UserManager.getIns().getMapZodiacName().get(Strs.parse(lastcode, 0)));
                    GradientDrawable bgball = (GradientDrawable) helper.getView(R.id.tvball).getBackground();
                    if (blueboList.contains(Integer.parseInt(lastcode))) {
                        bgball.setColor(context.getResources().getColor(R.color.blue_2396f7));
                    } else if (redboList.contains(Integer.parseInt(lastcode))) {
                        bgball.setColor(context.getResources().getColor(R.color.red_ff2c66));
                    } else if (greenboList.contains(Integer.parseInt(lastcode))) {
                        bgball.setColor(context.getResources().getColor(R.color.green_09af1c));
                    }

                    LinearLayout balllayout = null;
                    for (int i = 0; i < arrCode.length - 1; i++) {
                        balllayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.view_ball_lhc, flowLayout, false);
                        TextView ball = (TextView) balllayout.findViewById(R.id.ball);
                        TextView name = (TextView) balllayout.findViewById(R.id.name);
                        GradientDrawable bg = (GradientDrawable) ball.getBackground();

                        if (blueboList.contains(Integer.parseInt(arrCode[i]))) {
                            bg.setColor(context.getResources().getColor(R.color.blue_2396f7));
                        } else if (redboList.contains(Integer.parseInt(arrCode[i]))) {
                            bg.setColor(context.getResources().getColor(R.color.red_ff2c66));
                        } else if (greenboList.contains(Integer.parseInt(arrCode[i]))) {
                            bg.setColor(context.getResources().getColor(R.color.green_09af1c));
                        }
                        ball.setText(arrCode[i]);
                        name.setText(UserManager.getIns().getMapZodiacName().get(Strs.parse(arrCode[i], 0)));
                        flowLayout.addView(balllayout);
                    }
                    flowLayout.requestLayout();
                }
            };


            //总和
            mDaxiaoAdapter = new BaseQuickAdapter<LotteryOpenHistory, BaseViewHolder>(R.layout.view_ball_lhc_zonghe) {
                @Override
                protected void convert(BaseViewHolder helper, LotteryOpenHistory item) {
                    if (item == null) {
                        return;
                    }
                    helper.setText(R.id.tv_time, Utils.getDate(item.getTime() * 1000, "HH:mm"));
                    helper.setText(R.id.tv_issue, item.getIssue());

                    FlowLayout flowLayout = (FlowLayout) helper.getView(R.id.llBallsBlue);
                    flowLayout.removeAllViews();

                    if (Strs.isEmpty(item.getCode())) {
                        helper.setText(R.id.tv_time, "\t\t\t\t\t\t\t等待开奖...⏳ ");
                        flowLayout.setVisibility(View.GONE);
                        helper.getView(R.id.zonghe).setVisibility(View.GONE);
                        return;
                    }
                    helper.getView(R.id.zonghe).setVisibility(View.VISIBLE);
                    flowLayout.setVisibility(View.VISIBLE);

                    //开奖号球
                    String[] arrCode = item.getCode().split(",");


                    TextView tvNumDS = null;
                    TextView tvNumDx = null;
                    TextView tvNumBB = null;
                    int zongheNum = 0;
                    for (int i = 0; i < arrCode.length; i++) {
                        zongheNum += Integer.parseInt(arrCode[i]);
                    }

                    helper.setText(R.id.zonghe, String.valueOf(zongheNum));

                    tvNumDS = (TextView) LayoutInflater.from(context).inflate(R.layout.view_ball_blue_lhc_zonghe, flowLayout, false);
                    tvNumDS.setText("总和" + Utils.getLotteryCodeTypeDS(String.valueOf(zongheNum)));
                    if (Strs.isEqual("单", Utils.getLotteryCodeTypeDS(String.valueOf(zongheNum)))) {
                        GradientDrawable bg = (GradientDrawable) tvNumDS.getBackground();
                        bg.setColor(context.getResources().getColor(R.color.blue_2396f7));
                    } else {
                        GradientDrawable bg = (GradientDrawable) tvNumDS.getBackground();
                        bg.setColor(context.getResources().getColor(R.color.red_ff2c66));
                    }

                    tvNumDx = (TextView) LayoutInflater.from(context).inflate(R.layout.view_ball_blue_lhc_zonghe, flowLayout, false);
                    tvNumDx.setText("总和" + (zongheNum >= 175 ? "大" : "小"));
                    if (zongheNum >= 175) {
                        GradientDrawable bg = (GradientDrawable) tvNumDx.getBackground();
                        bg.setColor(context.getResources().getColor(R.color.blue_2396f7));
                    } else {
                        GradientDrawable bg = (GradientDrawable) tvNumDx.getBackground();
                        bg.setColor(context.getResources().getColor(R.color.red_ff2c66));
                    }

                    flowLayout.addView(tvNumDS);
                    flowLayout.addView(tvNumDx);

                    flowLayout.requestLayout();
                }
            };

            //特码
            mDanShuangAdapter = new BaseQuickAdapter<LotteryOpenHistory, BaseViewHolder>(R.layout.view_ball_lhc_tema) {
                @Override
                protected void convert(BaseViewHolder helper, LotteryOpenHistory item) {
                    if (item == null) {
                        return;
                    }
                    helper.setText(R.id.tv_time, Utils.getDate(item.getTime() * 1000, "HH:mm"));
                    helper.setText(R.id.tv_issue, item.getIssue());

                    FlowLayout flowLayout = (FlowLayout) helper.getView(R.id.llBallsBlue);
                    flowLayout.removeAllViews();

                    if (Strs.isEmpty(item.getCode())) {
                        helper.setText(R.id.tv_time, "\t\t\t\t\t\t\t等待开奖...⏳ ");
                        flowLayout.setVisibility(View.GONE);
                        helper.getView(R.id.ll).setVisibility(View.GONE);
                        return;
                    }
                    helper.getView(R.id.ll).setVisibility(View.VISIBLE);
                    flowLayout.setVisibility(View.VISIBLE);

                    //开奖号球
                    String[] arrCode = item.getCode().split(",");

                    String lastcode = arrCode[arrCode.length - 1];

                    helper.setText(R.id.tvball, lastcode);
                    helper.setText(R.id.tvname, UserManager.getIns().getMapZodiacName().get(Strs.parse(lastcode, 0)));
                    GradientDrawable bgball = (GradientDrawable) helper.getView(R.id.tvball).getBackground();
                    if (blueboList.contains(Integer.parseInt(lastcode))) {
                        bgball.setColor(context.getResources().getColor(R.color.blue_2396f7));
                    } else if (redboList.contains(Integer.parseInt(lastcode))) {
                        bgball.setColor(context.getResources().getColor(R.color.red_ff2c66));
                    } else if (greenboList.contains(Integer.parseInt(lastcode))) {
                        bgball.setColor(context.getResources().getColor(R.color.green_09af1c));
                    }

                    //单双
                    TextView tvDSNum = (TextView) LayoutInflater.from(context).inflate(R.layout.view_ball_blue_lhc_zonghe, flowLayout, false);
                    tvDSNum.setText(Utils.getLotteryCodeTypeDS(lastcode));
                    GradientDrawable tvDSNumbg = (GradientDrawable) tvDSNum.getBackground();
                    if (Strs.isEqual("单", Utils.getLotteryCodeTypeDS(lastcode))) {
                        tvDSNumbg.setColor(context.getResources().getColor(R.color.blue_2396f7));
                    } else {
                        tvDSNumbg.setColor(context.getResources().getColor(R.color.red_ff2c66));
                    }
                    flowLayout.addView(tvDSNum);

                    //大小
                    TextView tvDXNum = (TextView) LayoutInflater.from(context).inflate(R.layout.view_ball_blue_lhc_zonghe, flowLayout, false);
                    tvDXNum.setText(Utils.getLotteryCodeTypeLhcDX(lastcode));
                    GradientDrawable tvDXNumbg = (GradientDrawable) tvDXNum.getBackground();
                    if (Strs.isEqual("大", Utils.getLotteryCodeTypeLhcDX(lastcode))) {
                        tvDXNumbg.setColor(context.getResources().getColor(R.color.blue_2396f7));
                    } else {
                        tvDXNumbg.setColor(context.getResources().getColor(R.color.red_ff2c66));
                    }
                    flowLayout.addView(tvDXNum);

                    //合单双
                    TextView tvHDSNum = (TextView) LayoutInflater.from(context).inflate(R.layout.view_ball_blue_lhc_zonghe, flowLayout, false);
                    tvHDSNum.setText("合" + Utils.getLotteryCodeTypeLhcHDS(lastcode));
                    GradientDrawable tvHDSNumbg = (GradientDrawable) tvHDSNum.getBackground();
                    if (Strs.isEqual("单", Utils.getLotteryCodeTypeLhcHDS(lastcode))) {
                        tvHDSNumbg.setColor(context.getResources().getColor(R.color.blue_2396f7));
                    } else {
                        tvHDSNumbg.setColor(context.getResources().getColor(R.color.red_ff2c66));
                    }
                    flowLayout.addView(tvHDSNum);

                    //合大小
                    //TextView tvHDXNum = (TextView) LayoutInflater.from(context).inflate(R.layout.view_ball_blue_lhc_zonghe, flowLayout, false);
                    //tvHDXNum.setText("合" + Utils.getLotteryCodeTypeLhcHDX(lastcode));
                    //GradientDrawable tvHDXNumbg = (GradientDrawable) tvHDXNum.getBackground();
                    //tvHDXNumbg.setColor(context.getResources().getColor(R.color.red_ff2c66));
                    //flowLayout.addView(tvHDXNum);
                    //尾大小
                    TextView tvWDXNum = (TextView) LayoutInflater.from(context).inflate(R.layout.view_ball_blue_lhc_zonghe, flowLayout, false);
                    tvWDXNum.setText("尾" + Utils.getLotteryCodeTypeLhcWDX(lastcode));
                    GradientDrawable tvWDXNumbg = (GradientDrawable) tvWDXNum.getBackground();
                    if (Strs.isEqual("大", Utils.getLotteryCodeTypeLhcWDX(lastcode))) {
                        tvWDXNumbg.setColor(context.getResources().getColor(R.color.blue_2396f7));
                    } else {
                        tvWDXNumbg.setColor(context.getResources().getColor(R.color.red_ff2c66));
                    }
                    flowLayout.addView(tvWDXNum);

                    flowLayout.requestLayout();
                }
            };

        } else {
            mCodeAdapter = new BaseQuickAdapter<LotteryOpenHistory, BaseViewHolder>(R.layout.item_lottery_play_code) {
                @Override
                protected void convert(BaseViewHolder helper, LotteryOpenHistory item) {
                    if (item == null) {
                        return;
                    }
                    helper.setText(R.id.tv_time, Utils.getDate(item.getTime() * 1000, "HH:mm"));
                    helper.setText(R.id.tv_issue, item.getIssue());

                    FlowLayout flowLayout = (FlowLayout) helper.getView(R.id.llBallsBlue);
                    flowLayout.removeAllViews();

                    if (Strs.isEmpty(item.getCode())) {
                        helper.setText(R.id.tv_time, "\t\t\t\t\t\t\t等待开奖...⏳ ");
                        flowLayout.setVisibility(View.GONE);
                        return;
                    }

                    flowLayout.setVisibility(View.VISIBLE);
                    //开奖号球
                    String[] arrCode = item.getCode().split(",");

                    TextView tvNum = null;

                    for (int i = 0; i < arrCode.length; i++) {

                        if (Strs.isEqual("PK10", lotteryKind.getPlayCategory())) {
                            tvNum = (TextView) LayoutInflater.from(context).inflate(R.layout.view_ball_blue_small_2, flowLayout, false);

                            GradientDrawable bg = (GradientDrawable) tvNum.getBackground();
                            if (i == 0) {
                                bg.setColor(context.getResources().getColor(R.color.orange_f98f36));
                            } else if (i == 1) {
                                bg.setColor(context.getResources().getColor(R.color.blue_2396f7));
                            } else if (i == 2) {
                                bg.setColor(context.getResources().getColor(R.color.orange_ED5735));
                            } else {
                                bg.setColor(context.getResources().getColor(R.color.red_ff2c66));
                            }
                        } else {
                            tvNum = (TextView) LayoutInflater.from(context).inflate(R.layout.view_ball_blue_small_1, flowLayout, false);

                            GradientDrawable bg = (GradientDrawable) tvNum.getBackground();
                            bg.setColor(context.getResources().getColor(R.color.red_ff2c66));
                        }

                        tvNum.setText(arrCode[i]);
                        flowLayout.addView(tvNum);
                    }
                    flowLayout.requestLayout();
                }
            };

            //单双
            mDanShuangAdapter = new BaseQuickAdapter<LotteryOpenHistory, BaseViewHolder>(R.layout.item_lottery_play_code) {
                @Override
                protected void convert(BaseViewHolder helper, LotteryOpenHistory item) {
                    if (item == null) {
                        return;
                    }
                    helper.setText(R.id.tv_time, Utils.getDate(item.getTime() * 1000, "HH:mm"));
                    helper.setText(R.id.tv_issue, item.getIssue());

                    FlowLayout flowLayout = (FlowLayout) helper.getView(R.id.llBallsBlue);
                    flowLayout.removeAllViews();
                    if (Strs.isEmpty(item.getCode())) {
                        helper.setText(R.id.tv_time, "\t\t\t\t\t\t\t等待开奖...⏳ ");
                        flowLayout.setVisibility(View.GONE);
                        return;
                    }

                    flowLayout.setVisibility(View.VISIBLE);

                    //开奖号球
                    String[] arrCode = item.getCode().split(",");

                    TextView tvNum = null;

                    for (int i = 0; i < arrCode.length; i++) {
                        if (Strs.isEqual("PK10", lotteryKind.getPlayCategory())) {
                            tvNum = (TextView) LayoutInflater.from(context).inflate(R.layout.view_ball_blue_small_2, flowLayout, false);

                            if (Strs.isEqual("单", Utils.getLotteryCodeTypeDS(arrCode[i]))) {
                                GradientDrawable bg = (GradientDrawable) tvNum.getBackground();
                                bg.setColor(context.getResources().getColor(R.color.blue_2396f7));
                            } else {
                                GradientDrawable bg = (GradientDrawable) tvNum.getBackground();
                                bg.setColor(context.getResources().getColor(R.color.red_ff2c66));
                            }
                        } else {
                            tvNum = (TextView) LayoutInflater.from(context).inflate(R.layout.view_ball_blue_small_1, flowLayout, false);

                            if (Strs.isEqual("单", Utils.getLotteryCodeTypeDS(arrCode[i]))) {
                                tvNum.setBackgroundDrawable(Views.fromDrawables(R.drawable.sh_bg_ball_blue2_solid));
                                GradientDrawable bg = (GradientDrawable) tvNum.getBackground();
                                bg.setColor(context.getResources().getColor(R.color.blue_2396f7));
                            } else {
                                tvNum.setBackgroundDrawable(Views.fromDrawables(R.drawable.sh_bg_ball_blue_solid));
                                GradientDrawable bg = (GradientDrawable) tvNum.getBackground();
                                bg.setColor(context.getResources().getColor(R.color.red_ff2c66));
                            }
                        }

                        tvNum.setText(Utils.getLotteryCodeTypeDS(arrCode[i]));
                        flowLayout.addView(tvNum);
                    }
                    flowLayout.requestLayout();
                }
            };

            //大小
            mDaxiaoAdapter = new BaseQuickAdapter<LotteryOpenHistory, BaseViewHolder>(R.layout.item_lottery_play_code) {
                @Override
                protected void convert(BaseViewHolder helper, LotteryOpenHistory item) {
                    if (item == null) {
                        return;
                    }
                    helper.setText(R.id.tv_time, Utils.getDate(item.getTime() * 1000, "HH:mm"));
                    helper.setText(R.id.tv_issue, item.getIssue());

                    FlowLayout flowLayout = (FlowLayout) helper.getView(R.id.llBallsBlue);
                    flowLayout.removeAllViews();

                    if (Strs.isEmpty(item.getCode())) {
                        helper.setText(R.id.tv_time, "\t\t\t\t\t\t\t等待开奖...⏳ ");
                        flowLayout.setVisibility(View.GONE);
                        return;
                    }

                    flowLayout.setVisibility(View.VISIBLE);

                    //开奖号球
                    String[] arrCode = item.getCode().split(",");

                    TextView tvNum = null;
                    for (int i = 0; i < arrCode.length; i++) {
                        if (Strs.isEqual("PK10", lotteryKind.getPlayCategory())) {
                            tvNum = (TextView) LayoutInflater.from(context).inflate(R.layout.view_ball_blue_small_2, flowLayout, false);

                            if (Strs.isEqual("大", Utils.getLotteryCodeTypeDX(arrCode[i], lotteryKind.getPlayCategory()))) {
                                GradientDrawable bg = (GradientDrawable) tvNum.getBackground();
                                bg.setColor(context.getResources().getColor(R.color.blue_2396f7));
                            } else {
                                GradientDrawable bg = (GradientDrawable) tvNum.getBackground();
                                bg.setColor(context.getResources().getColor(R.color.red_ff2c66));
                            }

                        } else {
                            tvNum = (TextView) LayoutInflater.from(context).inflate(R.layout.view_ball_blue_small_1, flowLayout, false);

                            if (Strs.isEqual("大", Utils.getLotteryCodeTypeDX(arrCode[i], lotteryKind.getPlayCategory()))) {
                                tvNum.setBackgroundDrawable(Views.fromDrawables(R.drawable.sh_bg_ball_blue2_solid));
                                GradientDrawable bg = (GradientDrawable) tvNum.getBackground();
                                bg.setColor(context.getResources().getColor(R.color.blue_2396f7));
                            } else {
                                tvNum.setBackgroundDrawable(Views.fromDrawables(R.drawable.sh_bg_ball_blue_solid));
                                GradientDrawable bg = (GradientDrawable) tvNum.getBackground();
                                bg.setColor(context.getResources().getColor(R.color.red_ff2c66));
                            }
                        }

                        tvNum.setText(Utils.getLotteryCodeTypeDX(arrCode[i], lotteryKind.getPlayCategory()));
                        flowLayout.addView(tvNum);
                    }
                    flowLayout.requestLayout();
                }
            };
        }

        mRecycleView.setAdapter(mCodeAdapter);
        trend_RadioGroup.check(R.id.rb_btn1);
        trend_RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_btn1:
                        mRecycleView.setAdapter(mCodeAdapter);
                        break;
                    case R.id.rb_btn2:
                        mRecycleView.setAdapter(mDaxiaoAdapter);
                        break;
                    case R.id.rb_btn3:
                        mRecycleView.setAdapter(mDanShuangAdapter);
                        break;
                }
            }
        });
    }

    private void updateStaticOpen() {
        HttpActionTouCai.getLotteryOpenCodeHistory(this, lotteryKind.getCode(), true, new AbHttpResult() {
            @Override
            public boolean onGetString(String str) {
                try {
                    ArrayList<LotteryOpenHistory> listOpen = new Gson().fromJson(str, new TypeToken<ArrayList<LotteryOpenHistory>>() {
                    }.getType());
                    mCodeAdapter.setNewData(listOpen);
                    mDanShuangAdapter.setNewData(listOpen);
                    mDaxiaoAdapter.setNewData(listOpen);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        updateStaticOpen();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLotteryCodeUpdate(LotteryCodeUpdateBean event) {
        if (event != null && ((ActLotteryMain) context).tabLayout.getSelectedTabPosition() == 0) {
            onRefresh();
        }
    }

}
