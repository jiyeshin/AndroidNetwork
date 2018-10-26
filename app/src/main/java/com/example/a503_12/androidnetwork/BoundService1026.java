package com.example.a503_12.androidnetwork;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class BoundService1026 extends Service {
    //데이터를 전송할 수 있는 Binder 클래스 생성
    class MyLocalBinder extends Binder{
        BoundService1026 getService(){
            return BoundService1026.this;
        }
    }

    //Binder 객체를 생성
    IBinder myBinder = new MyLocalBinder();

    public BoundService1026() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    //서비스를 등록한 곳에서 호출할 메소드
    public  String remoteMethod(){
        return "웨이백홈 러브다이브 오늘밤 생각나 ";
    }
}
