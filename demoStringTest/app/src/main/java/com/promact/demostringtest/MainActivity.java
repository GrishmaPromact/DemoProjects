package com.promact.demostringtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
   /* private static final char[] chars = {'a', 'b', 'c','1','2','3', 'd', 'e', 'f'};
    static final char[] charsTwo = {'a', 'b', 'c', 'd','1','2', 'e', 'f'};
    public static final char[] charsThree = {'a', 'b', 'c','1', 'd', 'e', 'f'};*/

    static {
        System.loadLibrary("keys");
    }

    public native String getNativeKey1();
    public native String getNativeKey2();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* String[] mlFiles = getResources().getStringArray(R.array.ml_names);

        char[] chars = {'a', 'b', 'c', 'd', 'e', 'f'};
        String str = String.valueOf(chars);

        String strd = "abbcccddef";
        char charArray[] = strd.toCharArray();

        for (int i = 0; i < charArray.length; i++) {
            char name = charArray[i];
            Log.d("ML files name", "hfjsh::" + name);
        }

        Log.d("hi", str);
        for (int i = 0; i < mlFiles.length; i++) {
            String name = mlFiles[i];
            Log.d("ML files name", name);
        }
        String demo = getResources().getString(R.string.hello);
        Log.d("demo string name", demo);*/


        String key1 = new String(Base64.decode(getNativeKey1(),Base64.DEFAULT));
        String key2 = new String(Base64.decode(getNativeKey2(), Base64.DEFAULT));

        ((TextView)findViewById(R.id.key)).setText("Key1-->"+key1+"\nKey2-->"+key2);

        String text = "Hello World";
        String key = "Bar12345Bar12345"; // 128 bit key
        // Create key and cipher
        try {
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            System.err.println("Encrypted string is:::"+new String(encrypted));
            // decrypt the text
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            String decrypted = new String(cipher.doFinal(encrypted));
            System.err.println("DEcrypted string is::"+decrypted);
        }
        catch (Exception e){
            Log.e("Exception",e.getMessage());
        }

    }
    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

}
