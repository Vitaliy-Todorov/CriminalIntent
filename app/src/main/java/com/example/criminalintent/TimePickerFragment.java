package com.example.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment {

    private static final String TEG = "myLogs";
    public static final String EXTRA_TIME = "com.example.criminalintent.time";
    private static final String ARG_TIME = "time";

    private TimePicker mTimePicker;

    public Dialog onCreateDialog(Bundle savedInstanceState){

        final Date date = (Date) getArguments().getSerializable(ARG_TIME);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        mTimePicker = view.findViewById(R.id.dialog_time_picker);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(calendar.get(Calendar.HOUR));
            mTimePicker.setMinute(calendar.get(Calendar.MINUTE));
        }else {
            mTimePicker.setCurrentHour(calendar.get(Calendar.HOUR));
            mTimePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        }

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {;
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            calendar.set(Calendar.HOUR, mTimePicker.getHour());
                            calendar.set(Calendar.MINUTE, mTimePicker.getMinute());
                        }else {
                            calendar.set(Calendar.HOUR, mTimePicker.getCurrentHour());
                            calendar.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());
                        }
                        setResult(Activity.RESULT_OK, calendar.getTime());
                    }
                })
                .create();
    }

    private void setResult(int resultCode, Date date) {
        if(getTargetFragment() == null){
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    public static TimePickerFragment newIntent(Date time){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, time);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
