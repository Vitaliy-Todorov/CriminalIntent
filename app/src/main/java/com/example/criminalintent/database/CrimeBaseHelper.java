/*В этом классе создаётся таблица*/

package com.example.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.criminalintent.database.CrimeDbSchema.CrimeTable;

public class CrimeBaseHelper extends SQLiteOpenHelper {
    /*код создания исходной базы данных размещается в onCreate(SQLiteDatabase), код обновления — в onUpgrade(SQLiteDatabase, int, int),*/

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + CrimeTable.NAME + "(" +                                    //имя таблици
                " _id integer primary key autoincrement, " +
                CrimeTable.Cols.UUID + ", " +
                CrimeTable.Cols.TITLE + ", " +
                CrimeTable.Cols.DATE + ", " +
                CrimeTable.Cols.SOLVED +
                ")");                                                                           //Пояснения по созданию этой строчки смотри ст 286-287. А вообще, это кода определения таблицы
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
