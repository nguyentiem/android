package com.pdfreader.scanner.pdfviewer.ui.pdftoimage;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.data.model.ImageExtractData;
import com.pdfreader.scanner.pdfviewer.listener.OnItemImageActionListener;
import com.pdfreader.scanner.pdfviewer.utils.image.ImageUtils;

import java.io.File;

public class PdfToImageViewHolder extends RecyclerView.ViewHolder {
    private ImageView mImageView;
    private ImageView mShareView;
    private ImageView mDownloadView;
    private ImageView mDeleteView;
    private TextView mNameView;

    public PdfToImageViewHolder(@NonNull View itemView) {
        super(itemView);

        mShareView = itemView.findViewById(R.id.item_share_view);
        mImageView = itemView.findViewById(R.id.item_image_view);
        mDownloadView = itemView.findViewById(R.id.item_download_view);
        mDeleteView = itemView.findViewById(R.id.item_delete_view);
        mNameView = itemView.findViewById(R.id.item_name_view);
    }

    @SuppressLint("SetTextI18n")
    public void bindView(int position, ImageExtractData imageExtractData, OnItemImageActionListener listener) {
        itemView.setOnClickListener(v -> listener.onClick(position));
        mNameView.setText(imageExtractData.getPage() + "");
        ImageUtils.loadImageFromUriToView(itemView.getContext(), new File(imageExtractData.getFilePath()), mImageView);

        mDownloadView.setOnClickListener(view -> {
            listener.onDownload(position);
        });

        mShareView.setOnClickListener(view -> {
            listener.onShare(position);
        });

        mDeleteView.setOnClickListener(v -> {
            listener.onDelete(position);
        });
    }
}