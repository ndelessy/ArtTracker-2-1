package edu.mdc.entec.north.arttracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import java.io.InputStream;

public class Utils {
    public static Bitmap loadBitmapFromAssets(Context context, String path) {
        InputStream stream = null;
        try {
            stream = context.getAssets().open(path);
            return BitmapFactory.decodeStream(stream);
        } catch (Exception ignored) {

        } finally {
            try {
                if(stream != null) {
                    stream.close();
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    public static Drawable loadDrawableFromAssets(Context context, String path) {
        InputStream stream = null;
        try {
            stream = context.getAssets().open(path);
            return Drawable.createFromStream(stream, null);
        } catch (Exception ignored) {

        } finally {
            try  {
                if(stream != null) {
                    stream.close();
                }
            } catch (Exception ignored) {

            }
        }
        return null;
    }
}
