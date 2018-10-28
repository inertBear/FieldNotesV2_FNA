package com.fieldnotes.fna.validation;

import android.content.Context;

/**
 * This class handles the validation of the FieldNote objects. It is responsible for ensuring that no
 * incomplete or incorrect values get from the User-end and into the database. FieldNoteValidation will
 * throw an IllegalArgumentException if the data is incomplete.
 *
 * Create on 5/9/18
 */

public class FNValidate {

    private Context mContext;

    private FNValidate(Context context) {
        mContext = context;
    }

    /**
     * validate String being put into a FieldNote
     *
     * @param value
     * @return
     */
    public static String validate(String value) throws IllegalArgumentException {
        if(!value.trim().isEmpty()) {
            value = value.replaceAll(",", ".");
            value = value.replaceAll("(?:\\n|\\r)", ". ");
            return value;
        } else {
            throw new IllegalArgumentException("Please Fill in ALL fields");
        }
    }

    //TODO: change to DATETIME and overload method name
    public static String validateDateTime(String value) throws IllegalArgumentException{
        if(!value.trim().isEmpty()) {
            return value;
        } else {
            throw new IllegalArgumentException("Please select Dates and Times");
        }
    }

    //TODO: change to int and overload method name
    public static String validateInt(String value) throws IllegalArgumentException{
        if(!value.trim().isEmpty()){
            return value;
        } else {
            throw new IllegalArgumentException("Please select Start and End Mileage");
        }
    }

    public static String validateSpinner(String value) throws IllegalArgumentException{
        if(!value.equals("Location") || value.equals("Billing")){
            return value;
        } else {
            throw new IllegalArgumentException("Please select Location Code and Billing Code");
        }
    }
}
