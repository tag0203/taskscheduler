package com.example.tag.taskscheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class MyAlarmNotificationReceiver extends BroadcastReceiver {
    private static PowerManager.WakeLock wl;

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyAlarmNotificationReceiver");
        wl.acquire();
        Log.v("レシーバログ", "action: " + intent.getAction());
        Intent notification = new Intent(context,
                AlarmNotification.class);
        //ここがないと画面を起動できません
        notification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(notification);
    }

    public static void releaseWakeLock() {
        if (wl != null) {
            Log.v("test", "wake lock relese");
            wl.release();
            wl = null;
        }
    }
}