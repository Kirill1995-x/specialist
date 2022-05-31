package com.tohelp.specialist.prepare;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

public class InformationAboutFile
{
    private Uri uri;
    private Context context;

    public InformationAboutFile(Context context, Uri uri)
    {
        this.context = context;
        this.uri = uri;
    }

    public Uri getUri()
    {
        return uri;
    }

    public String getTitle()
    {
        if (uri.toString().startsWith("content://")) {
            Cursor cursor = null;
            try
            {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst())
                {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index>=0) return cursor.getString(index);
                    else return "";
                }
                else return "";
            } finally { if (cursor != null) cursor.close();}
        }
        else {
            return new File(Objects.requireNonNull(uri.getPath())).getName();
        }
    }

    private long getSizeOfFile()
    {
        if(uri.toString().startsWith("content://"))
        {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst())
                {
                    int index = cursor.getColumnIndex(OpenableColumns.SIZE);
                    if(index>=0) return cursor.getLong(index);
                    else return 0;
                }
                else return 0;
            } finally { if (cursor != null) cursor.close();}
        }
        else
        {
            return  new File(Objects.requireNonNull(uri.getPath())).length();
        }
    }

    public boolean checkSizeOfFile()
    {
        return getSizeOfFile()/1024<1024;
    }

    public byte[] getFile()
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] array = new byte[(int) getSizeOfFile()];

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);

            if(inputStream!=null) {
                int nRead;
                while ((nRead = inputStream.read(array, 0, array.length)) != -1) {
                    buffer.write(array, 0, nRead);
                }
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer.toByteArray();
    }
}
