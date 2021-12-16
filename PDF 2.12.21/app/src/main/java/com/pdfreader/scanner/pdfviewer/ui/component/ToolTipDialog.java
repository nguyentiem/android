package com.pdfreader.scanner.pdfviewer.ui.component;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.pdfreader.scanner.pdfviewer.R;

public class ToolTipDialog extends Dialog {
    public ToolTipDialog(@NonNull Context context, ClickListener clickListener) {
        super(context);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tooltip_layout);
        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.9);
        getWindow().setLayout(width, ActionBar.LayoutParams.WRAP_CONTENT);
        LinearLayout upLoad = findViewById(R.id.upload_file);
        upLoad.setOnClickListener(v -> clickListener.onClickUpLoad());
        LinearLayout scan = findViewById(R.id.scan);
        scan.setOnClickListener((v) -> clickListener.onClickScan());
        LinearLayout merge = findViewById(R.id.mergePdf);
        merge.setOnClickListener((v) -> clickListener.onMergePDFs());
    }

    @Override
    protected void onStop() {
        super.onStart();
        dismiss();
    }

    public interface ClickListener {
        public void onClickUpLoad();

        public void onClickScan();

        public void onMergePDFs();
    }
}
