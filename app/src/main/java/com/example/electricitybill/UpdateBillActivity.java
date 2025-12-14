package com.example.electricitybill;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;

public class UpdateBillActivity extends AppCompatActivity {

    Spinner spinnerMonth;
    EditText etUnits, etRebate;
    DataHelper dbHelper;
    int billId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bill);

        spinnerMonth = findViewById(R.id.spinnerMonth);
        etUnits = findViewById(R.id.etUnits);
        etRebate = findViewById(R.id.etRebate);
        Button btnUpdate = findViewById(R.id.btnUpdate);

        dbHelper = new DataHelper(this);
        billId = getIntent().getIntExtra("ID", -1);

        spinnerMonth.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList("Jan","Feb","Mar","Apr","May","Jun",
                        "Jul","Aug","Sep","Oct","Nov","Dec")));

        loadData();

        btnUpdate.setOnClickListener(v -> updateData());
    }


    private void loadData() {
        Cursor c = dbHelper.getBillById(billId);
        if (c.moveToFirst()) {
            etUnits.setText(String.valueOf(c.getInt(2)));
            etRebate.setText(String.valueOf(c.getDouble(3)));
        }
        c.close();
    }


    private void updateData() {

        String unitsStr = etUnits.getText().toString().trim();
        String rebateStr = etRebate.getText().toString().trim();

        if (unitsStr.isEmpty()) {
            etUnits.setError("Please enter electricity units");
            etUnits.requestFocus();
            return;
        }

        if (rebateStr.isEmpty()) {
            etRebate.setError("Please enter rebate percentage");
            etRebate.requestFocus();
            return;
        }

        int units = Integer.parseInt(unitsStr);
        double rebate = Double.parseDouble(rebateStr);

        if (units <= 0) {
            etUnits.setError("Units must be greater than 0");
            return;
        }

        if (rebate < 0 || rebate > 5) {
            etRebate.setError("Rebate must be between 0 and 5%");
            return;
        }

        double total = calculateTotalCharge(units);
        double finalCost = total - (total * rebate / 100);

        dbHelper.updateBill(
                billId,
                spinnerMonth.getSelectedItem().toString(),
                units, rebate, total, finalCost
        );

        Toast.makeText(this, "Bill updated successfully", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    private double calculateTotalCharge(int units) {
        double total = 0;
        int r = units;

        if (r > 0) { int x = Math.min(r, 200); total += x * 0.218; r -= x; }
        if (r > 0) { int x = Math.min(r, 100); total += x * 0.334; r -= x; }
        if (r > 0) { int x = Math.min(r, 300); total += x * 0.516; r -= x; }
        if (r > 0) { total += r * 0.546; }

        return total;
    }
}
