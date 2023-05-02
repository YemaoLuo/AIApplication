package com.cpb.aiapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cpb.aiapplication.helper.SolutionHelper;

public class RunActivity extends AppCompatActivity {

    private Button runBtn;
    private Button historyBtn;

    private TextView mVal;
    private TextView nVal;
    private TextView kVal;
    private TextView jVal;
    private TextView sVal;
    private TextView resultView;

    private SolutionHelper sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        runBtn = findViewById(R.id.runBtn);
        historyBtn = findViewById(R.id.historyBtn);
        mVal = findViewById(R.id.mVal);
        nVal = findViewById(R.id.nVal);
        kVal = findViewById(R.id.kVal);
        jVal = findViewById(R.id.jVal);
        sVal = findViewById(R.id.sVal);
        resultView = findViewById(R.id.resultView);
        sh = new SolutionHelper();
        runBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int m, n, k, j, s;
                try {
                    m = Integer.parseInt(mVal.getText().toString());
                    n = Integer.parseInt(nVal.getText().toString());
                    k = Integer.parseInt(kVal.getText().toString());
                    j = Integer.parseInt(jVal.getText().toString());
                    s = Integer.parseInt(sVal.getText().toString());
                    if (!sh.validate(m, n, k, j, s)) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    mVal.setText("");
                    nVal.setText("");
                    kVal.setText("");
                    jVal.setText("");
                    sVal.setText("");
                    Toast.makeText(RunActivity.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("RunActivity", "m: " + m + " n: " + n + " k: " + k + " j: " + j + " s: " + s);
                resultView.setText("Starting...PleaseWait...");
            }
        });
    }
}