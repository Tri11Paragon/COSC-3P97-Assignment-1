package com.mouseboy.assignment1.helpers;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Utils {

    // why does java not have a nice decimal formatter
    // function makes decimals nice, only displays 2 digits of precision
    public static String formatDecimal(double value) {
        BigDecimal decimalValue = BigDecimal.valueOf(value);

        String pattern;
        if (decimalValue.stripTrailingZeros().scale() <= 0)
            pattern = "#,##0";
        else
            pattern = "#,##0.##";

        return new DecimalFormat(pattern).format(value);
    }

}
