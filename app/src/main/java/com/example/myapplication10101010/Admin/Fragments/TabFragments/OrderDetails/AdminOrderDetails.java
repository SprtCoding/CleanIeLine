package com.example.myapplication10101010.Admin.Fragments.TabFragments.OrderDetails;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication10101010.FCM.FCMNotificationSender;
import com.example.myapplication10101010.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdminOrderDetails extends AppCompatActivity {
    private TextView close_btn, order_ID, order_status, pickup_address_tv, time,
            pickup_time_tv, delivery_address_tv, subTotal, delivery_fee, total_payment, services_type,
            edit_btn, save_btn, customer_provided_tv, detergent_tv, fabcon_tv;
    private EditText delivery_instructions_tv, wash_dry_kg, bed_linens_kg, comforter_kg;
    private CollectionReference bookedColRef, userTokenColRef, notificationColRef, priceListColRef;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String BookedID, user_name, user_id, user_token;
    private ProgressDialog loading;
    double sub_totals = 0, bedSheetPricePerKg = 0, comforterPricePerKg = 0, washDryPricePerKg = 0, fabconPrice = 0, liquidPrice = 0, powderPrice = 0, customerProvidedPrice = 0,
    total_wash_dry = 0;
    AlertDialog.Builder saveAlertBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_details);
        _init();

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        bookedColRef = db.collection("ServicesBooked");
        userTokenColRef = db.collection("USER_TOKEN");
        notificationColRef = db.collection("NOTIFICATIONS");
        priceListColRef = db.collection("PriceList");

        priceListColRef.document("Items")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        bedSheetPricePerKg = documentSnapshot.getDouble("BedSheetWeight");
                        comforterPricePerKg = documentSnapshot.getDouble("ComforterWeight");
                        washDryPricePerKg = documentSnapshot.getDouble("Per_kg");
                        fabconPrice = documentSnapshot.getDouble("Fabcon");
                        liquidPrice = documentSnapshot.getDouble("Liquid");
                        powderPrice = documentSnapshot.getDouble("Powder");
                        customerProvidedPrice = documentSnapshot.getDouble("CustomerProvided");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());

        setTextWatcher();

        loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Please wait...");
        loading.setCancelable(true);

        saveAlertBuilder = new AlertDialog.Builder(this);

        if(getIntent() != null) {
            BookedID = getIntent().getStringExtra("ID");
            user_name = getIntent().getStringExtra("UserName");
            user_id = getIntent().getStringExtra("UserID");
            order_ID.setText(BookedID);
        }

        retrieveData();

        edit_btn.setOnClickListener(view -> {
            wash_dry_kg.setEnabled(true);
            wash_dry_kg.requestFocus();
        });

        save_btn.setOnClickListener(view -> {
            saveAlertBuilder.setTitle("Saved")
                    .setMessage("It will notify " + user_name + ", Want to proceed?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        loading.show();
                        Handler handler = new Handler();
                        Runnable runnable = () -> {
                            if(!TextUtils.isEmpty(wash_dry_kg.getText().toString())) {
                                if (total_wash_dry != 0) {
                                    // Subtract the previous total_price_perKG
                                    sub_totals -= total_wash_dry;
                                }
                                total_wash_dry += washDryPricePerKg * Double.parseDouble(wash_dry_kg.getText().toString());
                                sub_totals += total_wash_dry;
                                Toast.makeText(this, ""+sub_totals, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AdminOrderDetails.this, "Enter Kilogram!", Toast.LENGTH_SHORT).show();
                            }

//                            Map<String, Object> map = new HashMap<>();
//                            map.put("OrderStatus", "Updated");
//                            map.put("Kilogram", Double.parseDouble(wash_dry_kg.getText().toString()));
//                            map.put("BedSheetWeight", Double.parseDouble(bed_linens_kg.getText().toString()));
//                            map.put("ComforterWeight", Double.parseDouble(comforter_kg.getText().toString()));
//
//                            bookedColRef.document(BookedID)
//                                    .update(map)
//                                    .addOnSuccessListener(unused -> {
//                                        Toast.makeText(this,  "Sent successfully to " + user_name + ".", Toast.LENGTH_SHORT).show();
//                                        sendNotification(user_id, 200);
//                                        setNotification();
//                                        loading.dismiss();
//                                    })
//                                    .addOnFailureListener(e -> Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
                        };
                        handler.postDelayed(runnable, 3000);
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                        dialogInterface.cancel();
                        loading.dismiss();
                    })
                    .show();
        });

        close_btn.setOnClickListener(view -> {
            finish();
        });
    }

    private void setTextWatcher() {
        wash_dry_kg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void retrieveData() {
        bookedColRef.document(BookedID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        String status = documentSnapshot.getString("OrderStatus");
                        String s_type = documentSnapshot.getString("ServicesType");
                        String pickup_address = documentSnapshot.getString("PickUpAddress");
                        String pickup_time = documentSnapshot.getString("PickUpTime");
                        String delivery_address = documentSnapshot.getString("DeliveryAddress");
                        String delivery_instructions = documentSnapshot.getString("DeliveryInstruction");
                        double sub_total = documentSnapshot.getDouble("SubTotal");
                        double d_fee = documentSnapshot.getDouble("DeliveryFee");
                        double total_pay = documentSnapshot.getDouble("TotalCost");
                        double wash_dry_kgs = documentSnapshot.getDouble("Kilogram");
                        double beedsheets_kgs = documentSnapshot.getDouble("BedSheetWeight");
                        double comforter_kgs = documentSnapshot.getDouble("ComforterWeight");
                        boolean customer_provided = documentSnapshot.getBoolean("Customer_Provided");
                        boolean powder_detergent = documentSnapshot.getBoolean("Powder");
                        boolean liquid_detergent = documentSnapshot.getBoolean("Liquid");
                        boolean fabcon = documentSnapshot.getBoolean("Fabcon");
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
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                            String formattedTime = timeFormat.format(timeOrdered);
                            time.setText("Today " + formattedTime);
                        } else {
                            // If it's not today, format as "dd-MM-yyyy hh:mm a"
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
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
                        services_type.setText(s_type);
                        wash_dry_kg.setText(String.valueOf(wash_dry_kgs));
                        bed_linens_kg.setText(String.valueOf(beedsheets_kgs));
                        comforter_kg.setText(String.valueOf(comforter_kgs));

                        // Get your drawable for the end (right side)
                        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawableEndCheck = getResources().getDrawable(R.drawable.check_circle_18);
                        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawableEndWrong = getResources().getDrawable(R.drawable.circle_xmark_18);

                        if(customer_provided) {
                            int tintColor = getResources().getColor(R.color.primary_color);
                            drawableEndCheck.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
                            customer_provided_tv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableEndCheck, null);

                            sub_totals += customerProvidedPrice;

                        }else {
                            int tintColor = getResources().getColor(R.color.red);
                            drawableEndWrong.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
                            customer_provided_tv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableEndWrong, null);
                        }
                        if(liquid_detergent) {
                            int tintColor = getResources().getColor(R.color.primary_color);
                            drawableEndCheck.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
                            detergent_tv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableEndCheck, null);

                            sub_totals += liquidPrice;

                        }else {
                            int tintColor = getResources().getColor(R.color.red);
                            drawableEndWrong.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
                            detergent_tv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableEndWrong, null);
                        }
                        if(powder_detergent) {
                            int tintColor = getResources().getColor(R.color.primary_color);
                            drawableEndCheck.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
                            detergent_tv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableEndCheck, null);

                            sub_totals += powderPrice;

                        }else {
                            int tintColor = getResources().getColor(R.color.red);
                            drawableEndWrong.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
                            detergent_tv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableEndWrong, null);
                        }
                        if(fabcon) {
                            int tintColor = getResources().getColor(R.color.primary_color);
                            drawableEndCheck.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
                            fabcon_tv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableEndCheck, null);

                            sub_totals += fabconPrice;

                        }else {
                            int tintColor = getResources().getColor(R.color.red);
                            drawableEndWrong.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
                            fabcon_tv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableEndWrong, null);
                        }

                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void setNotification() {
        // Get the current date and time
        Date currentDate = new Date();

        Map<String, Object> map = new HashMap<>();
        map.put("to", user_id);
        map.put("from", mAuth.getCurrentUser().getUid());
        map.put("Details", "Your transaction is being updated by admin.\nYour total payment now is " + convertToPhilippinePesoD(200));
        map.put("Date", currentDate);

        notificationColRef.add(map)
                .addOnSuccessListener(documentReference -> {
                    String id = documentReference.getId();
                    Map<String, Object> idValue = new HashMap<>();
                    idValue.put("NotificationID", id);
                    notificationColRef
                            .document(id)
                            .update(map);
                })
                .addOnFailureListener(e -> Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void sendNotification(String ID, double total_pay) {
        userTokenColRef = db.collection("USER_TOKEN");

        userTokenColRef.document(ID).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()) {
                user_token = documentSnapshot.getString("token");
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    FCMNotificationSender.sendNotification(
                            this,
                            user_token,
                            "Clean In Line",
                            "Your transaction is being updated by admin.\nYour total payment now is " + convertToPhilippinePesoD(total_pay)
                    );
                }, 3000);
            }
        }).addOnFailureListener(e -> Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void _init() {
        detergent_tv = findViewById(R.id.detergent_tv);
        fabcon_tv = findViewById(R.id.fabcon_tv);
        customer_provided_tv = findViewById(R.id.customer_provided_tv);
        bed_linens_kg = findViewById(R.id.bed_linens_kg);
        comforter_kg = findViewById(R.id.comforter_kg);
        save_btn = findViewById(R.id.save_btn);
        edit_btn = findViewById(R.id.edit_btn);
        services_type = findViewById(R.id.services_type);
        wash_dry_kg = findViewById(R.id.wash_dry_kg);
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