package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ab.http.AbHttpAO;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Strs;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.global.App;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.ContactsBackMsgMode;
import com.desheng.app.toucai.model.ContactsMode;
import com.desheng.app.toucai.model.LastMessageImContactsMode;
import com.desheng.app.toucai.model.LatestMsgComeRefreshMode;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.util.Utils;
import com.desheng.base.manager.UserManager;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ActContacts extends AbAdvanceActivity {
    public static final int TYPE_LEVEL_0 = 1;
    public static final int TYPE_PERSON = 2;
    private TextView tv_title;
    private RecyclerView contactsRecylerview;
    private ArrayList<MultiItemEntity> list = new ArrayList<>();
    private int userOpenItem;

    public static void launch(Activity act) {
        simpleLaunch(act, ActContacts.class);
    }

    @Override
    public void init() {
        hideToolbar();
        View headRoot = findViewById(R.id.vgToolbarGroup);
        paddingHeadOfStatusHeight(headRoot);
        setStatusBarTranslucentAndLightContentWithPadding();
        tv_title = ((TextView) headRoot.findViewById(R.id.tv_title));
        tv_title.setText("联系人");
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_contacts;
    }

    public void initView() {
        EventBus.getDefault().register(this);
        contactsRecylerview = ((RecyclerView) findViewById(R.id.contactsList));
        contactsRecylerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new myAdapter(list);
        contactsRecylerview.setAdapter(mAdapter);
        initStatistics(true, null);
    }

    myAdapter mAdapter;
    boolean shangjiStatus = false;
    boolean xiajiStatus = false;

    class myAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

        public myAdapter(List<MultiItemEntity> data) {
            super(data);
            addItemType(TYPE_LEVEL_0, R.layout.item_expandable_lv0);
            addItemType(TYPE_PERSON, R.layout.item_text_view);
        }

        @Override
        protected void convert(final BaseViewHolder holder, MultiItemEntity item) {
            switch (holder.getItemViewType()) {
                case TYPE_LEVEL_0:
                    final Level0Item lv0 = (Level0Item) item;
                    holder.setText(R.id.title, lv0.title)
                            .setText(R.id.sub_title, lv0.subTitle)
                            .setImageResource(R.id.iv, lv0.isExpanded() ? R.mipmap.ic_line_act : R.mipmap.ic_line);

                    //刷新时保持类别开合状态
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userOpenItem = holder.getAdapterPosition();
                            if (lv0.isExpanded()) {
                                collapse(userOpenItem);
                            } else {
                                expand(userOpenItem);
                            }
                        }
                    });

                    break;
                case TYPE_PERSON:
                    final ContactsMode person = (ContactsMode) item;
                    holder.setText(R.id.tv, person.getIsKefu() == 1 ? "专线" : (person.getIsParent() == 1 ? "我的上级" : person.getCn()));
                    holder.setText(R.id.time, person.getTime());
                    holder.getView(R.id.time).setVisibility(Strs.isEmpty(person.getTime()) ? View.GONE : View.VISIBLE);
                    holder.setText(R.id.msgNotRead, String.valueOf(person.getUnReadCount()));
                    holder.getView(R.id.msgNotRead).setVisibility(person.getUnReadCount() > 0 ? View.VISIBLE : View.GONE);

                    if (Strs.isEmpty(person.getLastmsg())) {
                        holder.getView(R.id.statusOnline).setVisibility(View.VISIBLE);
                        holder.setText(R.id.statusOnline, person.isOnline() ? "在线" : "离线请留言");
                        holder.getView(R.id.newMsg).setVisibility(View.GONE);
                    } else {
                        holder.getView(R.id.statusOnline).setVisibility(View.GONE);
                        holder.getView(R.id.newMsg).setVisibility(View.VISIBLE);
                        holder.setText(R.id.newMsg, person.getLastmsg());
                    }

                    ((TextView) holder.getView(R.id.statusOnline))
                            .setTextColor(person.isOnline() ? getResources().getColor(R.color.blue_2396f7)
                                    : getResources().getColor(R.color.gray));
                    if (person.getIsParent() == 1 && person.getIsKefu() == 0) {
                        holder.setImageResource(R.id.iv_icon, person.isOnline() ? R.mipmap.icon_shangji_online : R.mipmap.icon_shangji_offline);
                    } else if (person.getIsParent() == 0 && person.getIsKefu() == 0) {
                        holder.setImageResource(R.id.iv_icon, person.isOnline() ? R.mipmap.icon_xiaji_online : R.mipmap.icon_xiaji_offline);
                    } else if (person.getIsKefu() == 1) {
                        holder.setImageResource(R.id.iv_icon, person.isOnline() ? R.mipmap.ic_superior : R.mipmap.ic_superior_2);
                    }

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActTalk.launch(ActContacts.this, person);
                        }
                    });

                    break;
            }
        }
    }

    List<ContactsMode> contactslist = new ArrayList<>();

    /**
     * d
     *
     * @param isMsgCome 用于区分有消息来时，这时候自动刷新列表不显示进度
     */
    public void initStatistics(boolean isMsgCome, ContactsBackMsgMode backmessageInf) {
        HttpActionTouCai.getcontactsList(this, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                if (isMsgCome) {
                    DialogsTouCai.showProgressDialog(ActContacts.this, "");
                }
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<List<ContactsMode>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    contactslist = getField(extra, "data", null);
                    UserManagerTouCai.getIns().setNotReadMsgPool(contactslist);
                    getLastMsgInList(contactslist, backmessageInf);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                if (isMsgCome) {
                    DialogsTouCai.hideProgressDialog(ActContacts.this);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (isMsgCome) {
                    DialogsTouCai.hideProgressDialog(ActContacts.this);
                }
            }
        });
    }

    private ArrayList<MultiItemEntity> generateData(List<ContactsMode> datas) {

        int personCountshangjiOnLine = 0;
        int personCountxiajiaOnLine = 0;
        int personCountKefuOnLine = 0;

        List<ContactsMode> listShangji = new ArrayList<>();
        List<ContactsMode> listxiaji = new ArrayList<>();
        List<ContactsMode> kefu = new ArrayList<>();

        for (ContactsMode data : datas) {
            if (data.getIsKefu() == 1) {
                if (data.isOnline()) {//需要调整顺序，在线的在前面
                    kefu.add(0, data);
                    personCountKefuOnLine++;
                } else {
                    kefu.add(data);
                }
            } else {
                if (data.getIsParent() == 1) {
                    if (data.isOnline()) {//需要调整顺序，在线的在前面
                        listShangji.add(0, data);
                        personCountshangjiOnLine++;
                    } else {
                        listShangji.add(data);
                    }
                } else {
                    if (data.isOnline()) {//需要调整顺序，在线的在前面
                        listxiaji.add(0, data);
                        personCountxiajiaOnLine++;
                    } else {
                        listxiaji.add(data);
                    }
                }
            }
        }

        ArrayList<MultiItemEntity> res = new ArrayList<>();

        Level0Item lv0 = new Level0Item("专线", "( " + personCountKefuOnLine + " / " + kefu.size() + " )");
        for (ContactsMode contactsMode : kefu) {
            lv0.addSubItem(contactsMode);
        }
        if (kefu.size() > 0) {
            res.add(lv0);
        }

        Level0Item lv1 = new Level0Item("我的上级", "( " + personCountshangjiOnLine + " / " + listShangji.size() + " )");
        for (ContactsMode contactsMode : listShangji) {
            lv1.addSubItem(contactsMode);
        }
        res.add(lv1);

        Level0Item lv2 = new Level0Item("我的下级", "( " + personCountxiajiaOnLine + " / " + listxiaji.size() + " )");
        for (ContactsMode contactsMode : listxiaji) {
            lv2.addSubItem(contactsMode);
        }
        res.add(lv2);

        return res;
    }

    class Level0Item extends AbstractExpandableItem<ContactsMode> implements MultiItemEntity {
        public String title;
        public String subTitle;

        public Level0Item(String title, String subTitle) {
            this.subTitle = subTitle;
            this.title = title;
        }

        @Override
        public int getItemType() {
            return TYPE_LEVEL_0;
        }

        @Override
        public int getLevel() {
            return 0;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //有新的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEventBus(ContactsBackMsgMode backmessageInfo) {
        if (backmessageInfo == null) {
            return;
        }
        getLastMsgInList(backmessageInfo);
    }

    private void getLastMsgInList(List<ContactsMode> modeList, ContactsBackMsgMode backmessageInf) {
        StringBuffer buffer = new StringBuffer();
        //拼接inviteCode
        for (ContactsMode mode : modeList) {
            buffer.append(mode.getInviteCode()).append(",");
        }

        if (modeList.size() > 0) {
            buffer.subSequence(0, buffer.length() - 1);
        }

        HttpActionTouCai.getLastMessage(this, buffer.toString(), new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {

            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<List<LastMessageImContactsMode>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    List<LastMessageImContactsMode> lastMsglists = getField(extra, "data", null);

                    for (ContactsMode contactsMode : modeList) {
                        if (backmessageInf != null && Strs.isEqual(contactsMode.getInviteCode(), backmessageInf.getSendInviteCode())) {
                            contactsMode.setTime(Utils.getDate(backmessageInf.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));

                        }
                    }

                    for (LastMessageImContactsMode lastMsgbean : lastMsglists) {
                        for (ContactsMode contactsMode : modeList) {
                            if (Strs.isEqual(lastMsgbean.getInviteCode(), contactsMode.getInviteCode())) {
                                if (lastMsgbean != null && lastMsgbean.getLastMessage() != null) {
                                    if (lastMsgbean.getLastMessage() != null && lastMsgbean.getLastMessage().getMsgType() == 1) {
                                        contactsMode.setLastmsg("图片消息");
                                    } else {
                                        contactsMode.setLastmsg(lastMsgbean.getLastMessage().getText());
                                    }
                                    contactsMode.setTime(lastMsgbean.getLastMessage().getCreateTime());
                                }
                            }
                        }
                    }

                    if (list != null) {
                        list.clear();
                        list.addAll(generateData(modeList));
                    }

                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                mAdapter.expand(userOpenItem);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    private void getLastMsgInList(ContactsBackMsgMode backmessageInf) {
        for (ContactsMode contactsMode : contactslist) {
            if (Strs.isEqual(contactsMode.getInviteCode(), backmessageInf.getSendInviteCode())) {
                LatestMsgComeRefreshMode latestMsgComeRefreshMode = UserManagerTouCai.getIns().getNotReadMsgPool(backmessageInf.getSendInviteCode());
                if (latestMsgComeRefreshMode != null) {
                    int notReadMsgPool = latestMsgComeRefreshMode.getNewMsgNotReadCount();
                    Log.d("ActContacts", "notReadMsgPool:" + notReadMsgPool);
                    contactsMode.setUnReadCount(notReadMsgPool);
                    contactsMode.setLastmsg(latestMsgComeRefreshMode.getText());
                    contactsMode.setTime(Utils.getDate(latestMsgComeRefreshMode.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                    //contactsMode.setLastmsg();
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initStatistics(true, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!UserManager.getIns().isLogined()) {
            UserManager.getIns().redirectToLogin();
        }
    }
}
