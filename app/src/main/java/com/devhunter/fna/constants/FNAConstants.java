package com.devhunter.fna.constants;

/**
 * Created by inertbear on 10/28/18.
 */

public class FNAConstants {

    // FNA Titles
    public static final String ADD_NOTE_FRAGMENT_TITLE = "Add Note";
    public static final String SEARCH_NOTE_FRAGMENT_TITLE = "Search Note";

    // FNA Preferences
    public static final String PREFS_NAME = "fnPrefFile";
    public static final String PREF_USERNAME = "username";
    public static final String PREF_PASSWORD = "password";
    public static final String PREF_CUSTOMER_KEY = "customerkey";
    public static final String PREF_REMEMBER_LOGIN = "remember_login";

    // FNA static Arrays
    // NOTE: there is no way to implement a "spinner hint" with using an Android resource array
    public static final String[] LOCATION_ARRAY = new String[]{"Field", "Office", "Shop", "N/A", "Location"};
    public static final String[] BILLING_CODE_ARRAY = new String[]{"Billable", "Not Billable", "Turn-key", "N/A", "Billing"};
}
