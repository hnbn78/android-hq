package com.desheng.app.toucai.panel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;

import com.ab.util.Toasts;
import com.pearl.view.jellyrefresh.JellyRefreshLayout;
import com.pearl.view.jellyrefresh.PullToRefreshLayout;
import com.shark.tc.R;


public class ActLotteryLHC extends AppCompatActivity {

    private JellyRefreshLayout mJellyLayout;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_lottery_lhc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Jelly");
        mJellyLayout = (JellyRefreshLayout) findViewById(R.id.jelly_refresh);
        mJellyLayout.setPullToRefreshListener(new PullToRefreshLayout.PullToRefreshListener() {
            @Override
            public void onPrepareRefresh(PullToRefreshLayout pullToRefreshLayout) {
              
                        Toasts.show(ActLotteryLHC.this, "正在刷新...", true);
                        mJellyLayout.setRefreshing(false);
                
            }

            @Override
            public void onTriggerRefresh(PullToRefreshLayout pullToRefreshLayout) {

            }

            @Override
            public void onExtending(PullToRefreshLayout pullToRefreshLayout) {
              
                    Toasts.show(ActLotteryLHC.this, "显示历史...", true);
                    mJellyLayout.findViewById(R.id.tvHestory).setVisibility(View.VISIBLE);
            
            }

            @Override
            public void onSecondaryExtending(PullToRefreshLayout pullToRefreshLayout) {
                
            }

            @Override
            public void onCollasping(PullToRefreshLayout pullToRefreshLayout) {
                Toasts.show(ActLotteryLHC.this, "关闭历史...", true);
                mJellyLayout.findViewById(R.id.tvHestory).setVisibility(View.GONE);
            }
        });
        View loadingView = LayoutInflater.from(this).inflate(R.layout.view_loading, null);
        findViewById(R.id.vgCenter).bringToFront();
        mJellyLayout.setLoadingView(loadingView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    
}
