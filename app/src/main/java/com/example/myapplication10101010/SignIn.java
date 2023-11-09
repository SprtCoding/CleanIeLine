package com.example.myapplication10101010;


import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication10101010.Admin.Dashboard;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity {
    private TextView CreateBtn;
    private EditText email, pass;
    private MaterialButton signInBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private CollectionReference userTokenColRef;
    Task<DocumentSnapshot> userColRef;
    private ProgressDialog loading;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);
        MultiDex.install(this);
        _init();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        loading = new ProgressDialog(this);
        loading.setTitle("Sign In");
        loading.setMessage("Please wait...");

        db = FirebaseFirestore.getInstance();

        userTokenColRef = db.collection("USER_TOKEN");

        checkUser();

        signInBtn.setOnClickListener(view -> {
            loading.show();
            String _email = email.getText().toString().trim();
            String _password = pass.getText().toString().trim();

            if(TextUtils.isEmpty(_email)) {
                email.setError("Enter your email.");
                email.requestFocus();
                loading.dismiss();
            }else if(TextUtils.isEmpty(_password)) {
                pass.setError("Enter your password.");
                pass.requestFocus();
                loading.dismiss();
            }else {
                signInEmailPass(_email, _password);
            }
        });

        CreateBtn.setOnClickListener(view -> {
            Intent i = new Intent(this, SignUp.class);
            startActivity(i);
        });

    }

    private void checkUser() {
        if(mUser != null) {
            userColRef = db.collection("USERS").document(mUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.exists()) {
                            type = documentSnapshot.getString("ACCOUNT_TYPE");
                            Intent i;
                            if(type.equals("User")) {
                                i = new Intent(this, UserDashboard.class);
                            }else {
                                i = new Intent(this, Dashboard.class);
                            }
                            startActivity(i);
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void signInEmailPass(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            userColRef = db.collection("USERS").document(authResult.getUser().getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.exists()) {

                            FirebaseMessaging.getInstance().getToken()
                                    .addOnCompleteListener(task1 -> {
                                        if (!task1.isSuccessful()) {
                                            Toast.makeText(SignIn.this, "Fetching FCM registration token failed :" + task1.getException(), Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        // Get new FCM registration token
                                        String token = task1.getResult();
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("token", token);
                                        userTokenColRef.document(mAuth.getCurrentUser().getUid()).set(map);
                                    });

                            type = documentSnapshot.getString("ACCOUNT_TYPE");
                            if(type.equals("User")) {
                                Intent i = new Intent(this, UserDashboard.class);
                                startActivity(i);
                                loading.dismiss();
                                finish();
                            }else {
                                Intent i = new Intent(this, Dashboard.class);
                                startActivity(i);
                                finish();
                                loading.dismiss();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            loading.dismiss();
        });
    }

    private void _init() {
        CreateBtn = findViewById(R.id.CreateBtn);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        signInBtn = findViewById(R.id.signInBtn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}