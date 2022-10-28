package com.pdfreader.scanner.pdfviewer.utils.adapter;

import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.listener.OnDataOptionClickListener;
import com.pdfreader.scanner.pdfviewer.utils.ColorUtils;

public class DataOptionViewHolder extends RecyclerView.ViewHolder {
    private ConstraintLayout mContentView;
    private TextView mNameView;
    private RadioButton mCheckBoxView;

    public DataOptionViewHolder(@NonNull View itemView) {
        super(itemView);
        initView();
    }

    private void initView() {
        mContentView = itemView.findViewById(R.id.item_content_view);
        mNameView = itemView.findViewById(R.id.item_name_view);
        mCheckBoxView = itemView.findViewById(R.id.item_checkbox_view);
    }

    public void bindView(int position, String nameOption, boolean isSelected, OnDataOptionClickListener listener, boolean isShowColor) {
        if (isShowColor) {
            int[] COLOR_LIST = {R.color.red_theme_color, R.color.blue_theme_color, R.color.jade_theme_color, R.color.violet_theme_color, R.color.orange_theme_color, R.color.green_theme_color, R.color.yellow_theme_color};
            int color = ColorUtils.getColorFromResource(itemView.getContext(), COLOR_LIST[position]);
            mNameView.setTextColor(color);
        }

        mNameView.setText(nameOption);

        mCheckBoxView.setChecked(isSelected);

        mContentView.setOnClickListener(v -> {
            listener.onClickItem(position);
        });

        mCheckBoxView.setOnClickListener(v -> {
            listener.onClickItem(position);
        });
    }
}
