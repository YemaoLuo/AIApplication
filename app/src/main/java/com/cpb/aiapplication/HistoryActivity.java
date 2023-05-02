package com.cpb.aiapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cpb.aiapplication.adapter.MyGridViewAdapter;
import com.cpb.aiapplication.helper.DBHelper;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private Button backBtn;
    private Button removeAllBtn;

    private GridView dataList;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        backBtn = findViewById(R.id.backBtn);
        removeAllBtn = findViewById(R.id.removeAllBtn);
        dataList = findViewById(R.id.dataList);
        dbHelper = new DBHelper(this);
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, RunActivity.class);
            startActivity(intent);
        });
        removeAllBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
            builder.setTitle("Confirm");
            builder.setMessage("Remove all history log?");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                boolean flag = dbHelper.removeAll();
                if (flag) {
                    Intent intent = new Intent(HistoryActivity.this, HistoryActivity.class);
                    startActivity(intent);
                    Toast.makeText(HistoryActivity.this, "Done", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HistoryActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("No", (dialogInterface, i) -> Toast.makeText(HistoryActivity.this, "Cancelled", Toast.LENGTH_SHORT).show());
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        List<String> allFiles = dbHelper.loadAll();
        Log.d("HistoryActivity", "Files: " + allFiles.toString());
        if (allFiles.size() == 0) {
            setContentView(R.layout.no_data_item);
            Button backBtn2 = findViewById(R.id.backBtn2);
            backBtn2.setOnClickListener(v -> {
                Intent intent = new Intent(HistoryActivity.this, RunActivity.class);
                startActivity(intent);
            });
        } else {
            dataList.setAdapter(new MyGridViewAdapter(this, allFiles));
        }
    }
}