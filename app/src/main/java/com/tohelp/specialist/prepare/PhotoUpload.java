package com.tohelp.specialist.prepare;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Base64;

import com.tohelp.specialist.settings.Variable;
import com.tohelp.specialist.settings.Encryption;

import java.io.ByteArrayOutputStream;

public class PhotoUpload
{
    private String id;
    private String access_token;
    private String old_name_of_photo;
    private Bitmap bitmap;
    private Context context;
    private SharedPreferences sharedPreferences;

    public PhotoUpload(Bitmap bitmap, Context context) {
        this.bitmap = bitmap;
        this.context = context;
        sharedPreferences = this.context.getSharedPreferences(Variable.APP_PREFERENCES, Context.MODE_PRIVATE);
        try {
            id = Encryption.decrypt(sharedPreferences.getString("shared_id",""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        old_name_of_photo = sharedPreferences.getString("shared_name_of_photo","");
        access_token = sharedPreferences.getString("shared_access_token","");
    }

    public String getId() {
        return id;
    }

    public String getAccessToken()
    {
        return access_token;
    }

    public String getOldNameOfPhoto() {
        return old_name_of_photo;
    }

    public Bitmap getResizeBitmap()
    {
        int width=this.bitmap.getWidth();
        int height=this.bitmap.getHeight();
        float scaleWidth=((float)225)/width;
        float scaleHeight=((float)225)/height;
        Matrix matrix=new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(this.bitmap, 0, 0, width, height, matrix, false);
    }

    public String imageToString()
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        getResizeBitmap().compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }
}
