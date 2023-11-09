package com.example.myapplication10101010.BottomFragments.CheckOuts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication10101010.BottomFragments.Services.DropOffServices;
import com.example.myapplication10101010.FCM.FCMNotificationSender;
import com.example.myapplication10101010.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Payment extends AppCompatActivity {
    private LinearLayout cod_ll;
    private EditText mobile_no_et;
    private RadioGroup payment_rg;
    private MaterialButton place_order;
    private TextView subTotal, delivery_fee, total_payment;
    private CollectionReference itemsCollection, userTokenColRef, userColRef, notificationColRef;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String deliveryType, BookedID, userToken, myName, admin_ID = "aoj3JnaYwTe35QlaqGXYwW1Nrc82";
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        _init();

        mAuth = FirebaseAuth.getInstance();

        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setTitle("Place Order");
        loading.setMessage("Please wait...");

        if(getIntent() != null) {
            BookedID = getIntent().getStringExtra("BookedID");
            subTotal.setText(convertToPhilippinePesoD(getIntent().getDoubleExtra("SubTotal", 0)));
            delivery_fee.setText(convertToPhilippinePesoD(getIntent().getDoubleExtra("DeliveryFee", 0)));
            total_payment.setText(convertToPhilippinePesoD(getIntent().getDoubleExtra("Total_Cost", 0)));
        }

        db = FirebaseFirestore.getInstance();
        itemsCollection = db.collection("ServicesBooked");
        userColRef = db.collection("USERS");
        notificationColRef = db.collection("NOTIFICATIONS");

        userColRef.document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        myName = documentSnapshot.getString("NAME");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show());

        place_order.setOnClickListener(view -> {
            loading.show();
            int selectedRadioButtonId = payment_rg.getCheckedRadioButtonId();

            if (selectedRadioButtonId != -1) {
                // Find the selected radio button
                RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);

                // Get the text of the selected radio button
                deliveryType = selectedRadioButton.getText().toString();

                updateOrder(deliveryType);
            } else {
                // No radio button is selected, handle this case as needed
                Toast.makeText(Payment.this, "No option selected", Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        });

        payment_rg.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            // Check which radio button is selected
            if (checkedId == R.id.cod) {
                // Show the layout when radioOption1 is selected
                cod_ll.setVisibility(View.VISIBLE);
            } else {
                // Hide the layout when any other radio button is selected
                cod_ll.setVisibility(View.GONE);
            }
        });

    }

    private void updateOrder(String deliveryType) {
        // Get the current date and time
        Date currentDateTime = new Date();
        // @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        // String formattedTime = timeFormat.format(currentTime);

        Map<String, Object> map = new HashMap<>();
        map.put("PaymentMethod", deliveryType);
        map.put("ContactNo", mobile_no_et.getText().toString());
        map.put("OrderStatus", "Processing");
        map.put("DateTimePlaced", currentDateTime);

        itemsCollection.document(BookedID)
                .update(map)
                .addOnSuccessListener(unused -> {
                    sendNotification(admin_ID, myName);
                    setNotification();
                    showOrderPlaceDialog();
                    loading.dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                });
    }

    private void setNotification() {
        // Get the current date and time
        Date currentDate = new Date();

        Map<String, Object> map = new HashMap<>();
        map.put("to", admin_ID);
        map.put("from", mAuth.getCurrentUser().getUid());
        map.put("Details", "Placed Transactions.");
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

    private void sendNotification(String ID, String name) {
        userTokenColRef = db.collection("USER_TOKEN");

        userTokenColRef.document(ID).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()) {
                userToken = documentSnapshot.getString("token");
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    FCMNotificationSender.sendNotification(
                            this,
                            userToken,
                            "Clean In Line",
                            name + " Placed transactions."
                    );
                }, 3000);
            }
        }).addOnFailureListener(e -> Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @SuppressLint("SetTextI18n")
    private void showOrderPlaceDialog() {
        View placeOrderDialog = LayoutInflater.from(Payment.this).inflate(R.layout.place_order_dialog, null);
        AlertDialog.Builder placeOrderDialogBuilder = new AlertDialog.Builder(Payment.this);

        placeOrderDialogBuilder.setView(placeOrderDialog);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView id_number = placeOrderDialog.findViewById(R.id.id_numbers);
        MaterialButton view_details = placeOrderDialog.findViewById(R.id.view_details);

        final AlertDialog placeOrderDialogs = placeOrderDialogBuilder.create();

        id_number.setText("#"+BookedID);

        view_details.setOnClickListener(view -> {
            Intent i = new Intent(Payment.this, OrderDetails.class);
            i.putExtra("ID", BookedID);
            startActivity(i);
            finish();
        });

        Objects.requireNonNull(placeOrderDialogs.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        placeOrderDialogs.setCanceledOnTouchOutside(false);

        placeOrderDialogs.show();
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

    private void _init() {
        mobile_no_et = findViewById(R.id.mobile_no_et);
        cod_ll = findViewById(R.id.cod_ll);
        subTotal = findViewById(R.id.subTotal);
        delivery_fee = findViewById(R.id.delivery_fee);
        total_payment = findViewById(R.id.total_payment);
        place_order = findViewById(R.id.place_order);
        payment_rg = findViewById(R.id.payment_rg);
    }
}