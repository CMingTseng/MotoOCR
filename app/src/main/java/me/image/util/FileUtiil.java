package me.image.util;

import android.graphics.Bitmap;

public class FileUtiil {

    public static Bitmap doubleArrayToBitmap(int[][] pixelss, int width, int height) {
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y * width + x] = pixelss[x][y];

            }
        }

        Bitmap bmp = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);

        return bmp;

    }

    public static Bitmap singleArrayToBitmap(int[] pixels, int width, int height) {
        Bitmap bmp = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);

        return bmp;
    }
}
