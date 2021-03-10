package com.desheng.base.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ab.global.Config;
import com.ab.global.ENV;
import com.ab.module.MM;
import com.ab.thread.ThreadCollector;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.desheng.base.controller.FloatActionController;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.FloatWindowManager;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.VersionUpdateInfo;
import com.desheng.base.receiver.HomeWatcherReceiver;
import com.desheng.base.view.FloatCallBack;
import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.ping.PingResult;
import com.stealthcopter.networktools.ping.PingStats;
import com.tencent.bugly.crashreport.BuglyLog;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 悬浮窗在服务中创建，通过暴露接口FloatCallBack与Activity进行交互
 */
public class FloatMonkService extends Service implements FloatCallBack {
    /**
     * home键监听
     */
    private HomeWatcherReceiver mHomeKeyReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        FloatActionController.getInstance().registerCallLittleMonk(this);
        //注册广播接收者
        mHomeKeyReceiver = new HomeWatcherReceiver();
        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeKeyReceiver, homeFilter);
        //初始化悬浮窗UI
        initWindowData();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 初始化WindowManager
     */
    private void initWindowData() {
        FloatWindowManager.createFloatWindow(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //移除悬浮窗
        FloatWindowManager.removeFloatWindowManager();
        //注销广播接收者
        if (null != mHomeKeyReceiver) {
            unregisterReceiver(mHomeKeyReceiver);
        }
    }

    /////////////////////////////////////////////////////////实现接口////////////////////////////////////////////////////
    @Override
    public void guideUser(int type) {
        FloatWindowManager.updataRedAndDialog(this);
    }


    /**
     * 悬浮窗的隐藏
     */
    @Override
    public void hide() {
        FloatWindowManager.hide();
    }

    /**
     * 悬浮窗的显示
     */
    @Override
    public void show(Activity act) {
        FloatWindowManager.show(act);
    }

    /**
     * 添加数量
     */
    @Override
    public void addObtainNumer() {
        FloatWindowManager.addObtainNumer(this);
        guideUser(4);
    }

    /**
     * 减少数量
     */
    @Override
    public void setObtainNumber(int number) {
        FloatWindowManager.setObtainNumber(this, number);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("FloatMonkService", "onStartCommand");

        UserManager.getIns().checkOnlineConfig(this, new UserManager.IConfigUpdateCallback() {
            @Override
            public void onGetConfig(VersionUpdateInfo info) {
                Log.e("FloatMonkService", "onStartCommand---onGetConfig");
                testLineSpeed();
            }

            @Override
            public void onFail() {
                String urlFevorite = UserManager.getIns().getUrlFevorite();
                if (Strs.isNotEmpty(urlFevorite)) {
                    MM.http.setHost(urlFevorite);
                    ENV.curr.host = urlFevorite;
                } else if (Strs.isNotEmpty(BaseConfig.ForceHost)) {
                    MM.http.setHost(BaseConfig.ForceHost);
                    ENV.curr.host = BaseConfig.ForceHost;
                }

                BuglyLog.e("FloatMonkService", "========= onFail() ====> 使用写死host" + ENV.curr.host);
                Log.e("FloatMonkService", "========= onFail() ====> 使用写死host" + ENV.curr.host);
            }
        });
        return START_NOT_STICKY;
    }

    private static ArrayList<Ping> listPing;
    Handler mHandler;

    public void testLineSpeed() {
        final ArrayList<String> urls;
        if (Strs.isEqual(BaseConfig.FLAG_TOUCAI, BaseConfig.custom_flag)) {
            urls = UserManager.getIns().getUrls();
        } else {
            urls = UserManager.getIns().getApUrls();
        }

        if (urls == null || urls.size() == 0) {
            return;
        }

        listPing = new ArrayList<Ping>();
        final ItemLineAdapter adapter = new ItemLineAdapter(this);

        mHandler = new Handler();
        for (int i = 0; i < urls.size(); i++) {
            final String host = urls.get(i).replace("http://", "").replace("https://", "");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                int position = 0;
                                for (int j = 0; j < urls.size(); j++) {
                                    if (urls.get(j).replace("http://", "").replace("https://", "").equalsIgnoreCase(host)) {
                                        position = j;
                                        break;
                                    }
                                }

                                if (Strs.parse(adapter.getItem(position).get("delay"), 0) <= 0) {
                                    adapter.getItem(position).put("delay", "超时");
                                    adapter.notifyDataSetChanged();
                                }
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
                                adapter.getItem(position).put("delay", String.valueOf((int) (pingStats.getMaxTimeTaken()) <= 0 ? "" : (int) (pingStats.getMaxTimeTaken())));

                                BuglyLog.e("FloatMonkService", "线路：" + "--" + host + "--线路延迟（ms）：" + adapter.getItem(position).get("delay"));
                                Log.e("FloatMonkService", "线路：" + "--" + host + "--线路延迟（ms）：" + adapter.getItem(position).get("delay"));

                                mHandler.post(new Runnable() {
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
                        if (e instanceof UnknownHostException) {
                            adapter.getItem(position).put("delay", "超时");
                        } else {
                            adapter.getItem(position).put("delay", "");
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                    if (UserManager.getIns().isNotLogined()) {
                        ThreadCollector.getIns().postDelayOnUIThread(2000, new Runnable() {
                            @Override
                            public void run() {
                                int index = 0;
                                for (int i = 0; i < urls.size(); i++) {
                                    if (Nums.parse(adapter.getItem(i).get("delay"), 10000) <
                                            Nums.parse(adapter.getItem(index).get("delay"), 10000)) {
                                        index = i;
                                    }
                                }
                                final int finalIndex = index;
                                Log.e("FloatMonkService", "switchLine---------index:" + index);
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        switchLine(finalIndex, adapter);
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

    public static void switchLine(int position, ItemLineAdapter adapter) {
        String finalUrl = adapter.getItem(position).get("url");

        BuglyLog.e("FloatMonkService", "switchLine---------finalUrl:" + finalUrl);
        Log.e("FloatMonkService", "线路自动切换至最佳线路---------finalUrl:" + finalUrl);

        //检查url,否为空时会导致AbHttpAOe类中的getHostWithoutHttp() 方法获取不到域名-->导致.domain(getHostWithoutHttp())报数据格式异常错误
        if (Strs.isEmpty(finalUrl)) {
            finalUrl = Config.isDebug() ? BaseConfig.HOST_TEST : BaseConfig.ForceHost;
            Log.e("FloatMonkService", "finalUrl为空，使用默认域名访问:ENV.curr.host：" + finalUrl);
        }

        ENV.curr.host = finalUrl;
        MM.http.setHost(finalUrl);

        UserManager.getIns().setUrlFevorite(finalUrl);

        if ("sso".equalsIgnoreCase(Config.HOST_USER_PREFIX)) { //切线路掉线, 直接切, 其他页去登录
            if (!switch_success) {
                switch_success = true;
                BuglyLog.e("FloatMonkService", "切换成功, 请重新登录!");
            }

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
                        BuglyLog.e("FloatMonkService", "切换成功!");
                        switch_success = true;
                    }
                }

                @Override
                public void onUserDataInitFaild() {
                    //nothing
                    BuglyLog.e("FloatMonkService", "切换失败!");
                }

                @Override
                public void onAfter() {
                    //nothing
                }
            });
        }
    }

    public static class ItemLineAdapter extends BaseAdapter {

        private List<HashMap<String, String>> listDatas = new ArrayList<>();

        private Context context;
        private LayoutInflater layoutInflater;

        public ItemLineAdapter(Context context) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(context);

            List<String> urls;
            if (Strs.isEqual(BaseConfig.FLAG_TOUCAI, BaseConfig.custom_flag)) {
                urls = UserManager.getIns().getUrls();
            } else {
                urls = UserManager.getIns().getApUrls();
            }
            int size = urls.size();
            for (int i = 0; i < size; i++) {
                HashMap<String, String> map = new HashMap<>();
                map.put("url", urls.get(i));
                listDatas.add(map);
            }
        }

        @Override
        public int getCount() {
            return listDatas.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
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
            return convertView;
        }
    }
}
