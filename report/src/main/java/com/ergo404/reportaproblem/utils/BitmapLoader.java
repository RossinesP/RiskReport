package com.ergo404.reportaproblem.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by pierrerossines on 29/04/2014.
 */
public class BitmapLoader {

    public static Bitmap generateThumb(String fileName, int sizeX, int sizeY) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/sizeX, photoH/sizeY);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(fileName, bmOptions);
    }
}
