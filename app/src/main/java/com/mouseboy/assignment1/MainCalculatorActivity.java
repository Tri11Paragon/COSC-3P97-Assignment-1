package com.mouseboy.assignment1;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class MainCalculatorActivity extends AppCompatActivity {

    public static String formatDecimal(double value) {
        BigDecimal decimalValue = BigDecimal.valueOf(value);

        String pattern;

        if (decimalValue.stripTrailingZeros().scale() <= 0) {
            pattern = "#,##0";
        } else {
            pattern = "#,##0.##";
        }

        DecimalFormat df = new DecimalFormat(pattern);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');

        df.setDecimalFormatSymbols(symbols);

        return df.format(value);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_calculator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}