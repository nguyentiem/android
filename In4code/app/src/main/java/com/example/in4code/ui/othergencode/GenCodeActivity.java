package com.example.in4code.ui.othergencode;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.in4code.databinding.ActivityGenCodeBinding;


public class GenCodeActivity extends AppCompatActivity implements GenCodeNavigation{

    private AppBarConfiguration appBarConfiguration;
    private ActivityGenCodeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGenCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }


}