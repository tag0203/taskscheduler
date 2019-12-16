package com.example.tag.taskscheduler;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    private HomeFragment homeFragment;
    private AlarmFragment alarmFragment;
    public TaskFragment taskFragment;

    private static final int REQ_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        homeFragment = new HomeFragment();
        alarmFragment = new AlarmFragment();
        taskFragment = new TaskFragment();

        setFragment(taskFragment);
        setFragment(homeFragment);

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.nav_home:
                        mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(homeFragment);
                        return true;

                    case R.id.nav_alarm:
                        mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(alarmFragment);
                        return true;


                    case R.id.nav_task:
                        mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(taskFragment);
                        return true;

                    default:
                        return false;

                }
            }
        });
    }


    private void setFragment(android.support.v4.app.Fragment fragment) {

        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    void move() {
        //countdownにタスク関連のデータを送る
        Intent intent = new Intent(getApplication(), AlarmActivity.class);
        intent.putExtra("time", taskFragment.getTime());
        intent.putExtra("amount", taskFragment.getAmount());
        intent.putExtra("task", taskFragment.getMyDataset());
        intent.putExtra("cut time", taskFragment.getCutTime());
        intent.putExtra("finish time", homeFragment.getFTime());
        intent.putExtra("cut task", taskFragment.getCutTask());
        intent.putExtra("alarm time", homeFragment.getAtime());
        startActivity(intent);
    }

}
