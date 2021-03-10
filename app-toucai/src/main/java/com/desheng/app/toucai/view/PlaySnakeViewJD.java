package com.desheng.app.toucai.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ab.util.Nums;
import com.shark.tc.R;

/**
 * 玩法脚
 * Created by lee on 2018/3/13.
 */
public class PlaySnakeViewJD extends FrameLayout {
    private ViewGroup vgSnake;
    private TextView tvAmount;
    private TextView tvBoundy;
    private long hitCount = 0;
    private double money = 1.0d;
    private double bonus = 0.0;
    private double bonusUnit = 1.0;

    public PlaySnakeViewJD(@NonNull Context context) {
        super(context);
        init(context);
    }

    public PlaySnakeViewJD(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlaySnakeViewJD(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    protected void init(Context context) {
        vgSnake = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.view_lottery_play_snake_lhc, this, false);
        tvAmount = (TextView)vgSnake.findViewById(R.id.tvAmount);
        tvBoundy = (TextView)vgSnake.findViewById(R.id.tvBoundy);
    
        this.addView(vgSnake);
    }

    public void setHitCount(long hitCount){
        this.hitCount = hitCount;
        String money = String.format("共计<font color=#F94F79>%d</font>注, 总计<font color=#F94F79>%s</font>元", hitCount, Nums.formatDecimal(hitCount * this.money * bonusUnit, 3));
        tvAmount.setText(Html.fromHtml(money));
    }

    public void setMoney(double m){
        this.money = m;

        if (money <= 0) {
            money = 1;
        }

        String money = String.format("共计<font color=#F94F79>%d</font>注, 总计<font color=#F94F79>%s</font>元", hitCount, Nums.formatDecimal(hitCount * this.money * bonusUnit, 3));
        tvAmount.setText(Html.fromHtml(money));
    }

    public double getMoneyTotal(){
        return hitCount * money * bonusUnit * 1.0;
    }

    public long  getHitCount(){
        return hitCount;
    }
    public double getRewards() {
        return bonus * bonusUnit * 1.0;
    }

//    public void setBonus(double bonus){
//        if(getVisibility() != View.VISIBLE){
//            setVisibility(View.VISIBLE);
//        }
//        this.bonus = bonus;
//        String format = String.format("玩法奖金<font color=#F94F79>%s</font>元", Nums.formatDecimal(bonus * bonusUnit, 3));
//        tvBoundy.setText(Html.fromHtml(format));
//    }
//    public void setBonusUnit(double bonusUnit){
//        this.bonusUnit = bonusUnit;
//        String format = String.format("玩法奖金<font color=#F94F79>%s</font>元", Nums.formatDecimal(bonus * bonusUnit, 3));
//        tvBoundy.setText(Html.fromHtml(format));
//        String money = String.format("共<font color=#F94F79>%d</font>注, 共计<font color=#F94F79>%s</font>元", hitCount, Nums.formatDecimal(hitCount * money * bonusUnit, 3));
//        tvAmount.setText(Html.fromHtml(money));
//    }

    public void clearAll(){
        tvBoundy.setText("");
        tvAmount.setText("");
        hitCount = 0;
        money = 0;
        bonus = 0.0;
        bonusUnit = 1.0;
    }
}
