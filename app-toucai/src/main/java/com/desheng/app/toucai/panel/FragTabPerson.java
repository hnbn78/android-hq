package com.desheng.app.toucai.panel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.callback.AbCallback;
import com.ab.http.AbHttpResult;
import com.ab.util.ArraysAndLists;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.view.MaterialDialog;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.UserBindStatus;
import com.desheng.base.panel.ActAccountChangeRecord;
import com.desheng.base.panel.ActBankCardList;
import com.desheng.base.panel.ActBettingRecordList;
import com.desheng.base.panel.ActBindBankCard;
import com.desheng.base.panel.ActChaseRecordList;
import com.desheng.base.panel.ActDepositRecord;
import com.desheng.base.panel.ActLotteryReport;
import com.desheng.base.panel.ActWithdrawalsRecord;
import com.desheng.base.view.DraggableFlagView;
import com.pearl.act.base.AbBaseFragment;
import com.shark.tc.R;

import java.util.HashMap;

/**
 * 账户页面
 */
@Deprecated
public class FragTabPerson extends AbBaseFragment implements View.OnClickListener, IRefreshableFragment {
    public static final String[] arrRefresh = {UserManagerTouCai.EVENT_USER_LOGINED,
            UserManagerTouCai.EVENT_USER_INFO_UNPDATED,
            UserManagerTouCai.EVENT_USER_LOGOUTED};
    
    private ImageView ivSlidePotrait;
    private TextView tvAccount;
    private TextView tvType;
    private TextView tvWithdrawCnt;
    private TextView tvBalanceCnt;
    private ImageView ivDeposit;
    private ImageView ivTransfer;
    private ImageView ivWithdrawal;
    private RelativeLayout vgPersonCenter;
    private RelativeLayout vgSafe;
    private RelativeLayout vgFunds;
    private RelativeLayout vgBankCard;
    private RelativeLayout vgBetting;
    private RelativeLayout vgAccountChange;
    private RelativeLayout vgLotteryReport;
    private RelativeLayout vgChaseRecord;
    private RelativeLayout vgChargeRecode;
    private RelativeLayout vgWithdrawRecord;
    private RelativeLayout vgThirdGameRecode;
    private RelativeLayout vgTrans;
    private UserManager.EventReceiver receiver;
    private MaterialDialog materialDialog;
    private View vgThirdTransfer;
    private View vgUserMessage;
    private View vgGameRecord;
    private View vgGameDayReport;
    private View vgThirdTransferOld;
    private DraggableFlagView mUserMsgDot;
    
    
    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_person;
    }
    
    
    @Override
    public void init(View view) {
        tvAccount = (TextView) view.findViewById(R.id.tvAccount);
        tvType = (TextView) view.findViewById(R.id.tvType);
        tvWithdrawCnt = (TextView) view.findViewById(R.id.tvWithdrawCnt);
        
        ivWithdrawal = (ImageView) view.findViewById(R.id.ivWithdrawal);
        ivWithdrawal.setOnClickListener(this);
        
        tvBalanceCnt = (TextView) view.findViewById(R.id.tvBalanceCnt);
        ivDeposit = (ImageView) view.findViewById(R.id.ivDeposit);
        ivDeposit.setOnClickListener(this);
    
        ivTransfer = (ImageView) view.findViewById(R.id.ivTransfer);
        ivTransfer.setOnClickListener(this);
        
        vgSafe = (RelativeLayout) view.findViewById(R.id.vgSafe);
        vgSafe.setOnClickListener(this);
        vgFunds = (RelativeLayout) view.findViewById(R.id.vgFunds);
        vgFunds.setOnClickListener(this);
        vgBankCard = (RelativeLayout) view.findViewById(R.id.vgBankCard);
        vgBankCard.setOnClickListener(this);
        vgBetting = (RelativeLayout) view.findViewById(R.id.vgBetting);
        vgBetting.setOnClickListener(this);
        vgAccountChange = (RelativeLayout) view.findViewById(R.id.vgAccountChange);
        vgAccountChange.setOnClickListener(this);
        vgLotteryReport = (RelativeLayout) view.findViewById(R.id.vgLotteryReport);
        vgLotteryReport.setOnClickListener(this);
        vgChaseRecord = (RelativeLayout) view.findViewById(R.id.vgChaseRecord);
        vgChaseRecord.setOnClickListener(this);
        vgChargeRecode = (RelativeLayout) view.findViewById(R.id.vgChargeRecode);
        vgChargeRecode.setOnClickListener(this);
        vgWithdrawRecord = (RelativeLayout) view.findViewById(R.id.vgWithdrawRecord);
        vgWithdrawRecord.setOnClickListener(this);
        vgThirdTransfer = view.findViewById(R.id.vgThirdTransfer);
        vgThirdTransfer.setOnClickListener(this);
        vgThirdGameRecode = view.findViewById(R.id.vgThirdGameRecode);
        vgThirdGameRecode.setOnClickListener(this);
        vgUserMessage = view.findViewById(R.id.vgUserMessage);
        vgUserMessage.setOnClickListener(this);
        mUserMsgDot =  (DraggableFlagView) view.findViewById(R.id.mUserMsgDot);
        receiver = new UserManagerTouCai.EventReceiver().register(getActivity(), new UserManagerTouCai.UserEventHandler() {
            @Override
            public void handleEvent(String eventName, Bundle bundle) {
                if (ArraysAndLists.findIndexWithEquals(eventName, arrRefresh) != -1) {
                    if (UserManagerTouCai.EVENT_USER_INFO_UNPDATED.equals(eventName) ||
                            UserManagerTouCai.EVENT_USER_LOGOUTED.equals(eventName)) { //数剧已更新则主动刷新ui
                        updateUserInfo();
                    }else{
                        refresh(null, null); //登录登出则重新拉取接口
                    }
                }
            }
        });
    }
    
    @Override
    public void refresh(String eventName, Bundle newBundle) {
        UserManagerTouCai.getIns().refreshUserData();
    }
    
    private void updateUserInfo() {
        if (UserManager.getIns().isLogined()) {
            tvAccount.setText(UserManagerTouCai.getIns().getMainUserName());
            tvType.setText("会员类型:" +UserManager.getIns().getUserTypeName());
            double canDraw = UserManagerTouCai.getIns().getLotteryAvailableBalance();
            tvWithdrawCnt.setText(Nums.formatDecimal(canDraw, 3));
            tvBalanceCnt.setText(Nums.formatDecimal(UserManagerTouCai.getIns().getLotteryAvailableBalance(), 3));
            //mUserMsgDot.setVisibility(View.VISIBLE);
            //mUserMsgDot.setText(""+UserManager.getIns().getMsgCount());
        }else{
            tvAccount.setText("掘金者");
            tvType.setText("会员类型:" + "");
            tvWithdrawCnt.setText("0");
            tvBalanceCnt.setText("0");
           // mUserMsgDot.setVisibility(View.INVISIBLE);
        }
    }
    
   
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivWithdrawal:
                ActWithdrawalsTouCai.launch(getActivity());
                break;
            case R.id.ivTransfer:
                UserManager.getIns().checkUserBindStatus(new UserManager.IUserBindCheckCallback() {
                    @Override
                    public void onBefore() {
            
                    }
        
                    @Override
                    public void onUserBindChecked(UserBindStatus status) {
                        if (!status.isBindWithdrawPassword()) {
                            showCheckBankDialog(new AbCallback<Object>() {
                                @Override
                                public boolean callback(Object obj) {
                                    ActBindBankCard.launch(getActivity(), true);
                                    return true;
                                }
                            });
                        } else {
                            showFunPasswordDialog(new AbCallback<String>() {
                                @Override
                                public boolean callback(final String obj) {
                                    HttpAction.modifyFundsPassword(obj, obj, new AbHttpResult() {
        
                                        @Override
                                        public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                            if (msg.contains("密码相同")) {
//                                                ActThirdTransferFast.launch(getActivity(), obj);
                                            } else {
                                                Toasts.show(getActivity(), "请输入正确资金密码", false);
                                            }
                                            return true;
                                        }
        
                                        @Override
                                        public boolean onError(int status, String content) {
                                            Toasts.show(getActivity(), content, false);
                                            return true;
                                        }
                                    });
                                    
                                    return true;
                                }
                            });
                        }
                    }
        
                    @Override
                    public void onUserBindCheckFailed(String msg) {
            
                    }
        
                    @Override
                    public void onAfter() {
            
                    }
                });
                
                break;
            case R.id.ivDeposit:
                ActDeposit.launch(getActivity());
                break;
            case R.id.vgSafe:
                ActSafeCenter.launch(getActivity());
                break;
            case R.id.vgFunds:
                ActFundsManagment.launch(getActivity(), false);
                break;
            case R.id.vgBankCard:
                UserManager.getIns().checkUserBindStatus(new UserManager.IUserBindCheckCallback() {
                    @Override
                    public void onBefore() {
                    
                    }
                    
                    @Override
                    public void onUserBindChecked(UserBindStatus status) {
                        if (!status.isBindWithdrawPassword()) {
                            showCheckBankDialog(new AbCallback<Object>() {
                                @Override
                                public boolean callback(Object obj) {
                                    ActBindBankCard.launch(getActivity(), true);
                                    return true;
                                }
                            });
                        } else {
                            ActBankCardList.launch(getActivity());
                        }
                    }
                    
                    @Override
                    public void onUserBindCheckFailed(String msg) {
                    
                    }
                    
                    @Override
                    public void onAfter() {
                    
                    }
                });
                break;
            case R.id.vgBetting:
                ActBettingRecordList.launch(getActivity());
                break;
            case R.id.vgLotteryReport:
                ActLotteryReport.launch(getActivity(), false);
                break;
            case R.id.vgChaseRecord:
                ActChaseRecordList.launch(getActivity());
                break;
            case R.id.vgChargeRecode:
                ActDepositRecord.launch(getActivity(), false);
                break;
            case R.id.vgWithdrawRecord:
                ActWithdrawalsRecord.launch(getActivity(), false);
                break;
            case R.id.vgAccountChange:
                ActAccountChangeRecord.launch(getActivity());
                break;
            case R.id.vgThirdTransfer:
                ActThirdTransferRecordList.launch(getActivity());
                break;
            case R.id.vgThirdGameRecode:
                ActThirdGameRecordTouCai.launch(getActivity());
                break;
            case R.id.vgUserMessage:
                ActUserMsgList.launch(getActivity());
                break;
        }
    }
    
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            receiver.unregister(getActivity());
        }
    }
    
    
    public void showCheckBankDialog(final AbCallback<Object> callback) {
        if (materialDialog != null) {
            materialDialog.dismiss();
        }
        materialDialog = new MaterialDialog(getActivity());
        materialDialog.setMessage("亲，先绑定取款人");
        materialDialog.setCanceledOnTouchOutside(true);
        materialDialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                callback.callback(null);
            }
        });
        materialDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }
    
    private void showFunPasswordDialog(final AbCallback<String> callback) {
        if (materialDialog != null) {
            materialDialog.dismiss();
        }
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_fun_password, null);
        ImageView ivClose = viewGroup.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        
        final EditText etPoint = (EditText) viewGroup.findViewById(R.id.etPoint);
        Button btnConfirm = (Button) viewGroup.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = etPoint.getText().toString();
                if (Strs.isEmpty(pwd)) {
                    Toast.makeText(getActivity(), "请输入资金密码", Toast.LENGTH_LONG).show();
                    return;
                }
                callback.callback(pwd);
                materialDialog.dismiss();
            }
        });
        materialDialog = Dialogs.showCustomDialog(getActivity(), viewGroup, true);
    }
    
    public static void launchTransfer(Context act) {
        Intent itt = new Intent(act, ActTransferrecordActivity.class);
        act.startActivity(itt);
    }
    
}
