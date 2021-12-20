package com.example.in4code.ui.recycleview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.in4code.R;
import com.example.in4code.databinding.ItemImageViewBinding;
import com.example.in4code.repos.image.ImageQR;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ImageRecycleAdapter extends RecyclerView.Adapter <ImageRecycleAdapter.ImageViewHolder>{

    public ImageRecycleAdapter (ItemChosen mListener){
        this.mListener = mListener;
    }

    private int mCurrentChose =-1;
   private List<ImageQR> listImage = new ArrayList<>();
   private ImageQR choseImage;
    private ItemChosen mListener;
    public ImageQR getChoseImage(){
        if(mCurrentChose>=0&&mCurrentChose<listImage.size()){
            return listImage.get(mCurrentChose);
        }
        return null;
    }

    public int getmCurrentChose() {
        return mCurrentChose;
    }

    public void setmCurrentChose(int currentChose) {
        // bo chon
         if(currentChose>=0&&currentChose<listImage.size()){
        if(this.mCurrentChose>=0&&this.mCurrentChose<listImage.size()){
            int tem = this.mCurrentChose ;
            mCurrentChose =-1;
            notifyItemChanged(tem);
        }
        this.mCurrentChose = currentChose;


            notifyItemChanged(this.mCurrentChose);
        }
    }



    public ItemChosen getmListener() {
        return mListener;
    }

    public void setmListener(ItemChosen mListener) {
        this.mListener = mListener;
    }



    public void setListImage( List<ImageQR> mListImage){
        this.listImage =mListImage;
//        Log.d("TAG", ": "+listImage.size());
        notifyDataSetChanged();
    }
    public List<ImageQR> getListImage() {
        return listImage;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_view, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int width = layoutParams.width = (int) (parent.getWidth() / 3.1);
        layoutParams.height = (int) (width * 1.0);
        view.setLayoutParams(layoutParams);
        return new ImageViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ((ImageViewHolder) holder).bindView(position, listImage.get(position), mCurrentChose, mListener);
    }


    @Override
    public int getItemCount() {
        return listImage.size();
    }


    class ImageViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout mContentView;
        private ImageView mImageView;
        private View mSelectedView;
        private CardView mOrderView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            initView();
        }

        private void initView() {
            mContentView = itemView.findViewById(R.id.item_content_view);
            mImageView = itemView.findViewById(R.id.item_image_view);
            mSelectedView = itemView.findViewById(R.id.item_selected_view);
            mOrderView = itemView.findViewById(R.id.item_order_view);
        }

        @SuppressLint("SetTextI18n")
        public void bindView(int position, ImageQR imageData, int  chose, ItemChosen listener) {
           if(chose== position){
               mOrderView.setVisibility(View.VISIBLE);
               mSelectedView.setVisibility(View.VISIBLE);
           }else{
               mSelectedView.setVisibility(View.GONE);
               mOrderView.setVisibility(View.GONE);
           }
            mImageView.setVisibility(View.VISIBLE);



                Glide.with(itemView.getContext())
                        .load(imageData.getFilePath())
                        .into(mImageView);

            mContentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOrderView.setVisibility(View.VISIBLE);
                    mSelectedView.setVisibility(View.VISIBLE);
                    listener.onClick(position);
                }
            });

        }
    }
    public interface ItemChosen {
        void onClick(int current);
    }
}
