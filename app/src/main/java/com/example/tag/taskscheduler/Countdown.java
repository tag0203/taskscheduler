package com.example.tag.taskscheduler;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Countdown extends AppCompatActivity {

    private TextView task;
    private TextView timerText;
    private TextView nexttask;
    private String[] mDataset = new String[20]; //タスク名
    private int[] time = new int[20]; //タスクの理想時間
    private int[] cut_time = new int[20]; //タスクを省略してもよい時間
    private int[] ct = new int[20];
    private int[] cut_task = new int[20];
    private int task_amount; //タスクの数
    private int i = 0; //現在実行されているタスク
    private int k = 0; //現在省略されているタスク数
    private int m_ct = 0; //カットできるタスク数
    private int now_time; //現在の時刻
    private int f_time; //タスクを終了する時間、出発時間
    private Calendar c1;
    private CountDown countDown;
    private FinishCountDown finishCountDown;

    private int alltime;
    private int allctime;

    private final SimpleDateFormat HdataFormat =
            new SimpleDateFormat("H時間mm分ss秒", Locale.US);

    private final SimpleDateFormat MdataFormat =
            new SimpleDateFormat("mm分ss秒", Locale.US);

    private final SimpleDateFormat SdataFormat =
            new SimpleDateFormat("ss秒", Locale.US);

    private final SimpleDateFormat df =
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

    private final SimpleDateFormat fdf =
            new SimpleDateFormat("H時間mm分", Locale.US);

    private long interval = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countdown);

        //タスクのデータを受け取る
        Intent intent = getIntent();
        mDataset = intent.getStringArrayExtra("task");
        time = intent.getIntArrayExtra("time");
        cut_time = intent.getIntArrayExtra("cut time");
        task_amount = intent.getIntExtra("amount",0);
        f_time = intent.getIntExtra("finish time",0);
        ct = intent.getIntArrayExtra("cut task");

        for(i=0;i<task_amount;i++){
            if(ct[i]!=0){
                cut_task[ct[i]-1] = i;
                m_ct++;
            }
        }
        i = 0;

        Button nextButton = findViewById(R.id.next_button);
        task = findViewById(R.id.task_name);

        timerText = findViewById(R.id.timer);
        timerText.setText(SdataFormat.format(0));

        nexttask = findViewById(R.id.next_time);

        start_log();
        scheduling();
        c1 = Calendar.getInstance();
        now_time = (c1.get(Calendar.HOUR_OF_DAY))*3600+(c1.get(Calendar.MINUTE))*60+(c1.get(Calendar.SECOND));
        if(f_time > now_time) {
            finishCountDown = new FinishCountDown((f_time - now_time) * 1000, 10000);
            finishCountDown.start();
        }

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDown.cancel();
                countDown.soundrelease();
                i++;
                if(i >= task_amount){
                    logFileOutput("実行終了");
                    Toast toast = Toast.makeText(Countdown.this, "全てのタスクが終了しました！", Toast.LENGTH_LONG);
                    toast.show();
                    finish();
                }else {
                    if (k > 0) k--;
                    scheduling();
                }
            }
        });
    }

    //短縮アルゴリズム
    protected void scheduling(){
        //現在時刻を取得(秒)
        c1 = Calendar.getInstance();
        now_time = (c1.get(Calendar.HOUR_OF_DAY))*3600+(c1.get(Calendar.MINUTE))*60+(c1.get(Calendar.SECOND));

        //出発時刻との差を計算
        if(now_time <= f_time){
            now_time = f_time - now_time;

            //求まった差とタスクの合計時間を比較
            allctime = 0;
            alltime = 0;
            for(int l=i;l<task_amount;l++){
                alltime += time[l];
                allctime += cut_time[l];
            }

            if(alltime <= now_time){
//                while(ct[i] != 0 && ct[i] <= k){
//                    i++;
//                }
                now_time = now_time - alltime;
                countDown = new CountDown((time[i] + (time[i] * now_time / alltime)) * 1000, interval);
                logFileOutput("タスク加算 タスク名:"+mDataset[i]+" タスク時間:"+((time[i] + (time[i] * now_time / alltime))/60)+"分"+((time[i] + (time[i] * now_time / alltime))%60)+"秒");
                if(i+1 < task_amount){
                    int ntime = 0;
                    ntime = (time[i+1] + (time[i+1] * now_time / alltime))*1000;
                    if(ntime < 60000){
                        nexttask.setText(mDataset[i+1]+" "+SdataFormat.format(ntime+54000000));
                    }else if(ntime < 3600000){
                        nexttask.setText(mDataset[i+1]+" "+MdataFormat.format(ntime+54000000));
                    }else{
                        nexttask.setText(mDataset[i+1]+" "+HdataFormat.format(ntime+54000000));
                    }
                }else{
                    nexttask.setText("次のタスクはありません");
                }
            }else{
                for(k=0;k<m_ct;k++){
                    if(now_time > alltime - allctime){
                        break;
                    }
                    if(cut_task[k] >= i){
                        alltime -= time[cut_task[k]];
                        allctime -= cut_time[cut_task[k]];
                    }
                }
                if(now_time < alltime - allctime){
                    logFileOutput("タスクを削っても間に合わないため実行終了" + now_time);
                    Toast toast = Toast.makeText(Countdown.this, "削減可能なタスクを削っても間に合わないため実行終了", Toast.LENGTH_LONG);
                    toast.show();
                    finish();
                }
                while(ct[i] != 0 && ct[i] <= k && i < task_amount){
                    i++;
                }
                //タスク時間の計算
                now_time = alltime - now_time;
                countDown = new CountDown((time[i] - (now_time*cut_time[i]/allctime)) * 1000, interval);
                logFileOutput("タスク省略 タスク名:"+mDataset[i]+" タスク時間:"+(time[i] - (now_time*cut_time[i]/allctime))/60+"分"+(time[i] - (now_time*cut_time[i]/allctime))%60+"秒");
                int m = 1;
                while(ct[i+m] != 0 && ct[i+m] <= k && i+m < task_amount){
                    m++;
                }
                if(i+m < task_amount){
                    int ntime = 0;
                    ntime = (time[i+m] - (now_time*cut_time[i+m]/allctime)) * 1000;
                    if(ntime < 60000){
                        nexttask.setText(mDataset[i+1]+" "+SdataFormat.format(ntime+54000000));
                    }else if(ntime < 3600000){
                        nexttask.setText(mDataset[i+1]+" "+MdataFormat.format(ntime+54000000));
                    }else{
                        nexttask.setText(mDataset[i+1]+" "+HdataFormat.format(ntime+54000000));
                    }
                }else{
                    nexttask.setText("次のタスクはありません");
                }
            }
            task.setText(mDataset[i]);
            countDown.start();
        }else{
            logFileOutput("出発時間が現在時刻より前なので実行終了");
            Toast toast = Toast.makeText(Countdown.this, "出発時刻が現在時刻より前です", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
    }

    class CountDown extends CountDownTimer {
        MediaPlayer finish_alarm;
        private int fivem_flag = 1;
        private int onem_flag = 1;
        private int thirtys_flag = 1;
        private int tens_flag = 1;

        private SoundPool sp;
        private int soundId1;
        private int soundId2;

        CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            sp = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
            soundId1 = sp.load(Countdown.this, R.raw.bell1, 1);
            soundId2 = sp.load(Countdown.this, R.raw.bell2, 1);
        }

        @Override
        public void onFinish() {
            // 完了
            finish_alarm = MediaPlayer.create(Countdown.this, R.raw.alarm);
            finish_alarm.start();
            timerText.setText(SdataFormat.format(54000000));
            finish_alarm.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    finish_alarm.release();
                    finish_alarm = null;
                }
            });
            sp.release();
            sp = null;
        }

        // インターバルで呼ばれる
        @Override
        public void onTick(long millisUntilFinished) {
            // 残り時間を分、秒、ミリ秒に分割
            //long mm = millisUntilFinished / 1000 / 60;
            //long ss = millisUntilFinished / 1000 % 60;
            //long ms = millisUntilFinished - ss * 1000 - mm * 1000 * 60;
            //timerText.setText(String.format("%1$02d:%2$02d.%3$03d", mm, ss, ms));

            if(millisUntilFinished < 60000){
                timerText.setText(SdataFormat.format(millisUntilFinished+54000000));
            }else if(millisUntilFinished < 3600000){
                timerText.setText(MdataFormat.format(millisUntilFinished+54000000));
            }else{
                timerText.setText(HdataFormat.format(millisUntilFinished+54000000));
            }

            if(millisUntilFinished < 10000 && tens_flag == 1){
                fivem_flag = 0;
                onem_flag = 0;
                thirtys_flag = 0;
                tens_flag = 0;
                sp.play(soundId1, 0.7F, 0.7F, 0, 0, 1.0F);
            }else if(millisUntilFinished < 30000 && thirtys_flag == 1){
                fivem_flag = 0;
                onem_flag = 0;
                thirtys_flag = 0;
                sp.play(soundId1, 0.7F, 0.7F, 0, 0, 1.0F);
            }else if(millisUntilFinished < 60000 && onem_flag == 1){
                fivem_flag = 0;
                onem_flag = 0;
                sp.play(soundId2, 0.7F, 0.7F, 0, 0, 1.0F);
            }else if(millisUntilFinished < 300000 && fivem_flag == 1){
                fivem_flag = 0;
                sp.play(soundId1, 0.7F, 0.7F, 0, 0, 1.0F);
            }
        }

        public void soundrelease(){
            if(sp != null){
                sp.release();
            }
            if(finish_alarm != null){
                finish_alarm.release();
            }
        }
    }

    class FinishCountDown extends CountDownTimer {
        TextView finish_time;

        FinishCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            finish_time = findViewById(R.id.finish_time);
        }

        @Override
        public void onFinish() {
            // 完了
            finish_time.setText(fdf.format(54000000));
        }

        // インターバルで呼ばれる
        @Override
        public void onTick(long millisUntilFinished) {
            // 残り時間を分、秒、ミリ秒に分割
            //long mm = millisUntilFinished / 1000 / 60;
            //long ss = millisUntilFinished / 1000 % 60;
            //long ms = millisUntilFinished - ss * 1000 - mm * 1000 * 60;
            //timerText.setText(String.format("%1$02d:%2$02d.%3$03d", mm, ss, ms));

            finish_time.setText(fdf.format(millisUntilFinished+54000000));

        }
    }

    protected void start_log(){
        for(int l=0;l<task_amount;l++){
            alltime += time[l];
            allctime += cut_time[l];
        }
        logFileOutput("タスク数:"+task_amount+" タスク合計時間:"+alltime/60+"分"+alltime%60+"秒 タスク省略可能時間:"+allctime/60+"分"+allctime%60+"秒");
    }

    private void sampleFileInput(){

        InputStream in;
        String lineBuffer;

        try {
            in = openFileInput("log.txt"); //LOCAL_FILE = "log.txt";

            BufferedReader reader= new BufferedReader(new InputStreamReader(in,"UTF-8"));
            while( (lineBuffer = reader.readLine()) != null ){
                Log.d("FileAccess",lineBuffer);
            }
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
    }

    private void sampleFileOutput(){

        OutputStream out;
        try {
            out = openFileOutput("log.txt",MODE_PRIVATE|MODE_APPEND);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));

            //追記する
            writer.append("write test");
            writer.append(System.getProperty("line.separator"));
            writer.close();
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
    }

    private void logFileOutput(String str){
        OutputStream out;
        Date time = new Date(System.currentTimeMillis());
        try {
            out = openFileOutput("log.txt",MODE_PRIVATE|MODE_APPEND);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));

            //追記する
            writer.append(df.format(time)+" countdownログ: ");
            writer.append(str);
            writer.append(System.getProperty("line.separator"));
            Log.d("countdownログ","write: "+str);
            writer.close();
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if(countDown != null){
            countDown.soundrelease();
            countDown.cancel();
        }
        if(finishCountDown != null)
            finishCountDown.cancel();
    }
}
