package com.pdfreader.scanner.pdfviewer.ui.copy;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.constants.DataConstants;
import com.pdfreader.scanner.pdfviewer.data.model.FileData;
import com.pdfreader.scanner.pdfviewer.listener.OnFileItemWithOptionClickListener;
import com.pdfreader.scanner.pdfviewer.listener.OnFolderItemWithOptionClickListener;
import com.pdfreader.scanner.pdfviewer.utils.DateTimeUtils;

public class CopyViewHolder  extends RecyclerView.ViewHolder {
    private ConstraintLayout mContentView;
    private ImageView mImageView;
    private ImageView mShareView;
    private ImageView mMoreView;
    private TextView mNameView;
    private TextView mDateTextView;
    private TextView mDirText;

    public CopyViewHolder(@NonNull View itemView) {
        super(itemView);
        initView();
    }

    private void initView() {
        mContentView = itemView.findViewById(R.id.item_content_view);
        mImageView = itemView.findViewById(R.id.item_image_view);
        mShareView = itemView.findViewById(R.id.item_share_view);
        mMoreView = itemView.findViewById(R.id.item_more_view);
        mNameView = itemView.findViewById(R.id.item_name_view_main);
        mDateTextView = itemView.findViewById(R.id.item_date_text_view);
        mDirText = itemView.findViewById(R.id.item_dir_text_view);
    }

    @SuppressLint({"StaticFieldLeak", "UseCompatLoadingForDrawables", "SetTextI18n"})
    public void bindView(int position, FileData fileData, int currentItem, OnFolderItemWithOptionClickListener listener) {

        if (fileData.getFileType().equals(DataConstants.FILE_TYPE_DIRECTORY)) {
            mImageView.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_folder));
            mNameView.setText(fileData.getDisplayName());
            mDirText.setText(fileData.getFilePath());
            mShareView.setVisibility(View.GONE);
            mMoreView.setVisibility(View.GONE);
            mContentView.setLongClickable(false);
            if (fileData.getTimeAdded() > 0) {
                mDateTextView.setVisibility(View.VISIBLE);
                mDateTextView.setText(DateTimeUtils.fromTimeUnixToDateTimeString(fileData.getTimeAdded()));
            } else {
                mDateTextView.setVisibility(View.GONE);
            }
        }
        mContentView.setOnClickListener(v -> {
            listener.onClickItem(position);
        });
    }
}
