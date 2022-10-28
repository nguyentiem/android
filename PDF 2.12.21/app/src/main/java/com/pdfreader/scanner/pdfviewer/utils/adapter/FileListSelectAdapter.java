package com.pdfreader.scanner.pdfviewer.utils.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.data.model.FileData;
import com.pdfreader.scanner.pdfviewer.listener.OnFileItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class FileListSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "FileListAdapter";
    private List<FileData> mFileList = new ArrayList<FileData>();
    private List<FileData> mSelectedList = new ArrayList<FileData>();
    public List<FileData> getListVideoData() {
        return mFileList;
    }

    private OnFileItemClickListener mListener;

    public FileListSelectAdapter(OnFileItemClickListener listener) {
        this.mListener = listener;
    }

    public void setData(List<FileData> videoList) {
        mFileList = new ArrayList<>();
        mFileList.addAll(videoList);
        notifyDataSetChanged();
    }

    public int getNumberSelectedFile() {
        return mSelectedList.size();
    }

    public List<FileData> getSelectedList() {
        return mSelectedList;
    }

    public void removeSelectedList() {
        mSelectedList = new ArrayList<>();
        notifyDataSetChanged();
    }
public void addAll(){
   for (FileData file :mFileList){
       if (mSelectedList.contains(file)) {
         continue;
       } else {
           mSelectedList.add(file);
       }
   }
    notifyDataSetChanged();
}
    public void revertData(int position) {
        if (mSelectedList.contains(mFileList.get(position))) {
            mSelectedList.remove(mFileList.get(position));
        } else {
            mSelectedList.add(mFileList.get(position));
        }

        notifyItemChanged(position);
    }

    public boolean isSelected(int position) {
        return mSelectedList.contains(mSelectedList.get(position));
    }

    public FileListSelectAdapter() {
        mSelectedList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_file_view, parent, false);
        return new FileListSelectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((FileListSelectViewHolder) holder).bindView(position, mFileList.get(position), mSelectedList.contains(mFileList.get(position)), mListener);
    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }
}
