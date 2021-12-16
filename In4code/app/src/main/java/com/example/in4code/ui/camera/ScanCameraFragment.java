package com.example.in4code.ui.camera;

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
import com.example.in4code.databinding.LayoutCameraScanningBinding;
import com.example.in4code.ui.favorite.FavoriteViewModel;

public class ScanCameraFragment extends Fragment implements CameraFragmentNavigation{


    private LayoutCameraScanningBinding binding;
    private Context mContext;
    public ScanCameraFragment(){

    }
    public ScanCameraFragment(Context context){
        this.mContext =context;
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = LayoutCameraScanningBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
