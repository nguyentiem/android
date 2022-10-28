package com.pdfreader.scanner.pdfviewer.ui.component;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.utils.ToastUtils;

public class RenameFileDialog extends BaseCenterDialog {

    private Context mContext;
    private String mOldName;
    private RenameFileListener mListener;

    public RenameFileDialog(@NonNull Context context, String oldName, RenameFileListener listener) {
        super(context);
        mContext = context;
        mOldName = oldName;
        mListener = listener;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rename_file);

        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.9);
        getWindow().setLayout(width, ActionBar.LayoutParams.WRAP_CONTENT);

        Button cancelBtn = findViewById(R.id.btn_no);
        Button submitBtn = findViewById(R.id.btn_yes);
        EditText nameEditBtn = findViewById(R.id.enter_file_name_edt);

        nameEditBtn.setText(mOldName);
        cancelBtn.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onCancel();
            }
            dismiss();
        });
        submitBtn.setOnClickListener(v -> {
            String newName = nameEditBtn.getText().toString().trim();
            if (newName == null || newName.length() == 0) {
                ToastUtils.showMessageShort(getContext(), getContext().getString(R.string.please_input_name_text));
            } else {
                if (mListener != null) {
                    mListener.onSubmitName(newName);
                }
                dismiss();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public interface RenameFileListener {
        void onSubmitName(String newName);
        void onCancel();
    }
}
