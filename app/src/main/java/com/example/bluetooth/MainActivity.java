package com.example.bluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSum= (Button)findViewById(R.id.btnSum);
        TextView resul= (TextView)findViewById(R.id.tVResult);
        EditText eTVal1 = (EditText)findViewById(R.id.eTVal1);
        EditText eTVal2 = (EditText)findViewById(R.id.etVal2);


        btnSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sum = 0;
                int val1= Integer.parseInt(eTVal1.getText().toString());
                int val2=Integer.parseInt(eTVal2.getText().toString());
                sum = val1+ val2;

                resul.setText(String.valueOf(sum));
                eTVal1.setText("0");
                eTVal2.setText("0");
            }
        });
    }

}