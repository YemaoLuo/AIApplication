package com.cpb.aiapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RunActivity extends AppCompatActivity {

    private Button runBtn = findViewById(R.id.runBtn);

    private TextView mVal = findViewById(R.id.mVal);
    private TextView nVal = findViewById(R.id.nVal);
    private TextView kVal = findViewById(R.id.kVal);
    private TextView jVal = findViewById(R.id.jVal);
    private TextView sVal = findViewById(R.id.sVal);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
    }
}