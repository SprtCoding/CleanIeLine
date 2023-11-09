package com.example.myapplication10101010;

import android.os.Bundle;
import android.widget.Toast;

import com.example.myapplication10101010.BottomFragments.HomeFragment;
import com.example.myapplication10101010.BottomFragments.NotificationFragment;
import com.example.myapplication10101010.BottomFragments.OrdersFragment;
import com.example.myapplication10101010.SideFragments.AboutFragment;
import com.example.myapplication10101010.BottomFragments.AccountFragment;
import com.example.myapplication10101010.SideFragments.ServicesFragment;
import com.example.myapplication10101010.SideFragments.SupportFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import java.util.Objects;

public class UserDashboard extends AppCompatActivity {
    private Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_dashboard);
        MultiDex.install(this);

        bottomNavigationView = findViewById(R.id.navigation);

        setToolbar();
        initView();
        initNavView();
        loadFragment(new HomeFragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Handle the Home menu item click here
                loadFragment(new HomeFragment());
            } else if (itemId == R.id.nav_orders) {
                // Handle the Search menu item click here
                loadFragment(new OrdersFragment());
            } else if (itemId == R.id.nav_notification) {
                // Handle the Notification menu item click here
                loadFragment(new NotificationFragment());
            } else if (itemId == R.id.nav_profile) {
                // Handle the Message menu item click here
                loadFragment(new AccountFragment());
            }
            return true;
        });

    }

    private void initNavView() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.nav_support) {
                fragment = new SupportFragment();
            } else if (itemId == R.id.nav_about) {
                fragment = new AboutFragment();
            } else if (itemId == R.id.nav_services) {
                fragment = new ServicesFragment();
            }
            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
            drawerLayout.closeDrawer(GravityCompat.START);
            return loadFragment(fragment);
        });
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(0);
    }

    private void initView() {

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setToolbarNavigationClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
        toggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
        toggle.syncState();
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
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }else
            super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}