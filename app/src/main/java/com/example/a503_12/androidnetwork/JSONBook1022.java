package com.example.a503_12.androidnetwork;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class JSONBook1022 extends AppCompatActivity {

    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    ListView listView;

    class ThreadEx extends Thread{
        //다운로드 받은 문자열을 저장할 변수
        String json = "";
        @Override
        public void run() {
            try {
                //EditTex에 입력한 값으로 검색하기
                EditText bookname = (EditText)findViewById(R.id.bookname);
                String sam = URLEncoder.encode(bookname.getText().toString());
                //다운로드 받을 주소 생성
                URL url = new URL("https://apis.daum.net/search/book?output=json&q=" + sam);

                //URL연결 객체 생성
                HttpURLConnection con = (HttpURLConnection)url.openConnection();

                //카카오 API의 인증 설정
                 con.setRequestProperty("Authorization", "KakaoAK b16d7284a45e8beff8b1dccb4e8a272f");

                 //옵션 설정
                 con.setUseCaches(false);
                 con.setConnectTimeout(30000);

                 //정해진 api를 사용하기 떄문에 인코딩 안해도 됨.
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                StringBuilder sb = new StringBuilder();

                while(true){
                    String line = br.readLine();
                    if(line == null)break;
                    sb.append(line + "\n");
                }
                json = sb.toString();

                //다운로드 받은 문자열을 확인
                //Log.e("제이손", json);

                br.close();
                con.disconnect();
            }catch (Exception e){
                Log.e("다운로드 실패", e.getMessage());
            }

            //json파싱
            try{
                //문자열을 객체로 생성
                JSONObject book = new JSONObject(json);

                //channel 키의 데이터를 JSONObject 타입으로 가져오기 - 맨 앞의 channel을 벗겨냄.
                //{}는 Object, []는 Array
                JSONObject channel = book.getJSONObject("channel");
                //Log.e("channel", channel.toString());
                JSONArray items = channel.getJSONArray("item");

                //검색할 때마다 list 초기화
                list.clear();
                //배열 데이터를 순회
                for(int i =0; i<items.length();i=i+1){
                    //각 인덱스에 해당하는 항목을 JSONObject로 가져오기
                    JSONObject item = items.getJSONObject(i);

                    //list에 제목과 세일 가격을 가져와서 추가
                   list.add(item.getString("title") + ":" + item.getString("sale_price"));
                }

                //핸들러를 호출해서 리스트 뷰를 다시 출력
                handler.sendEmptyMessage(0);

            }catch (Exception e){
                Log.e("파싱 에러", e.getMessage());
            }
        }
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsonbook1022);

        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, list);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        Button btnJson = (Button)findViewById(R.id.btnJson);
        btnJson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadEx th = new ThreadEx();
                th.start();
            }
        });
    }
}
