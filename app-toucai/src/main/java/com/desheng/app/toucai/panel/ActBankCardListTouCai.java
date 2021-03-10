package com.desheng.app.toucai.panel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.ab.callback.AbCallback;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.thread.ThreadCollector;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.model.XunibiAddressBean;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.BankCardInfo;
import com.desheng.base.model.UserBindStatus;
import com.desheng.base.panel.ActChangeFundPassword;
import com.desheng.base.panel.ActWeb;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.rmondjone.locktableview.DisplayUtil;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Request;

/**
 * Created by user on 2018/4/16.
 */

public class ActBankCardListTouCai extends AbAdvanceActivity {

    private boolean isFirstBind = true;
    private RecyclerView lvCards;
    private ArrayList<BankCardInfo> listCard;
    private ArrayList<BankCardInfo> mCardList = new ArrayList<>();
    private BaseQuickAdapter<BankCardInfo, BaseViewHolder> adapter;


    public static void launch(Context act) {
        Intent itt = new Intent(act, ActBankCardListTouCai.class);
        act.startActivity(itt);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_bank_card_list;
    }

    @Override
    protected void init() {
        setStatusBarTranslucentAndLightContentWithPadding();
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "银行卡列表");
        lvCards = findViewById(R.id.lvCards);

        lvCards.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickAdapter<BankCardInfo, BaseViewHolder>(R.layout.item_bank_info, mCardList) {
            @Override
            protected void convert(BaseViewHolder helper, BankCardInfo item) {
                String bankFace = UserManager.getIns().getBankFace(item.getBankName());
                String bankBg = UserManager.getIns().getBankBG(item.getBankName());

                if (Strs.isNotEmpty(bankFace)) {
                    if (item.getCardType() == -1) {
                        ((ImageView) helper.getView(R.id.ivBankLogo)).setImageResource(R.mipmap.icon_withdraw_xunibi);
                    } else {
                        Glide.with(ActBankCardListTouCai.this).load(Uri.parse(bankFace)).
                                placeholder(R.mipmap.ic_bank_def).
                                into(((ImageView) helper.getView(R.id.ivBankLogo)));
                    }
                }

                if (Strs.isNotEmpty(bankBg)) {
                    if (item.getCardType() == -1) {
                        ((ImageView) helper.getView(R.id.iv_bank_bg)).setImageResource(R.mipmap.xunibi_bg);
                    } else {
                        Glide.with(ActBankCardListTouCai.this).load(Uri.parse(bankBg)).
                                placeholder(R.mipmap.ic_bank_bg).
                                into(((ImageView) helper.getView(R.id.iv_bank_bg)));
                    }
                }

                helper.setText(R.id.tvBankName, item.getBankName());
                helper.setText(R.id.tv_card_code, item.getCardType() == -1 ? item.getBankCardId() : ("****    ****    ****    " + item.getBankCardId()));

                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                double device_width = (DisplayUtil.screenWidthPx) * 0.9;

                if (item.getCardType() == -1) {
                    helper.setVisible(R.id.tv_account_address, false);
                } else {
                    helper.setVisible(R.id.tv_account_address, true);
                    helper.setText(R.id.tv_account_address, item.getBankCardName() + "**   " + item.getBankBranch());
                }

                if (item.getCardType() == 2) {
                    helper.setText(R.id.tvCardType, "信用卡");
                } else if (item.getCardType() == -1) {
                    helper.setText(R.id.tvCardType, "虚拟币");
                } else {
                    helper.setText(R.id.tvCardType, "借记卡");
                }

                lp.width = (int) (device_width);
                lp.height = (int) (device_width / 3);
                ((ImageView) helper.getView(R.id.iv_bank_bg)).setLayoutParams(lp);
            }
        };
        adapter.setEnableLoadMore(false);
        ImageView ivFooterViewCard = new ImageView(this);
        ivFooterViewCard.setImageResource(R.mipmap.ic_addcard);
        ImageView ivFooterViewXunibi = new ImageView(this);
        ivFooterViewXunibi.setImageResource(R.mipmap.ic_addcard_pocket);
        adapter.addFooterView(ivFooterViewCard, 1);
        adapter.addFooterView(ivFooterViewXunibi, 2);
        lvCards.setAdapter(adapter);

        ivFooterViewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listCard != null && listCard.size() >= 5) {
                    Toasts.show(ActBankCardListTouCai.this, "银行卡最多添加5张!", false);
                    return;
                } else {
                    UserManager.getIns().checkUserBindStatus(new UserManager.IUserBindCheckCallback() {
                        @Override
                        public void onBefore() {
                            ThreadCollector.getIns().runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    Dialogs.showProgressDialog(ActBankCardListTouCai.this, "");
                                }
                            });
                        }

                        @Override
                        public void onUserBindChecked(final UserBindStatus status) {
                            ActBindBankCardToucai.launch(ActBankCardListTouCai.this, !status.isBindWithdrawName());
                        }

                        @Override
                        public void onUserBindCheckFailed(String msg) {
                            Toasts.show(ActBankCardListTouCai.this, msg, false);
                        }

                        @Override
                        public void onAfter() {
                            ThreadCollector.getIns().postDelayOnUIThread(400, new Runnable() {
                                @Override
                                public void run() {
                                    Dialogs.hideProgressDialog(ActBankCardListTouCai.this);
                                }
                            });
                        }
                    });
                }
            }
        });

        ivFooterViewXunibi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActAddXunibiPocketAddress.launch(ActBankCardListTouCai.this);
            }
        });

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BankCardInfo bankCardInfo = (BankCardInfo) adapter.getData().get(position);
                if (bankCardInfo == null) {
                    return;
                }
                if (!bankCardInfo.getIsXunibiCardType()) {
                    HttpAction.setDefaultCard(mCardList.get(position).getId(), new AbHttpResult() {
                        @Override
                        public void setupEntity(AbHttpRespEntity entity) {

                        }

                        @Override
                        public void onBefore(Request request, int id, String host, String funcName) {
                            lvCards.post(new Runnable() {
                                @Override
                                public void run() {
                                    Dialogs.showProgressDialog(ActBankCardListTouCai.this, "");
                                }
                            });
                        }

                        @Override
                        public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                            if (code == 0 && error == 0) {
                                Toasts.show(ActBankCardListTouCai.this, "设置成功!", true);
                            } else {
                                Toasts.show(ActBankCardListTouCai.this, msg, false);
                            }
                            return true;
                        }

                        @Override
                        public void onAfter(int id) {
                            lvCards.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Dialogs.hideProgressDialog(ActBankCardListTouCai.this);
                                }
                            }, 400);
                        }
                    });
                }
            }
        });
    }

    private void initData() {
        HttpAction.getUserBankCardList(ActBankCardListTouCai.this, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<BankCardInfo>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getFieldObject(extra, "data", null) != null) {
                    listCard = (ArrayList<BankCardInfo>) getFieldObject(extra, "data", null);
                    mCardList.addAll(listCard);
                    isFirstBind = (listCard.size() == 0);
                    if (Strs.isNotEmpty(UserManager.getIns().getWithDrawName())) {
                        isFirstBind = false;
                    }

                    adapter.notifyDataSetChanged();

                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCardList != null) {
            mCardList.clear();
        }
        checkUserBankCard();
        initData();
        getXunibiPocketAddress();
    }

    private void getXunibiPocketAddress() {
        HttpAction.getUsdtWithdrawAddress(this, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<XunibiAddressBean>>() {
                }.getType());
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActBankCardListTouCai.this, "");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    ArrayList<XunibiAddressBean> xunibiAddressBeans = getFieldObject(extra, "data", ArrayList.class);
                    if (xunibiAddressBeans != null) {
                        for (XunibiAddressBean xunibiAddressBean : xunibiAddressBeans) {
                            BankCardInfo e = new BankCardInfo();
                            e.setBankCardId(xunibiAddressBean.getAddress());
                            e.setIsXunibiCardType(true);
                            e.setBankName("USDT(ERC20)");
                            e.setCardType(-1);
                            mCardList.add(e);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Toast.makeText(ActBankCardListTouCai.this, content + "", Toast.LENGTH_SHORT).show();
                Dialogs.hideProgressDialog(ActBankCardListTouCai.this);
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Dialogs.hideProgressDialog(ActBankCardListTouCai.this);
            }
        });
    }

    private void checkUserBankCard() {
        UserManager.getIns().checkUserBindStatus(new UserManager.IUserBindCheckCallback() {
            @Override
            public void onBefore() {
                lvCards.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActBankCardListTouCai.this, "");
                    }
                });
            }

            @Override
            public void onUserBindChecked(UserBindStatus status) {
                isFirstBind = !status.isBindCard();
            }

            @Override
            public void onUserBindCheckFailed(String msg) {
                Toasts.show(ActBankCardListTouCai.this, msg, false);
            }

            @Override
            public void onAfter() {
                lvCards.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.hideProgressDialog(ActBankCardListTouCai.this);
                    }
                }, 400);
            }
        });
    }

    private void showUpdateTip(String tip2, String left, String right) {
        DialogsTouCai.showUpdateDialog(this, tip2, left, right, true, new AbCallback<Object>() {
            @Override
            public boolean callback(Object obj) {
                Intent intent = new Intent(ActBankCardListTouCai.this, ActChangeFundPassword.class);
                intent.putExtra("isWithdraw", true);
                startActivity(intent);
                return true;
            }
        }, new AbCallback<Object>() {
            @Override
            public boolean callback(Object obj) {
                ActWeb.launchCustomService(ActBankCardListTouCai.this);
                return true;
            }
        });
    }
}
