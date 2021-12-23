package com.example.in4code.ui.component;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.in4code.R;
import com.example.in4code.ui.scan.ScanActivityNavigation;
import com.example.in4code.ui.scan.camera.CameraFragmentNavigation;
import com.google.mlkit.vision.barcode.Barcode;

public class ResultScanQRDialog extends Dialog {

        Barcode barcode;
        Context context;
    ScanActivityNavigation listener;
    CameraFragmentNavigation mListener2;
    public ResultScanQRDialog(@NonNull Context mcontext, Barcode mBarcode , ScanActivityNavigation mListener, CameraFragmentNavigation continueListener) {
            super(mcontext);

            this.context = mcontext;
            this.barcode =mBarcode;
            this.listener =mListener;
this.mListener2 = continueListener;
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_result_scan_qr);

            int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9);
            getWindow().setLayout(width, ActionBar.LayoutParams.WRAP_CONTENT);

            //        Button cancelBtn = findViewById(R.id.btn_no);
            Button btContinue = findViewById(R.id.bt_dialog_scan_qr_continue);
            Button btBack = findViewById(R.id.bt_dialog_scan_qr_back);
            TextView message = findViewById(R.id.content_dialog_scan_qr);

            if(barcode==null){
                message.setText("cant scan image");

            }else{
                Rect bounds = barcode.getBoundingBox();
                Point[] corners = barcode.getCornerPoints();

                String rawValue = barcode.getRawValue();

                int valueType = barcode.getValueType();
                // See API reference for complete list of supported types
                switch (valueType) {
                    case Barcode.TYPE_WIFI:
                        String ssid = barcode.getWifi().getSsid();
                        String password = barcode.getWifi().getPassword();
                        message.setText("SSID: "+ssid+" Password: "+password);
//            int type = barcode.getWifi().getEncryptionType();
                        break;
                    case Barcode.TYPE_URL:
                        String title = barcode.getUrl().getTitle();
                        String url = barcode.getUrl().getUrl();
                        message.setText(url);
                        break;
                    case Barcode.TYPE_TEXT:
                        message.setText(barcode.getDisplayValue());
                        break;
                }
            }
            btContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    mListener2.continueProcess();
                }
            });
            btBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    listener.finishScan();
                }
            });

        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

    }