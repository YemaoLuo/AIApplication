package com.cpb.aiapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cpb.aiapplication.helper.DBHelper;
import com.cpb.aiapplication.helper.SolutionHelper;

import java.util.ArrayList;
import java.util.List;

public class RunActivity extends AppCompatActivity {

    private boolean flag;

    private Thread thread;

    private Button runBtn;
    private Button historyBtn;

    private TextView mVal;
    private TextView nVal;
    private TextView kVal;
    private TextView jVal;
    private TextView sVal;
    private TextView chosenSamplesVal;
    private TextView resultView;

    private ProgressBar progressBar;

    private SolutionHelper sh;
    private DBHelper dbh;

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
        chosenSamplesVal = findViewById(R.id.chosenSamplesVal);
        resultView = findViewById(R.id.resultView);
        String noticeText = "m is within the range of [45, 54]\n" +
                "n is within the range of [7, 25]\n" +
                "k is within the range of [4, 7]\n" +
                "s is within the range of [3, 7]\n" +
                "j is between the minimum value of s and k.";
        resultView.setText(noticeText);
        progressBar = findViewById(R.id.progressBar);
        flag = true;
        sh = new SolutionHelper();
        dbh = new DBHelper(this);
        runBtn.setOnClickListener(v -> {
            resultView.setText("Starting...Please Wait...");
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
                    resultView.setText(noticeText);
                    Toast.makeText(RunActivity.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("RunActivity", "m: " + m + " n: " + n + " k: " + k + " j: " + j + " s: " + s);
                thread = new Thread(() -> {
                    long startTime = System.currentTimeMillis();
                    List<List<Integer>> result = new ArrayList<>();
                    List<Integer> chosenSamples;
                    if (chosenSamplesVal.getText().length() == 0) {
                        chosenSamples = sh.generateChosenSamples(m, n);
                        chosenSamplesVal.setText(chosenSamples.toString().substring(1, chosenSamples.toString().length() - 1));
                    } else {
                        chosenSamples = new ArrayList<>();
                        String[] chosenSamplesStr = chosenSamplesVal.getText().toString().split(",");
                        for (String str : chosenSamplesStr) {
                            chosenSamples.add(Integer.parseInt(str.trim()));
                        }
                        if (chosenSamples.size() != n) {
                            chosenSamples = new ArrayList<>(sh.generateChosenSamples(m, n));
                            chosenSamplesVal.setText(chosenSamples.toString().substring(1, chosenSamples.toString().length() - 1));
                        }
                    }
                    List<List<Integer>> possibleResults = sh.generatePossibleResults(chosenSamples, k);
                    List<List<Integer>> coverList = sh.generateCoverList(chosenSamples, j);

                    double initSize = coverList.size();
                    if (n <= 12) {
                        while (!coverList.isEmpty()) {
                            List<Integer> candidateResult = sh.getCandidateResultSingleThread(possibleResults, coverList, s);
                            result.add(candidateResult);
                            sh.removeCoveredResults(candidateResult, coverList, s);
                            possibleResults.remove(candidateResult);
                            double percent = (1 - coverList.size() / initSize) * 100;
                            progressBar.setProgress((int) percent);
                            String showProgress = "Progress: " + (int) percent + "%";
                            resultView.setText(showProgress);
                            Log.d("RunActivity", "Progress: " + percent);
                        }
                    } else {
                        List<Integer> candidateResult = possibleResults.get(0);
                        result.add(candidateResult);
                        sh.removeCoveredResults(candidateResult, coverList, s);
                        while (!coverList.isEmpty()) {
                            candidateResult = sh.getCandidateResult(candidateResult, possibleResults, coverList, s);
                            result.add(candidateResult);
                            sh.removeCoveredResults(candidateResult, coverList, s);
                            possibleResults.remove(candidateResult);
                            double percent = (1 - coverList.size() / initSize) * 100;
                            progressBar.setProgress((int) percent);
                            String showProgress = "Progress: " + (int) percent + "%";
                            resultView.setText(showProgress);
                            Log.d("RunActivity", "Progress: " + percent);
                        }
                    }

                    String resultStr = "";
                    resultStr += "Chosen samples: \n" + chosenSamples + "\n" + "\n";
                    resultStr += "Result: \n";
                    for (List<Integer> list : result) {
                        resultStr += list.toString() + "\n";
                    }
                    resultStr += "\n";
                    resultStr += "Reuslt Size: " + result.size() + "\n" + "\n"
                            + "Total time cost: " + (System.currentTimeMillis() - startTime) + "ms";
                    String finalResultStr = resultStr;
                    runOnUiThread(() -> {
                        Log.d("RunActivity", finalResultStr);
                        resultView.setText(finalResultStr);
                        String fileName = dbh.nameBuilder(m, n, k, j, s);
                        if (fileName.equals("ERROR")) {
                            Toast.makeText(RunActivity.this, "DB Error", Toast.LENGTH_SHORT).show();
                        } else {
                            boolean save = dbh.save(fileName, finalResultStr);
                            if (!save) {
                                Toast.makeText(RunActivity.this, "Save Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    flag = true;
                });
                thread.start();
                flag = false;
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(RunActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("A thread is running. Want to kill it?");
                builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                    Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    startActivity(intent);
                    System.exit(0);
                });
                builder.setNegativeButton("No", (dialogInterface, i) -> Toast.makeText(RunActivity.this, "Cancelled", Toast.LENGTH_SHORT).show());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        historyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RunActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
        Intent flagIntent = getIntent();
        String data = flagIntent.getStringExtra("flag");
        if (data != null && data.equals("true")) {
            flagIntent.removeExtra("flag");
            historyBtn.performClick();
        }
    }
}