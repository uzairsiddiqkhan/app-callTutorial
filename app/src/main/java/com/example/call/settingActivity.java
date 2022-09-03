package com.example.call;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.call.databinding.ActivitySettingBinding;

public class settingActivity extends AppCompatActivity {
   ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}