/*Этот класса позволяет дописывать методы для работы с cursor.
* В частности здесь реализование метод для считывания информации из таблици*/

package com.example.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.criminalintent.Crime;

import java.util.Date;
import java.util.UUID;

import static com.example.criminalintent.database.CrimeDbSchema.*;

public class CrimeCursorWrapper extends CursorWrapper {
//    CursorWrapper — этот класс позволяет дополнить класс Cursor, полученный извне, новыми методами.

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        /*Данный метод, создан, что бы не писать в коде следующее:
        String uuidString = cursor.getString(
            cursor.getColumnIndex(CrimeTable.Cols.UUID));
        String title = cursor.getString(
            cursor.getColumnIndex(CrimeTable.Cols.TITLE));
        long date = cursor.getLong(
            cursor.getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = cursor.getInt(
            cursor.getColumnIndex(CrimeTable.Cols.SOLVED));

        А просто вызывать метод getCrime(), этот метод вызывается для cursor переданного в объект

        Вы можете использовать либо getString(int) чтобы получить строку. Метод getString удобно использовать, когда
        в строку из ресурсов нужно внести дополнительные данные Пример: String header = getString(R.id.headerSection)
        https://habr.com/ru/post/133503/

         С помощью метода getColumnIndex() с указанием имени колонки мы можем извлечь данные, которые хранятся в них.*/

        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));                    //Этот метод вызывается для cursor переданного в объект
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));


        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);

        return crime;
    }
}
