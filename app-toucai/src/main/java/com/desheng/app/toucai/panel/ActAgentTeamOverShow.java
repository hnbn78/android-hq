package com.desheng.app.toucai.panel;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ab.util.AbDateUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.adapter.PopupItemAdapter;
import com.noober.background.view.BLTextView;
import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.List;

public class ActAgentTeamOverShow extends AbAdvanceActivity implements View.OnClickListener {
    private TextView tv_title;
    String[] types = {"充值", "提现", "投注量", "派奖", "活动", "返点", "新增用户"};
    String[] dates = {"今天", "最近三天", "最近七天"};
    String[] teamDetailArray = {"充值", "提现", "消费", "派奖", "返点"};

    private TextView teamBalance, teamMemberNum, agentNum, playerOnlineNum;
    private BLTextView startTimeBL, stopTimeBL, selectFastBL, selectMethodBL;
    private RecyclerView agentRc;
    private BaseQuickAdapter<TeamData, BaseViewHolder> adapter;
    private PopupItemAdapter popupAdapter;
    private String[] args = types;
    private boolean isDates;
    private String startTime, endTime;
    private static final int CHOOSE_DEPOSIT_AMOUNT = 0;
    private static final int CHOOSE_WITHDRAW_AMOUNT = 1;
    private static final int CHOOSE_CONFIRM_AMOUNT = 2;
    private static final int CHOOSE_AWARD_AMOUNT = 3;
    private static final int CHOOSE_ACTIVITY_AMOUNT = 4;
    private static final int CHOOSE_POINT_AMOUNT = 5;
    private static final int CHOOSE_NEW_USER_COUNT = 6;
    private PopupWindow popupWindow;
    private int type = CHOOSE_DEPOSIT_AMOUNT;

    @Override
    protected int getLayoutId() {
        return R.layout.act_agent_team_overshow;
    }

    @Override
    protected void init() {
        hideToolbar();
        View headRoot = findViewById(R.id.vgToolbarGroup);
        paddingHeadOfStatusHeight(headRoot);
        setStatusBarTranslucentAndLightContentWithPadding();
        tv_title = ((TextView) headRoot.findViewById(R.id.tv_title));
        tv_title.setText("团队总览");
        initView();
    }

    private void initView() {
        teamBalance = ((TextView) findViewById(R.id.teamBalance));
        agentRc = ((RecyclerView) findViewById(R.id.agentRc));
        teamMemberNum = ((TextView) findViewById(R.id.teamMemberNum));
        agentNum = ((TextView) findViewById(R.id.agentNum));
        playerOnlineNum = ((TextView) findViewById(R.id.playerOnlineNum));
        startTimeBL = ((BLTextView) findViewById(R.id.startTime));
        stopTimeBL = ((BLTextView) findViewById(R.id.stopTime));
        selectFastBL = ((BLTextView) findViewById(R.id.selectFast));
        selectMethodBL = ((BLTextView) findViewById(R.id.selectMethod));

        startTimeBL.setOnClickListener(this);
        stopTimeBL.setOnClickListener(this);
        selectFastBL.setOnClickListener(this);
        selectMethodBL.setOnClickListener(this);

        agentRc.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new BaseQuickAdapter<TeamData, BaseViewHolder>(R.layout.item_team_data) {
            @Override
            protected void convert(BaseViewHolder helper, TeamData item) {
                helper.setText(R.id.tvTeamCount, item.value);
                helper.setText(R.id.tvTeamKey, item.key);
            }
        };
        agentRc.setAdapter(adapter);
        popupWindow = createPopupWindow();
        initThisData();
    }

    private void initThisData() {
        List<TeamData> teamData = new ArrayList<>();

        int length = teamDetailArray.length;
        for (int i = 0; i < length; i++) {
            teamData.add(new TeamData(teamDetailArray[i], String.valueOf(i)));
        }

        adapter.setNewData(teamData);
    }

    private PopupWindow createPopupWindow() {
        // initialize a pop up window type
        PopupWindow popupWindow = new PopupWindow(this);

        View view = View.inflate(this, R.layout.simple_dropdown_item_1line, null);
        // the drop down list is a list view
        ListView listViewSort = (ListView) view;
        popupAdapter = new PopupItemAdapter(this, args);
        // set our adapter and pass our pop up window contents
        listViewSort.setAdapter(popupAdapter);
        // set on item selected
        listViewSort.setOnItemClickListener((parent, view1, position, id) -> {
            if (isDates) {
                switch (position) {
                    case 0:
                        startTime = AbDateUtil.
                                getStringByFormat(System.currentTimeMillis() - 1000 * 60 * 60 * 24,
                                        AbDateUtil.dateFormatYMDHMS);
                        break;
                    case 1:
                        startTime = AbDateUtil.
                                getStringByFormat(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 3,
                                        AbDateUtil.dateFormatYMDHMS);
                        break;
                    case 2:
                        startTime = AbDateUtil.
                                getStringByFormat(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 6,
                                        AbDateUtil.dateFormatYMDHMS);
                        break;
                }
                selectFastBL.setText(args[position]);
                onLoadData();
            } else {
                type = position;
                selectMethodBL.setText(args[position]);
                bindChartView();
            }
            popupWindow.dismiss();
        });
        // some other visual settings for popup window
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_b0_r3_white));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(150);
        popupWindow.setContentView(listViewSort);
        return popupWindow;
    }

    private void bindChartView() {
        
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.selectFast:
                isDates = true;
                args = dates;
                popupAdapter.resetArgs(args);
                popupWindow.showAsDropDown(selectFastBL, -50, 10);
                break;
            case R.id.selectMethod:
                isDates = false;
                args = types;
                popupAdapter.resetArgs(args);
                popupWindow.showAsDropDown(selectMethodBL, -50, 10);
                break;
        }
    }

    private void onLoadData() {

    }

    class TeamData {
        String key;
        String value;

        TeamData(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
