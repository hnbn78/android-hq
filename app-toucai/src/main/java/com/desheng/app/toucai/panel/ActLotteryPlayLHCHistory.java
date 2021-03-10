package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.http.AbHttpResult;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LotteryOpenHistory;
import com.desheng.base.panel.ActLotteryOpen;
import com.desheng.base.panel.ActLotteryTrend;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.List;

public class ActLotteryPlayLHCHistory extends AbAdvanceActivity {

    public static void launch(Activity activity, int lotteryId, String title) {
        Intent itt = new Intent(activity, ActLotteryPlayLHCHistory.class);
        itt.putExtra("lotteryId", lotteryId);
        itt.putExtra("title", title);
        activity.startActivity(itt);
        activity.overridePendingTransition(R.anim.open_top, R.anim.close);
    }

    private ListView listView;
    private ViewGroup vgToolbarGroup;

    private int lotteryId;
    private ILotteryKind lotteryKind;

    @Override
    protected int getLayoutId() {
        return R.layout.act_lottery_play_lhc_history;
    }

    @Override
    protected void init() {
        lotteryId = getIntent().getIntExtra("lotteryId", 0);
        String t = getIntent().getStringExtra("title");
        lotteryKind = CtxLottery.getIns().findLotteryKind(lotteryId);
        listView = findViewById(R.id.list);
        vgToolbarGroup = findViewById(R.id.vgToolbarGroup);

        hideToolbar();
        setToolbarBgColor(R.color.colorPrimary);
        paddingHeadOfStatusHeight(vgToolbarGroup);
        setStatusBarTranslucentAndLightContent();

        TextView title = findViewById(R.id.tvLotteryTitleCenter);
        title.setText(t);
        findViewById(R.id.ibLotteryPlayRightBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ListPopupWindow popup = new ListPopupWindow(ActLotteryPlayLHCHistory.this);
                popup.setBackgroundDrawable(getResources().getDrawable(R.mipmap.bg_popup_menu_lottery_play));

                if (LotteryKind.LHC.getId() == lotteryId) {
                    popup.setAdapter(new ActLotteryPlayLHC.PopupMenuAdapter(ActLotteryPlayLHCHistory.this));
                    popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            switch (position) {
                                case 0:
                                    //近期开奖
                                    ActLotteryOpen.launch(ActLotteryPlayLHCHistory.this, lotteryId);
                                    break;
                                case 1:
                                    ActBetRecord.launch(ActLotteryPlayLHCHistory.this);
                                    break;
                            }
                        }
                    });
                } else {
                    popup.setAdapter(new ActLotteryPlay.PopupMenuAdapter(ActLotteryPlayLHCHistory.this));
                    popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            switch (position) {
                                case 0:
                                    //走势图
                                    ActLotteryTrend.launch(ActLotteryPlayLHCHistory.this, lotteryId);
                                    break;
                                case 1:
                                    //近期开奖
                                    ActLotteryOpen.launch(ActLotteryPlayLHCHistory.this, lotteryId);
                                    break;
                                case 2:
                                    ActBetRecord.launch(ActLotteryPlayLHCHistory.this);
                                    break;
                            }
                        }
                    });
                }
                popup.setVerticalOffset(Views.dp2px(1));
                popup.setContentWidth(Views.dp2px(114.5f));
                popup.setAnchorView(v);
                popup.show();
            }
        });

        updateStaticOpen();
    }

    private void updateStaticOpen() {
        HttpActionTouCai.getLotteryOpenCodeHistory(this, lotteryKind.getCode(), true, new AbHttpResult() {
            @Override
            public boolean onGetString(String str) {
                try {
                    ArrayList<LotteryOpenHistory> listOpen = new Gson().fromJson(str, new TypeToken<ArrayList<LotteryOpenHistory>>() {
                    }.getType());
                    ListAdapter a = new LotteryOpenHistoryAdapter(listOpen);
                    listView.setAdapter(a);
                } catch (Exception e) {
                    Toasts.show(ActLotteryPlayLHCHistory.this, "暂无开奖信息!", false);
                }
                return true;
            }
        });
    }

    private class LotteryOpenHistoryAdapter extends BaseAdapter {
        private int[] ballBg = {
                R.drawable.ic_lhc_ball_red,
                R.drawable.ic_lhc_ball_red,
                R.drawable.ic_lhc_ball_blue,
                R.drawable.ic_lhc_ball_green,
        };
        private List<LotteryOpenHistory> list;
        int ballCount;

        public LotteryOpenHistoryAdapter(List<LotteryOpenHistory> list) {
            this.list = list;

            int ballCount = 6;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) != null && list.get(i).getCode() != null) {
                    String[] arr = list.get(i).getCode().split(",");

                    if (arr != null && arr.length > 0) {
                        ballCount = arr.length;
                        break;
                    }
                }
            }
            this.ballCount = ballCount;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.item_lottery_play_lhc_open_record, null);
                convertView.setTag(new LotteryOpenHistoryAdapter.ViewHolder(convertView));
            }

            LotteryOpenHistoryAdapter.ViewHolder vh = (LotteryOpenHistoryAdapter.ViewHolder) convertView.getTag();

            LotteryOpenHistory history = list.get(position);
            vh.issue.setText(String.format("第%s期", history.getIssue()));
//            vh.category.setText(history.getLottery());

            String[] codes;
            if (history.getCode() == null) {
                codes = new String[ballCount];
                for (int i = 0; i < ballCount; i++)
                    codes[i] = "0";
            } else {
                codes = history.getCode().split(",");
            }

            for (int i = 0; i < 10; i++) {
                if (codes == null || i > codes.length - 1) {
                    vh.num[i].setVisibility(View.GONE);
                } else {
                    vh.num[i].setVisibility(View.VISIBLE);
                    vh.num[i].setText(codes[i]);
                    try {
                        vh.num[i].setBackgroundResource(ballBg[UserManager.getIns().getListLHCBallColor().get(Strs.parse(codes[i], 1))]);
                    } catch (Exception e) {
                        e.printStackTrace();
                        vh.num[i].setBackgroundResource(ballBg[0]);
                    }
                }
            }

            if (ballCount > 8) {
                for (int i = 0; i < 10; i++) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) vh.num[i].getLayoutParams();
                    params.width = Views.dp2px(27);
                    params.height = Views.dp2px(27);
                    params.leftMargin = Views.dp2px(2);
                    params.rightMargin = Views.dp2px(2);
                    vh.num[i].setLayoutParams(params);
                    vh.num[i].setTextSize(12);
                }
            } else {
                for (int i = 0; i < 10; i++) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) vh.num[i].getLayoutParams();
                    params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    params.leftMargin = Views.dp2px(2);
                    params.rightMargin = Views.dp2px(2);
                    vh.num[i].setLayoutParams(params);
                    vh.num[i].setTextSize(15);
                }
            }


            return convertView;
        }

        class ViewHolder {
            TextView issue;
            TextView[] num = new TextView[10];
            TextView category;

            ViewHolder(View view) {
                issue = view.findViewById(R.id.tv_issue);
                num[0] = view.findViewById(R.id.tv_ball_1);
                num[1] = view.findViewById(R.id.tv_ball_2);
                num[2] = view.findViewById(R.id.tv_ball_3);
                num[3] = view.findViewById(R.id.tv_ball_4);
                num[4] = view.findViewById(R.id.tv_ball_5);
                num[5] = view.findViewById(R.id.tv_ball_6);
                num[6] = view.findViewById(R.id.tv_ball_7);
                num[7] = view.findViewById(R.id.tv_ball_8);
                num[8] = view.findViewById(R.id.tv_ball_9);
                num[9] = view.findViewById(R.id.tv_ball_10);
                category = view.findViewById(R.id.tv_category);
            }
        }
    }

}
