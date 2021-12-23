package com.example.in4code.ui.component;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.in4code.R;
import com.google.mlkit.vision.barcode.Barcode;

public class ResultQRDialog extends Dialog {
    Barcode barcode;
    Context context;

    public ResultQRDialog(@NonNull Context mcontext, Barcode mBarcode) {
        super(mcontext);

        this.context = mcontext;
        this.barcode = mBarcode;


        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm);

        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9);
        getWindow().setLayout(width, ActionBar.LayoutParams.WRAP_CONTENT);


        Button submitBtn = findViewById(R.id.ok_dialog_image_scan);
        TextView message = findViewById(R.id.content_text_image_scan);
        if (barcode == null) {
            message.setText("cant scan image");

        } else {
            Rect bounds = barcode.getBoundingBox();
            Point[] corners = barcode.getCornerPoints();

            String rawValue = barcode.getRawValue();

            int valueType = barcode.getValueType();
            // See API reference for complete list of supported types
            switch (valueType) {
                case Barcode.TYPE_WIFI:
                    String ssid = barcode.getWifi().getSsid();
                    String password = barcode.getWifi().getPassword();
                    message.setText("SSID: " + ssid + " Password: " + password);
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
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
