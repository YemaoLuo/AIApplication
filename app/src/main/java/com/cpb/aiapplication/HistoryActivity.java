package com.cpb.aiapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cpb.aiapplication.helper.DBHelper;

public class HistoryActivity extends AppCompatActivity {

    private Button backBtn;
    private Button removeAllBtn;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        backBtn = findViewById(R.id.backBtn);
        removeAllBtn = findViewById(R.id.removeAllBtn);
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
                    Toast.makeText(HistoryActivity.this, "Done", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(HistoryActivity.this, "Failed", Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton("No", (dialogInterface, i) -> Toast.makeText(HistoryActivity.this, "Cancelled", Toast.LENGTH_LONG).show());
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        Log.d("HistoryActivity", "Files: " + dbHelper.loadAll().toString());
    }
}