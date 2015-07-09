package aw.untas.com.timerappwidget;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TimerService extends Service {

    private static final String TAG="TSA";
    private static String ACTION_UPDATE_ALL="com.untas.UPDATE_ALL";

    private static String ACTION_SERVICE_STOP="com.untas.ACTION_SERVICE_STOP";

    private static final int UPDATE_TIME = 1000;
    // 周期性更新 widget 的线程
    private UpdateThread mUpdateThread;
    private Context mContext;



    @Override
    public void onCreate() {
        Log.d(TAG,"Service Createed");
        // 创建并开启线程UpdateThread
        mUpdateThread = new UpdateThread();
        mUpdateThread.start();

        mContext = this.getApplicationContext();

        super.onCreate();
    }

    @Override
    public void onDestroy(){


        // 中断线程，即结束线程。
        if (mUpdateThread != null) {
            mUpdateThread.interrupt();
        }

        mContext.sendBroadcast(new Intent(ACTION_SERVICE_STOP));
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * 服务开始时，即调用startService()时，onStartCommand()被执行。
     * onStartCommand() 这里的主要作用：
     * (01) 将 appWidgetIds 添加到队列sAppWidgetIds中
     * (02) 启动线程
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    private class UpdateThread extends Thread {
        @Override
        public void run() {
            super.run();

            try {

                while (true) {
                    Intent updateIntent=new Intent(ACTION_UPDATE_ALL);
                    mContext.sendBroadcast(updateIntent);
                    Thread.sleep(UPDATE_TIME);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
