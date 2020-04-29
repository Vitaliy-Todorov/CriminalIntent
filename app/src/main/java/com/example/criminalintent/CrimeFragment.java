//Запоняем фрагмент

package com.example.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static android.widget.CompoundButton.*;

public class CrimeFragment extends Fragment implements View.OnClickListener{

    private static final String TEG = "myLogs";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mBtnTime;
    private CheckBox mSolvedCheckBox;

    private DateFormat mDfDate = new SimpleDateFormat("EEEE, MMM dd, yyyy");
    private DateFormat mDfTime = new SimpleDateFormat("HH:mm");

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //UUID crimeID = (UUID) getActivity().getIntent().getSerializableExtra(MainActivity.EXTRA_CRIME_ID);          //Это всё служет для передачи данных между активностями, в отличик от реализованной передаче межуд фрагментами.
        UUID crimeID = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeID);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){              //заплняет макет (View)
        View v = inflater.inflate(R.layout.fragment_crime, container, false);           //LayoutInflater – это класс, который умеет из содержимого layout-файла создать View-элемент. Метод который это делает называется inflate. inflater.inflate(идентификатор ресурса макета, родитель представления, нужно ли включать заполненное представление в родителя) - явно заполняем представление фрагмента

        mTitleField = v.findViewById(R.id.crime_titleET);
        mDateButton = v.findViewById(R.id.crime_date);
        mBtnTime = v.findViewById(R.id.crime_time);
        mSolvedCheckBox = v.findViewById(R.id.crime_solved);

        updateDate();
        updateTime();

        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());                                       //charSequence.toString() - представляет ввод пользователя. mCrime.setTitle() - смотри метод Crime
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //mDateButton.setEnabled(false);                                                          //Блокировка кнопки

        mDateButton.setOnClickListener(this);
        mBtnTime.setOnClickListener(this);

        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(b);
            }
        });
        return v;
    }

    private void updateTime() {
        mBtnTime.setText(mDfTime.format(mCrime.getDate()));
    }

    @Override
    public void onClick(View view){
        FragmentManager manager = getFragmentManager();
        switch (view.getId()){
            case R.id.crime_date:
                DatePickerFragment dialogDate = DatePickerFragment.newInstance(mCrime.getDate());
                dialogDate.setTargetFragment(CrimeFragment.this, REQUEST_DATE);             //назначить CrimeFragment целевым фрагментом (target fragment) для DatePickerFragment.
                dialogDate.show(manager, DIALOG_DATE);                                              //добавлет экземпляра DialogFragment в FragmentManager и вывода его на экран (выводит всплывающее окно на экран)
                break;
            case R.id.crime_time:
                TimePickerFragment dialogTime = TimePickerFragment.newIntent(mCrime.getDate());
                dialogTime.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialogTime.show(manager, DIALOG_TIME);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if(requestCode == REQUEST_DATE) {                                                       //показывает от кого прошли данные
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);        //берёт данный пересланные сюда из фрагмента для которого является целевым
            mCrime.setDate(date);
            updateDate();
        }
        if(requestCode == REQUEST_TIME) {                                                       //показывает от кого прошли данные
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);        //берёт данный пересланные сюда из фрагмента для которого является целевым
            mCrime.setDate(date);
            updateTime();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(mCrime.getId());
                getActivity().finish();
                return true;
            default:
                super.onOptionsItemSelected(item);
                return true;
        }
    }

    private void updateDate() {
        mDateButton.setText(mDfDate.format(mCrime.getDate()));                                   //помещаеть текст на кнопку
    }

    public static CrimeFragment newInstance(UUID crimeId){                                      //Передаёт crimeId во фрагмент, в данном случаи из MainActivity
        Bundle args = new Bundle();                                                             //Аналог Intent только если интент применяется для передачи данных между активнастями, то Bundle служет для передачи данных между фрагментами.
        args.putSerializable(ARG_CRIME_ID, crimeId);                                            //Добовляем crimeId в Bundle

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);                                                            //добавляем Bundle во фрагмент.
        return fragment;
    }
}
