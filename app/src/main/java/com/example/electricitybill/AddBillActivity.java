package com.example.electricitybill;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;

public class AddBillActivity extends AppCompatActivity {

    private Spinner spinnerMonth;
    private EditText etUnits;
    private RadioGroup radioGroupRebate;
    private TextView tvTotal, tvFinalCost;
    private DataHelper dbHelper;

    private double currentTotal = 0;
    private double currentFinal = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);

        setTitle("Add Bill");

        spinnerMonth = findViewById(R.id.spinnerMonth);
        etUnits = findViewById(R.id.etUnits);
        radioGroupRebate = findViewById(R.id.radioGroupRebate);
        tvTotal = findViewById(R.id.tvTotal);
        tvFinalCost = findViewById(R.id.tvFinalCost);
        Button btnSave = findViewById(R.id.btnSave);

        dbHelper = new DataHelper(this);

        // Populate months in spinner with placeholder
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList("Select Month","January","February","March","April","May","June",
                        "July","August","September","October","November","December")
        );
        spinnerMonth.setAdapter(adapter);

        // Recalculate totals when units change
        etUnits.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { calculateAndDisplay(); }
        });

        // Recalculate totals when rebate changes
        radioGroupRebate.setOnCheckedChangeListener((group, checkedId) -> calculateAndDisplay());

        btnSave.setOnClickListener(v -> saveData());
    }

    private void calculateAndDisplay() {
        String unitsStr = etUnits.getText().toString().trim();
        int units = 0;
        try {
            units = Integer.parseInt(unitsStr);
            if (units <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            tvTotal.setText("Total: -");
            tvFinalCost.setText("Final: -");
            return;
        }

        currentTotal = calculateTotalCharge(units);

        int selectedId = radioGroupRebate.getCheckedRadioButtonId();
        double rebate = 0;
        if (selectedId != -1) {
            RadioButton rb = findViewById(selectedId);
            try {
                rebate = Double.parseDouble(rb.getText().toString().replace("%","").trim());
            } catch (NumberFormatException ignored) {}
        }

        currentFinal = currentTotal - (currentTotal * rebate / 100);

        tvTotal.setText(String.format("Total cost: RM %.2f", currentTotal));
        tvFinalCost.setText(String.format("Final cost: RM %.2f", currentFinal));
    }

    private void saveData() {
        String month = spinnerMonth.getSelectedItem().toString();

        // Check if user has selected a valid month
        if (month.equals("Select Month")) {
            Toast.makeText(this, "Please select a month", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.monthExists(month)) {
            Toast.makeText(this, "Bill for this month already exists", Toast.LENGTH_LONG).show();
            return;
        }

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

        // Save current calculated values
        boolean inserted = dbHelper.insertBill(month, units, rebate, currentTotal, currentFinal);
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
