package com.example.myapplication10101010;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication10101010.FireStoreQuery.DBQuery;
import com.example.myapplication10101010.FireStoreQuery.MyCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUp extends AppCompatActivity {
    private TextView signInBtn;
    private EditText full_name, email, pass, con_pass;
    private MaterialButton signUpBtn;
    private FirebaseAuth mAuth;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        MultiDex.install(this);
        _init();

        DBQuery.g_firestore = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        loading = new ProgressDialog(this);
        loading.setTitle("Creating Account");
        loading.setMessage("Please wait...");

        signUpBtn.setOnClickListener(view -> {
            loading.show();
            String _fullName = full_name.getText().toString();
            String _email = email.getText().toString();
            String _password = pass.getText().toString();
            String _con_pass = con_pass.getText().toString();

            if(TextUtils.isEmpty(_fullName)) {
                full_name.setError("Full Name is required!");
                full_name.requestFocus();
                loading.dismiss();
            }else if(TextUtils.isEmpty(_email)) {
                email.setError("Email is required!");
                email.requestFocus();
                loading.dismiss();
            }else if(TextUtils.isEmpty(_password)) {
                pass.setError("Please enter your password!");
                pass.requestFocus();
                loading.dismiss();
            }else if(TextUtils.isEmpty(_con_pass)) {
                con_pass.setError("You need to confirm your password!");
                con_pass.requestFocus();
                loading.dismiss();
            }else {
                if(_password.equals(_con_pass)) {
                    SignInWithEmailPass(_fullName, _email, _password);
                }else {
                    Toast.makeText(this, "Password not match! Try Again.", Toast.LENGTH_SHORT).show();
                    con_pass.requestFocus();
                    loading.dismiss();
                }
            }
        });

        signInBtn.setOnClickListener(view -> {
            finish();
        });

    }

    private void SignInWithEmailPass(String fullName, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            DBQuery.createUserData(email, fullName, mAuth.getCurrentUser().getUid(), "User", new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(SignUp.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(SignUp.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("FirebaseAuth", e.getMessage());
            loading.dismiss();
        });
    }

    private void _init() {
        signInBtn = findViewById(R.id.signInBtn);
        full_name = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        con_pass = findViewById(R.id.con_pass);
        signUpBtn = findViewById(R.id.signUpBtn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}