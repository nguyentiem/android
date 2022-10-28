package com.pdfreader.scanner.pdfviewer.ui.component;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.data.model.FileData;
import com.pdfreader.scanner.pdfviewer.utils.DateTimeUtils;

public class PdfOptionDialog extends BottomSheetDialogFragment {
    private FileOptionListener mListener;
    private boolean mBookmarked;
    private String mNameFile;
    private int mPosition;
    private long mDate;
    private FileData mFile;

    public PdfOptionDialog() {
        // Required empty public constructor
    }
//    public PdfOptionDialog(boolean isBookmarked, String nameFile, long date, FileOptionListener listener) {
//        this.mListener = listener;
//        this.mBookmarked = isBookmarked;
//        mNameFile = nameFile;
//
//        mDate = date;
//    }
    public PdfOptionDialog(boolean isBookmarked, String fileName, long date, int position, FileOptionListener listener) {
        this.mListener = listener;
        this.mBookmarked = isBookmarked;
        mNameFile = fileName;
        mPosition = position;
        mDate = date;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.sheet_dialog_style);
    }

    private void setAutoExpanded() {
        if (getDialog() != null) {
            getDialog().setOnShowListener(dialog -> {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (bottomSheetInternal != null) {
                    BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            });
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_view_pdf_option, container, false);
        TextView nameFile = v.findViewById(R.id.more_name);
        nameFile.setText(mNameFile);
        nameFile.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.openFile(mPosition);
            }
            dismiss();
        });

        TextView dateFile = v.findViewById(R.id.more_date);
        if (mDate != -1) {
            dateFile.setText(DateTimeUtils.fromTimeUnixToDateString(mDate));
        } else {
            dateFile.setText("Locked file");
        }

//        ImageView upload = v.findViewById(R.id.more_upload);
//        upload.setOnClickListener(v1 -> {
//            if (mListener != null) {
//                mListener.uploadFile(mPosition);
//            }
//            dismiss();
//        });

//        ImageView print = v.findViewById(R.id.more_print);
//        print.setOnClickListener(v1 -> {
//            if (mListener != null) {
//                mListener.printFile(mPosition);
//            }
//            dismiss();
//        });
        ConstraintLayout shareOption = v.findViewById(R.id.more_layout_share);
        shareOption.setOnClickListener(v1 -> {
            if (mListener != null) {
                mListener.shareFile(mPosition);
            }
            dismiss();
        });

        ConstraintLayout copyOption = v.findViewById(R.id.more_layout_coppy);
        copyOption.setOnClickListener(v1 -> {
            if (mListener != null) {
                mListener.copyFile(mPosition);

            }
            dismiss();
        });

        ConstraintLayout mergeOption = v.findViewById(R.id.more_layout_merge);
        mergeOption.setOnClickListener(v1 -> {
            if (mListener != null) {

                mListener.mergeFile(mPosition);
            }
            dismiss();
        });

        TextView addBookmarkTv = v.findViewById(R.id.more_textview_name);
        ImageView addBookmarkImg = v.findViewById(R.id.more_imageview_name);
        if (mBookmarked) {
            addBookmarkTv.setText(R.string.more_locked_remove_bm);
            addBookmarkImg.setImageDrawable(getContext().getDrawable(R.drawable.ic_remove_bookmark));
        } else {
            addBookmarkTv.setText(R.string.more_locked_add_bm);
            addBookmarkImg.setImageDrawable(getContext().getDrawable(R.drawable.ic_more_locked_file_add_bm));
        }

        ConstraintLayout bookmarkOption = v.findViewById(R.id.more_layout_add_bm);
        bookmarkOption.setOnClickListener(v1 -> {
            if (mListener != null) {
                mListener.optionBookmark(mPosition, !mBookmarked);
            }
            dismiss();
        });




        ConstraintLayout renameOption = v.findViewById(R.id.more_layout_rename);
        renameOption.setOnClickListener(v1 -> {
            if (mListener != null) {
                mListener.renameFile(mPosition);
            }
            dismiss();
        });

        ConstraintLayout saveDriveOption = v.findViewById(R.id.more_layout_save_drive);
        saveDriveOption.setOnClickListener(v1 -> {
            if (mListener != null) {
                mListener.saveDrive(mPosition);
            }
            dismiss();
        });

        ConstraintLayout printOption = v.findViewById(R.id.more_layout_print);
        printOption.setOnClickListener(v1 -> {
            if (mListener != null) {
                mListener.printFile(mPosition);
            }
            dismiss();
        });

        ConstraintLayout deleteOption = v.findViewById(R.id.more_layout_delete);
        deleteOption.setOnClickListener(v1 -> {
            if (mListener != null) {
                mListener.deleteFile(mPosition);
            }
            dismiss();
        });

        return v;


    }

    public interface FileOptionListener {
        void openFile(int position);
        void optionBookmark(int position, boolean isAdd);
        void shareFile(int position);
        void copyFile(int position);
        void mergeFile(int position);
        void renameFile(int position);
        void saveDrive(int position);
        void printFile(int position);
        void deleteFile(int position);
    }

//    public interface FileViewOptionListener {
//
//        void optionBookmark();
//        void shareFile();
//        void copyFile();
//        void mergeFile();
//        void renameFile();
//        void saveDrive();
//        void printFile();
//        void deleteFile();
//    }

}
