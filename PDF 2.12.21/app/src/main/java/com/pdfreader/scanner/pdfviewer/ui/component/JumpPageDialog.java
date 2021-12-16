package com.pdfreader.scanner.pdfviewer.ui.component;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.utils.ToastUtils;

public class JumpPageDialog extends BaseCenterDialog {

    private Context mContext;
    private int mNumberPage;
    private SplitRangeListener mListener;

    @SuppressLint("SetTextI18n")
    public JumpPageDialog(@NonNull Context context, int numberPage, SplitRangeListener listener) {
        super(context);
        mContext = context;
        mNumberPage = numberPage;
        mListener = listener;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_jump_to_page);

        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.9);
        getWindow().setLayout(width, ActionBar.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams param = getWindow().getAttributes();
        param.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        param.y = 10;
        //wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        getWindow().setAttributes(param);
//        Button cancelBtn = findViewById(R.id.btn_no);
//        Button submitBtn = findViewById(R.id.btn_yes);

        EditText startPageEdt = findViewById(R.id.enter_page_edt);
        startPageEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.d("keybroad", "onEditorAction: ");
                    try {
                        int toPage = Integer.parseInt(startPageEdt.getText().toString());

                        if (toPage <= 0 || toPage > mNumberPage) {
                            ToastUtils.showMessageShort(mContext, mContext.getString(R.string.view_pdf_jump_to_page_error));
                             startPageEdt.setText("");
                            return false;
                        }

                        if (mListener != null) {
                            mListener.onSubmitRange(toPage);
                        }
                        startPageEdt.clearFocus();

                        dismiss();
                    } catch (Exception e) {
                        startPageEdt.setText("");
                        ToastUtils.showMessageShort(mContext, mContext.getString(R.string.view_pdf_jump_to_page_error));
                    }
                    dismiss();
                }
                return false;
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    public interface SplitRangeListener {
        void onSubmitRange(int page);

        void onCancel();
    }
}
