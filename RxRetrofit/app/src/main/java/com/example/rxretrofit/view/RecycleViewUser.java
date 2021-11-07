package com.example.rxretrofit.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rxretrofit.R;


public class RecycleViewUser extends RecyclerView.ViewHolder {
   public ImageView avatar ;
    public TextView id,name,login;
    public RecycleViewUser(@NonNull View itemView) {
        super(itemView);
        avatar =itemView.findViewById(R.id.avatar);
        id = itemView.findViewById(R.id.iduser);
        name =itemView.findViewById(R.id.nameuser);
        login =itemView.findViewById(R.id.login);
    }
}
