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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
    private Button mReportButton;
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
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){              //заплняет макет (View)
        View v = inflater.inflate(R.layout.fragment_crime, container, false);           //LayoutInflater – это класс, который умеет из содержимого layout-файла создать View-элемент. Метод который это делает называется inflate. inflater.inflate(идентификатор ресурса макета, родитель представления, нужно ли включать заполненное представление в родителя) - явно заполняем представление фрагмента

        mTitleField = v.findViewById(R.id.crime_titleET);
        mDateButton = v.findViewById(R.id.crime_date);
        mBtnTime = v.findViewById(R.id.crime_time);
        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mReportButton = v.findViewById(R.id.crime_report);

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
        mReportButton.setOnClickListener(this);

        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(b);
            }
        });
        return v;
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
            case R.id.crime_report:
                Intent intent = new Intent(Intent.ACTION_SEND);                                 //ACTION_SEND - Выполняемое действие (action)
                intent.setType("text/plain");                                                   //Интент не содержит ссылок на данные и не имеет категорий, но определяет тип text/plain. Местонахождение данных — это может быть как ссылка на данные, находящиеся за пределами устройства (скажем, URL веб-страницы), так и URI файла или URI контента, ссылающийся на запись ContentProvider
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());                           //Тип данных, с которыми работает действие
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));        //Необязательные категории — если действие указывает, что нужно сделать, категория обычно описывает, где, когда или как вы пытаетесь использовать операцию.
                intent = Intent.createChooser(intent, getString(R.string.send_report));             //Говорит о том, что бы информация отображалась в всплывающем окне при использовании неявного интента.

                startActivity(intent);
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
                Intent intent = CrimeListActivity.newInstanceCLA(getActivity(), -1);
                startActivity(intent);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateDate() {
        mDateButton.setText(mDfDate.format(mCrime.getDate()));                                   //помещаеть текст на кнопку
    }

    private void updateTime() {
        mBtnTime.setText(mDfTime.format(mCrime.getDate()));
    }

    private String getCrimeReport() {
        String solvedString = null;
        if(mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        }else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        Locale locale = new Locale("ru");
        DateFormat dateFormat = new SimpleDateFormat("EEEE, MMM dd, yyyy, HH:mm", locale);
        String dateString = dateFormat.format( mCrime.getDate());

        String suspect = mCrime.getSuspect();
        if(suspect == null){
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), solvedString, dateString, suspect);

        return report;
    }

    public static CrimeFragment newInstanceCF(UUID crimeId){                                      //Передаёт crimeId во фрагмент, в данном случаи из MainActivity
        Bundle args = new Bundle();                                                             //Аналог Intent только если интент применяется для передачи данных между активнастями, то Bundle служет для передачи данных между фрагментами.
        args.putSerializable(ARG_CRIME_ID, crimeId);                                            //Добовляем crimeId в Bundle

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);                                                            //добавляем Bundle во фрагмент.
        return fragment;
    }
}
