package com.example.myapplication10101010.BottomFragments.CheckOuts;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication10101010.R;
import com.example.myapplication10101010.UserDashboard;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class OrderDetails extends AppCompatActivity {
    private TextView close_btn, order_ID, order_status, pickup_address_tv, time,
            pickup_time_tv, delivery_address_tv, subTotal, delivery_fee, total_payment;
    private EditText delivery_instructions_tv;
    private CollectionReference bookedColRef;
    private FirebaseFirestore db;
    private String BookedID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        _init();

        db = FirebaseFirestore.getInstance();
        bookedColRef = db.collection("ServicesBooked");

        if(getIntent() != null) {
            BookedID = getIntent().getStringExtra("ID");
            order_ID.setText(BookedID);
        }

        retrieveData();

        close_btn.setOnClickListener(view -> {
            finish();
        });

    }

    @SuppressLint("SetTextI18n")
    private void retrieveData() {
        bookedColRef.document(BookedID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        String status = documentSnapshot.getString("OrderStatus");
                        String pickup_address = documentSnapshot.getString("PickUpAddress");
                        String pickup_time = documentSnapshot.getString("PickUpTime");
                        String delivery_address = documentSnapshot.getString("DeliveryAddress");
                        String delivery_instructions = documentSnapshot.getString("DeliveryInstruction");
                        double sub_total = documentSnapshot.getDouble("SubTotal");
                        double d_fee = documentSnapshot.getDouble("DeliveryFee");
                        double total_pay = documentSnapshot.getDouble("TotalCost");
                        Date timeOrdered = documentSnapshot.getDate("DateTimePlaced");

                        if(status.equals("Order Completed")) {
                            order_status.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(97, 178, 236)));
                        }else if(status.equals("Processing")) {
                            order_status.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(164, 164, 164)));
                        }else if(status.equals("Order Accepted")) {
                            order_status.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(49, 149, 91)));
                        }else if(status.equals("Rejected")) {
                            order_status.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(191, 64, 64)));
                        }

                        // Check if the date is today
                        Calendar calendar = Calendar.getInstance();
                        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                        calendar.setTime(timeOrdered);
                        int timeDay = calendar.get(Calendar.DAY_OF_MONTH);

                        if (currentDay == timeDay) {
                            // If it's today, format as "Today hh:mm a"
                            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                            String formattedTime = timeFormat.format(timeOrdered);
                            time.setText("Today " + formattedTime);
                        } else {
                            // If it's not today, format as "dd-MM-yyyy hh:mm a"
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                            String formattedDateTime = dateFormat.format(timeOrdered);
                            time.setText(formattedDateTime);
                        }
                        pickup_address_tv.setText(pickup_address);
                        order_status.setText(status);
                        pickup_time_tv.setText(pickup_time);
                        delivery_address_tv.setText(delivery_address);
                        subTotal.setText(convertToPhilippinePesoD(sub_total));
                        delivery_fee.setText(convertToPhilippinePesoD(d_fee));
                        total_payment.setText(convertToPhilippinePesoD(total_pay));
                        delivery_instructions_tv.setText(delivery_instructions);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void _init() {
        close_btn = findViewById(R.id.close_btn);
        order_ID = findViewById(R.id.order_ID);
        order_status = findViewById(R.id.order_status);
        pickup_address_tv = findViewById(R.id.pickup_address_tv);
        time = findViewById(R.id.time);
        pickup_time_tv = findViewById(R.id.pickup_time_tv);
        delivery_address_tv = findViewById(R.id.delivery_address_tv);
        subTotal = findViewById(R.id.subTotal);
        delivery_fee = findViewById(R.id.delivery_fee);
        total_payment = findViewById(R.id.total_payment);
        delivery_instructions_tv = findViewById(R.id.delivery_instructions_tv);
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
}