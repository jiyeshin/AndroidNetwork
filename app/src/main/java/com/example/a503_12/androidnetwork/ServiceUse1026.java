package com.example.a503_12.androidnetwork;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ServiceUse1026 extends AppCompatActivity {

    TextView textView;
    BoundService1026 boundService1026;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_use1026);

        //서비스 객체 생성
        boundService1026 = new BoundService1026();

        //인텐트 생성
        Intent intent = new Intent(this, BoundService1026.class);

        //서비스와 바인드된 경우와, 바인드가 해제된 경우에 호출되는 메소드를 소유한 ServiceConnection 객체 생성
        ServiceConnection myConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                BoundService1026.MyLocalBinder binder = (BoundService1026.MyLocalBinder)service;
                boundService1026 = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        //바운드 서비스로 등록
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

        textView = (TextView)findViewById(R.id.txtView);
        Button btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = boundService1026.remoteMethod();
                textView.setText(result);
            }
        });
    }
}
