package com.fieldnotes.fna.preferences;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fieldnotes.fna.R;

import static android.content.Context.MODE_PRIVATE;
import static com.fieldnotes.fna.constants.FNAConstants.PREFS_NAME;
import static com.fieldnotes.fna.constants.FNAConstants.PREF_AUTOLOG;

public class PreferenceFragment extends PreferenceFragmentCompat {
    private SwitchPreferenceCompat mRememberLogin;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.layout_preferences);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        // hide transparent background of fragment
        if (view != null) {
            view.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
        // Remember Login preference
        mRememberLogin = (SwitchPreferenceCompat) findPreference("remember_login");
        mRememberLogin.setChecked(getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(PREF_AUTOLOG, false));
        mRememberLogin.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (!mRememberLogin.isChecked()) {
                    //user "turns on" switch
                    getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                            .edit()
                            .putBoolean(PREF_AUTOLOG, true)
                            .apply();
                } else {
                    // user "turns off" switch
                    getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                            .edit()
                            .putBoolean(PREF_AUTOLOG, false)
                            .apply();
                }
                return true;
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        View user_settings = getActivity().findViewById(R.id.user_settings);
        user_settings.setVisibility(View.VISIBLE);
    }
}