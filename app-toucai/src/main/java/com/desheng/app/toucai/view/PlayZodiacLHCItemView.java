package com.desheng.app.toucai.view;

import android.content.Context;
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

public class PlayZodiacLHCItemView extends FrameLayout {
    private TextView tvTitle;
    private TextView tvOdds;
    private ImageView ivZodiac;
    private GridLayout glGrid;

    private List<TextView> labelNums;

    private int[] bgRes = {
            R.mipmap.bg_lhc_zodiac_header_sxshu, // unused index
            R.mipmap.bg_lhc_zodiac_header_sxshu,
            R.mipmap.bg_lhc_zodiac_header_sxniu,
            R.mipmap.bg_lhc_zodiac_header_sxhu,
            R.mipmap.bg_lhc_zodiac_header_sxtu,
            R.mipmap.bg_lhc_zodiac_header_sxlong,
            R.mipmap.bg_lhc_zodiac_header_sxshe,
            R.mipmap.bg_lhc_zodiac_header_sxma,
            R.mipmap.bg_lhc_zodiac_header_sxyang,
            R.mipmap.bg_lhc_zodiac_header_sxhou,
            R.mipmap.bg_lhc_zodiac_header_sxji,
            R.mipmap.bg_lhc_zodiac_header_sxgou,
            R.mipmap.bg_lhc_zodiac_header_sxzhu,
    };
    
//    private int[] iconRes = {
//            R.mipmap.bg_lhc_zodiac_ball_round1, // unused index
//            R.mipmap.bg_lhc_zodiac_ball_round1,
//            R.mipmap.bg_lhc_zodiac_ball_round2,
//            R.mipmap.bg_lhc_zodiac_ball_round3,
//            R.mipmap.bg_lhc_zodiac_ball_round4,
//            R.mipmap.bg_lhc_zodiac_ball_round5,
//            R.mipmap.bg_lhc_zodiac_ball_round6,
//            R.mipmap.bg_lhc_zodiac_ball_round7,
//            R.mipmap.bg_lhc_zodiac_ball_round8,
//            R.mipmap.bg_lhc_zodiac_ball_round9,
//            R.mipmap.bg_lhc_zodiac_ball_round10,
//            R.mipmap.bg_lhc_zodiac_ball_round11,
//            R.mipmap.bg_lhc_zodiac_ball_round12,
//    };

    public PlayZodiacLHCItemView(Context context) {
        super(context);
        init();
    }

    public PlayZodiacLHCItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayZodiacLHCItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.view_lottery_play_zodic_lhc_item, null);
        //llBg = view.findViewById(R.id.ll_bg);
        //llRightBg = view.findViewById(R.id.fl_right_bg);
        tvOdds = view.findViewById(R.id.tv_odds);
        tvTitle = view.findViewById(R.id.tv_title);
        glGrid = view.findViewById(R.id.glDigits);
        //labelOdds = view.findViewById(R.id.tv_odds_label);
        ivZodiac = view.findViewById(R.id.iv_zodiac_header);
        addView(view);
    }

    public void setUIConfig(LotteryPlayLHCUI.CellUIBean bean, LotteryPlayUserInfoLHC userInfoLHC) {
        ivZodiac.setImageResource(bgRes[bean.getShowColors().get(0)]);
        tvTitle.setText(bean.getGname());

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

        labelNums = new ArrayList<>(bean.getShowNums().size());
        for (int i = 0; i < bean.getShowNums().size(); i++) {
            View view = View.inflate(getContext(), R.layout.item_lhc_play_zodiac_num, null);
            TextView num = view.findViewById(R.id.tv_num);
            ImageView iv = view.findViewById(R.id.iv_bg);
            num.setText(bean.getShowNums().get(i));
            labelNums.add(num);
            glGrid.addView(view);
        }
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
    }
}
