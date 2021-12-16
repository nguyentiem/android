package com.example.in4code.ui.listimage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.in4code.databinding.LayoutCameraScanningBinding;
import com.example.in4code.databinding.LayoutListImageCanBinding;
import com.example.in4code.ui.scan.ScanActivityViewModel;

public class ListImageFragment extends Fragment {
    Context context;
    LayoutListImageCanBinding binding ;
    ListImageFragmentViewModel viewModel;
 public ListImageFragmentViewModel getViewModel(){
     viewModel =  new ViewModelProvider((ViewModelStoreOwner) context).get(ListImageFragmentViewModel.class);
     return viewModel;
 }

    public ListImageFragment(Context context) {
        this.context=context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
     getViewModel();
        binding = LayoutListImageCanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }
}
