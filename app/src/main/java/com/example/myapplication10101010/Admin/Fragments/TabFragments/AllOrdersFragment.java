package com.example.myapplication10101010.Admin.Fragments.TabFragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myapplication10101010.Admin.Fragments.Adapters.AllOrdersAdapter;
import com.example.myapplication10101010.Admin.Fragments.Model.ServicesBookedList;
import com.example.myapplication10101010.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllOrdersFragment extends Fragment {
    private RecyclerView all_orders_rv;
    private LinearLayout no_data_ll;
    private CollectionReference bookedColRef;
    private FirebaseFirestore db;
    private AllOrdersAdapter allOrdersAdapter;
    private List<ServicesBookedList> servicesBookedLists;
    ProgressDialog _getDataLoading;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_all_orders, container, false);
        _init();

        _getDataLoading = new ProgressDialog(getContext());
        _getDataLoading.setMessage("Loading data...");
        _getDataLoading.show();

        servicesBookedLists = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        bookedColRef = db.collection("ServicesBooked");

        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        all_orders_rv.setHasFixedSize(true);
        all_orders_rv.setLayoutManager(llm);

        bookedColRef
                .whereIn("OrderStatus", Arrays.asList("Processing", "Order Accepted"))
                .addSnapshotListener((value, error) -> {
                    if(value != null && error == null) {
                        if(!value.isEmpty()) {
                            servicesBookedLists.clear();
                            for(QueryDocumentSnapshot doc : value) {
                                double total = 0;
                                double kg = 0;
                                double sTotal = 0;
                                double dFee = 0;
                                if(doc.getDouble("TotalCost") != null) {
                                    total = doc.getDouble("TotalCost");
                                }else {
                                    total = 0;
                                }
                                if(doc.getDouble("Kilogram") != null) {
                                    kg = doc.getDouble("Kilogram");
                                }else {
                                    kg = 0;
                                }
                                if(doc.getDouble("SubTotal") != null) {
                                    sTotal = doc.getDouble("SubTotal");
                                }else {
                                    sTotal = 0;
                                }
                                if(doc.getDouble("DeliveryFee") != null) {
                                    dFee = doc.getDouble("DeliveryFee");
                                }else {
                                    dFee = 0;
                                }
                                servicesBookedLists.add(new ServicesBookedList(
                                        doc.getString("ServicesType"),
                                        doc.getString("UserID"),
                                        doc.getString("OrderStatus"),
                                        doc.getString("BookedID"),
                                        doc.getString("PickUpAddress"),
                                        doc.getString("PickUpTime"),
                                        doc.getString("DeliveryInstruction"),
                                        doc.getString("DeliveryAddress"),
                                        doc.getString("PaymentMethod"),
                                        doc.getString("ContactNo"),
                                        total,
                                        kg,
                                        sTotal,
                                        dFee,
                                        doc.getDate("DateTimePlaced")
                                ));
                            }
                            _getDataLoading.dismiss();
                            allOrdersAdapter = new AllOrdersAdapter(getContext(), servicesBookedLists);
                            all_orders_rv.setAdapter(allOrdersAdapter);
                        }else {
                            _getDataLoading.dismiss();
                            no_data_ll.setVisibility(View.VISIBLE);
                            all_orders_rv.setVisibility(View.GONE);
                        }
                    }else {
                        Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        return v;
    }

    private void _init() {
        all_orders_rv = v.findViewById(R.id.all_orders_rv);
        no_data_ll = v.findViewById(R.id.no_data_ll);
    }
}