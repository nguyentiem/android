package com.pdfreader.scanner.pdfviewer.ui.component;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

public class ViewPdfOptionDialog extends BottomSheetDialogFragment {
    private FileOptionListener mListener;
    private boolean mBookmarked;
    private String mNameFile;

    private boolean mIsSupportDelete;
    private TextView mBookmarkView;
    private ImageView mAddBookmarkImg;

    public ViewPdfOptionDialog(boolean mIsBookmarked, boolean pdfEncrypted, boolean mIsNeedToReview, String fileName, FileOptionListener fileOptionListener) {
        // Required empty public constructor
    }

    public ViewPdfOptionDialog(boolean isBookmarked, boolean isSupportDelete, String nameFile, FileOptionListener listener) {
        this.mListener = listener;
        this.mBookmarked = isBookmarked;
        this.mNameFile = nameFile;

        this.mIsSupportDelete = isSupportDelete;
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

        mBookmarkView = v.findViewById(R.id.more_textview_name);
        mAddBookmarkImg = v.findViewById(R.id.more_imageview_name);
        setForBookmark();
        ConstraintLayout shareOption = v.findViewById(R.id.more_layout_share);
        shareOption.setOnClickListener(v1 -> {
            if (mListener != null) {
                mListener.shareFile();
            }
            dismiss();
        });

        ConstraintLayout copyOption = v.findViewById(R.id.more_layout_coppy);

//        copyOption.setVisibility(View.VISIBLE);
        copyOption.setOnClickListener(v1 -> {
                if (mListener != null) {
                    mListener.copyFile();
                }
                dismiss();
            });

        ConstraintLayout mergeOption = v.findViewById(R.id.more_layout_merge);

//        mergeOption.setVisibility(View.VISIBLE);
        mergeOption.setOnClickListener(v1 -> {
            if (mListener != null) {
                mListener.mergeFile();
            }
            dismiss();
        });

        ConstraintLayout bookmarkOption = v.findViewById(R.id.more_layout_add_bm);
        bookmarkOption.setOnClickListener(v1 -> {
            if (mListener != null) {
                mListener.optionBookmark();
            }
            dismiss();
        });
        ConstraintLayout renameOption = v.findViewById(R.id.more_layout_rename);
        renameOption.setOnClickListener(v1 -> {
            if (mListener != null) {
                mListener.renameFile();
            }
            dismiss();
        });

        ConstraintLayout saveDriveOption = v.findViewById(R.id.more_layout_save_drive);
        saveDriveOption.setOnClickListener(v1 -> {
            if (mListener != null) {
                mListener.saveDrive();
            }
            dismiss();
        });
        ConstraintLayout printOption = v.findViewById(R.id.more_layout_print);
        printOption.setOnClickListener(v1 -> {
            if (mListener != null) {
                mListener.printFile();
            }
            dismiss();
        });

        ConstraintLayout deleteOption = v.findViewById(R.id.more_layout_delete);
        if (mIsSupportDelete) {
            deleteOption.setOnClickListener(v1 -> {
                if (mListener != null) {
                    mListener.deleteFile();
                }
                dismiss();
            });
        } else {
            deleteOption.setVisibility(View.GONE);
        }

        return v;
    }

    public void setBookmark(boolean isBookmarked) {
        mBookmarked = isBookmarked;
        setForBookmark();
    }

    public void setForBookmark() {
        if (mBookmarked) {
            mBookmarkView.setText(R.string.more_locked_remove_bm);
            mAddBookmarkImg.setImageDrawable(getContext().getDrawable(R.drawable.ic_remove_bookmark));
        } else {
            mBookmarkView.setText(R.string.more_locked_add_bm);
            mAddBookmarkImg.setImageDrawable(getContext().getDrawable(R.drawable.ic_more_locked_file_add_bm));
        }
    }

    public interface FileOptionListener {
        void shareFile();
        void copyFile();
        void mergeFile();
        void optionBookmark();
        void renameFile();
        void saveDrive();
        void printFile();
        void deleteFile();
    }
}
