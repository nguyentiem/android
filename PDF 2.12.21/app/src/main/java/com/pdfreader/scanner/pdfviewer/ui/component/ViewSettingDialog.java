package com.pdfreader.scanner.pdfviewer.ui.component;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.data.model.ViewPdfOption;

public class ViewSettingDialog extends BottomSheetDialogFragment {
    private int  viewMode = 0, drakMode = 0, scollingMode = 0;
    ViewSettingListener mListener;
    ImageView singleMode;
    ImageView facingMode;
    TextView scollingText;
    ImageView scollingImg;
    ImageView drakImg;
    ViewPdfOption mViewOption;
    public ViewSettingListener getmListener() {
        return mListener;
    }

    public void setmListener(ViewSettingListener mListener) {
        this.mListener = mListener;
    }

    public ViewSettingDialog() {
    }

    public ViewSettingDialog(ViewPdfOption viewPdfOption, ViewSettingListener mListener) {
       this.mViewOption = viewPdfOption;
        this.mListener = mListener;
        viewMode = mViewOption.getmSingle();
        drakMode = mViewOption.getViewMode();
        scollingMode = mViewOption.getOrientation();
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.sheet_dialog_style);
    }

    public void setViewSingleMode() {
        if (viewMode !=0) {
           singleMode.setImageDrawable(getContext().getDrawable(R.drawable.img_nonsingle));
            facingMode.setImageDrawable(getContext().getDrawable(R.drawable.img_facing));
        } else {
            singleMode.setImageDrawable(getContext().getDrawable(R.drawable.img_single));
            facingMode.setImageDrawable(getContext().getDrawable(R.drawable.img_nonfacing));
        }
    }
    public void setScollingMode(){
        if(scollingMode!=0){
            scollingText.setText("Scolling direction: Horizonter");
            scollingImg.setImageDrawable((getContext().getDrawable(R.drawable.ic_switch_on)));
        }else{
            scollingText.setText("Scolling direction: Vertical");
            scollingImg.setImageDrawable((getContext().getDrawable(R.drawable.ic_swith_off)));
        }
    }
    public  void setDrakMode(){
        if(drakMode!=0){
            drakImg.setImageDrawable((getContext().getDrawable(R.drawable.ic_switch_on)));
        }else{
            drakImg.setImageDrawable((getContext().getDrawable(R.drawable.ic_swith_off)));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_view_setting, container, false);
        singleMode = v.findViewById(R.id.single_mode);
        facingMode = v.findViewById(R.id.facing_mode);
        scollingText = v.findViewById(R.id.scolling_textview);
        scollingImg = v.findViewById(R.id.scolling_switch);
        drakImg = v.findViewById(R.id.drak_theme_switch);
        setViewSingleMode();
        setDrakMode();
        setScollingMode();
        singleMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewMode!=0){
                    viewMode =0;
                    mViewOption.setmSingle(viewMode);
                    setViewSingleMode();
                    mListener.clickViewMode();

//                    facingMode.setImageDrawable(getContext().getDrawable(R.drawable.img_nonfacing));
//                    facingMode.setImageDrawable(getContext().getDrawable(R.drawable.img_single));
                }
            }
        });
        facingMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewMode==0){
                    viewMode=1;
                    mViewOption.setmSingle(viewMode);
                    setViewSingleMode();
                mListener.clickViewMode();
//                facingMode.setImageDrawable(getContext().getDrawable(R.drawable.img_facing));
//                singleMode.setImageDrawable(getContext().getDrawable(R.drawable.img_nonsingle));
                }
            }
        });
        scollingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scollingMode = (scollingMode+1)%2;
                mViewOption.setOrientation(scollingMode);
                setScollingMode();
                mListener.clickScolling();
            }
        });
        drakImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drakMode=(drakMode+1)%2;
                mViewOption.setViewMode(drakMode);
                setDrakMode();
                mListener.clickDrak();
            }
        });
        return v;
    }

    public interface ViewSettingListener {
        public void clickViewMode();

        public void clickScolling();

        public void clickDrak();
    }
}
