package com.promact.fileencryption;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    private static String keyString = "Bar12345Bar12345";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = (TextView) findViewById(R.id.sampleTv);
        Button encrypt = (Button) findViewById(R.id.encryptbtn);
        Button decrypt = (Button) findViewById(R.id.decryptbtn);



        encrypt.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           try {
                                               encrypt();
                                           } catch (InvalidKeyException e) {
                                               // TODO Auto-generated catch block
                                               e.printStackTrace();
                                           } catch (NoSuchAlgorithmException e) {
                                               // TODO Auto-generated catch block
                                               e.printStackTrace();
                                           } catch (NoSuchPaddingException e) {
                                               // TODO Auto-generated catch block
                                               e.printStackTrace();
                                           } catch (IOException e) {
                                               // TODO Auto-generated catch block
                                               e.printStackTrace();
                                           }
                                       }
                                   }
        );

        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    decrypt();
                } catch (InvalidKeyException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        /*if(file.exists())   // check if file exist
        {
            //Read text from file
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('n');
                }
            }
            catch (IOException e) {
                //You'll need to add proper error handling here
            }
            //Set the text
            tv.setText(text);
        }
        else
        {
            tv.setText("Sorry file doesn't exist!!");
        }*/

    }



    void encrypt() throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        // Here you read the cleartext.
       // File extStore = Environment.getExternalStorageDirectory();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "pdf-test.pdf");
        Log.d("file path:::", file.getAbsolutePath());
        Log.d("file name:::", file.getName());

        FileInputStream fis = new FileInputStream(file);
        // This stream write the encrypted text. This stream will be wrapped by
        // another stream.
        File newFile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "pdf-encrypted.pdf");
        FileOutputStream fos = new FileOutputStream(newFile);

        // Length is 16 byte
        SecretKeySpec sks = new SecretKeySpec(keyString.getBytes(),"AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        // Write bytes
        int b;
        byte[] d = new byte[8];
        while ((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        // Flush and close streams.
        cos.flush();
        cos.close();
        fis.close();
    }

    void decrypt() throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {

        //File extStore = Environment.getExternalStorageDirectory();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"pdf-encrypted.pdf");
        Log.d("file path:::", file.getAbsolutePath());
        Log.d("file name:::", file.getName());
        FileInputStream fis = new FileInputStream(  file);
        File newFile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "pdf-decrypted.pdf");

        FileOutputStream fos = new FileOutputStream( newFile );
        SecretKeySpec sks = new SecretKeySpec(keyString.getBytes(),
                "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int b;
        byte[] d = new byte[8];
        while ((b = cis.read(d)) != -1) {
            fos.write(d, 0, b);
        }
        fos.flush();
        fos.close();
        cis.close();
    }






}
