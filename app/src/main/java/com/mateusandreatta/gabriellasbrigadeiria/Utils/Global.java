package com.mateusandreatta.gabriellasbrigadeiria.Utils;

import android.content.Context;

import com.mateusandreatta.gabriellasbrigadeiria.R;

import java.text.NumberFormat;
import java.util.Locale;

public class Global {

    public static String gabriellasBrigadeiriaAddress = "-25.512240092843843, -49.1847911890005";

    public static String translateFirebaseException(Context c, String msg){

        switch (msg) {
            case "The email address is badly formatted.":
                return c.getResources().getString(R.string.firebase_exception_email_badly_formattd);
            case "The given password is invalid. [ Password should be at least 6 characters ]":
                return c.getResources().getString(R.string.firebase_exception_password_invalid_least_6_characters);
            case "The email address is already in use by another account.":
                return c.getResources().getString(R.string.firebase_exception_email_already_in_use);
            case "There is no user record corresponding to this identifier. The user may have been deleted.":
                return c.getResources().getString(R.string.firebase_exception_no_user_record_with_identifier);
            case "The password is invalid or the user does not have a password.":
                return c.getResources().getString(R.string.firebase_exception_password_invalid);
            default:
                return msg;
        }
    }

    public static String formatCurrencyDoubleValue(Double price){
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
        return numberFormat.format(price);
    }

    public static Double getDoubleValueFromMaskedEditText(String s){
        String clean = s.replace(" ", "");
        clean = clean.replace(" ", "");
        clean = clean.replace("R$", "");
        clean = clean.replace(".","");
        clean = clean.replace(",", ".");
        return Double.parseDouble(clean);
    }

}
