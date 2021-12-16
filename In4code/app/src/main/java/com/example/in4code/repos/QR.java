package com.example.in4code.repos;

import android.net.Uri;


public class QR {
 private   String name;
 private Uri imageUri;
 private   String filePath;
 private   String content;
 public QR(){

    }
    public QR(String name, Uri imageUri, String filePath, String content) {
        this.name = name;
        this.imageUri = imageUri;
        this.filePath = filePath;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
