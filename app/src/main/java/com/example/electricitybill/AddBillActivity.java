package com.example.electricitybill;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;

public class AddBillActivity extends AppCompatActivity {

    private Spinner spinnerMonth;
    private EditText etUnits;
    private RadioGroup radioGroupRebate;
    private DataHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);

        setTitle("Add Bill");

        spinnerMonth = findViewById(R.id.spinnerMonth);
        etUnits = findViewById(R.id.etUnits);
        radioGroupRebate = findViewById(R.id.radioGroupRebate);
        Button btnSave = findViewById(R.id.btnSave);

        dbHelper = new DataHelper(this);

        // Populate months in spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList("Jan","Feb","Mar","Apr","May","Jun",
                        "Jul","Aug","Sep","Oct","Nov","Dec")
        );
        spinnerMonth.setAdapter(adapter);

        btnSave.setOnClickListener(v -> saveData());
    }

    private void saveData() {
        String month = spinnerMonth.getSelectedItem().toString();

        // Check if month already exists
        if (dbHelper.monthExists(month)) {
            Toast.makeText(this,
                    "Bill for this month already exists",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Get units
        String unitsStr = etUnits.getText().toString().trim();
        if (unitsStr.isEmpty()) {
            etUnits.setError("Please enter electricity units (kwh)");
            return;
        }

        int units;
        try {
            units = Integer.parseInt(unitsStr);
        } catch (NumberFormatException e) {
            etUnits.setError("Invalid number");
            return;
        }

        if (units <= 0) {
            etUnits.setError("Units must be greater than 0");
            return;
        }

        // Get rebate
        int selectedId = radioGroupRebate.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select rebate percentage", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton rb = findViewById(selectedId);
        double rebate;
        try {
            rebate = Double.parseDouble(rb.getText().toString().replace("%","").trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid rebate value", Toast.LENGTH_SHORT).show();
            return;
        }

        double total = calculateTotalCharge(units);
        double finalCost = total - (total * rebate / 100);

        // Insert into database
        boolean inserted = dbHelper.insertBill(month, units, rebate, total, finalCost);
        if (inserted) {
            Toast.makeText(this, "Bill added successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Failed to add bill", Toast.LENGTH_LONG).show();
        }
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
