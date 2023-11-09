package com.example.myapplication10101010.BottomFragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication10101010.Admin.Fragments.Adapters.MyOrdersAdapter;
import com.example.myapplication10101010.Admin.Fragments.Model.ServicesBookedList;
import com.example.myapplication10101010.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {
    private RecyclerView orders_rv;
    private LinearLayout no_data_ll;
    List<ServicesBookedList> servicesBookedLists;
    MyOrdersAdapter myOrdersAdapter;
    private CollectionReference servicesColRef;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ProgressDialog loading;
    FirebaseUser _user;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_orders, container, false);
        _init();

        loading = new ProgressDialog(getContext());
        loading.setCancelable(false);
        loading.setMessage("Loading data...");
        loading.show();

        servicesBookedLists = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        servicesColRef = db.collection("ServicesBooked");

        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        orders_rv.setHasFixedSize(true);
        orders_rv.setLayoutManager(llm);

        mAuth = FirebaseAuth.getInstance();
        _user = mAuth.getCurrentUser();

        if(_user != null) {
            servicesColRef.whereEqualTo("UserID", _user.getUid())
                    .addSnapshotListener((value, error) -> {
                        if(error == null && value != null) {
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
                                myOrdersAdapter = new MyOrdersAdapter(getContext(), servicesBookedLists);
                                orders_rv.setAdapter(myOrdersAdapter);
                                loading.dismiss();
                            }else {
                                no_data_ll.setVisibility(View.VISIBLE);
                                orders_rv.setVisibility(View.GONE);
                                loading.dismiss();
                            }
                        }else {
                            Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                        }
                    });
        }

        return v;
    }

    private void _init() {
        orders_rv = v.findViewById(R.id.orders_rv);
        no_data_ll = v.findViewById(R.id.no_data_ll);
    }
}