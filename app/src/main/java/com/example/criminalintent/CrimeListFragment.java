//Заполяем фрагмент

package com.example.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CrimeListFragment extends Fragment {

    private static final String TEG = "myLogs";
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private static final String CLICK_POSITION = "com.example.criminalintent.CrimeListFragment";

    private RecyclerView mCrimeRecyclerView;                                                    //В нём создаётся прокручиваемый список
    private CrimeAdapter mAdapter;
    private LinearLayout mNoCrimeLL;
    private Button mNewCrimeBtn;

    private static List<Crime> mCrimes = new ArrayList<>();

    private boolean mSubtitleVisible;
    private int mClickPosition = -1;
    private Callbacks mCallbacks;

    public  interface Callbacks {                                                               //Создаём интерфейс который будет передавать информацияю. В данном случаи говорит CrimeListActivity какое приступление было выбранно из списка
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);                                                                //активировать меню. А точнее говорит FragmentManager о том, что но должен получить onCreateOptionsMenu(…)
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){

        if(saveInstanceState != null){                                                         //Информация о нажатии в самом фрагменте
            mSubtitleVisible = saveInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
            mClickPosition = saveInstanceState.getInt("click position", 0);
        }

        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mNoCrimeLL = view.findViewById(R.id.no_crimeLL);
        mNewCrimeBtn = view.findViewById(R.id.new_crimeBtn);
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);

        mNewCrimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newInstanceCPA(getActivity(), crime.getId());
                startActivity(intent);
            }
        });

        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));            //виджету RecyclerView назначается другой объект LayoutManager. Объект LayoutManager управляет позиционированием элементов, а также определяет поведение прокрутки. при отсутствии LayoutManager виджет RecyclerView просто погибнет в тщетной попытке что-нибудь сделать.

        updateUI();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int fromPos = viewHolder.getAdapterPosition();
                CrimeLab.get(getActivity()).deleteCrime(mCrimes.get(fromPos).getId());
                updateUI();
                mCallbacks.onCrimeSelected(null);
            }
        }).attachToRecyclerView(mCrimeRecyclerView);

        return view;
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {         //Запоняет предствавление фрагмента. Holder - обладатель

        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent, int viewType){            //Явно заполняем представление фрагмента. После изменения getItemViewType, viewType передаёт адрес XML макета который надо возвращать
            super(inflater.inflate(viewType, parent, false));                       //inflater.inflate(идентификатор ресурса макета, родитель представления, нужно ли включать заполненное представление в родителя) - явно заполняем представление фрагмента

            itemView.setOnClickListener(this);                                                  //Эта строчка (место где происходит нажатие) и implements View.OnClickListener нужны для обработки нажатия на представление

            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date_list);
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);
        }

        @Override
        public void onClick(View view){
//          Toast.makeText(getActivity(), mCrime.getTitle() + " clicked!", Toast.LENGTH_SHORT).show();          //Активирует всплывающее окно Toast.makeText(куда помещаем, текст сообщения, время в течении которого будет отображение сообщения)
            mClickPosition = this.getAdapterPosition();
            //Intent intent = CrimePagerActivity.newInstanceCPA(getActivity(), mCrime.getId());              //Создаёт объект Intent для передачи информации в активность CrimePagerActivity, передаёт Id преступления.
            //startActivity(intent);                                                              //Отправляет запрос в ОС, и ОС создаёт новую активность.
            mCallbacks.onCrimeSelected(mCrime);                                                  //Передаёт в активность CrimeListActivity, какой crime нужно передать во фрагмент CrimeFragment
        }

        public void bind(Crime crime){                                                          //Добовляет view в представление
            mCrime = crime;

            DateFormat df = new SimpleDateFormat("EEEE, MMM dd, yyyy, HH:mm", Locale.ENGLISH);     //Меняем формат даты на Friday, Jul 22, 2016

            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(df.format(mCrime.getDate()));
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);        //VISIBLE - ВИДИМЫЙ. GONE - УШЕДШИЙ
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{

        public CrimeAdapter(List<Crime> crimes){                                                //Конструктор класса
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {        //Создаёт представления заполняющие экран
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());                 //getActivity() - Вернуть активность, с которой в данный момент связан этот фрагмент.

            return new CrimeHolder(layoutInflater, parent, viewType);                                     //Обращение к CrimeHolder для заполнения фрагмента
        }

        @Override
        public void onBindViewHolder( CrimeHolder holder, int position) {                       //Заполняет, перезаписывается ушедшее представление и появляется с новой записью с другой стороны. (Перезаписывает представления)
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();                                                              //size - используется для получения количества элементов в этом списке
        }

        @Override
        public int getItemViewType(int position){                                               //Переназначает номер представленя с position на то что выдачт return
            if(!mCrimes.get(position).isSolved()) {
                return R.layout.list_item_crime;
            } else {
                return R.layout.list_item_serious_crime;
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        if(getArguments() != null || (int)getArguments().getSerializable(CLICK_POSITION) > -2) {                                                            //Информация о нажатии вне данного фрагмента
            mClickPosition = (int) getArguments().getSerializable(CLICK_POSITION);
        }
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle savedIntendState){
        super.onSaveInstanceState(savedIntendState);

        savedIntendState.putInt("click position", mClickPosition);
        savedIntendState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {                         //В этом методе мы вызываем метод MenuInflater.inflate(int, Menu) и передаем идентификатор ресурса своего файла меню. Вызов заполняет экземпляр Menu командами, определенными в файле.
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {                                       //обрабатывает нажатие кнопки на панель инструментов
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                //Intent intent = CrimePagerActivity.newInstanceCPA(getActivity(), crime.getId());
                //startActivity(intent);
                updateUI();                                                                     //Эта и следуящая строчка вместо предыдущих, для случая с планшетным режимом
                mCallbacks.onCrimeSelected(crime);                                               //Передаёт в активность CrimeListActivity, какой crime нужно передать во фрагмент CrimeFragment
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();                                          //invalidateOptionsMenu (аннулировать меню параметров) - Объявите, что меню опций изменилось, поэтому его следует создать заново
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public  void onDetach() {
        /*Вызывается, когда фрагмент больше не привязан к своей активности. Это вызывается после
        onDestroy(), за исключением случаев, когда экземпляр фрагмента сохраняется при повторном
        создании действия (см. setRetainInstance(boolean)), И в этом случае он вызывается после onStop().*/
        super.onDetach();
        mCallbacks = null;                                                                      //По сути говорим Callbacks, что фрагмента больше нет, и что бы он его не показывал
    }

    private void updateSubtitle() {                                                             //запаолняет ActionBar
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        int crimeSize = crimeLab.getCrimes().size();
        //String subtitle = getString(R.string.subtitle_format, crimeCount);                    //Если нужно дабавить однозначную надпись
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeSize);       //Если есть несколько вариантов надписи (множественное единственное число)

        if(!mSubtitleVisible){                                                                  //после повтороного нажатия кнопки перестаёт отображать надпись.
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();                         //AppCompatActivity - Базовый класс для фктивности, использующих функции панели действий ст 216 (панель инструментов в библиотеки AppCompat называется «панелью действий»)
        activity.getSupportActionBar().setSubtitle(subtitle);                                   //(панель инструментов в библиотеки AppCompat называется «панелью действий») getSupportActionBar - выдаёт ActionBar. ActionBar - Основная панель инструментов в действий, которая может отображать заголовок действия, возможности навигации на уровне приложения и другие интерактивные элементы. setSubtitle - запаолняет ActionBar
    }

    public void updateUI(){                                                                    //Обновляет элементы CrimeListFragment
        CrimeLab crimeLab = CrimeLab.get(getActivity());                                        //Эта и следующая строчка служать для создания списка приступлений crimes
        mCrimes = crimeLab.getCrimes();

        if(mCrimes.size() != 0) {
            mNoCrimeLL.setVisibility(View.INVISIBLE);                                           //Скрывает представлеие
        }

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(mCrimes);                                                    //Здесь заполняется список RecyclerView, с помощью метода onCreateViewHolder. В данном случаи RecyclerView заполняется crimes. CrimeAdapter - класс наследник RecyclerView
            mCrimeRecyclerView.setAdapter(mAdapter);                                                //Добовляет адаптер. Адаптеры упрощают связывание данных с элементом управления. Помещаем mAdapter в mCrimeRecyclerView
        } else {
            if(mClickPosition >= 0){
                //mAdapter.setCrimes(crimes);                                                   //В моём случаи эта строчка не нужена, так как у меня mCrimes - static
                mAdapter.notifyItemChanged(mClickPosition);                                     //Обновляет конкретное представление в RecyclerView
            }else {
                //mAdapter.setCrimes(crimes);                                                   //В моём случаи эта строчка не нужена, так как у меня mCrimes - static
                mAdapter.notifyDataSetChanged();                                                    //notifyDataSetChanged приказать RecyclerView перезагрузить все элементы, видимые в настоящее время.. В данном случаи используется для того, что бы при возвращение с предыдущего окна данные обновились, в противном случаи они остануться те ми же, что до именения.
            }
        }

        updateSubtitle();
    }

    public static CrimeListFragment newInstanceCLF(CrimeListFragment fragment, int mClickPosition){                                      //Передаёт crimeId во фрагмент, в данном случаи из MainActivity
        Bundle args = new Bundle();                                                             //Аналог Intent только если интент применяется для передачи данных между активнастями, то Bundle служет для передачи данных между фрагментами.
        args.putSerializable(CLICK_POSITION, mClickPosition);                                            //Добовляем crimeId в Bundle

        fragment.setArguments(args);                                                            //добавляем Bundle во фрагмент.
        return fragment;
    }

    /*public void setCrimes(List<Crime> crimes) {
        mCrimes = crimes;                                                                       //В моём случаи этот метод не нужен, так как у меня mCrimes - static
    }*/
}
