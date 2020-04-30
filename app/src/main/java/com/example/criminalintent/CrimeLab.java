//Класс модели

package com.example.criminalintent;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.criminalintent.database.CrimeBaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CrimeLab {

    private static final String TEG = "myLogs";

    private static CrimeLab sCrimeLab;

    private Map<UUID, Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mDatabase;


    public void addCrime(Crime crime) {
        mCrimes.put(crime.getId(), crime);
    }

    public void deleteCrime(UUID id) {
        mCrimes.remove(id);
    }

    public static CrimeLab get(Context context){
        if (sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context){
        mContext = context.getApplicationContext();
                /*getApplicationContext() - через этот метод получается доступ к singleton-экземпляру
                (единственному на всё приложение). Этот контекст привязан к жизненному циклу
                приложения. Контекст приложения может использоваться там, где вам нужен контекст,
                жизненный цикл которого не связан с текущим контекстом или когда вам нужно передать
                контекст за пределы Activity.*/
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
                /*getWritableDatabase() класс CrimeBaseHelper выполняет следующее:

                1)открывает /data/data/com.bignerdranch.android.criminalintent/databases/crimeBase.db.
                Если файл базы данных не существует, то он создается;

                2)если база данных открывается впервые, вызывает метод onCreate(SQLiteDatabase) с
                последующим сохранением последнего номера версии;

                3)если база данных открывается не впервые, проверяет номер ее версии. Если версия
                базы данных в CrimeOpenHelper выше, то вызывается метод onUpgrade(SQLiteDatabase, int, int).*/
        mCrimes = new LinkedHashMap<>();
    }

    public List<Crime> getCrimes(){
        return new ArrayList<>(mCrimes.values());                                               //values() метод для перечисления возвращает массив, содержащий список констант перечисления.
    }

    public Crime getCrime(UUID id){
        return mCrimes.get(id);
    }
}
