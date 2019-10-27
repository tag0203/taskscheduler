package com.example.tag.taskscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmActivity extends AppCompatActivity {
    private TextView alarm;
    private TextView start;
    private Button start_button;
    private MyAlarmManager mam;
    private String[] mDataset = new String[20]; //タスク名
    private int[] time = new int[20]; //タスクの理想時間
    private int[] cut_time = new int[20]; //タスクを省略してもよい時間
    private int task_amount; //タスクの数
    private int f_time;
    private int[] ct = new int[20];

    private long now_date;
    private Calendar c1;

    private SimpleDateFormat dataFormat =
            new SimpleDateFormat("H:mm", Locale.US);

    private final SimpleDateFormat df =
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        final Intent get = getIntent();
        c1 = Calendar.getInstance();

        mam = new MyAlarmManager(this);
        mam.addAlarm(get.getIntExtra("alarm time", 0));

        mDataset = get.getStringArrayExtra("task");
        time = get.getIntArrayExtra("time");
        cut_time = get.getIntArrayExtra("cut time");
        task_amount = get.getIntExtra("amount",0);
        f_time = get.getIntExtra("finish time",0);
        ct = get.getIntArrayExtra("cut task");

        alarm = findViewById(R.id.alarm_time);
        alarm.setText(dataFormat.format(54000000+get.getIntExtra("alarm time",0)*1000));
        start = findViewById(R.id.start_time);
        start.setText(dataFormat.format(get.getIntExtra("finish time",0)*1000+54000000));
        start_button = findViewById(R.id.button1);

        log();

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent count_start = new Intent(getApplication(),Countdown.class);
                count_start.putExtra("task",get.getStringArrayExtra("task"));
                count_start.putExtra("time",get.getIntArrayExtra("time"));
                count_start.putExtra("cut time",get.getIntArrayExtra("cut time"));
                count_start.putExtra("amount", get.getIntExtra("amount",0));
                count_start.putExtra("finish time", get.getIntExtra("finish time",0));
                count_start.putExtra("cut task", get.getIntArrayExtra("cut task"));
                finish();
                startActivity(count_start);
            }
        });

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mam.cancel();
    }

    private void log(){
        OutputStream out;
        Date now_time = new Date(System.currentTimeMillis());
        try {
            out = openFileOutput("log.txt",MODE_PRIVATE|MODE_APPEND);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));

            //追記する
            writer.append(System.getProperty("line.separator"));
            writer.append(df.format(now_time));
            writer.append(System.getProperty("line.separator"));
            writer.append("出発時間"+f_time/3600 + "時" + (f_time%3600)/60 + "分");
            writer.append(System.getProperty("line.separator"));
            writer.append("タスク名,通常時間,短縮できる時間,省略するか");
            writer.append(System.getProperty("line.separator"));
            for(int i=0;i<task_amount;i++){
                writer.append(mDataset[i]+","+time[i]+","+cut_time[i]+",");
                if(ct[i] == 0){
                    writer.append("省略しない");
                }else{
                    writer.append(String.valueOf(ct[i]));
                }
                writer.append(System.getProperty("line.separator"));
            }
            Log.d("FileAccess","write");
            writer.close();
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
    }
}
