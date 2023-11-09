package com.example.myapplication10101010.BottomFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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
    private TextView name;
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

    private void _init() {
        name = v.findViewById(R.id.name);
        drop_off = v.findViewById(R.id.drop_off);
        pick_up = v.findViewById(R.id.pick_up);
        pick_up_deliver = v.findViewById(R.id.pick_up_deliver);
    }

}