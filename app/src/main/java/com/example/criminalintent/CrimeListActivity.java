//Помещает фрагмент CrimeListFragment() в активность
//плюс здесь реализован адаптер (adapter) — объект контроллера, который находится между RecyclerView и набором данных с информацией, которую должен вывести RecyclerView.

package com.example.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity {

    private static final String TEG = "myLogs";
    private static final String EXTRA_CLA_INTEGER = "com.example.criminalintent.CLF.int";

    private  CrimeListFragment mFragment = new CrimeListFragment();

    @Override
    protected Fragment createFragment(){                                                        //Создаётся фрагмент. Функция createFragment(); переопределяется в классе создающем фрагмент. Изначально определена в классе SingleFragmentActivity. На выхлде выдаёт объект класса Fragment
        return mFragment;
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
