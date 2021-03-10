package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;

import com.ab.global.ENV;
import com.ab.util.ImageUtils;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.base.util.QRCodeUtil;
import com.google.zxing.WriterException;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import java.io.File;
import java.io.IOException;

public class ActShare extends AbAdvanceActivity {
    String path;
    Bitmap bg;
    Bitmap qr;
    private ImageView imageView;

    public static void launch(Activity activity, String invateCode, String host) {
        Intent intent = new Intent(activity, ActShare.class);
        intent.putExtra("invateCode", invateCode);
        intent.putExtra("host", host);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_share;
    }

    @Override
    protected void init() {
        String invateCode = getIntent().getStringExtra("invateCode");
        String host = getIntent().getStringExtra("host");

        path = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES) + File.separator + "toucai_share.png";
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "好友分享");
        setStatusBarTranslucentAndLightContentWithPadding();
        imageView = findViewById(R.id.iv_bg);

        if (Strs.isEmpty(invateCode)) {
            Toasts.show("邀请码不存在", false);
            finish();
            return;
        }

        try {
            bg = BitmapFactory.decodeStream(getAssets().open("bg_share_to_freind.webp")).copy(Bitmap.Config.ARGB_8888, true);
            qr = QRCodeUtil.Create2DCode(host + Consitances.RECOMMEND_LINK + invateCode, 350, 350);
            Canvas canvas = new Canvas(bg);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(getResources().getColor(R.color.orange_ED5735));
            canvas.drawRoundRect(new RectF(667 - 30, 1230 - 30, 667 + 265 + 30, 1230 + 265 + 30), 20, 20, paint);
            paint.setColor(0xFFFFFFFF);
            canvas.drawRect(new Rect(667 - 10, 1230 - 10, 667 + 265 + 10, 1230 + 265 + 10), paint);
            canvas.drawBitmap(qr, 667, 1230, new Paint(Paint.ANTI_ALIAS_FLAG));
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageView.setImageBitmap(bg);
    }

    public void onSharedClick(View view) {
        try {

            ImageUtils.savePngFileWithCompress(bg, path);
            Uri imageUri = FileProvider.getUriForFile(this, getApplicationInfo().processName + ".fileProvider", new File(path));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.setType("image/*");
            startActivity(Intent.createChooser(shareIntent, "分享到"));
        } catch (Exception e) {
            e.printStackTrace();
            Toasts.show("分享失败:" + e.getMessage());
        }
    }
}
