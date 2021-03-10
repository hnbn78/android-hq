package com.pearl.view.Keyboard;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pearl.view.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * 弹框里面的View
 */
public class PasswordView extends RelativeLayout {
    
    private  VirtualKeyboardView virtualKeyboardView;
    Context mContext;
    private int maxCnt;

    private TextView[] tvList;      //用数组保存6个TextView，为什么用数组？
    private View[] arrVgInputs;      //用数组保存6个TextView，为什么用数组？

    private ImageView[] imgList;      //用数组保存6个TextView，为什么用数组？


    private ImageView imgCancel;

    private ArrayList<Map<String, String>> valueList;

    private int currentIndex = -1;    //用于记录当前输入密码格位置

    public PasswordView(Context context) {
        this(context, null);
    }

    public PasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        View view = View.inflate(context, R.layout.view_password, this);
     
        initView(view);
        
    }
    

    private void initView(View view) {
        tvList = new TextView[6];
        imgList = new ImageView[6];
        arrVgInputs = new View[6];

        tvList[0] = (TextView) view.findViewById(R.id.tvInput1);
        tvList[1] = (TextView) view.findViewById(R.id.tvInput2);
        tvList[2] = (TextView) view.findViewById(R.id.tvInput3);
        tvList[3] = (TextView) view.findViewById(R.id.tvInput4);
        tvList[4] = (TextView) view.findViewById(R.id.tvInput5);
        tvList[5] = (TextView) view.findViewById(R.id.tvInput6);
        
        imgList[0] = (ImageView) view.findViewById(R.id.img_pass1);
        imgList[1] = (ImageView) view.findViewById(R.id.img_pass2);
        imgList[2] = (ImageView) view.findViewById(R.id.img_pass3);
        imgList[3] = (ImageView) view.findViewById(R.id.img_pass4);
        imgList[4] = (ImageView) view.findViewById(R.id.img_pass5);
        imgList[5] = (ImageView) view.findViewById(R.id.img_pass6);
    
        arrVgInputs[0] = view.findViewById(R.id.vgInput1);
        arrVgInputs[1] = view.findViewById(R.id.vgInput2);
        arrVgInputs[2] = view.findViewById(R.id.vgInput3);
        arrVgInputs[3] = view.findViewById(R.id.vgInput4);
        arrVgInputs[4] = view.findViewById(R.id.vgInput5);
        arrVgInputs[5] = view.findViewById(R.id.vgInput6);
    }

   

    public void setupView(int count, VirtualKeyboardView virtualKeyboardView, final boolean isPassword, final OnPasswordInput pass) {
        maxCnt = count;
        this.virtualKeyboardView = virtualKeyboardView;
        valueList = virtualKeyboardView.getValueList();
        for (int i = 6; i > count ; i--) {
            arrVgInputs[i-1].setVisibility(View.GONE);
        }
        
        // 这里、重新为数字键盘gridView设置了Adapter
        virtualKeyboardView.getGridView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 9 || position == 10) {    //点击0~9按钮

                    if (currentIndex >= -1 && currentIndex < maxCnt -1) {      //判断输入位置————要小心数组越界
                        ++currentIndex;
                        tvList[currentIndex].setText(valueList.get(position).get("name"));

                        if(isPassword){
                            tvList[currentIndex].setVisibility(View.INVISIBLE);
                            imgList[currentIndex].setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (position == 11) {      //点击退格键
                        if (currentIndex - 1 >= -1) {      //判断是否删除完毕————要小心数组越界

                            tvList[currentIndex].setText("");

                            tvList[currentIndex].setVisibility(View.VISIBLE);
                            imgList[currentIndex].setVisibility(View.INVISIBLE);

                            currentIndex--;
                        }
                    }
                    
                    if(position == 9) {
                        clearInput();
                        if (pass != null) {
                            pass.onClearHistory();
                        }
                    }
                }
            }
        });
        
        setOnFinishInput(pass);
    }
    
    public void clearInput() {
        currentIndex = -1;
        for (int i = 0; i < maxCnt; i++) {
            tvList[i].setText("");
        }
    }

    //设置监听方法，在第6位输入完成后触发
    public void setOnFinishInput(final OnPasswordInput pass) {
        tvList[maxCnt - 1].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().length() == 1) {

                    String strPassword = "";     //每次触发都要先将strPassword置空，再重新获取，避免由于输入删除再输入造成混乱

                    for (int i = 0; i < 6; i++) {
                        strPassword += tvList[i].getText().toString().trim();
                    }
                    if (pass != null) {
                        pass.onInputFinish(strPassword);    //接口中要实现的方法，完成密码输入完成后的响应逻辑
                    }
                }
            }
        });
    }

    public VirtualKeyboardView getVirtualKeyboardView() {
        return virtualKeyboardView;
    }

}
