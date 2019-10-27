package com.example.tag.taskscheduler;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
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
import java.util.ArrayList;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class TaskFragment extends Fragment{

    private MainActivity parent;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button add_task;
    private Button save;
    private Button delete;

    private ArrayList<String> task_name = new ArrayList<>();
    private ArrayList<Integer> time = new ArrayList<>();
    private ArrayList<Integer> cut_time = new ArrayList<>();
    private ArrayList<Integer> cut_task = new ArrayList<>(); //タスクを省略するかしないか

    private ArrayList<String> tDataset = new ArrayList<>();
    private ArrayList<String> ctDataset = new ArrayList<>();
    private ArrayList<Integer> cuttime_list = new ArrayList<>();
    private ArrayList<String> ctnDataset = new ArrayList<>();

    private TextView task_amount;
    private TextView sum_time;
    private TextView sum_cuttime;

    private int taskchange_frag = 0;

    public TaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof Activity){
            parent = (MainActivity) getActivity();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        parent = (MainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        taskFileInput();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.task_list);
        add_task = (Button) view.findViewById(R.id.add_task);
        save = (Button) view.findViewById(R.id.save);
        delete = (Button) view.findViewById(R.id.delete);
        task_amount = (TextView)view.findViewById(R.id.task_amount);
        sum_time = (TextView)view.findViewById(R.id.sum_time);
        sum_cuttime = (TextView) view.findViewById(R.id.sum_cuttime);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager

//        Context context = view.getContext();
        mLayoutManager = new LinearLayoutManager(parent);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(parent, DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);

        add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(task_name.size()<20){
                    add_activity();
                }else{
                    Toast.makeText(parent, "タスクは20個までしか追加できません", Toast.LENGTH_LONG).show();
                }

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //どのタスクを削除するか選ばせる
                new AlertDialog.Builder(getActivity())
                        .setTitle("Selector")
                        .setItems(task_name.toArray(new String[task_name.size()]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                edit_task(which);
                                list_set();
                            }
                        })
                        .show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //どのタスクを削除するか選ばせる
                new AlertDialog.Builder(getActivity())
                        .setTitle("Selector")
                        .setItems(task_name.toArray(new String[task_name.size()]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //本当に削除するか確かめる
                                final int stock = which;
                                // item_which pressed
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("確認")
                                        .setMessage(task_name.get(which)+"を削除しますか")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // OK button pressed
                                                delete_task(stock);
                                                taskFileOutput();
                                                list_set();
                                            }
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            }
                        })
                        .show();
            }
        });

        list_set();

        return view;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if(taskchange_frag == 1) {
            taskFileOutput();
        }
    }

    private void add_activity(){
        Intent intent = new Intent(parent.getApplication(),AddtaskActivity.class);
        intent.putExtra("task num", getAmount());
        intent.putExtra("cut num", cuttime_list);
        intent.putExtra("task name", task_name);
        startActivityForResult(intent, 1000);
    }

    private void edit_task(int i){
        Intent intent = new Intent(parent.getApplication(),EdittaskActivity.class);
        intent.putExtra("task num", getAmount());
        intent.putExtra("cut num", cuttime_list);
        intent.putExtra("task name", task_name);
        intent.putExtra("name", task_name.get(i));
        intent.putExtra("task time", time.get(i));
        intent.putExtra("cut time", cut_time.get(i));
        intent.putExtra("cut task", cut_task.get(i));
        intent.putExtra("i", i);
        startActivityForResult(intent, 2000);
    }

    private void list_set(){
        int sum_t = 0;
        int sum_ct = 0;
        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(task_name, tDataset, ctDataset, ctnDataset);
        mRecyclerView.setAdapter(mAdapter);

        task_amount.setText("タスク数:"+String.valueOf(task_name.size())+"/20個");
        for(int i=0;i<time.size();i++){
            sum_t+=time.get(i);
            sum_ct+=cut_time.get(i);
        }
        sum_time.setText("合計時間:"+String.valueOf(sum_t/60)+"分"+String.valueOf(sum_t%60)+"秒");
        sum_cuttime.setText("合計最短時間:"+String.valueOf(sum_ct/60)+"分"+String.valueOf(sum_ct%60)+"秒");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode == parent.RESULT_OK && requestCode == 1000 && null != data) {
            String task2 = data.getStringExtra("task name");
            int time2 = data.getIntExtra("time", 0);
            int cut_time2 = data.getIntExtra("cut time", 0);
            int num2 = data.getIntExtra("num", 0);
            int cut_num2 = data.getIntExtra("cut num", 0);
            AddList(task2, time2, cut_time2, num2, cut_num2);
        }
        if(resultCode == parent.RESULT_OK && requestCode == 2000 && null != data) {
            String task2 = data.getStringExtra("task name");
            int time2 = data.getIntExtra("time", 0);
            int cut_time2 = data.getIntExtra("cut time", 0);
            int num2 = data.getIntExtra("num", 0);
            int cut_num2 = data.getIntExtra("cut num", 0);
            int initial_value = data.getIntExtra("initial value", 0);
            delete_task(initial_value);
            AddList(task2, time2, cut_time2, num2, cut_num2);

        }
    }

    private void AddList(String name, int get_time, int get_ct, int when, int cut){
        cuttime_list.clear();
        task_name.add(when, name);
        time.add(when, get_time);
        cut_time.add(when, get_ct);

        if(cut == 0){
            cut_task.add(when, cut);
            for(int m = 0; m < cut_task.size(); m++){
                for(int n = 0; n < cut_task.size(); n++){
                    if(cut_task.get(n) == m+1){
                        cuttime_list.add(n);
                        break;
                    }
                }
            }
        }else{
            cut_task.add(when, 0);
            for(int m = 0; m < task_name.size(); m++){
                for(int n = 0; n < task_name.size(); n++){
                    if(cut_task.get(n) == m+1){
                        cuttime_list.add(n);
                        break;
                    }
                }
            }

            cuttime_list.add(cut-1, when);

            for(int m = 0;m < cuttime_list.size(); m++){
                cut_task.set(cuttime_list.get(m), m+1);
            }
        }
        tDataset.clear();
        ctDataset.clear();
        ctnDataset.clear();
        for(int m=0;m<time.size();m++){
            if(time.get(m) < 60){
                tDataset.add(String.valueOf(time.get(m)) + "秒");
            }else{
                tDataset.add(String.valueOf(time.get(m)/60) + "分"+String.valueOf(time.get(m)%60) + "秒");
            }
            if(cut_time.get(m) < 60){
                ctDataset.add(String.valueOf(cut_time.get(m)) + "秒");
            }else{
                ctDataset.add(String.valueOf(cut_time.get(m)/60) + "分"+String.valueOf(cut_time.get(m)%60) + "秒");
            }
            if(cut_task.get(m) == 0){
                ctnDataset.add("省略しない");
            }else{
                ctnDataset.add(String.valueOf(cut_task.get(m)));
            }
        }

        taskchange_frag = 1;
        taskFileOutput();
        list_set();
        Toast.makeText(parent,"タスクが正常に追加されました", Toast.LENGTH_LONG).show();
    }

    private void delete_task(int i){
        task_name.remove(i);
        time.remove(i);
        cut_task.remove(i);
        cut_time.remove(i);
        tDataset.remove(i);
        ctDataset.remove(i);
        ctnDataset.remove(i);
        //省略順位を更新
        cuttime_list.clear();
        for(int m = 0; m < cut_task.size(); m++){
            for(int n = 0; n < cut_task.size(); n++){
                if(cut_task.get(n) == m+1){
                    cuttime_list.add(n);
                    break;
                }
            }
        }
        for(int m = 0;m < cuttime_list.size(); m++){
            cut_task.set(cuttime_list.get(m), m+1);
            ctnDataset.set(cuttime_list.get(m),String.valueOf(m+1));

        }
    }

    public int getAmount(){ return task_name.size(); }
    public String[] getMyDataset(){return task_name.toArray(new String[20]);}
    public int[] getTime(){return intListtoArray(time);}
    public int[] getCutTime(){
        int a[] = new int[20];

        for(int i = 0;i<cut_time.size();i++){
            a[i] = time.get(i) - cut_time.get(i);
        }
        return a;
    }
    public int[] getCutTask(){return intListtoArray(cut_task);}

    public int getAmountCT(){return cut_task.size();}

    private int[] intListtoArray(ArrayList<Integer> list){
        int i[] = new int[20];

        for(int n=0;n<list.size();n++){
            i[n] = list.get(n);
        }
        return i;
    }

    private void taskFileInput(){
        InputStream in;
        String lineBuffer;
        int i = 0;

        try {
            in = parent.openFileInput("task.txt"); //LOCAL_FILE = "log.txt";

            BufferedReader reader= new BufferedReader(new InputStreamReader(in,"UTF-8"));
            task_name.clear();
            time.clear();
            cut_task.clear();
            cut_time.clear();
            tDataset.clear();
            ctDataset.clear();
            ctnDataset.clear();
            while( (lineBuffer = reader.readLine()) != null ){
                String str[] = lineBuffer.split(",");
                task_name.add(str[0]);
                time.add(Integer.parseInt(str[1]));
                cut_time.add(Integer.parseInt(str[2]));
                cut_task.add(Integer.parseInt(str[3]));
                if(time.get(i) < 60){
                    tDataset.add(String.valueOf(time.get(i)) + "秒");
                }else{
                    tDataset.add(String.valueOf(time.get(i)/60) + "分"+String.valueOf(time.get(i)%60) + "秒");
                }
                if(cut_time.get(i) < 60){
                    ctDataset.add(String.valueOf(cut_time.get(i)) + "秒");
                }else{
                    ctDataset.add(String.valueOf(cut_time.get(i)/60) + "分"+String.valueOf(cut_time.get(i)%60) + "秒");
                }
                if(cut_task.get(i) == 0){
                    ctnDataset.add("省略しない");
                }else{
                    ctnDataset.add(String.valueOf(cut_task.get(i)));
                }

                i++;
                Log.d("FileAccess",lineBuffer);
            }
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }

        cuttime_list.clear();
        for(int m = 0; m < cut_task.size(); m++){
            for(int n = 0; n < cut_task.size(); n++){
                if(cut_task.get(n) == m+1){
                    cuttime_list.add(n);
                    break;
                }
            }
        }
    }


    private void taskFileOutput(){
        OutputStream out;
        parent.deleteFile("task.txt");
        try {
            out = parent.openFileOutput("task.txt",MODE_PRIVATE|MODE_APPEND);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));

            //追記する
            for(int i = 0;i<task_name.size();i++){
                writer.append(task_name.get(i)+","+time.get(i)+","+cut_time.get(i)+","+cut_task.get(i));
                writer.append(System.getProperty("line.separator"));
            }
            writer.close();
            Toast toast = Toast.makeText(parent, "タスクが正常に保存されました！", Toast.LENGTH_LONG);
            toast.show();
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
        taskchange_frag = 0;
    }

    private void logFileOutput(String str){
        OutputStream out;
        try {
            out = parent.openFileOutput("log.txt",MODE_PRIVATE|MODE_APPEND);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));

            //追記する
            writer.append(str);
            writer.append(System.getProperty("line.separator"));
            Log.d("FileAccess","write"+str);
            writer.close();
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
    }

    //テスト用のタスクデータを入力する関数
//    private void test_code(){
//        int task_amount = 10
//        Log.d("デバッグ", "test_code");
//        for(int i=0; i<task_amount; i++) {
//            task_name.add("Data_0"+String.valueOf(i));
//            time.add(60+(i*60));
//            cut_time.add(time.get(i)/2);
//            if(time.get(i) < 60){
//                tDataset.add(String.valueOf(time.get(i)) + "秒");
//            }else{
//                tDataset.add(String.valueOf(time.get(i)/60) + "分"+String.valueOf(time.get(i)%60) + "秒");
//            }
//        }
//
//        cut_task.add(7);
//        cut_task.add(2);
//        cut_task.add(3);
//        cut_task.add(5);
//        cut_task.add(4);
//        cut_task.add(0);
//        cut_task.add(1);
//        cut_task.add(8);
//        cut_task.add(6);
//        cut_task.add(9);
//    }
}

