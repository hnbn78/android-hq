package com.desheng.app.toucai.function;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BaseInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.RedPacketGetDetail;
import com.desheng.app.toucai.panel.ActDeposit;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.util.ScreenUtils;
import com.desheng.app.toucai.view.RedPacketViewgroup;
import com.google.gson.reflect.TypeToken;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * 独立的功能模块
 * 功能：红包雨功能
 */
public class RedPacketMode implements RedPacketViewgroup.IPacketOpened {

    private Activity mActivity;
    private View mRedpacketView;
    private FrameLayout mFramRedPacket;

    public RedPacketMode(Activity activity, FrameLayout framRedPacket, View redpacketView) {
        mActivity = activity;
        mRedpacketView = redpacketView;
        mFramRedPacket = framRedPacket;
    }


    private static final int mInitY = 60;
    //循环生成红包，使用static，不持有Activity的引用
    private static Handler mHandler = new Handler();
    private int mRedPacketCount;
    private TextView mPacketChance;
    private TextView mTimer;
    private int[] mSize;//保存屏幕尺寸
    private FrameLayout mFrameLayout;
    private int mTotalAmount;//保存抢到的红包总金额
    private int mTotalAccount = 10000;//红包的个数
    private int mCurrentAccount;//当前生成的个数，一旦达到总的红包个数，停止继续生成红包
    private int mDuration = 4000;//每个动画的默认时长
    private int mDelay = 130;//每次生成红包的默认间隔
    private TimeInterpolator[] mInterpolators;//保存不同的插值器
    private int redPacketCount;
    private double amountSub;
    private String actID;
    private Runnable runnable;
    private LinearLayout mLLchance;
    private String mMissionId;

    public void initView(int RedPacketCount, double AmountSub, String Id, String missionId) {

        redPacketCount = RedPacketCount;
        actID = Id;
        amountSub = AmountSub;
        mMissionId = missionId;
        mSize = ScreenUtils.getWindowWidthAndHeight(mActivity);//获取屏幕的宽和高
        initInterpolator();//设置好插值器数组，然后随机设置插值器，红包的动画就会速度不同的效果
        mLLchance = ((LinearLayout) mRedpacketView.findViewById(R.id.LLchance));
        mFrameLayout = mRedpacketView.findViewById(R.id.container);
        mTimer = ((TextView) mRedpacketView.findViewById(R.id.timer));
        mPacketChance = ((TextView) mRedpacketView.findViewById(R.id.packetChance));

        mPacketChance.setText(String.valueOf(redPacketCount));

        startAnimation();//开始动画
        /** 倒计时60秒，一次1秒 */
        mCountDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimer.setText((millisUntilFinished / 1000) + "s");
                if (millisUntilFinished / 1000 == 0) {
                    mTimer.setVisibility(View.GONE);
                } else {
                    mTimer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFinish() {
                //进入结算
                if (packetGetCount != 0) {
                    goCheckout(packetGetCount);
                }

                if (packetGetCount == 0) {
                    if (mHandler != null) {
                        mHandler.removeCallbacks(runnable);
                        mFramRedPacket.setVisibility(View.GONE);
                        mFramRedPacket.removeAllViews();
                        mCountDownTimer = null;
                    }
                }

            }
        }.start();
    }

    CountDownTimer mCountDownTimer;

    private void startAnimation() {

        runnable = new Runnable() {
            @Override
            public void run() {

                if (mCurrentAccount > mTotalAccount) {
                    return;
                } else {
                    mCurrentAccount++;
                }

                final RedPacketViewgroup redPacketView = new RedPacketViewgroup(mActivity, mCurrentAccount);
                redPacketView.setmIPacketOpened(RedPacketMode.this::onPacketOpened);
                mFrameLayout.addView(redPacketView);//把红包添加进来
                redPacketView.setX(getInitialX());

                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(redPacketView, "translationY",
                        -ScreenUtils.dpToPixel(mInitY), mSize[1] + ScreenUtils.dpToPixel(mInitY));

                //随机设置插值器
                objectAnimator.setInterpolator(mInterpolators[ScreenUtils.getRandomInt(3)]);

                //设置动画时长，也可以设置成随机的
                objectAnimator.setDuration(mDuration);

                //给动画添加监听器，当动画结束时，主要做两件事1.判断红包是否被拆开，如拆开，增加抢到的红包金额
                //2.remove子view
                objectAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (redPacketView.isClicked()) {
                            getPacketFromServer();
                        }
                        mFrameLayout.removeView(redPacketView);//一旦动画结束，立即remove，让系统及时回收
                    }
                });

                objectAnimator.start();
                mHandler.postDelayed(this, mDelay);
            }
        };
        mHandler.postDelayed(runnable, mDelay);
    }

    List<RedPacketGetDetail> redPacketGetDetailList = new ArrayList<>();

    boolean erroFlag = false;

    private void getPacketFromServer() {
        HttpActionTouCai.getActivityDoChouJiang(mActivity, actID, new AbHttpResult() {

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                super.setupEntity(entity);
                entity.putField("data", new TypeToken<RedPacketGetDetail>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    RedPacketGetDetail redPacketGetDetail = getFieldObject(extra, "data", RedPacketGetDetail.class);
                    redPacketGetDetailList.add(redPacketGetDetail);
                    packetGetCount++;
                    redPacketCount--;
                    if (redPacketCount > 0) {
                        mPacketChance.setText(String.valueOf(redPacketCount));
                    } else if (redPacketCount == 0) {//领取机会用完
                        if (mHandler != null) {
                            mHandler.removeCallbacks(runnable);
                            mFrameLayout.setVisibility(View.GONE);
                            mLLchance.setVisibility(View.GONE);
                            //进入结算
                            goCheckout(packetGetCount);
                            mCountDownTimer.cancel();
                            mTimer.setVisibility(View.GONE);
                        }
                    }
                }

                if (code == -6 && !erroFlag) {

                    //如果奖池没有余额的时候
                    DialogsTouCai.showRedPacketMissionTipDialog(mActivity, "红包雨", -1, "红包雨已经被领完了~ 下次活动再见", "确定", R.mipmap.end_of_the_activity, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //进入结算
                            if (packetGetCount != 0) {
                                goCheckout(packetGetCount);
                            }

                            if (packetGetCount == 0) {
                                if (mHandler != null) {
                                    mHandler.removeCallbacks(runnable);
                                    mFramRedPacket.setVisibility(View.GONE);
                                    mFramRedPacket.removeAllViews();
                                    mCountDownTimer = null;
                                }
                            }
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //进入结算
                            if (packetGetCount != 0) {
                                goCheckout(packetGetCount);
                            }

                            if (packetGetCount == 0) {
                                if (mHandler != null) {
                                    mHandler.removeCallbacks(runnable);
                                    mFramRedPacket.setVisibility(View.GONE);
                                    mFramRedPacket.removeAllViews();
                                    mCountDownTimer = null;
                                }
                            }
                        }
                    });

                    UserManagerTouCai.getIns().setRedPacketAwardPoolStatus(true);//
                    UserManagerTouCai.getIns().setRedPacketMissionID(mMissionId);
                    erroFlag = true;
                }

                return super.onSuccessGetObject(code, error, msg, extra);
            }
        });
    }


    public void onRestart() {
        if (mHandler != null) {
            mHandler.removeCallbacks(runnable);
        }

        if (mFramRedPacket != null) {
            mFramRedPacket.setVisibility(View.GONE);
            mFramRedPacket.removeAllViews();
        }

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    public void onDestory() {
        if (mHandler != null) {
            mHandler.removeCallbacks(runnable);
        }

        if (mFramRedPacket != null) {
            mFramRedPacket.setVisibility(View.GONE);
            mFramRedPacket.removeAllViews();
        }

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    public void onStop() {
        if (mHandler != null) {
            mHandler.removeCallbacks(runnable);
        }

        if (mFramRedPacket != null) {
            mFramRedPacket.setVisibility(View.GONE);
            mFramRedPacket.removeAllViews();
        }

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    public void onResume() {
        if (mHandler != null) {
            mHandler.removeCallbacks(runnable);
        }

        if (mFramRedPacket != null) {
            mFramRedPacket.setVisibility(View.GONE);
            mFramRedPacket.removeAllViews();
        }

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    int packetGetCount = 0;

    /**
     * 进入结算
     */
    private void goCheckout(int packetGetCount) {

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }

        mFrameLayout.setVisibility(View.GONE);
        mLLchance.setVisibility(View.GONE);

        DialogsTouCai.showRedPacketMissionCheckout(mActivity, redPacketGetDetailList, packetGetCount, amountSub, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActDeposit.launch(mActivity);
                mFramRedPacket.setVisibility(View.GONE);
                mFramRedPacket.removeAllViews();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CtxLotteryTouCai.launchLotteryPlay(mActivity, LotteryKind.find("腾讯分分彩").getId());
                mFramRedPacket.setVisibility(View.GONE);
                mFramRedPacket.removeAllViews();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFramRedPacket.setVisibility(View.GONE);
                mFramRedPacket.removeAllViews();
            }
        });
    }


    //红包生成时的初始X坐标，这里设置为0-屏幕width-红包width
    private float getInitialX() {
        int max = (int) (mSize[0] - ScreenUtils.dpToPixel(50));
        Random random = new Random();
        float ranNum = random.nextInt(max);
        return ranNum;
    }

    private void initInterpolator() {
        //这里设置了属性动画的不同的插值器，第一个是线性Interpolator,匀速，第二个持续加速，第三个先加速再减速
        //如果对动画还有不了解的，推荐博客:http://hencoder.com/page/2/
        mInterpolators = new BaseInterpolator[]{new LinearInterpolator(), new AccelerateInterpolator(),
                new AccelerateDecelerateInterpolator()};
    }

    @Override
    public void onPacketOpened() {
    }
}
