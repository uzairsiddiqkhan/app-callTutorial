package com.example.call;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.call.databinding.ActivityNotificationBinding;

public class notificationActivity extends AppCompatActivity {
ActivityNotificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}