package com.neighbours.neighbours.Util;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Joker on 9/21/17.
 */

public class WriteToFileUtil {

    public static File writeToFile(Context context, Bitmap bitmap, String fileName) throws IOException {
        //create a file to write bitmap data
        File f = new File(context.getCacheDir(), fileName + ".jpg");
        f.createNewFile();

        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();

        return f;
    }
}
