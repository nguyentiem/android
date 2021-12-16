package com.example.miniproject1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class KhaiBaoFrag extends Fragment {
//    private Button click3 ;
    //WebView khaibao;
    String webkhaibao ="https://tokhaiyte.vn/";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.khaibao,container,false);
        MainActivity.webView1 = view.findViewById(R.id.Wv_kb);
        MainActivity.webView1.setWebViewClient(new MyBrowser());
        MainActivity.webView1.getSettings().setLoadsImagesAutomatically(true);
        MainActivity.webView1.getSettings().setJavaScriptEnabled(true);
        MainActivity.webView1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        MainActivity.webView1.loadUrl(webkhaibao);
//        click3 = view.findViewById(R.id.click3);
//        click3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "khai bao ",Toast.LENGTH_LONG).show();
//            }
//        });

        return view;
    }
    protected class  MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}
