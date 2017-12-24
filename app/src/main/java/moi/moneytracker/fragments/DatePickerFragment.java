package moi.moneytracker.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import moi.moneytracker.activities.AddTransactionActivity;

/**
 * Created by Ali on 29-Oct-17.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }


    // Do something with the date chosen by the user
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        Date date = new GregorianCalendar(year, month, day).getTime();
        AddTransactionActivity addTransaction = (AddTransactionActivity) getActivity();
        addTransaction.ChangeDateText( date);
        addTransaction.setFormattedDate(year,month,day);
    }
}
