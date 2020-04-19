//Заполяем фрагмент

package com.example.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CrimeListFragment extends Fragment {

    private static final String TEG = "myLogs";

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);

        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));            //виджету RecyclerView назначается другой объект LayoutManager. Объект LayoutManager управляет позиционированием элементов, а также определяет поведение прокрутки. при отсутствии LayoutManager виджет RecyclerView просто погибнет в тщетной попытке что-нибудь сделать.

        updateUI();

        return view;
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {         //Запоняет предствавление фрагмента. Holder - обладатель

        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent, int viewType){            //Явно заполняем представление фрагмента
            super(inflater.inflate(viewType, parent, false));                       //inflater.inflate(идентификатор ресурса макета, родитель представления, нужно ли включать заполненное представление в родителя) - явно заполняем представление фрагмента

            itemView.setOnClickListener(this);                                                  //Эта строчка (место где происходит нажатие) и implements View.OnClickListener нужны для обработки нажатия на представление

            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);
        }

        public void bind(Crime crime){                                                          //Добовляет view в представление
            mCrime = crime;

            DateFormat df = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.ENGLISH);     //Меняем формат даты на Friday, Jul 22, 2016

            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(df.format(mCrime.getDate()));
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);        //VISIBLE - ВИДИМЫЙ. GONE - УШЕДШИЙ
        }

        @Override
        public void onClick(View view){
//          Toast.makeText(getActivity(), mCrime.getTitle() + " clicked!", Toast.LENGTH_SHORT).show();          //Активирует всплывающее окно Toast.makeText(куда помещаем, текст сообщения, время в течении которого будет отображение сообщения)
            Intent intent = MainActivity.newIntent(getActivity(), mCrime.getId());              //Создаёт объект Intent для передачи информации в активность MainActivity, передаёт Id преступления.
            startActivity(intent);                                                              //Отправляет запрос в ОС, и ОС создаёт новую активность.
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{

        private List<Crime> mCrimes;

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
            if(mCrimes.get(position).getRequiresPolice()) {
                return R.layout.list_item_crime;
            } else {
                return R.layout.list_item_serious_crime;
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    private void updateUI(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());                                        //Эта и следующая строчка служать для создания списка приступлений crimes
        List<Crime> crimes = crimeLab.getCrimes();
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);                                                    //Здесь заполняется список RecyclerView, с помощью метода onCreateViewHolder. В данном случаи RecyclerView заполняется crimes. CrimeAdapter - класс наследник RecyclerView
            mCrimeRecyclerView.setAdapter(mAdapter);                                                //Добовляет адаптер. Адаптеры упрощают связывание данных с элементом управления. Помещаем mAdapter в mCrimeRecyclerView
        } else {
            mAdapter.notifyDataSetChanged();                                                    //notifyDataSetChanged приказать RecyclerView перезагрузить все элементы, видимые в настоящее время.. В данном случаи используется для того, что бы при возвращение с предыдущего окна данные обновились, в противном случаи они остануться те ми же, что до именения.
        }
    }
}
