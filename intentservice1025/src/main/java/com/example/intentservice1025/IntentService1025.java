package com.example.intentservice1025;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class IntentService1025 extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_service1025);

        //스레드
        Thread th = new Thread(){
            @Override
            public void run() {
                for (int i = 0; i < 30; i = i + 1) {
                    try {
                        Thread.sleep(1000);
                        Log.e("Thread", i + "");

                    } catch (Exception e) {
                        Log.e("Thread예외", e.getMessage());
                    }
                }
            }
        };
        th.start();


        //인텐트 서비스
        Intent intent = new Intent(this, MyIntentService1025.class);
        //서비스 실행
        startService(intent);



        //스타트 서비스
        Intent intent1 = new Intent(this, StartService1025.class);
        startService(intent1);
    }
}
