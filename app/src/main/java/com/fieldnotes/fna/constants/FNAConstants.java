package com.fieldnotes.fna.constants;

public class FNAConstants {

    // FNA Fragment Titles
    public static final String ADD_NOTE_FRAGMENT_TITLE = "Add Note";
    public static final String SEARCH_NOTE_FRAGMENT_TITLE = "Search Note";

    // FNA Preferences
    public static final String PREFS_NAME = "FNPrefs";
    public static final String PREF_USERNAME = "username";
    public static final String PREF_PASSWORD = "password";
    public static final String PREF_TOKEN = "token";
    public static final String PREF_AUTOLOG = "rememberLogin";

    // FNA static Arrays
    // NOTE: there is no way to implement a "spinner hint" with using an Android resource array
    public static final String[] LOCATION_ARRAY = new String[]{"Field", "Office", "Shop", "Not Set", "Location"};
    public static final String[] BILLING_CODE_ARRAY = new String[]{"Billable", "Not Billable", "Turn-key", "Not Set", "Billing"};
}
