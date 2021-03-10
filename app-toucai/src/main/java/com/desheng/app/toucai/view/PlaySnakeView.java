package com.desheng.app.toucai.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.util.Nums;
import com.desheng.base.manager.UserManager;
import com.desheng.base.util.CoinHelper;
import com.shark.tc.R;

import java.util.HashMap;
import java.util.List;

/**
 * 玩法脚
 * Created by lee on 2018/3/13.
 */
public class PlaySnakeView extends FrameLayout {
    private LinearLayout vgSnake;
    private TextView tvAmount;
    private TextView tvBoundy;
    private long hitCount = 0;
    private int moneyTimes = 1;
    private double bonus = 0.0;
    private double bonusUnit = 1.0;
    private double mBonusMax = 0.0;
    private double mBonusMin = 0.0;
    private HashMap<String, Double> doubleList;
    private TextView tvAccountYue;

    public PlaySnakeView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public PlaySnakeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlaySnakeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    protected void init(Context context) {
        vgSnake = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.view_lottery_play_snake, this, false);
        tvAmount = (TextView) vgSnake.findViewById(R.id.tvAmount);
        tvBoundy = (TextView) vgSnake.findViewById(R.id.tvBoundy);
        tvAccountYue = (TextView) vgSnake.findViewById(R.id.tvAccountYue);
        String money = String.format("共<font color=#ff2b65>%d</font>注 ￥<font color=#ff2b65>%s</font>", 0, 0.000, 3);
        tvAmount.setText(Html.fromHtml(money));
        this.addView(vgSnake);
    }

    public void setAccountAmount(String accountAmount) {
        tvAccountYue.setText(Html.fromHtml(accountAmount));
    }

    public void setHitCount(long hitCount) {
        this.hitCount = hitCount;
        this.moneyTimes = UserManager.getIns().getHintTimes();
        String money = String.format("共<font color=#ff2b65>%d</font>注 ￥<font color=#ff2b65>%s</font>", hitCount, Nums.formatDecimal(hitCount * moneyTimes * bonusUnit, 3));
        tvAmount.setText(Html.fromHtml(money));
    }

    public void setMoneyTimes(int moneyTimes, boolean isNewLongHu) {
        this.moneyTimes = moneyTimes;
        String money = String.format("共<font color=#ff2b65>%d</font>注 ￥<font color=#ff2b65>%s</font>",
                hitCount, Nums.formatDecimal(hitCount * moneyTimes * bonusUnit, 3));
        tvAmount.setText(Html.fromHtml(money));
        if (isNewLongHu) {
            setBonus(doubleList);
        } else {
            if (mBonusMax != 0.0 && mBonusMin != 0.0) {
                setBonus(mBonusMin, mBonusMax);
            } else {
                setBonus(bonus);
            }
        }
    }

    public double getMoneyTotal() {
        return hitCount * moneyTimes * bonusUnit * 1.0;
    }

    public long getHitCount() {
        return hitCount;
    }

    public double getRewards() {
        return bonus * bonusUnit * 1.0;
    }

    private String moneyText = "共计：<font color=#ff2b65>%d</font>注\t\t<font color=#ff2b65>%s</font>"
            + CoinHelper.getUnitWithId(CoinHelper.getInstance().currentCurrency);

    public void setBonus(HashMap<String, Double> bonus) {
        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
        }

        if (bonus == null) {
            return;
        }

        this.doubleList = bonus;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bonus.size(); i++) {
            String arg = Nums.formatDecimal(bonus.get(String.valueOf(i)) * bonusUnit * moneyTimes, 2);
            sb.append(arg);
            if (i < bonus.size() - 1)
                sb.append("/");
        }
        String bonusStr = "<font color=#ff0000>%s</font>" + CoinHelper.getUnitWithId(CoinHelper.getInstance().currentCurrency);
        String format = String.format(bonusStr, sb.toString());
        tvBoundy.setText(Html.fromHtml(format));
    }

    public void setBonus(double bonus) {
        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
        }
        this.bonus = bonus;
        mBonusMin = 0.0;
        mBonusMax = 0.0;
        String format = String.format("<font color=#ff3e3d>%s</font>", Nums.formatDecimal(bonus * bonusUnit * moneyTimes, 3));
        tvBoundy.setText(Html.fromHtml(format));
    }

    public void setBonusForLH(double bonus, boolean ismax) {
        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
        }
        this.bonus = bonus;
        mBonusMin = ismax ? 0.0 : bonus;
        mBonusMax = ismax ? bonus : 0.0;
        String format = String.format("<font color=#ff3e3d>%s</font>", Nums.formatDecimal(bonus * bonusUnit * moneyTimes, 2));
        tvBoundy.setText(Html.fromHtml(format));
    }

    public void setBonus(double bonusMin, double bonusMax) {
        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
        }
        this.bonus = bonusMin;
        this.mBonusMin = bonusMin;
        this.mBonusMax = bonusMax;
        String format = String.format("<font color=#ff3e3d>%s</font>", Nums.formatDecimal(bonusMin * bonusUnit * moneyTimes, 2));
        String format2 = String.format("<font color=#ff3e3d>%s</font>", Nums.formatDecimal(bonusMax * bonusUnit * moneyTimes, 2));
        tvBoundy.setText(Html.fromHtml(format + "-" + format2));
    }

    //龙虎和 都选择到了
    public int showLHH() {
        if (mBonusMax != 0.0 && mBonusMin != 0.0) {
            return 3;
        }
        if (mBonusMax != 0.0 && mBonusMin == 0.0) {
            return 2;
        }

        if (mBonusMax == 0.0 && mBonusMin != 0.0) {
            return 1;
        }
        return -1;
    }

    public void setBonusUnit(double bonusUnit) {
        this.bonusUnit = bonusUnit;
        String format = String.format("<font color=#ff3e3d>%s</font>", Nums.formatDecimal(bonus * bonusUnit * moneyTimes, 3));
        if (mBonusMax != 0.0 && mBonusMin != 0.0) {
            format = String.format("<font color=#ff3e3d>%s</font>", Nums.formatDecimal(mBonusMin * bonusUnit * moneyTimes, 3));
            String formatMax = String.format("<font color=#ff3e3d>%s</font>", Nums.formatDecimal(mBonusMax * bonusUnit * moneyTimes, 3));
            tvBoundy.setText(Html.fromHtml(format + "-" + formatMax));
        } else {
            tvBoundy.setText(Html.fromHtml(format));
        }
        String money = String.format("共<font color=#ff2b65>%d</font>注 ￥<font color=#ff2b65>%s</font>", hitCount, Nums.formatDecimal(hitCount * moneyTimes * bonusUnit, 3));
        tvAmount.setText(Html.fromHtml(money));
    }

    public void setBonusUnitOldLH(double bonusUnit) {
        this.bonusUnit = bonusUnit;
        String format = String.format("<font color=#ff3e3d>%s</font>", Nums.formatDecimal(bonus * bonusUnit * moneyTimes, 2));
        if (mBonusMax != 0.0 && mBonusMin != 0.0) {
            format = String.format("<font color=#ff3e3d>%s</font>", Nums.formatDecimal(mBonusMin * bonusUnit * moneyTimes, 2));
            String formatMax = String.format("<font color=#ff3e3d>%s</font>", Nums.formatDecimal(mBonusMax * bonusUnit * moneyTimes, 2));
            tvBoundy.setText(Html.fromHtml(format + "-" + formatMax));
        } else {
            tvBoundy.setText(Html.fromHtml(format));
        }
        String money = String.format("共<font color=#ff2b65>%d</font>注 ￥<font color=#ff2b65>%s</font>", hitCount, Nums.formatDecimal(hitCount * moneyTimes * bonusUnit, 3));
        tvAmount.setText(Html.fromHtml(money));
    }

    /**
     * @param bonusUnit 单位
     *                  新龙虎 奖金算法
     */
    public void setNewLHBonusUnit(double bonusUnit) {
        this.bonusUnit = bonusUnit;
        if (doubleList == null) return;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < doubleList.size(); i++) {
            String arg = Nums.formatDecimal(doubleList.get(String.valueOf(i)) * bonusUnit * moneyTimes, 2);
            sb.append(arg);
            if (i < doubleList.size() - 1)
                sb.append("/");
        }
        String bonus = "<font color=#ff0000>%s</font>" + CoinHelper.getUnitWithId(CoinHelper.getInstance().currentCurrency);
        String format = String.format(bonus, sb.toString());
        tvBoundy.setText(Html.fromHtml(format));
        String money = String.format(moneyText, hitCount, Nums.formatDecimal(hitCount * moneyTimes * bonusUnit, 3));
        tvAmount.setText(Html.fromHtml(money));
    }

    public void clearAll() {
        tvBoundy.setText("");
        tvAmount.setText("");
        hitCount = 0;
        //moneyTimes = 1;
        bonus = 0.0;
        mBonusMax = 0.0;
        bonusUnit = 1.0;
    }
}
