package com.example.myapplication10101010.Admin.Fragments.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication10101010.Admin.Fragments.Model.ServicesBookedList;
import com.example.myapplication10101010.BottomFragments.CheckOuts.OrderDetails;
import com.example.myapplication10101010.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder>{
    Context context;
    List<ServicesBookedList> servicesBookedLists;

    public MyOrdersAdapter(Context context, List<ServicesBookedList> servicesBookedLists) {
        this.context = context;
        this.servicesBookedLists = servicesBookedLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_orders_layout,parent,false);
        return new MyOrdersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServicesBookedList bookedList = servicesBookedLists.get(position);

        CollectionReference userColRef = FirebaseFirestore.getInstance().collection("USERS");

        holder.order_ID.setText(bookedList.getBookedID());
        holder.order_status.setText(bookedList.getOrderStatus());
        holder.total_payment.setText(convertToPhilippinePesoD(bookedList.getTotalCost()));
        holder.address.setText(bookedList.getPickUpAddress());
        holder.number.setText(bookedList.getContactNo());

        if(bookedList.getOrderStatus().equals("Order Completed")) {
            holder.order_status.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(97, 178, 236)));
        }else if(bookedList.getOrderStatus().equals("Processing")) {
            holder.order_status.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(164, 164, 164)));
        }else if(bookedList.getOrderStatus().equals("Order Accepted")) {
            holder.order_status.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(49, 149, 91)));
        }else if(bookedList.getOrderStatus().equals("Rejected")) {
            holder.order_status.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(191, 64, 64)));
        }

        userColRef.document(bookedList.getUserID())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("NAME");
                        holder.fullname.setText(name);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show());

        holder.view_details.setOnClickListener(view -> {
            Intent i = new Intent(context, OrderDetails.class);
            i.putExtra("ID", bookedList.getBookedID());
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
        private TextView order_ID, order_status, total_payment, fullname, number, address;
        private MaterialButton view_details;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            order_ID = itemView.findViewById(R.id.order_ID);
            order_status = itemView.findViewById(R.id.order_status);
            total_payment = itemView.findViewById(R.id.total_payment);
            fullname = itemView.findViewById(R.id.fullname);
            number = itemView.findViewById(R.id.number);
            address = itemView.findViewById(R.id.address);
            view_details = itemView.findViewById(R.id.view_details);
        }
    }
}
