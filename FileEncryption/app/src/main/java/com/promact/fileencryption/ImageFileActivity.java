package com.promact.fileencryption;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by grishma on 22-03-2018.
 */


public class ImageFileActivity extends AppCompatActivity {
     File outputDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/weights/");
    //private static String keyString = "Bar12345Bar12345";
    private static String keyString = "01020304050607080900010203040506";
    private static String ivString = "01020304050607080900010203040506";
    File inputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "weights.zip");
    File encryptedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/weights.enc");
    File decryptedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "weights.zip");


  /*  private static void unzip(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/

    public static void unzipMyZip(String zipFileName,
                                  String directoryToExtractTo) {
        Enumeration entriesEnum;
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(zipFileName);
            entriesEnum = zipFile.entries();

            File directory = new File(directoryToExtractTo);

            /**
             * Check if the directory to extract to exists
             */
            if (!directory.exists()) {
                /**
                 * If not, create a new one.
                 */
                new File(directoryToExtractTo).mkdir();
                System.err.println("...Directory Created -" + directoryToExtractTo);
            }
            while (entriesEnum.hasMoreElements()) {
                try {
                    ZipEntry entry = (ZipEntry) entriesEnum.nextElement();

                    if (entry.isDirectory()) {
                        /**
                         * Currently not unzipping the directory structure.
                         * All the files will be unzipped in a Directory
                         *
                         **/
                    } else {

                        System.err.println("Extracting file: "
                                + entry.getName());
                        /**
                         * The following logic will just extract the file name
                         * and discard the directory
                         */
                        int index = 0;
                        String name = entry.getName();
                        index = entry.getName().lastIndexOf("/");
                        if (index > 0 && index != name.length())
                            name = entry.getName().substring(index + 1);

                        System.out.println(name);

                        writeFile(zipFile.getInputStream(entry),
                                new BufferedOutputStream(new FileOutputStream(
                                        directoryToExtractTo + name)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            zipFile.close();
        } catch (IOException ioe) {
            System.err.println("Some Exception Occurred:");
            ioe.printStackTrace();
            return;
        }
    }

    public static final void writeFile(InputStream in, OutputStream out)
            throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);

        in.close();
        out.close();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get key
       /* key = getKey();
        // Get IV
        iv = getIV();*/
        Button encrypt = (Button) findViewById(R.id.encryptbtn);
        final Button decrypt = (Button) findViewById(R.id.decryptbtn);
        final TextView sampleTv = (TextView) findViewById(R.id.sampleTv);

        encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (inputFile.exists()) {
                        encrypt(inputFile, encryptedFile);
                    } else {
                        sampleTv.setText("Ooops..Sorry!!..Input file does not exist!!!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    decrypt(encryptedFile, decryptedFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void encrypt(File inputFile, File outputFile)
            throws Exception {
        doCrypto(Cipher.ENCRYPT_MODE, inputFile, outputFile);
        System.out.println("File encrypted successfully!");
    }

    public void decrypt(File inputFile, File outputFile)
            throws Exception {
        doCrypto(Cipher.DECRYPT_MODE, inputFile, outputFile);
        Log.d("Hello", "File decrypted successfully!");
    }

    private void doCrypto(int cipherMode, File inputFile,
                          File outputFile) {
        byte[] secret = hexStringToByteArray(keyString);
        byte[] iv = hexStringToByteArray(ivString);
        Cipher cipher = initCipher(cipherMode, iv, secret);

        try (FileInputStream inputStream = new FileInputStream(inputFile)) {
            byte[] inputBytes = new byte[(int) inputFile.length()];
            boolean count;
            while (count = inputStream.read(inputBytes) > 0) {
                try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                    if (cipher != null) {
                        byte[] outputBytes = convertInputBytesToOutputBytes(cipher, inputBytes);
                        outputStream.write(outputBytes);
                        if (cipherMode == Cipher.DECRYPT_MODE)
                            unzipMyZip(outputFile.getAbsolutePath(),"/sdcard/download/myfolder/");
                        }
                }
            }
        } catch (IOException e) {
            Log.e("Hello", e.getMessage());
        }
    }

    private Cipher initCipher(int cipherMode, byte[] iv, byte[] secret) {
        Key secretKey = new SecretKeySpec(secret, "AES");
        Log.d("Key::", String.valueOf(secretKey));
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(cipherMode, secretKey, new IvParameterSpec(iv));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            Log.e("Hello", e.getMessage());
        } catch (InvalidAlgorithmParameterException e) {
            Log.e("Hello", "NEW EXCEPTION:" + e.getMessage());
        }
        return cipher;
    }

    public byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private byte[] convertInputBytesToOutputBytes(Cipher cipher, byte[] inputBytes) {
        byte[] outputBytes = new byte[0];
        try {
            outputBytes = cipher.doFinal(inputBytes);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            Log.e("Helloooo", e.getMessage());
        }
        return outputBytes;
    }
}
