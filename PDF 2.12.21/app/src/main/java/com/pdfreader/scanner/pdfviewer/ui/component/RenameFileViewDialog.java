package com.pdfreader.scanner.pdfviewer.ui.component;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.data.model.FileData;
import com.pdfreader.scanner.pdfviewer.utils.ToastUtils;

public class RenameFileViewDialog  extends  BaseCenterDialog {
    private FileData fileData;
    private Context mContext;
    private String mOldName;
    EditText nameEditBtn;
    TextView information;
    private RenameFileViewDialog.RenameFileViewListener mListener;

    public RenameFileViewDialog(@NonNull Context context, FileData fileData, RenameFileViewListener mListener) {
        super(context);
        this.fileData = fileData;
        this.mListener = mListener;
        mContext = context;

        String displayName;
        try {
            displayName = this.fileData.getDisplayName().substring(0, this.fileData.getDisplayName().lastIndexOf("."));
            mOldName= displayName;
        } catch (Exception e) {
            mOldName=new String("no name");
        }

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rename_file_view);

        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.9);
        getWindow().setLayout(width, ActionBar.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams param = getWindow().getAttributes();
        param.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        param.y = 100;
        //wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        getWindow().setAttributes(param);

        nameEditBtn = findViewById(R.id.enter_file_name_edt_view);
        information = findViewById(R.id.view_size);
        nameEditBtn.setText(mOldName);
        information.setText(fileData.getSize()+" KB * "+fileData.getPages()+" Pages");
    }

    public FileData getFileData() {
        return fileData;
    }

    public void setFileData(FileData fileData) {
        this.fileData = fileData;
    }

    @SuppressLint("WrongConstant")
    public RenameFileViewDialog(@NonNull Context context, String oldName, RenameFileViewDialog.RenameFileViewListener listener) {
        super(context);
        mContext = context;
        mOldName = oldName;
        mListener = listener;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rename_file_view);

        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.9);
        getWindow().setLayout(width, ActionBar.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams param = getWindow().getAttributes();
        param.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        param.y = 100;
        //wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        getWindow().setAttributes(param);
        nameEditBtn = findViewById(R.id.enter_file_name_edt_view);
        nameEditBtn.setText(mOldName);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStop() {
        String newName = nameEditBtn.getText().toString().trim();
            if (newName == null || newName.length() == 0) {
                ToastUtils.showMessageShort(getContext(), getContext().getString(R.string.please_input_name_text));
            } else {
                if (mListener != null) {
                    mListener.onSubmitName(newName);
                }
                dismiss();
            }
        super.onStop();
    }

    public interface RenameFileViewListener {
        void onSubmitName(String newName);

    }
}
