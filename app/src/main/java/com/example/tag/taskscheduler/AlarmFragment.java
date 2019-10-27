package com.example.tag.taskscheduler;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.ref.SoftReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlarmFragment extends Fragment {

    private SeekBar volume;
    private TextView vol_num;
    private Button start_stop;

    private MediaPlayer mp;
    private int volume_num;

    public AlarmFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_alarm, container, false);

//        volume = view.findViewById(R.id.volumeBar);
//        vol_num = view.findViewById(R.id.vol_num);
        start_stop = view.findViewById(R.id.start_stop);

//        vol_num.setText("アラーム音量:"+volume.getProgress()+"%");
//        volume_num = volume.getProgress();
        start_stop.setText("サンプル再生");

//        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                //つまみをドラッグする時
//                vol_num.setText("アラーム音量:"+String.valueOf(progress) +"%");
//                volume_num = progress;
//                if(mp!=null)
//                    mp.setVolume(volume_num,volume_num);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                //つまみに触れた時
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                //つまみを離した時
//            }
//        });
         start_stop.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (mp == null) {
                     mp = MediaPlayer.create(getActivity(), R.raw.alarm);
//                     mp.setVolume(volume_num, volume_num);
                     mp.setLooping(true);
                     mp.start();
                     start_stop.setText("サンプル停止");
                 }
                 else if(mp != null){
                     mp.stop();
                     mp.reset();
                     mp.release();
                     mp = null;
                     start_stop.setText("サンプル再生");
                 }
             }
         });

        return view;
    }

    @Override
    public void onPause(){
        super.onPause();

        if(mp!=null){
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }
    }

//    public int getVol(){return volume.getProgress();}
}
