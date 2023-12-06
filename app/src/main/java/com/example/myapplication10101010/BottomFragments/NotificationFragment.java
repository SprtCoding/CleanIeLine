package com.example.myapplication10101010.BottomFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication10101010.Admin.Fragments.Adapters.NotificationAdapter;
import com.example.myapplication10101010.Admin.Fragments.Model.NotificationList;
import com.example.myapplication10101010.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {
    View v;
    private RecyclerView notificationRV;
    private LinearLayout no_data_ll;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private CollectionReference notificationColRef;
    NotificationAdapter notificationAdapter;
    List<NotificationList> notificationLists;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_notification, container, false);
        _init();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        notificationLists = new ArrayList<>();

        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        notificationRV.setHasFixedSize(true);
        notificationRV.setLayoutManager(llm);

        db = FirebaseFirestore.getInstance();
        notificationColRef = db.collection("NOTIFICATIONS");

        if(mUser != null) {
            notificationColRef.whereEqualTo("to", mUser.getUid())
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

                                notificationAdapter = new NotificationAdapter(getContext(), notificationLists);
                                notificationRV.setAdapter(notificationAdapter);
                            }else {
                                notificationRV.setVisibility(View.GONE);
                                no_data_ll.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        return v;
    }

    private void _init() {
        notificationRV = v.findViewById(R.id.notif_rv);
        no_data_ll = v.findViewById(R.id.no_data_ll);
    }
}