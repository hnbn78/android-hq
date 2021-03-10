package com.desheng.app.toucai.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BasePageFragment extends Fragment {

    protected boolean isViewInitiated;
    protected boolean isVisibleToUser;
    protected boolean isDataInitiated;
    private View mRootview;//视图
    public Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = ((Activity) context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootview = inflater.inflate(setContentView(), container, false);
        /**初始化的时候去加载数据**/
        initView(mRootview);
        return mRootview;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewInitiated = true;
        prepareFetchData();
    }

    /**
     * 用这个方法来判断当前UI是否可见
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        prepareFetchData();
    }


    /**
     * 子Fragment只需要继承父类，实现抽象方法，在fetchData（）里做网络请求或者其他耗时操作即可
     */
    public abstract void fetchData();

    /**
     * 就是当前UI可见，并且fragment已经初始化完毕，如果网络数据未加载，那么请求数据，或者需要强制刷新页面，那么也去请求数据
     *
     * @return
     */
    public boolean prepareFetchData() {
        return prepareFetchData(false);
    }


    public boolean prepareFetchData(boolean forceUpdate) {
        if (isVisibleToUser && isViewInitiated && (!isDataInitiated || forceUpdate)) {
            fetchData();
            isDataInitiated = true;
            return true;
        }
        return false;
    }

    protected abstract int setContentView();//显示的布局

    protected abstract void initView(View rootview);
}
