package com.pearl.act.util;


import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.core.GuideLayout;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.listener.OnLayoutInflatedListener;
import com.app.hubert.guide.listener.OnPageChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.app.hubert.guide.model.HighlightView;
import com.pearl.act.BuildConfig;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GuidePageHelper {
    public static final TouCaiGuidePage DUMMY_GUIDE_PAGE = TouCaiGuidePage.newInstance();

    public static class Builder implements OnGuideChangedListener {
        com.app.hubert.guide.core.Builder builder;
        TouCaiGuideController controller;
        String username;
        List<Pair<String, TouCaiGuidePage>> guidepages = new ArrayList<>();
        OnPageChangeListener listener;
        boolean lastCancelable = true;
        String tag = "";

        Builder(Fragment frag, String username) {
            builder = NewbieGuide.with(frag);
            this.username = username;
        }

        Builder(Activity activity, String username) {
            builder = NewbieGuide.with(activity);
            this.username = username;
        }

        public Builder setTag(String tag) {
            this.tag = username + tag;
            builder.setLabel(this.tag);
            return this;
        }

        public Builder addGuidePage(final String tag, TouCaiGuidePage page) {
            if (page.getHighLights().size() == 0 || page == DUMMY_GUIDE_PAGE)
                return this;

            builder.addGuidePage(page);
            final Pair<String, TouCaiGuidePage> p = new Pair<String, TouCaiGuidePage>(tag, page);
            guidepages.add(p);
            page.setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                @Override
                public void onLayoutInflated(View view, Controller controller) {
                    if (listener != null)
                        listener.onInflate(controller, tag, guidepages.indexOf(p), view);
                }
            });
            return this;
        }

        public void setOnPageChangedListener(OnPageChangeListener listener) {
            this.listener = listener;
        }

        public void setLastPageCancelable(boolean cancelable) {
            lastCancelable = cancelable;
        }

        public boolean show() {
//            if (BuildConfig.DEBUG)
//                builder.alwaysShow(true);

            builder.setOnPageChangedListener(new OnPageChangedListener() {
                @Override
                public void onPageChanged(int i) {
                    if (listener != null)
                        listener.onPageChanged(guidepages.get(i).first, i, guidepages.get(i).second, controller);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            controller.refreshLayout();
                        }
                    });
                }
            });

            if(guidepages.size()>0){
                guidepages.get(guidepages.size() - 1).second.setEverywhereCancelable(lastCancelable);
            }

            builder.setOnGuideChangedListener(this);
            controller = new TouCaiGuideController(builder.show(), guidepages);

//            if (BuildConfig.DEBUG) {
//                return true;
//            } else {
                try {
                    Field spField = controller.controller.getClass().getDeclaredField("sp");
                    spField.setAccessible(true);
                    SharedPreferences sp = (SharedPreferences) spField.get(controller.controller);
                    final int showed = sp.getInt(tag, 0);

                    if (showed < 1)
                        return true;
                    else
                        return false;
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
//            }

            return false;
        }

        public void finish() {
            controller.controller.remove();
        }

        public List<Pair<String, TouCaiGuidePage>> getGuidepages() {
            return guidepages;
        }

        @Override
        public void onShowed(Controller controller) {
            if (listener != null)
                listener.onShow();
        }

        @Override
        public void onRemoved(Controller controller) {
            if (listener != null)
                listener.onRemove();
        }
    }


    public static Builder build(Fragment fragment, String username, String tag) {
        return new Builder(fragment, username).setTag(tag);
    }

    public static Builder build(Activity activity, String username, String tag) {
        return new Builder(activity, username).setTag(tag);
    }

    public interface OnPageChangeListener {
        void onInflate(Controller controller, String tag, int pos, View root);

        void onPageChanged(String tag, int pos, TouCaiGuidePage guidePage, TouCaiGuideController controller);

        void onShow();

        void onRemove();
    }

    public static class TouCaiGuidePage extends GuidePage {
        @IdRes
        int layoutAnchor = 0;
        int gravity;

        public TouCaiGuidePage setLayoutAnchor(int layoutAnchor, int direction) {
            this.layoutAnchor = layoutAnchor;
            this.gravity = direction;
            return this;
        }

        public static TouCaiGuidePage newInstance() {
            return new TouCaiGuidePage();
        }

        @Override
        public TouCaiGuidePage addHighLight(View view) {
            super.addHighLight(view);
            return this;
        }

        @Override
        public TouCaiGuidePage addHighLight(RectF rectF) {
            super.addHighLight(rectF);
            return this;
        }

        @Override
        public TouCaiGuidePage setLayoutRes(int resId, int... id) {
            super.setLayoutRes(resId, id);
            return this;
        }
    }

    public static class TouCaiGuideController {
        com.app.hubert.guide.core.Controller controller;
        List<Pair<String, TouCaiGuidePage>> guidepages;

        public TouCaiGuideController(com.app.hubert.guide.core.Controller controller, List<Pair<String, TouCaiGuidePage>> guidepages) {
            this.controller = controller;
            this.guidepages = guidepages;
        }

        public void refreshLayout() {
            try {
                Field currentField = controller.getClass().getDeclaredField("current");
                currentField.setAccessible(true);
                int current = currentField.getInt(controller);
                Field currentLayoutField = controller.getClass().getDeclaredField("currentLayout");
                currentLayoutField.setAccessible(true);
                GuideLayout currentLayout = (GuideLayout) currentLayoutField.get(controller);

                if (guidepages.get(current).second.layoutAnchor != 0) {
                    TouCaiGuidePage guidePage = guidepages.get(current).second;
                    View anchorview = currentLayout.findViewById(guidePage.layoutAnchor);

                    if (anchorview != null) {
                        List<HighLight> highlights = guidePage.getHighLights();
                        List<RectF> rects = new ArrayList<>(highlights.size());

                        for (HighLight h : highlights) {
                            RectF rect = h.getRectF(currentLayout);

                            if (rect.width() == 0 || rect.height() == 0)
                                continue;
                            RectF r = h.getRectF(currentLayout);
                            rects.add(r);
                        }


                        RectF anchorRect = new HighlightView(anchorview, HighLight.Shape.RECTANGLE, 0, 0).getRectF(currentLayout);
                        if (rects.size() > 0) {
                            RectF target = rects.get(0);
                            float dx = 0, dy = 0;
                            if ((guidePage.gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.LEFT) {
                                dx = target.centerX() - anchorRect.centerX();
                                dx -= anchorRect.width() / 2 + target.width() / 2;
                            } else if ((guidePage.gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.RIGHT) {
                                dx = target.centerX() - anchorRect.centerX();
                                dx += anchorRect.width() / 2 + target.width() / 2;
//                            } else if ((guidePage.gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL) {
                            } else {
                                dx = target.centerX() - anchorRect.centerX();
                            }
                            if ((guidePage.gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.TOP) {
                                dy =  target.centerY() - anchorRect.centerY();
                                dy -= anchorRect.height() / 2 + target.height() / 2;
                            } else if ((guidePage.gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.BOTTOM) {
                                dy =  target.centerY() - anchorRect.centerY();
                                dy += anchorRect.height() / 2 + target.height() / 2;
//                            } else if ((guidePage.gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL) {
                            } else {
                                dy =  target.centerY() - anchorRect.centerY();
                            }

                            if (anchorRect.right + dx > currentLayout.getWidth()) {
                                dx = currentLayout.getWidth() - anchorRect.right;
                            } else if (anchorRect.left + dx < 0) {
                                dx = -anchorRect.left;
                            }

                            if (anchorRect.bottom + dy > currentLayout.getHeight()) {
                                dy = currentLayout.getHeight() - anchorRect.bottom;
                            } else if (anchorRect.top + dy < 0) {
                                dy = -anchorRect.top;
                            }

                            anchorview.setTranslationY(dy + anchorview.getTranslationY());
                            anchorview.setTranslationX(dx + anchorview.getTranslationX());
                        }
                    }
                }

                currentLayout.invalidate();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        public void showPage(int i) {
            controller.showPage(i);
        }

        public GuideLayout getCurrentLayout() {
            try {
                Field currentLayoutField = controller.getClass().getDeclaredField("currentLayout");
                currentLayoutField.setAccessible(true);
                GuideLayout currentLayout = (GuideLayout) currentLayoutField.get(controller);
                return currentLayout;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    public static RectF getViewLocationInWindow(View v) {
        int[] pos = new int[2];
        v.getLocationInWindow(pos);
        return new RectF(pos[0], pos[1], pos[0] + v.getWidth(), pos[1] + v.getHeight());
    }
}
