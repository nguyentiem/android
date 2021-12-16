package com.example.in4code.ui.favorite;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.in4code.databinding.FragmentFavoriteBinding;


public class FavoriteFragment extends Fragment {

    private FavoriteViewModel favoriteViewModel;
    private FragmentFavoriteBinding binding;
    private Context mContext;
   public FavoriteFragment(){

    }
    public FavoriteFragment(Context context){
   this.mContext =context;
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        favoriteViewModel =
                new ViewModelProvider(this).get(FavoriteViewModel.class);

        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSlideshow;

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}