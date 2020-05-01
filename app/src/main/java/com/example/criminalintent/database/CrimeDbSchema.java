/*В этом классе хранится информация о таблице, её название, название её сттолбцов
Файл CrimeDbSchema.java помещен в отдельный пакет database, который будет использоваться для организации всего кода, относящегося к базам данных.*/

package com.example.criminalintent.database;

public class CrimeDbSchema {

    public static final class CrimeTable{

        public static final String NAME = "crimes";

        public static final class Cols{

            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
        }
    }
}
