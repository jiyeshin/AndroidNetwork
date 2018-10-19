package com.example.a503_12.androidnetwork;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLConnection1019 extends AppCompatActivity {
    EditText txtUrl;
    Button btnDownload;
    TextView txtHtml;

    //진행상황을 출력할 진행 대화상자
    ProgressDialog progressDialog;


    //데이터를 출력할 핸들러 만들기
    //핸들러는 한번만 만들면 되기 때문에 클래스 생성 없이 객체만 만드는 방식으로 생성하면 됨.
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //Log.e("핸들러가 불러졌나요?","네!");
            txtHtml.setText(msg.obj.toString());

            //다운로드 완료 후 progressDialog 닫기
            progressDialog.dismiss();
        }
    };

    //클래스 안만들고 객체 만드는 방법.
    //객체가 딱 한번만 만들어짐. 한번만 사용할 때 이렇게 해도 됨.
    //웬만해선 한번만 사용하는 경우가 잘 없으므로 아래처럼 클래스를 만든 다음에 객체 호출 하는 방법으로 하자.
    Thread th = new Thread(){
    };

    //클래스를 만들고 객체를 별도로 만드는 방법.
    //여러번 호출할 수 있음.
    //버튼 누를 때 마다 호출할 것이므로 이 방법을 사용.
    class  ThreadEx extends Thread{
        @Override
        public void run() {
            try{
                //Log.e("스레드", "스레드 시작");
                //다운로드 받을 주소 가져오기
                String addr = txtUrl.getText().toString();

                //문자열 주소로 url 객체 생성
                URL downloadUrl = new URL(addr);

                //연결 객체 생성
                HttpURLConnection con = (HttpURLConnection)downloadUrl.openConnection();

                //옵션 설정
                con.setConnectTimeout(20000);
                con.setUseCaches(false);
                Log.e("커넥션", con.toString());

                //문자열 다운로드 받기 위해 스트림 생성
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                //줄 단위로 문자열을 읽어서 sb에 추가
                StringBuilder sb = new StringBuilder();
                while(true){
                    String line = br.readLine();

                    if(line == null){
                        break;
                    }else {
                        //Log.e("리드라인", line);
                        sb.append(line+"\n");
                    }
                }
                //전부 가져왔으면 닫기
                br.close();
                con.disconnect();

                //Message에 저장해서 handler에 메시지 전송
                Message message = new Message();
                message.obj = sb.toString();
                handler.sendMessage(message);

            }catch (Exception e){
                //Log.e("다운로드 에러", e.getMessage());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urlconnection1019);

        txtUrl = (EditText)findViewById(R.id.txtUrl);
        btnDownload = (Button)findViewById(R.id.btnDownload);
        txtHtml = (TextView)findViewById(R.id.txtHtml);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(URLConnection1019.this, "","다운로드 중...");
                //Log.e("버튼 클릭", "클릭이벤트 수행 시작");
                ThreadEx th = new ThreadEx();
                th.start();
            }
        });
    }
}
