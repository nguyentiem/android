package com.example.in4code.ui.main.home;

import android.app.ProgressDialog;
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
import android.widget.Toast;

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
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment {
  private ImageView mQRCode;
  private Context mContext;
  private Bitmap bitmap;
  private QRGEncoder qrgEncoder;
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public HomeFragment(Context activity) {
        this.mContext = activity;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        ProgressDialog progress = new ProgressDialog(mContext);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mQRCode = root.findViewById(R.id.img_qr_code);
        Single.create(new SingleOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Bitmap> emitter) throws Exception {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    WindowManager manager = (WindowManager) mContext.getSystemService(mContext.WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int width = point.x;
                    int height = point.y;

                    int dimen = width < height ? width : height;
                    dimen = dimen * 3 / 4;
                    qrgEncoder = new QRGEncoder("nguyentiem", null, QRGContents.Type.TEXT, dimen);

                    try {

                        bitmap = qrgEncoder.encodeAsBitmap();
                        emitter.onSuccess(bitmap);
                    } catch (WriterException e) {
                        e.printStackTrace();
                        Log.e("Tag", e.toString());
                    }


                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Bitmap>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Bitmap mbitmap) {

                        progress.dismiss();
                        mQRCode.setImageBitmap(mbitmap);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("TAG", "gen QRcode: ");
                    }
                });
//        Log.d("TAG", "onCreateView: "+bitmap.toString());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}