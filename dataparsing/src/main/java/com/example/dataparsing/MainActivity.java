package com.example.dataparsing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnXML = (Button)findViewById(R.id.btnXML);
        btnXML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, XMLParsing.class);
                startActivity(intent);
            }
        });

        Button btnJSON = (Button)findViewById(R.id.btnJSON);
        btnJSON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, JSONParsing.class);
                startActivity(intent);
            }
        });
    }

}
