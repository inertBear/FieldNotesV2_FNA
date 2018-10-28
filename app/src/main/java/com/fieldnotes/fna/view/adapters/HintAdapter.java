package com.fieldnotes.fna.view.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * This adapter works as a hack to include a HINT on a Spinner view object. The spinner array needs
 * to be programmatically added to the Spinner with the last value in the array being the "hint"
 * example:
 * "String[] billingCodeArray = new String[]{"value 1", "value 2", ..., "YOUR_HINT"}
 * <p>
 * Created on 5/14/2018.
 */

public class HintAdapter extends ArrayAdapter<String> {

    public HintAdapter(Context context, int resource) {
        super(context, resource);
    }

    public HintAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public HintAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
    }

    public HintAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public HintAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    public HintAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public int getCount() {
        // don't display last item. It is used as hint.
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return setCentered(super.getView(position, convertView, parent));
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return setCentered(super.getDropDownView(position, convertView, parent));
    }

    private View setCentered(View view) {
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
//        textView.setGravity(Gravity.CENTER);
        return view;
    }

}