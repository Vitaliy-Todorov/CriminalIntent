//Помещает фрагмент CrimeListFragment() в активность
//плюс здесь реализован адаптер (adapter) — объект контроллера, который находится между RecyclerView и набором данных с информацией, которую должен вывести RecyclerView.

package com.example.criminalintent;

import androidx.fragment.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){                                                        //Создаётся фрагмент. Функция createFragment(); переопределяется в классе создающем фрагмент. Изначально определена в классе SingleFragmentActivity. На выхлде выдаёт объект класса Fragment
        return new CrimeListFragment();
    }
}
