package com.desheng.app.toucai.helper;

import android.app.Activity;
import android.view.View;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.AwardPoolDetailoMode;
import com.desheng.app.toucai.model.NewUserMission;
import com.desheng.app.toucai.panel.ActBindBankCardToucai;
import com.desheng.app.toucai.panel.ActBindEmail;
import com.desheng.app.toucai.panel.ActBindPhone;
import com.desheng.app.toucai.panel.ActChoujiangListDetail;
import com.desheng.app.toucai.panel.ActEditAccount;
import com.desheng.app.toucai.panel.ActSettingFundsPwd;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class BonusPoolActivityHelper {

    public interface IBonusPoolCallback {
        void onBonusPoolIDExist(List<NewUserMission> listMission);

        void onGetBonusPoolConfig(AwardPoolDetailoMode poolDetail);

        void onGetBonusPoolConfigFaild();
    }

    public interface IBonusPoolChoujiangCallback {
        void onBonusPoolChoujiangOver();
    }

    /**
     * 处理奖池抽奖逻辑
     *
     * @param activity
     * @param callback
     * @param poolDetailo
     */
    public static void getBonusPoolDoChoujiang(Activity activity, IBonusPoolChoujiangCallback callback, AwardPoolDetailoMode poolDetailo) {
        HttpActionTouCai.getMissionList(BonusPoolActivityHelper.class, Consitances.ImissionTypeConfig.AWARD_POOL, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                DialogsTouCai.showProgressDialog(activity, "");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<NewUserMission>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    List<NewUserMission> listMission = getField(extra, "data", null);
                    if (listMission.size() > 0) {
                        NewUserMission finalMission = listMission.get(0);
                        String id = finalMission.id;
                        if (Strs.isNotEmpty(id)) {
                            HttpActionTouCai.getBonusPoolDoChoujiang(this, id, new AbHttpResult() {

                                @Override
                                public void setupEntity(AbHttpRespEntity entity) {
                                    entity.putField("data", new TypeToken<List<String>>() {
                                    }.getType());
                                }

                                @Override
                                public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                    if (code == 0 && error == 0) {
                                        List<String> listCodes = getField(extra, "data", null);
                                        if (listCodes != null && listCodes.size() > 0) {
                                            Collections.reverse(listCodes);
                                            if (poolDetailo != null) {
                                                DialogsTouCai.showChoujianngDialog(activity, listCodes, poolDetailo.getActivityIssueNo(), new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        ActChoujiangListDetail.launch(activity, id, poolDetailo.getActivityIssueNo());
                                                    }
                                                });
                                            }
                                            if (callback != null) {
                                                callback.onBonusPoolChoujiangOver();
                                            }
                                        }
                                    } else {
                                        switch (code) {
                                            case -4:
                                                DialogsTouCai.showDialog(activity, "温馨提醒", "参与该活动需要绑定真实姓名", "前往绑定", v -> {
                                                    ActEditAccount.launcher(activity);
                                                });
                                                break;
                                            case -5:
                                                if (!UserManagerTouCai.getIns().getIsBindWithdrawPassword()) {
                                                    DialogsTouCai.showDialog(activity, "温馨提醒", "参与该活动需要设置资金密码", "前往设置", v -> {
                                                        ActSettingFundsPwd.launcher(activity, false);
                                                    });
                                                } else {
                                                    DialogsTouCai.showDialog(activity, "温馨提醒", "参与该活动需要绑定银行卡", "前往绑定", v -> {
                                                        ActBindBankCardToucai.launch(activity, true);
                                                    });
                                                }
                                                break;
                                            case -6:
                                                DialogsTouCai.showDialog(activity, "温馨提醒", "参与该活动需要绑定手机号", "前往绑定", v -> {
                                                    ActBindPhone.launch(activity);
                                                });
                                                break;
                                            case -7:
                                                DialogsTouCai.showDialog(activity, "温馨提醒", "参与该活动需要绑定邮箱", "前往绑定", v -> {
                                                    ActBindEmail.launcher(activity);
                                                });
                                                break;
                                            default:
                                                DialogsTouCai.showSimpleDialog(activity, "提示信息", "" + msg, "确认");
                                                break;
                                        }
                                    }
                                    return super.onSuccessGetObject(code, error, msg, extra);
                                }
                            });
                        }
                    }
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(activity, content, false);
                return true;
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                DialogsTouCai.hideProgressDialog(activity);
            }
        });

    }

    /**
     * 处理奖池显示逻辑
     *
     * @param iBonusPoolCallback
     */
    public static void getMissionList(IBonusPoolCallback iBonusPoolCallback) {
        HttpActionTouCai.getMissionList(BonusPoolActivityHelper.class, Consitances.ImissionTypeConfig.AWARD_POOL, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<NewUserMission>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    List<NewUserMission> listMission = getField(extra, "data", null);
                    if (iBonusPoolCallback != null) {
                        iBonusPoolCallback.onBonusPoolIDExist(listMission);
                        if (listMission.size() > 0) {
                            NewUserMission finalMission = listMission.get(0);
                            String id = finalMission.id;
                            if (Strs.isNotEmpty(id)) {
                                getAwardPoolMissionData(id, iBonusPoolCallback);
                            }
                        }
                    }
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                return true;
            }
        });
    }

    private static void getAwardPoolMissionData(String activityId, IBonusPoolCallback iBonusPoolCallback) {

        HttpActionTouCai.getBonusPoolActivityDetail(BonusPoolActivityHelper.class, activityId, new AbHttpResult() {

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<AwardPoolDetailoMode>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    AwardPoolDetailoMode poolDetailo = getField(extra, "data", null);
                    if (iBonusPoolCallback != null) {
                        iBonusPoolCallback.onGetBonusPoolConfig(poolDetailo);
                    }
                } else {
                    if (iBonusPoolCallback != null) {
                        iBonusPoolCallback.onGetBonusPoolConfigFaild();
                    }
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }
        });
    }


}
