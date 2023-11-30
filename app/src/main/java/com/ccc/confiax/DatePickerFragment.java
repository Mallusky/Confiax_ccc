package com.ccc.confiax;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import org.joda.time.LocalDate;

import java.util.Calendar;

/**
 * Fragment for DatePickerDialog handling.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private LocalDate initiallySelectedDate;

    public interface OnDateSetListener {
        void onDateSet(LocalDate selectedDate);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(initiallySelectedDate.toDate());
        DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        pickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        return pickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        OnDateSetListener activity = (OnDateSetListener) getActivity();
        Log.d(getClass().getSimpleName(), String.format("year %d month %d dayOfMonth %d", year, month, dayOfMonth));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        activity.onDateSet(new LocalDate(calendar.getTime()));
    }

    public void setInitiallySelectedDate(LocalDate initiallySelectedDate) {
        this.initiallySelectedDate = initiallySelectedDate;
    }


}
