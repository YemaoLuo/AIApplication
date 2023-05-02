package com.cpb.aiapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button enterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enterBtn = findViewById(R.id.enterBtn);
        enterBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RunActivity.class);
            startActivity(intent);
        });
    }
}