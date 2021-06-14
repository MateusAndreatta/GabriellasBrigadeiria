package com.mateusandreatta.gabriellasbrigadeiria.Utils;


import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;

public class BrRealMoneyTextWatcher implements TextWatcher {

    public static final Locale DEFAULT_LOCALE = new Locale("pt", "BR");

    public static DecimalFormat NUMBER_FORMAT = (DecimalFormat) DecimalFormat.getCurrencyInstance(DEFAULT_LOCALE);

    public static class Helper {
        public static String formatNumber(String originalNumber) {
            String number = originalNumber.replaceAll("[^\\d]", "");
            BigDecimal value = new BigDecimal(number).movePointLeft(FRACTION_DIGITS);
            return NUMBER_FORMAT.format(value);
        }
    }

    public static final int FRACTION_DIGITS = 2;

    public static final String DECIMAL_SEPARATOR;

    public static final String CURRENCY_SYMBOL;

    static {
        NUMBER_FORMAT.setMaximumFractionDigits(FRACTION_DIGITS);
        NUMBER_FORMAT.setMaximumFractionDigits(FRACTION_DIGITS);
        NUMBER_FORMAT.setParseBigDecimal(true);
        DECIMAL_SEPARATOR = String.valueOf(NUMBER_FORMAT.getDecimalFormatSymbols().getDecimalSeparator());
        CURRENCY_SYMBOL = NUMBER_FORMAT.getCurrency().getSymbol(DEFAULT_LOCALE);
    }

    final EditText target;

    public BrRealMoneyTextWatcher(EditText target) {
        this.target = target;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() != 0) {
            target.removeTextChangedListener(this);
            String valueStr = Helper.formatNumber(s.toString());
            target.setText(valueStr);
            target.setSelection(valueStr.length());
            target.addTextChangedListener(this);
        }
    }
}