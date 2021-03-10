package com.pearl.view.SimpleCollapse;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.pearl.view.R;


/**
 * Created by lee on 2017/10/11.
 */
public class SimpleCollaspingAppBarLayout extends AppBarLayout {
    
    private SimpleCoordinatorLayout simpleParent;
    private CollapsingToolbarLayout ctlContainer;
    private RelativeLayout vgCollasping;
    private RelativeLayout vgAppBar;
    private CollapsingState state;
    private ICollaspingListener listener;
    long lastOffset = 0;
    private boolean autoCollapse = true;
    private boolean isSelfTouch = false;
    
    public SimpleCollaspingAppBarLayout(Context context) {
        super(context);
        init(context);
    }
    
    public SimpleCollaspingAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    
    private void init(Context context) {
        View child = LayoutInflater.from(context).inflate(R.layout.view_simple_collasping_appbar_layout, this);
        ctlContainer = (CollapsingToolbarLayout) child.findViewById(R.id.ctlContainer);
        vgCollasping = (RelativeLayout)ctlContainer.findViewById(R.id.vgCollasping);
        vgAppBar = (RelativeLayout)ctlContainer.findViewById(R.id.vgAppBar);
        
        addOnOffsetChangedListener(new OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(simpleParent == null){
                    simpleParent = (SimpleCoordinatorLayout) getParent();
                }
                if(-verticalOffset >= appBarLayout.getTotalScrollRange()){
                    state = CollapsingState.COLLAPSED; //折叠状态
                    simpleParent.setWillFlitting(false);
                    if(listener != null){
                        listener.onCollapsed(verticalOffset);
                    }
                }else if(verticalOffset >= 0){ //展开状态
                    state = CollapsingState.EXPANDED;
                    simpleParent.setWillFlitting(false);
                    if(listener != null){
                        listener.onExpanded(verticalOffset);
                    }
                }else{
                    if(state == CollapsingState.COLLAPSED){ //从折叠走向展开
                        state = CollapsingState.MOVING_TO_EXPANDED;
                        if(listener != null){
                            listener.onMovingToExpanded(simpleParent.isFlitting(), verticalOffset);
                        }
                    }else if(state == CollapsingState.EXPANDED){
                        state = CollapsingState.MOVING_TO_COLLAPSED;
                        if(listener != null){
                            listener.onMovingToCollapsed(simpleParent.isFlitting(), verticalOffset);
                        }
                    }else {
                        //移动状态中不改状态
                        if(state == CollapsingState.MOVING_TO_COLLAPSED){
                            if(listener != null){
                                listener.onMovingToCollapsed(simpleParent.isFlitting(), verticalOffset);
                            }
                            //自动折叠
                            if(Math.abs(verticalOffset) > 50 && !simpleParent.isFlitting() && autoCollapse){
                                long delta = verticalOffset - lastOffset;
                                if(delta < 0 ){
                                    simpleParent.setWillFlitting(true);
                                    setExpanded(false, true);
                                }
                            }
                        }else if(state == CollapsingState.MOVING_TO_EXPANDED){
                            if(listener != null){
                                listener.onMovingToExpanded(simpleParent.isFlitting(), verticalOffset);
                            }
                            //自动展开
                            if(appBarLayout.getTotalScrollRange() + verticalOffset > 30 && !simpleParent.isFlitting()  && autoCollapse){
                                long delta = verticalOffset - lastOffset;
                                if(delta > 0 ){
                                    simpleParent.setWillFlitting(true);
                                    setExpanded(true, true);
                                }
                            }
                        }
                    }
                }
                lastOffset = verticalOffset;
            }
        });
    }
    
    
    
    public CollapsingState getState() {
        return state;
    }
    
    public void setState(CollapsingState state) {
        this.state = state;
    }
    
    public boolean isAutoCollapse() {
        return autoCollapse;
    }
    
    public void setAutoCollapse(boolean autoCollapse) {
        this.autoCollapse = autoCollapse;
    }
    
    public void setCollaspingContent(View view){
        vgCollasping.addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
    
    public void setCollaspingContent(int resId){
        setCollaspingContent(LayoutInflater.from(getContext()).inflate(resId, this, false));
    }
    
    public void setAppBar(View view){
        vgAppBar.addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
    
    public void setAppBar(int resId) {
        setAppBar(LayoutInflater.from(getContext()).inflate(resId, this, false));
    }
    
    public ICollaspingListener getCollaspingListener() {
        return listener;
    }
    
    public void setCollaspingListener(ICollaspingListener listener) {
        this.listener = listener;
    }
    
    public enum CollapsingState {
        EXPANDED,
        MOVING_TO_EXPANDED,
        COLLAPSED,
        MOVING_TO_COLLAPSED
    }
    
    public interface ICollaspingListener{
        void onExpanded(int verticalOffset);
        void onCollapsed(int verticalOffset);
        void onMovingToExpanded(boolean isFliting, int verticalOffset);
        void onMovingToCollapsed(boolean isFliting, int verticalOffset);
    }
    
}
