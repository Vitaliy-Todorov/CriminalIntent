//Класс модели

package com.example.criminalintent;


import android.content.Context;
import android.util.Log;

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

    public void addCrime(Crime crime) {
        mCrimes.put(crime.getId(), crime);
    }

    public static CrimeLab get(Context context){
        if (sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context){
        mCrimes = new LinkedHashMap<>();
    }

    public List<Crime> getCrimes(){
        return new ArrayList<>(mCrimes.values());                                               //values() метод для перечисления возвращает массив, содержащий список констант перечисления.
    }

    public Crime getCrime(UUID id){
        return mCrimes.get(id);
    }
}
