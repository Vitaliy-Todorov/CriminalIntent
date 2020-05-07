package com.example.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

import java.io.File;

public class BigPicturePickerFragment extends DialogFragment {

    private static final String ARG_PHOTO_FILE = "photoFile";

    private ImageView mPhotoView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_big_picture, null);

        File mPhotoFile = (File) getArguments().getSerializable(ARG_PHOTO_FILE);

        mPhotoView = view.findViewById(R.id.dialog_big_picture);

        if (mPhotoFile == null || !mPhotoFile.exists()) {                                         //exists() - Проверяет, существует ли файл или каталог, обозначенный этим абстрактным путем.
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    public static BigPicturePickerFragment newInstance(File mPhotoFile){
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO_FILE, mPhotoFile);

        BigPicturePickerFragment fragment = new BigPicturePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
