package com.example.electricitybill;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;

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
        ArrayList<BillItem> billList = new ArrayList<>(); // Include ID

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int billId = cursor.getInt(0);      // ID column
                String month = cursor.getString(1); // month column (short name like "Jan")
                double finalCost = cursor.getDouble(5); // finalCost column

                billList.add(new BillItem(billId, month, finalCost));
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Define month order (short names to match AddBillActivity)
        final String[] monthOrder = {
                "January","February","March","April","May","June",
                "July","August","September","October","November","December"
        };
        java.util.List<String> monthList = Arrays.asList(monthOrder);

        // Sort by month
        billList.sort((b1, b2) -> {
            int index1 = monthList.indexOf(b1.month);
            int index2 = monthList.indexOf(b2.month);
            return Integer.compare(index1, index2);
        });

        // Populate display list and ID list
        ArrayList<String> displayList = new ArrayList<>();
        idList.clear();
        for (BillItem item : billList) {
            displayList.add(item.month + " - RM " + String.format("%.2f", item.finalCost));
            idList.add(item.id); // keep IDs in sorted order
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                displayList
        );
        listView.setAdapter(adapter);
    }

    // Updated BillItem class
    private static class BillItem {
        int id;
        String month;
        double finalCost;

        BillItem(int id, String month, double finalCost) {
            this.id = id;
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
                // Show confirmation dialog before deleting
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete this bill?")
                        .setPositiveButton("Yes", (confirmDialog, whichButton) -> {
                            dbHelper.deleteBill(billId);
                            loadListView();
                            Toast.makeText(this, "Bill deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        builder.show();
    }

}
