package com.pearl.view.pinyinSearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ab.callback.AbCallback;
import com.pearl.view.R;

/**
 * Created by lee on 2018/1/22.
 */

public class PinYinSearchView extends FrameLayout {
    private LinearLayout empty;
    private AutoCompleteTextView search;
    private String[] listStrs;
    
    public PinYinSearchView(@NonNull Context context) {
        super(context);
        init(context);
    }
    
    public PinYinSearchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    public PinYinSearchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    private void init(Context context) {
        View.inflate(context, R.layout.ab_alert_dialog_search_pinyin, this);
        search = (AutoCompleteTextView) findViewById(R.id.search);
        empty = (LinearLayout) findViewById(R.id.empty);
        empty.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
            }
        });
       
    }
    
    // 支持拼音检索
    public void setBackupListStrs(String [] listStrs){
        this.listStrs = listStrs;
        // 自动提示适配器
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, str);
        SearchAdapter<String> adapter = new SearchAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, listStrs, SearchAdapter.ALL);
        search.setAdapter(adapter);
    }
    
    public void setDropdownTextClickListener(final AbCallback<String> listener){
        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.callback( parent.getItemAtPosition(position).toString() );
                }
               
            }
        });
    }
    
   public Editable getText(){
        return search.getText();
   }
   
   public void setText(String str){
       search.setText(str);
   }
}
