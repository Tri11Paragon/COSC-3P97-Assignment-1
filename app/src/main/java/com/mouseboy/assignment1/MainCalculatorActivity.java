package com.mouseboy.assignment1;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mouseboy.assignment1.parser.StateFarm;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class MainCalculatorActivity extends AppCompatActivity {

    // I missing having decltype already
    private final StateFarm state = new StateFarm(this,"");

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

        findViewById(R.id.b0).setOnClickListener((View view) -> state.addNumber("0"));
        findViewById(R.id.b1).setOnClickListener((View view) -> state.addNumber("1"));
        findViewById(R.id.b2).setOnClickListener((View view) -> state.addNumber("2"));
        findViewById(R.id.b3).setOnClickListener((View view) -> state.addNumber("3"));
        findViewById(R.id.b4).setOnClickListener((View view) -> state.addNumber("4"));
        findViewById(R.id.b5).setOnClickListener((View view) -> state.addNumber("5"));
        findViewById(R.id.b6).setOnClickListener((View view) -> state.addNumber("6"));
        findViewById(R.id.b7).setOnClickListener((View view) -> state.addNumber("7"));
        findViewById(R.id.b8).setOnClickListener((View view) -> state.addNumber("8"));
        findViewById(R.id.b9).setOnClickListener((View view) -> state.addNumber("9"));

        findViewById(R.id.bac).setOnClickListener((View view) -> state.clearAll());
        findViewById(R.id.bc).setOnClickListener((View view) -> state.clearCurrent());

        findViewById(R.id.bplus).setOnClickListener((View view) -> state.addOperator("+"));
        findViewById(R.id.bdiv).setOnClickListener((View view) -> state.addOperator("÷"));
        findViewById(R.id.bminus).setOnClickListener((View view) -> state.addOperator("-"));
        findViewById(R.id.bmul).setOnClickListener((View view) -> state.addOperator("*"));

        findViewById(R.id.bdot).setOnClickListener((View view) -> state.dot());
        findViewById(R.id.bneg).setOnClickListener((View view) -> state.neg());
        findViewById(R.id.bequals).setOnClickListener((View view) -> state.equals());
        findViewById(R.id.bpar).setOnClickListener((View view) -> state.paren());
    }

}