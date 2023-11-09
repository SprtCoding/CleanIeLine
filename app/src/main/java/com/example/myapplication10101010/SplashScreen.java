package com.example.myapplication10101010;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.myapplication10101010.Admin.Dashboard;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class SplashScreen extends AppCompatActivity {
    FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private CollectionReference userTokenColRef;
    private FirebaseFirestore db;
    Task<DocumentSnapshot> userColRef;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        MultiDex.install(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();

        userTokenColRef = db.collection("USER_TOKEN");

        Handler handler = new Handler();

        handler.postDelayed(() -> {
            checkUser();
        }, 2000);

    }
    private void checkUser() {
        if(mUser != null) {
            userColRef = db.collection("USERS").document(mUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.exists()) {

                            FirebaseMessaging.getInstance().getToken()
                                    .addOnCompleteListener(task1 -> {
                                        if (!task1.isSuccessful()) {
                                            Toast.makeText(SplashScreen.this, "Fetching FCM registration token failed :" + task1.getException(), Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        // Get new FCM registration token
                                        String token = task1.getResult();
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("token", token);
                                        userTokenColRef.document(mUser.getUid()).set(map);
                                    });

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
        }else {
            Intent i = new Intent(SplashScreen.this, SignIn.class);
            startActivity(i);
            finish();
        }
    }
}