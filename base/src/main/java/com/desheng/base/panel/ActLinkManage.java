package com.desheng.base.panel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.global.Config;
import com.ab.global.ENV;
import com.ab.util.Nums;
import com.desheng.base.global.BaseConfig;
import com.pearl.act.util.UIHelper;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.ClipboardUtils;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.model.LinkMangeBean;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.view.SpaceTopDecoration;
import com.pearl.webview.utils.X5WebView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * 链接管理
 */
public class ActLinkManage extends AbAdvanceActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout srlRefresh;
    private RecyclerView rvLink;

    private LinkManagerAdapter adapter;
    private LinkMangeBean linkMangeBean;
    private List<LinkMangeBean.ListBean> listBeanList;
    private int page = 0;
    private int pageSize = 10;

    public static void launch(Activity context) {
        simpleLaunch(context, ActLinkManage.class);
    }

    @Override
    protected void init() {

        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "链接管理");
        setStatusBarTranslucentAndLightContentWithPadding();

        srlRefresh = (SwipeRefreshLayout) findViewById(R.id.srlRefresh);
        srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);

        rvLink = (RecyclerView) findViewById(R.id.rvLink);
        rvLink.setLayoutManager(Views.genLinearLayoutManagerV(ActLinkManage.this));
        rvLink.addItemDecoration(new SpaceTopDecoration(7));

        getLinkList();

    }

    @Override
    public int getLayoutId() {
        return R.layout.act_link_manage;
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        getLinkList();
    }

    private void getLinkList() {

        HttpAction.linkManage(null, page, pageSize, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {

                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(true);
                    }
                }, 100);
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", LinkMangeBean.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    linkMangeBean = getField(extra, "data", null);
                    listBeanList = linkMangeBean.getList();

                    adapter = new LinkManagerAdapter(ActLinkManage.this, listBeanList);
//                    View headView= LayoutInflater.from(ActLinkManage.this).inflate(R.layout.item_link_manage_headview,null);
//                    adapter.addHeaderView(headView);
                    rvLink.setAdapter(adapter);
                }

                return true;
            }

            @Override
            public void onAfter(int id) {

            }

            @Override
            public void onFinish() {
                super.onFinish();
                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(false);
                    }
                }, 600);
            }
        });
    }

    protected class LinkManagerAdapter extends BaseQuickAdapter<LinkMangeBean.ListBean, LinkManagerAdapter.ViewHolder> {

        private Context ctx;

        public LinkManagerAdapter(Context ctx, List<LinkMangeBean.ListBean> data) {
            super(R.layout.item_link_manage, data);
            this.ctx = ctx;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int positions) {
            super.onBindViewHolder(holder, positions);

//            if(positions==0){
//                holder.layout_title.setVisibility(View.VISIBLE);
//            }else {
//                holder.layout_title.setVisibility(View.GONE);
//            }
        }

        @Override
        protected void convert(final ViewHolder viewHolder, final LinkMangeBean.ListBean item) {
            String link = item.getCode();
            //TODO 上线前必须改这里
            if(Config.custom_flag.equals(BaseConfig.FLAG_MENGXIANG)){
                link = item.getCode().replace("yx/rgv","register");
            }
            viewHolder.tv_register_code.setText(link);
            if (Config.custom_flag.equals(BaseConfig.FLAG_HETIANXIA)) {
                viewHolder.tv_back_point.setText("" + Nums.formatDecimal(item.getPoint().add(new BigDecimal(Integer.toString(5))),1));
            }else if( Config.custom_flag.equals(BaseConfig.FLAG_JINDU)){
                viewHolder.tv_back_point.setText("" + Nums.formatDecimal(item.getPoint().add(new BigDecimal(Integer.toString(10))),1));
            } else {
                viewHolder.tv_back_point.setText("" + Nums.formatDecimal(item.getPoint(),1));
            }

            if (item.getType() == 1) {
                viewHolder.tv_user_type.setText("代理");
            } else {
                viewHolder.tv_user_type.setText("会员");
            }

            if (Strs.isEmpty(item.getExpireTime())) {
                viewHolder.tv_over_date.setText("永不过期");
            } else {
                viewHolder.tv_over_date.setText(item.getExpireTime());
            }

            viewHolder.tv_copy_link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String data=viewHolder.tv_register_code.getText().toString();
                    if(data.endsWith(".html")){
                        data = data.replace(".html", "");
                    }

                    ClipboardUtils.copyText(ActLinkManage.this, ENV.curr.host + data);
                    Toasts.show(ActLinkManage.this,"复制到剪切板");
                }
            });

            viewHolder.tv_register_code.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String data=viewHolder.tv_register_code.getText().toString();
                    if(data.endsWith(".html")){
                        data = data.replace(".html", "");
                    }
                    ActWebX5.launchOutside(ActLinkManage.this,ENV.curr.host + data);
                }
            });
            viewHolder.tv_delete_link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ActLinkManage) ctx).deleteLink("" + item.getId());
                }
            });
        }

        public class ViewHolder extends BaseViewHolder {
//            LinearLayout layout_title;
            TextView tv_register_code;
            TextView tv_user_type;
            TextView tv_back_point;
            TextView tv_over_date;
            TextView tv_copy_link;
            TextView tv_delete_link;

            public ViewHolder(View view) {
                super(view);
//                layout_title =(LinearLayout) view.findViewById(R.id.layout_title);
                tv_register_code = (TextView) view.findViewById(R.id.tv_register_code);
                tv_user_type = (TextView) view.findViewById(R.id.tv_user_type);
                tv_back_point = (TextView) view.findViewById(R.id.tv_back_point);
                tv_over_date = (TextView) view.findViewById(R.id.tv_over_date);
                tv_copy_link = (TextView) view.findViewById(R.id.tv_copy_link);
                tv_delete_link = (TextView) view.findViewById(R.id.tv_delete_link);
            }
        }


    }

    public void deleteLink(String id) {
        HttpAction.deleteLink(null, id, new AbHttpResult() {

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                Toasts.show(ActLinkManage.this,msg);
                getLinkList();
                return true;

            }

            @Override
            public boolean onGetString(String str) {
                return super.onGetString(str);
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(ActLinkManage.this,content);
                return true;
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }


}
