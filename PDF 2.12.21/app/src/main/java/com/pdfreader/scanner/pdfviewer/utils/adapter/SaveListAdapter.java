package com.pdfreader.scanner.pdfviewer.utils.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.data.model.SavedData;
import com.pdfreader.scanner.pdfviewer.listener.OnFileItemWithOptionClickListener;
import com.pdfreader.scanner.pdfviewer.utils.nativeads.NativeAdsViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SaveListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "FileListAdapter";
    private List<SavedData> mFileList = new ArrayList<>();
    private int mCurrentItem = -1;

    public List<? extends SavedData> getListVideoData() {
        return mFileList;
    }

    private OnFileItemWithOptionClickListener mListener;

    public SaveListAdapter(OnFileItemWithOptionClickListener listener) {
        this.mListener = listener;
    }

    public static final int ADS_INDEX = 1;

    public void setData(List<? extends SavedData> videoList) {
        mFileList = new ArrayList<>();
        mCurrentItem = -1;
        mFileList.addAll(videoList);
        notifyDataSetChanged();
    }

    public void setCurrentItem(int position) {
        int temp = mCurrentItem;
        mCurrentItem = position;

        notifyItemChanged(temp);
        notifyItemChanged(mCurrentItem);
    }

    public void clearAllData() {
        mFileList.clear();
        notifyDataSetChanged();
    }

    public void clearData(int position) {
        if (position < 0 || position > mFileList.size())   return;
        if (mCurrentItem == position) mCurrentItem = -1;
        mFileList.remove(position);
        if (position == 0 && mFileList.size() > 1) {
            Collections.swap(mFileList, 0, 1);
        }
        notifyDataSetChanged();
    }

    public void updateData(int position, SavedData fileData) {
        mFileList.set(position, fileData);
        notifyItemChanged(position);
    }

    public SaveListAdapter() {
    }

    @Override
    public int getItemViewType(int position) {
        if (position == ADS_INDEX) {
            return 1;
        } else {
            return super.getItemViewType(position);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_view, parent, false);
            return new SaveListViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_small_native_control, parent, false);
            return new NativeAdsViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            ((SaveListViewHolder) holder).bindView(position, mFileList.get(position), mCurrentItem, mListener);
        } else {
            ((NativeAdsViewHolder) holder).bindView(false);
        }
    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }
}
