package com.example.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

//ст 260 Дочерняя активность будет запускаться вызовом startActivityForResult() из фрагмента родительской активности. При уничтожении дочерней активности родительская активность будет получать вызов onActivityResult(…), который будет перенаправляться фрагменту, запустившему дочернюю активность

public class DatePickerFragment extends DialogFragment {                                        //объекта AlertDialog обычно удобно упаковать его в экземпляр DialogFragment — субкласса Fragment

    private static final String TEG = "myLogs";
    public static final String EXTRA_DATE = "com.example.criminalintent.date";
    private static final String ARG_DATE = "date";

    private DatePicker mDatePicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);                                                 //задаём формат даты
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);                                    //задаём формат даты

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);    //LayoutInflater – это класс, который умеет из содержимого layout-файла создать View-элемент. Метод который это делает называется inflate

        mDatePicker = v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);                           //отправляет дату в диалоговое окно (субкласса Dialog)

        return new AlertDialog.Builder(getActivity())                                           //AlertDialog.Builder, предоставляющий динамичный интерфейс для конструирования экземпляров AlertDialog (Диалоговое окно (субкласса Dialog))
                .setView(v)
                .setTitle(R.string.date_picker_title)
                //.setPositiveButton(android.R.string.ok, null (обрабатываем нажатие))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int year = mDatePicker.getYear();                                       //задаём формат даты
                        int moth = mDatePicker.getMonth();
                        int dey = mDatePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year, moth, dey).getTime();           //задаём формат даты
                        sendResult(Activity.RESULT_OK, date);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, Date date) {                                        //передаёт данные целевому фрагменту
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);       //onActivityResult - передаёт данные целевому фрагменту, или центральной активносте. getTargetFragment() - сохраняет целевой фрагмент. getTargetRequestCode() - сохраняет код запроса (по моему это показывает от кого пришли данные). код запроса, соответствующий коду, переданному setTargetFragment(…), по которому целевой фрагмент узнает, кто возвращает результат;
    }

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();                                                             //Аналог Intent только если интент применяется для передачи данных между активнастями, то Bundle служет для передачи данных между фрагментами.
        args.putSerializable(ARG_DATE, date);                                                   //Добовляем crimeId в Bundle

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);                                                            //добавляем Bundle во фрагмент.
        return fragment;
    }
}
