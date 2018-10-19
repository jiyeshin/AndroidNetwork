package com.example.a503_12.androidnetwork;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;

public class CSVUse1019 extends AppCompatActivity {

    //ListView는 3가지 변수가 항상 있어야 함.
    ArrayList<String> list;
    ArrayAdapter<String>adapter;
    ListView listView;

    //ListView를 재출력하는 핸들러
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //리트
            adapter.notifyDataSetChanged();
        }
    };

    //웹 서버로부터 데이터를 읽어서 List의 내용을 수정하는 스레드 클래스
    class ThreadEx extends Thread{
        @Override
        public void run() {
            try{
                String addr = "http://192.168.0.13:8080/androidserver/jbj95.csv";
                URL url = new URL(addr);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setConnectTimeout(30000);
                con.setUseCaches(false);

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                list.clear();
                while (true){
                    String line = br.readLine();
                    if(line == null)break;

                    String [] ar = line.split(",");
                    for(String temp : ar){
                        list.add(temp);
                    }
                }
                br.close();
                con.disconnect();
                handler.sendEmptyMessage(0);

            }catch (Exception e){
                Log.e("다운로드 실패", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csvuse1019);

        //리스트 출력
        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(CSVUse1019.this, android.R.layout.simple_list_item_1, list);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        ThreadEx th = new ThreadEx();
        th.start();
    }
}
