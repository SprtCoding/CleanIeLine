package com.example.myapplication10101010.Admin.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication10101010.R;
import com.example.myapplication10101010.SignIn;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminAccountFragment extends Fragment {
    View v;
    private TextView name, email;
    private MaterialButton logOutBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser mUSer;
    private FirebaseFirestore db;
    Task<DocumentSnapshot> userColRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_admin_account, container, false);

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
                            String _email = documentSnapshot.getString("EMAIL_ID");
                            name.setText(_name);
                            email.setText(_email);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        logOutBtn.setOnClickListener(view -> {
            mAuth.signOut();
            Intent i = new Intent(getContext(), SignIn.class);
            startActivity(i);
            getActivity().finish();
        });

        return v;
    }
    private void _init() {
        name = v.findViewById(R.id.name);
        email = v.findViewById(R.id.email);
        logOutBtn = v.findViewById(R.id.logOutBtn);
    }
}