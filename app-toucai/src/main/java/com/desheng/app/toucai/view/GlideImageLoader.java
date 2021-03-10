package com.desheng.app.toucai.view;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.ab.global.ENV;
import com.bumptech.glide.Glide;
import com.desheng.app.toucai.model.BannerBean;
import com.youth.banner.loader.ImageLoader;

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object pathBean, ImageView mImageView) {
        /**
         注意：
         1.图片加载器由自己选择，这里不限制，只是提供几种使用方法
         2.返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
         传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
         切记不要胡乱强转！
         */

        BannerBean bannerBean = (BannerBean) pathBean;
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        if (bannerBean.imgUrl == null || bannerBean.imgUrl.startsWith("file://")) {
            Glide.with(context).load(Uri.parse(bannerBean.imgUrl)).into(mImageView);
        } else {
            Glide.with(context).load(ENV.curr.host + bannerBean.imgUrl).into(mImageView);
        }
    }

    @Override
    public ImageView createImageView(Context context) {
        //CustomRoundAngleImageView simpleDraweeView = new CustomRoundAngleImageView(context);
        return new ImageView(context);
    }
}
