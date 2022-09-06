package com.example.call;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.call.databinding.ActivityFindPeopleBinding;

public class findPeopleActivity extends AppCompatActivity {
    ActivityFindPeopleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityFindPeopleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



    }
}