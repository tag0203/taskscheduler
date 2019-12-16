package com.example.tag.taskscheduler;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class AlarmNotification extends Activity {
    private WakeLock wakelock;
    private KeyguardLock keylock;

    private MediaPlayer mp;
    private Button finish;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);
        Log.v("通知ログ", "create");

        //wakelock,keylockを使用する場合onPauseのコメントアウトも外す
        // スリープ状態から復帰する
//        wakelock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
//                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
//                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
//                        | PowerManager.ON_AFTER_RELEASE, "disableLock");
//        wakelock.acquire();
//
//        // スクリーンロックを解除する
//        KeyguardManager keyguard = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        keylock = keyguard.newKeyguardLock("disableLock");
//        keylock.disableKeyguard();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        finish = (Button)findViewById(R.id.alarm_stop);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mp == null)
            mp = MediaPlayer.create(this, R.raw.alarm);
        mp.setLooping(true);
        mp.start();
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.v("デバッグ", "アラームonpause");
//
//        //wakelock,keylockをリリース、これがないと画面が操作不可状態に陥る
//        //このままだとアプリ使用時に落ちる
//        wakelock.release();
//        keylock.reenableKeyguard();
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopAndRelaese();
    }

    private void stopAndRelaese() {
        if (mp != null) {
            mp.stop();
            mp.release();
        }
    }
}
