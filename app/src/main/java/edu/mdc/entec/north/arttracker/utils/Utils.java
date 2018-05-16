package edu.mdc.entec.north.arttracker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Utils {
    public static Bitmap loadBitmapFromAssets(Context context, String path) {
        InputStream stream = null;
        try {
            //image was in the assets folder
            stream = context.getAssets().open(path);
            return BitmapFactory.decodeStream(stream);
        } catch (Exception ignored) {
            //image was downloaded and saved in the file system
            File dir = new File(context.getFilesDir(), "images");
            if(! dir.exists()){
                dir.mkdir();
            }
            File f = new File(dir, path.substring(path.indexOf("/")+1));
            try {
                FileInputStream foStream = new FileInputStream(f);
                return BitmapFactory.decodeStream(foStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

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
