package com.example.electricitybill;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ViewBillActivity extends AppCompatActivity {

    DataHelper dbHelper;

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
                            "\nUnits: " + c.getInt(2) +
                            "\nRebate: " + c.getDouble(3) + "%" +
                            "\nTotal: RM " + c.getDouble(4) +
                            "\nFinal: RM " + c.getDouble(5)
            );
        }
    }
}
