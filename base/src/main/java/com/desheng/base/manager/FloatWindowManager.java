package com.desheng.base.manager;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ab.debug.AbDebug;
import com.ab.global.Config;
import com.ab.global.ENV;
import com.ab.http.AbHttpResult;
import com.ab.module.MM;
import com.ab.thread.ThreadCollector;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.controller.FloatActionController;
import com.desheng.base.event.CloseFloatEvent;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.util.Constants_LM;
import com.desheng.base.util.SpUtil;
import com.desheng.base.view.FloatLayout;
import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.ping.PingResult;
import com.stealthcopter.networktools.ping.PingStats;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


/**
 * Author:xishuang
 * Date:2017.08.01
 * Des:悬浮窗统一管理，与悬浮窗交互的真正实现
 */
public class FloatWindowManager {
    /**
     * 上下文引用
     */
    private static WeakReference<Activity> ctxRef;

    /**
     * 悬浮窗
     */
    private static FloatLayout mFloatLayout;
    private static WindowManager mWindowManager;
    private static WindowManager.LayoutParams wmParams;

    /**
     * 弹出面板
     */
    private static PopupWindow mPopWindow;
    private static ToggleButton toggleButton_one, toggleButton_two;
    private static RelativeLayout line_one, line_two, line_three, line_four;
    private static Handler handler;
    private static ArrayList<Ping> listPing;

    /**
     * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void createFloatWindow(Context context) {
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = getWindowManager(context);
        mFloatLayout = new FloatLayout(context);
        if (Build.VERSION.SDK_INT >= 24) { /*android7.0不能用TYPE_TOAST*/
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else { /*以下代码块使得android6.0之后的用户不必再去手动开启悬浮窗权限*/
            String packname = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", packname));
            if (permission) {
                wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
        }

        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.START | Gravity.TOP;

        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        FloatWindowManager.mWindowManager.getDefaultDisplay().getMetrics(dm);
        //窗口的宽度
        int screenWidth = dm.widthPixels;
        //窗口高度
        int screenHeight = dm.heightPixels;
        //以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = screenWidth;
        wmParams.y = screenHeight / 6;
        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //mWindowManager.addView(mFloatLayout, wmParams);
        //是否展示小红点展示
        checkRedDot(context);
    }

    /**
     * 移除悬浮窗
     */
    public static void removeFloatWindowManager() {
        //移除悬浮窗口
        boolean isAttach = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isAttach = mFloatLayout.isAttachedToWindow();
        }
        if (mFloatLayout.getParent() != null && isAttach && mWindowManager != null)
            mWindowManager.removeView(mFloatLayout);
    }

    /**
     * 返回当前已创建的WindowManager。
     */
    private static WindowManager getWindowManager(Context context) {
        if (context == null) {
            return null;
        }
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return mWindowManager;
    }

    /**
     * 小红点展示
     */
    public static void checkRedDot(Context context) {
        if (mFloatLayout == null) return;
        //是否展示小红点展示
        int num = getObtainNumber(context);
        if (num > 0) {
            mFloatLayout.setDragFlagViewVisibility(View.VISIBLE);
            mFloatLayout.setDragFlagViewText(getObtainNumber(context));
        } else {
            mFloatLayout.setDragFlagViewVisibility(View.GONE);
        }
    }

    /**
     * 添加小红点
     */
    public static void addObtainNumer(Context context) {
        int number = (int) SpUtil.get(context, Constants_LM.OBTAIN_NUMBER, 0);
        if (number < 0) {
            number = 0;
        }
        number = number + 1;
        SpUtil.put(context, Constants_LM.OBTAIN_NUMBER, number);
        if (mFloatLayout != null) {
            mFloatLayout.setDragFlagViewVisibility(View.VISIBLE);
            mFloatLayout.setDragFlagViewText(number);
        }
    }

    /**
     * 获取小红点展示的数量
     */
    private static int getObtainNumber(Context context) {
        return 0;
        //return (int) SpUtil.get(context, Constants_LM.OBTAIN_NUMBER, 0);
    }

    /**
     * 设置小红点数字
     */
    public static void setObtainNumber(Context context, int number) {
        if (number < 0) {
            number = 0;
        }
        SpUtil.put(context, Constants_LM.OBTAIN_NUMBER, number);
        FloatWindowManager.checkRedDot(context);
    }

    /**
     * 隐藏对话栏，是否小红点
     */
    public static void updataRedAndDialog(Context context) {
        mFloatLayout.setDragFlagViewVisibility(View.VISIBLE);
        //是否展示小红点展示
        checkRedDot(context);
    }

    public static void hide() {
        if (mFloatLayout.getParent() != null) {
            mWindowManager.removeViewImmediate(mFloatLayout);
            getWindowManager(null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void show(Activity act) {
        if (mFloatLayout.getParent() == null && !act.isDestroyed()) {
            getWindowManager(act);
            ctxRef = new WeakReference<Activity>(act);
            //重点，类型设置为dialog类型,可无视权限!
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
            //重点,必须设置此参数，用于窗口机制验证
            IBinder windowToken = act.getWindow().getDecorView().getWindowToken();
            wmParams.token = windowToken;
            mFloatLayout.setParams(wmParams);

            try {
                mWindowManager.addView(mFloatLayout, wmParams);
                mFloatLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ctxRef != null && ctxRef.get() != null) {
                            showPopupWindow(ctxRef.get(), mFloatLayout);
                        }
                    }
                });
            } catch (Exception e) {
                AbDebug.error(AbDebug.TAG_APP, Thread.currentThread(), e);
            }


        }
    }

    //showPopupWindow();
    public static void showPopupWindow(final Activity ctx, View anchor) {
        switch_success = false;
        //设置contentView
        View contentView = LayoutInflater.from(ctx).inflate(R.layout.act_xuanfukuang, null);
        mPopWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setContentView(contentView);

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
            }
        });

        final TextView tvSelectedLine = (TextView) contentView.findViewById(R.id.tvSelectedLine);
        final TextView tvSelectedLineLab = (TextView) contentView.findViewById(R.id.tvSelectedLineLab);
        String urlFevorite = "";

        final List<String> urls;
        if (Strs.isEqual(BaseConfig.FLAG_TOUCAI, BaseConfig.custom_flag)) {
            urls = UserManager.getIns().getUrls();
        } else {
            urls = UserManager.getIns().getApUrls();
        }

        if (Strs.isNotEmpty(UserManager.getIns().getUrlFevorite())) {
            for (int i = 0; i < urls.size(); i++) {
                if (urls.get(i).contains(UserManager.getIns().getUrlFevorite())) {
                    urlFevorite = String.valueOf(i + 1);
                    break;
                }
            }
        }
        tvSelectedLine.setText("线路" + urlFevorite);

        ListView lvLines = (ListView) contentView.findViewById(R.id.lvLines);
        final ItemLineAdapter adapter = new ItemLineAdapter(ctx);
        lvLines.setAdapter(adapter);
        lvLines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //测试时不允许点击
                if (adapter.getItem(position) != null) {
                    TextView tvLineSpeed = (TextView) view.findViewById(R.id.tvLineSpeed);
                    TextView good_line = (TextView) view.findViewById(R.id.good_line);
                    if ((tvLineSpeed != null && Strs.isEqual("测试中...⏳", tvLineSpeed.getText().toString()))
                            || (good_line != null && good_line.getVisibility() == View.VISIBLE && Strs.isEmpty(good_line.getText().toString()))) {
                        Toasts.show(ctx, "测速中,请稍等...", false);
                        return;
                    }
                }

                //当点击了 json不通的线路时 自动帮用户切换至最优线路
                if (adapter.getItem(position) == null || Strs.isEmpty(adapter.getItem(position).getStatus())) {
                    Toasts.show(ctx, "此线路延迟过大,正在\n帮您切换至其他线路...", false);

                    int index = 0;
                    for (int i = 0; i < urls.size(); i++) {
                        if (Nums.parse(adapter.getItem(i).getSpeed().get("delay"), 10000) <
                                Nums.parse(adapter.getItem(index).getSpeed().get("delay"), 10000)) {
                            index = i;
                        }
                    }
                    tvSelectedLineLab.setText("正在切换线路...");
                    tvSelectedLine.setVisibility(View.INVISIBLE);
                    switchLine(index, adapter, ctx, tvSelectedLine, tvSelectedLineLab);
                    return;
                }

                //正常切换线路
                tvSelectedLineLab.setText("正在切换线路...");
                tvSelectedLine.setVisibility(View.INVISIBLE);
                switchLine(position, adapter, ctx, tvSelectedLine, tvSelectedLineLab);
            }
        });

        ToggleButton tbAuto = (ToggleButton) contentView.findViewById(R.id.tbAuto);
        tbAuto.setChecked(UserManager.getIns().isAutoSwitch());
        tbAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserManager.getIns().setAutoSwitch(isChecked);
                if (isChecked) {
                    tvSelectedLineLab.setText("正在切换线路...");
                    tvSelectedLine.setVisibility(View.INVISIBLE);
                    ThreadCollector.getIns().postDelayOnUIThread(100, new Runnable() {
                        @Override
                        public void run() {
                            int index = 0;
                            for (int i = 0; i < urls.size(); i++) {
                                if (Strs.isNotEmpty(adapter.getItem(i).getStatus())) {//保障线路畅通
                                    if (Nums.parse(adapter.getItem(i).getSpeed().get("delay"), 10000) <
                                            Nums.parse(adapter.getItem(index).getSpeed().get("delay"), 10000)) {
                                        index = i;//选出最佳线路
                                    }
                                }
                            }
                            final int finalIndex = index;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    switchLine(finalIndex, adapter, ctx, tvSelectedLine, tvSelectedLineLab);
                                }
                            });
                        }
                    });
                }
            }
        });
        ToggleButton tbFloat = (ToggleButton) contentView.findViewById(R.id.tbFloat);
        tbFloat.setChecked(UserManager.getIns().isShowFloat());
        tbFloat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserManager.getIns().setShowFloat(isChecked);
                if (!isChecked) {
                    if (mPopWindow != null && mPopWindow.isShowing()) {
                        mPopWindow.dismiss();
                    }
                    FloatActionController.getInstance().hide();
                    EventBus.getDefault().post(new CloseFloatEvent());
                }
            }
        });
        //显示PopupWindow
        mPopWindow.showAtLocation(ctx.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (listPing != null) {
                    for (int i = 0; i < listPing.size(); i++) {
                        if (listPing.get(i) != null) {
                            listPing.get(i).cancel();
                        }
                    }
                    listPing = null;
                }
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                }
            }
        });

        final String urlOrigin = UserManager.getIns().getUrlFevorite();

        handler = new Handler();
        listPing = new ArrayList<Ping>();
        for (int i = 0; i < urls.size(); i++) {
            final String host = urls.get(i).replace("http://", "").replace("https://", "");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                int position = 0;
                                for (int j = 0; j < urls.size(); j++) {
                                    if (urls.get(j).replace("http://", "").replace("https://", "").equalsIgnoreCase(host)) {
                                        position = j;
                                        break;
                                    }
                                }

                                adapter.notifyDataSetChanged();

                            }
                        }, 5000);
                        Ping ping = Ping.onAddress(host).setTimeOutMillis(3000).setTimes(1).doPing(new Ping.PingListener() {
                            @Override
                            public void onResult(PingResult pingResult) {
                            }

                            @Override
                            public void onFinished(PingStats pingStats) {
                                int position = 0;
                                for (int j = 0; j < urls.size(); j++) {
                                    if (urls.get(j).replace("http://", "").replace("https://", "").equalsIgnoreCase(pingStats.getAddress().getHostName())) {
                                        position = j;
                                        break;
                                    }
                                }
                                adapter.getItem(position).getSpeed().put("delay", String.valueOf((int) (pingStats.getMaxTimeTaken()) <= 0 ? "" : (int) (pingStats.getMaxTimeTaken())));

                                //ping完后 进行json访问测试,看此线路是否真正畅通
                                final int finalPosition = position;
                                HttpAction.getTeappJson(this, "http://" + host, new AbHttpResult() {
                                    @Override
                                    public boolean onGetString(String str) {
                                        if (Strs.isEqual("1", str)) {
                                            adapter.getItem(finalPosition).setStatus("yes");//真正通畅的线路
                                            adapter.notifyDataSetChanged();
                                        }
                                        return true;
                                    }

                                    @Override
                                    public void onFinish() {
                                        super.onFinish();
                                        //犹豫getTeappJson 测试更换了host,所以测试完后需要把域名改回来以前的
                                        ENV.curr.host = urlOrigin;
                                        MM.http.setHost(urlOrigin);
                                        UserManager.getIns().setUrlFevorite(urlOrigin);
                                    }
                                });

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                        listPing.add(ping);
                    } catch (Exception e) {
                        int position = -1;
                        for (int j = 0; j < urls.size(); j++) {
                            if (e.getMessage().contains(urls.get(j).replace("http://", "").replace("https://", ""))) {
                                position = j;
                                break;
                            }
                        }

                        if (adapter.getItem(position) != null) {
                            if (e instanceof UnknownHostException) {
                                //ping速发生异常了 也需要再 进行json访问测试,看此线路是否真正畅通
                                final int finalPosition = position;
                                HttpAction.getTeappJson(this, "http://" + host, new AbHttpResult() {
                                    @Override
                                    public boolean onError(int status, String content) {
                                        adapter.getItem(finalPosition).getSpeed().put("delay", "999ms");
                                        return super.onError(status, content);
                                    }

                                    @Override
                                    public boolean onGetString(String str) {
                                        if (Strs.isEqual("1", str)) {
                                            adapter.getItem(finalPosition).getSpeed().put("delay", String.valueOf(new Random().nextInt(150) + 50));
                                            adapter.getItem(finalPosition).setStatus("yes");//真正通畅的线路
                                            adapter.notifyDataSetChanged();
                                        }
                                        return true;
                                    }

                                    @Override
                                    public void onFinish() {
                                        super.onFinish();
                                        //犹豫getTeappJson 测试更换了host,所以测试完后需要把域名改回来以前的
                                        ENV.curr.host = urlOrigin;
                                        MM.http.setHost(urlOrigin);
                                        UserManager.getIns().setUrlFevorite(urlOrigin);
                                    }
                                });
                            } else {
                                //ping速发生异常了 也需要再 进行json访问测试,看此线路是否真正畅通
                                final int finalPosition = position;
                                HttpAction.getTeappJson(this, "http://" + host, new AbHttpResult() {

                                    @Override
                                    public boolean onError(int status, String content) {
                                        adapter.getItem(finalPosition).getSpeed().put("delay", "");
                                        return super.onError(status, content);
                                    }

                                    @Override
                                    public boolean onGetString(String str) {
                                        if (Strs.isEqual("1", str)) {
                                            adapter.getItem(finalPosition).getSpeed().put("delay", String.valueOf(new Random().nextInt(150) + 50));
                                            adapter.getItem(finalPosition).setStatus("yes");//真正通畅的线路
                                            adapter.notifyDataSetChanged();
                                        }
                                        return true;
                                    }

                                    @Override
                                    public void onFinish() {
                                        super.onFinish();
                                        //犹豫getTeappJson 测试更换了host,所以测试完后需要把域名改回来以前的
                                        ENV.curr.host = urlOrigin;
                                        MM.http.setHost(urlOrigin);
                                        UserManager.getIns().setUrlFevorite(urlOrigin);
                                    }
                                });
                            }

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }

                    if (UserManager.getIns().isAutoSwitch()) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvSelectedLineLab.setText("正在切换线路...");
                                tvSelectedLine.setVisibility(View.INVISIBLE);
                            }
                        });

                        ThreadCollector.getIns().postDelayOnUIThread(2000, new Runnable() {
                            @Override
                            public void run() {
                                int index = 0;
                                for (int i = 0; i < urls.size(); i++) {
                                    if (Nums.parse(adapter.getItem(i).getSpeed().get("delay"), 10000) <
                                            Nums.parse(adapter.getItem(index).getSpeed().get("delay"), 10000)) {
                                        index = i;
                                    }
                                }
                                final int finalIndex = index;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        switchLine(finalIndex, adapter, ctx, tvSelectedLine, tvSelectedLineLab);
                                    }
                                });
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private static boolean switch_success = false;

    public static void switchLine(int position, ItemLineAdapter adapter, final Activity ctx, final TextView tvSelectedLine, final TextView tvSelectedLineLab) {
        String finalUrl = adapter.getItem(position).getSpeed().get("url");
        final String oldUrl = ENV.curr.host;
        final String text = "线路" + (position + 1);
        ENV.curr.host = finalUrl;
        MM.http.setHost(finalUrl);

        tvSelectedLine.setVisibility(View.VISIBLE);
        tvSelectedLineLab.setText("当前线路: ");
        tvSelectedLine.setText(text);
        UserManager.getIns().setUrlFevorite(finalUrl);

        if ("sso".equalsIgnoreCase(Config.HOST_USER_PREFIX)) { //切线路掉线, 直接切, 其他页去登录
            if (!switch_success) {
                switch_success = true;
                Toasts.show(ctx, "切换成功, 请重新登录!", true);
            }

            mPopWindow.dismiss();
            ThreadCollector.getIns().postDelayOnUIThread(1000, new Runnable() {
                @Override
                public void run() {
                    UserManager.getIns().redirectToLoginForRecheck();
                }
            });

        } else {  //切换线路不掉线.
            UserManager.getIns().initUserDataForceCookie(new UserManager.IUserDataSyncCallback() {
                @Override
                public void onUserDataInited() {
                    //nothing
                }

                @Override
                public void afterUserDataInited() {
                    if (!switch_success) {
                        Toasts.show(ctx, "切换成功!", true);
                        switch_success = true;
                    }

                    mPopWindow.dismiss();
                }

                @Override
                public void onUserDataInitFaild() {
                    //nothing
                    Toasts.show(ctx, "切换失败!", false);
                }

                @Override
                public void onAfter() {
                    //nothing
                }
            });
        }
    }


    static class LineStatusEntry {
        String status;
        HashMap<String, String> speed;

        public LineStatusEntry(HashMap<String, String> speed) {
            this.speed = speed;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public HashMap<String, String> getSpeed() {
            return speed;
        }

        public void setSpeed(HashMap<String, String> speed) {
            this.speed = speed;
        }
    }

    public static class ItemLineAdapter extends BaseAdapter {

        private List<LineStatusEntry> listDatas = new ArrayList<>();

        private Context context;
        private LayoutInflater layoutInflater;

        public ItemLineAdapter(Context context) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(context);
            List<String> apUrlList;
            if (Strs.isEqual(BaseConfig.FLAG_TOUCAI, BaseConfig.custom_flag)) {
                apUrlList = UserManager.getIns().getUrls();
            } else {
                apUrlList = UserManager.getIns().getApUrls();
            }

            if (apUrlList.isEmpty()) {
                return;
            }

            int size = apUrlList.size();
            for (int i = 0, l = size; i < l; i++) {
                HashMap<String, String> map = new HashMap<>();
                map.put("url", apUrlList.get(i));
                LineStatusEntry lineStatusEntry = new LineStatusEntry(map);
                listDatas.add(lineStatusEntry);
            }
        }

        @Override
        public int getCount() {
            return listDatas.size();
        }

        @Override
        public LineStatusEntry getItem(int position) {
            if (position < 0) {
                return null;
            }
            return listDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_line, null);
                convertView.setTag(new ViewHolder(convertView));
            }

            if (null != getItem(position)) {
                initializeViews(position, getItem(position), (ViewHolder) convertView.getTag());
            }
            return convertView;
        }

        private void initializeViews(int position, LineStatusEntry item, ViewHolder holder) {
            holder.tvLineName.setText("线路" + (position + 1));
            if (Strs.isNotEmpty(item.getStatus())) {
                holder.tvgoodSpeed.setVisibility(View.VISIBLE);
            }

            if (Strs.isEmpty(item.getSpeed().get("delay"))) {
                if (Strs.isNotEmpty(item.getStatus())) {
                    holder.tvLineSpeed.setText(String.valueOf(new Random().nextInt(150) + 50) + "ms");
                    holder.tvgoodSpeed.setBackgroundResource(R.drawable.line_speed_tips_green);
                    holder.tvgoodSpeed.setText("极 快");
                    holder.tvLineSpeed.setTextColor(Color.BLUE);
                } else {
                    holder.tvLineSpeed.setText("测试中...⏳");
                    holder.tvLineSpeed.setTextColor(Color.BLUE);
                }

            } else {
                if (Strs.parse(item.getSpeed().get("delay"), 0) > 0) {

                    if (Strs.parse(item.getSpeed().get("delay"), 0) <= 200) {
                        holder.tvgoodSpeed.setText("极 快");
                        holder.tvgoodSpeed.setBackgroundResource(R.drawable.line_speed_tips_green);
                    } else if (Strs.parse(item.getSpeed().get("delay"), 0) <= 400) {
                        holder.tvgoodSpeed.setText("较 快");
                        holder.tvgoodSpeed.setBackgroundResource(R.drawable.line_speed_tips_green);
                    } else if (Strs.parse(item.getSpeed().get("delay"), 0) <= 800) {
                        holder.tvgoodSpeed.setText("畅 通");
                        holder.tvgoodSpeed.setBackgroundResource(R.drawable.line_speed_tips_green);
                    } else if (Strs.parse(item.getSpeed().get("delay"), 0) < 999) {
                        holder.tvgoodSpeed.setText("较 慢");
                        holder.tvgoodSpeed.setBackgroundResource(R.drawable.line_speed_tips_yellow);
                    }
                    holder.tvLineSpeed.setText(item.getSpeed().get("delay") + "ms");
                    holder.tvLineSpeed.setTextColor(Color.BLUE);
                } else {
                    holder.tvLineSpeed.setText(item.getSpeed().get("delay"));
                    holder.tvLineSpeed.setTextColor(Color.RED);
                    holder.tvgoodSpeed.setText("缓 慢");
                    holder.tvgoodSpeed.setBackgroundResource(R.drawable.line_speed_tips_red);
                }
            }

        }

        protected class ViewHolder {
            private TextView tvLineName;
            private TextView tvLineSpeed;
            private TextView tvgoodSpeed;

            public ViewHolder(View view) {
                tvLineName = view.findViewById(R.id.tvLineName);
                tvLineSpeed = view.findViewById(R.id.tvLineSpeed);
                tvgoodSpeed = view.findViewById(R.id.good_line);
            }
        }
    }

}
