package com.example.myapplication10101010.BottomFragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication10101010.Admin.Fragments.Adapters.ViewPagerAdapter;
import com.example.myapplication10101010.BottomFragments.Services.DropOffServices;
import com.example.myapplication10101010.BottomFragments.Services.PickUpAndDeliver;
import com.example.myapplication10101010.BottomFragments.Services.PickUpServices;
import com.example.myapplication10101010.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {
    private TextView name, next_btn, back_btn;
    private ViewPager my_viewPager;
    private LinearLayout mDotLayout;
    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;
    private RelativeLayout drop_off, pick_up, pick_up_deliver;
    private FirebaseAuth mAuth;
    private FirebaseUser mUSer;
    private FirebaseFirestore db;
    private Task<DocumentSnapshot> userColRef;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false);
        _init();

        mAuth = FirebaseAuth.getInstance();
        mUSer = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();

        viewPagerAdapter = new ViewPagerAdapter(getContext());
        my_viewPager.setAdapter(viewPagerAdapter);
        setIndicator(0);
        my_viewPager.addOnPageChangeListener(viewListener);

        _setClickListener();

        if(mUSer != null) {
            userColRef = db.collection("USERS")
                    .document(mUSer.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.exists()) {
                            String _name = documentSnapshot.getString("NAME");
                            name.setText(_name);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        drop_off.setOnClickListener(view -> {
            Intent i = new Intent(getContext(), DropOffServices.class);
            startActivity(i);
        });

        pick_up.setOnClickListener(view -> {
            Intent i = new Intent(getContext(), PickUpServices.class);
            startActivity(i);
        });

        pick_up_deliver.setOnClickListener(view -> {
            Intent i = new Intent(getContext(), PickUpAndDeliver.class);
            startActivity(i);
        });

        return v;
    }

    public void setIndicator(int position) {
        dots = new TextView[4];
        mDotLayout.removeAllViews();

        for(int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getContext());
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                dots[i].setTextColor(getResources().getColor(R.color.white,getActivity().getApplicationContext().getTheme()));
            }
            mDotLayout.addView(dots[i]);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dots[position].setTextColor(getResources().getColor(R.color.dark_late_gray,getActivity().getApplicationContext().getTheme()));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setIndicator(position);

            if(position > 0) {
                back_btn.setVisibility(View.VISIBLE);
            }else {
                back_btn.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private int getItem(int i) {
        return my_viewPager.getCurrentItem() + i;
    }

    private void _setClickListener() {
        back_btn.setOnClickListener(view -> {
            if(getItem(0) > 0) {
                my_viewPager.setCurrentItem(getItem(-1), true);
            }
        });
        next_btn.setOnClickListener(view -> {
            if(getItem(0) < 4) {
                my_viewPager.setCurrentItem(getItem(1), true);
            }
        });
    }

    private void _init() {
        name = v.findViewById(R.id.name);
        drop_off = v.findViewById(R.id.drop_off);
        pick_up = v.findViewById(R.id.pick_up);
        pick_up_deliver = v.findViewById(R.id.pick_up_deliver);

        next_btn = v.findViewById(R.id.next_btn);
        back_btn = v.findViewById(R.id.back_btn);
        my_viewPager = v.findViewById(R.id.my_viewpager);
        mDotLayout = v.findViewById(R.id.indicator_ll);
    }

}