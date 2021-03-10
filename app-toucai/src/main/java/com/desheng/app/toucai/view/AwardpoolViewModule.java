package com.desheng.app.toucai.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.util.Nums;
import com.ab.util.Strs;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.util.ConvertUtils;
import com.desheng.app.toucai.util.DateTool;
import com.desheng.base.manager.UserManager;
import com.shark.tc.R;

public class AwardpoolViewModule extends RelativeLayout {

    private View awardView;
    private TextView awardCount;
    private LinearLayout choujiang;
    private ScrollingDigitalAnimation awardPool;
    private TextView hourTime;
    private TextView dayTime;

    private double awardPoolData;
    private int hourTimeData;
    private int dayTimeData;
    private int awardCountData;
    private TextView mmTime;
    private TextView getCodeNow;
    Context mContext;
    private int awardPoolTakeType;
    private double mNextAmountSub;

    public AwardpoolViewModule(Context context) {
        this(context, null);
    }

    public AwardpoolViewModule(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AwardpoolViewModule(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        awardView = LayoutInflater.from(context).inflate(R.layout.layout_award_pool_module, this);
        initView();
    }

    private void initView() {
        awardCount = ((TextView) awardView.findViewById(R.id.awardCount));
        getCodeNow = ((TextView) awardView.findViewById(R.id.getCodeNow));
        choujiang = ((LinearLayout) awardView.findViewById(R.id.choujiang));
        awardPool = ((ScrollingDigitalAnimation) awardView.findViewById(R.id.awardPool));
        hourTime = ((TextView) awardView.findViewById(R.id.hourTime));
        dayTime = ((TextView) awardView.findViewById(R.id.dayTime));
        mmTime = ((TextView) awardView.findViewById(R.id.mmTime));


        setAwardCount(0);
    }

    public void setAwardCount(int awardCountd) {
        this.awardCountData = awardCountd;

        choujiang.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iAwardPoolChoujiang != null) {
                    if (awardCountData > 0) {
                        iAwardPoolChoujiang.onAwardPoolChoujiangClick();
                    } else {
                        iAwardPoolChoujiang.onAwardPoolNoCountClick(awardPoolTakeType,mNextAmountSub);
                    }
                }
            }
        });

        choujiang.setBackgroundResource(awardCountd > 0 ? R.mipmap.award_pool_button_data : R.mipmap.award_pool_button_no_data);
        if (awardCountd < 0) {
            awardCountd = 0;
        }
        awardCount.setText("(" + String.valueOf(awardCountd) + "次)");
        if (mContext == null) {
            return;
        }
        getCodeNow.setTextColor(awardCountd > 0 ? mContext.getResources().getColor(R.color.red) : mContext.getResources().getColor(R.color.text_AF3215));
        awardCount.setTextColor(awardCountd > 0 ? mContext.getResources().getColor(R.color.red) : mContext.getResources().getColor(R.color.text_AF3215));
    }

    public void setAwardPool(double awardPoold) {
        this.awardPoolData = awardPoold;
        //String oldStartNum = awardPool.getText().toString();
        String moneyShowFrom = UserManagerTouCai.getIns().getBonusPoolMoneyShowFrom();

        final String formatPoolMoney = Nums.formatDecimal(awardPoold, 2);
        Log.d("AwardpoolViewModule", "moneyShowFrom: " + moneyShowFrom + "-----awardPoold: " + formatPoolMoney);

        awardPool.postDelayed(new Runnable() {
            @Override
            public void run() {
                awardPool.setNumberString(moneyShowFrom, formatPoolMoney);
                //把上一次数据存在本地
                UserManagerTouCai.getIns().setBonusPoolMoneyShowFrom(formatPoolMoney);
            }
        }, 300);
    }

    public void setAwardPoolTakeType(int takeType) {
        this.awardPoolTakeType = takeType;
    }

    public void setNextAmountSub(double nextAmountSub) {
        this.mNextAmountSub = nextAmountSub;
    }

    public void setHourTime(int hourTimed) {
        this.hourTimeData = hourTimed;
    }

    public void setDayTime(int dayTimed) {
        this.dayTimeData = dayTimed;
        String[] toDDhh = ConvertUtils.longtimesToDDhh(dayTimed);
        dayTime.setText(toDDhh[0]);
        hourTime.setText(toDDhh[1]);
        mmTime.setText(toDDhh[2]);
    }

    public void setiAwardPoolChoujiang(IAwardPoolChoujiangListner iAwardPoolChoujiang) {
        this.iAwardPoolChoujiang = iAwardPoolChoujiang;
    }

    IAwardPoolChoujiangListner iAwardPoolChoujiang;

    public interface IAwardPoolChoujiangListner {
        void onAwardPoolChoujiangClick();

        void onAwardPoolNoCountClick(int taketype, double nextAmountSub);
    }
}
