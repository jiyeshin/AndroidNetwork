package com.example.a503_12.androidnetwork;

import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BroadcastA1025 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_a1025);

        registerReceiver(new BroadcastReceiver1025(), new IntentFilter("com.example.sendbroadcast"));
    }
}
