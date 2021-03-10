package com.desheng.app.toucai.panel;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.ab.global.AbDevice;
import com.ab.global.ENV;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.app.hubert.guide.core.Controller;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.consitance.CommonConsts;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.global.AppUmengPushHandler;
import com.desheng.app.toucai.global.ConfigTouCai;
import com.desheng.app.toucai.model.BankCardRealInfo;
import com.desheng.app.toucai.model.DajiangPushEventModel;
import com.desheng.app.toucai.model.UpdateContentList;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.DepositCategory;
import com.desheng.base.model.LastOrderInfo;
import com.desheng.base.model.PersonInfo;
import com.desheng.base.model.RechargeInfo;
import com.desheng.base.panel.ActWebX5;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.act.util.GuidePageHelper;
import com.shark.tc.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * Created by lee on 2018/4/11.
 */
public class FragTabDepositIndex extends AbBaseFragment implements SwipeRefreshLayout.OnRefreshListener, IRefreshableFragment {
    public static final String[] arrRefresh = {UserManager.EVENT_USER_LOGINED,
            UserManager.EVENT_USER_LOGOUTED};

    public static final List<DepositCategory> listCate = new ArrayList<>();

    private FrameLayout vg_deposit;
    private GridLayout glCategory;
    private NestedScrollView nsvCategory;
    private SwipeRefreshLayout srlRefresh;
    private View vgEmpty;
    private ViewGroup[] arrVgCate;
    private ImageView[] arrIvCate;

    private RechargeInfo rechargeInfo;

    private LayoutInflater inflater;
    private ImageView vipIcon;
    private ViewFlipper mAnnouncementFilpper;
    private AppUmengPushHandler mAppUmengPushHandler;
    private ImageView mIvBG;

    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_deposit_index;
    }

    @Override
    public void init(View root) {
        EventBus.getDefault().register(this);
        srlRefresh = (SwipeRefreshLayout) root.findViewById(R.id.srlDepositRefresh);
        srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);

        nsvCategory = root.findViewById(R.id.nsvCategory);
        vgEmpty = root.findViewById(R.id.vgEmpty);
        mAnnouncementFilpper = root.findViewById(R.id.filpper);

        glCategory = (GridLayout) root.findViewById(R.id.glCategory);
        vg_deposit = root.findViewById(R.id.vg_deposit);
        vipIcon = root.findViewById(R.id.iv_vip_channel);
        mIvBG = root.findViewById(R.id.ivBG);

        int skinSetting = UserManager.getIns().getSkinSetting();
        //3.2.2新年版本暂时不修改充值页皮肤
        skinSetting = 0;//0即为默认皮肤
        glCategory.setBackgroundResource(CommonConsts.setChargeIconBg(skinSetting));
        setBgPic(skinSetting);
    }

    //定义处理接收的方法,处理大奖推送的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(DajiangPushEventModel pushEventModel) {
        if (TextUtils.isEmpty(pushEventModel.getContent())) {
            return;
        }

        mAnnouncementFilpper.setVisibility(View.VISIBLE);

        View childview1 = LayoutInflater.from(getContext()).inflate(R.layout.layout_announcement_itemview, null);
        ((TextView) childview1.findViewById(R.id.tvcontent)).setText(pushEventModel.getContent());
        mAnnouncementFilpper.addView(childview1);

        if (mAnnouncementFilpper.getChildCount() == 1) {
            View childview = LayoutInflater.from(getContext()).inflate(R.layout.layout_announcement_itemview, null);
            ((TextView) childview.findViewById(R.id.tvcontent)).setText(pushEventModel.getContent());
            mAnnouncementFilpper.addView(childview);
        }

        mAnnouncementFilpper.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mAnnouncementFilpper.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAnnouncementFilpper.setVisibility(View.GONE);
                    }
                }, 5000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!mAnnouncementFilpper.isFlipping()) {
            mAnnouncementFilpper.startFlipping();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAnnouncementFilpper.isFlipping()) {
            mAnnouncementFilpper.stopFlipping();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAnnouncementFilpper != null) {
            mAnnouncementFilpper.stopFlipping();
            mAnnouncementFilpper.removeAllViews();
        }
        //注销注册
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onShow() {
        super.onShow();
        onRefresh();
    }

    @Override
    public void onHide() {
        super.onHide();
    }

    @Override
    public void refresh(String eventName, Bundle newBundle) {
        onRefresh();
    }

    @Override
    public void onRefresh() {
        vipIcon.setVisibility(View.GONE);

        if (UserManager.getIns().isLogined()) {
            getPayCategory();
            listCard();

            UserManager.getIns().getPersonSettingInfo(new UserManager.IUserInfoGetCallBack() {
                @Override
                public void onCallBack(PersonInfo personInfo) {
                    if (personInfo.isVipFlag()) {
                        vipIcon.setVisibility(View.VISIBLE);
                        AnimationDrawable animationDrawable = (AnimationDrawable) vipIcon.getDrawable();
                        if (animationDrawable.isRunning()) {
                            animationDrawable.stop();
                            animationDrawable.selectDrawable(0);
                        }
                        animationDrawable.start();
                        animationDrawable.selectDrawable(0);
                        vipIcon.setOnClickListener(v -> ActWebX5.launch(getContext(), personInfo.getVipChannelUrl(), true));
                    } else {
                        vipIcon.setVisibility(View.GONE);
                    }
                }
            });

        }
    }

    private void getPayCategory() {
        HttpActionTouCai.requestAllCategory(context, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<DepositCategory>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    listCate.clear();
                    listCate.addAll(getField(extra, "data", null));
                    getPayMethod();
                }

                return true;
            }
        });
    }

    private void getPayMethod() {
        HttpAction.getAllThirdPayment(context, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                srlRefresh.post(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(true);
                    }
                });
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", RechargeInfo.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    rechargeInfo = getFieldObject(extra, "data", null);
                    processAllData();
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(false);
                    }
                }, 400);
                return true;
            }

            @Override
            public void onAfter(int id) {
                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(false);
                    }
                }, 400);
            }
        });
    }


    public void listCard() {
        HttpActionTouCai.listCard(context, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<BankCardRealInfo>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    ArrayList<BankCardRealInfo> list = getField(extra, "data", null);
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            if (Strs.isNotEmpty(list.get(i).bankCardName)) {
                                UserManager.getIns().setBankCardRealName(list.get(i).bankCardName);
                                break;
                            }
                        }
                    }
                }
                return true;
            }
        });
    }


    public void processAllData() {
        int count = (rechargeInfo.thridList == null ? 0 : rechargeInfo.thridList.size());
        count += (rechargeInfo.transferList == null ? 0 : rechargeInfo.transferList.size());
        count += (rechargeInfo.qrCodeList == null ? 0 : rechargeInfo.qrCodeList.size());
        if (!rechargeInfo.rechargeConfig.isOpen ||
                count == 0) {
            vgEmpty.setVisibility(View.VISIBLE);
            nsvCategory.setVisibility(View.GONE);
            return;
        } else {
            vgEmpty.setVisibility(View.GONE);
            nsvCategory.setVisibility(View.VISIBLE);
        }

        List<RechargeInfo.CommonChargeListBean> sortedCommonList = new ArrayList<>();
        for (int j = 0; j < listCate.size(); j++) {
            if (rechargeInfo.thridList != null) {
                for (int i = 0; i < rechargeInfo.thridList.size(); i++) {
                    if (listCate.get(j).isCategory(rechargeInfo.thridList.get(i).payChannelCategoryId)) {
                        sortedCommonList.add(rechargeInfo.thridList.get(i).getCommon());
                    }
                }
            }

            if (rechargeInfo.transferList != null) {
                for (int i = 0; i < rechargeInfo.transferList.size(); i++) {
                    if (listCate.get(j).isCategory(rechargeInfo.transferList.get(i).payChannelCategoryId)) {
                        RechargeInfo.CommonChargeListBean common = rechargeInfo.transferList.get(i).getCommon();
                        sortedCommonList.add(common);
                    }
                }
            }

            if (rechargeInfo.qrCodeList != null) {
                for (int i = 0; i < rechargeInfo.qrCodeList.size(); i++) {
                    if (listCate.get(j).isCategory(rechargeInfo.qrCodeList.get(i).payChannelCategoryId)) {
                        if ("奉南琴".equals(rechargeInfo.qrCodeList.get(i).nickName) ||
                                "这个 可以有".equals(rechargeInfo.qrCodeList.get(i).nickName)) {
                            //nothing
                            continue;
                        } else {
                            RechargeInfo.CommonChargeListBean common = rechargeInfo.qrCodeList.get(i).getCommon();
                            common.name = common.nickName;
                            sortedCommonList.add(common);
                        }
                    }
                }
            }
        }

        Collections.sort(sortedCommonList, new Comparator<RechargeInfo.CommonChargeListBean>() {

            public int compare(RechargeInfo.CommonChargeListBean o1, RechargeInfo.CommonChargeListBean o2) {
                // 按照sort进行降序排列
                if (o1.sort > o2.sort) {
                    return -1;
                }
                if (o1.sort == o2.sort) {
                    return 0;
                }
                return 1;
            }
        });

        List<RechargeInfo.CommonChargeListBean> commonList = null;
        for (int j = 0; j < listCate.size(); j++) {
            commonList = new ArrayList<>();

            for (int i = 0; i < sortedCommonList.size(); i++) {
                if (listCate.get(j).isCategory(sortedCommonList.get(i).payChannelCategoryId)) {
                    commonList.add(sortedCommonList.get(i));
                }
            }

            listCate.get(j).listCommon = commonList;
        }

        arrVgCate = new ViewGroup[listCate.size()];
        arrIvCate = new ImageView[listCate.size()];
        inflater = LayoutInflater.from(getActivity());
        for (int i = 0; i < listCate.size(); i++) {
            arrVgCate[i] = (ViewGroup) inflater.inflate(R.layout.item_recharge_category, null);
            arrVgCate[i].setLayoutParams(new GridLayout.LayoutParams());
            Views.loadBackgroudAny(context, arrVgCate[i], listCate.get(i).mobileLogoPicture);
            int widthInPx = (AbDevice.SCREEN_WIDTH_PX - Views.dp2px(32)) / 2;
            Views.setWidthAndHeight(arrVgCate[i], widthInPx, (int) (widthInPx * 0.53));
            Views.setMargin(arrVgCate[i], Views.dp2px(3), Views.dp2px(3), Views.dp2px(3), Views.dp2px(3));
            arrIvCate[i] = arrVgCate[i].findViewById(R.id.ivCate);
            arrVgCate[i].setTag(listCate.get(i));
            arrVgCate[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DepositCategory cate = (DepositCategory) v.getTag();

                    if (cate.listCommon == null || cate.listCommon.size() == 0) {
                        Toasts.show(context, cate.categoryName + "未配置充值渠道, 请联系客服!", false);
                        return;
                    }

                    getLastOrder(cate);

                }
            });
            if (Strs.isNotEmpty(listCate.get(i).mobileActivityPicture)) {
                arrIvCate[i].setVisibility(View.VISIBLE);
                Views.loadImageAnyWithWidth(context, arrIvCate[i], listCate.get(i).mobileActivityPicture, Views.dp2px(30));
            } else {
                arrIvCate[i].setVisibility(View.INVISIBLE);
            }
        }

        glCategory.removeAllViews();
        for (int i = 0; i < listCate.size(); i++) {
            //if (listCate.get(i).listCommon != null && listCate.get(i).listCommon.size() > 0) {
            glCategory.addView(arrVgCate[i]);
            //}
        }
    }

    private void getLastOrder(DepositCategory cate) {
        HttpActionTouCai.getRecordByCategoryId(context, cate.id, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                DialogsTouCai.showProgressDialog(context, "");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", LastOrderInfo.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    if (getFieldObject(extra, "data", null) != null) {
                        LastOrderInfo lastOrderInfo = getFieldObject(extra, "data", LastOrderInfo.class);
                        if (lastOrderInfo.rechargeOrder != null) {
                            if (lastOrderInfo.qrCodePayInfo != null) {
                                ActDepositNextQR.launchForStage2(context, cate, lastOrderInfo);
                            } else if (lastOrderInfo.remitBankInfo != null) {
                                ActDepositNextTrans.launchForStage2(context, cate, lastOrderInfo, cate.categoryName + "转账");
                            } else {
                                ActDepositDetail.launch(context, cate);
                            }
                        } else {
                            ActDepositDetail.launch(context, cate);
                        }
                    } else {
                        ActDepositDetail.launch(context, cate);
                    }
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                ActDepositDetail.launch(context, cate);
                return true;
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                DialogsTouCai.hideProgressDialog(context);
            }
        });
    }

    public void getShowGuideParams() {
        UserManager.getIns().isFirstRecharge(new UserManager.IGuideCallBack() {
            @Override
            public void onCallBack(boolean show) {
                if (show) {
                    glCategory.post(new Runnable() {
                        @Override
                        public void run() {
//                                if (vg_deposit.getVisibility() == View.VISIBLE && UserManager.getIns().getRechargeShowGuideIndex()) {
                            setupGuidePage2();
//                                }
                        }
                    });
                }
            }
        });
    }


    private static final String GUIDE_DEPOSIT_INDEX = "guide_deposit_index";

    private void setupGuidePage2() {
        GuidePageHelper.Builder builder = GuidePageHelper.build(this, UserManager.getIns().getMainUserName(), GUIDE_DEPOSIT_INDEX);
        GuidePageHelper.TouCaiGuidePage guidePagePlus = GuidePageHelper.TouCaiGuidePage.newInstance()
                .addHighLight(new RectF())
                .setLayoutAnchor(R.id.content, Gravity.TOP | Gravity.RIGHT)
                .setLayoutRes(R.layout.view_guide_page_deposit, R.id.iv_guide);

        builder.addGuidePage(GUIDE_DEPOSIT_INDEX, guidePagePlus);
        builder.setLastPageCancelable(true);
        builder.setOnPageChangedListener(new GuidePageHelper.OnPageChangeListener() {
            @Override
            public void onInflate(Controller controller, String tag, int pos, View root) {

            }

            @Override
            public void onPageChanged(String tag, int pos, GuidePageHelper.TouCaiGuidePage guidePage, GuidePageHelper.TouCaiGuideController controller) {
                if ("plus".equals(tag)) {

                }

            }

            @Override
            public void onShow() {

            }

            @Override
            public void onRemove() {

            }
        });
//        UserManager.getIns().setRechargeSHowGuideIndex(false);
        builder.show();
    }

    private void setBgPic(int skinSetting) {

        if (skinSetting == 0) {
            HttpActionTouCai.getUpdateContentList(this, Consitances.contentManager.BG_RECHARGE, new AbHttpResult() {
                @Override
                public void setupEntity(AbHttpRespEntity entity) {
                    entity.putField("data", new TypeToken<UpdateContentList>() {
                    }.getType());
                }

                @Override
                public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                    if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                        UpdateContentList list = getField(extra, "data", null);

                        if (list == null || list.getList() == null || list.getList().size() == 0) {
                            mIvBG.setBackgroundResource(CommonConsts.setChargeBg(skinSetting));
                            return true;
                        }
                        List<UpdateContentList.UpdateContent> contents = list.getList();
                        //加载启动图，同时要处理异常情况 异常或者加载失败都要显示默认图
                        Glide.with(getContext()).load(ENV.curr.host + contents.get(0).getMobilePictureUrl()).asBitmap().into(mIvBG);
                    } else {
                        mIvBG.setBackgroundResource(CommonConsts.setChargeBg(skinSetting));
                    }
                    return super.onSuccessGetObject(code, error, msg, extra);
                }

                @Override
                public boolean onError(int status, String content) {
                    mIvBG.setBackgroundResource(CommonConsts.setChargeBg(skinSetting));
                    return super.onError(status, content);
                }
            });
        } else {
            mIvBG.setBackgroundResource(CommonConsts.setChargeBg(skinSetting));
        }
    }

}
