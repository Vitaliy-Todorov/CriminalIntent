//Класс модели

package com.example.criminalintent;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.criminalintent.database.CrimeBaseHelper;
import com.example.criminalintent.database.CrimeCursorWrapper;
import com.example.criminalintent.database.CrimeDbSchema;
import com.example.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.io.File;
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

    public static CrimeLab get(Context context){
        if (sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public void addCrime(Crime crime) {
        ContentValues values = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME, null, values);                        //insert(Имя таблици, XXX, Значение которое мы передаём в таблицу) - добавление записи в базу данных. XXX - (может принимать значение uuid) поволяет обойти ошибку при передачи пустого объекта, и записывает вместо него null. Подробнее стр 292
    }

    public void deleteCrime(UUID id) {
        mDatabase.delete(
                CrimeTable.NAME,
                CrimeTable.Cols.UUID + " = ?",
                new String[] { id.toString() });
    }

    public List<Crime> getCrimes(){
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);
            /*try – определяет блок кода, в котором может произойти исключение;
            catch – определяет блок кода, в котором происходит обработка исключения;
            finally – определяет блок кода, который является необязательным, но при
            его наличии выполняется в любом случае независимо от результатов выполнения блока try.*/
        try {
            cursor.moveToFirst();
                /*Вызов moveToFirst() выполняет две функции: позволяет проверить, вернул ли запрос пустой набор
                (путем проверки возвращаемого значения), и он перемещает курсор в первый результат (когда набор не пуст)*/
            while (!cursor.isAfterLast()) {                                                     //Метод isAfterLast() позволяет проверить, достигнут ли конец выборки
                crimes.add(cursor.getCrime());
                cursor.moveToNext();                                                            //moveToNext() — перемещает курсор на следующую строку
            }
        } finally {
            cursor.close();                                                                     //close() - закрывает курсор для освобождения памяти
        }
        return crimes;
    }

    public Crime getCrime(UUID id){
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );
        try {
            if (cursor.getCount() == 0) {                                                       //getCount() — возвращает количество строк в результирующем наборе данных
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + " = ?",
                new String[] { uuidString });                                                   //update(Имя таблици, Значение которое мы передаём в таблицу, условие WHERE (третий аргумент), значения аргументов в условии WHERE) - изменяет данные в таблице
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {

        /*Cursor - это интерфейс, который представляет собой двумерную таблицу любой базы данных.
        Когда вы пытаетесь получить некоторые данные с помощью инструкции SELECT, база данных сначала
        создаст объект Cursor и вернет ссылку на вас*/

        Cursor cursor = mDatabase.query(                                                        //Как я понял эта штука формирует запрос, подробнее можно посмотреть на:https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase?hl=ru#query(java.lang.String,%20java.lang.String%5B%5D,%20java.lang.String,%20java.lang.String%5B%5D,%20java.lang.String,%20java.lang.String,%20java.lang.String)
                CrimeTable.NAME,
                null,                                                                   //Число столбцов, если null - выбираются все столбцы
                whereClause,                                                                    //Фильтр, объявляющий, какие строки возвращать, отформатированный в виде предложения SQL WHERE (исключая сам WHERE). Передача null вернет все строки для данной таблицы.
                whereArgs,                                                                      //Вы можете включить? S в выделение, которые будут заменены значениями из selectionArgs, чтобы они появлялись в выделении. Значения будут связаны как строки.
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new CrimeCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();                                             //Класс ContentValues обеспечивает хранение пар «ключ-значение», однако в отличие от HashMap или Bundle, он предназначен для хранения типов данных, которые могут содержаться в базах данных SQLite
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());

        return values;
    }

    public File getPhotoFile(Crime crime) {
        File filesDir = mContext.getFilesDir();                                                //Возвращает абсолютный путь к каталогу в файловой системе, где openFileOutput(String, int)хранятся файлы, созданные с помощью .
        return  new File(filesDir, crime.getPhotoFilename());                                   //File(путь, имя) - не создаёт файла, а лишь создаёт под него место
    }
}
