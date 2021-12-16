package com.example.miniproject1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
 //  Button click1,click2,click3,click4;
    ViewPager mPager;
    FragAdapter fragmentadapter;
    public static WebView webView1,webView2;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbarmenu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        mPager  = findViewById(R.id.viewpager);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Nguyễn Tiệm");
        ///////////////////////////////////////////////////////
        fragmentadapter = new FragAdapter(getSupportFragmentManager());
        mPager.setAdapter(fragmentadapter);


    }

    @Override
    public void onBackPressed() {
        switch(mPager.getCurrentItem()){
            case 1:
                if(webView1.canGoBack()){
                    webView1.goBack();
                }else{
                    super.onBackPressed();
                }
                break;
            case 0:
                if(webView2.canGoBack()){
                   webView2.goBack();
                }else{
                    super.onBackPressed();
                }
                break;

            default:
                super.onBackPressed();
                break;
        }
    }
//
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Toast.makeText(this, "Item 1 clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item2:
                Toast.makeText(this, "Item 2 clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.search:
                 Toast.makeText(this, "search clicked", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return false;
        }

        //return super.onOptionsItemSelected(item);
    }
}
