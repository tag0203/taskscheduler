//package com.example.tag.taskscheduler;
//
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.CountDownTimer;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Locale;
//
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class CountFragment extends Fragment {
//    private TextView task;
//    private TextView timerText;
//    private String[] mDataset = new String[20]; //タスク名
//    private int[] time = new int[20]; //タスクの理想時間
//    private int[] cut_time = new int[20]; //タスクを省略してもよい時間
//    private int[] ct = new int[20];
//    private int[] cut_task = new int[20];
//    private int task_amount; //タスクの数
//    private int i = 0; //現在実行されているタスク
//    private int k = 0; //現在省略されているタスク数
//    private int m_ct = 9; //カットできるタスク数
//    private int now_time; //現在の時刻
//    private int f_time; //タスクを終了する時間、出発時間
//    private Calendar c1;
//    CountDown countdown;
//    SubActivity parent;
//
//    private int alltime;
//    private int allctime;
//
//    private SimpleDateFormat dataFormat =
//            new SimpleDateFormat("mm:ss", Locale.US);
//
//    private long interval = 500;
//
//    public CountFragment() {
//        // Required empty public constructor
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_count, container, false);
//
//        mDataset = getArguments().getStringArray("task");
//        time = getArguments().getIntArray("time");
//        cut_time = getArguments().getIntArray("cut time");
//        task_amount = getArguments().getInt("amount",0);
//        f_time = getArguments().getInt("finish time",0);
//        ct = getArguments().getIntArray("cut task");
//
//        Button nextButton = (Button)view.findViewById(R.id.fnext_button);
//        task = (TextView)view.findViewById(R.id.ftask_name);
//
//        timerText = (TextView)view.findViewById(R.id.ftimer);
//        timerText.setText(dataFormat.format(0));
//
////        scheduling();
////        task.setText(mDataset[i]);
////        countdown.start();
////
////        nextButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                countdown.cancel();
////                i++;
////                if(i >= task_amount){
////                    parent.finish();
////                }
////                if(k > 0) k--;
////                scheduling();
////                task.setText(mDataset[i]);
////                countdown.start();
////            }
////        });
//
//        return view;
//    }
//
//    //短縮アルゴリズム
//    protected void scheduling(){
//        //現在時刻を取得(秒)
//        c1 = Calendar.getInstance();
//        now_time = (c1.get(Calendar.HOUR_OF_DAY))*3600+(c1.get(Calendar.MINUTE))*60+(c1.get(Calendar.SECOND));
//
//        //出発時刻との差を計算
//        if(now_time < f_time){
//            now_time = f_time - now_time;
//        }else{parent.finish();}
//
//        //求まった差とタスクの合計時間を比較
//        allctime = 0;
//        alltime = 0;
//        for(int l=i;l<task_amount;l++){
//            alltime += time[l];
//            allctime += cut_time[l];
//
//        }
//        if(alltime < now_time){
//            while(ct[i] != 0 && ct[i] <= k){
//                i++;
//            }
//            countdown = new CountDown(time[i] * 1000, interval);
//        }else{
//            for(k=0;k<m_ct;k++){
//                if(now_time > alltime - allctime){
//                    break;
//                }
//                if(cut_task[k] >= i){
//                    alltime -= time[cut_task[k]];
//                    allctime -= cut_time[cut_task[k]];
//                }
//            }
//            if(now_time < alltime - allctime){
//                parent.finish();
//            }
//            while(ct[i] != 0 && ct[i] <= k){
//                i++;
//            }
//            //タスク時間の計算
//            now_time = alltime - now_time;
//            countdown = new CountDown((time[i] - (now_time*cut_time[i]/allctime)) * 1000, interval);
//        }
//
//    }
//
//    class CountDown extends CountDownTimer {
//
//        CountDown(long millisInFuture, long countDownInterval) {
//            super(millisInFuture, countDownInterval);
//        }
//
//        @Override
//        public void onFinish() {
//            // 完了
//            timerText.setText(dataFormat.format(0));
//        }
//
//        // インターバルで呼ばれる
//        @Override
//        public void onTick(long millisUntilFinished) {
//            // 残り時間を分、秒、ミリ秒に分割
//            //long mm = millisUntilFinished / 1000 / 60;
//            //long ss = millisUntilFinished / 1000 % 60;
//            //long ms = millisUntilFinished - ss * 1000 - mm * 1000 * 60;
//            //timerText.setText(String.format("%1$02d:%2$02d.%3$03d", mm, ss, ms));
//
//            timerText.setText(dataFormat.format(millisUntilFinished));
//
//        }
//    }
//
//    @Override
//    public void onAttach(Context context){
//        super.onAttach(context);
//
//        if(context instanceof Activity){
//            parent = (SubActivity) getActivity();
//        }
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
//        parent = (SubActivity) activity;
//    }
//
//}
