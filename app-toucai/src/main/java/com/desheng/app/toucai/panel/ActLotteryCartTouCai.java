package com.desheng.app.toucai.panel;

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
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.thread.ThreadCollector;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.databinding.ActLotteryCartBinding;
import com.desheng.base.event.BuyedLotteryEvent;
import com.desheng.base.event.ChasedLotteryEvent;
import com.desheng.base.event.ClearLotteryPlaySelectedEvent;
import com.desheng.base.event.DeleteFromCartEvent;
import com.desheng.base.manager.UserManager;
import com.desheng.base.panel.ActLotteryCart;
import com.desheng.base.panel.ActLotteryChase;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.ItemDeviderDecoration;
import com.shark.tc.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Request;

/**
 * 彩票购物车
 * Created by lee on 2018/3/7.
 */
public class ActLotteryCartTouCai extends AbAdvanceActivity<ActLotteryCartBinding> implements SwipeRefreshLayout.OnRefreshListener {

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
        Intent itt = new Intent(activity, ActLotteryCartTouCai.class);
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
                    showCartEmptyDialog(ActLotteryCartTouCai.this);
                } else {
                    ActLotteryChase.launch(ActLotteryCartTouCai.this, lotteryId, listNumAddedCart);
                }
            }
        });

        getBinding().tvBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listNumAddedCart == null || listNumAddedCart.size() == 0) {
                    showCartEmptyDialog(ActLotteryCartTouCai.this);
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
        HttpActionTouCai.maxBonus(this, lotteryId, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", String.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    double money = 0;
                    int hitCount = 0;
                    for (HashMap<String, Object> map : listNumAddedCart) {
                        money += (Double) map.get(CtxLottery.Order.MONEY);
                        hitCount += (Long) map.get(CtxLottery.Order.HIT);
                    }

                    String data = getField(extra, "data", "0");
                    List<Dialog> dialogs = new ArrayList<>();

                    _buy();

//                    Dialog dialog = DialogsTouCai.showLotteryListBuyConfirmDialog(
//                            Act,
//                            String.valueOf(listNumAddedCart.get(0).get(CtxLottery.Order.ISSUE).toString()),
//                            String.valueOf(hitCount),
//                            Nums.formatDecimal(money, 2),
//                            String.valueOf(listNumAddedCart.size()),
//                            data,
//                            new BaseAdapter() {
//                                @Override
//                                public int getCount() {
//                                    return listNumAddedCart.size();
//                                }
//
//                                @Override
//                                public Object getItem(int position) {
//                                    return listNumAddedCart.get(position);
//                                }
//
//                                @Override
//                                public long getItemId(int position) {
//                                    return 0;
//                                }
//
//                                @Override
//                                public View getView(int position, View convertView, ViewGroup parent) {
//                                    if (convertView == null)
//                                        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_lottery_list_buy_confirm_item, parent, false);
//
//                                    TextView method = convertView.findViewById(R.id.tv_lottery_method);
//                                    method.setText(String.valueOf(listNumAddedCart.get(position).get(CtxLottery.Order.PLAY_NAME)));
//                                    TextView count = convertView.findViewById(R.id.tv_lottery_count);
//                                    count.setText(String.format("@%d倍*%s注", listNumAddedCart.get(position).get(CtxLottery.Order.MULTIPLE), listNumAddedCart.get(position).get(CtxLottery.Order.HIT)));
//                                    TextView content = convertView.findViewById(R.id.tv_lottery_content);
//                                    content.setText(String.valueOf(listNumAddedCart.get(position).get(CtxLottery.Order.CONTENT)));
//                                    convertView.findViewById(R.id.ib_del).setVisibility(listNumAddedCart.size() > 1 ? View.VISIBLE : View.GONE);
//                                    convertView.findViewById(R.id.ib_del).setOnClickListener(v -> {
//                                        listNumAddedCart.remove(position);
//                                        notifyDataSetChanged();
//                                        adapter.notifyDataSetChanged();
//                                        updateBottomText();
//
//                                        double money = 0;
//                                        int hitCount = 0;
//                                        for (HashMap<String, Object> map : listNumAddedCart) {
//                                            money += (Double) map.get(CtxLottery.Order.MONEY);
//                                            hitCount += (Long) map.get(CtxLottery.Order.HIT);
//                                        }
//                                        ((TextView) dialogs.get(0).findViewById(R.id.tv_title2)).setText(
//                                                String.format("共计:¥%s元/%s注", Nums.formatDecimal(money, 2), String.valueOf(hitCount)));
//
//                                        if (listNumAddedCart.size() == 0 && dialogs.size() > 0)
//                                            dialogs.get(0).dismiss();
//                                    });
//                                    return convertView;
//                                }
//                            },
//                            () -> {
//                                _buy();
//                            });
//
//                    dialogs.add(dialog);
                } else {
                    Toasts.show(Act, "获取限红信息失败", false);
                }
                return true;
            }
        });
    }

    private void _buy() {
        if (listNumAddedCart == null || listNumAddedCart.size() == 0) {
            showCartEmptyDialog(ActLotteryCartTouCai.this);
            return;
        }

        HttpAction.addOrder(ActLotteryCartTouCai.this, listNumAddedCart, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", Double.TYPE);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActLotteryCartTouCai.this, "");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    DialogsTouCai.showLotteryBuyOkDialog(ActLotteryCartTouCai.this, () -> finish());

                    //删除所有添加的购物车注数
                    EventBus.getDefault().post(new DeleteFromCartEvent(true, null));

                    listNumAddedCart.clear();
                    adapter.notifyDataSetChanged();

                    //刷新用户资金
                    UserManager.getIns().setLotteryAvailableBalance((getField(extra, "data", 0.0)));
                    UserManager.getIns().sendBroadcaset(ActLotteryCartTouCai.this, UserManager.EVENT_USER_INFO_UNPDATED, null);

                    //清除选球数据
                    EventBus.getDefault().post(new ClearLotteryPlaySelectedEvent());

                    //刷新玩法页面记录
                    EventBus.getDefault().post(new BuyedLotteryEvent());

                    //刷新底部
                    updateBottomText();
                }else if (code == -1000) {
                    showTimerDialogs();
                } else {
                    if (Strs.isEqual("余额不足", msg)
                            || Strs.isEqual("没有足够的余额", msg)) {
                        DialogsTouCai.showBalanceNotEnough(ActLotteryCartTouCai.this);
                    } else {
                        DialogsTouCai.showDialog(ActLotteryCartTouCai.this, "温馨提醒", msg, null);
                    }
                }

                return true;
            }

            @Override
            public void onAfter(int id) {
                Dialogs.hideProgressDialog(ActLotteryCartTouCai.this);
            }
        });
    }

    private void showTimerDialogs() {
        Dialogs.showProgressDialog(this, "");
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                Dialogs.hideProgressDialog(ActLotteryCartTouCai.this);
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
            holder.item = item;
            Glide.with(ctx).load("file:///android_asset/lottery_kind/logo/" + lotteryId + ".png").into(holder.ivIcon);
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
            holder.cbDelete.setOnCheckedChangeListener(null);
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
            holder.minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int va = Strs.parse(holder.etTimes.getText().toString(), 0) - 1;
                    if (va < 1)
                        va = 1;
                    else if (va > 99999)
                        va = 99999;
                    holder.etTimes.setText(Strs.of(va));
                }
            });
            holder.plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int va = Strs.parse(holder.etTimes.getText().toString(), 0) + 1;
                    if (va < 1)
                        va = 1;
                    else if (va > 99999)
                        va = 99999;
                    holder.etTimes.setText(Strs.of(va));
                }
            });
            holder.etTimes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    int times = Strs.parse(holder.etTimes.getText().toString(), 0);
                    if (times < 1) {
                        times = 1;
                        holder.etTimes.setText(String.valueOf(times));
                        return;
                    } else if (times > 99999) {
                        times = 99999;
                        holder.etTimes.setText(String.valueOf(times));
                        return;
                    }
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
//            tags.add(item.get(CtxLottery.Order.HIT) + "注");
            tags.add(item.get(CtxLottery.Order.MULTIPLE) + "倍");
            tags.add(Nums.formatDecimal(item.get(CtxLottery.Order.MONEY), 3) + "元");
            holder.rvTags.setAdapter(new TagAdapter(tags));

        }

        public void updateBall(AddLotteryVH holder, HashMap<String, Object> item) {
            String content = item.get(CtxLottery.Order.CONTENT).toString();
//            List<String> balls = new ArrayList<>();
//            char[] b = content.replaceAll("[,\\s-√\\[\\]]", "").toCharArray();
//            for (char s : b) {
//                balls.add(String.valueOf(s));
//            }

            StringBuilder sb = new StringBuilder();
            for (char c : content.replace(",", "|").toCharArray()) {
                if (sb.length() > 0)
                    sb.append(" ");
                sb.append(c);
            }
            holder.tvBalls.setText(sb.toString());
        }

        protected class AddLotteryVH extends BaseViewHolder {
            private ImageView ivIcon;
            private TextView tvIssue;
            private RecyclerView rvTags;
            private TextView tvBalls;
            private CheckBox cbDelete;
            private View container;
            private View minus;
            private View plus;
            private EditText etTimes;
            private boolean showDelete = false;

            HashMap<String, Object> item;

            public AddLotteryVH(View view) {
                super(view);
                container = view.findViewById(R.id.layout_container);
                ivIcon = view.findViewById(R.id.iv_lottery_icon);
                tvIssue = (TextView) view.findViewById(R.id.tv_issue);
                rvTags = view.findViewById(R.id.rv_tags);
                cbDelete = view.findViewById(R.id.cb_delete);
                tvBalls = view.findViewById(R.id.tv_balls);
                minus = view.findViewById(R.id.tvTimeMinus);
                plus = view.findViewById(R.id.tvTimePlus);
                etTimes = view.findViewById(R.id.etTimeMulti);
                rvTags.setLayoutManager(new LinearLayoutManager(ActLotteryCartTouCai.this, LinearLayoutManager.HORIZONTAL, false));
                etTimes.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (item != null) {
                            int times = Strs.parse(s.toString(), 1);
                            int orignalTimes = (int) item.get(CtxLottery.Order.MULTIPLE);
                            double money = (double) item.get(CtxLottery.Order.MONEY);
                            money = money / orignalTimes * times;
                            item.put(CtxLottery.Order.MONEY, money);
                            item.put(CtxLottery.Order.MULTIPLE, times);
                            updateTags(AddLotteryVH.this, item);
                            updateBall(AddLotteryVH.this, item);
                            updateBottomText();
                        }
                    }
                });
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

    public void showCartEmptyDialog(Context context) {
        final Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_lottery_cart_empty, null);
        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        root.findViewById(R.id.btn_ok).setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.setContentView(root);
        dialog.show();
    }

}
