package es.uv.jaimediazgonzalez.facilquedar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Familia Diaz on 30/06/2017.
 */

public class DateDialog  implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    EditText _editText;
    private int _day = 0;
    private int _month = 0;
    private int _birthYear = 0;
    private Context _context;

    public DateDialog(Context context, int editTextViewID)
    {
        Activity act = (Activity)context;
        this._editText = (EditText)act.findViewById(editTextViewID);
        this._editText.setOnClickListener(this);
        this._context = context;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        _birthYear = year;
        _month = monthOfYear;
        _day = dayOfMonth;
        updateDisplay();
    }

    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(_context, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        if(_birthYear == 0) {
            dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        }
        else{
            calendar.set(_birthYear, _month, _day);
            dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        }
        dialog.show();

    }

    public void updateDate(int year, int monthOfYear, int dayOfMonth){
        this._birthYear = year;
        this._month = monthOfYear;
        this._day = dayOfMonth;
    }

    // updates the date in the birth date EditText
    private void updateDisplay() {

        _editText.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(_day).append("/").append(_month + 1).append("/").append(_birthYear).append(" "));
    }

    public int get_day() {
        return _day;
    }

    public void set_day(int _day) {
        this._day = _day;
    }

    public int get_month() {
        return _month;
    }

    public void set_month(int _month) {
        this._month = _month;
    }

    public int get_birthYear() {
        return _birthYear;
    }

    public void set_birthYear(int _birthYear) {
        this._birthYear = _birthYear;
    }
}