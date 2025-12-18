package com.example.electricitybill;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

    @SuppressLint("DefaultLocale")
    private void loadListView() {
        Cursor cursor = dbHelper.getAllBills();
        ArrayList<String> list = new ArrayList<>();
        ArrayList<BillItem> tempList = new ArrayList<>(); // Temporary list to sort
        idList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int billId = cursor.getInt(0);      // assuming ID is at column 0
                String month = cursor.getString(1); // month column
                double finalCost = cursor.getDouble(5); // finalCost column

                idList.add(billId);
                tempList.add(new BillItem(month, finalCost)); // store for sorting
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Define month order
        final String[] monthOrder = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        // Sort tempList according to month order
        tempList.sort((b1, b2) -> {
            int index1 = java.util.Arrays.asList(monthOrder).indexOf(b1.month);
            int index2 = java.util.Arrays.asList(monthOrder).indexOf(b2.month);
            return Integer.compare(index1, index2);
        });

        // Convert sorted tempList to display strings
        list.clear();
        for (BillItem item : tempList) {
            list.add(item.month + " - RM " + String.format("%.2f", item.finalCost));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                list
        );
        listView.setAdapter(adapter);
    }

    // Helper class to hold month and cost
    private static class BillItem {
        String month;
        double finalCost;

        BillItem(String month, double finalCost) {
            this.month = month;
            this.finalCost = finalCost;
        }
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
