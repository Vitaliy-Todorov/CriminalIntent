//Запоняем фрагмент

package com.example.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
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
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.widget.CompoundButton.*;

public class CrimeFragment extends Fragment implements View.OnClickListener{

    private static final String TEG = "myLogs";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_BIG_PICTURE = "DialogBigPicture";
    private static final int REQUEST_LOCATION_PERMISSIONS = 4;
    private static final String[] LOCATION_PERMISSIONS = new String[] {Manifest.permission.READ_CONTACTS};
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_NAME = 2;
    private static final int REQUEST_NUMBER = 3;
    private static final int REQUEST_PHOTO= 4;

    private Crime mCrime;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mBtnTime;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallCriminal;
    private CheckBox mSolvedCheckBox;
    private ImageView mPhotoView;
    private ImageButton mPhotoButton;

    private DateFormat mDfDate = new SimpleDateFormat("EEEE, MMM dd, yyyy");
    private DateFormat mDfTime = new SimpleDateFormat("HH:mm");
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //UUID crimeID = (UUID) getActivity().getIntent().getSerializableExtra(MainActivity.EXTRA_CRIME_ID);          //Это всё служет для передачи данных между активностями, в отличик от реализованной передаче межуд фрагментами.
        UUID crimeID = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeID);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);                          //Cохранение местонахождения файла фотографии
        setHasOptionsMenu(true);
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
        mCallCriminal = v.findViewById(R.id.call_criminal);
        mPhotoButton = v.findViewById(R.id.crime_camera);
        mPhotoView = v.findViewById(R.id.crime_photo);
        updatePhotoView();

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
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //mDateButton.setEnabled(false);                                                          //Блокировка кнопки

        mDateButton.setOnClickListener(this);
        mBtnTime.setOnClickListener(this);
        mReportButton.setOnClickListener(this);
        mPhotoButton.setOnClickListener(this);
        mPhotoView.setOnClickListener(this);

//Дальше идёт обработака отправки смс и совершения звонка
        //В общем, по идеи дальнейший код проверяет, нашёл ли Intent (в данном случаи pickContact) к какому приложению обратится, если нет, то он блокирует кнопку.
        //pickContact.addCategory(Intent.CATEGORY_HOME);                                          //Так и не понял толком, как эта строчка работает. Вообще она сделана для проверке следующего за ней кода, что бы в if было true.
        /*addCategory - Добавить новую категорию к цели. Категории предоставляют дополнительную информацию о действии, которое выполняет намерение. При разрешении намерения будут использоваться только те действия, которые обеспечивают все запрошенные категории.*/
        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);                                                   //setEnabled - Выключает кнопку.
        }
        /*PackageManager - класс для получения различных видов информации, связанной с пакетами приложений, которые в настоящее время установлены на устройстве.
        Вы можете найти этот класс через Context.getPackageManager.

        Вызывая resolveActivity(Intent, int), вы приказываете найти активность, соответствующую переданному интенту.

        PackageManager.MATCH_DEFAULT_ONLY - Если вы укажете, MATCH_DEFAULT_ONLY вызов вернет
        ResolveInfo объект для всех действий, которые соответствуют предоставленной Intent. При
        выполнении согласования, Android будет рассматривать только мероприятия , которые имеют
        CATEGORY=DEFAULTв своем <intent-filter>определении в манифесте. Это то же самое поведение
        соответствия, используемый при вызове startActivity()на Intent. Если вы не укажете этот флаг,
        запрос вернет все совпадающие действия, даже те, которые не содержат CATEGORY=DEFAUL Tих
        <intent-filter>. Конечно, если то, Intent что вы передаете, queryIntentActivities()уже
        содержит CATEGORY=DEFAULT, то флаг не нужен.*/

        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_NAME);
                /*startActivityForResult(Запускаемое действие, Код запроса) - Запустите действие, для которого вы хотите получить результат, когда оно закончится. Когда эта операция завершается, ваш метод
                onActivityResult () будет вызываться с заданным кодом запроса. REQUEST_NAME - ЗАПРОС_КОНТАКТА*/
            }
        });

        //Второй способ заблокировать кнопку:
        boolean canCall = packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) != null;
        mCallCriminal.setEnabled(canCall);
        mCallCriminal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_NUMBER);
            }
        });
//Здесь заканчивается обработака отправки смс и совершения звонка (большая часть кода находится в onActivityResult)

        if(mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
        }

        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(b);
                updateCrime();
            }
        });

        return v;
    }

    @Override
    public void onClick(View view){
        PackageManager packageManager = getActivity().getPackageManager();
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
                /*Intent intent = new Intent(Intent.ACTION_SEND);                                 //ACTION_SEND - Выполняемое действие (action)
                intent.setType("text/plain");                                                   //Интент не содержит ссылок на данные и не имеет категорий, но определяет тип text/plain. Местонахождение данных — это может быть как ссылка на данные, находящиеся за пределами устройства (скажем, URL веб-страницы), так и URI файла или URI контента, ссылающийся на запись ContentProvider
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());                           //Тип данных, с которыми работает действие
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));        //Необязательные категории — если действие указывает, что нужно сделать, категория обычно описывает, где, когда или как вы пытаетесь использовать операцию.
                intent = Intent.createChooser(intent, getString(R.string.send_report));             //Говорит о том, что бы информация отображалась в всплывающем окне при использовании неявного интента.
                    Следующий код делает то же самое, является аналогом предыдущего*/
                Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .createChooserIntent();

                startActivity(intent);
                break;
            case R.id.crime_camera:
                Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);        //MediaStore - через этот класс проиходит работа с аудиовизуальными интерфейсами (короче к нему нужно обращаться по поводу аудиовизуальных штук). ACTION_CAPTURE_IMAGE - по умолчанию запускает приложение камеры и делает снимокно результат не является фотографией в полном разрешении.
                if(mPhotoFile == null && captureImageIntent.resolveActivity(packageManager) == null) { break; }         //Если нет нужного приложения ничего не делать

                Uri uri = FileProvider
                        .getUriForFile(getActivity(), "com.bignerdranch.android.criminalintent.fileprovider", mPhotoFile);
                                //getUriForFile - преобразует локальный путь к файлу в объект Uri, «понятный» приложению камеры. com.bignerdranch.android.criminalintent.fileprovider - путь укаханный в фйле Manifests, в месте активации класса FileProvider
                captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        //Чтобы получить выходное изображение в высоком разрешении, необходимо сообщить, где должно храниться изображение в файловой системе. Эта задача решается передачей URI для места, в котором должен сохраняться файл, в MediaStore.EXTRA_OUTPUT. URI будет указывать на место, предоставленное FileProvider.

                //Дальнейший код написан для получения доступа
                List<ResolveInfo> cameraActivities = getActivity().getPackageManager()
                        .queryIntentActivities(captureImageIntent, PackageManager.MATCH_DEFAULT_ONLY);
                                //queryIntentActivities - Получить все действия, которые могут быть выполнены для данного намерения

                for(ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            //grantUriPermission - Предоставьте разрешение на доступ к определенному Uri другому пакету
                                    //FLAG_GRANT_WRITE_URI_PERMISSION - Флаг предоставляет разрешение на запись для этого конкретного Uri. Включение атрибута android:grantUriPermissions в объявление провайдера было необходимо для того, чтобы разблокировать эту функциональность.
                }
                //Доступ получен

                startActivityForResult(captureImageIntent, REQUEST_PHOTO);
                break;
            case R.id.crime_photo:
                BigPicturePickerFragment bigPicture =  BigPicturePickerFragment.newInstance(mPhotoFile);
                bigPicture.show(manager, DIALOG_BIG_PICTURE);
                break;
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
            updateCrime();
            updateDate();
        }
        if(requestCode == REQUEST_TIME) {                                                       //показывает от кого прошли данные
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);        //берёт данный пересланные сюда из фрагмента для которого является целевым
            mCrime.setDate(date);
            updateCrime();
            updateTime();
        }

        if(requestCode == REQUEST_NAME && data != null) {
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

        if (requestCode == REQUEST_NUMBER && data != null) {
            if(!hasLocationPermission()){
                requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);         //Если права READ_CONTACTS не получины, запрашивает у пользователя разрешение на их получение, так же, нужно указать в файле Manifest, строчку <uses-permission android:name="android.permission.READ_CONTACTS"/>
            }

            //от сюда и до следующего коментария, идёт запрос номера телефона
            Uri contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String[] queryFields = new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

            if(cursor.getCount() == 0) {
                return;
            }

            try {
                cursor.moveToFirst();
                String number_ = cursor.getString(0);
                mCrime.setNumber(number_);

                //Вызов по выбранному номеру телефону
                Uri number = Uri.parse("tel:" + number_);

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(number);

                updateCrime();
                startActivity(intent);
                //Сдесь вызов по номеру заканчивается
            }finally {
                cursor.close();
            }
        }

        if (requestCode == REQUEST_PHOTO) {                                                     //обновляет изображение на mPhotoViewпосле возвращения с камеры
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.bignerdranch.android.criminalintent.fileprovider",
                    mPhotoFile);

            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);             //revokeUriPermission - отозвать разрешение и снова перекрыть доступ к файлу (как бы закрывает разрешение полученное с помощью grantUriPermissio)

            updateCrime();
            updatePhotoView();
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

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private String getCrimeReport() {                                                           //Формирует текст отчёта по преступлению для  смс сообщения
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

    private void updateCrime() {                                                                //Обновляет список преступлений в планшетном режиме
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    private void updateDate() {
        mDateButton.setText(mDfDate.format(mCrime.getDate()));                                   //помещаеть текст на кнопку
    }

    private void updateTime() {
        mBtnTime.setText(mDfTime.format(mCrime.getDate()));
    }

    private void updatePhotoView() {                                                            //Вславляет фото с камеры в mPhotoView
        if(mPhotoFile == null || !mPhotoFile.exists()){                                         //exists() - Проверяет, существует ли файл или каталог, обозначенный этим абстрактным путем.
            mPhotoView.setImageDrawable(null);                                                  //setImageDrawable(Drawable drawable) - Устанавливает рисование как содержимое этого ImageView.
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private boolean hasLocationPermission(){                                                    //Проверяет наличие прав доступа READ_CONTACTS, так же, нужно указать в файле Manifest, строчку <uses-permission android:name="android.permission.READ_CONTACTS"/>
        int result = ContextCompat
                .checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static CrimeFragment newInstanceCF(UUID crimeId){                                      //Передаёт crimeId во фрагмент, в данном случаи из MainActivity
        Bundle args = new Bundle();                                                             //Аналог Intent только если интент применяется для передачи данных между активнастями, то Bundle служет для передачи данных между фрагментами.
        args.putSerializable(ARG_CRIME_ID, crimeId);                                            //Добовляем crimeId в Bundle

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);                                                            //добавляем Bundle во фрагмент.
        return fragment;
    }
}
