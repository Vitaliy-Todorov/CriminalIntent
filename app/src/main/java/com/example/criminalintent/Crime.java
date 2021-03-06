package com.example.criminalintent;

import java.util.Date;
import java.util.UUID;

public class Crime {

    public static final int HOUR = 10;
    public static final int MINUTE = 12;

    private UUID mId;
    private String mTitle;
    private String mSuspect;
    private Date mDate;
    private boolean mRequiresPolice;
    private boolean mSolved;
    private String mNumber;

    public Crime(){
        this(UUID.randomUUID());
    }

    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public boolean getRequiresPolice() {
        return mRequiresPolice;
    }

    public void setRequiresPolice(boolean requiresPolice) {
        mRequiresPolice = requiresPolice;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    public String getPhotoFilename() {
        return  "IMG_" + getId().toString() + ".jpg";
    }
}
