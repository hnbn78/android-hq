package com.desheng.app.toucai.panel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.thread.ThreadCollector;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.model.LotteryCodeUpdateBean;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.util.ChangdiptopxUtil;
import com.desheng.base.util.LogUtil;
import com.desheng.base.util.TrendTypeIdUtil;
import com.desheng.base.view.trendchart.CartWqBean;
import com.desheng.base.view.trendchart.DialogTrendSettingNew;
import com.desheng.base.view.trendchart.TrendDataChangeBean;
import com.desheng.base.view.trendchart.TrendHeadChangeBean;
import com.desheng.base.view.trendchart.TrendTypeBean;
import com.desheng.base.view.trendchart.TrendTypeChooseBeanNew;
import com.desheng.base.view.trendchart.TrendTypeIndexBean;
import com.desheng.base.view.trendchart.TrendView;
import com.desheng.base.view.trendchart.adapter.MyTrendTypeAdapter;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.trendchart.HeadCustomGridView;
import com.pearl.view.trendchart.HeaderHorizontalScrollView;
import com.pearl.view.trendchart.ScrollChangeCallback;
import com.pearl.view.trendchart.TrendScrollViewWidget;
import com.shark.tc.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class FragTrend extends AbBaseFragment implements View.OnClickListener, ScrollChangeCallback, MyTrendTypeAdapter.OnTypeItemClickListener {


    private List<CartWqBean> cartWqBeanList = new ArrayList();

    private ChangdiptopxUtil changdiptopxUtil;
    private int chooseNum = 30;
    private TextView gameTrendIssue100Text;
    private TextView gameTrendIssue30Text;
    private TextView gameTrendIssue50Text;
    private TextView gameTrendTitleText;
    private TextView gameTrendSettingButton;
    private TrendView gameTrendTrendText;
    private LinearLayout game_trend_linear_header;
    private TextView headTvIssue;
    private LinearLayout game_trend_linear_header_other;
    private TrendScrollViewWidget mContentScroll;
    private HeadCustomGridView game_trend_grid_header;
    private HeaderHorizontalScrollView mHeadScroll;

    //    表格颜色设置
    private TrendDataChangeBean trendDataChangeBean = new TrendDataChangeBean();
    //    顶部设置
    private TrendHeadChangeBean trendHeadChangeBean = new TrendHeadChangeBean();
    //    表格数字设置
    private TrendTypeChooseBeanNew trendTypeChooseBean = new TrendTypeChooseBeanNew();

    private String lotteryId;
    private ILotteryKind lottery;
    private String lotteryType;
    private DialogTrendSettingNew dialogTrendSettingNew;
    private TextView game_trend_shunxu;
    private boolean isZhengxu = true;

    public static Fragment newInstance(String lotteryId) {
        Fragment fragment = new FragTrend();
        Bundle arguments = new Bundle();
        arguments.putString("lotteryId", lotteryId);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_game_trend;
    }

    @Override
    public void init(View root) {
        EventBus.getDefault().register(this);
        initView(root);
        lotteryId = getArguments().getString("lotteryId");
        lottery = CtxLottery.getIns().findLotteryKind(Integer.parseInt(lotteryId));
        lotteryType = TrendTypeIdUtil.getTrendType(Integer.parseInt(lotteryId));
        boolean trendShunxu = UserManager.getIns().getTrendShunxu();
        setTitleName();
        getTrendData(trendShunxu);
    }

    public void refreshTrendview() {
        setTitleName();
        boolean trendShunxu = UserManager.getIns().getTrendShunxu();
        getTrendData(trendShunxu);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLotteryCodeUpdate(LotteryCodeUpdateBean event) {
        if (event != null && ((ActLotteryMain) context).tabLayout.getSelectedTabPosition() == 3 && isShowing) {
            refreshTrendview();
        }
    }

    private void initView(View root) {
        gameTrendIssue100Text = root.findViewById(R.id.game_trend_issue100_text);
        gameTrendIssue30Text = root.findViewById(R.id.game_trend_issue30_text);
        gameTrendIssue50Text = root.findViewById(R.id.game_trend_issue50_text);
        gameTrendTitleText = root.findViewById(R.id.game_trend_title_text);
        gameTrendSettingButton = root.findViewById(R.id.game_trend_setting_button);
        gameTrendTrendText = root.findViewById(R.id.game_trend_trend_text);
        game_trend_shunxu = root.findViewById(R.id.game_trend_shunxu);

        gameTrendSettingButton.setOnClickListener(this);
        gameTrendIssue30Text.setOnClickListener(this);
        gameTrendIssue50Text.setOnClickListener(this);
        gameTrendIssue100Text.setOnClickListener(this);
        game_trend_shunxu.setOnClickListener(this);

        headTvIssue = root.findViewById(R.id.tv_issue_name);

        mHeadScroll = root.findViewById(R.id.game_trend_header_scroll);
        game_trend_linear_header = root.findViewById(R.id.game_trend_header_linear);
        game_trend_grid_header = root.findViewById(R.id.game_trend_grid_header);
        game_trend_linear_header_other = root.findViewById(R.id.game_trend_header_other_linear);

        mContentScroll = root.findViewById(R.id.game_trend_scroll_content);

        mContentScroll.setScrollViewListener(this);
        mHeadScroll.setScrollViewListener(this);

        changdiptopxUtil = new ChangdiptopxUtil();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.width = changdiptopxUtil.dip2pxInt(TrendView.LEFT_WIDTH);
        headTvIssue.setLayoutParams(lp);
    }

    private void setTitleName() {
        String lotteryName = lottery.getShowName();
        if (Strs.isNotEmpty(lotteryName)) {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append(lotteryName);
            localStringBuilder.append("走势");
            lotteryName = localStringBuilder.toString();
            //setToolbarTitleCenter(lotteryName);
        }
    }

    private int pPosition = 0;
    private int itemPosition = 0;

    @Override
    public void onItemClick(TrendTypeIndexBean indexBean, int pPosition, int itemPosition) {
        UserManager.getIns().setPposition(lotteryType, pPosition);
        UserManager.getIns().setItemPosition(lotteryType, itemPosition);

        this.pPosition = pPosition;
        this.itemPosition = itemPosition;

        if (dialogTrendSettingNew != null && dialogTrendSettingNew.isShowing()) {
            dialogTrendSettingNew.dismiss();
        }

        resetDataOnView(pPosition, itemPosition, chooseNum);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.game_trend_issue100_text:
                setIssueUI(gameTrendIssue100Text);
                chooseNum = 100;
                //resetDataOnView(pPosition, itemPosition, chooseNum);
                refreshTrendview();
                break;
            case R.id.game_trend_issue30_text:
                setIssueUI(gameTrendIssue30Text);
                chooseNum = 30;
                //resetDataOnView(pPosition, itemPosition, chooseNum);
                refreshTrendview();
                break;
            case R.id.game_trend_issue50_text:
                setIssueUI(gameTrendIssue50Text);
                chooseNum = 50;
                //resetDataOnView(pPosition, itemPosition, chooseNum);
                refreshTrendview();
                break;
            case R.id.game_trend_setting_button:
                dialogTrendSettingNew = new DialogTrendSettingNew(context, lotteryType, this::onItemClick);
                dialogTrendSettingNew.setTrendSettingData(lotteryId);
                dialogTrendSettingNew.show();
                break;
            case R.id.game_trend_shunxu:
                boolean trendShunxu = UserManager.getIns().getTrendShunxu();
                if (trendShunxu) {
                    game_trend_shunxu.setText("倒序");
                    UserManager.getIns().setTrendShunxu(false);
                } else {
                    game_trend_shunxu.setText("正序");
                    UserManager.getIns().setTrendShunxu(true);
                }
                refreshTrendview();
                break;
        }
    }

    private void resetDataOnView(int pPosition, int itemPosition, int chooseNum) {
//        获取走势图配置
        List<TrendTypeBean> trendTypeBeans = trendTypeChooseBean.chooseTrendType(Integer.parseInt(lotteryId));
        LogUtil.logE("trendTypeBeans", trendTypeBeans.toString());
        if (null != trendTypeBeans && trendTypeBeans.size() > 0) {

            setTitleViewText(pPosition, itemPosition, trendTypeBeans);
            //获取配置的位置
            List<int[]> posList = trendTypeChooseBean.posList((trendTypeBeans.get(pPosition).getIndex_list().get(itemPosition)).getPos());

            if (posList == null) {
                return;
            }

            if (posList.size() <= 0) {
                return;
            }

            cartWqBeanList = trendDataChangeBean.changeData(cartWqBeanList, posList, trendTypeBeans.get(pPosition));

            trendHeadChangeBean.changeHeadView(game_trend_linear_header, game_trend_linear_header_other,
                    game_trend_grid_header, context, changdiptopxUtil, posList, trendTypeBeans.get(pPosition));

            trendDataChangeBean.initTrendData(chooseNum, cartWqBeanList, gameTrendTrendText, posList, context, trendTypeBeans.get(pPosition));
        }

    }

    private void setTitleViewText(int pPosition, int itemPosition, List<TrendTypeBean> trendTypeBeans) {
        StringBuilder sb = new StringBuilder();
        sb.append(trendTypeBeans.get(pPosition).getName());
        sb.append("_");
        sb.append((trendTypeBeans.get(pPosition).getIndex_list().get(itemPosition)).getName());
        String title = sb.toString();
        if (title.contains("\n")) {
            title = title.replaceAll("\n", "| ");
        }

        gameTrendTitleText.setText(title);
    }

    private void setIssueUI(TextView paramTextView) {
        gameTrendIssue30Text.setBackgroundResource(R.drawable.bg_rb_lottery_button_normal);
        gameTrendIssue50Text.setBackgroundResource(R.drawable.bg_rb_lottery_button_normal);
        gameTrendIssue100Text.setBackgroundResource(R.drawable.bg_rb_lottery_button_normal);
        gameTrendIssue30Text.setTextColor(ContextCompat.getColor(context, R.color.login_bottom_text_color));
        gameTrendIssue50Text.setTextColor(ContextCompat.getColor(context, R.color.login_bottom_text_color));
        gameTrendIssue100Text.setTextColor(ContextCompat.getColor(context, R.color.login_bottom_text_color));
        paramTextView.setBackgroundResource(R.drawable.login_button_bg);
        paramTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
    }

    private void getTrendData(boolean trendShunxu) {
        int id = 0;
        //经典菲律宾5分彩兼容
        if (lottery.getId() == CtxLottery.getIns().lotteryKind("FeiLvBin_5Fen_JD").getId()) {
            id = CtxLottery.getIns().lotteryKind("FeiLvBin_5FEN").getId();
        } else if (lottery.getId() == CtxLottery.getIns().lotteryKind("BeiJing_PK10_JD").getId()) {
            id = CtxLottery.getIns().lotteryKind("BeiJing_PK10").getId();
        } else {
            id = lottery.getId();
        }
        HttpAction.getTrend(this, id, chooseNum, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", String.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                ThreadCollector.getIns().postDelayOnUIThread(100, new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(context, "");
                    }
                });
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    String codeStr = getField(extra, "data", "");
                    LogUtil.logE("1.元数据", codeStr);
                    if (Strs.isNotEmpty(codeStr)) {
                        String[] arrayInfos = codeStr.split(",");

                        switch (lotteryType) {
                            case TrendTypeIdUtil.TYPE_SSC_STR:
                                cartWqBeanList = trendDataChangeBean.reverseDataList(getCartWqBeans(arrayInfos, trendShunxu));
                                break;
                            case TrendTypeIdUtil.TYPE_3D_STR:
                            case TrendTypeIdUtil.TYPE_KUAI_SAN_STR:
                            case TrendTypeIdUtil.TYPE_11XUAN_5_STR:
                            case TrendTypeIdUtil.TYPE_PK10_STR:
                                cartWqBeanList = getCartWqBeans(arrayInfos, trendShunxu);
                                break;
                        }

                        pPosition = UserManager.getIns().getPposition(lotteryType);
                        itemPosition = UserManager.getIns().getItemPosition(lotteryType);

                        resetDataOnView(pPosition, itemPosition, chooseNum);

                    }
                } else {
                    Toasts.show(msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                ThreadCollector.getIns().postDelayOnUIThread(500, new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.hideProgressDialog(context);
                    }
                });
            }
        });
    }

    /**
     * 第一层封装数据
     *
     * @param codeArray
     * @return
     */
    @NonNull
    private List<CartWqBean> getCartWqBeans(String[] codeArray, boolean trendShunxu) {
        List<CartWqBean> cartWqBeans = new ArrayList<>();

        int count = ((Integer.parseInt(lotteryId) != 2)) ? 1 : 2;

        for (int i = 0; i < codeArray.length; i++) {
            String code = codeArray[i];
            CartWqBean cartWqBean = new CartWqBean();
            String[] arrayStrs = code.split("\\|");
//            期号
            cartWqBean.setIssue(arrayStrs[0]);
            StringBuilder sbCode = new StringBuilder();
            int k;
            for (int j = 0; j < arrayStrs[1].length(); j = k) {
                StringBuilder sb = new StringBuilder();
                String str = arrayStrs[1];
                k = j + count;
                sb.append(str, j, k);
                sb.append(",");
                sbCode.append(sb.toString());
            }
            if (sbCode.length() > 0) {
                sbCode.deleteCharAt(sbCode.length() - 1);
            }
//            号码
            cartWqBean.setCode(sbCode.toString());
            cartWqBeans.add(cartWqBean);
        }
        //LogUtil.logE("2.封装数据", "封装code和期号");

        //部分台子 需要将最新一期放在最下方.
        if (!trendShunxu) {
            Collections.reverse(cartWqBeans);
        }
        return cartWqBeans;
    }

    public void changeXScroll(int paramInt) {
        mContentScroll.scrollTo(paramInt, mContentScroll.getScrollY());
        mHeadScroll.scrollTo(paramInt, 0);
    }

    @Override
    public void changeYScoll(int top) {

    }

    public void changeYScroll(int paramInt) {
        mContentScroll.scrollTo(mContentScroll.getScrollX(), paramInt);
        mHeadScroll.scrollTo(mContentScroll.getScrollX(), 0);
    }


    public void destroy() {
        EventBus.getDefault().unregister(this);
        if (gameTrendTrendText != null) {
            gameTrendTrendText.destroyDrawingCache();
            gameTrendTrendText = null;
        }
    }
}
