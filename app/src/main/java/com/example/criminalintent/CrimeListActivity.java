//Помещает фрагмент CrimeListFragment() в активность
//плюс здесь реализован адаптер (adapter) — объект контроллера, который находится между RecyclerView и набором данных с информацией, которую должен вывести RecyclerView.

package com.example.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;

import java.util.zip.Inflater;

public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    private static final String TEG = "myLogs";
    private static final String EXTRA_CLA_INTEGER = "com.example.criminalintent.CLF.int";

    private  CrimeListFragment mFragment = new CrimeListFragment();
    private Fragment newDetail;

    @Override
    protected Fragment createFragment(){                                                        //Создаётся фрагмент. Функция createFragment(); переопределяется в классе создающем фрагмент. Изначально определена в классе SingleFragmentActivity. На выхлде выдаёт объект класса Fragment
        return mFragment;
    }

    @LayoutRes                                                                                  //@LayoutRes - Обозначает, что целочисленное возвращаемое значение параметра, поля или метода, как ожидается, будет ссылкой на ресурс макета
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (crime == null) {
            Log.d(TEG, "onCrimeSelected - " + null);
            getSupportFragmentManager().beginTransaction()
                    .detach(newDetail)
                    .commit();
        }else if (findViewById(R.id.detail_fragment_container) == null) {                             //если используется планшетный интерфейс — поместить CrimeFragment в detail_fragment_container. Создаёт фрагмент с подробной информацией и выводит его на экран
            Intent intent = CrimePagerActivity.newInstanceCPA(this, crime.getId());
            startActivity(intent);
        } else {                                                                                //Переключение между фрагментами
            newDetail = CrimeFragment.newInstanceCF(crime.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {                                                   //При вызове этого метода в CrimeFragment, в CrimeListFragment вызывается метод updateUI(),и обновляет список приступлений. Но что он работал, его нужно поместить в CrimePagerActivity (это нужно, что бы и режиме телевона эта штука работала)
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        listFragment.updateUI();
    }

    @Override
    public void onResume(){
        super.onResume();

        CrimeListFragment.newInstanceCLF(mFragment, getIntent().getIntExtra(EXTRA_CLA_INTEGER, -2));
    }

    public static Intent newInstanceCLA(Context context, int mClickPosition){
        Intent intent = new Intent(context, CrimeListActivity.class);
        intent.putExtra(EXTRA_CLA_INTEGER, mClickPosition);

        return intent;
    }
}
