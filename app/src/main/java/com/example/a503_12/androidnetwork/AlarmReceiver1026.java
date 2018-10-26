package com.example.a503_12.androidnetwork;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

public class AlarmReceiver1026 extends BroadcastReceiver {

    //리시버가 호출되면 수행되는 메소드
    @Override
    public void onReceive(Context context, Intent intent) {
        MediaPlayer player = MediaPlayer.create(context.getApplicationContext(), R.raw.alarm);
        player.start();
    }
}
