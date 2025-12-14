package com.example.electricitybill;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    DataHelper dbHelper;
    ArrayList<Integer> idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My App");


        listView = findViewById(R.id.listView);
        dbHelper = new DataHelper(this);
        idList = new ArrayList<>();

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnAbout = findViewById(R.id.btnAbout);

        loadListView();


        listView.setOnItemClickListener((parent, view, position, id) -> {
            showActionDialog(position);
        });


        btnAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddBillActivity.class)));


        btnAbout.setOnClickListener(v ->
                startActivity(new Intent(this, AboutActivity.class)));
    }

    private void loadListView() {
        Cursor cursor = dbHelper.getAllBills();
        ArrayList<String> list = new ArrayList<>();
        idList.clear();

        if (cursor.moveToFirst()) {
            do {
                idList.add(cursor.getInt(0));
                list.add(cursor.getString(1) +
                        " - RM " + String.format("%.2f", cursor.getDouble(5)));
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                list
        );
        listView.setAdapter(adapter);
    }

    private void showActionDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action");
        builder.setItems(new String[]{"View", "Update", "Delete"}, (dialog, which) -> {

            int billId = idList.get(position);

            if (which == 0) {
                startActivity(new Intent(this, ViewBillActivity.class)
                        .putExtra("ID", billId));
            } else if (which == 1) {
                startActivity(new Intent(this, UpdateBillActivity.class)
                        .putExtra("ID", billId));
            } else {
                dbHelper.deleteBill(billId);
                loadListView();
            }
        });
        builder.show();
    }
}
