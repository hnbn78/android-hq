package com.desheng.base.panel;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.global.Config;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.thread.ThreadCollector;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.databinding.ActLotteryCartBinding;
import com.desheng.base.event.BuyedLotteryEvent;
import com.desheng.base.event.ChasedLotteryEvent;
import com.desheng.base.event.ClearLotteryPlaySelectedEvent;
import com.desheng.base.event.DeleteFromCartEvent;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.ItemDeviderDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Request;

/**
 * 彩票购物车
 * Created by lee on 2018/3/7.
 */
public class ActLotteryCart extends AbAdvanceActivity<ActLotteryCartBinding> implements SwipeRefreshLayout.OnRefreshListener {

    private CartAdapter adapter;

    private int lotteryId;
    private ILotteryKind lottery;
    private ArrayList<HashMap<String, Object>> listNumAddedCart;
    UserManager.EventReceiver receiver;
    private boolean showDelete = false;
    private LinearLayoutManager lotteryLayoutmanager;

    /**
     * @param activity
     * @param lotteryId        彩票id
     * @param listNumAddedCart
     */
    public static void launch(Activity activity, int lotteryId, ArrayList<HashMap<String, Object>> listNumAddedCart) {
        Intent itt = new Intent(activity, ActLotteryCart.class);
        itt.putExtra("lotteryId", lotteryId);
        itt.putExtra("listNumAddedCart", listNumAddedCart);
        activity.startActivity(itt);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_lottery_cart;
    }

    @Override
    protected void init() {
        getBinding().tvAvailableMoney.setText(Nums.formatDecimal(UserManager.getIns().getLotteryAvailableBalance(), 3));
        receiver = new UserManager.EventReceiver().register(this, new UserManager.UserEventHandler() {
            @Override
            public void handleEvent(String eventName, Bundle bundle) {
                if (eventName.equals(UserManager.EVENT_USER_INFO_UNPDATED)) {
                    getBinding().tvAvailableMoney.setText(Nums.formatDecimal(UserManager.getIns().getLotteryAvailableBalance(), 3));
                }
            }
        });

        lotteryId = getIntent().getIntExtra("lotteryId", 0);
        lottery = (ILotteryKind) CtxLottery.getIns().findLotteryKind(lotteryId);
        listNumAddedCart = (ArrayList<HashMap<String, Object>>) getIntent().getSerializableExtra("listNumAddedCart");
        if (listNumAddedCart == null) listNumAddedCart = new ArrayList<>();
        setToolbarLeftBtn(UIHelper.BACK_WHITE_CIRCLE_ARROW);
        if (UIHelper.toolbarBgResId > 0)
            setToolbarBgImage(UIHelper.toolbarBgResId);
        else
            setToolbarBgColor(R.color.colorPrimary);
        setToolbarTitleCenter("购彩车");
        setToolbarTitleCenterColor(R.color.white);
        setStatusBarTranslucentAndLightContentWithPadding();
        setToolbarButtonRightText("删除");
        setToolbarButtonRightTextColor(R.color.white);
        setToolbarRightButtonGroupClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDelete = !showDelete;
                if (showDelete) {
                    setToolbarButtonRightText("确认");
                } else {
                    setToolbarButtonRightText("删除");
                    adapter.removeDeletedItem();
                }
                updateBottomText();
                adapter.notifyDataSetChanged();
            }
        });

        getBinding().srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        getBinding().srlRefresh.setOnRefreshListener(this);

        updateBottomText();

        adapter = new CartAdapter(this, listNumAddedCart);
        lotteryLayoutmanager = Views.genLinearLayoutManagerV(this);
        getBinding().rvLottery.setLayoutManager(lotteryLayoutmanager);
        getBinding().rvLottery.addItemDecoration(new ItemDeviderDecoration(this, ItemDeviderDecoration.VERTICAL_LIST));
        getBinding().rvLottery.setAdapter(adapter);
//        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                View root = getLayoutInflater().inflate(R.layout.dialog_bet_record_content, null);
//                ((TextView) root.findViewById(R.id.tv_content)).setText(String.valueOf(listNumAddedCart.get(position).get(CtxLottery.Order.CONTENT)));
//                root.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Dialogs.dissmisCustomDialog();
//                    }
//                });
//                Dialogs.showCustomDialog(ActLotteryCart.this, root, true)
//                        .show();
//            }
//        });

        getBinding().tvPersumeNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listNumAddedCart == null || listNumAddedCart.size() == 0) {
                    Toasts.show(ActLotteryCart.this, "请先选择号码", false);
                } else {
                    ActLotteryChase.launch(ActLotteryCart.this, lotteryId, listNumAddedCart);
                }
            }
        });

        getBinding().tvBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listNumAddedCart == null || listNumAddedCart.size() == 0) {
                    Toasts.show(ActLotteryCart.this, "请先添加号码!", false);
                    return;
                }

                buy();
            }
        });

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void buy() {
        HttpAction.addOrder(ActLotteryCart.this, listNumAddedCart, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", Double.TYPE);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActLotteryCart.this, "");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    showLotteryBuyOkDialog(ActLotteryCart.this);

                    //删除所有添加的购物车注数
                    EventBus.getDefault().post(new DeleteFromCartEvent(true, null));

                    listNumAddedCart.clear();
                    adapter.notifyDataSetChanged();

                    //刷新用户资金
                    UserManager.getIns().setLotteryAvailableBalance((getField(extra, "data", 0.0)));
                    UserManager.getIns().sendBroadcaset(ActLotteryCart.this, UserManager.EVENT_USER_INFO_UNPDATED, null);

                    //清除选球数据
                    EventBus.getDefault().post(new ClearLotteryPlaySelectedEvent());

                    //刷新玩法页面记录
                    EventBus.getDefault().post(new BuyedLotteryEvent());

                    //刷新底部
                    updateBottomText();

                    //完成后退出
                    finish();
                } else if (code == -1000) {
                    showTimerDialogs();
                } else {
                    Toasts.show(ActLotteryCart.this, msg, false);
                }

                return true;
            }

            @Override
            public void onAfter(int id) {
                Dialogs.hideProgressDialog(ActLotteryCart.this);
            }
        });
    }

    private void showTimerDialogs() {
        Dialogs.showProgressDialog(this, "");
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                Dialogs.hideProgressDialog(ActLotteryCart.this);
            }
        }, 5000);
    }

    private void updateBottomText() {
        double totalMoney = 0.0;
        long totalHit = 0;
        int totolTimes = 0;
        for (int i = 0; i < listNumAddedCart.size(); i++) {
            totalMoney += (double) listNumAddedCart.get(i).get(CtxLottery.Order.MONEY);
            totalHit += (long) listNumAddedCart.get(i).get(CtxLottery.Order.HIT);
            totolTimes += (int) listNumAddedCart.get(i).get(CtxLottery.Order.MULTIPLE);
        }
        getBinding().tvMoney.setText("共");
        SpannableString sign = new SpannableString("￥");
        sign.setSpan(new ForegroundColorSpan(0xffff3b35), 0, sign.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sign.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, sign.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        getBinding().tvMoney.append(sign);
        SpannableString money = new SpannableString(Nums.formatDecimal(totalMoney, 3));
        money.setSpan(new ForegroundColorSpan(0xFFff3e3d), 0, money.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        money.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, money.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        getBinding().tvMoney.append(money);
        getBinding().tvHit.setText("共");
        SpannableString hit = new SpannableString(String.valueOf(totalHit));
        hit.setSpan(new ForegroundColorSpan(0xFFff3e3d), 0, hit.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        hit.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, hit.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        getBinding().tvHit.append(hit);
        getBinding().tvHit.append("注");
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    public void refresh() {
        ThreadCollector.getIns().postDelayOnUIThread(300, new Runnable() {
            @Override
            public void run() {
                getBinding().srlRefresh.setRefreshing(false);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBuyedInCartEvent(ChasedLotteryEvent event) {
        finish();
    }


    protected class CartAdapter extends BaseQuickAdapter<HashMap<String, Object>, CartAdapter.AddLotteryVH> {
        private final Context ctx;
        private ArrayList<HashMap<String, Object>> deleted = new ArrayList<>();

        public CartAdapter(Context ctx, ArrayList<HashMap<String, Object>> listData) {
            super(R.layout.item_lottery_cart_list, listData);
            this.ctx = ctx;
        }

        @Override
        protected void convert(final AddLotteryVH holder, final HashMap<String, Object> item) {
            holder.tvName.setText(lottery.getShowName());
            holder.tvIssue.setText("第" + String.valueOf(item.get(CtxLottery.Order.ISSUE).toString()) + "期");
            updateTags(holder, item);
            updateBall(holder, item);
//            holder.tvSelectedNum.setText(String.valueOf(item.get(CtxLottery.Order.CONTENT).toString()));
//            holder.tvSelectedNum.setMaxLines(5);
//            holder.tvSelectedNum.setEllipsize(TextUtils.TruncateAt.END);
//            holder.tvMoney.setText(Nums.formatDecimal(item.get(CtxLottery.Order.MONEY), 3)+ "元");
//            holder.tvPlayName.setText(String.valueOf(item.get(CtxLottery.Order.PLAY_NAME)));
//            holder.tvMoneyTimes.setText(item.get(CtxLottery.Order.MULTIPLE) + "倍");
//            holder.ivDelete.setTag(getData().indexOf(item));
//            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Integer index = (Integer) v.getTag();
//                    remove(index);
//                    EventBus.getDefault().post(new DeleteFromCartEvent(false, new int[]{index}));
//                    updateBottomText();
//                }
//            });
            holder.cbDelete.setChecked(deleted.contains(item));
            holder.cbDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked & !deleted.contains(item))
                        deleted.add(item);
                    else
                        deleted.remove(item);
                }
            });
            holder.etTimes.setText(item.get(CtxLottery.Order.MULTIPLE).toString());
            holder.etTimes.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    item.put(CtxLottery.Order.MULTIPLE, s.toString());
                    updateTags(holder, item);
                }
            });
            holder.minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int va = Strs.parse(holder.etTimes.getText().toString(), 0) - 1;
                    if (va < 0)
                        va = 0;
                    holder.etTimes.setText(Strs.of(va));
                }
            });
            holder.plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int va = Strs.parse(holder.etTimes.getText().toString(), 0) + 1;
                    if (va < 0)
                        va = 0;
                    holder.etTimes.setText(Strs.of(va));
                }
            });

            int pos = listNumAddedCart.indexOf(item);
//            if (lotteryLayoutmanager.findFirstVisibleItemPosition() > pos
//                    || lotteryLayoutmanager.findLastVisibleItemPosition() < pos)
//                holder.showDelete(showDelete, false);
//            else
            holder.showDelete(showDelete, true);
        }

        public void removeDeletedItem() {
            List<HashMap<String, Object>> data = getData();
            int[] arr = new int[deleted.size()];
            for (int i = 0; i < deleted.size(); i++) {
                arr[i] = data.indexOf(deleted.get(i));
            }
            EventBus.getDefault().post(new DeleteFromCartEvent(false, arr));
            data.removeAll(deleted);
            deleted.clear();
        }

        public void updateTags(AddLotteryVH holder, HashMap<String, Object> item) {
            List<String> tags = new ArrayList<>();
            tags.add(String.valueOf(item.get(CtxLottery.Order.PLAY_NAME)));
            tags.add(item.get(CtxLottery.Order.MULTIPLE) + "倍");
            tags.add(Nums.formatDecimal(item.get(CtxLottery.Order.MONEY), 3) + "元");
            holder.rvTags.setAdapter(new TagAdapter(tags));

        }

        public void updateBall(AddLotteryVH holder, HashMap<String, Object> item) {
            List<String> balls = new ArrayList<>();
            char[] b = item.get(CtxLottery.Order.CONTENT).toString().replaceAll("[,\\s-√\\[\\]]", "").toCharArray();
            for (char s : b) {
                balls.add(String.valueOf(s));
            }

        }

        protected class AddLotteryVH extends BaseViewHolder {
            private TextView tvName;
            private TextView tvIssue;
            private RecyclerView rvTags;
            private TextView tvBalls;
            private CheckBox cbDelete;
            private View container;
            private View minus;
            private View plus;
            private EditText etTimes;
            private boolean showDelete = false;


            public AddLotteryVH(View view) {
                super(view);
                container = view.findViewById(R.id.layout_container);
                tvName = (TextView) view.findViewById(R.id.tv_lottery_name);
                tvIssue = (TextView) view.findViewById(R.id.tv_issue);
                rvTags = view.findViewById(R.id.rv_tags);
                cbDelete = view.findViewById(R.id.cb_delete);
                tvBalls = view.findViewById(R.id.tv_balls);
                minus = view.findViewById(R.id.tvTimeMinus);
                plus = view.findViewById(R.id.tvTimePlus);
                etTimes = view.findViewById(R.id.etTimeMulti);
                rvTags.setLayoutManager(new LinearLayoutManager(ActLotteryCart.this, LinearLayoutManager.HORIZONTAL, false));
            }

            public void showDelete(boolean show, boolean animate) {
                if (show && (!showDelete)) {
                    showDelete = true;
                    cbDelete.setVisibility(View.VISIBLE);

                    if (animate) {
                        cbDelete.setTranslationX(-Views.dp2px(20));
                        cbDelete.animate().translationX(0);
                        container.animate().translationX(Views.dp2px(20));
                    } else {
                        cbDelete.setTranslationX(0);
                        container.setTranslationX(Views.dp2px(20));
                    }
                } else if (!show && showDelete) {
                    showDelete = false;

                    if (animate) {
                        container.animate().translationX(0);
                        final ViewPropertyAnimator animator = cbDelete.animate().translationX(-cbDelete.getMeasuredWidth());
                        animator.setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                cbDelete.setVisibility(View.GONE);
                                animator.setListener(null);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                cbDelete.setVisibility(View.GONE);
                                animator.setListener(null);
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        });
                    } else {
                        container.setTranslationX(0);
                        cbDelete.setTranslationX(-cbDelete.getMeasuredWidth());
                        cbDelete.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagHolder> {
        List<String> tags;

        public TagAdapter(List<String> tags) {
            this.tags = tags;
        }

        @Override
        public TagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = getLayoutInflater().inflate(R.layout.item_lottery_cart_list_tag, parent, false);
            return new TagHolder(root);
        }

        @Override
        public void onBindViewHolder(TagHolder holder, int position) {
            holder.textView.setText(tags.get(position));
        }

        @Override
        public int getItemCount() {
            return tags.size();
        }

        public class TagHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public TagHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView;
            }
        }
    }

//    public class BallAdapter extends RecyclerView.Adapter<BallAdapter.BallHolder> {
//        List<String> balls;
//
//        public BallAdapter(List<String> balls) {
//            this.balls = balls;
//        }
//
//        @Override
//        public BallHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View root = getLayoutInflater().inflate(R.layout.item_lottery_cart_list_ball, parent, false);
//            return new BallHolder(root);
//        }
//
//        @Override
//        public void onBindViewHolder(BallHolder holder, int position) {
//            holder.textView.setText(balls.get(position));
//        }
//
//        @Override
//        public int getItemCount() {
//            return balls.size();
//        }
//
//        public class BallHolder extends RecyclerView.ViewHolder {
//            TextView textView;
//
//            public BallHolder(View itemView) {
//                super(itemView);
//                textView = (TextView) itemView;
//            }
//        }
//    }


    public static void showLotteryBuyOkDialog(Context context) {
        final Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_lottery_play_buy_ok, null);
        root.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(root);
        dialog.show();
    }
}
