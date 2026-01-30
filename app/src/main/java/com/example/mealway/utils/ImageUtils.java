package com.example.mealway.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageUtils {

    public static Bitmap decodeUriToBitmap(Context context, Uri imageUri) {
        try {
            InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
            return BitmapFactory.decodeStream(imageStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float ratio = (float) width / (float) height;
        if (ratio > 1) {
            width = maxSize;
            height = (int) (maxSize / ratio);
        } else {
            height = maxSize;
            width = (int) (maxSize * ratio);
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    public static String bitmapToBase64(Bitmap bitmap, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static String toDataUrl(String base64Image) {
        return "data:image/jpeg;base64," + base64Image;
    }
}
