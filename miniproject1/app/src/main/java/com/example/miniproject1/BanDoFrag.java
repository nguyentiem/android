package com.example.miniproject1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

public class BanDoFrag extends Fragment {
    //private WebView browser;
    private String bando ="https://news.google.com/covid19/map?hl=vi&mid=/m/01crd5&gl=VN&ceid=VN:vi";
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }
    @Override
    public Lifecycle getLifecycle() {
        return super.getLifecycle();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.bando,container,false);
        MainActivity.webView2 = (WebView) view.findViewById(R.id.webview);
        MainActivity.webView2.setWebViewClient(new MyBrowser());
        MainActivity.webView2.getSettings().setLoadsImagesAutomatically(true);
        MainActivity.webView2.getSettings().setJavaScriptEnabled(true);
        MainActivity.webView2.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        MainActivity.webView2.loadUrl(bando);
         return view ;
    }

    protected class  MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
