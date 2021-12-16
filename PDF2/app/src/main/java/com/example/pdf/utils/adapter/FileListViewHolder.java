package com.example.pdf.utils.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.data.model.FileData;
import com.pdfreader.scanner.pdfviewer.listener.OnFileItemWithOptionClickListener;
import com.pdfreader.scanner.pdfviewer.utils.DateTimeUtils;
import com.pdfreader.scanner.pdfviewer.utils.file.FileUtils;

public class FileListViewHolder extends RecyclerView.ViewHolder {
    private ConstraintLayout mContentView;
    private ImageView mImageView;// pdf view
    private ImageView mShareView; // bookmark
    private ImageView mMoreView; // menu view
    private TextView mNameView; // name
    private TextView mSizeView; // size
    private TextView mDateTextView; // date
    private TextView mDirTexView;


    public FileListViewHolder(@NonNull View itemView) {
        super(itemView);
        initView();
    }

    private void initView() {
        mContentView = itemView.findViewById(R.id.item_content_view);
        mImageView = itemView.findViewById(R.id.item_image_view);
        mShareView = itemView.findViewById(R.id.item_share_view);
        mMoreView = itemView.findViewById(R.id.item_more_view);
        mNameView = itemView.findViewById(R.id.item_name_view);
        mDateTextView = itemView.findViewById(R.id.item_date_text_view);
        mSizeView = itemView.findViewById(R.id.item_size_text_view);
        mDirTexView = itemView.findViewById(R.id.item_dir_text_view);
    }


    public void setForBookmark(boolean isBookmarked) {
        if (isBookmarked) {
            mShareView.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_remove_bookmark));
        } else {

            mShareView.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_more_locked_file_add_bm));
        }
    }
    @SuppressLint({"StaticFieldLeak", "UseCompatLoadingForDrawables", "SetTextI18n"})
    public void bindView(int position, FileData fileData, int currentItem, OnFileItemWithOptionClickListener listener) {

        if (fileData.getFilePath() != null) {
            mNameView.setText(fileData.getDisplayName());
        }

        mDateTextView.setVisibility(View.VISIBLE);

        if (fileData.getTimeAdded() > 0) {
            String text = itemView.getContext().getString(R.string.full_detail_file, DateTimeUtils.fromTimeUnixToDateTimeString(fileData.getTimeAdded()),"");
            mDateTextView.setText(text);
            mSizeView.setText(fileData.getSize() + " Kb");
        } else {
            mSizeView.setText(fileData.getSize() + " Kb");
        }
        setForBookmark(fileData.isBookMark());
        // mDateTextView.setText(FileUtils.getFileDirectoryPath(fileData.getFilePath()));
        mDirTexView.setText(FileUtils.getFileDirectoryPath(fileData.getFilePath()));
        mContentView.setOnClickListener(v -> {
            listener.onClickItem(position);
        });

        mMoreView.setOnClickListener(view -> {
            listener.onOptionItem(position);
        });

//        mShareView.setVisibility(View.GONE);
        mShareView.setOnClickListener(view ->{
            fileData.setBookMark(!fileData.isBookMark());
            setForBookmark(fileData.isBookMark());
            listener.onBookMarkItem(position,fileData.isBookMark());
        } );
        mContentView.setLongClickable(true);
        mContentView.setOnLongClickListener(v -> {
            listener.onOptionItem(position);
            return true;
        });
    }
}
