package com.example.electricitybill;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;

public class AddBillActivity extends AppCompatActivity {

    Spinner spinnerMonth;
    EditText etUnits, etRebate;
    DataHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);

        spinnerMonth = findViewById(R.id.spinnerMonth);
        etUnits = findViewById(R.id.etUnits);
        etRebate = findViewById(R.id.etRebate);
        Button btnSave = findViewById(R.id.btnSave);

        dbHelper = new DataHelper(this);

        spinnerMonth.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList("Jan","Feb","Mar","Apr","May","Jun",
                        "Jul","Aug","Sep","Oct","Nov","Dec")));

        btnSave.setOnClickListener(v -> saveData());
    }

    private void saveData() {
        int units = Integer.parseInt(etUnits.getText().toString());
        double rebate = Double.parseDouble(etRebate.getText().toString());

        double total = calculateTotalCharge(units);
        double finalCost = total - (total * rebate / 100);

        dbHelper.insertBill(
                spinnerMonth.getSelectedItem().toString(),
                units, rebate, total, finalCost
        );

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

