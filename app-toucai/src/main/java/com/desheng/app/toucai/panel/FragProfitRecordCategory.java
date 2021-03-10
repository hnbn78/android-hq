package com.desheng.app.toucai.panel;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Nums;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.model.LotteryProfitLoss;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.mypicker.DateUtil;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class FragProfitRecordCategory extends AbBaseFragment {
    Button btnProfitAmount;
    Button btnLossAmount;
    int queryType = 1;
    RecyclerView recyclerView;
    View profitHeader;
    View lossHeader;
    View emptyView;
    PieChart chart;
    String mDate;
    ViewGroup layoutAmount;
    private ProfitAdapter profitAdapter = new ProfitAdapter();
    private LossAdapter lossAdapter = new LossAdapter();
    private TextView mTotalMoney;

    @Override
    public int getLayoutId() {
        return R.layout.frag_profit_record;
    }

    @Override
    public void init(View root) {
        btnProfitAmount = root.findViewById(R.id.btn_profit_amount);
        btnLossAmount = root.findViewById(R.id.btn_loss_amount);
        recyclerView = root.findViewById(R.id.recycleView);
        profitHeader = root.findViewById(R.id.view_profit_header);
        lossHeader = root.findViewById(R.id.view_loss_header);
        chart = root.findViewById(R.id.chart_pie);
        emptyView = root.findViewById(R.id.emptyview);
        layoutAmount = root.findViewById(R.id.layout_amount);
        mTotalMoney = ((TextView) root.findViewById(R.id.totalMoney));

        initChart();

        btnProfitAmount.setOnClickListener(v -> {
            btnProfitAmount.setSelected(true);
            btnLossAmount.setSelected(false);
            queryType = 1;
            search(mDate);
        });

        btnLossAmount.setOnClickListener(v -> {
            btnProfitAmount.setSelected(false);
            btnLossAmount.setSelected(true);
            queryType = 2;
            search(mDate);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        //解决数据加载不完的问题
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

        btnProfitAmount.setSelected(true);
        mDate = DateUtil.getToday();
        search(mDate);
    }

    private void initChart() {
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setDragDecelerationFrictionCoef(0.95f);
//        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
//        chart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        chart.setCenterText(generateCenterSpannableText("0.00", queryType));
        chart.setCenterTextSize(21);
        chart.setCenterTextColor(0xFF333333);
        chart.setExtraOffsets(50.0f, 12.0f, 50.0f, 12.0f);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setRotationEnabled(false);
        chart.setHighlightPerTapEnabled(true);//点击是否放大
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(72f);
        chart.setTransparentCircleRadius(72f);
        chart.setDrawCenterText(true);
        chart.setRotationAngle(0);
        chart.setHighlightPerTapEnabled(false);
        chart.setEntryLabelColor(0xFF818181);
        chart.setEntryLabelTextSize(10);
        chart.setUsePercentValues(false);
        // enable rotation of the chart by touch
//        chart.setRotationEnabled(true);
//        chart.setHighlightPerTapEnabled(true);
        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);
        // add a selection listener
//        chart.setOnChartValueSelectedListener(this);
        //chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
    }

    public void search(String date) {
        int type = queryType;
        mDate = date;
        recyclerView.setAdapter(null);
        profitAdapter.data.clear();
        lossAdapter.data.clear();

        if (type == 1) {
            profitHeader.setVisibility(View.VISIBLE);
            lossHeader.setVisibility(View.GONE);
            recyclerView.setAdapter(profitAdapter);
        } else {
            profitHeader.setVisibility(View.GONE);
            lossHeader.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(lossAdapter);
        }

        HttpActionTouCai.getLotteryProfitLoss(this, date, type, new AbHttpResult() {

                    @Override
                    public void onBefore(Request request, int id, String host, String funcName) {
                        super.onBefore(request, id, host, funcName);
                        DialogsTouCai.showProgressDialog(getActivity(), "");
                    }

                    @Override
                    public void setupEntity(AbHttpRespEntity entity) {
                        entity.putField("data", LotteryProfitLoss.class);
                    }

                    @Override
                    public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                        if (code == 0 && error == 0) {
                            LotteryProfitLoss lotteryProfitLoss = getField(extra, "data", null);

                            if (lotteryProfitLoss != null
                                    && lotteryProfitLoss.getRecordMap() != null
                                    && lotteryProfitLoss.getRecordList() != null
                                    && lotteryProfitLoss.getRecordCount() >= 0) {
                                if (type == 1) {
                                    profitAdapter.data.clear();
                                    profitAdapter.data.addAll(lotteryProfitLoss.getRecordList());
                                    profitAdapter.notifyDataSetChanged();
                                } else {
                                    lossAdapter.data.clear();
                                    lossAdapter.data.addAll(lotteryProfitLoss.getRecordList());
                                    lossAdapter.notifyDataSetChanged();
                                }

                                mTotalMoney.setText(String.valueOf(lotteryProfitLoss.getRecordMap().getProfitAmount()));

                                setAmount(
                                        type,
                                        lotteryProfitLoss.getRecordMap().getAwardAmountSum(),
                                        lotteryProfitLoss.getRecordMap().getConfirmAmountSum(),
                                        lotteryProfitLoss.getRecordMap().getProfitAmountSum());

                                if (lotteryProfitLoss.getRecordCount() == 0) {
                                    emptyView.setVisibility(View.VISIBLE);
                                    chart.setVisibility(View.GONE);
                                } else {
                                    emptyView.setVisibility(View.GONE);
                                    chart.setVisibility(View.VISIBLE);

                                    List<PieEntry> data = new ArrayList<>();
                                    List<LotteryProfitLoss.RecordListBean> recordListForPieChart = lotteryProfitLoss.getRecordList();

                                    if (recordListForPieChart.size() <= 5) {
                                        for (LotteryProfitLoss.RecordListBean bean : recordListForPieChart) {
                                            data.add(new PieEntry((float) Math.abs(0.05 + bean.getDataAccountFor()), Nums.formatDecimal(bean.getDataAccountFor() * 100, 2) + "%" + bean.getLotteryName()));
                                        }
                                    } else {
                                        for (int i = 0; i < 5; i++) {
                                            data.add(new PieEntry((float) Math.abs(0.05 + recordListForPieChart.get(i).getDataAccountFor()),
                                                    Nums.formatDecimal(recordListForPieChart.get(i).getDataAccountFor() * 100, 2) + "%" + recordListForPieChart.get(i).getLotteryName()));
                                        }
                                        data.add(new PieEntry((float) Math.abs(0.05 + Double.parseDouble(lotteryProfitLoss.getDataAccountFor().getDataAccountFor())),
                                                Nums.formatDecimal(Double.parseDouble(lotteryProfitLoss.getDataAccountFor().getDataAccountFor()) * 100, 2) + "%" + lotteryProfitLoss.getDataAccountFor().getLotteryName()));
                                    }

                                    PieDataSet dataSet = new PieDataSet(data, "Label");

                                    ArrayList<Integer> colors = new ArrayList<Integer>();
                                    colors.add(getResources().getColor(R.color.profit_chart_1));
                                    colors.add(getResources().getColor(R.color.profit_chart_2));
                                    colors.add(getResources().getColor(R.color.profit_chart_3));
                                    colors.add(getResources().getColor(R.color.profit_chart_4));
                                    colors.add(getResources().getColor(R.color.profit_chart_5));
//                                    colors.add(getResources().getColor(R.color.profit_chart_6));
//                                    colors.add(getResources().getColor(R.color.profit_chart_7));
//                                    colors.add(getResources().getColor(R.color.profit_chart_8));
//                                    colors.add(getResources().getColor(R.color.profit_chart_9));
//                                    colors.add(getResources().getColor(R.color.profit_chart_10));
                                    colors.add(getResources().getColor(R.color.profit_chart_other));
                                    dataSet.setColors(colors);
                                    dataSet.setSelectionShift(0f);
                                    dataSet.setValueLinePart1OffsetPercentage(80.f);
                                    dataSet.setValueLinePart1Length(0.2f);
                                    dataSet.setValueLinePart2Length(0.25f);
                                    dataSet.setValueLineColor(0xffaaaaaa);
                                    //dataSet.setUsingSliceColorAsValueLineColor(true);
                                    dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                                    dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                                    PieData pieData = new PieData(dataSet);
                                    pieData.setValueFormatter(new PercentFormatter());
                                    pieData.setDrawValues(false);
//                                    pieData.setValueTextSize(12f);
//                                    pieData.setValueTextColor(Color.BLACK);
                                    chart.setData(pieData);
                                    chart.setCenterText(generateCenterSpannableText(Nums.formatDecimal(lotteryProfitLoss.getRecordMap().getProfitAmountSum(), 2), type));
                                    chart.invalidate();
                                }
                            } else {
                                emptyView.setVisibility(View.VISIBLE);
                                chart.setVisibility(View.GONE);
                                chart.setData(null);
                                chart.setCenterText(generateCenterSpannableText("0.00", type));
                                setAmount(type, 0, 0, 0);
                            }
                        }
                        return true;
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        DialogsTouCai.hideProgressDialog(getActivity());
                    }
                }
        );
    }

    private void setAmount(int type, double award, double confirm, double profit) {
        layoutAmount.removeAllViews();

        if (type == 1) {
            View inflate = getLayoutInflater().inflate(R.layout.item_profit_record_list, layoutAmount, false);
            inflate.setBackgroundResource(R.drawable.bg_profit_item_bottom);
            layoutAmount.addView(inflate);
        } else {
            View inflate = getLayoutInflater().inflate(R.layout.item_loss_record_list, layoutAmount, false);
            inflate.setBackgroundResource(R.drawable.bg_profit_item_bottom);
            layoutAmount.addView(inflate);
        }

        ((TextView) layoutAmount.findViewById(R.id.tv_lottery_name)).setText("总计");
        ((TextView) layoutAmount.findViewById(R.id.tv_valid_bet_amount)).setText(Nums.formatDecimal(confirm, 2));
        ((TextView) layoutAmount.findViewById(R.id.tv_award_amount)).setText(Nums.formatDecimal(award, 2));
        ((TextView) layoutAmount.findViewById(R.id.tv_profit_amount)).setText((type == 1 ? "+" : "") + Nums.formatDecimal(profit, 2));
    }

    private SpannableString generateCenterSpannableText(String amount, int tp) {

        SpannableString s = new SpannableString(amount + "\n\n- " + "盈利总额" + " -");
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length() - 9, 0);
        s.setSpan(new RelativeSizeSpan(.5f), s.length() - 9, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(0xff949494), s.length() - 9, s.length(), 0);
        return s;
    }

    class ProfitAdapter extends RecyclerView.Adapter<ProfitAdapter.ProfitViewHolder> {
        private List<LotteryProfitLoss.RecordListBean> data = new ArrayList<>(0);

        @Override
        public ProfitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profit_record_list, parent, false);
            return new ProfitViewHolder(root);
        }

        @Override
        public void onBindViewHolder(ProfitViewHolder holder, int position) {
            LotteryProfitLoss.RecordListBean bean = data.get(position);
            holder.name.setText(bean.getLotteryName());
            holder.profitAmount.setText("+" + Nums.formatDecimal(bean.getProfitAmountSum(), 2));
            holder.awardAmount.setText(Nums.formatDecimal(bean.getAwardAmountSum(), 2));
            holder.validbetAmount.setText(Nums.formatDecimal(bean.getConfirmAmountSum(), 2));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ProfitViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView validbetAmount;
            TextView awardAmount;
            TextView profitAmount;

            public ProfitViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.tv_lottery_name);
                validbetAmount = itemView.findViewById(R.id.tv_valid_bet_amount);
                awardAmount = itemView.findViewById(R.id.tv_award_amount);
                profitAmount = itemView.findViewById(R.id.tv_profit_amount);
            }
        }
    }

    class LossAdapter extends RecyclerView.Adapter<LossAdapter.LossViewHolder> {
        private List<LotteryProfitLoss.RecordListBean> data = new ArrayList<>();

        @Override
        public LossViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loss_record_list, parent, false);
            return new LossViewHolder(root);
        }

        @Override
        public void onBindViewHolder(LossViewHolder holder, int position) {
            LotteryProfitLoss.RecordListBean bean = data.get(position);
            holder.name.setText(bean.getLotteryName());
            holder.profitAmount.setText(Nums.formatDecimal(bean.getProfitAmountSum(), 2));
            holder.awardAmount.setText(Nums.formatDecimal(bean.getAwardAmountSum(), 2));
            holder.validbetAmount.setText(Nums.formatDecimal(bean.getConfirmAmountSum(), 2));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class LossViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView validbetAmount;
            TextView awardAmount;
            TextView profitAmount;

            public LossViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.tv_lottery_name);
                validbetAmount = itemView.findViewById(R.id.tv_valid_bet_amount);
                awardAmount = itemView.findViewById(R.id.tv_award_amount);
                profitAmount = itemView.findViewById(R.id.tv_profit_amount);
            }
        }
    }

}
