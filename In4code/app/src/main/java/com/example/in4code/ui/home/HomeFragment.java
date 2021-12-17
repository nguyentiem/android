package com.example.in4code.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.in4code.R;
import com.example.in4code.databinding.FragmentHomeBinding;
import com.example.in4code.ui.main.MainActivity;
import com.example.in4code.ui.main.MainActivityNavigation;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class HomeFragment extends Fragment {
    ImageView mQRCode;
    Context mContext;


    MainActivityNavigation mActivity;
    Bitmap bitmap;
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    QRGEncoder qrgEncoder;
    public HomeFragment (){
    }

    public HomeFragment (MainActivityNavigation activity){

        this.mActivity =(MainActivity) activity;
        this.mContext  = (Context) this.mActivity;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

       mActivity = (MainActivity) mActivity;
       ((MainActivity) mActivity).getBinding().mainLayout.appBarLayout.searchToolbar.setVisibility(View.GONE);
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mQRCode = root.findViewById(R.id.img_qr_code);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            WindowManager manager = (WindowManager) mContext.getSystemService(mContext.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;

            int dimen = width < height ? width : height;
            dimen = dimen * 3 / 4;
            qrgEncoder = new QRGEncoder(homeViewModel.getText().getValue(), null, QRGContents.Type.TEXT, dimen);

            try {

                    bitmap = qrgEncoder.encodeAsBitmap();
                    mQRCode.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                    Log.e("Tag", e.toString());
                }




        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}