package aw.untas.com.timerappwidget;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Implementation of App Widget functionality.
 */
public class TimerAppWidget extends AppWidgetProvider {

    private static final String TAG = "TSA";

    private boolean DEBUG = true;
    // 启动ExampleAppWidgetService服务对应的action
    private final static Intent TIMER_SERVICE_INTENT =
            new Intent("aw.untas.com.TIMER_SERVICE");
    // 更新 widget 的广播对应的action
    private final String ACTION_UPDATE_ALL = "com.untas.UPDATE_ALL";

    private static String ACTION_SERVICE_STOP="com.untas.ACTION_SERVICE_STOP";
    // 保存 widget 的id的HashSet，每新建一个 widget 都会为该 widget 分配一个 id。
    private static Set<Integer> idsSet = new HashSet();



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate(): appWidgetIds.length="+appWidgetIds.length);
        // 每次 widget 被创建时，对应的将widget的id添加到set中
        for (int appWidgetId : appWidgetIds) {
            idsSet.add(Integer.valueOf(appWidgetId));
        }

        startServiceIfNo(context);
        prtSet();

    }

    // 当 widget 被初次添加 或者 当 widget 的大小被改变时，被调用
    @Override
    public void onAppWidgetOptionsChanged(Context context,
                                          AppWidgetManager appWidgetManager, int appWidgetId,
                                          Bundle newOptions) {
        Log.d(TAG, "onAppWidgetOptionsChanged");
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
                newOptions);
    }

    // widget被删除时调用
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(TAG, "onDeleted(): appWidgetIds.length="+appWidgetIds.length);

        // 当 widget 被删除时，对应的删除set中保存的widget的id
        for (int appWidgetId : appWidgetIds) {
            idsSet.remove(Integer.valueOf(appWidgetId));
        }
        prtSet();

        super.onDeleted(context, appWidgetIds);
    }

    // 第一个widget被创建时调用
    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled");
        // 在第一个 widget 被创建时，开启服务
       startServiceIfNo(context);
        super.onEnabled(context);
    }

    // 最后一个widget被删除时调用
    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled");

        // 在最后一个 widget 被删除时，终止服务
        context.stopService(TIMER_SERVICE_INTENT);

        super.onDisabled(context);
    }

    // 接收广播的回调函数
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (ACTION_UPDATE_ALL.equals(action)) {
            // “更新”广播
            updateAllAppWidgets(context, AppWidgetManager.getInstance(context), idsSet);
        }else if(ACTION_SERVICE_STOP.equals(action)){
            startServiceIfNo(context);
        }

        super.onReceive(context, intent);
    }

    private static void startServiceIfNo(Context context){

        if(!isServiceWork(context,TimerService.class.getName())) //如果服务被停止，将重新启动
        {
            Log.d(TAG,"启动service");
          //  context.startService(new Intent(context, TimerService.class));
            context.startService(TIMER_SERVICE_INTENT);
        }
    }

    private static  final DateFormat DF=new SimpleDateFormat("HH : mm : ss");
    private static void updateAllAppWidgets(Context context, AppWidgetManager manager, Set<Integer> idsSet) {

        Date now = new Date();
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.timer_widget_1);
        for(Integer tmpAppId:idsSet){
            int appId=tmpAppId.intValue();

           // remoteView.setTextViewText(R.id.appwidget_text,DF.format(date));
            int h=now.getHours();
            remoteView.setImageViewResource(R.id.nb_h0,Utils.NUMBERS[h%10]);
            remoteView.setImageViewResource(R.id.nb_h1,Utils.NUMBERS[h/10]);

            int m=now.getMinutes();
            remoteView.setImageViewResource(R.id.nb_m0,Utils.NUMBERS[m%10]);
            remoteView.setImageViewResource(R.id.nb_m1,Utils.NUMBERS[m/10]);

            int s = now.getSeconds();
            remoteView.setImageViewResource(R.id.nb_s0,Utils.NUMBERS[s%10]);
            remoteView.setImageViewResource(R.id.nb_s1,Utils.NUMBERS[s/10]);

            manager.updateAppWidget(appId,remoteView);
        }
    }

    public static boolean  isServiceWork(Context mContext, String serviceName) {

        boolean isWork = false;
        ActivityManager myAM = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = (List<ActivityManager.RunningServiceInfo>)myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for(int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            Log.d(TAG,mName);
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }


    // 调试用：遍历set
    private void prtSet() {
        if (DEBUG) {
            int index = 0;
            int size = idsSet.size();
            Iterator it = idsSet.iterator();
            Log.d(TAG, "total:" + size);
            while (it.hasNext()) {
                Log.d(TAG, index + " -- " + ((Integer)it.next()).intValue());
            }
        }
    }
}

