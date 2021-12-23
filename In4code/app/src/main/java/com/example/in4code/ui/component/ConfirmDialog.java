package com.example.in4code.ui.component;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.in4code.R;

public class ConfirmDialog extends Dialog {
    private Context mContext;


    public ConfirmDialog(@NonNull Context context, String titleString, String messageString) {
        super(context);
        mContext = context;


        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm);

        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.9);
        getWindow().setLayout(width, ActionBar.LayoutParams.WRAP_CONTENT);

//        Button cancelBtn = findViewById(R.id.btn_no);
        Button submitBtn = findViewById(R.id.ok_dialog_image_scan);
        TextView title = findViewById(R.id.title_text_gallary);
        TextView message = findViewById(R.id.content_text_image_scan);

        title.setText(titleString);
        message.setText(messageString);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}