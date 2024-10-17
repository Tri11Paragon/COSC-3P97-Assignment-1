package com.mouseboy.assignment1;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class MainCalculatorActivity extends AppCompatActivity {

    // I missing having decltype already
    public static final ArrayList<Integer> buttonIDs = new ArrayList<>();


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

        // evil.
        buttonIDs.add(R.id.b0);
        buttonIDs.add(R.id.b1);
        buttonIDs.add(R.id.b2);
        buttonIDs.add(R.id.b3);
        buttonIDs.add(R.id.b4);
        buttonIDs.add(R.id.b5);
        buttonIDs.add(R.id.b6);
        buttonIDs.add(R.id.b7);
        buttonIDs.add(R.id.b8);
        buttonIDs.add(R.id.b9);

        buttonIDs.add(R.id.bac);
        buttonIDs.add(R.id.bc);

        buttonIDs.add(R.id.bplus);
        buttonIDs.add(R.id.bdiv);
        buttonIDs.add(R.id.bminus);
        buttonIDs.add(R.id.bmul);

        buttonIDs.add(R.id.bdot);
        buttonIDs.add(R.id.bneg);
        buttonIDs.add(R.id.bequals);
        buttonIDs.add(R.id.bpar);

    }

}