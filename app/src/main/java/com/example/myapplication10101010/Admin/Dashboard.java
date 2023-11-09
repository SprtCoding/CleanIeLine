package com.example.myapplication10101010.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.multidex.MultiDex;

import android.os.Bundle;

import com.example.myapplication10101010.Admin.Fragments.AdminAccountFragment;
import com.example.myapplication10101010.Admin.Fragments.AdminHomeFragment;
import com.example.myapplication10101010.Admin.Fragments.EarningsFragment;
import com.example.myapplication10101010.Admin.Fragments.PriceListFragment;
import com.example.myapplication10101010.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Dashboard extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        MultiDex.install(this);
        bottomNavigationView = findViewById(R.id.navigation);

        loadFragment(new AdminHomeFragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Handle the Home menu item click here
                loadFragment(new AdminHomeFragment());
            } else if (itemId == R.id.nav_earnings) {
                // Handle the Search menu item click here
                loadFragment(new EarningsFragment());
            } else if (itemId == R.id.nav_price) {
                // Handle the Search menu item click here
                loadFragment(new PriceListFragment());
            } else if (itemId == R.id.nav_account) {
                // Handle the Notification menu item click here
                loadFragment(new AdminAccountFragment());
            }
            return true;
        });
    }
    private boolean loadFragment(Fragment fragment) {
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container,fragment)
                    .commit();
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}