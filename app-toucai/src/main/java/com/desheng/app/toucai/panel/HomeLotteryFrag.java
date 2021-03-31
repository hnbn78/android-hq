package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.context.LotteryTypeCustom;
import com.desheng.app.toucai.context.ThirdGamePlatform;
import com.desheng.app.toucai.fragment.BasePageFragment;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.LotteryInfoCustom;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.panel.ActAddMoreLottery;
import com.desheng.base.util.CacheServerResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shark.tc.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class HomeLotteryFrag extends BasePageFragment {

    List<MtestEntity> data = new ArrayList<>();
    private RecyclerView mShoucangRv;
    private RecyclerView mGameHallRv;
    private BaseQuickAdapter<TempEntry, BaseViewHolder> mAdapter;
    private List<String> listGameHallTabTitle = new ArrayList<>(0);
    private List<LotteryTypeCustom> listType;
    private BaseQuickAdapter<LotteryInfo, BaseViewHolder> mShoucangAdapter;
    private List<LotteryInfo> listLotteryInfo;
    private static final int KEY_GAME_TYPE = 9;

    public static HomeLotteryFrag newInstance() {
        HomeLotteryFrag fragment = new HomeLotteryFrag();
        return fragment;
    }

    @Override
    public void fetchData() {
        //getOpenData();
    }

    @Override
    protected int setContentView() {
        return R.layout.home_lottery_frag_layout;
    }

    @Override
    protected void initView(View rootview) {
        mShoucangRv = rootview.findViewById(R.id.ShoucangRv);
        mGameHallRv = rootview.findViewById(R.id.GameHallRv);
        mGameHallRv.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new BaseQuickAdapter<TempEntry, BaseViewHolder>(R.layout.item_home_lottery_layout) {
            @Override
            protected void convert(BaseViewHolder helper, TempEntry item) {
                boolean showRc = false;
                RecyclerView rc = (RecyclerView) helper.getView(R.id.innerRecycleView);
                rc.setLayoutManager(new GridLayoutManager(mActivity, 3));
                BaseQuickAdapter<LotteryInfoCustom, BaseViewHolder> adapter = new BaseQuickAdapter<LotteryInfoCustom,
                        BaseViewHolder>(R.layout.item_layout_game_hall_caizhong) {
                    @Override
                    protected void convert(BaseViewHolder helper, LotteryInfoCustom item2) {
                        helper.setText(R.id.tvName, item2.getShowName());
                        helper.setVisible(R.id.ivTag, item2.getHot() == 1);
                    }
                };
                rc.setAdapter(adapter);

                adapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter ad, View view, int position) {
                        LotteryInfoCustom infoCustom = adapter.getData().get(position);
                        if (UserManager.getIns().isLogined()) {
                            DialogsTouCai.showProgressDialog(mActivity, "");
                            if (infoCustom.getShowType() == KEY_GAME_TYPE) {//进入游戏
                                getH5GameUrl(mContext, infoCustom);
                            } else {
                                CtxLotteryTouCai.launchLotteryPlay(mContext, infoCustom);//进入彩票
                            }
                        } else {
                            UserManager.getIns().redirectToLogin((Activity) mContext);
                        }
                    }
                });

                if (item.getData().size() == 1) {
                    boolean isOpen0 = item.getData().get(0).isOpen;
                    showRc = isOpen0;
                    if (isOpen0) {
                        adapter.setNewData(item.getData().get(0).getData());
                    }
                    helper.setVisible(R.id.ivSelect1, isOpen0);
                    helper.setVisible(R.id.tv1, true);
                    helper.setVisible(R.id.tv2, false);
                    helper.setVisible(R.id.tv3, false);
                    helper.setBackgroundRes(R.id.tv1, getBgfromName(item.getData().get(0).name));
                } else if (item.getData().size() == 2) {
                    boolean isOpen0 = item.getData().get(0).isOpen;
                    boolean isOpen1 = item.getData().get(1).isOpen;
                    showRc = isOpen0 || isOpen1;
                    if (isOpen0) {
                        adapter.setNewData(item.getData().get(0).getData());
                    }
                    helper.setVisible(R.id.ivSelect1, isOpen0);
                    if (isOpen1) {
                        adapter.setNewData(item.getData().get(1).getData());
                    }
                    helper.setVisible(R.id.ivSelect2, isOpen1);
                    helper.setVisible(R.id.tv1, true);
                    helper.setVisible(R.id.tv2, true);
                    helper.setVisible(R.id.tv3, false);
                    helper.setBackgroundRes(R.id.tv1, getBgfromName(item.getData().get(0).name));
                    helper.setBackgroundRes(R.id.tv2, getBgfromName(item.getData().get(1).name));
                } else if (item.getData().size() == 3) {
                    boolean isOpen0 = item.getData().get(0).isOpen;
                    boolean isOpen1 = item.getData().get(1).isOpen;
                    boolean isOpen2 = item.getData().get(2).isOpen;
                    showRc = isOpen0 || isOpen1 || isOpen2;
                    if (isOpen0) {
                        adapter.setNewData(item.getData().get(0).getData());
                    }
                    helper.setVisible(R.id.ivSelect1, isOpen0);
                    if (isOpen1) {
                        adapter.setNewData(item.getData().get(1).getData());
                    }
                    helper.setVisible(R.id.ivSelect2, isOpen1);
                    if (isOpen2) {
                        adapter.setNewData(item.getData().get(2).getData());
                    }
                    helper.setVisible(R.id.ivSelect3, isOpen2);
                    helper.setVisible(R.id.tv1, true);
                    helper.setVisible(R.id.tv2, true);
                    helper.setVisible(R.id.tv3, true);
                    helper.setBackgroundRes(R.id.tv1, getBgfromName(item.getData().get(0).name));
                    helper.setBackgroundRes(R.id.tv2, getBgfromName(item.getData().get(1).name));
                    helper.setBackgroundRes(R.id.tv3, getBgfromName(item.getData().get(2).name));
                }
                rc.setVisibility(showRc ? View.VISIBLE : View.GONE);
                helper.getView(R.id.tv1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.getData().get(0).isOpen) {
                            List<TempEntry> data = mAdapter.getData();
                            for (TempEntry datum : data) {
                                List<MtestEntity> innerdata = datum.getData();
                                for (MtestEntity innerdatum : innerdata) {
                                    innerdatum.setOpen(false);
                                }
                            }
                        } else {
                            List<TempEntry> data = mAdapter.getData();
                            for (TempEntry datum : data) {
                                List<MtestEntity> innerdata = datum.getData();
                                for (MtestEntity innerdatum : innerdata) {
                                    innerdatum.setOpen(false);
                                }
                            }
                            if (item.getData().get(0).isOpen) {
                                item.getData().get(0).setOpen(false);
                            } else {
                                item.getData().get(0).setOpen(true);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });

                helper.getView(R.id.tv2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.getData().get(1).isOpen) {
                            List<TempEntry> data = mAdapter.getData();
                            for (TempEntry datum : data) {
                                List<MtestEntity> innerdata = datum.getData();
                                for (MtestEntity innerdatum : innerdata) {
                                    innerdatum.setOpen(false);
                                }
                            }
                        } else {
                            List<TempEntry> data = mAdapter.getData();
                            for (TempEntry datum : data) {
                                List<MtestEntity> innerdata = datum.getData();
                                for (MtestEntity innerdatum : innerdata) {
                                    innerdatum.setOpen(false);
                                }
                            }

                            if (item.getData().get(1).isOpen) {
                                item.getData().get(1).setOpen(false);
                            } else {
                                item.getData().get(1).setOpen(true);
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                });

                helper.getView(R.id.tv3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.getData().get(2).isOpen) {
                            List<TempEntry> data = mAdapter.getData();
                            for (TempEntry datum : data) {
                                List<MtestEntity> innerdata = datum.getData();
                                for (MtestEntity innerdatum : innerdata) {
                                    innerdatum.setOpen(false);
                                }
                            }
                        } else {
                            List<TempEntry> data = mAdapter.getData();
                            for (TempEntry datum : data) {
                                List<MtestEntity> innerdata = datum.getData();
                                for (MtestEntity innerdatum : innerdata) {
                                    innerdatum.setOpen(false);
                                }
                            }
                            if (item.getData().get(2).isOpen) {
                                item.getData().get(2).setOpen(false);
                            } else {
                                item.getData().get(2).setOpen(true);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        mGameHallRv.setAdapter(mAdapter);
        mShoucangRv.setLayoutManager(new GridLayoutManager(mActivity, 3));
        mShoucangAdapter = new BaseQuickAdapter<LotteryInfo, BaseViewHolder>(R.layout.item_game_hall_shoucang) {
            @Override
            protected void convert(BaseViewHolder helper, LotteryInfo item) {
                helper.setText(R.id.tvName, item.getShowName());
                ILotteryKind kind = LotteryKind.find(item.getId());
                Glide.with(mContext).load(Uri.parse(kind.getIconActive())).into(((ImageView) helper.getView(R.id.ivTag)));
            }
        };
        mShoucangRv.setAdapter(mShoucangAdapter);
        mShoucangAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (UserManager.getIns().isLogined()) {
                    DialogsTouCai.showProgressDialog(mActivity, "");
                    LotteryInfo lotteryInfo = mShoucangAdapter.getData().get(position);
                    CtxLotteryTouCai.launchLotteryPlay(mActivity, lotteryInfo);//进入彩票
                } else {
                    UserManager.getIns().redirectToLogin(mActivity);
                }
            }
        });
        rootview.findViewById(R.id.ivAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserManager.getIns().isLogined()) {
                    ActAddMoreLottery.launch(mActivity);
                } else {
                    UserManager.getIns().redirectToLogin(mActivity);
                }
            }
        });
        getOpenData();
    }

    private int getBgfromName(String name) {
        switch (name) {
            case "低频彩":
                return R.mipmap.home_diping;
            case "时时彩":
                return R.mipmap.home_shishicai;
            case "高频彩":
                return R.mipmap.home_gaopin;
            case "11选5":
                return R.mipmap.home_11xuan5;
            case "其他":
                return R.mipmap.home_qita;
            case "菲律宾":
                return R.mipmap.home_flbcai;
            case "快三":
                return R.mipmap.home_kuai3;
            case "PK10":
                return R.mipmap.home_pk10;
            case "经典彩种":
                return R.mipmap.home_jingdian;
            case "热门游戏":
                return R.mipmap.home_remen;
            case "天彩VR":
                return R.mipmap.home_vr;
            default:
                return 0;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserSelectedLotterys();
    }

    private void getUserSelectedLotterys() {
        if (listLotteryInfo == null) {
            listLotteryInfo = new ArrayList<>();
        }
        listLotteryInfo.clear();
        ArrayList<Integer> selectedLotterys = UserManagerTouCai.getIns().getUserSelectedLotterys();
        for (int i = 0; i < selectedLotterys.size(); i++) {
            ILotteryKind kind = CtxLottery.getIns().findLotteryKind(selectedLotterys.get(i));
            listLotteryInfo.add(new LotteryInfo(kind.getId(), kind.getCode(), kind.getShowName(), kind.getIconActive()));
        }
        if (mShoucangAdapter != null) {
            mShoucangAdapter.setNewData(listLotteryInfo);
        }
    }

    /**
     * 获取首页游戏大厅数据
     */
    public void getOpenData() {
        if (CacheServerResponse.isCacheDataFailure(mActivity.getApplicationContext(), "getLotteryRoom")) {
            HttpActionTouCai.getLotteryRoom(this, new AbHttpResult() {

                @Override
                public void onBefore(Request request, int id, String host, String funcName) {
                    Dialogs.showLoadingDialog(mActivity, "");
                }

                @Override
                public boolean onGetString(String str) {
                    if (Strs.isNotEmpty(str)) {

                        if (CacheServerResponse.saveObject(mActivity.getApplicationContext(), "getLotteryRoom", str.replace("\\n", ""))) {
                            Log.e("FragTabHome", "缓存成功");
                        } else {
                            Log.e("FragTabHome", "缓存失败");
                        }

                        if (listType == null) {
                            listType = new ArrayList<LotteryTypeCustom>();
                        }
                        listType.clear();
                        Collection<? extends LotteryTypeCustom> lotteryTypeCustoms = (Collection<? extends LotteryTypeCustom>) new Gson().fromJson(str.replace("\\n", ""), new TypeToken<ArrayList<LotteryTypeCustom>>() {
                        }.getType());
                        listType.addAll(lotteryTypeCustoms);
                        Collections.sort(listType, new Comparator<LotteryTypeCustom>() {
                            @Override
                            public int compare(LotteryTypeCustom o1, LotteryTypeCustom o2) {
                                return o1.getSort() - o2.getSort();
                            }
                        });

                        listGameHallTabTitle.clear();
                        data.clear();
                        List<ArrayList<LotteryInfoCustom>> gameRvList = new ArrayList<>();

                        for (LotteryTypeCustom custom : listType) {
                            if (custom.getLotteryList() != null && custom.getLotteryList().size() > 0 &&
                                    (!Strs.isEqual("CQ9电游", custom.getLotteryName()) && !Strs.isEqual("PT电游", custom.getLotteryName()))
                                    && !Strs.isEqual("LEG棋牌", custom.getLotteryName()) && !Strs.isEqual("热门彩种", custom.getLotteryName())
                                    && !Strs.isEqual("大唐棋牌", custom.getLotteryName())) {
                                listGameHallTabTitle.add(custom.getLotteryName());
                                ArrayList<LotteryInfoCustom> gameList = new ArrayList<>(0);
                                for (LotteryInfoCustom datum : custom.getLotteryList()) {
                                    if (datum.getThirdPlatformId() != ThirdGamePlatform.PT.getPlatformId()
                                            && datum.getThirdPlatformId() != ThirdGamePlatform.CQ.getPlatformId()) {
                                        gameList.add(datum);
                                    }
                                }

                                MtestEntity e = new MtestEntity();
                                e.setData(gameList);
                                e.setName(custom.getLotteryName());
                                data.add(e);
                            }
                        }


                        List<TempEntry> tempData = new ArrayList<>();

                        int size = data.size();
                        int k = ((int) Math.ceil(size * 1.0 / 3 * 1.0));

                        Log.e("HomeLotteryFrag", "k:" + k);

                        for (int i = 0; i < k; i++) {
                            List<MtestEntity> dd = new ArrayList<>();
                            if (3 * i < size) {
                                dd.add(data.get(3 * i));
                            }
                            if (3 * i + 1 < size) {
                                dd.add(data.get(3 * i + 1));
                            }
                            if (3 * i + 2 < size) {
                                dd.add(data.get(3 * i + 2));
                            }
                            TempEntry e = new TempEntry();
                            e.setData(dd);
                            tempData.add(e);
                        }
                        mAdapter.setNewData(tempData);

//                        Observable.just("").map(new Function<String, Boolean>() {
//                            @Override
//                            public Boolean apply(String s) throws Exception {
//
//
//                                return true;
//                            }
//                        }).subscribeOn(Schedulers.io())//把工作线程指定为了IO线程
//                                .observeOn(AndroidSchedulers.mainThread())//把回调线程指定为了UI线程
//                                .subscribe(new Consumer<Boolean>() {
//                                    @Override
//                                    public void accept(Boolean aBoolean) throws Exception
//
//
//                                });
                    }
                    return true;
                }

                @Override
                public boolean onError(int status, String content) {
                    return super.onError(status, content);
                }

                @Override
                public void onFinish() {
                    Dialogs.hideProgressDialog(mActivity);
                }

            });
        } else {

            Serializable getLotteryRoom = CacheServerResponse.readObject(mActivity.getApplicationContext(), "getLotteryRoom");
            String cachedData = (String) getLotteryRoom;
            Log.d("FragTabHome", "getLotteryRoom1 :" + cachedData);
            if (listType == null) {
                listType = new ArrayList<LotteryTypeCustom>();
            }
            listType.clear();
            Collection<? extends LotteryTypeCustom> lotteryTypeCustoms = (Collection<? extends LotteryTypeCustom>) new Gson().fromJson(cachedData, new TypeToken<ArrayList<LotteryTypeCustom>>() {
            }.getType());
            listType.addAll(lotteryTypeCustoms);
            Collections.sort(listType, new Comparator<LotteryTypeCustom>() {
                @Override
                public int compare(LotteryTypeCustom o1, LotteryTypeCustom o2) {
                    return o1.getSort() - o2.getSort();
                }
            });

            listGameHallTabTitle.clear();
            data.clear();

            for (LotteryTypeCustom custom : listType) {
                if (custom.getLotteryList() != null && custom.getLotteryList().size() > 0 &&
                        (!Strs.isEqual("CQ9电游", custom.getLotteryName()) && !Strs.isEqual("PT电游", custom.getLotteryName())
                                && !Strs.isEqual("LEG棋牌", custom.getLotteryName()) && !Strs.isEqual("热门彩种", custom.getLotteryName()))
                        && !Strs.isEqual("大唐棋牌", custom.getLotteryName())) {
                    listGameHallTabTitle.add(custom.getLotteryName());

                    ArrayList<LotteryInfoCustom> gameList = new ArrayList<>(0);
                    for (LotteryInfoCustom datum : custom.getLotteryList()) {
                        if (datum.getThirdPlatformId() != ThirdGamePlatform.PT.getPlatformId()
                                && datum.getThirdPlatformId() != ThirdGamePlatform.CQ.getPlatformId()) {
                            gameList.add(datum);
                        }
                    }

                    MtestEntity e = new MtestEntity();
                    e.setData(gameList);
                    e.setName(custom.getLotteryName());
                    data.add(e);
                }
            }


            List<TempEntry> tempData = new ArrayList<>();

            int size = data.size();
            int k = ((int) Math.ceil(size * 1.0 / 3 * 1.0));

            Log.e("HomeLotteryFrag", "k:" + k);

            for (int i = 0; i < k; i++) {
                List<MtestEntity> dd = new ArrayList<>();
                if (3 * i < size) {
                    dd.add(data.get(3 * i));
                }
                if (3 * i + 1 < size) {
                    dd.add(data.get(3 * i + 1));
                }
                if (3 * i + 2 < size) {
                    dd.add(data.get(3 * i + 2));
                }
                TempEntry e = new TempEntry();
                e.setData(dd);
                tempData.add(e);
            }

            mAdapter.setNewData(tempData);

//            Observable.just("")
//                    .map(new Function<String, Boolean>() {
//                        @Override
//                        public Boolean apply(String s) throws Exception {
//
//                            return true;
//                        }
//                    }).subscribeOn(Schedulers.io())//把工作线程指定为了IO线程
//                    .observeOn(AndroidSchedulers.mainThread())//把回调线程指定为了UI线程
//                    .subscribe(new Consumer<Boolean>() {
//                        @Override
//                        public void accept(Boolean B) throws Exception, OnErrorNotImplementedException {
//                            if (!B) {
//                                return;
//                            }
//
//                            listGameHallTabTitle.clear();
//                            data.clear();
//
//                            for (LotteryTypeCustom custom : listType) {
//                                if (custom.getLotteryList() != null && custom.getLotteryList().size() > 0 &&
//                                        (!Strs.isEqual("CQ9电游", custom.getLotteryName()) && !Strs.isEqual("PT电游", custom.getLotteryName())
//                                                && !Strs.isEqual("LEG棋牌", custom.getLotteryName()) && !Strs.isEqual("热门彩种", custom.getLotteryName()))) {
//                                    listGameHallTabTitle.add(custom.getLotteryName());
//
//                                    ArrayList<LotteryInfoCustom> gameList = new ArrayList<>(0);
//                                    for (LotteryInfoCustom datum : custom.getLotteryList()) {
//                                        if (datum.getThirdPlatformId() != ThirdGamePlatform.PT.getPlatformId()
//                                                && datum.getThirdPlatformId() != ThirdGamePlatform.CQ.getPlatformId()) {
//                                            gameList.add(datum);
//                                        }
//                                    }
//
//                                    MtestEntity e = new MtestEntity();
//                                    e.setData(gameList);
//                                    e.setName(custom.getLotteryName());
//                                    data.add(e);
//                                }
//                            }
//
//
//                            List<TempEntry> tempData = new ArrayList<>();
//
//                            int size = data.size();
//                            int k = ((int) Math.ceil(size * 1.0 / 3 * 1.0));
//
//                            Log.e("HomeLotteryFrag", "k:" + k);
//
//                            for (int i = 0; i < k; i++) {
//                                List<MtestEntity> dd = new ArrayList<>();
//                                if (3 * i < size) {
//                                    dd.add(data.get(3 * i));
//                                }
//                                if (3 * i + 1 < size) {
//                                    dd.add(data.get(3 * i + 1));
//                                }
//                                if (3 * i + 2 < size) {
//                                    dd.add(data.get(3 * i + 2));
//                                }
//                                TempEntry e = new TempEntry();
//                                e.setData(dd);
//                                tempData.add(e);
//                            }
//
//                            mAdapter.setNewData(tempData);
//                        }
//                    });
//            RxJavaPlugins.setErrorHandler(new emp);
        }
    }

    private void getH5GameUrl(Context context, LotteryInfoCustom lotteryInfoCustom) {
        HttpActionTouCai.getGetThirdGameUrl(this, lotteryInfoCustom.getThirdPlatformId(), lotteryInfoCustom.getCode(), new AbHttpResult() {

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<String>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    String data = (String) extra.get("data");
                    if (data != null) {
                        Log.d("SimplePageFragment", data);
                        checkQipaiAccount(context, lotteryInfoCustom, data);
                    }
                } else {
                    Toasts.show(mActivity, msg, false);
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogsTouCai.hideProgressDialog(mActivity);
            }
        });
    }

    private void checkQipaiAccount(Context mContext, LotteryInfoCustom lotteryListdata, String dataJson) {

        //查询用户余额
        HttpActionTouCai.getThirdPocketAmount(mContext, lotteryListdata.getThirdPlatformId(), new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", Double.TYPE);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (code == 0 && error == 0) {
                    Double balance = getField(extra, "data", 0.0);

                    if (balance >= 50) {//棋牌资金大于等于50元，直接进入游戏
                        ActThirdGameMain.launch(mActivity, lotteryListdata.getThirdPlatformId(),
                                ThirdGamePlatform.findByPlatformId(lotteryListdata.getThirdPlatformId()).getScreenOrintation(), dataJson);
                        UserManagerTouCai.getIns().setUserChangwanGameData(lotteryListdata);
                    } else {
                        //先检查棋牌账户资金是否充足
                        double mainAc = UserManagerTouCai.getIns().getLotteryAvailableBalance();
                        String cbId = ThirdGamePlatform.findByPlatformId(lotteryListdata.getThirdPlatformId()).getCbId();
                        String gametitle = ThirdGamePlatform.findByPlatformId(lotteryListdata.getThirdPlatformId()).getTitle();
                        DialogsTouCai.showDSchessAcountCheckDialog(mActivity, balance, mainAc, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {//跳转到充值页
                                ActDeposit.launch(mContext);
                            }
                        }, new DialogsTouCai.IChargeAndGameListener() {//转账后进入游戏
                            @Override
                            public boolean onChargeAndGameListener(View v, int chargeMoney, String pwd, Dialog dialog) {
                                Log.e("SimplePageFragment", chargeMoney + "");
                                return recharge(v, chargeMoney, cbId, lotteryListdata, dataJson, pwd, dialog);
                            }
                        }, new View.OnClickListener() {//直接进入游戏
                            @Override
                            public void onClick(View v) {
                                //ActWebX5.launch(mContext, "DS棋牌", dataJson, true);
                                ActThirdGameMain.launch(mActivity, lotteryListdata.getThirdPlatformId(),
                                        ThirdGamePlatform.findByPlatformId(lotteryListdata.getThirdPlatformId()).getScreenOrintation(), dataJson);
                                UserManagerTouCai.getIns().setUserChangwanGameData(lotteryListdata);
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogsTouCai.hideProgressDialog(mActivity);
                            }
                        }, gametitle);
                    }
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }
        });

    }

    private boolean recharge(View contentView, int chargeMoneyamount, String cbAccount,
                             LotteryInfoCustom lotteryListdata, String dataJson, String pwd, Dialog dialog) {

        if (chargeMoneyamount <= 0) {
            Toasts.show(mActivity, "单次转入金额最低1元, 请重试", false);
            return false;
        }

        double outBalance = UserManager.getIns().getLotteryAvailableBalance();
        if (chargeMoneyamount > outBalance) {
            Toasts.show(mActivity, "转账金额超过转出账户余额, 请重试", false);
            return false;
        }

        if (chargeMoneyamount > 100000) {
            Toasts.show(mActivity, "单次转入金额最高100000元, 请重试", false);
            return false;
        }

        final String cbIdOut = CtxLottery.MAIN_POCKET_CBID;
        String cbIdIn = cbAccount;
        HttpAction.getPlayerTransferRefreshToken(mActivity, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                contentView.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(mActivity, "");
                    }
                });
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", String.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    String token = getField(extra, "data", "");
                    if (Strs.isNotEmpty(token)) {

                        HttpAction.commitPlayerTransferTC(getActivity(), cbIdOut, cbIdIn, chargeMoneyamount, pwd, token, new AbHttpResult() {
                            @Override
                            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                if (code == 0 && error == 0) {

                                    ActThirdGameMain.launch(mActivity, lotteryListdata.getThirdPlatformId(),
                                            ThirdGamePlatform.findByPlatformId(lotteryListdata.getThirdPlatformId()).getScreenOrintation(), dataJson);
                                    UserManagerTouCai.getIns().setUserChangwanGameData(lotteryListdata);

                                    Toasts.show(mActivity, "转账成功, 请稍候查询余额!", true);
                                    //同步用户数据
                                    UserManager.getIns().initUserData(new UserManager.IUserDataSyncCallback() {
                                        @Override
                                        public void onUserDataInited() {
                                        }

                                        @Override
                                        public void afterUserDataInited() {
                                        }

                                        @Override
                                        public void onUserDataInitFaild() {
                                        }

                                        @Override
                                        public void onAfter() {
                                        }
                                    });

                                    return true;

                                } else {
                                    Toasts.show(mActivity, msg, false);
                                }
                                return true;
                            }

                            @Override
                            public boolean onError(int status, String content) {
                                Dialogs.hideProgressDialog(mActivity);
                                Toasts.show(mActivity, content, false);
                                return true;
                            }

                            @Override
                            public void onAfter(int id) {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                Dialogs.hideProgressDialog(mActivity);
                            }
                        });

                    }
                } else {

                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Dialogs.hideProgressDialog(mActivity);
                Toasts.show(mActivity, content, false);
                return true;
            }
        });

        return false;
    }

    class TempEntry {
        public String name;
        private List<MtestEntity> data;

        public List<MtestEntity> getData() {
            return data;
        }


        public void setData(List<MtestEntity> data) {
            this.data = data;
        }
    }

    class MtestEntity {
        public String name;
        private List<LotteryInfoCustom> data;
        private boolean isOpen;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<LotteryInfoCustom> getData() {
            return data;
        }

        public void setData(List<LotteryInfoCustom> data) {
            this.data = data;
        }

        public boolean isOpen() {
            return isOpen;
        }

        public void setOpen(boolean open) {
            isOpen = open;
        }

    }
}
