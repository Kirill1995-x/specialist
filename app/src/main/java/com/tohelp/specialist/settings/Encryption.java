package com.tohelp.specialist.settings;

import android.util.Base64;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encryption
{
    static private SecretKeySpec generateKey() throws Exception
    {
        final MessageDigest messageDigest=MessageDigest.getInstance(Variable.SHA);
        byte[] bytes=Variable.secretKey.getBytes(Variable.UTF);
        messageDigest.update(bytes, 0, bytes.length);
        byte[] key=messageDigest.digest();
        SecretKeySpec secretKeySpec=new SecretKeySpec(key, Variable.AES);
        return secretKeySpec;
    }

    static public String encrypt(String data) throws Exception
    {
        SecretKeySpec secretKeySpec=generateKey();
        Cipher cipher=Cipher.getInstance(Variable.AES);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encVal=cipher.doFinal(data.getBytes());
        String encryptedValue= Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }

    static public String decrypt(String encryptedValue) throws Exception
    {
        SecretKeySpec secretKeySpec=generateKey();
        Cipher cipher=Cipher.getInstance(Variable.AES);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decodedValue=Base64.decode(encryptedValue, Base64.DEFAULT);
        byte[] decValue= cipher.doFinal(decodedValue);
        String decryptedValue=new String(decValue);
        return decryptedValue;
    }
}
