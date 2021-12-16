package com.example.hello;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
Button create;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        create =findViewById(R.id.create_folder);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                //todo when permission is granted
            } else {

                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                startActivity(intent);
            }
        }
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setupFile();
            }
        });

    }

    private void setupFile() {
        Log.d("TAG", "setupFile: "+Environment.getExternalStorageDirectory().getAbsolutePath());
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/NguyenTiem/");//  /PDFfiles/
        if (!dir.exists()) {
            boolean isDirectoryCreated = dir.mkdir();
            if (!isDirectoryCreated) {
                Log.e("Error", "Directory could not be created");
            }
        }
       File mFilePdf = new File(dir, "newfile" + ".pdf");

        try {

            mFilePdf.createNewFile();
            FileOutputStream fOut = new FileOutputStream(mFilePdf);


            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new
                    PdfDocument.PageInfo.Builder(100, 100, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();

            canvas.drawText("Nguyen van tiem", 10, 10, paint);



            document.finishPage(page);
            document.writeTo(fOut);
            document.close();
        }catch (IOException e){
            Log.i("error",e.getLocalizedMessage());
        }



//        ContentResolver resolver = getContentResolver();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/NguyenTiem");
//        String path = String.valueOf(resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues));
//        File folder = new File(path);
//        Log.d("path", "setupFile: "+path);
//        boolean isCreada = folder.exists();
//        if(!isCreada) {
//            folder.mkdirs();
//        }
    }
}