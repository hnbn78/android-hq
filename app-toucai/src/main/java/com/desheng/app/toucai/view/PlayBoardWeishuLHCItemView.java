package com.desheng.app.toucai.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.util.Strs;
import com.desheng.base.model.LotteryPlayLHCUI;
import com.desheng.base.model.LotteryPlayUserInfoLHC;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayBoardWeishuLHCItemView extends FrameLayout {
    private TextView tvTitle;
    private TextView tvOdds;
    private GridLayout glGrid;
    private View lltitle;

    private TextView labelOdds;
    private List<TextView> labelNums;
    private List<ImageView> labelIV;

    private int[] colors = {
            R.color.lhc_banbo_board_title_red_big,
            R.color.lhc_banbo_board_title_red_small,
            R.color.lhc_banbo_board_title_red_single,
            R.color.lhc_banbo_board_title_red_double,
            R.color.lhc_banbo_board_title_red_multi_single,
            R.color.lhc_banbo_board_title_red_multi_double,

            R.color.lhc_banbo_board_title_green_big,
            R.color.lhc_banbo_board_title_green_small,
            R.color.lhc_banbo_board_title_green_single,
            R.color.lhc_banbo_board_title_green_double,
            R.color.lhc_banbo_board_title_green_multi_single,
            R.color.lhc_banbo_board_title_green_multi_double,

            R.color.lhc_banbo_board_title_blue_big,
            R.color.lhc_banbo_board_title_blue_small,
            R.color.lhc_banbo_board_title_blue_single,
            R.color.lhc_banbo_board_title_blue_double,
            R.color.lhc_banbo_board_title_blue_multi_single,
            R.color.lhc_banbo_board_title_blue_multi_double,

            R.color.lhc_weishu_0,
            R.color.lhc_weishu_1,
            R.color.lhc_weishu_2,
            R.color.lhc_weishu_3,
            R.color.lhc_weishu_4,
            R.color.lhc_weishu_5,
            R.color.lhc_weishu_6,
            R.color.lhc_weishu_7,
            R.color.lhc_weishu_8,
            R.color.lhc_weishu_9,
    };

    int bgRes[] = {
            R.drawable.bg_lhc_ball_red,
            R.drawable.bg_lhc_ball_red,
            R.drawable.bg_lhc_ball_red,
            R.drawable.bg_lhc_ball_red,
            R.drawable.bg_lhc_ball_red,
            R.drawable.bg_lhc_ball_red,

            R.drawable.bg_lhc_ball_green,
            R.drawable.bg_lhc_ball_green,
            R.drawable.bg_lhc_ball_green,
            R.drawable.bg_lhc_ball_green,
            R.drawable.bg_lhc_ball_green,
            R.drawable.bg_lhc_ball_green,

            R.drawable.bg_lhc_ball_blue,
            R.drawable.bg_lhc_ball_blue,
            R.drawable.bg_lhc_ball_blue,
            R.drawable.bg_lhc_ball_blue,
            R.drawable.bg_lhc_ball_blue,
            R.drawable.bg_lhc_ball_blue,

            R.drawable.bg_lhc_ball_red,
            R.drawable.bg_lhc_ball_red,
            R.drawable.bg_lhc_ball_red,
            R.drawable.bg_lhc_ball_red,
            R.drawable.bg_lhc_ball_red,
            R.drawable.bg_lhc_ball_red,
            R.drawable.bg_lhc_ball_red,
            R.drawable.bg_lhc_ball_red,
            R.drawable.bg_lhc_ball_red,
            R.drawable.bg_lhc_ball_red,
    };

    int txtRes[] = {
            R.color.bg_lhc_ball_text_red,
            R.color.bg_lhc_ball_text_red,
            R.color.bg_lhc_ball_text_red,
            R.color.bg_lhc_ball_text_red,
            R.color.bg_lhc_ball_text_red,
            R.color.bg_lhc_ball_text_red,

            R.color.bg_lhc_ball_text_green,
            R.color.bg_lhc_ball_text_green,
            R.color.bg_lhc_ball_text_green,
            R.color.bg_lhc_ball_text_green,
            R.color.bg_lhc_ball_text_green,
            R.color.bg_lhc_ball_text_green,

            R.color.bg_lhc_ball_text_blue,
            R.color.bg_lhc_ball_text_blue,
            R.color.bg_lhc_ball_text_blue,
            R.color.bg_lhc_ball_text_blue,
            R.color.bg_lhc_ball_text_blue,
            R.color.bg_lhc_ball_text_blue,

            R.color.bg_lhc_ball_text_red,
            R.color.bg_lhc_ball_text_red,
            R.color.bg_lhc_ball_text_red,
            R.color.bg_lhc_ball_text_red,
            R.color.bg_lhc_ball_text_red,
            R.color.bg_lhc_ball_text_red,
            R.color.bg_lhc_ball_text_red,
            R.color.bg_lhc_ball_text_red,
            R.color.bg_lhc_ball_text_red,
            R.color.bg_lhc_ball_text_red,
    };

    ColorStateList txtColor[] = new ColorStateList[txtRes.length];
    private int[] arrColor;
    public PlayBoardWeishuLHCItemView(Context context) {
        super(context);
        init();
    }

    public PlayBoardWeishuLHCItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayBoardWeishuLHCItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.view_lottery_play_board_weishu_lhc_item, null);
        tvTitle = view.findViewById(R.id.tv_title);
        tvOdds = view.findViewById(R.id.tv_odds);
        glGrid = view.findViewById(R.id.glDigits);
        lltitle = view.findViewById(R.id.ll_title);
        labelOdds = view.findViewById(R.id.tv_odds_label);
        addView(view);

        for (int i = 0; i < colors.length; i++) {
            colors[i] = getResources().getColor(colors[i]);
        }

        for (int i = 0; i < txtRes.length; i++) {
            txtColor[i] = getResources().getColorStateList(txtRes[i]);
        }
    }

    public void setUIConfig(LotteryPlayLHCUI.CellUIBean bean, LotteryPlayUserInfoLHC userInfoLHC) {
        tvTitle.setText(bean.getGname());
        tvTitle.setTextColor(txtColor[bean.getShowColors().get(0)]);
        tvOdds.setTextColor(txtColor[bean.getShowColors().get(0)]);
        labelOdds.setTextColor(txtColor[bean.getShowColors().get(0)]);
        lltitle.setBackgroundResource(R.drawable.shape_solid_white_border_1dp);

        if(userInfoLHC == null || userInfoLHC.getObj() == null || userInfoLHC.getObj().getLines() == null){
            tvOdds.setText(bean.getLhcIDReq());
        }else{
            String line = userInfoLHC.getObj().getLines().get(bean.getLhcIDReq());
            Pattern pattern = Pattern.compile("@[\\d\\.]+");
            Matcher matcher = null;
            if (!Strs.isEmpty(line)) {
                matcher = pattern.matcher(line);
            }
            String str = null;
            if(matcher != null && matcher.find()) {
                tvOdds.setText(matcher.group().replace("@", ""));
            } else {
                tvOdds.setText(bean.getLhcIDReq());
            }
        }

        arrColor = new int[bean.getShowColors().size()];
        labelNums = new ArrayList<>(bean.getShowNums().size());
        labelIV = new ArrayList<>(bean.getShowNums().size());
        for (int i = 0; i < bean.getShowNums().size(); i++) {
            arrColor[i] = bean.getShowColors().get(i);
            View view = View.inflate(getContext(), R.layout.item_lhc_play_banbo_num, null);
            TextView num = view.findViewById(R.id.tv_num);
            ImageView iv = view.findViewById(R.id.iv_bg);
            num.setText(bean.getShowNums().get(i));
            num.setTextColor(txtColor[arrColor[i]]);
            //Drawable drawable = new LHCZodiacNumCircleDrawable(getResources().getDrawable(R.mipmap.ic_bg_lhc_banbo_num), colors[bean.getShowColors().get(i)]);
            iv.setImageResource(bgRes[arrColor[i]]);
            labelNums.add(num);
            labelIV.add(iv);
            glGrid.addView(view);
        }
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        if (selected) {
            lltitle.setBackgroundColor(colors[arrColor[0]]);
        } else {
            lltitle.setBackgroundResource(R.drawable.shape_solid_white_border_1dp);
        }
    }
}
