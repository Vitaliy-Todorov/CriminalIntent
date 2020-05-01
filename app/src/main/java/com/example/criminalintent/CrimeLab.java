//Класс модели

package com.example.criminalintent;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.criminalintent.database.CrimeBaseHelper;
import com.example.criminalintent.database.CrimeDbSchema;
import com.example.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CrimeLab {

    private static final String TEG = "myLogs";

    private static CrimeLab sCrimeLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

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
    }

    public void addCrime(Crime crime) {
        ContentValues values = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME, null, values);                        //insert(Имя таблици, XXX, Значение которое мы передаём в таблицу) - добавление записи в базу данных. XXX - (может принимать значение uuid) поволяет обойти ошибку при передачи пустого объекта, и записывает вместо него null. Подробнее стр 292
    }

    public void deleteCrime(UUID id) {
    }

    public static CrimeLab get(Context context){
        if (sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public List<Crime> getCrimes(){
        return new ArrayList<>();                                               //values() метод для перечисления возвращает массив, содержащий список констант перечисления.
    }

    public Crime getCrime(UUID id){
        return null;}

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();                                             //Класс ContentValues обеспечивает хранение пар «ключ-значение», однако в отличие от HashMap или Bundle, он предназначен для хранения типов данных, которые могут содержаться в базах данных SQLite
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        return values;
    }
}
