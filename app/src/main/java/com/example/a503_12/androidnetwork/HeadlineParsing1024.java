package com.example.a503_12.androidnetwork;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class HeadlineParsing1024 extends AppCompatActivity {

    //리스트뷰에 출력될 데이터
    ArrayList<String> list;
    //출력을 위한 리스트 뷰
    ListView listView;
    //데이터와 ListView를 연결시켜 줄 Adapter
    ArrayAdapter<String> adapter;

    //진행상황을 출력할 대화상자
    ProgressDialog progressDialog;

    //스와이프
    SwipeRefreshLayout swipeRefreshLayout;

    //화면을 갱신할 핸들러
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    //웹에서 다운로드 받을 스레드
    class ThreadEx extends Thread{
        @Override
        public void run() {
            //다운로드 받은 문자열을 저장할 객체
            StringBuilder sb = new StringBuilder();

            try {
                //문자열을 다운로드 받는 코드 영역
                URL url = new URL("http://rss.hankooki.com/daily/dh_life.xml");
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setUseCaches(false);
                con.setConnectTimeout(20000);

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                while (true){
                    String line = br.readLine();
                    if(line==null)break;
                    sb.append(line+"\n");
                }
                br.close();
                con.disconnect();
                //Log.e("다운로드 받은 문자열", sb.toString());

            }catch (Exception e){
                Log.e("다운로드 실패", e.getMessage());
            }

            //xml파싱
            try{
                //파싱을 수행할 객체 생성
                DocumentBuilderFactory docfactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = docfactory.newDocumentBuilder();

                //다운로드 받은 문자열을 InputStream으로 변환
                //원하는 xml이 utf-8로 되어있으면 바로 Document doc = builder.parse("여기에 경로"); 로 써도 된다.
                InputStream is = new ByteArrayInputStream(sb.toString().getBytes("euc-kr"));

                //메모리에 펼치기 - DOM
                Document doc = builder.parse("http://rss.hankooki.com/daily/dh_life.xml");

                //루트 가져오기
                Element root = doc.getDocumentElement();

                //원하는 태그의 데이터를 가져오기
                NodeList nodeList = root.getElementsByTagName("title");
                //Log.e("nodeList: ", nodeList.toString());

                //반복문으로 태그를 순회
                for(int i = 2; i<nodeList.getLength(); i=i+1){
                    //태그를 하나씩 가져오기
                    Node node = nodeList.item(i);

                    //태그 안의 문자열을 가져와서 리스트에 추가
                    Node contents = node.getFirstChild();
                    String title = contents.getNodeValue();
                    list.add(title);
                }

                //핸들러 호출
                handler.sendEmptyMessage(0);



            }catch (Exception e){
                Log.e("xml 파싱 실패", e.getMessage());
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headline_parsing1024);

        //ListView를 출력하기 위한 데이터 생성
        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView = (ListView)findViewById(R.id.listView);

        //데이터와 ListView를 연결
        listView.setAdapter(adapter);

        progressDialog = ProgressDialog.show(this, "로딩", "로딩중...");

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ThreadEx().start();
            }
        });

        //스레드 생성하고 시작
        ThreadEx th = new ThreadEx();
        th.start();
    }
}
