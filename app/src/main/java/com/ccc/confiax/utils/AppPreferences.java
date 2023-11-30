package com.ccc.confiax.utils;

import org.joda.time.LocalDate;

import java.text.DateFormat;
import java.util.Date;

/**
 * Utility class holding common application preferences.
 */
public final class AppPreferences {

    public static final int PERIOD_NOTIFICATION_ID = 4321;
    public static final int FERTILE_NOTIFICATION_ID = 4322;
    public static final int OVULATION_NOTIFICATION_ID = 4323;

    private AppPreferences() { }

    public static final String APPLICATION_PREFIX = "com.ccc.confiax.";
    public static final String SHARED_PREFERENCES_FILE = APPLICATION_PREFIX + "shared_preferences";
    public static final String BASIC_USER_PREFERENCES_AVAILABLE = "basic_user_preferences_available";
    public static final String DATE_PICKER_DIALOG_TAG = APPLICATION_PREFIX + "date_picker_dialog";
    public static final String MENSTRUATION_LENGTH_KEY = APPLICATION_PREFIX + "menstruation_length";
    public static final String DEFAULT_MENSTRUATION_LENGTH = "5";
    public static final String PERIOD_LENGTH_KEY = APPLICATION_PREFIX + "period_length";
    public static final String DEFAULT_PERIOD_LENGTH = "28";
    public static final String LAST_PERIOD_DATE_KEY = APPLICATION_PREFIX + "last_period_date";

    public static String defaultLastPeriodDate() {
        return convertDateToString(new LocalDate());
    }

    public static String convertDateToString(LocalDate localDate) {
        return DateFormat.getDateInstance(DateFormat.SHORT).format(localDate.toDate());
    }

    public static LocalDate convertStringToDate(String dateString, LocalDate defaultDate) {
        Date d = OptionalUtil.parseDate(dateString, DateFormat.getDateInstance(DateFormat.SHORT)).orNull();
        return d != null ? new LocalDate(d) : defaultDate;
    }

    public static final String INCOMING_PERIOD_NOTIFICATION_KEY = APPLICATION_PREFIX + "incoming_period_notification";
    public static final String FERTILE_DAYS_NOTIFICATION_KEY = APPLICATION_PREFIX + "fertile_days_notification";
    public static final String OVULATION_NOTIFICATION_KEY = APPLICATION_PREFIX + "ovulation_notification";
    public static final String PERIOD_DAYS_BEANS_LIST_KEY = APPLICATION_PREFIX + "period_days_beans_list";
}