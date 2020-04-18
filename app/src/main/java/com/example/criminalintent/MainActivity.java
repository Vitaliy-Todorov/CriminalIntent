//Помещает фрагмент CrimeFragment() в активность

package com.example.criminalintent;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.util.UUID;

public class MainActivity extends SingleFragmentActivity {

    private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";

    @Override
    protected Fragment createFragment(){                                                        //Создаётся фрагмент. Функция createFragment(); переопределяется в классе создающем фрагмент. Изначально определена в классе SingleFragmentActivity. На выходе выдаёт объект класса Fragment
        UUID crimeID = (UUID) getIntent().getSerializableExtra(MainActivity.EXTRA_CRIME_ID);          //Получаем UUID из Intent
        return CrimeFragment.newInstance(crimeID);
    }

    public static Intent newIntent(Context packageContext, UUID crimeId){                     //Это всё служет для передачи данных между активностями
        Intent intent = new Intent(packageContext, MainActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }
}
