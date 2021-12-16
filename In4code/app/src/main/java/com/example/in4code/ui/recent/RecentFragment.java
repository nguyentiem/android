package com.example.in4code.ui.recent;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.in4code.databinding.FragmentRecentBinding;

public class RecentFragment extends Fragment implements RecentFragmentNavigation{

    private RecentViewModel galleryViewModel;
    private FragmentRecentBinding binding;
    Context mContext;

    public RecentFragment() {
        // Required empty public constructor
    }
    public RecentFragment(Context context){
        this.mContext =context;
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(RecentViewModel.class);

        binding = FragmentRecentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGallery;

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}