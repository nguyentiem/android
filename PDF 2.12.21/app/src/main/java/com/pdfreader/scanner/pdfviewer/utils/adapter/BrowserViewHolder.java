package com.pdfreader.scanner.pdfviewer.utils.adapter;

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
import com.pdfreader.scanner.pdfviewer.utils.DateTimeUtils;

public class BrowserViewHolder extends RecyclerView.ViewHolder {
    private ConstraintLayout mContentView;
    private ImageView mImageView;
    private ImageView mShareView;
    private ImageView mMoreView;
    private TextView mNameView;
    private TextView mDateTextView;
    private ImageView mLockView;

    public BrowserViewHolder(@NonNull View itemView) {
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

    }

    @SuppressLint({"StaticFieldLeak", "UseCompatLoadingForDrawables", "SetTextI18n"})
    public void bindView(int position, FileData fileData, int currentItem, OnFileItemWithOptionClickListener listener) {

        if (fileData.getFileType().equals(DataConstants.FILE_TYPE_DIRECTORY)) {
            mImageView.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_folder));
            mNameView.setText(fileData.getDisplayName());
            mShareView.setVisibility(View.GONE);
            mMoreView.setVisibility(View.GONE);

            mContentView.setLongClickable(false);

            if (fileData.getTimeAdded() > 0) {
                mDateTextView.setVisibility(View.VISIBLE);
                mDateTextView.setText(DateTimeUtils.fromTimeUnixToDateTimeString(fileData.getTimeAdded()));
            } else {
                mDateTextView.setVisibility(View.GONE);
            }
        } else {
            mShareView.setVisibility(View.VISIBLE);
            mMoreView.setVisibility(View.VISIBLE);

            mImageView.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_pdf));

            if (fileData.getFilePath() != null) {
                mNameView.setText(fileData.getDisplayName());
            }

            mMoreView.setOnClickListener(view -> {
                listener.onOptionItem(position);
            });

            mShareView.setVisibility(View.GONE);

            mContentView.setLongClickable(true);
            mContentView.setOnLongClickListener(v -> {
                listener.onOptionItem(position);
                return true;
            });

            if (fileData.getTimeAdded() > 0) {
                String text = itemView.getContext().getString(R.string.full_detail_file, DateTimeUtils.fromTimeUnixToDateTimeString(fileData.getTimeAdded()), (fileData.getSize() + " Kb"));
                mDateTextView.setText(text);
            } else {
                mDateTextView.setText(fileData.getSize() + " Kb");
            }
        }

        mContentView.setOnClickListener(v -> {
            listener.onClickItem(position);
        });
    }
}
