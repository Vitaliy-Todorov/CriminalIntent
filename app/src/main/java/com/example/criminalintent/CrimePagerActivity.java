package com.example.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View.OnClickListener;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity implements OnClickListener {

    private static final String TEG = "myLogs";
    private static final String EXTRA_CRIME_ID = "com.example.criminalintent.crime_id";

    private Button mBtnNext;
    private Button mBtnPrev;
    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = findViewById(R.id.crime_view_pager);                                       //поиск ViewPager в представлении активности
        mBtnNext = findViewById(R.id.next_button);
        mBtnPrev = findViewById(R.id.prev_button);

        //Листаем страници
        mCrimes = CrimeLab.get(this).getCrimes();                                               //мы получаем от CrimeLab набор данных
        FragmentManager fragmentManager = getSupportFragmentManager();                          //получаем экземпляр FragmentManager для активности. Для управления фрагментами
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager,                    //FragmentStatePagerAdapter — ваш агент, управляющий взаимодействием с ViewPager. Что именно делает агент? Вкратце, он добавляет возвращаемые фрагменты в активность и помогает ViewPager идентифицировать представления фрагментов для их правильного размещения.
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {              //BEHAVIOR_SET_USER_VISIBLE_HINT - Указывает, что Fragment.setUserVisibleHint(boolean)будет вызван при изменении текущего фрагмента.
            @NonNull
            @Override
            public Fragment getItem(int position) {                                             //getItem(int) - возвращает фрагмент. Получает наборе данных, после чего использует их для создания и возвращет правильно настроенной экземпляр CrimeFragment.
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {                                                             //getCount() возвращает текущее количество элементов в списке
                return mCrimes.size();
            }
        });
        //Листаем страници

        mBtnNext.setOnClickListener(this);
        mBtnPrev.setOnClickListener(this);

        for (int i = 0; i < mCrimes.size(); i++){
            if (mCrimes.get(i).getId().equals(crimeId)){
                mViewPager.setCurrentItem(i);                                                   //setCurrentItem - измените текущий элемент (отображаемый в ViewPager) по индексу найденного объекта
                break;
            }
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.next_button:
                mViewPager.setCurrentItem(mCrimes.size());
                break;
            case R.id.prev_button:
                mViewPager.setCurrentItem(0);
                break;
        }
    }

    public static Intent newIntent(Context packageContext, UUID crimeId){
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }
}
