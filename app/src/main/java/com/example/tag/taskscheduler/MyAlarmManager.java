package com.example.tag.taskscheduler;

import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class MyAlarmManager {
    Context c;
    AlarmManager am;
    private PendingIntent mAlarmSender;

    public MyAlarmManager(Context c){
        this.c = c;
        am = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        Log.v("MyAlarmManger","初期化完了");
    }

    public void addAlarm(/*今はなにもなしで*/){
        mAlarmSender = PendingIntent.getService(c, -1, new Intent(c, MyAlarmService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        // アラーム時間設定
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.SECOND, 10);
        cal.set(Calendar.MILLISECOND, 0);
        Log.v("MyAlarmManagerログ",cal.getTimeInMillis()+"ms");
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), mAlarmSender);
        Log.v("MyAlarmManagerログ","アラームセット完了");
    }

    public void addAlarm(int time){
        mAlarmSender = PendingIntent.getService(c, -1, new Intent(c, MyAlarmService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        // アラーム時間設定
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, time/3600);
        cal.set(Calendar.MINUTE, (time%3600)/60);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // 過去だったら明日にする
        if(cal.getTimeInMillis() < System.currentTimeMillis()){
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        Log.v("MyAlarmManagerログ",cal.getTimeInMillis()+"ms");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            am.setAlarmClock(new AlarmManager.AlarmClockInfo(cal.getTimeInMillis(), null), mAlarmSender);
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            am.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), mAlarmSender);
        }else {
            am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), mAlarmSender);
        }
        Log.v("MyAlarmManagerログ","アラームセット完了");
    }

    public void cancel(){
        mAlarmSender.cancel();
        am.cancel(mAlarmSender);
    }
}