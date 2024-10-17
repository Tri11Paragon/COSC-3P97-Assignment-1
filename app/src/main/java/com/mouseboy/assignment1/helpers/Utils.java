package com.mouseboy.assignment1.helpers;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Utils {

    // why does java not have a nice decimal formatter
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

}
