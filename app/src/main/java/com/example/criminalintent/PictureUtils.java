package com.example.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class PictureUtils {

    public static Bitmap getScaledBitmap(String path, Activity activity) {                      //Метод проверяет размер экрана и уменьшает изображение до этого размера.
        Point size = new Point();                                                               //Объект содержащий две целочисленные координаты
        activity.getWindowManager().getDefaultDisplay().getSize(size);
                /*getWindowManager() - Получить менеджер окон (объект типа WindowManager) для отображения пользовательских окон.
                *getDefaultDisplay() - Возвращает объект, Displayна котором этот WindowManagerэкземпляр будет создавать новые окна.
                *Display - Предоставляет информацию о размере и плотности логического дисплея.
                *getSize() - Получает размер дисплея в пикселях.*/
        return getScaledBitmap(path, size.x, size.y);
    }

    public static Bitmap getScaledBitmap(String path,int destWidth, int destHeight) {           //Масштабирует изображение в соответствии с параметрами destWidth destHeight, то есть подгоняет изображение под эти размеры.
        BitmapFactory.Options options = new BitmapFactory.Options();                            //BitmapFactory.Options() - Создайте объект параметров по умолчанию, который, если оставить его без изменений, даст тот же результат от декодера, как если бы было передано значение null.
        options.inJustDecodeBounds = true;                                                      //inJustDecodeBounds - (смотри описание outWidth) значение true позволит запрашивать растровое изображение (даже если оно null) без необходимости выделять память для его пикселей.
        BitmapFactory.decodeFile(path, options);                                                //decodeFile - Декодировать путь к файлу в растровое изображение. Если указанное имя файла является нулевым или не может быть декодировано в растровое изображение, функция возвращает нулевое значение.

        float srcWidth = options.outWidth;
                /*float - 32-разрядное число в формате IEEE 754 с плавающей точкой

                outWidth - Результирующая ширина растрового изображения. Если inJustDecodeBoundsустановлено значение
                false, это будет ширина выходного растрового изображения после применения любого масштабирования. Если
                true, это будет ширина входного изображения без учета масштабирования.
                outWidth будет установлен в -1, если при попытке декодирования возникла ошибка.*/
        float srcHeight = options.outHeight;                                                    //outHeight - Результирующая высота растрового изображения (доп информацию смотри описание outWidth)

        int inSampleSize = 1;
        if(srcHeight > destHeight || srcWidth > destWidth) {
            float heightScale = srcHeight / destHeight;
            float widthScale = srcWidth / destWidth;
            inSampleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);     //округляет float до int
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;                                                    //inSampleSize - сжимает изображение (точнее масштабирует) (как я понял)

        return BitmapFactory.decodeFile(path, options);
    }
}
