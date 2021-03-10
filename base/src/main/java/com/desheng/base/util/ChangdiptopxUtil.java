package com.desheng.base.util;


import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChangdiptopxUtil {
    public final int Frame = 3;
    public final int Linear = 1;
    public final int MATCH_PARENT = -1;
    public final int Relative = 2;
    public final int WRAP_CONTENT = -2;
    private int bottom;
    private float density;
    private int height;
    private int left;
    private FrameLayout.LayoutParams paramsFrame;
    private LinearLayout.LayoutParams paramsLinear;
    private RelativeLayout.LayoutParams paramsRelative;
    private int right;
    private float screenWidth;
    private int textsize;
    private int top;
    private int width;

    public ChangdiptopxUtil() {
        this.density = Resources.getSystem().getDisplayMetrics().density;
        this.screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private void SelectWidthHeigthFloat(float paramFloat1, float paramFloat2) {
        if (paramFloat1 == -2.0F) {
            this.width = -2;
        } else if (paramFloat1 == -1.0F) {
            this.width = -1;
        } else {
            this.width = dip2pxFloat(paramFloat1);
        }
        if (paramFloat2 == -2.0F) {
            this.height = -2;
            return;
        }
        if (paramFloat2 == -1.0F) {
            this.height = -1;
            return;
        }
        this.height = dip2pxFloat(paramFloat2);
    }

    private void SelectWidthHeigthInt(int paramInt1, int paramInt2) {
        if (paramInt1 == -2) {
            this.width = -2;
        } else if (paramInt1 == -1) {
            this.width = -1;
        } else {
            this.width = dip2pxInt(paramInt1);
        }
        if (paramInt2 == -2) {
            this.height = -2;
            return;
        }
        if (paramInt2 == -1) {
            this.height = -1;
            return;
        }
        this.height = dip2pxInt(paramInt2);
    }

    public void AdaptiveButtonFloat(Button paramButton, float paramFloat) {
        if (paramFloat > 0.0F) {
            this.textsize = getFontSizeFloat(paramFloat);
            paramButton.setTextSize(0, this.textsize);
        }
    }

    public void AdaptiveButtonFloat(Button paramButton, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3) {
        AdaptiveButtonFloat(paramButton, paramFloat1);
        SelectLayoutFloat(paramInt, paramButton, paramFloat2, paramFloat3);
    }

    public void AdaptiveButtonFloat(Button paramButton, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7) {
        AdaptiveButtonFloat(paramButton, paramFloat1);
        SelectMarginsLayoutFloat(paramInt, paramButton, paramFloat2, paramFloat3);
        SelectMarginsFloat(paramButton, paramInt, paramFloat4, paramFloat5, paramFloat6, paramFloat7);
    }

    public void AdaptiveButtonInt(Button paramButton, int paramInt) {
        if (paramInt > 0) {
            this.textsize = getFontSizeInt(paramInt);
            paramButton.setTextSize(0, this.textsize);
        }
    }

    public void AdaptiveButtonInt(Button paramButton, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        AdaptiveButtonInt(paramButton, paramInt2);
        SelectLayoutInt(paramInt1, paramButton, paramInt3, paramInt4);
    }

    public void AdaptiveButtonInt(Button paramButton, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) {
        AdaptiveButtonInt(paramButton, paramInt2);
        SelectMarginsLayoutInt(paramInt1, paramButton, paramInt3, paramInt4);
        SelectMarginsInt(paramButton, paramInt1, paramInt5, paramInt6, paramInt7, paramInt8);
    }

    public void AdaptiveFrameFloat(View paramView, float paramFloat1, float paramFloat2) {
        SelectWidthHeigthFloat(paramFloat1, paramFloat2);
        this.paramsFrame = new FrameLayout.LayoutParams(this.width, this.height);
        paramView.setLayoutParams(this.paramsFrame);
    }

    public void AdaptiveFrameInt(View paramView, int paramInt1, int paramInt2) {
        SelectWidthHeigthInt(paramInt1, paramInt2);
        this.paramsFrame = new FrameLayout.LayoutParams(this.width, this.height);
        paramView.setLayoutParams(this.paramsFrame);
    }

    public void AdaptiveLinearFloat(View paramView, float paramFloat1, float paramFloat2) {
        SelectWidthHeigthFloat(paramFloat1, paramFloat2);
        this.paramsLinear = new LinearLayout.LayoutParams(this.width, this.height);
        paramView.setLayoutParams(this.paramsLinear);
    }

    public void AdaptiveLinearInt(View paramView, int paramInt1, int paramInt2) {
        SelectWidthHeigthInt(paramInt1, paramInt2);
        this.paramsLinear = new LinearLayout.LayoutParams(this.width, this.height);
        paramView.setLayoutParams(this.paramsLinear);
    }

    public void AdaptiveRelatFloat(View paramView, float paramFloat1, float paramFloat2) {
        SelectWidthHeigthFloat(paramFloat1, paramFloat2);
        this.paramsRelative = new RelativeLayout.LayoutParams(this.width, this.height);
        paramView.setLayoutParams(this.paramsRelative);
    }

    public void AdaptiveRelatInt(View paramView, int paramInt1, int paramInt2) {
        SelectWidthHeigthInt(paramInt1, paramInt2);
        this.paramsRelative = new RelativeLayout.LayoutParams(this.width, this.height);
        paramView.setLayoutParams(this.paramsRelative);
    }

    public void AdaptiveTextFloat(TextView paramTextView, float paramFloat) {
        if (paramFloat > 0.0F) {
            this.textsize = getFontSizeFloat(paramFloat);
            paramTextView.setTextSize(0, this.textsize);
        }
    }

    public void AdaptiveTextFloat(TextView paramTextView, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3) {
        AdaptiveTextFloat(paramTextView, paramFloat1);
        SelectLayoutFloat(paramInt, paramTextView, paramFloat2, paramFloat3);
    }

    public void AdaptiveTextFloat(TextView paramTextView, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7) {
        AdaptiveTextFloat(paramTextView, paramFloat1);
        SelectMarginsLayoutFloat(paramInt, paramTextView, paramFloat2, paramFloat3);
        SelectMarginsFloat(paramTextView, paramInt, paramFloat4, paramFloat5, paramFloat6, paramFloat7);
    }

    public void AdaptiveTextInt(TextView paramTextView, int paramInt) {
        if (paramInt > 0) {
            this.textsize = getFontSizeInt(paramInt);
            paramTextView.setTextSize(0, this.textsize);
        }
    }

    public void AdaptiveTextInt(TextView paramTextView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        AdaptiveTextInt(paramTextView, paramInt2);
        SelectLayoutInt(paramInt1, paramTextView, paramInt3, paramInt4);
    }

    public void AdaptiveTextInt(TextView paramTextView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) {
        AdaptiveTextInt(paramTextView, paramInt2);
        SelectMarginsLayoutInt(paramInt1, paramTextView, paramInt3, paramInt4);
        SelectMarginsInt(paramTextView, paramInt1, paramInt5, paramInt6, paramInt7, paramInt8);
    }

    public void AdaptiveViewFloat(View paramView, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6) {
        SelectMarginsLayoutFloat(paramInt, paramView, paramFloat1, paramFloat2);
        SelectMarginsFloat(paramView, paramInt, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
    }

    public void AdaptiveViewInt(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
        SelectMarginsLayoutInt(paramInt1, paramView, paramInt2, paramInt3);
        SelectMarginsInt(paramView, paramInt1, paramInt4, paramInt5, paramInt6, paramInt7);
    }

    public void SelectLayoutFloat(int paramInt, View paramView, float paramFloat1, float paramFloat2) {
        if (paramInt == 1) {
            AdaptiveLinearFloat(paramView, paramFloat1, paramFloat2);
            return;
        }
        if (paramInt == 2) {
            AdaptiveRelatFloat(paramView, paramFloat1, paramFloat2);
            return;
        }
        if (paramInt == 3) {
            AdaptiveFrameFloat(paramView, paramFloat1, paramFloat2);
        }
    }

    public void SelectLayoutInt(int paramInt1, View paramView, int paramInt2, int paramInt3) {
        if (paramInt1 == 1) {
            AdaptiveLinearInt(paramView, paramInt2, paramInt3);
            return;
        }
        if (paramInt1 == 2) {
            AdaptiveRelatInt(paramView, paramInt2, paramInt3);
            return;
        }
        if (paramInt1 == 3) {
            AdaptiveFrameInt(paramView, paramInt2, paramInt3);
        }
    }

    public void SelectMarginsFloat(View paramView, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
        this.left = dip2pxFloat(paramFloat1);
        this.top = dip2pxFloat(paramFloat2);
        this.right = dip2pxFloat(paramFloat3);
        this.bottom = dip2pxFloat(paramFloat4);
        if (paramInt == 1) {
            this.paramsLinear.setMargins(this.left, this.top, this.right, this.bottom);
            paramView.setLayoutParams(this.paramsLinear);
            return;
        }
        if (paramInt == 2) {
            this.paramsRelative.setMargins(this.left, this.top, this.right, this.bottom);
            paramView.setLayoutParams(this.paramsRelative);
            return;
        }
        if (paramInt == 3) {
            this.paramsFrame.setMargins(this.left, this.top, this.right, this.bottom);
            paramView.setLayoutParams(this.paramsFrame);
        }
    }

    public void SelectMarginsInt(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
        this.left = dip2pxInt(paramInt2);
        this.top = dip2pxInt(paramInt3);
        this.right = dip2pxInt(paramInt4);
        this.bottom = dip2pxInt(paramInt5);
        if (paramInt1 == 1) {
            this.paramsLinear.setMargins(this.left, this.top, this.right, this.bottom);
            paramView.setLayoutParams(this.paramsLinear);
            return;
        }
        if (paramInt1 == 2) {
            this.paramsRelative.setMargins(this.left, this.top, this.right, this.bottom);
            paramView.setLayoutParams(this.paramsRelative);
            return;
        }
        if (paramInt1 == 3) {
            this.paramsFrame.setMargins(this.left, this.top, this.right, this.bottom);
            paramView.setLayoutParams(this.paramsFrame);
        }
    }

    public void SelectMarginsLayoutFloat(int paramInt, View paramView, float paramFloat1, float paramFloat2) {
        if (paramInt == 1) {
            SelectWidthHeigthFloat(paramFloat1, paramFloat2);
            this.paramsLinear = new LinearLayout.LayoutParams(this.width, this.height);
            return;
        }
        if (paramInt == 2) {
            SelectWidthHeigthFloat(paramFloat1, paramFloat2);
            this.paramsRelative = new RelativeLayout.LayoutParams(this.width, this.height);
            return;
        }
        if (paramInt == 3) {
            SelectWidthHeigthFloat(paramFloat1, paramFloat2);
            this.paramsFrame = new FrameLayout.LayoutParams(this.width, this.height);
        }
    }

    public void SelectMarginsLayoutInt(int paramInt1, View paramView, int paramInt2, int paramInt3) {
        if (paramInt1 == 1) {
            SelectWidthHeigthInt(paramInt2, paramInt3);
            this.paramsLinear = new LinearLayout.LayoutParams(this.width, this.height);
            return;
        }
        if (paramInt1 == 2) {
            SelectWidthHeigthInt(paramInt2, paramInt3);
            this.paramsRelative = new RelativeLayout.LayoutParams(this.width, this.height);
            return;
        }
        if (paramInt1 == 3) {
            SelectWidthHeigthInt(paramInt2, paramInt3);
            this.paramsFrame = new FrameLayout.LayoutParams(this.width, this.height);
        }
    }

    public int dip2pxFloat(float paramFloat) {
        return (int) (this.screenWidth / 320.0F * (paramFloat / this.density) + 0.5F);
    }

    public int dip2pxInt(int paramInt) {
        return (int) (this.screenWidth / 320.0F * paramInt + 0.5F);
    }

    public int getFontSizeFloat(float paramFloat) {
        return (int) (this.screenWidth * paramFloat / (this.density * 320.0F) + 0.5F);
    }

    public int getFontSizeInt(int paramInt) {
        return (int) (this.screenWidth * paramInt / 320.0F + 0.5F);
    }
}

