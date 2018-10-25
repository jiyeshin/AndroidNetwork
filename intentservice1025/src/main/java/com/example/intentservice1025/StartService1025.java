package com.example.intentservice1025;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class StartService1025 extends Service {
    public StartService1025() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("스타트 서비스", "시작");
    }

    //위에 있는 onCreate 다음에 호출되는 메소드
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        for(int i =0; i<30; i=i+1){
            try{
                Thread.sleep(1000);
                Log.e("StartService",i+"");

            }catch (Exception e){
                Log.e("스타트서비스예외", e.getMessage());
            }
        }
        //스타트 서비스 종료
        stopSelf();

        //종료된 후 바로 다시 시작할 수 있도록 생성
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("스타트 서비스", "종료");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("StartService", "바인드 호출");
       return null;
    }
}
