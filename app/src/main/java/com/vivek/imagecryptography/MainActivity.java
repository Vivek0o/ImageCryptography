package com.vivek.imagecryptography;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    private String path;
    Context context=this;
    View vw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void selectimage(View view) {
        vw=view;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image"),PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data !=null && data.getData()!=null){
            Uri uri = data.getData();
            Uri selectedImageUri = data.getData( );
            String picturePath = abc(uri );
            Log.d("Picture Path", picturePath);
            try{
                path = abc(uri);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                ImageView imageView = findViewById(R.id.imageView2);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Please select a valid file...", Toast.LENGTH_SHORT).show();
            selectimage(vw);
        }

    }
    public String abc(Uri contentURI){
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        cursor.moveToFirst();
        String image_id = cursor.getString(0);
        image_id = image_id.substring(image_id.lastIndexOf(":") + 1);
        cursor.close();
        cursor = context.getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    public void encrypt(View view) {

        try{
            FileInputStream file = new FileInputStream(path);
            FileOutputStream outputStream = new FileOutputStream(path+"-Encrypt.jpg");
            byte k[] = "NiTh5252".getBytes();
            SecretKeySpec key = new SecretKeySpec(k,"DES");
            Cipher enc = Cipher.getInstance("DES");
            enc.init(Cipher.ENCRYPT_MODE,key);
            CipherOutputStream cos = new CipherOutputStream(outputStream,enc);
            byte[] buf = new byte[1024];
            int read;
            while((read=file.read(buf))!=-1){
                cos.write(buf,0,read);
            }
            file.close();
            outputStream.flush();
            cos.close();
            Toast.makeText(getApplicationContext(),"The file encrypted Successfully",Toast.LENGTH_LONG).show();
        }
        catch (Exception e){
           Toast.makeText(getApplicationContext(),path+" "+e,Toast.LENGTH_LONG).show();
            System.out.println(path);
        }
    }
    public void Decrypt(View view) {
        try{
            FileInputStream file = new FileInputStream(path);
            FileOutputStream outputStream = new FileOutputStream(path+"-Decrypt.jpg");
            byte k[] = "NiTh5252".getBytes();
            SecretKeySpec key = new SecretKeySpec(k,"DES");
            Cipher enc = Cipher.getInstance("DES");
            enc.init(Cipher.DECRYPT_MODE,key);
            CipherOutputStream cos = new CipherOutputStream(outputStream,enc);
            byte[] buf = new byte[1024];
            int read;
            while((read=file.read(buf))!=-1){
                cos.write(buf,0,read);
            }
            file.close();
            outputStream.flush();
            cos.close();
            Toast.makeText(getApplicationContext(),"The file encrypted Successfully",Toast.LENGTH_LONG).show();
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),path+" "+e,Toast.LENGTH_LONG).show();
        }
    }
}
