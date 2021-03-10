package com.desheng.app.toucai.util;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ab.dialog.AbSampleDialogFragment;
import com.pearl.view.pager.AbSlidingPlayView;
import com.shark.tc.R;


public class DialogDepositHelp extends AbSampleDialogFragment {
	public static DialogDepositHelp curr = null;
	public AbSlidingPlayView mSlidingPlayView = null;
	public ImageView ivLeftBtn = null;
	public ImageView ivRightBtn = null;
	public static int [] picAlipayIds = {R.mipmap.img_alipay_help_1, R.mipmap.img_alipay_help_3, R.mipmap.img_alipay_help_2, R.mipmap.img_alipay_help_4};
	public static int [] picWeiXinIds = {R.mipmap.img_weixin_help_1, R.mipmap.img_weixin_help_2, R.mipmap.img_weixin_help_3, R.mipmap.img_weixin_help_4, R.mipmap.img_weixin_help_5};
	public int [] picIds = null;
	
	public static void showIns(AppCompatActivity ctx, String code){
        if (curr != null) {
            curr.dismiss();
        }
        curr = new DialogDepositHelp();
        if(code.equals("微信")){
            curr.setPicIds(picWeiXinIds);
        }else{
            curr.setPicIds(picAlipayIds);
        }
        curr.show(ctx.getSupportFragmentManager(), "DialogDepositHelp");
	}
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.dialog_deposite_help, null);
        mSlidingPlayView = (AbSlidingPlayView)root.findViewById(R.id.mAbSlidingPlayView);
        mSlidingPlayView.setPageLineImage(R.drawable.shape_oval_white, R.drawable.shape_oval_gray);
		mSlidingPlayView.setNavHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        View playView = null;
        ImageView imgPlay = null;
        for (int i = 0; i < picIds.length; i++) {
            //item中设置padding 实现间距
            playView = inflater.inflate(R.layout.item_sliding_page_view, null);
            imgPlay = (ImageView) playView.findViewById(R.id.imgPlay);
            imgPlay.setImageResource(picIds[i]);
            mSlidingPlayView.addView(playView);
            if(i == 3){
                imgPlay.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                      dismiss();
                    }
                });
            }
        }
        
        //v1 版备份
        ivLeftBtn = root.findViewById(R.id.ivLeftBtn);
        ivRightBtn = root.findViewById(R.id.ivRightBtn);
    
        ivLeftBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                int position = mSlidingPlayView.getViewPager().getCurrentItem();
                position--;
                if(position < 0){
                    position = 0;
                }
                if (position >= picIds.length - 1) {
                    position = picIds.length - 1;
                }
                mSlidingPlayView.getViewPager().setCurrentItem(position);
            }
        });
    
        ivRightBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                int position = mSlidingPlayView.getViewPager().getCurrentItem();
                position++;
                if(position < 0){
                    position = 0;
                }
                if (position >= picIds.length - 1) {
                    position = picIds.length - 1;
                }
                mSlidingPlayView.getViewPager().setCurrentItem(position);
            }
        });
        root.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                curr = null;
            }
        });
		return root;
    }
    
    
    public  void setPicIds(int[] picWIds) {
        picIds = picWIds;
    }
}
