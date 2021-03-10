package com.desheng.app.toucai.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ab.global.AbDevice;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LotteryPlay;
import com.desheng.base.model.LotteryPlayUserInfo;
import com.shark.tc.R;

import java.util.HashMap;

/**
 * 玩法脚
 * Created by lee on 2018/3/13.
 */
public class PlayFootView extends FrameLayout {
    private TextView labBonus;
    private SeekBar pbProgress;
    private TextView tvBonusLab;
    private ImageView tvMinus;
    private TextView tvBonus;
    private ImageView tvPlus;
    private EditText etNumTimes;
    private TextView tvModel;

    private LotteryPlayUserInfo playInfo;
    public PlaySnakeView snakeView;

    private LotteryPlayUserInfo.MathodBean method;
    double unitMoney = 1;
    double currRevPercentInSysPoint;
    double currPercentInSysPoint;
    private int currPoint;
    private int modelPos = 3;
    private boolean isNewLongHu;
    private boolean isOldLongHu;
    //private List<Double> bonus = new ArrayList<>();
    private HashMap<String, Double> bonusMap = new HashMap<>();
    private RadioGroup unitRgroup;


    public PlayFootView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public PlayFootView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlayFootView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        LinearLayout inner = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.view_lottery_play_foot, this, false);
        labBonus = inner.findViewById(R.id.labBonus);
        pbProgress = inner.findViewById(R.id.pbProgress);
        tvBonus = inner.findViewById(R.id.tvBonus);
        tvMinus = inner.findViewById(R.id.tvMinus);
        etNumTimes = inner.findViewById(R.id.etMulti);
        tvPlus = inner.findViewById(R.id.tvPlus);
        tvModel = inner.findViewById(R.id.tv_model);
        unitRgroup = inner.findViewById(R.id.unitRgroup);
        this.addView(inner);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                etNumTimes.clearFocus();
            }
        });

        setMoneyMode();

        tvModel.setOnClickListener(v -> {
            tvModel.setActivated(true);
            showDropUp();
        });
        etNumTimes.setSelection(1);

        unitRgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.Rbyuan:
                        modelPos = 3;
                        break;
                    case R.id.Rbjiao:
                        modelPos = 2;
                        break;
                    case R.id.Rbfen:
                        modelPos = 1;
                        break;
                    case R.id.Rbli:
                        modelPos = 0;
                        break;
                    default:
                }
                updateUnitMoney();
                if (isNewLongHu) {
                    snakeView.setNewLHBonusUnit(unitMoney);
                } else if (isOldLongHu) {
                    snakeView.setBonusUnitOldLH(unitMoney);
                } else {
                    snakeView.setBonusUnit(unitMoney);
                }
                UserManager.getIns().setMoneyMode(modelPos);
            }
        });
    }

    public void reset() {
        //String[] arr = getContext().getResources().getStringArray(R.array.model_entries);
        //etNumTimes.setText("1");
        etNumTimes.setSelection(1);
        modelPos = UserManager.getIns().getMoneyMode();
//        tvModel.setText(arr[modelPos]);
        setMoneyMode();
        updateUnitMoney();
        snakeView.setBonusUnit(unitMoney);
    }

    public void setSnakeView(PlaySnakeView snakeView) {
        this.snakeView = snakeView;
    }

    public void setLotteryInfo(LotteryPlay play, LotteryPlayUserInfo info) {
        this.playInfo = info;
        if (play != null) {
            isNewLongHu = play.lotteryCode.contains("xinlonghu");
            isOldLongHu = play.lotteryCode.contains("allInOne_longhu");
            this.method = playInfo.getMethod().get(play.getPlayId());
        }

        //倍数
        etNumTimes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Strs.parse(s.toString(), 0) <= 0) {
                    s.replace(0, s.length(), "");
                } else if (Strs.parse(s.toString(), 0) > 99999) {
                    s.replace(0, s.length(), "99999");
                }
                snakeView.setMoneyTimes(Strs.parse(s.toString(), 0), isNewLongHu);
                UserManager.getIns().setHintTimes(Strs.parse(s.toString(), 0));
            }
        });

        tvMinus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int time = Strs.parse(etNumTimes.getText().toString(), 0);
                time--;
                if (time <= 1) {
                    time = 1;
                }
                etNumTimes.setText(String.valueOf(time));
                UserManager.getIns().setHintTimes(time);
            }
        });
        tvPlus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int time = Strs.parse(etNumTimes.getText().toString(), 0);
                time++;
                if (time > 99999) {
                    time = 99999;
                }
                etNumTimes.setText(String.valueOf(time));
                UserManager.getIns().setHintTimes(time);
            }
        });

        //100倍progress
        if (playInfo == null) {
            return;
        }
        final double sysPoint = playInfo.getConfig().getSysPoint();
        currPercentInSysPoint = 0;
        currRevPercentInSysPoint = sysPoint;
        currPoint = getCurrentPoint();
        tvBonus.setText(Math.round(getCurrentPoint()) + "/" + Nums.formatDecimal(currRevPercentInSysPoint, 1) + "%");
        Log.d("PlayFootView", "sysPoint:" + sysPoint);
        pbProgress.setMax((int) (sysPoint * 100));
        pbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //当前的正向进度
                String currPercentStr = Nums.formatDecimal(progress * 1.0 / (sysPoint * 100.0) * sysPoint, 1);

                currPercentInSysPoint = Double.parseDouble(currPercentStr);
                currRevPercentInSysPoint = sysPoint - currPercentInSysPoint;
                //最小数加进度值
                currPoint = getCurrentPoint();
                tvBonus.setText(currPoint + "/" + Nums.formatDecimal(currRevPercentInSysPoint, 1) + "%");
                //UserManager.getIns().setGameAward(progress);
                Log.d("PlayFootView", "progress:  " + progress);
                if (isNewLongHu) {
                    setBonusList(play.lotteryCode, info);
                    snakeView.setBonus(bonusMap);
                } else if (isOldLongHu) {
                    double bonusMin = 2.22;
                    double bonusMax = 9.98;
                    LotteryPlayUserInfo.MathodBean methodBean1 = info.getMethod().get("lhwq");
                    if (methodBean1 != null) {
                        bonusMax = getBonus(methodBean1);
                        bonusMin = bonusMax * 4 / 18;
                    }
                    snakeView.setBonus(bonusMin, bonusMax);
                } else {
                    double bonus = getBonusWithUnit();
                    snakeView.setBonus(bonus);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (sysPoint == 0) {
                    Toasts.show(getContext(), "当前用户返点为0, 不能调节!", false);
                    return;
                }
                int progress = seekBar.getProgress();
                Log.e("PlayFootView", "progress:" + progress);
                UserManager.getIns().setBounsProgress(progress);
            }
        });

        int bounsProgress = UserManager.getIns().getBounsProgress();
        if (bounsProgress >= 0) {
            pbProgress.setProgress(bounsProgress);
            //当前的正向进度
            String currPercentStr = Nums.formatDecimal(bounsProgress * 1.0 / (sysPoint * 100.0) * sysPoint, 1);

            currPercentInSysPoint = Double.parseDouble(currPercentStr);
            currRevPercentInSysPoint = sysPoint - currPercentInSysPoint;
            //最小数加进度值
            currPoint = getCurrentPoint();
            tvBonus.setText(currPoint + "/" + Nums.formatDecimal(currRevPercentInSysPoint, 1) + "%");
            //UserManager.getIns().setGameAward(progress);
            Log.d("PlayFootView", "progress:  " + bounsProgress);
            if (isNewLongHu) {
                setBonusList(play.lotteryCode, info);
                snakeView.setBonus(bonusMap);
            } else if (isOldLongHu) {
                double bonusMin = 2.22;
                double bonusMax = 9.98;
                LotteryPlayUserInfo.MathodBean methodBean1 = info.getMethod().get("lhwq");
                if (methodBean1 != null) {
                    bonusMax = getBonus(methodBean1);
                    bonusMin = bonusMax * 4 / 18;
                }
                snakeView.setBonus(bonusMin, bonusMax);
            } else {
                double bonus = getBonusWithUnit();
                snakeView.setBonus(bonus);
            }
        } else {
            pbProgress.setProgress((int) (sysPoint * 100));
            //初始化
            //当前的正向进度
            String currPercentStr = Nums.formatDecimal(sysPoint, 1);
            currPercentInSysPoint = Double.parseDouble(currPercentStr);
            currRevPercentInSysPoint = sysPoint - currPercentInSysPoint;
            //最小数加进度值
            currPoint = getCurrentPoint();
            tvBonus.setText(currPoint + "/" + Nums.formatDecimal(currRevPercentInSysPoint, 1) + "%");
            //UserManager.getIns().setGameAward(progress);
            double initbonus = getBonusWithUnit();
            snakeView.setBonus(initbonus);
        }

        int gameId = info.getGameId();
        switch (gameId) {
            case 200:
            case 201:
            case 202:
                if ("lhwq".equals(play.lotteryCode)) {
                    snakeView.setBonus(2.217, getBonus());
                } else {
                    snakeView.setBonus(getBonus());
                }
                break;
            default:
                if ("lhwq".equals(play.lotteryCode)) {
                    isOldLongHu = true;
                    snakeView.setBonus(2.18, getBonus());
                } else if (play.lotteryCode.contains("gflonghuhe")) {
                    isNewLongHu = true;
                } else {
                    snakeView.setBonus(getBonus());
                }
                break;
        }

        snakeView.setHitCount(0);

        if (UserManager.getIns().getGameAward() >= 0) {
            Log.d("PlayFootView", "getGameAward():" + UserManager.getIns().getGameAward());
            //pbProgress.setProgress(UserManager.getIns().getGameAward());
        }

        modelPos = UserManager.getIns().getMoneyMode();
        updateUnitMoney();
        if (snakeView != null) {
            snakeView.setBonusUnit(unitMoney);
        }

        if (isNewLongHu) {
            setBonusList(play.lotteryCode, info);
            snakeView.setBonus(bonusMap);
        } else if (isOldLongHu) {
            double bonusMin = 2.22;
            double bonusMax = 9.98;
            LotteryPlayUserInfo.MathodBean methodBean1 = info.getMethod().get("lhwq");
            if (methodBean1 != null) {
                bonusMax = getBonus(methodBean1);
                bonusMin = bonusMax * 4 / 18;
            }
            snakeView.setBonus(bonusMin, bonusMax);
        } else {
            snakeView.setBonus(getBonus(method));
        }

        int hintTimes = UserManager.getIns().getHintTimes();
        etNumTimes.setText(String.valueOf(hintTimes));
        if (snakeView != null) {
            snakeView.setMoneyTimes(hintTimes, isNewLongHu);
        }
    }

    public void refreshBonus() {
        //snakeView.setBonus(getBonus());
        if (playInfo == null) {
            return;
        }
        final double sysPoint = playInfo.getConfig().getSysPoint();
        //初始化
        //当前的正向进度
        String currPercentStr = Nums.formatDecimal(sysPoint, 1);
        currPercentInSysPoint = Double.parseDouble(currPercentStr);
        currRevPercentInSysPoint = sysPoint - currPercentInSysPoint;
        //最小数加进度值
        currPoint = getCurrentPoint();
        tvBonus.setText(currPoint + "/" + Nums.formatDecimal(currRevPercentInSysPoint, 1) + "%");
        //UserManager.getIns().setGameAward(progress);
        double initbonus = getBonusWithUnit();
        snakeView.setBonus(initbonus);

    }

    public String getModel() {
        String model = "yuan";
        switch (modelPos) {
            case 0:
                model = "li";
                break;
            case 1:
                model = "fen";
                break;
            case 2:
                model = "jiao";
                break;
            case 3:
                model = "yuan";
                break;
        }
        return model;
    }

    private void setMoneyMode() {
        int moneyMode = UserManager.getIns().getMoneyMode();
        switch (moneyMode) {
            case 0:
                unitRgroup.check(R.id.Rbli);
                tvModel.setText("厘");
                break;
            case 1:
                unitRgroup.check(R.id.Rbfen);
                tvModel.setText("分");
                break;
            case 2:
                unitRgroup.check(R.id.Rbjiao);
                tvModel.setText("角");
                break;
            case 3:
                unitRgroup.check(R.id.Rbyuan);
                tvModel.setText("元");
                break;
            default:
        }
    }

    private void setBonusList(String lotteryCode, LotteryPlayUserInfo userPlayInfo) {
        /*判断是否有新龙虎的内容*/
        LotteryPlayUserInfo.MathodBean methodBean1 = userPlayInfo.getMethod().get("1v5gflonghuhe_long");
        if (methodBean1 != null) {
            bonusMap.put("0", getBonus(methodBean1));
        }

        LotteryPlayUserInfo.MathodBean methodBean3 = userPlayInfo.getMethod().get("1v5gflonghuhe_he");
        if (methodBean3 != null) {
            bonusMap.put("1", getBonus(methodBean3));
        }

        LotteryPlayUserInfo.MathodBean methodBean2 = userPlayInfo.getMethod().get("1v5gflonghuhe_hu");
        if (methodBean2 != null) {
            bonusMap.put("2", getBonus(methodBean2));
        }
    }

    public double getBonusWithUnit() {
        return getBonus(method);
    }

//    public List<Double> getBonusWithUnitNewLH() {
//        List<Double> doubles = new ArrayList<>();
//        for (Double aDouble : bonus) {
//            doubles.add(getBonus(method) * unitMoney);
//        }
//        return doubles;
//    }

    private double getBonus() {
        if (method == null) {
            return 0;
        }
        if (playInfo == null) {
            return 0;
        }
        LotteryPlayUserInfo.ConfigBean config = playInfo.getConfig();
        //double sysPoint = playInfo.getConfig().getSysCode();
        double sysCode = config.getSysCode();
        double maxBetCode = config.getMaxBetCode();
        //double userMinCode = sysPoint - UserManagerTouCai.getIns().getLotteryPoint() * 20;
        //double percent = Nums.parse(this.method.getBonus(), 0.0) / userMinCode;
        //double currentPoint = getCurrentPoint();
        //double bonusPoint = (currentPoint * percent) * (config.getSysUnitMoney() / 2.0);
        return (Nums.parse(this.method.getBonus(), 0.0) / maxBetCode) * sysCode * (config.getSysUnitMoney() / 2.0);
    }

    private double getBonus(LotteryPlayUserInfo.MathodBean method) {
        if (method == null) {
            return 0;
        }
        if (playInfo == null) {
            return 0;
        }
        LotteryPlayUserInfo.ConfigBean config = playInfo.getConfig();
        double bonus = Nums.parse(method.getBonus(), 0.0);//19
        double x = Nums.parse(method.getX(), 0.0);//20
        double sysUnitMoney = config.getSysUnitMoney() / 2.0;
        int currentPoint = getCurrentPoint();
        int minBetCode = config.getMinBetCode();
        double result = (bonus + ((currentPoint - minBetCode) / 2000.0) * x) * sysUnitMoney;
        Log.e("PlayFootView", "result-bonus: " + result);
        return result;
    }

    /**
     * x
     * $UserMinCode = $sysCode - $sysPoint * 20;
     * var $percent = $bonus / $UserMinCode;
     * var $money = ($UserCode * $percent) * ($sysUnitMoney / 2);
     *
     * @return
     */
//    public int getCurrentPoint() {
//        if (playInfo == null) {
//            return 0;
//        }
//        LotteryPlayUserInfo.ConfigBean config = playInfo.getConfig();
//        double sysPoint = playInfo.getConfig().getSysPoint();
//        int res = (int) Math.round(config.getMaxBetCode() + (currPercentInSysPoint / sysPoint) * (config.getMaxBetCode() - config.getMinBetCode()));
//        if (res == 0) {
//            res = config.getMaxBetCode();
//        }
//        return res;
//    }

    private double sysPointBase = 0;

    public int getCurrentPoint() {
        if (playInfo == null) {
            return 0;
        }
        LotteryPlayUserInfo.ConfigBean config = playInfo.getConfig();
        double sysPoint = config.getSysPoint();
        int res = (int) Math.round(config.getMinBetCode() + (currPercentInSysPoint / (sysPoint - sysPointBase))
                * (config.getMaxBetCode() - config.getMinBetCode()));
        if (res == 0) {
            res = config.getMinBetCode();
        }
        return res;
    }

    public int getNumTimes() {
        return Views.getValue(etNumTimes, 1);
    }

    private void showDropUp() {
        View pop = LayoutInflater.from(getContext()).inflate(R.layout.view_popup_lottery_cart_model, null);
        ViewGroup container = pop.findViewById(R.id.layout_container);
        PopupWindow modelPopUp = new PopupWindow(pop);
        modelPopUp.setOutsideTouchable(true);
        modelPopUp.setFocusable(true);
        modelPopUp.setFocusable(true);//这里必须设置为true才能点击区域外或者消失
        modelPopUp.setTouchable(true);//这个控制PopupWindow内部控件的点击事件
        modelPopUp.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modelPopUp.setOutsideTouchable(true);
        modelPopUp.update();
        modelPopUp.setContentView(pop);
        modelPopUp.setOnDismissListener(() -> {
            tvModel.setActivated(false);
        });
        String[] arr = getContext().getResources().getStringArray(R.array.model_entries);


        for (int i = 0; i < arr.length; i++) {
            final int finali = i;
            TextView child = (TextView) LayoutInflater.from(container.getContext()).inflate(R.layout.view_popup_lottery_cart_model_item, container, false);
            container.addView(child);
            child.setText(arr[finali]);

            if (finali == modelPos)
                child.setTextColor(0xfffd6e6e);
            else
                child.setTextColor(0xff333333);

            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    modelPopUp.dismiss();
                    modelPos = finali;
                    updateUnitMoney();
                    if (isNewLongHu) {
                        snakeView.setNewLHBonusUnit(unitMoney);
                    } else {
                        snakeView.setBonusUnit(unitMoney);
                    }
                    tvModel.setText(arr[finali]);
                    UserManager.getIns().setMoneyMode(finali);
                }
            });
        }
        pop.measure(MeasureSpec.makeMeasureSpec(AbDevice.SCREEN_WIDTH_PX, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(AbDevice.SCREEN_HEIGHT_PX, MeasureSpec.AT_MOST));
        modelPopUp.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        modelPopUp.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        modelPopUp.showAsDropDown(tvModel, 0, -pop.getMeasuredHeight() - tvModel.getMeasuredHeight());
    }

    private void updateUnitMoney() {
        switch (modelPos) {
            case 0:
                unitMoney = 0.001;
                break;
            case 1:
                unitMoney = 0.01;
                break;
            case 2:
                unitMoney = 0.1;
                break;
            case 3:
                unitMoney = 1;
                break;
        }
    }
}
