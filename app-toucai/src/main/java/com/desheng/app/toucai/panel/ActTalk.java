package com.desheng.app.toucai.panel;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.global.ENV;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.MD5;
import com.ab.util.Strs;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.adapter.ChatAdapter;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.global.App;
import com.desheng.app.toucai.helper.EmotionInputDetector;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.ContactsBackMsgMode;
import com.desheng.app.toucai.model.ContactsHistoryMode;
import com.desheng.app.toucai.model.ContactsMessageMode;
import com.desheng.app.toucai.model.ContactsMode;
import com.desheng.app.toucai.model.MessageBackInfo;
import com.desheng.app.toucai.model.MessageInfo;
import com.desheng.app.toucai.model.MsgReadHuizhiMode;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.util.GlobalOnItemClickManagerUtils;
import com.desheng.app.toucai.util.ImageUtil;
import com.desheng.app.toucai.util.Utils;
import com.desheng.app.toucai.view.AndroidBug5497Workaround;
import com.desheng.app.toucai.view.NoScrollViewPager;
import com.desheng.app.toucai.view.expendview.ExpandableLayoutListView;
import com.desheng.base.action.HttpAction;
import com.desheng.base.global.BaseConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.nanchen.compresshelper.CompressHelper;
import com.pearl.act.base.AbBaseActivity;
import com.pearl.act.util.StatusBarHelper;
import com.shark.tc.R;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.functions.Consumer;
import io.socket.client.Ack;
import io.socket.client.Socket;
import okhttp3.Request;

public class ActTalk extends AbBaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_CHOOSE = 22;
    EasyRecyclerView chatList;
    NoScrollViewPager viewpager;
    EditText editText;
    ImageView emotionSend;
    ImageView emotionVoice;
    ImageView imageButton;
    TextView voiceText, btnSendImageMsg, btnCancel;
    ImageView emotionAdd;
    RelativeLayout emotionLayout;
    ImageView emotionButton;
    int msgIndex = 0;
    private TextView tv_title;
    private ImageView iv;
    private ExpandableLayoutListView listview;
    private ArrayList<Fragment> fragments;
    private ChatAdapter chatAdapter;
    private LinearLayoutManager layoutManager;
    private EmotionInputDetector mDetector;
    private List<MessageInfo> messageInfos;
    private ImageView gotolist;
    private ContactsMode mContactsMode;
    private TextView statusOnline;
    private View statusBar;
    private Gson gson;
    private int reconectCishu = 0;
    private int reBuildConectCishu = 0;
    private RxPermissions rxPermissions;
    private RecyclerView imageRc;
    private RelativeLayout imageLayout;
    private LinearLayout llSendTextMsg;
    private RelativeLayout flSeePhoto;
    private ImageView photoview;
    private ImageView ivcancel;

    public static void launch(Activity act, ContactsMode contactsMode) {
        Intent itt = new Intent(act, ActTalk.class);
        itt.putExtra("ContactsMode", contactsMode);
        act.startActivity(itt);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.act_talk);
        //沉浸式状态栏与EditText冲突问题
        AndroidBug5497Workaround.assistActivity(findViewById(android.R.id.content));
        gson = new Gson();
        initView();
    }

    private void initView() {
        EventBus.getDefault().register(this);
        rxPermissions = new RxPermissions(this);
        tv_title = ((TextView) findViewById(R.id.tv_title));
        statusOnline = ((TextView) findViewById(R.id.statusOnline));
        statusBar = ((View) findViewById(R.id.statusBar));
        flSeePhoto = ((RelativeLayout) findViewById(R.id.flSeePhoto));
        photoview = ((ImageView) findViewById(R.id.photoview));
        ivcancel = ((ImageView) findViewById(R.id.ivcancel));
        ViewGroup.LayoutParams layoutParams = statusBar.getLayoutParams();
        layoutParams.height = StatusBarHelper.getStatusBarHeight(this);
        statusBar.setLayoutParams(layoutParams);

        mContactsMode = ((ContactsMode) getIntent().getParcelableExtra("ContactsMode"));
        if (mContactsMode != null) {
            tv_title.setText(mContactsMode.getIsKefu() == 1 ? "专线" : (mContactsMode.getIsParent() == 1 ? "我的上级" : mContactsMode.getCn()));
            statusOnline.setText(mContactsMode.isOnline() ? "在线" : "离线");
        }

        chatList = ((EasyRecyclerView) findViewById(R.id.chat_list));
        viewpager = (NoScrollViewPager) findViewById(R.id.viewpager);
        editText = (EditText) findViewById(R.id.edit_text);
        emotionSend = (ImageView) findViewById(R.id.emotion_send);
        imageButton = (ImageView) findViewById(R.id.image_button);
        imageLayout = (RelativeLayout) findViewById(R.id.image_layout);
        imageRc = (RecyclerView) findViewById(R.id.imageRc);
        emotionVoice = (ImageView) findViewById(R.id.emotion_voice);
        btnSendImageMsg = (TextView) findViewById(R.id.btnSendImageMsg);
        btnCancel = (TextView) findViewById(R.id.btnCancel);
        voiceText = (TextView) findViewById(R.id.voice_text);
        emotionAdd = (ImageView) findViewById(R.id.emotion_add);
        emotionLayout = (RelativeLayout) findViewById(R.id.emotion_layout);
        emotionButton = (ImageView) findViewById(R.id.emotion_button);
        llSendTextMsg = (LinearLayout) findViewById(R.id.llSendTextMsg);

        imageButton.setVisibility(mContactsMode.getIsKefu() == 1 ? View.VISIBLE : View.GONE);

        imageButton.setOnClickListener(this::onClick);
        btnSendImageMsg.setOnClickListener(this::onClick);
        btnCancel.setOnClickListener(this::onClick);
        flSeePhoto.setOnClickListener(this::onClick);
        ivcancel.setOnClickListener(this::onClick);
        mDetector = EmotionInputDetector.with(this)
                .setEmotionView(emotionLayout)
                .setViewPager(viewpager)
                .bindToContent(chatList)
                .bindToEditText(editText)
                .bindToEmotionButton(emotionButton)
                .bindToAddButton(emotionAdd)
                .bindToSendButton(emotionSend)
                .bindToVoiceButton(emotionVoice)
                .bindToVoiceText(voiceText)
                .bindToPerson(mContactsMode)//绑定用户
                .build();

        GlobalOnItemClickManagerUtils globalOnItemClickListener = GlobalOnItemClickManagerUtils.getInstance(this);
        globalOnItemClickListener.attachToEditText(editText);

        chatAdapter = new ChatAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatList.setLayoutManager(layoutManager);
        chatList.setAdapter(chatAdapter);
        chatList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        chatAdapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        mDetector.hideEmotionLayout(false);
                        mDetector.hideSoftInput();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        //chatAdapter.addItemClickListener(itemClickListener);
        LoadData();

        getTalkHistory(msgIndex);

        chatList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTalkHistory(getTalkWindowisHuizhiMsgCount());//聊天窗总共有多少条,  直接就用条数作为下标去取(注意去掉未发送成功的消息数)
            }
        });

        ((ImageButton) findViewById(R.id.ibLeftBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDetector.setiOnSendNewMessage(new EmotionInputDetector.IOnSendNewMessage() {
            @Override
            public void onSendNewMessage(MessageInfo messageInfo) {
                if (messageInfo == null) {
                    return;
                }
                //拿到消息先更新界面
                MessageEventBus(messageInfo);
                //组装json,发送给服务端
                String msgjson = gson.toJson(new ContactsMessageMode(String.valueOf(System.currentTimeMillis()), messageInfo.getInviteCode(),
                        messageInfo.getToParent(), 0, messageInfo.getContent()));
                Log.e("acttalk", "消息准备完毕：---->准备发送服务器时间：" +
                        Utils.getDate(System.currentTimeMillis(), "MM月dd日 HH:mm:ss") + "\n---内容：" + msgjson);
                try {
                    JSONObject obj = new JSONObject(msgjson);
                    Socket socketClient = ((App) getApplication()).getSocketClient();
                    if (socketClient != null && Strs.isNotEmpty(msgjson)) {
                        socketClient.emit("message", obj, new Ack() {
                            @Override
                            public void call(Object... args) {
                                Log.e("acttalk", "发送成功后的回执：" + args[0].toString());
                                Log.e("acttalk", "消息发送成功--->收到回执时间：" +
                                        Utils.getDate(System.currentTimeMillis(), "MM月dd日 HH:mm:ss"));
                                MessageEventBus(new MessageBackInfo(messageInfo));
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

//        imageRc.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        mSelectIvAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_simple_imageview) {
//            @Override
//            protected void convert(BaseViewHolder helper, String item) {
//                Glide.with(ActTalk.this).load(item).into(((ImageView) helper.getView(R.id.iv)));
//            }
//        };
//        imageRc.setAdapter(mSelectIvAdapter);

        chatAdapter.addItemClickListener(new ChatAdapter.onItemClickListener() {
            @Override
            public void onHeaderClick(int position) {

            }

            @Override
            public void onImageClick(View view, GlideUrl glideUrl, String localimagePath) {
                flSeePhoto.setVisibility(View.VISIBLE);
                Glide.with(ActTalk.this).load(glideUrl == null ? localimagePath : glideUrl).into(photoview);
            }

            @Override
            public void onVoiceClick(ImageView imageView, int position) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_button:
                if (rxPermissions == null) {
                    return;
                }
                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            String authority = "";
                            if (Strs.isEqual(BaseConfig.custom_flag, BaseConfig.FLAG_JINFENG_2)) {
                                authority = "com.my.app.jf2.fileProvider";
                            } else if (Strs.isEqual(BaseConfig.custom_flag, BaseConfig.FLAG_JINFENG_3)) {
                                authority = "com.my.app.huanq.fileProvider";
                            }
                            Matisse.from(ActTalk.this)
                                    .choose(MimeType.ofImage())//图片类型
                                    .countable(true)//true:选中后显示数字;false:选中后显示对号
                                    .maxSelectable(1)//可选的最大数
                                    .maxOriginalSize(1)
                                    .capture(true)//选择照片时，是否显示拍照
                                    .captureStrategy(new CaptureStrategy(true, authority))//参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                                    .imageEngine(new GlideEngine())//图片加载引擎
                                    .forResult(REQUEST_CODE_CHOOSE);//
                        }
                    }
                });
                break;
            case R.id.btnSendImageMsg:

//                if (newFile == null || absolutePath == null) {
//                    Toast.makeText(this, "请选择图片", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                String imagePath = absolutePath;
//                MessageInfo messageInfo = new MessageInfo();
//                messageInfo.setImageUrl(imagePath);
//                messageInfo.setInviteCode(mContactsMode.getInviteCode());
//                messageInfo.setToParent(String.valueOf(mContactsMode.getIsParent()));
//                MessageEventBus(messageInfo);
//                upLoadImageMsg(newFile, messageInfo);
                break;
            case R.id.btnCancel:
                llSendTextMsg.setVisibility(View.VISIBLE);
                break;
            case R.id.flSeePhoto:
                flSeePhoto.setVisibility(View.GONE);
                break;
            case R.id.ivcancel:
                flSeePhoto.setVisibility(View.GONE);
                break;
        }
    }

    private int getTalkWindowisHuizhiMsgCount() {
        int tempcount = 0;
        for (MessageInfo info : messageInfos) {
            if (info.isHuiZhi()) {
                tempcount++;
            }
        }
        Log.d("ActTalk", "getTalkWindowisHuizhiMsgCount:" + tempcount);
        return tempcount;
    }

    /**
     * 构造聊天数据
     */
    private void LoadData() {
        messageInfos = new ArrayList<>();
    }


    public void MessageEventBus(final MessageInfo messageInfo) {
        if (messageInfo == null) {
            return;
        }
        messageInfo.setType(Consitances.CHAT_ITEM_TYPE_RIGHT);
        messageInfo.setSendState(Consitances.CHAT_ITEM_SENDING);
        messageInfo.setTime(Utils.getDate());

        messageInfos.add(messageInfo);
        chatAdapter.add(messageInfo);
        chatList.scrollToPosition(chatAdapter.getCount() - 1);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                messageInfo.setSendState(Consitances.CHAT_ITEM_SEND_SUCCESS);
                chatAdapter.notifyDataSetChanged();
            }
        }, 200);
    }

    public void MessageEventBus(final MessageBackInfo messageBackInfo) {
        if (messageBackInfo == null) {
            return;
        }
        chatList.postDelayed(new Runnable() {
            public void run() {
                if (messageInfos.contains(messageBackInfo.getMessageInfo())) {
                    messageBackInfo.getMessageInfo().setHuiZhi(true);
                    chatAdapter.notifyDataSetChanged();
                }
            }
        }, 20);
    }

    //有新的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEventBus(ContactsBackMsgMode backmessageInfo) {
        if (backmessageInfo == null) {
            return;
        }
        //保证消息发到对应的人的窗口
        if (!Strs.isEqual(backmessageInfo.getSendInviteCode(), mContactsMode.getInviteCode())) {
            return;
        }

        chatList.postDelayed(new Runnable() {
            public void run() {
                MessageInfo message = new MessageInfo();
                message.setContent(backmessageInfo.getText());
                message.setType(Consitances.CHAT_ITEM_TYPE_LEFT);
                message.setSendState(Consitances.CHAT_ITEM_SEND_SUCCESS);
                message.setTime(Utils.getDate(backmessageInfo.getCreateTime(), "MM-dd HH:mm:ss"));
                messageInfos.add(message);
                chatAdapter.add(message);
                chatList.scrollToPosition(chatAdapter.getCount() - 1);

                //设置消息已读状态
                if (messageInfos.size() > 0) {
                    HttpActionTouCai.setChatMessageRead(this, mContactsMode.getInviteCode()
                            , String.valueOf(backmessageInfo.getId()), new AbHttpResult());
                }
            }
        }, 300);
    }

    //已读回执
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEventBus(MsgReadHuizhiMode msgReadHuizhiMode) {
        if (msgReadHuizhiMode == null) {
            return;
        }
        chatList.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Strs.isEqual(mContactsMode.getInviteCode(), msgReadHuizhiMode.getReceiverInviteCode())) {
                    for (MessageInfo messageInfo : messageInfos) {
                        messageInfo.setIsRead(true);
                        chatAdapter.notifyDataSetChanged();
                    }
                }
            }
        }, 200);
    }

    private void getTalkHistory(int index) {
        if (mContactsMode == null) {
            return;
        }
        HttpActionTouCai.getTalkhistory(this, index, mContactsMode.getInviteCode(), new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                DialogsTouCai.showProgressDialog(ActTalk.this, "");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<List<ContactsHistoryMode>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    List<ContactsHistoryMode> contactsHistorylist = getField(extra, "data", null);
                    //Collections.reverse(contactsHistorylist);
                    for (ContactsHistoryMode historyMode : contactsHistorylist) {

                        MessageInfo message = new MessageInfo();
                        message.setContent(historyMode.getMsgType() == 1 ? null : historyMode.getText());
                        message.setImageUrl(historyMode.getMsgType() == 1 ? (ENV.curr.host + "/file/getFile?id=" + historyMode.getText()) : null);
                        message.setMsgType(historyMode.getMsgType());
                        message.setHuiZhi(true);
                        message.setMsgId(String.valueOf(historyMode.getId()));
                        message.setType(historyMode.getIsSelf() == 0 ? Consitances.CHAT_ITEM_TYPE_LEFT : Consitances.CHAT_ITEM_TYPE_RIGHT);
                        if (historyMode.getIsSelf() == 1) {//设置已读未读状态
                            message.setIsRead(historyMode.getStatus() == 0 ? false : true);
                        }
                        message.setTime(Utils.getDate(historyMode.getCreateTime(), "MM-dd HH:mm:ss"));
                        messageInfos.add(0, message);
                    }

                    chatAdapter.clear();
                    chatAdapter.addAll(messageInfos);

                    if (index == 0) {
                        chatList.scrollToPosition(chatAdapter.getCount() - 1);

                        //设置消息已读状态
                        if (messageInfos.size() > 0) {
                            HttpActionTouCai.setChatMessageRead(this, mContactsMode.getInviteCode()
                                    , messageInfos.get(messageInfos.size() - 1).getMsgId(), new AbHttpResult() {
                                        @Override
                                        public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                            UserManagerTouCai.getIns().setMsgIsRead(mContactsMode.getInviteCode());
                                            return super.onSuccessGetObject(code, error, msg, extra);
                                        }
                                    });
                        }
                    } else {
                        if (contactsHistorylist.size() == 0) {
                            Toast.makeText(ActTalk.this, "无更多历史消息", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                chatList.setRefreshing(false);
                DialogsTouCai.hideProgressDialog(ActTalk.this);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> result = Matisse.obtainResult(data);
            if (result != null && result.size() > 0) {
                Uri uri = result.get(0);
                String realPathFromUri = ImageUtil.getRealFilePath(this, uri);

                File newFile = new CompressHelper.Builder(this)
                        .setMaxWidth(720)  // 默认最大宽度为720
                        .setMaxHeight(960) // 默认最大高度为960
                        .setQuality(80)    // 默认压缩质量为80
                        .setFileName(MD5.md5(realPathFromUri))
                        .setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .build()
                        .compressToFile(new File(realPathFromUri));
                String absolutePath = newFile.getAbsolutePath();

                if (newFile == null || absolutePath == null) {
                    Toast.makeText(this, "请选择图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                String imagePath = absolutePath;
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setImageUrl(imagePath);
                messageInfo.setInviteCode(mContactsMode.getInviteCode());
                messageInfo.setToParent(String.valueOf(mContactsMode.getIsParent()));
                MessageEventBus(messageInfo);
                upLoadImageMsg(newFile, messageInfo);
            }
        }
    }


    private void upLoadImageMsg(File newFile, MessageInfo messageInfo) {
        if (mContactsMode == null) {
            return;
        }
        HttpAction.upLoadImageMsgFile(this, newFile, mContactsMode.getInviteCode(), new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                DialogsTouCai.showProgressDialog(ActTalk.this, "");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<String>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    String imageId = getField(extra, "data", null);
                    try {
                        String encode = URLEncoder.encode(imageId, "UTF-8");
                        //组装json,发送给服务端
                        String msgjson = gson.toJson(new ContactsMessageMode(String.valueOf(System.currentTimeMillis()), mContactsMode.getInviteCode(),
                                String.valueOf(mContactsMode.getIsParent()), 1, encode));
                        Log.d("acttalk", "消息准备完毕：---->准备发送服务器时间：" +
                                Utils.getDate(System.currentTimeMillis(), "MM月dd日 HH:mm:ss") + "\n---内容：" + msgjson);
                        try {
                            JSONObject obj = new JSONObject(msgjson);
                            Socket socketClient = ((App) getApplication()).getSocketClient();
                            if (socketClient != null && Strs.isNotEmpty(msgjson)) {
                                socketClient.emit("message", obj, new Ack() {
                                    @Override
                                    public void call(Object... args) {
                                        Log.d("acttalk", "发送成功后的回执：" + args[0].toString());
                                        Log.d("acttalk", "消息发送成功--->收到回执时间：" +
                                                Utils.getDate(System.currentTimeMillis(), "MM月dd日 HH:mm:ss"));
                                        MessageEventBus(new MessageBackInfo(messageInfo));
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogsTouCai.hideProgressDialog(ActTalk.this);
            }
        });
    }

}
