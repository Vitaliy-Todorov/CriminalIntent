//Помещает фрагмент CrimeListFragment() в активность
//плюс здесь реализован адаптер (adapter) — объект контроллера, который находится между RecyclerView и набором данных с информацией, которую должен вывести RecyclerView.

package com.example.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks {

    private static final String TEG = "myLogs";
    private static final String EXTRA_CLA_INTEGER = "com.example.criminalintent.CLF.int";

    private  CrimeListFragment mFragment = new CrimeListFragment();

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
