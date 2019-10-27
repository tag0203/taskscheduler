package com.example.tag.taskscheduler;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private NumberPicker numPicker_hour1;
    private NumberPicker numPicker_minutes1;
    private NumberPicker numPicker_hour2;
    private NumberPicker numPicker_minutes2;
    private Button start_button;
    private int f_time;
    private int alarm_time;

    private MainActivity parent;



    public HomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        numPicker_hour1 = (NumberPicker)view.findViewById(R.id.numPicker_hour1);
        numPicker_minutes1 = (NumberPicker)view.findViewById(R.id.numPicker_minutes1);
        numPicker_hour2 = (NumberPicker)view.findViewById(R.id.numPicker_hour2);
        numPicker_minutes2 = (NumberPicker)view.findViewById(R.id.numPicker_minutes2);
        start_button = (Button)view.findViewById(R.id.start_button);

        numPicker_hour1.setMaxValue(23);
        numPicker_hour1.setMinValue(0);
        numPicker_minutes1.setMaxValue(59);
        numPicker_minutes1.setMinValue(0);

        numPicker_hour2.setMaxValue(23);
        numPicker_hour2.setMinValue(0);
        numPicker_minutes2.setMaxValue(59);
        numPicker_minutes2.setMinValue(0);

        taskFileInput();

        start_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                f_time = numPicker_hour1.getValue()*3600 + numPicker_minutes1.getValue()*60;
                alarm_time = numPicker_hour2.getValue()*3600 + numPicker_minutes2.getValue()*60;
                taskFileOutput();
                parent.move();
            }
        });

        return view;
    }

    @Override
    public void onPause(){
        super.onPause();

        taskFileOutput();
    }

    public int getFTime(){return f_time;}
    public int getAtime(){return alarm_time;}

    private void taskFileInput(){
        InputStream in;
        String lineBuffer;

        try {
            in = parent.openFileInput("morning_time.txt"); //LOCAL_FILE = "log.txt";

            BufferedReader reader= new BufferedReader(new InputStreamReader(in,"UTF-8"));
            if( (lineBuffer = reader.readLine()) != null ){
                numPicker_hour1.setValue(Integer.parseInt(lineBuffer)/3600);
                numPicker_minutes1.setValue((Integer.parseInt(lineBuffer)%3600)/60);
                if( (lineBuffer = reader.readLine()) != null ){
                    numPicker_hour2.setValue(Integer.parseInt(lineBuffer)/3600);
                    numPicker_minutes2.setValue((Integer.parseInt(lineBuffer)%3600)/60);
                }
                Log.d("FileAccess",lineBuffer);
            }
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
    }


    private void taskFileOutput(){
        OutputStream out;
        parent.deleteFile("morning_time.txt");
        try {
            out = parent.openFileOutput("morning_time.txt",MODE_PRIVATE|MODE_APPEND);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));

            //追記する
            writer.append(String.valueOf(numPicker_hour1.getValue()*3600 + numPicker_minutes1.getValue()*60));
            writer.append(System.getProperty("line.separator"));
            writer.append(String.valueOf(numPicker_hour2.getValue()*3600 + numPicker_minutes2.getValue()*60));
            writer.append(System.getProperty("line.separator"));
            writer.close();
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
    }

}
