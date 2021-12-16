package com.example.in4code.ui.recycleview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.in4code.R;
import com.example.in4code.databinding.ItemImageViewBinding;
import com.example.in4code.repos.ImageQR;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ImageRecycleAdapter extends RecyclerView.Adapter <ImageRecycleAdapter.ImageViewHolder>{
    public List<ImageQR> getListImage() {
        return listImage;
    }

    List<ImageQR> listImage = new ArrayList<>();
    int mCurrentChose =-1;
    public ItemChosen listener;
    public void setListImage( List<ImageQR> mListImage){
        this.listImage =mListImage;
        notifyDataSetChanged();
    }
    public ImageRecycleAdapter (){

    }
    public ImageRecycleAdapter (ItemChosen mListener){
           this.listener = mListener;
    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemImageViewBinding itemImageViewBinding = DataBindingUtil.inflate(inflater,R.layout.item_image_view,parent,false);
        ImageViewHolder imageViewHolder =new ImageViewHolder(itemImageViewBinding);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Context context = holder.itemImageViewBinding.getRoot().getContext();
        File imgFile = new File(listImage.get(position).getFilePath());
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.itemImageViewBinding.itemImage.setImageBitmap(myBitmap);
        }
    }


    @Override
    public int getItemCount() {
        return listImage.size();
    }
    class ImageViewHolder extends RecyclerView.ViewHolder{

        public  ItemImageViewBinding itemImageViewBinding;

        public ImageViewHolder(@NonNull ItemImageViewBinding itemView) {
            super(itemView.getRoot());
          this.itemImageViewBinding =itemView;
          itemImageViewBinding.itemImageBackground.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  // hien backgroud do
                  // dong backgroucd cu
                  // thay doi m current
              }
          });
        }
    }

    public interface ItemChosen {
        void onClick();
    }
}
