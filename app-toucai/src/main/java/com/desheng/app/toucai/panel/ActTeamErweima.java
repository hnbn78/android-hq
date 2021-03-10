package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.global.ENV;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.ImageUtils;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.model.AgenLinkInitBean;
import com.desheng.app.toucai.model.GetDesignHostMode;
import com.desheng.app.toucai.model.TeamAgentShareLink;
import com.desheng.app.toucai.model.UpdateContentList;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.util.ScreenUtils;
import com.desheng.app.toucai.util.Utils;
import com.desheng.app.toucai.view.ImageViewScrollView;
import com.desheng.base.util.QRCodeUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.WriterException;
import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.Request;

public class ActTeamErweima extends AbAdvanceActivity {

    private Bitmap bitmap2DCODE;
    private ImageView IvErweima;
    private TextView poster;
    private TextView copyLink;
    private String posterPath;

    public static void launch(Activity act) {
        simpleLaunch(act, ActTeamErweima.class);
    }

    @Override
    public void init() {
        hideToolbar();
        View headRoot = findViewById(R.id.vgToolbarGroup);
        paddingHeadOfStatusHeight(headRoot);
        setStatusBarTranslucentAndLightContentWithPadding();
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_team_erweima;
    }

    public void initView() {
        IvErweima = ((ImageView) findViewById(R.id.ivErweima));
        poster = ((TextView) findViewById(R.id.poster));
        copyLink = ((TextView) findViewById(R.id.copyLink));
        copyLink.setOnClickListener(this);
        poster.setOnClickListener(this);
        getData();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.poster:
                if (Strs.isEmpty(realshareLink)) {
                    Toast.makeText(this, "链接未获取成功", Toast.LENGTH_SHORT).show();
                    getData();
                } else {
                    try {
                        Bitmap postbg = BitmapFactory.decodeStream(getAssets().open("team_erweima_bg.webp")).copy(Bitmap.Config.ARGB_8888, true);
                        Bitmap erweima = QRCodeUtil.Create2DCode(realshareLink, 600, 600);
                        Canvas canvas = new Canvas(postbg);
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setColor(0xFFFFFFFF);
                        canvas.drawRoundRect(new RectF(320, 430, 810, 920), 13, 13, paint);
                        if (erweima != null) {
                            canvas.drawBitmap(erweima, 340, 450, new Paint(Paint.ANTI_ALIAS_FLAG));
                        }
                        ImageUtils.saveImageToGallery(this, postbg, "team_share_poster");

                        Toast.makeText(this, "海报已保存至相册", Toast.LENGTH_SHORT).show();
                    } catch (WriterException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case R.id.copyLink:
                if (Strs.isNotEmpty(agentShareLink)) {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("agentShareLink", agentShareLink);
                    // 将ClipData内容放到系统剪贴板里。
                    cm.setPrimaryClip(mClipData);
                    Toast.makeText(this, "链接复制成功", Toast.LENGTH_SHORT).show();
                    copyLink.setText("复制成功");
                    copyLink.setEnabled(false);
                    copyLink.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            copyLink.setText("复制推广链接");
                            copyLink.setEnabled(true);
                        }
                    }, 1500);
                } else {
                    Toast.makeText(this, "链接未获取成功", Toast.LENGTH_SHORT).show();
                    getData();
                }
                break;
        }
    }

    String realshareLink;

    private void getData() {
        HttpActionTouCai.getDesignedHost(this, new AbHttpResult() {
            @Override
            public boolean onGetString(String str) {
                if (Strs.isNotEmpty(str)) {
                    try {
                        GetDesignHostMode designHostMode = new Gson().fromJson(str, GetDesignHostMode.class);
                        if (designHostMode != null) {
                            HttpActionTouCai.getTeamAgentRegistLink(this, new AbHttpResult() {

                                @Override
                                public void onBefore(Request request, int id, String host, String funcName) {
                                    DialogsTouCai.showProgressDialog(ActTeamErweima.this, "");
                                }

                                @Override
                                public void setupEntity(AbHttpRespEntity entity) {
                                    entity.putField("data", new TypeToken<TeamAgentShareLink>() {
                                    }.getType());
                                }

                                @Override
                                public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                                    if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                                        TeamAgentShareLink linkdata = getField(extra, "data", null);//fc769c6ec8d22ecf.html

                                        //奇葩的字段，需要这样处理！！！
                                        String linkcode = linkdata.getCode().replace("/register/", "").replace(".html", "");
                                        HttpActionTouCai.getTeamAgentRegistLinkInit(this, linkcode, new AbHttpResult() {
                                            @Override
                                            public void setupEntity(AbHttpRespEntity entity) {
                                                entity.putField("data", new TypeToken<AgenLinkInitBean>() {
                                                }.getType());
                                            }

                                            @Override
                                            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                                                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                                                    AgenLinkInitBean agenLinkInitBean = getField(extra, "data", null);

                                                    if (agenLinkInitBean != null) {
                                                        realshareLink = designHostMode.getUrls() + "/rg/" + agenLinkInitBean.getParentId();
                                                        updateContentConfig(realshareLink);
                                                        try {
                                                            bitmap2DCODE = QRCodeUtil.Create2DCode(realshareLink, 330, 330);
                                                        } catch (WriterException e) {
                                                            e.printStackTrace();
                                                        }
                                                        IvErweima.setImageBitmap(bitmap2DCODE);
                                                    }
                                                }

                                                return super.onSuccessGetObject(code, error, msg, extra);
                                            }
                                        });
                                    }

                                    return super.onSuccessGetObject(code, error, msg, extra);
                                }

                                @Override
                                public boolean onError(int status, String content) {
                                    Toasts.show(ActTeamErweima.this, content, false);
                                    return true;
                                }

                                @Override
                                public void onAfter(int id) {
                                    super.onAfter(id);
                                    DialogsTouCai.hideProgressDialog(ActTeamErweima.this);
                                }
                            });
                        }
                    } catch (Exception e) {

                    }
                }
                return super.onGetString(str);
            }
        });
    }

    private void updateContentConfig(String link) {
        HttpActionTouCai.getUpdateContentList(this, Consitances.contentManager.TEANM_SHARE_LINK, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<UpdateContentList>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    UpdateContentList list = getField(extra, "data", null);

                    if (list != null && list.getList() != null && list.getList().size() > 0) {
                        agentShareLink = Utils.stripHtml(list.getList().get(0)
                                .getContent().replace("XXXXXXXXXXX", link)).replace("&darr;", "");
                    }
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }
        });
    }

    String agentShareLink = "";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap2DCODE != null && !bitmap2DCODE.isRecycled()) {
            bitmap2DCODE.recycle();
            bitmap2DCODE = null;
        }
    }
}
