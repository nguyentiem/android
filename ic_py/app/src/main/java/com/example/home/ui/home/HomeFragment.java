package com.example.home.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.home.data.Device;
import com.example.home.data.Home;
import com.example.home.data.Room;
import com.example.home.databinding.FragmentHomeBinding;
import com.example.home.ui.MainViewModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    MainViewModel mainViewModel;

    public HomeFragment(MainViewModel model) {
        this.mainViewModel = model;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}