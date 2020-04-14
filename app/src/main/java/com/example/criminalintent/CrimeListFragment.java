//Запоняем фрагмент

package com.example.criminalintent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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

    private class CrimeHolder extends RecyclerView.ViewHolder {                                             //Запоняет предствавление фрагмента.

        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_crime, parent, false));                   //inflater.inflate(идентификатор ресурса макета, родитель представления, нужно ли включать заполненное представление в родителя) - явно заполняем представление фрагмента

            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
        }

        public void bind(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
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

            return new CrimeHolder(layoutInflater, parent);                                     //Обращение к CrimeHolder для заполнения фрагмента
        }

        @Override
        public void onBindViewHolder( CrimeHolder holder, int position) {                       //Запоняет перезаписывается ушедшее представление и появляется с новой записью с другой стороны. (Перезаписывает представления)
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            Log.d(TEG, "getItemCount - "+mCrimes.size());
            return mCrimes.size();                                                              //size - используется для получения количества элементов в этом списке
        }
    }

    private void updateUI(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());                                        //Эта и следующая строчка служать для создания списка приступлений crimes
        List<Crime> crimes = crimeLab.getCrimes();
        mAdapter = new CrimeAdapter(crimes);                                                    //Здесь заполняется список RecyclerView, с помощью метода onCreateViewHolder. В данном случаи RecyclerView заполняется crimes. CrimeAdapter - класс наследник RecyclerView
        mCrimeRecyclerView.setAdapter(mAdapter);                                                //Добовляет адаптер. Адаптеры упрощают связывание данных с элементом управления. Помещаем mAdapter в mCrimeRecyclerView
        Log.d(TEG, "updateUI - " + crimes.size());
    }
}
