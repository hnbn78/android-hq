package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Toasts;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.adapter.UpdateContentAdapter;
import com.desheng.app.toucai.model.UpdateContentList;
import com.desheng.base.context.CtxLottery;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.mypicker.DateUtil;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 2018/3/13.
 */

public class ActUpdateContentList extends AbAdvanceActivity {

    public static void launch(Activity ctx) {
        simpleLaunch((Activity) ctx, ActUpdateContentList.class);
    }

    public static void launcher(Activity ctx) {
        Intent intent = new Intent(ctx, ActUpdateContentList.class);
        ctx.startActivity(intent);
    }

    private UpdateContentAdapter messageAdapter;
    int page;
    int size;
    private ListView listView;
    private SwipeRefreshLayout srlRefresh;
    public List<UpdateContentList.UpdateContent> messageBeanArrayList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.act_update_content_list;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "功能介绍");
        setStatusBarTranslucentAndLightContentWithPadding();
        initView();
        getContent();

    }

    private void initView() {
        listView = findViewById(R.id.listview);
        srlRefresh = findViewById(R.id.srlRefresh);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String content = CtxLottery.transHtmlFrag(messageBeanArrayList.get(position).getContent());
                ActUpdateContentDetail.launch(
                        ActUpdateContentList.this,
                        DateUtil.formatDate(messageBeanArrayList.get(position).getCreateTime(), DateUtil.ymdhms),
                        messageBeanArrayList.get(position).getTitle(),
                        content);
            }
        });
        srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                messageBeanArrayList.clear();
                getContent();
            }
        });
    }


    public void getContent() {
        HttpActionTouCai.getUpdateContentList(ActUpdateContentList.this, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<UpdateContentList>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    UpdateContentList list = getField(extra, "data", null);

                    if (list == null)
                        return true;

                    List<UpdateContentList.UpdateContent> contents = list.getList();
                    messageBeanArrayList.addAll(contents);
                    if (messageAdapter == null) {
                        messageAdapter = new UpdateContentAdapter(ActUpdateContentList.this,
                                messageBeanArrayList);
                        listView.setAdapter(messageAdapter);
                    } else {
                        messageAdapter.notifyDataSetChanged();
                    }
                } else if (code != 0 || error != 0) { // 出错才显示msg
                    Toasts.show(ActUpdateContentList.this, msg);
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
