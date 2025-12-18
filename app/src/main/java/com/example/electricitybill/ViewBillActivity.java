package com.example.electricitybill;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ViewBillActivity extends AppCompatActivity {

    DataHelper dbHelper;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bill);

        dbHelper = new DataHelper(this);

        int id = getIntent().getIntExtra("ID", -1);

        TextView tv = findViewById(R.id.tvDetail);

        Cursor c = dbHelper.getBillById(id);
        if (c.moveToFirst()) {
            tv.setText(
                    "Month: " + c.getString(1) +
                            "\nUnits (kWh): " + c.getInt(2) +
                            "\nRebate: " + String.format("%.2f", c.getDouble(3)) + "%" +
                            "\nTotal cost: RM " + String.format("%.2f", c.getDouble(4)) +
                            "\nFinal cost after rebate: RM " + String.format("%.2f", c.getDouble(5))
            );
        }
        c.close();
    }
}
