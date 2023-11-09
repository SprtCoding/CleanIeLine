package com.example.myapplication10101010.Admin.Fragments.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication10101010.Admin.Fragments.Model.ServicesBookedList;
import com.example.myapplication10101010.Admin.Fragments.TabFragments.OrderDetails.AdminOrderDetails;
import com.example.myapplication10101010.BottomFragments.CheckOuts.OrderDetails;
import com.example.myapplication10101010.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AllOrdersAdapter extends RecyclerView.Adapter<AllOrdersAdapter.ViewHolder>{
    Context context;
    List<ServicesBookedList> servicesBookedLists;
    private CollectionReference itemsCollection;
    private FirebaseFirestore db;
    private ProgressDialog loading;
    AlertDialog.Builder rejectAlertBuilder;

    public AllOrdersAdapter(Context context, List<ServicesBookedList> servicesBookedLists) {
        this.context = context;
        this.servicesBookedLists = servicesBookedLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_orders,parent,false);
        return new AllOrdersAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServicesBookedList bookedList = servicesBookedLists.get(position);

        rejectAlertBuilder = new AlertDialog.Builder(context);

        loading = new ProgressDialog(context);
        loading.setTitle("Loading");
        loading.setMessage("Please wait...");

        db = FirebaseFirestore.getInstance();
        itemsCollection = db.collection("ServicesBooked");

        CollectionReference userColRef = FirebaseFirestore.getInstance().collection("USERS");

        userColRef.document(bookedList.getUserID())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("NAME");
                        holder.fullname.setText(name);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show());

        holder.order_ID.setText(bookedList.getBookedID());
        holder.services_type.setText(bookedList.getServicesType());
        holder.total_payment.setText(convertToPhilippinePesoD(bookedList.getTotalCost()));
        holder.address.setText(bookedList.getPickUpAddress());
        holder.number.setText(bookedList.getContactNo());

        if(bookedList.getOrderStatus().equals("Order Accepted")) {
            holder.save_button.setText("Done");
            holder.save_button.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(49, 149, 91)));

            holder.reject_button.setVisibility(View.GONE);

            holder.save_button.setOnClickListener(view -> {
                loading.show();
                Map<String, Object> map = new HashMap<>();
                map.put("OrderStatus", "Order Completed");

                itemsCollection.document(bookedList.getBookedID())
                        .update(map)
                        .addOnSuccessListener(unused -> {
                            loading.dismiss();
                        })
                        .addOnFailureListener(e -> Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
            });
        }else if(bookedList.getOrderStatus().equals("Processing") || bookedList.getOrderStatus().equals("Updated")) {
            holder.save_button.setText("Accept");
            holder.save_button.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(33, 157, 206)));

            holder.save_button.setOnClickListener(view -> {
                loading.show();
                Map<String, Object> map = new HashMap<>();
                map.put("OrderStatus", "Order Accepted");

                itemsCollection.document(bookedList.getBookedID())
                        .update(map)
                        .addOnSuccessListener(unused -> {
                            loading.dismiss();
                        })
                        .addOnFailureListener(e -> Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
            });
        }else if(bookedList.getOrderStatus().equals("Order Completed")) {
            holder.save_button.setVisibility(View.GONE);
            holder.reject_button.setVisibility(View.GONE);
        }else if(bookedList.getOrderStatus().equals("Rejected")) {
            holder.save_button.setVisibility(View.GONE);
            holder.reject_button.setVisibility(View.GONE);
        }

        holder.reject_button.setOnClickListener(view -> {

            rejectAlertBuilder.setTitle("Reject")
                    .setMessage("Are you sure want to reject " + holder.fullname.getText().toString() + "?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        loading.show();
                        Handler handler = new Handler();
                        Runnable runnable = () -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("OrderStatus", "Rejected");

                            itemsCollection.document(bookedList.getBookedID())
                                    .update(map)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(context, holder.fullname.getText().toString() + " rejected successfully!", Toast.LENGTH_SHORT).show();
                                        loading.dismiss();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
                        };
                        handler.postDelayed(runnable, 3000);
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                        dialogInterface.cancel();
                        loading.dismiss();
                    })
                    .show();
        });

        holder.view_details.setOnClickListener(view -> {
            Intent i = new Intent(context, AdminOrderDetails.class);
            i.putExtra("ID", bookedList.getBookedID());
            i.putExtra("UserID", bookedList.getUserID());
            i.putExtra("UserName", holder.fullname.getText().toString());
            context.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return servicesBookedLists.size();
    }
    public static String convertToPhilippinePesoD(double amount) {
        // Create a Locale for the Philippines
        Locale philippinesLocale = new Locale("en", "PH");

        // Create a NumberFormat instance for the Philippine Peso currency
        NumberFormat philippinePesoFormat = NumberFormat.getCurrencyInstance(philippinesLocale);

        // Set the currency code to PHP
        philippinePesoFormat.setCurrency(Currency.getInstance("PHP"));

        // Format the numeric amount to Philippine Peso format
        return philippinePesoFormat.format(amount);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView order_ID, services_type, total_payment, fullname, number, address;
        MaterialButton view_details, save_button, reject_button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            order_ID = itemView.findViewById(R.id.order_ID);
            services_type = itemView.findViewById(R.id.services_type);
            total_payment = itemView.findViewById(R.id.total_payment);
            fullname = itemView.findViewById(R.id.fullname);
            number = itemView.findViewById(R.id.number);
            address = itemView.findViewById(R.id.address);
            view_details = itemView.findViewById(R.id.view_details);
            save_button = itemView.findViewById(R.id.save_button);
            reject_button = itemView.findViewById(R.id.reject_button);
        }
    }
}
