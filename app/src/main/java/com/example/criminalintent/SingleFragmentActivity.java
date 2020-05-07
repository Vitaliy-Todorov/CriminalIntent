package com.example.criminalintent;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        //setContentView(R.layout.activity_fragment);
        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager();                                       //добовляем фрагмент (наверное контейнер фрагмента). Если вы добавляете в активность несколько фрагментов, то обычно для каждого фрагмента создается отдельный контейнер со своим идентификатором.
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);                       //не понимаю зачем это, если поставить null то приложение функцианирует точно так же.
        //ст 172 Сначала у FragmentManager запрашивается фрагмент с идентификатором контейнерного представления R.id.fragmentContainer. Если этот фрагмент уже находится в списке, FragmentManager возвращает его.
        //fragment равно null. В этом случае мы создаем новый экземпляр CrimeFragment и новую транзакцию, которая добавляет фрагмент в список.
        if (fragment == null){
            fragment = createFragment();                                                        //создаётся фрагмент. Функция createFragment(); переопределяется в классе создающем фрагмент. На выхлде выдаёт объект класса Fragment
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)                                     //ст 170 FragmentTransaction, возвращают FragmentTransaction вместо void, что позволяет объединять их вызовы в цепочку. Таким образом, выделенный код в приведенном выше листинге, означает: «Создать новую транзакцию фрагмента, включить в нее одну операцию add, а затем закрепить».
                    .commit();
        }
    }

    @LayoutRes                                                                                  //@LayoutRes - Обозначает, что целочисленное возвращаемое значение параметра, поля или метода, как ожидается, будет ссылкой на ресурс макета
    protected int getLayoutResId() {
        return R.layout.activity_twopane;
    }
}
