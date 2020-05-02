//Запоняем фрагмент

package com.example.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
    private static final int REQUEST_CONTACT = 2;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mBtnTime;
    private Button mReportButton;
    private Button mSuspectButton;
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

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        /*Действие задается константой Intent.ACTION_PICK, а местонахождение данных — ContactsContract.Contacts.CONTENT_URI.
        *ContactsContract - ксласс через который происходит взаимодействие с контактами
        *ContactsContract.Contacts - покласс класса ContactsContract служащий для удаления, создания, изменения и запроса отдельно взятого контакта*/

        mTitleField = v.findViewById(R.id.crime_titleET);
        mDateButton = v.findViewById(R.id.crime_date);
        mBtnTime = v.findViewById(R.id.crime_time);
        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mReportButton = v.findViewById(R.id.crime_report);
        mSuspectButton = v.findViewById(R.id.crime_suspect);

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
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
                /*startActivityForResult(Запускаемое действие, Код запроса) - Запустите действие, для которого вы хотите получить результат, когда оно закончится. Когда эта операция завершается, ваш метод
                onActivityResult () будет вызываться с заданным кодом запроса. REQUEST_CONTACT - ЗАПРОС_КОНТАКТА*/
            }
        });

        if(mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
        }

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
        /*onActivityResult(): Вызывается, когда запущенное вами действие завершается, давая вам код запроса, с которого вы его начали,
          возвращаемый код результата и любые дополнительные данные из него. Код результата будет в том RESULT_CANCELEDслучае, если действие явно
          вернуло это, не вернуло никакого результата или завершилось сбоем во время своей операции.

          Вы получите этот вызов непосредственно перед onResume (), когда ваша деятельность возобновится.

          Этот метод никогда не вызывается, если ваша активность установлена noHistoryна true.*/

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

        if(requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();                                                    //URI - это специальный идентификатор, по которому можно определить абстрактный или физический ресурс. То есть это адрес ресурса.
            String[] queryFields = new String[] {ContactsContract.Contacts.DISPLAY_NAME};       //DISPLAY_NAME - ОТОБРАЖАЕМОЕ ИМЯ. String[] - список типа String.

            Cursor cursor = getActivity().getContentResolver()
                    .query(contactUri,                                                          //contactUri - Content Uri специфичен для отдельных поставщиков контента. query() - Реализуйте это для обработки запросов от клиентов. Подробнее: https://developer.android.com/reference/android/content/ContentProvider#query(android.net.Uri,%20java.lang.String[],%20android.os.Bundle,%20android.os.CancellationSignal)
                            queryFields,                                                        //String [], описывающий, какие столбцы возвращать.
                            null,                                                       //предложение WHERE.
                            null,                                                   //замена значения предложения WHERE (где, куда)
                            null);                                                      //Порядок сортировки, пример: People.NAME + "ASC"
            /*getContentResolver: Поставщик является компонентом приложения Android, который зачастую
            имеет собственный пользовательский интерфейс для работы с данными. Однако поставщики
            контента предназначены в первую очередь для использования другими приложениями, которые
            получают доступ к поставщику посредством клиентского объекта поставщика.

            Поставщик контента предоставляет данные внешним приложениям в виде одной или нескольких
            таблиц, аналогичных таблицам в реляционной базе данных

            Дополнительная информация: https://developer.android.com/guide/topics/providers/content-provider-basics?hl=ru
            И http://www.ohandroid.com/getcontentresolver.html*/

            if(cursor.getCount() == 0) {                                                        //getCount() - возвращает количество строк в результирующем наборе данных
                return;                                                                         //он просто выходит из метода в этот момент. После return выполняется, остальная часть кода не будет выполнена.
            }

            try {
                cursor.moveToFirst();
                String suspect = cursor.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            }finally {
                cursor.close();
            }
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
