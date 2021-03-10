package com.desheng.base.panel;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Toasts;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.adapter.MessageAdapter;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.model.MessageBean;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 2018/3/13.
 */

public class ActBroadList extends AbAdvanceActivity {

    public static void launch(Activity ctx) {
        simpleLaunch((Activity) ctx, ActBroadList.class);
    }

    public static void launcher(Activity ctx, String title) {
        Intent intent = new Intent(ctx, ActBroadList.class);
        intent.putExtra("title_key", title);
        ctx.startActivity(intent);
    }

    private MessageAdapter messageAdapter;
    int page;
    int size;
    private ListView listView;
    private SwipeRefreshLayout srlRefresh;
    public List<MessageBean> messageBeanArrayList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.act_message_main;
    }

    @Override
    protected void init() {
        String title = getIntent().getStringExtra("title_key");
        if (TextUtils.isEmpty(title))
            title = "公告";
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), title);
        setStatusBarTranslucentAndLightContentWithPadding();
        initView();
        getMessage();

    }

    private void initView() {
        listView = findViewById(R.id.listview);
        srlRefresh = findViewById(R.id.srlRefresh);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String content = CtxLottery.transHtmlFrag(messageBeanArrayList.get(position).getContent());
                ActWeb.launch(ActBroadList.this, messageBeanArrayList.get(position).getTitle()
                        , content);
            }
        });
        srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                messageBeanArrayList.clear();
                getMessage();
            }
        });
    }


    public void getMessage() {
        HttpAction.getMessageCenter(ActBroadList.this, size, page, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<MessageBean>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    List<MessageBean> list = getField(extra, "data", null);
                    messageBeanArrayList.addAll(list);
                    if (messageAdapter == null) {
                        messageAdapter = new MessageAdapter(ActBroadList.this,
                                messageBeanArrayList);
                        listView.setAdapter(messageAdapter);
                    } else {
                        messageAdapter.notifyDataSetChanged();
                    }
                } else if (code != 0 || error != 0) { // 出错才显示msg
                    Toasts.show(ActBroadList.this, msg);
                }

                return true;
            }

            @Override
            public void onAfter(int id) {
                srlRefresh.setRefreshing(false);
            }
        });


    }


}
