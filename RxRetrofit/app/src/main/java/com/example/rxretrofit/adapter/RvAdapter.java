package com.example.rxretrofit.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rxretrofit.R;
import com.example.rxretrofit.data.User;
import com.example.rxretrofit.view.RecycleViewUser;
import com.example.rxretrofit.viewmodel.MainVewmodel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RvAdapter extends RecyclerView.Adapter <RecycleViewUser>{
    List <User> users = new ArrayList<User>();



    public RvAdapter() {
        //users.add(new User("nguyen tiem",1,"https://avatars.githubusercontent.com/u/1?v=4","tiem"));
    }

        public void setUsers(List<User> users) {
        this.users = users;
    }
//    public void setUsers(User user){
//        users.add(user);
//    }

    @NonNull
    @Override
    public RecycleViewUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecycleViewUser(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_users, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewUser holder, int position) {
       holder.id.setText(" "+users.get(position).getId());
        holder.name.setText(users.get(position).getName());
       holder.login.setText(users.get(position).getLogin());
        Picasso.get().load(users.get(position).getAvatar_url()).into(holder.avatar);
    }


    @Override
    public int getItemCount() {
        return users.size();
    }
}
