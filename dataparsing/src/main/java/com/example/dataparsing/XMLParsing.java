package com.example.dataparsing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class XMLParsing extends AppCompatActivity {

    //기사 제목을 저장할 리스트
    ArrayList<String> titleList;
    ArrayAdapter<String> adapter;
    ListView listView;

    //링크를 저장할 리스트
    ArrayList<String> linkList;

    //스와이프: 업데이트를 위한 레이아웃
    SwipeRefreshLayout swipe_layout;

    //대화상자
    ProgressDialog progressDialog;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            adapter.notifyDataSetChanged();
            swipe_layout.setRefreshing(false);
        }
    };

    //데이터를 다운로드 받을 스레드
    class ThreadEX extends Thread{
        @Override
        public void run() {
            //다운로드 받은 문자열을 저장할 객체 생성
            StringBuilder sb = new StringBuilder();

            //데이터 다운로드 받기
            try{
                //데이터 다운로드 받는 주소
                URL url = new URL("http://file.mk.co.kr/news/rss/rss_30100041.xml");

                //연결
                HttpURLConnection con = (HttpURLConnection)url.openConnection();

                //옵션 설정
                con.setConnectTimeout(20000);
                con.setUseCaches(false);

                //데이터 읽기
                //BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                //문자열 다운로드 받기 위한 스트림 생성
                BufferedReader br = null;

                //해더 가져오기
                String headerType = con.getContentType();
                if(headerType.toUpperCase().indexOf("UTF-8")>=0){
                    //indexOf는 있는지 없는지 확인할 수 있고, 있으면 0또는 0보다 큰 숫자가 나온다.
                    //indexOf의 작동 원리는 인자로 들어간 값이 있는 위치를 알려준다.
                    br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                }else {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream(), "EUC-KR"));
                }


                while (true){
                    String line = br.readLine();
                    if(line==null)break;
                    sb.append(line + "\n");
                }

                br.close();
                con.disconnect();
                //Log.e("다운로드 받은 문자열 ", sb.toString());

            }catch (Exception e){
                //Log.e("다운로드 실패", e.getMessage());
            }
            try{
                //SAX Parser를 이용한 파싱 요청
                SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                SAXParser saxParser = saxParserFactory.newSAXParser();
                XMLReader xmlReader = saxParser.getXMLReader();

                //파싱을 수행해 줄 객체 생성
                SaxHandler saxHandler = new SaxHandler();

                //xml 파싱을 위임
                xmlReader.setContentHandler(saxHandler);

                //데이터 전달
                InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes("utf-8"));

                //파싱 시작
                xmlReader.parse(new InputSource(inputStream));

                //핸들러에게 메시지 전달
                handler.sendEmptyMessage(0);
            }catch (Exception e){
            }
        }
    }

    //xml 파싱을 수행해 줄 클래스
    class SaxHandler extends DefaultHandler {
        String content = null;

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            //Log.e("태그","문서읽기 시작");
            titleList.clear();
            linkList.clear();
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            //Log.e("태그","문서읽기 끝");
            //Log.e("제목", titleList.toString());
            //Log.e("링크",linkList.toString());
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            //Log.e("시작태그 큐네임", qName);
            content = null;
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            //Log.e("종료태그 큐네임", qName);
            if(qName.equals("title")){
                titleList.add(content);
            }else if(qName.equals("link")){
                linkList.add(content);
            }  }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            //Log.e("태그 안의 내용", new String(ch));
            //어디서부터 어디까지인지를 지정해 주는 것. byteStream으로 읽어오는 거랑 같은 원리.
            //이렇게 범위를 지정해주지 않으면 지정한 크기대로 나오게 되고, 비어 있는 부분은 쓰레기로 채워짐.
            content = new String(ch, start, length);
        } }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xmlparsing);

        //ListView 초기화
        titleList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,titleList);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

       //다른 변수 초기화
        linkList = new ArrayList<>();
        swipe_layout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        progressDialog = ProgressDialog.show(this,"매일경제-경제","다운로드 중...");

        //하단으로 드래그 했을 때 수행할 이벤트 핸들
        swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressDialog = ProgressDialog.show(XMLParsing.this,"매일경제-경제", "업데이트 중");
                new ThreadEX().start();
            }
        });
        //스레드 시작
        ThreadEX th = new ThreadEX();
        th.start();

        //리스트 뷰의 항목을 클릭했을 때 호출되는 이벤트 핸들러
        listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String link = linkList.get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intent);
            }
        });
    }
}
