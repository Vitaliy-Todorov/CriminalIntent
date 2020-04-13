//Помещает фрагмент CrimeFragment() в активность

package com.example.criminalintent;

import androidx.fragment.app.Fragment;

public class MainActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){                                                        //Создаётся фрагмент. Функция createFragment(); переопределяется в классе создающем фрагмент. Изначально определена в классе SingleFragmentActivity. На выхлде выдаёт объект класса Fragment
        return new CrimeFragment();
    }
}
