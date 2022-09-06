package com.example.call;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.call.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // nav view calling
        binding.navView.setOnItemSelectedListener(navigationBarView);

        //find button calling to find people Activity
        binding.findbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this, findPeopleActivity.class);
                startActivity(intent);
            }
        });


    }

    public NavigationBarView.OnItemSelectedListener navigationBarView = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.navigation_setting:
                    Intent intentSetting = new Intent(MainActivity.this, settingActivity.class);
                    startActivity(intentSetting);
                    break;
                case R.id.navigation_notification:
                    Intent intentNotification = new Intent(MainActivity.this, notificationActivity.class);
                    startActivity(intentNotification);
                    break;
                case R.id.navigation_logout:
                    AlertDialogLogout();


                    break;
            }

            return true;
        }
    };

    public void AlertDialogLogout() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Logging Out");
        alertDialog.setMessage("Are you sure you want to Log out?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent intentLogout = new Intent(MainActivity.this, registerActivity.class);
                startActivity(intentLogout);
                finish();

            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.create().show();
    }


}