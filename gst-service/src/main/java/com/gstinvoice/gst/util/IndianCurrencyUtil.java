package com.gstinvoice.gst.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class IndianCurrencyUtil {

    private static final String[] UNITS = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
            "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] TENS = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    public static String convertToWords(BigDecimal amount) {
        if (amount == null) return "Zero Rupees Only";

        BigDecimal rounded = amount.setScale(2, RoundingMode.HALF_UP);
        long rupees = rounded.longValue();
        int paise = rounded.remainder(BigDecimal.ONE).multiply(new BigDecimal(100)).intValue();

        if (rupees == 0 && paise == 0) {
            return "Zero Rupees Only";
        }

        StringBuilder words = new StringBuilder();
        if (rupees > 0) {
            words.append("Rupees ").append(convertNumber(rupees));
        }

        if (paise > 0) {
            if (rupees > 0) {
                words.append(" and ");
            }
            words.append(convertNumber(paise)).append(" Paise");
        }

        words.append(" Only");
        return words.toString().replaceAll("\\s+", " ").trim();
    }

    private static String convertNumber(long n) {
        if (n == 0) return "";
        if (n < 20) return UNITS[(int) n];
        if (n < 100) return TENS[(int) n / 10] + ((n % 10 != 0) ? " " + UNITS[(int) n % 10] : "");
        if (n < 1000) return UNITS[(int) n / 100] + " Hundred" + ((n % 100 != 0) ? " " + convertNumber(n % 100) : "");
        if (n < 100000) return convertNumber(n / 1000) + " Thousand" + ((n % 1000 != 0) ? " " + convertNumber(n % 1000) : "");
        if (n < 10000000) return convertNumber(n / 100000) + " Lakh" + ((n % 100000 != 0) ? " " + convertNumber(n % 100000) : "");
        return convertNumber(n / 10000000) + " Crore" + ((n % 10000000 != 0) ? " " + convertNumber(n % 10000000) : "");
    }
}

