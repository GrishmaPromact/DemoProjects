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
import java.security.Key;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class FileEncDecActivity extends AppCompatActivity {
    private static final int BUFFER_SIZE = 4096;
    private static String keyString = "Bar12345Bar12345";
    byte[] key, iv;
    File inputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "peace.zip");
    File encryptedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "peace.zip");
    File decryptedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "peace.zip");
    File outputDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/unzippedtestNew/");
    /*private static byte[] getKey() {
        KeyGenerator keyGen;
        byte[] dataKey = null;
        try {
            // Generate 256-bit key
            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            dataKey = secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dataKey;
    }

    private static byte[] getIV() {
        SecureRandom random = new SecureRandom();
        byte[] iv = random.generateSeed(16);
        return iv;
    }*/

    private static void unzip(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if (!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("Unzipping to " + newFile.getAbsolutePath());
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

    }

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

    public void encrypt(File inputFile, File outputFile)
            throws Exception {
        doCrypto(Cipher.ENCRYPT_MODE, inputFile, outputFile);
        System.out.println("File encrypted successfully!");
    }

    public void decrypt(File inputFile, File outputFile)
            throws Exception {
        doCrypto(Cipher.DECRYPT_MODE, inputFile, outputFile);
        System.out.println("File decrypted successfully!");
    }

    private void doCrypto(int cipherMode, File inputFile,
                          File outputFile) throws Exception {

        Key secretKey = new SecretKeySpec(keyString.getBytes(), "AES");
        Log.d("Key::", String.valueOf(secretKey));
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(cipherMode, secretKey);

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(outputBytes);

        if (cipherMode == Cipher.DECRYPT_MODE)
            unzipMyZip("/sdcard/download/peace.zip","/sdcard/download/unzipFolder/");
        inputStream.close();
        outputStream.close();

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
                        //ZipArchive zipArchive = new ZipArchive();
                        // zipArchive.unzip("/sdcard/download/peace.zip","/sdcard/download/unzipFolder","");
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
                    if (inputFile.exists()) {
                        decrypt(encryptedFile, decryptedFile);
                    } else {
                        sampleTv.setText("Ooops..Sorry!!..Input file does not exist!!!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
