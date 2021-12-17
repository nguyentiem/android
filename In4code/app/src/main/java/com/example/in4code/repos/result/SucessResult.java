package com.example.in4code.repos.result;

import com.google.mlkit.vision.barcode.Barcode;

public class SucessResult implements Result{

    public SucessResult(Barcode barcode) {
        this.barcode = barcode;
    }

    Barcode barcode;
    public void setMessage(String message) {
        this.message = message;
    }

    String message;
    @Override
    public boolean getType() {
        return true;
    }

    @Override
    public String getMessage() {
        if (message==null) message = "get barcode success";
        return message;
    }
    public Barcode getBarcode() {
        return barcode;
    }

    public void setBarcode(Barcode barcode) {
        this.barcode = barcode;
    }

}
