//package com.example.tag.taskscheduler;
//
//import android.content.Intent;
//import android.app.Activity;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.app.Fragment;
//import android.app.FragmentTransaction;
//
//public class SubActivity extends AppCompatActivity {
//
//    private CountFragment countfragment;
//    private Bundle bundle;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sub);
//        Intent intent = getIntent();
//        bundle = new Bundle();
//
//        bundle.putStringArray("task",intent.getStringArrayExtra("task"));
//        bundle.putIntArray("time",intent.getIntArrayExtra("time"));
//        bundle.putIntArray("cut time", intent.getIntArrayExtra("cut time"));
//        bundle.putInt("amount", intent.getIntExtra("amount",0));
//        bundle.putInt("finish time", intent.getIntExtra("finish time",0));
//        bundle.putIntArray("cut task", intent.getIntArrayExtra("cut task"));
//
//        // Fragmentを作成します
//        countfragment = new CountFragment();
//        countfragment.setArguments(bundle);
//        setFragment(countfragment);
//    }
//
//    private void setFragment(android.support.v4.app.Fragment fragment) {
//
//        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.container, fragment);
//        fragmentTransaction.commit();
//    }
//}
