package com.desheng.app.toucai.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.model.GetDesignHostMode;
import com.desheng.app.toucai.model.SuperPartnerStatistics;
import com.desheng.app.toucai.panel.ActShare;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shark.tc.R;

import java.util.HashMap;

import okhttp3.Request;

public class FragShareAndMakeMoney extends BasePageFragment implements View.OnClickListener {

    private TextView textView;
    private TextView tvTuiguang;
    private TextView TvLink;
    private TextView tvcopyLink;
    private ImageView imageView;
    Context mContext;

    public static FragShareAndMakeMoney newInstance() {
        FragShareAndMakeMoney fragment = new FragShareAndMakeMoney();
        return fragment;
    }

    @Override
    protected int setContentView() {
        return R.layout.frag_page_share;
    }

    @Override
    protected void initView(View rootview) {
        mContext = getContext();
        tvTuiguang = ((TextView) rootview.findViewById(R.id.tvTuiguang));
        TvLink = ((TextView) rootview.findViewById(R.id.TvLink));
        tvcopyLink = ((TextView) rootview.findViewById(R.id.tvcopyLink));
        imageView = rootview.findViewById(R.id.iv_bg);
        tvTuiguang.setOnClickListener(this);
        tvcopyLink.setOnClickListener(this);

        ((TextView) rootview.findViewById(R.id.tvMethod1)).setText(Html.fromHtml("<font color='#FF2C66'><bolt>方式一: </bolt></font>  点击分享推广按钮(将二维码海报发给好友扫码注册并下载游戏)"));
        ((TextView) rootview.findViewById(R.id.tvMethod2)).setText(Html.fromHtml("<font color='#FF2C66'><bolt>方式二: </bolt></font>  点击分享推广按钮(将二维码海报发给好友扫码注册并下载游戏)"));
    }

    @Override
    public void fetchData() {
        initStatistics();
    }

    public void initStatistics() {
        HttpActionTouCai.getRecommendInfo(this, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                DialogsTouCai.showProgressDialog(getActivity(), "");
                tvTuiguang.setEnabled(false);
                tvcopyLink.setEnabled(false);
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<SuperPartnerStatistics>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    superPartnerStatistics = getField(extra, "data", null);
                    if (superPartnerStatistics != null) {
                        HttpActionTouCai.getDesignedHost(this, new AbHttpResult() {

                            @Override
                            public boolean onGetString(String str) {
                                if (Strs.isNotEmpty(str)) {
                                    try {
                                        designHostMode = new Gson().fromJson(str, GetDesignHostMode.class);
                                        if (designHostMode != null) {
                                            TvLink.setText(designHostMode.getUrls() + Consitances.RECOMMEND_LINK + superPartnerStatistics.getInviteCode());
                                            tvcopyLink.setEnabled(true);
                                            tvTuiguang.setEnabled(true);
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                                return super.onGetString(str);
                            }
                        });
                    }
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                DialogsTouCai.hideProgressDialog(getActivity());
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (designHostMode != null && superPartnerStatistics != null) {
            TvLink.setText(designHostMode.getUrls() + Consitances.RECOMMEND_LINK + superPartnerStatistics.getInviteCode());
            tvcopyLink.setEnabled(true);
            tvTuiguang.setEnabled(true);
        }
    }

    SuperPartnerStatistics superPartnerStatistics;
    GetDesignHostMode designHostMode;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvTuiguang:
                if (superPartnerStatistics != null && designHostMode != null) {
                    ActShare.launch(getActivity(), superPartnerStatistics.getInviteCode(), designHostMode.getUrls());
                } else {
                    Toasts.show("重新获取邀请码", false);
                    fetchData();
                }
                break;
            case R.id.tvcopyLink:
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                if (superPartnerStatistics != null) {
                    ClipData mClipData = ClipData.newPlainText("recomendLink", TvLink.getText().toString().trim());
                    // 将ClipData内容放到系统剪贴板里。
                    cm.setPrimaryClip(mClipData);
                    Toast.makeText(mContext, "复制成功", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
