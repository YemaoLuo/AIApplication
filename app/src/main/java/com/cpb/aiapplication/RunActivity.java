package com.cpb.aiapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cpb.aiapplication.helper.SolutionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RunActivity extends AppCompatActivity {

    private boolean flag = true;

    private Thread thread;

    private Button runBtn;
    private Button historyBtn;

    private TextView mVal;
    private TextView nVal;
    private TextView kVal;
    private TextView jVal;
    private TextView sVal;
    private TextView resultView;

    private ProgressBar progressBar;

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
        progressBar = findViewById(R.id.progressBar);
        sh = new SolutionHelper();
        runBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    progressBar.setProgress(0);
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultView.setText("Starting...Please Wait...");
                        }
                    });
                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            long startTime = System.currentTimeMillis();
                            List<List<Integer>> result = new ArrayList<>();
                            List<Integer> chosenSamples = sh.generateChosenSamples(m, n);
                            List<List<Integer>> possibleResults = sh.generatePossibleResults(chosenSamples, k);
                            List<Set<Integer>> coverList = sh.generateCoverList(chosenSamples, j);

                            double initSize = coverList.size();
                            if (n <= 12) {
                                while (!coverList.isEmpty()) {
                                    List<Integer> candidateResult = sh.getCandidateResultSingleThread(possibleResults, coverList, s);
                                    result.add(candidateResult);
                                    sh.removeCoveredResults(candidateResult, coverList, s);
                                    possibleResults.remove(candidateResult);
                                    double percent = (1 - coverList.size() / initSize) * 100;
                                    progressBar.setProgress((int) percent);
                                    Log.d("RunActivity", "Progress: " + percent);
                                }
                            } else {
                                while (!coverList.isEmpty()) {
                                    List<Integer> candidateResult = sh.getCandidateResult(possibleResults, coverList, s);
                                    result.add(candidateResult);
                                    sh.removeCoveredResults(candidateResult, coverList, s);
                                    possibleResults.remove(candidateResult);
                                    double percent = (1 - coverList.size() / initSize) * 100;
                                    progressBar.setProgress((int) percent);
                                }
                            }

                            String resultStr = "";
                            resultStr += "Result: \n";
                            for (List<Integer> list : result) {
                                resultStr += list.toString() + "\n";
                            }
                            resultStr += "\n";
                            resultStr += "Reuslt Size: " + result.size() + "\n" + "\n"
                                    + "Total time cost: " + (System.currentTimeMillis() - startTime) + "ms";
                            String finalResultStr = resultStr;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("RunActivity", finalResultStr);
                                    resultView.setText(finalResultStr);
                                }
                            });
                            flag = true;
                        }
                    });
                    thread.start();
                    flag = false;
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RunActivity.this);
                    builder.setTitle("Confirm");
                    builder.setMessage("A thread is running. Want to kill it?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            thread.interrupt();
                            flag = true;
                            progressBar.setProgress(0);
                            Toast.makeText(RunActivity.this, "Thread killed", Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(RunActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
}