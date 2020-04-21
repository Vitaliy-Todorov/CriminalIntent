package com.example.criminalintent;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Date;

public class DatePickerFragment extends DialogFragment {                                        //объекта AlertDialog обычно удобно упаковать его в экземпляр DialogFragment — субкласса Fragment

    private static final String ARG_DATE = "date";

    private DatePicker mDatePicker;

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();                                                             //Аналог Intent только если интент применяется для передачи данных между активнастями, то Bundle служет для передачи данных между фрагментами.
        args.putSerializable(ARG_DATE, date);                                                   //Добовляем crimeId в Bundle

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);                                                            //добавляем Bundle во фрагмент.
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);    //LayoutInflater – это класс, который умеет из содержимого layout-файла создать View-элемент. Метод который это делает называется inflate

        return new AlertDialog.Builder(getActivity())                                           //AlertDialog.Builder, предоставляющий динамичный интерфейс для конструирования экземпляров AlertDialog (Диалоговое окно (субкласса Dialog))
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
