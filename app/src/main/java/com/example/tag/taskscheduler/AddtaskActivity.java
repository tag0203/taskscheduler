package com.example.tag.taskscheduler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class AddtaskActivity extends Activity {

    private EditText input_taskname;
    private EditText time;
    private EditText cutting_time;
    private Spinner cut_num;
    private Spinner num;
    private Button save;

    private Intent send = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtask);
        final Intent intent = getIntent();

        input_taskname = findViewById(R.id.input_taskname);
        time = findViewById(R.id.time);
        cutting_time = findViewById(R.id.cutting_time);
        cut_num = findViewById(R.id.spinner);
        num = findViewById(R.id.spinner2);

        save = findViewById(R.id.next);

        int amount = intent.getIntExtra("task num", 0);
        ArrayList<String> task_name = intent.getStringArrayListExtra("task name");
        ArrayList<String> num_list = new ArrayList<>();
        for(int i = 0;i<=amount;i++){
            if(i<task_name.size()){
                num_list.add(String.valueOf(i+1) + " " + task_name.get(i));
            }else {
                num_list.add(String.valueOf(i+1));
            }
        }
        spinner_adapter(num, num_list);

        ArrayList<Integer> cuttime_list = intent.getIntegerArrayListExtra("cut num");
        final ArrayList<String> cut_list = new ArrayList<>();
        cut_list.add("省略しない");
        for(int i = 0;i<=cuttime_list.size();i++){
            if(i < cuttime_list.size()){
                cut_list.add(String.valueOf(i+1) + " " + task_name.get(cuttime_list.get(i)));
            }else{
                cut_list.add(String.valueOf(i+1));
            }
        }
        spinner_adapter(cut_num,cut_list);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_taskname.length() == 0){
                    Toast toast = Toast.makeText(AddtaskActivity.this, "タスク名が入力されていません", Toast.LENGTH_LONG);
                    toast.show();
                }else if(time.length() == 0){
                    Toast toast = Toast.makeText(AddtaskActivity.this, "タスクにかけたい時間が入力されていません", Toast.LENGTH_LONG);
                    toast.show();
                }else if(cutting_time.length() == 0){
                    Toast toast = Toast.makeText(AddtaskActivity.this, "最低でもタスクにかけたい時間が入力されていません", Toast.LENGTH_LONG);
                    toast.show();
                }else if(Integer.parseInt(time.getText().toString()) <= Integer.parseInt(cutting_time.getText().toString())){
                    Toast toast = Toast.makeText(AddtaskActivity.this, "タスクにかけたい時間が省略できる時間より短いです", Toast.LENGTH_LONG);
                    toast.show();
                }else{
                    send.putExtra("task name", input_taskname.getText().toString());
                    send.putExtra("time", Integer.parseInt(time.getText().toString()));
                    send.putExtra("cut time", Integer.parseInt(cutting_time.getText().toString()));
                    send.putExtra("num", num.getSelectedItemPosition());
                    send.putExtra("cut num", cut_num.getSelectedItemPosition());
                    setResult(RESULT_OK, send);
                    finish();
                }
            }
        });
    }

    private void spinner_adapter(Spinner spinner, ArrayList<String> list){
        // Adapterの作成
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        // ドロップダウンのレイアウトを指定
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }
}
