package com.desheng.app.toucai.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shark.tc.R;

public class RedPacketViewgroup extends RelativeLayout implements View.OnClickListener {

    private float amount;//每个红包的金额

    private boolean isClicked;//判断红包是否被点击过，点击之后再点不会触发效果

    private Bitmap redPacketBitmap;//红包对应的bitmap

    private Paint paint;

    private int mId;

    private int width = 40;
    private int height = 46;

    public RedPacketViewgroup(Context context) {
        this(context, null);
    }

    public RedPacketViewgroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RedPacketViewgroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    View inflate;
    TextView money;
    ImageView bg;

    public RedPacketViewgroup(Context context, int id) {
        this(context, null);
        this.mId = id;
        inflate = LayoutInflater.from(context).inflate(R.layout.red_pockets, this, true);
        bg = (ImageView) inflate.findViewById(R.id.bg);
        bg.setOnClickListener(this);
        money = (TextView) inflate.findViewById(R.id.money);
        if (mId % 3 == 1) {
            bg.setBackgroundResource(R.mipmap.big_p1);
            money.setVisibility(GONE);
        } else if (mId % 3 == 2) {
            bg.setBackgroundResource(R.mipmap.in_a_p);
            money.setVisibility(GONE);
        } else {
            bg.setBackgroundResource(R.mipmap.small_p);
            money.setVisibility(GONE);
        }
    }


    //设置红包被拆开的图片
    private void setRedPacketBitmap() {
        bg.setBackgroundResource(R.mipmap.red_open);
        //money.setText(String.valueOf("$18.88"));
    }


    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    @Override
    public void onClick(View v) {
        setClicked(true);
        setRedPacketBitmap();
        if (mIPacketOpened != null) {
            mIPacketOpened.onPacketOpened();
        }
    }

    IPacketOpened mIPacketOpened;

    public void setmIPacketOpened(IPacketOpened mIPacketOpened) {
        this.mIPacketOpened = mIPacketOpened;
    }

    public interface IPacketOpened {
        void onPacketOpened();
    }
}