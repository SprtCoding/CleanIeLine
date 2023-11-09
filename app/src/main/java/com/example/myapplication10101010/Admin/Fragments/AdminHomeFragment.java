package com.example.myapplication10101010.Admin.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.myapplication10101010.Admin.AdminNotification;
import com.example.myapplication10101010.Admin.Fragments.Adapters.VPAdapter;
import com.example.myapplication10101010.Admin.Fragments.TabFragments.AllOrdersFragment;
import com.example.myapplication10101010.Admin.Fragments.TabFragments.CompletedOrdersFragment;
import com.example.myapplication10101010.Admin.Fragments.TabFragments.RejectedOrdersFragment;
import com.example.myapplication10101010.R;
import com.google.android.material.tabs.TabLayout;

public class AdminHomeFragment extends Fragment {
    View v;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView notification;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_admin_home, container, false);
        _init();

        tabLayout.setupWithViewPager(viewPager);

        VPAdapter vpAdapter = new VPAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new AllOrdersFragment(), "All Orders");
        vpAdapter.addFragment(new CompletedOrdersFragment(), "Completed Orders");
        vpAdapter.addFragment(new RejectedOrdersFragment(), "Rejected Orders");
        viewPager.setAdapter(vpAdapter);

        notification.setOnClickListener(view -> {
            Intent i = new Intent(getContext(), AdminNotification.class);
            startActivity(i);
        });

        return v;
    }

    private void _init() {
        tabLayout = v.findViewById(R.id.tab_ll);
        viewPager = v.findViewById(R.id.view_pager);
        notification = v.findViewById(R.id.notification);
    }
}