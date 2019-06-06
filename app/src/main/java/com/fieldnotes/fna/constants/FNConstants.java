package com.fieldnotes.fna.constants;

public class FNConstants {

    // JSON Data tags
    public static final String TICKET_NUMBER_TAG = "ticketNumber";
    public static final String USER_TAG = "userName";
    public static final String WELLNAME_TAG = "wellName";
    public static final String DATE_START_TAG = "dateStart";
    public static final String TIME_START_TAG = "timeStart";
    public static final String MILEAGE_START_TAG = "mileageStart";
    public static final String DESCRIPTION_TAG = "description";
    public static final String MILEAGE_END_TAG = "mileageEnd";
    public static final String DATE_END_TAG = "dateEnd";
    public static final String TIME_END_TAG = "timeEnd";
    public static final String PROJECT_NUMBER_TAG = "projectNumber";
    public static final String LOCATION_TAG = "location";
    public static final String GPS_TAG = "gps";
    public static final String BILLING_TAG = "billing";
    public static final String TOKEN_TAG = "token";

    // JSON User tags
    public static final String USERNAME_TAG = "UserName";
    public static final String USER_PASSWORD_TAG = "UserPassword";

    // JSON response tags
    public static final String RESPONSE_STATUS_TAG = "status";
    public static final String RESPONSE_STATUS_SUCCESS = "success";
    public static final String RESPONSE_STATUS_FAILURE = "failure";
    public static final String RESPONSE_MESSAGE_TAG = "message";
    public static final String RESPONSE_TOKEN_TAG = "token";

    // Web Service URLs
    public static final String REGISTER_URL = "http://www.fieldnotesfn.com/FN_PROCESSOR/FN_register.php";
    public static final String LOGIN_URL = "http://www.fieldnotesfn.com/FN_PROCESSOR/FN_login.php";
    public static final String ADD_NOTE_URL = "http://www.fieldnotesfn.com/FN_PROCESSOR/FN_addNote.php";
    public static final String SEARCH_NOTES_URL = "http://www.fieldnotesfn.com/FN_PROCESSOR/FN_searchNotes.php";
    public static final String UPDATE_NOTE_URL = "http://www.fieldnotesfn.com/FN_PROCESSOR/FN_updateNote.php";
    public static final String DELETE_NOTE_URL = "http://www.fieldnotesfn.com/FN_PROCESSOR/FN_deleteNote.php";
    public static final String ADD_USER_URL = "http://www.fieldnotesfn.com/FN_PROCESSOR/FN_addUser.php";
    public static final String SEARCH_USERS_URL = "http://www.fieldnotesfn.com/FN_PROCESSOR/FN_searchUsers.php";
    public static final String UPDATE_PASSWORD_URL = "http://www.fieldnotesfn.com/FN_PROCESSOR/FN_changePassword.php";
    public static final String DELETE_USER_URL = "http://www.fieldnotesfn.com/FN_PROCESSOR/FN_deleteUser.php";

    // HTTP Request Methods
    public static final String HTTP_REQUEST_METHOD_POST = "POST";
    public static final String HTTP_REQUEST_METHOD_GET = "GET";
}
