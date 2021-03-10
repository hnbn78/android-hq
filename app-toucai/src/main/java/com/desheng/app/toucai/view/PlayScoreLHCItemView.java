package com.desheng.app.toucai.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.util.Strs;
import com.desheng.base.model.LotteryPlayLHCUI;
import com.desheng.base.model.LotteryPlayUserInfoLHC;
import com.shark.tc.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayScoreLHCItemView extends FrameLayout {
    private TextView tvInstruction;
    private TextView tvOdds;
    private ImageView tvZodiac;
    private View title;
//    private ImageView ivImage;

    private int[] titleRes = {
            R.drawable.ic_bg_lhc_score_da,
            R.drawable.ic_bg_lhc_score_da,
            R.drawable.ic_bg_lhc_score_xiao,
            R.drawable.ic_bg_lhc_score_dan,
            R.drawable.ic_bg_lhc_score_shuang,
            R.drawable.ic_bg_lhc_score_dadan,
            R.drawable.ic_bg_lhc_score_dashuang,
            R.drawable.ic_bg_lhc_score_xiaodan,
            R.drawable.ic_bg_lhc_score_xiaoshuang,

    };

    public PlayScoreLHCItemView(Context context) {
        super(context);
        init();
    }

    public PlayScoreLHCItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayScoreLHCItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.view_lottery_play_score_lhc_item, null);
        tvInstruction = view.findViewById(R.id.tv_instruction);
        tvOdds = view.findViewById(R.id.tv_odds);
        //labelOdds = view.findViewById(R.id.tv_label_odds);
        title = view.findViewById(R.id.ll_title);
        tvZodiac = view.findViewById(R.id.iv_zodic);
//        ivImage = view.findViewById(R.id.iv_bg);
        addView(view);
    }

    public void setUIConfig(LotteryPlayLHCUI.CellUIBean bean, LotteryPlayUserInfoLHC userInfoLHC) {
        tvInstruction.setText(bean.getShowNums().get(0));
        title.setBackgroundResource(R.drawable.shape_solid_white_border_1dp);
        tvOdds.setTextColor(getResources().getColorStateList(R.color.bg_lhc_ball_text_red));
        //labelOdds.setTextColor(getResources().getColorStateList(R.color.bg_lhc_ball_text_red));

        if (userInfoLHC == null || userInfoLHC.getObj() == null || userInfoLHC.getObj().getLines() == null) {
            tvOdds.setText(bean.getLhcIDReq());
        } else {
            String line = userInfoLHC.getObj().getLines().get(bean.getLhcIDReq());
            Pattern pattern = Pattern.compile("@[\\d\\.]+");
            Matcher matcher = null;
            if (!Strs.isEmpty(line)) {
                matcher = pattern.matcher(line);
            }
            String str = null;
            if (matcher != null && matcher.find()) {
                tvOdds.setText(matcher.group().replace("@", ""));
            } else {
                tvOdds.setText(bean.getLhcIDReq());
            }
        }

        tvZodiac.setImageResource(titleRes[bean.getShowColors().get(0)]);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        if (selected) {
            title.setBackgroundResource(R.color.lhc_ball_red);
        } else {
            title.setBackgroundResource(R.drawable.shape_solid_white_border_1dp);
        }
    }
}
