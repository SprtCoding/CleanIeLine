package com.example.myapplication10101010.BottomFragments.CheckOuts;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication10101010.R;
import com.example.myapplication10101010.UserDashboard;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Checkout extends AppCompatActivity {
    private TextView select_pickup_time, subTotal, delivery_fee, total_payment;
    private EditText select_pickup_address, select_delivery_address, delivery_instructions;
    private MaterialButton proceed_checkout;
    private ImageView back_btn;
    private CollectionReference itemsCollection;
    private FirebaseFirestore db;
    private String bookedID;
    private double deliveryFee = 40, total_cost = 0, cost = 0;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        _init();

        db = FirebaseFirestore.getInstance();
        itemsCollection = db.collection("ServicesBooked");

        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setTitle("Loading");
        loading.setMessage("Please wait...");

        if(getIntent() != null) {
            bookedID = getIntent().getStringExtra("BookedID");

            itemsCollection.document(bookedID).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.exists()) {
                            cost = documentSnapshot.getDouble("SubTotal");
                            subTotal.setText(convertToPhilippinePesoD(cost));
                            delivery_fee.setText(convertToPhilippinePesoD(deliveryFee));
                            total_cost = cost + deliveryFee;
                            total_payment.setText(convertToPhilippinePesoD(total_cost));
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
        }

        select_pickup_time.setOnClickListener(view -> openTimeDialog());

        proceed_checkout.setOnClickListener(view -> {
            loading.show();
            String pickup_address = select_pickup_address.getText().toString();
            String pickup_time = select_pickup_time.getText().toString();
            String delivery_instruction = delivery_instructions.getText().toString();
            String delivery_address = select_delivery_address.getText().toString();

            if(pickup_address.equals("") || TextUtils.isEmpty(pickup_address)) {
                select_pickup_address.setError("Required!");
                select_pickup_address.requestFocus();
                loading.dismiss();
            } else if (pickup_time.equals("Select Pickup Time")) {
                Toast.makeText(this, "Required!", Toast.LENGTH_SHORT).show();
                loading.dismiss();
            } else if (delivery_instruction.equals("") || TextUtils.isEmpty(delivery_instruction)) {
                Toast.makeText(this, "Required!", Toast.LENGTH_SHORT).show();
                loading.dismiss();
            } else if (delivery_address.equals("") || TextUtils.isEmpty(delivery_address)) {
                select_delivery_address.setError("Required!");
                select_delivery_address.requestFocus();
                loading.dismiss();
            } else {
                updateOrder(pickup_address, pickup_time, delivery_instruction, delivery_address);
            }
        });

        back_btn.setOnClickListener(view -> {
            Intent i = new Intent(this, UserDashboard.class);
            startActivity(i);
            finish();
        });

    }

    private void updateOrder(String pickupAddress, String pickupTime, String deliveryInstruction, String deliveryAddress) {
        Map<String, Object> map = new HashMap<>();
        map.put("PickUpAddress", pickupAddress);
        map.put("PickUpTime", pickupTime);
        map.put("DeliveryInstruction", deliveryInstruction);
        map.put("DeliveryAddress", deliveryAddress);

        itemsCollection.document(bookedID)
                .update(map)
                .addOnSuccessListener(unused -> {
                    Intent i = new Intent(this, Payment.class);
                    i.putExtra("Total_Cost", total_cost);
                    i.putExtra("DeliveryFee", deliveryFee);
                    i.putExtra("SubTotal", cost);
                    i.putExtra("BookedID", bookedID);
                    startActivity(i);
                    loading.dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                });
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

    private void openTimeDialog() {

        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute1) -> {
                    Calendar selectedTime = Calendar.getInstance();
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedTime.set(Calendar.MINUTE, minute1);

                    // Format the selected time
                    String formattedTime = DateFormat.format("hh:mm aa", selectedTime).toString();
                    select_pickup_time.setText(formattedTime);
                },
                hour,
                minute,
                false // Set to true for 24-hour format, false for AM/PM format
        );

        timePickerDialog.show();
    }

    private void _init() {
        select_pickup_address = findViewById(R.id.select_pickup_address);
        select_pickup_time = findViewById(R.id.select_pickup_time);
        subTotal = findViewById(R.id.subTotal);
        delivery_fee = findViewById(R.id.delivery_fee);
        total_payment = findViewById(R.id.total_payment);
        back_btn = findViewById(R.id.back_btn);
        proceed_checkout = findViewById(R.id.proceed_checkout);
        select_delivery_address = findViewById(R.id.select_delivery_address);
        delivery_instructions = findViewById(R.id.delivery_instructions);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}