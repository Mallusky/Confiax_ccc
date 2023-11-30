package com.ccc.confiax.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ccc.confiax.beans.MenstrualCycleBean;
import com.ccc.confiax.utils.AppPreferences;
import com.ccc.confiax.utils.LocalDateSerializer;
import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.LocalDate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MenstrualCycleManager {

    private static final String TAG = "h";

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
            .create();

    private static final Type PERIOD_DAYS_GSON_TYPE = new TypeToken<ArrayList<MenstrualCycleBean>>(){}.getType();
    public static final String EMPTY_LIST = "[]";

    private final Context ctx;

    public MenstrualCycleManager(Context ctx) {
        this.ctx = ctx;
    }

    private SharedPreferences getPreferences() {
        return ctx.getSharedPreferences(AppPreferences.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    public List<MenstrualCycleBean> getAllMenstrualCycleBeans() {
        String strMenstrualCycle = getPreferences().getString(AppPreferences.PERIOD_DAYS_BEANS_LIST_KEY, EMPTY_LIST);
        return GSON.fromJson(strMenstrualCycle, PERIOD_DAYS_GSON_TYPE);
    }

    public Optional<MenstrualCycleBean> getLastPeriodDaysBean() {
        List<MenstrualCycleBean> beanList = getAllMenstrualCycleBeans();
        if (beanList.isEmpty()) {
            return Optional.absent();
        }

        return Optional.fromNullable(beanList.get(beanList.size() - 1));
    }

    public Set<LocalDate> getHistoricPeriodDays() {
        return getHistoricPeriodDays(getAllMenstrualCycleBeans());
    }

    public Set<LocalDate> getHistoricPeriodDays(List<MenstrualCycleBean> periodDaysBeanList) {
        Set<LocalDate> set = new HashSet<>();

        for (MenstrualCycleBean bean : periodDaysBeanList) {
            set.addAll(bean.getPeriodDays());
        }

        return set;
    }

    public Set<LocalDate> getHistoricFertileDays() {
        return getHistoricFertileDays(getAllMenstrualCycleBeans());
    }

    public Set<LocalDate> getHistoricFertileDays(List<MenstrualCycleBean> periodDaysBeanList) {
        Set<LocalDate> set = new HashSet<>();

        for (MenstrualCycleBean bean : periodDaysBeanList) {
            set.addAll(bean.getFertileDays());
        }

        return set;
    }

    public Set<LocalDate> getHistoricOvulationDays() {
        return getHistoricOvulationDays(getAllMenstrualCycleBeans());
    }

    public Set<LocalDate> getHistoricOvulationDays(List<MenstrualCycleBean> periodDaysBeanList) {
        Set<LocalDate> set = new HashSet<>();

        for (MenstrualCycleBean bean : periodDaysBeanList) {
            set.add(bean.getOvulationDay());
        }

        return set;
    }

    public int getPeriodLength() {
        return Integer.parseInt(getPreferences().getString(AppPreferences.PERIOD_LENGTH_KEY,
                AppPreferences.DEFAULT_PERIOD_LENGTH));
    }

    public int getMenstruationLength() {
        return Integer.parseInt(getPreferences().getString(AppPreferences.MENSTRUATION_LENGTH_KEY,
                AppPreferences.DEFAULT_MENSTRUATION_LENGTH));
    }

    public Optional<LocalDate> getLastPeriodDate() {
        String stringDate = getPreferences().getString(AppPreferences.LAST_PERIOD_DATE_KEY, null);
        if (stringDate == null) {
            return Optional.absent();
        }

        return Optional.fromNullable(AppPreferences.convertStringToDate(stringDate, null));
    }

    public void updateAllPeriodBeans(List<MenstrualCycleBean> periodDaysBeans) {
        String listString = GSON.toJson(periodDaysBeans, PERIOD_DAYS_GSON_TYPE);
        Log.i(TAG, String.format("New period days bean list: %s", listString));
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(AppPreferences.PERIOD_DAYS_BEANS_LIST_KEY, listString);
        editor.commit();
    }

    public void clearPeriodDates() {
        updateAllPeriodBeans(Collections.<MenstrualCycleBean>emptyList());
    }

    public void updateLastPeriodDate(LocalDate updatedLastPeriodDate) {
        String stringDate = AppPreferences.convertDateToString(updatedLastPeriodDate);
        Log.i(TAG, String.format("Updating last period day to: %s", stringDate));
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(AppPreferences.LAST_PERIOD_DATE_KEY, stringDate);
        editor.commit();
    }

    public boolean sendPeriodNotification() {
        return getPreferences().getBoolean(AppPreferences.INCOMING_PERIOD_NOTIFICATION_KEY, false);
    }

    public boolean sendFertileNotification() {
        return getPreferences().getBoolean(AppPreferences.FERTILE_DAYS_NOTIFICATION_KEY, false);
    }

    public boolean sendOvulationNotification() {
        return getPreferences().getBoolean(AppPreferences.OVULATION_NOTIFICATION_KEY, false);
    }

}
