package com.example.a503_12.androidnetwork;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;

public class AndroidUser1024 extends AppCompatActivity {
    EditText id, passwd;
    LinearLayout layout01;
    Button loginButton;
    ProgressDialog progressDialog;



    //스레드로 작업 한 후 화면 갱신을 위한 객체.
    //1개만 있으면 Message의 what을 구분해서 사용할 수 있기 때문에 바로 인스턴스 생성
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            if(msg.what == 1){
                layout01.setBackgroundColor(Color.RED);
            }else if(msg.what == 2){
                layout01.setBackgroundColor(Color.GREEN);
            }
        }
    };

    //비동기적으로 작업을 수행하기 위한 스레드 클래스
    //스레드는 재사용이 안되기 때문에 필요할 때 인스턴스를 만들어서 사용하므로
    //클래스로 만들어서 사용함.
    class ThreadEx extends Thread{
        @Override
        public void run() {
            try{
                String addr = "http://192.168.0.13:8080/androidserver/login?userid=";
                String login = id.getText().toString();
                String logpw = passwd.getText().toString();
                addr = addr + login + "&userpw=" + logpw;

                //문자열 주소를 URL로 변경
                URL url = new URL(addr);

                //연결 객체 생성
                HttpURLConnection con = (HttpURLConnection)url.openConnection();

                //옵션 설정
                //캐시(로컬에 저장해두고 사용) 사용 여부
                con.setUseCaches(false);
                //접속을 시도하는 최대 시간.
                //20초 동안 접속이 안되면 예외를 발생시킴. 이게 없으면 무한루프에 빠져버림.
                con.setConnectTimeout(20000);

                //문자열을 다운로드 받을 스트림 생성
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                StringBuilder sb = new StringBuilder();
                while(true){
                    String line = br.readLine();
                    if(line == null) break;
                    sb.append(line + "\n");
                }
                br.close();
                con.disconnect();

                //Log.e("다운 받은 데이터 ", sb.toString());

                //JSON 파싱
                JSONObject result = new JSONObject(sb.toString());
                String x = result.getString("userid");
                //파싱한 결과를 가지고 Message의 what을 달리해서 핸들러에게 전송.
                Message msg = new Message();
                if(x.equals("null")){
                    //Log.e("로그인 여부", "실패");
                    msg.what=1;
                }else {
                    //Log.e("로그인 여부", "성공");
                    msg.what=2;
                }
                handler.sendMessage(msg);

            }catch (Exception e){
                Log.e("다운로드 실패", e.getMessage());
            }
        }
    }


    //onCreate 메소드는 Activity가 만들어질 때 호출되는 메소드.
    //Activity가 실행될 때 무언인가 하고자 하면 onResume 메소드를 써야한다.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //layout 파일을 읽어서 메모리에 로드한 후 화면 출력을 준비하는 메소드 호출.
        //이 코드가 없으면 layout을 쓸 수가 없음.
        setContentView(R.layout.activity_android_user1024);

        id = (EditText)findViewById(R.id.id);
        passwd = (EditText)findViewById(R.id.passwd);
        layout01 = (LinearLayout)findViewById(R.id.layout01);
        loginButton = (Button)findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new Button.OnClickListener() {
            //리스너는 항상 interface라서 메소드는 implements 해야 함.
            @Override
            public void onClick(View v) {
                //진행 대화상자를 출력
                progressDialog = ProgressDialog.show(AndroidUser1024.this, "로그인", "로그인 처리중...");

                //스레드를 만들어서 실행
                ThreadEx th = new ThreadEx();
                th.start();

            }
        });

    }
}
