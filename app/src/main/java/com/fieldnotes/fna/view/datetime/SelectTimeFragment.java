package com.fieldnotes.fna.view.datetime;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Displays the "Time Select" Dialog
 * <p>
 * Created on 5/10/2018.
 */

public class SelectTimeFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private static TextView tv;

    public SelectTimeFragment() {
    }

    @SuppressLint("ValidFragment")
    public SelectTimeFragment(TextView t) {
        tv = t;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute, false);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        tv.setText(String.format("%02d:%02d", hourOfDay, minute));
    }
}
