package com.desheng.app.toucai.panel;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.context.ThirdGamePlatform;
import com.desheng.app.toucai.fragment.BasePageFragment;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.manager.UserManager;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class HomeThirdGameFrag extends BasePageFragment implements BaseQuickAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private BaseQuickAdapter<GameEtry, BaseViewHolder> adapter;
    private View rootView;

    public static HomeThirdGameFrag newInstance(int gameType) {
        HomeThirdGameFrag fragment = new HomeThirdGameFrag();
        Bundle args = new Bundle();
        args.putInt("gameType", gameType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void fetchData() {

    }

    @Override
    protected int setContentView() {
        return R.layout.home_third_game_layout;
    }

    @Override
    protected void initView(View rootview) {
        int gameType = getArguments().getInt("gameType");
        mRecyclerView = rootview.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        adapter = new BaseQuickAdapter<GameEtry, BaseViewHolder>(R.layout.item_third_game_layout) {
            @Override
            protected void convert(BaseViewHolder helper, GameEtry item) {
                helper.setBackgroundRes(R.id.ivIcon, item.getIconRes());
            }
        };
        mRecyclerView.setAdapter(adapter);
        initdata(gameType);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(BaseQuickAdapter ad, View view, int position) {
        if (adapter == null) {
            return;
        }
        GameEtry gameEtry = adapter.getData().get(position);
        if (gameEtry.getGoType()) {
            ActCQ9game.launcher(mActivity, gameEtry.getPlatformId());
        } else {
            if (UserManager.getIns().isLogined()) {
                ThirdGamePlatform thirdGamePlatform = ThirdGamePlatform.findByPlatformId(gameEtry.getPlatformId());
                getThirdGameUrl(thirdGamePlatform);
            } else {
                UserManager.getIns().redirectToLogin(mActivity);
            }
        }
    }

    private void initdata(int gameType) {
        List<GameEtry> data = new ArrayList<>();
        switch (gameType) {
            case 1:
                data.clear();
                data.add(new GameEtry(R.mipmap.home_ag, ThirdGamePlatform.AGIN.getPlatformId()));
                data.add(new GameEtry(R.mipmap.bbin, ThirdGamePlatform.BBIN.getPlatformId()));
                data.add(new GameEtry(R.mipmap.home_og, ThirdGamePlatform.OG.getPlatformId()));
                adapter.setNewData(data);
                break;
            case 2:
                data.clear();
                data.add(new GameEtry(R.mipmap.home_cq9, ThirdGamePlatform.CQ.getPlatformId(), true));
                data.add(new GameEtry(R.mipmap.home_pt, ThirdGamePlatform.PT.getPlatformId(), true));
                adapter.setNewData(data);
                break;
            case 3:
                data.clear();
                data.add(new GameEtry(R.mipmap.home_ky, ThirdGamePlatform.KY.getPlatformId()));
                data.add(new GameEtry(R.mipmap.home_datang, ThirdGamePlatform.DT.getPlatformId(),true));
                data.add(new GameEtry(R.mipmap.home_leg, ThirdGamePlatform.LEG.getPlatformId(),true));
                adapter.setNewData(data);
                break;
            case 4:
                data.clear();
                data.add(new GameEtry(R.mipmap.home_im, ThirdGamePlatform.IM.getPlatformId()));
                adapter.setNewData(data);
                break;
        }

    }

    public void getThirdGameUrl(ThirdGamePlatform thirdGamePlatform) {
        String subGame = "";
        if (thirdGamePlatform == ThirdGamePlatform.IM) {
            subGame = "sport";
        }
        HttpActionTouCai.getThirdGameLink(mActivity, thirdGamePlatform.getPlatformId(), subGame, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(mActivity, "");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", String.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    String url = getField(extra, "data", "");
                    if (Strs.isNotEmpty(url)) {
                        ActThirdGameMain.launch(mActivity, thirdGamePlatform.getPlatformId(), thirdGamePlatform.getScreenOrintation());
                    }
                } else {
                    processError(msg);
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                processError(content);
                return true;
            }

            @Override
            public void onFinish() {
                Dialogs.hideProgressDialog(mActivity);
            }

            public void processError(String content) {
                Toasts.show(mActivity, content, false);
            }
        });
    }

    class GameEtry {
        private int iconRes;
        private int platformId;
        private boolean goType;//跳转类型，是否跳转至全部游戏列表

        public GameEtry(int iconRes, int platformId) {
            this.iconRes = iconRes;
            this.platformId = platformId;
        }

        public GameEtry(int iconRes, int platformId, boolean goType) {
            this.iconRes = iconRes;
            this.platformId = platformId;
            this.goType = goType;
        }

        public boolean getGoType() {
            return goType;
        }

        public void setGoType(boolean goType) {
            this.goType = goType;
        }

        public int getIconRes() {
            return iconRes;
        }

        public void setIconRes(int iconRes) {
            this.iconRes = iconRes;
        }

        public int getPlatformId() {
            return platformId;
        }

        public void setPlatformId(int platformId) {
            this.platformId = platformId;
        }
    }
}
