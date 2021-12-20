package com.example.in4code.repos.image;

import android.net.Uri;

public class ImageQR {
    private   String name;
    private Uri imageUri;
    private   String filePath;



    private String content;



    private String fileType;
    public ImageQR(){

    }
    public ImageQR(String name, Uri imageUri, String filePath) {
        this.name = name;
        this.imageUri = imageUri;
        this.filePath = filePath;

    }

    public ImageQR(String name, Uri imageUri, String filePath,String fileType,  String content) {
        this.name = name;
        this.imageUri = imageUri;
        this.filePath = filePath;
        this.content = content;
        this.fileType = fileType;
    }
    public ImageQR(String name, Uri imageUri, String filePath, String fileType) {
        this.name = name;
        this.imageUri = imageUri;
        this.filePath = filePath;
        this.fileType = fileType;
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
    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
