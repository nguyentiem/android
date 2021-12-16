package com.pdfreader.scanner.pdfviewer.ui.component;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.data.model.FileData;
import com.pdfreader.scanner.pdfviewer.utils.ToastUtils;

public class MainOptionDialog extends BaseCenterDialog{
    private MainViewDialogListener mListener;
    private Context mContext;
   LinearLayout upload,scan,merge;
    public MainOptionDialog(@NonNull Context context, MainViewDialogListener listener) {
        super(context);
        mListener = listener;
        this.mContext = context;


            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.main_option_dialog);
            upload = findViewById(R.id.upload_layout_option);
            scan = findViewById(R.id.scan_layout_option);
            merge = findViewById(R.id.merge_layout_option);
            int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.9);
            getWindow().setLayout(width, ActionBar.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams param = getWindow().getAttributes();
            param.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            param.y = 400;
//            param.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            getWindow().setAttributes(param);
            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.clickUpload();
                    dismiss();
                }
            });
            merge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.clickMerge();
                    dismiss();
                }
            });
scan.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        mListener.clickScan();
        dismiss();
    }
});
        }



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        protected void onStop() {
            super.onStop();
        }

        public interface MainViewDialogListener {
            void clickUpload();
            void clickScan();
            void clickMerge();

        }
    }
