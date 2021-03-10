package com.pearl.act.base;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.ab.debug.AbDebug;
import com.ab.util.Strs;
import com.pearl.act.ActModule;

/**
 * fragment显示时, 回调AbBaseFragment的onShow方法.并
 * 默认实现界面的渐显,  隐藏时回调 onHide方法, 并默认实现界面渐隐
 * <p>
 * Created by  on 16/8/31.
 */
public abstract class AbBaseFragment extends Fragment {
    public Activity context;
    public boolean isShowing = false;
    public boolean isCreated = false;
    private View contentView = null;
    private int showingAnimResId = 0;
    private int hidingAnimResId = 0;
    private String pageName = "";

    protected boolean isViewInitiated;
    protected boolean isVisibleToUser;
    protected boolean isDataInitiated;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = ((Activity) context);
        AbDebug.log(AbDebug.TAG_UI, "fragment:" + getSimpleName() + " 开始附加于 activity:" + context.getClass().getSimpleName());
    }

    public abstract int getLayoutId();

    public abstract void init(View root);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(getLayoutId(), null);
            AbDebug.log(AbDebug.TAG_UI, "fragment:" + getSimpleName() + "#init()");
            init(contentView);
        } else {
            AbDebug.log(AbDebug.TAG_UI, "fragment:" + getSimpleName() + "#init skip");
        }

        return contentView;
    }

    /**
     * fragment加入activity成功, 可以开始加载数据,
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        isCreated = true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewInitiated = true;
        prepareFetchData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        prepareFetchData();
        if (isVisibleToUser) {
            if (!isShowing && contentView != null) {
                isShowing = true;
                AbDebug.log(AbDebug.TAG_UI, "fragment:" + getSimpleName() + "#onShow()");
                onShow();
            }
        } else {
            if (isShowing) {
                isShowing = false;
                AbDebug.log(AbDebug.TAG_UI, "fragment:" + getSimpleName() + "#onHide()");
                onHide();
            }
        }
    }

    public boolean prepareFetchData() {
        return prepareFetchData(false);
    }

    public boolean prepareFetchData(boolean forceUpdate) {
        if (isVisibleToUser && isViewInitiated && (!isDataInitiated || forceUpdate)) {
            //fetchData();
            isDataInitiated = true;
            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isShowing && contentView != null) {
            isShowing = true;
            AbDebug.log(AbDebug.TAG_UI, "progressFragment:" + getSimpleName() + "#onShow()");
            onShow();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isShowing) {
            isShowing = false;
            AbDebug.log(AbDebug.TAG_UI, "progressFragment:" + getSimpleName() + "#onHide()");
            onHide();
        }

    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public void setShowingAnim(int resId) {
        showingAnimResId = resId;
    }

    public void setHidingAnim(int resId) {
        hidingAnimResId = resId;
    }


    /**
     * 当显示时.重写以在任何情况下获取其是否显示
     */
    public void onShow() {
        startAnimation(showingAnimResId);
        if (ActModule.isAnalyticsEnable) {
            if (Strs.isNotEmpty(pageName)) {
                //统计实现
            }
        }
    }

    /**
     * 当隐藏时,重写以在任何情况下获取其是否显示
     */
    public void onHide() {
        startAnimation(hidingAnimResId);
        if (ActModule.isAnalyticsEnable) {
            if (Strs.isNotEmpty(pageName)) {
                //统计实现
            }
        }
    }

    private void startAnimation(int animResId) {
        if (getView() != null && getActivity() != null && !getActivity().isFinishing() && animResId != 0) {
            getView().startAnimation(AnimationUtils.loadAnimation(getActivity(), animResId));
        }
    }

    public String getName() {
        return getClass().getName();
    }

    public String getSimpleName() {
        return getClass().getSimpleName();
    }

    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public View getContentView() {
        return contentView;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public boolean isCreated() {
        return isCreated;
    }

    //快速启动
    public static void simpleLaunch(Context ctx, Class clazz){
        Intent itt = new Intent(ctx, clazz);
        ctx.startActivity(itt);
    }

}
