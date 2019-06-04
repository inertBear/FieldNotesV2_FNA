package com.fieldnotes.fna.view.datetime;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Displays the "Date Select" Dialog
 * <p>
 * Created on 5/9/2018.
 */

public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static TextView tv;

    public SelectDateFragment() {
    }

    @SuppressLint("ValidFragment")
    public SelectDateFragment(TextView t) {
        tv = t;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, yy, mm, dd);
    }

    public void onDateSet(DatePicker view, int yy, int mm, int dd) {
        String date = yy + "-" + (mm + 1) + "-" + dd;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-m-d");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-mm-dd");
        try {
            tv.setText(sdf2.format(sdf.parse(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
