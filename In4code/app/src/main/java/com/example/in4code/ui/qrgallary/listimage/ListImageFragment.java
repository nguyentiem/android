package com.example.in4code.ui.qrgallary.listimage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageProxy;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.in4code.databinding.ActivityGallaryQrcodeBinding;
import com.example.in4code.databinding.LayoutListImageCanBinding;
import com.example.in4code.repos.image.ImageQR;
import com.example.in4code.ui.component.ResultQRDialog;
import com.example.in4code.ui.recycleview.ImageRecycleAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListImageFragment extends Fragment implements ImageRecycleAdapter.ItemChosen{
  private  Context context;
  private  ActivityGallaryQrcodeBinding activityGallaryQrcodeBinding;
  private  LayoutListImageCanBinding binding ;
  private  ListImageFragmentViewModel viewModel;
  private  ImageRecycleAdapter adapter;
  private  ActivityGallaryQrcodeBinding bindingActivity;
  private  List<ImageQR> imageQRList =new ArrayList<>();
  private  ImageQR choseImage;
  private  int mCurrent =-1;
    private BarcodeScanner scanner;
 public ListImageFragmentViewModel getViewModel(){
     viewModel =  new ViewModelProvider((ViewModelStoreOwner) context).get(ListImageFragmentViewModel.class);
     return viewModel;
 }

    public ListImageFragment(Context context,ActivityGallaryQrcodeBinding mBinding) {
        this.context=context;
        this.activityGallaryQrcodeBinding = mBinding;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        getViewModel();
        binding = LayoutListImageCanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        adapter =new ImageRecycleAdapter(this);
        binding.listImage.setLayoutManager(new GridLayoutManager(context, 3));
        binding.listImage.setAdapter(adapter);
        activityGallaryQrcodeBinding.imageScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ///// scan image here

                if(mCurrent>=0&&mCurrent<imageQRList.size()){
                    ImageQR image = imageQRList.get(mCurrent);
                    File fileImage = new File(image.getFilePath());
                    if(fileImage.exists()){
                        Uri uri = Uri.fromFile(fileImage);
                        InputImage inputImage = getImageFromUri(uri);
                        processImageProxy(inputImage);
                    }
                }
            }
        });
        activityGallaryQrcodeBinding.imageScan.setVisibility(View.GONE);
        updateList();
        viewModel.startGetLocalImage();
        return root;
    }
public void updateList(){
     viewModel.getListLocalImage().observe(getViewLifecycleOwner(), new Observer<List<ImageQR>>() {
         @Override
         public void onChanged(List<ImageQR> list) {
             imageQRList =new ArrayList<>();
             imageQRList.addAll(list);
             Log.d("TAG", " "+imageQRList.size());
             adapter.setListImage(imageQRList);
         }
     });
}
    @Override
    public void onClick(int current) {
      mCurrent = current;
      adapter.setmCurrentChose(current);
      activityGallaryQrcodeBinding.imageScan.setVisibility(View.VISIBLE);
    }
    public InputImage getImageFromUri(Uri uri) {
        InputImage image = null;
        try {
            image = InputImage.fromFilePath(context, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
    @SuppressLint("UnsafeExperimentalUsageError")
    private void processImageProxy(InputImage inputImage) {

         Barcode bar ;
        scanner = BarcodeScanning.getClient();
        Log.d("TAG", "Thread: "+Thread.currentThread().getId());
        scanner.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(@NonNull List<Barcode> barcodes) {

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
//                        Toast.makeText(mContext.getApplicationContext(), exception.getMessage(),Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "onFailure: " + exception.getMessage());
                    }
                }).addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
            @Override
            public void onComplete(@NonNull Task<List<Barcode>> task) {
               List<Barcode> barcodes= task.getResult();
               if(barcodes ==null|| barcodes.size()==0){
                   Toast.makeText(context,"this image is not barcode ",Toast.LENGTH_SHORT).show();
               }else{
                   Barcode barcode =barcodes.get(0);
                   new ResultQRDialog(context,barcode).show();
                   scanner.close();
               }
            }
        });


    }

}
