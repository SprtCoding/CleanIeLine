package com.example.myapplication10101010.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myapplication10101010.Admin.Fragments.Adapters.NotificationAdapter;
import com.example.myapplication10101010.Admin.Fragments.Model.NotificationList;
import com.example.myapplication10101010.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminNotification extends AppCompatActivity {
    private ImageView back_btn;
    private RecyclerView notification_rv;
    private LinearLayout no_data_ll;
    private FirebaseFirestore db;
    private CollectionReference notificationColRef;
    FirebaseAuth mAuth;
    NotificationAdapter notificationAdapter;
    List<NotificationList> notificationLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notification);
        MultiDex.install(this);
        _init();

        mAuth = FirebaseAuth.getInstance();
        notificationLists = new ArrayList<>();

        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        notification_rv.setHasFixedSize(true);
        notification_rv.setLayoutManager(llm);

        db = FirebaseFirestore.getInstance();
        notificationColRef = db.collection("NOTIFICATIONS");

        notificationColRef.whereEqualTo("to", mAuth.getCurrentUser().getUid())
                        .addSnapshotListener((value, error) -> {
                            if(error == null && value != null) {
                                if(!value.isEmpty()) {
                                    notificationLists.clear();

                                    for(QueryDocumentSnapshot doc : value) {
                                        notificationLists.add(new NotificationList(
                                                doc.getString("from"),
                                                doc.getString("to"),
                                                doc.getString("Details"),
                                                doc.getDate("Date")
                                        ));
                                    }

                                    notificationAdapter = new NotificationAdapter(this, notificationLists);
                                    notification_rv.setAdapter(notificationAdapter);
                                }else {
                                    notification_rv.setVisibility(View.GONE);
                                    no_data_ll.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

        back_btn.setOnClickListener(view -> finish());

    }

    private void _init() {
        back_btn = findViewById(R.id.back_btn);
        notification_rv = findViewById(R.id.notification_rv);
        no_data_ll = findViewById(R.id.no_data_ll);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}