package com.pdfreader.scanner.pdfviewer.utils.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.data.model.SavedData;
import com.pdfreader.scanner.pdfviewer.listener.OnFileItemWithOptionClickListener;
import com.pdfreader.scanner.pdfviewer.utils.DateTimeUtils;
import com.pdfreader.scanner.pdfviewer.utils.file.FileUtils;
import com.pdfreader.scanner.pdfviewer.utils.pdf.PdfUtils;

public class SaveListViewHolder extends RecyclerView.ViewHolder {
    private ConstraintLayout mContentView;
    private ImageView mImageView;
    private ImageView mShareView;
    private ImageView mMoreView;
    private TextView mNameView;
    private TextView mSizeView;
    private TextView mDateTextView; // date
    private TextView mDirTexView;

    public SaveListViewHolder(@NonNull View itemView) {
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
        mSizeView = itemView.findViewById(R.id.item_size_text_view);
        mDirTexView = itemView.findViewById(R.id.item_dir_text_view);
    }

    @SuppressLint({"StaticFieldLeak", "UseCompatLoadingForDrawables"})
    public void bindView(int position, SavedData fileData, int currentItem, OnFileItemWithOptionClickListener listener) {

        if (fileData != null && fileData.getFilePath() != null) {
            mNameView.setText(fileData.getDisplayName());
            mDirTexView.setText(fileData.getFilePath());

            if (fileData.getTimeAdded() > 0) {
                mDateTextView.setVisibility(View.VISIBLE);
                mDateTextView.setText(DateTimeUtils.fromTimeUnixToDateTimeString(fileData.getTimeAdded()));
            } else {
                mDateTextView.setVisibility(View.GONE);
            }
           // setForBookmark();
            mContentView.post(() -> {
                try {
                    AsyncTask asyncTask = new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] objects) {
                            int numberPage = FileUtils.getNumberPages(fileData.getFilePath());

                            try {
                                ((Activity) itemView.getContext()).runOnUiThread(() -> {
                                    mSizeView.setText(itemView.getContext().getString(R.string.number_page, numberPage));
                                });

                            } catch (Exception ignored) {
                            }

                            return null;
                        }
                    };
                    asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } catch (Exception e) {
                    // donothing
                }

            });
        }
        mMoreView.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_open_pdf_more_vertical));
        mShareView.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_remove_history));

        mContentView.setOnClickListener(v -> {
            listener.onClickItem(position);
        });

       // mMoreView.setVisibility(View.GONE);

        mShareView.setOnClickListener(view -> {
            listener.onMainFunctionItem(position);
        });
         mMoreView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 listener.onOptionItem(position);
             }
         });
        mContentView.setLongClickable(true);
        mContentView.setOnLongClickListener(v -> {
            listener.onOptionItem(position);
            return true;
        });
    }
}
