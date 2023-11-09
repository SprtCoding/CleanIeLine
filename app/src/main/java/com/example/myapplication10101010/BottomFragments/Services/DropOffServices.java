package com.example.myapplication10101010.BottomFragments.Services;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication10101010.BottomFragments.CheckOuts.Checkout;
import com.example.myapplication10101010.BottomFragments.CheckOuts.Payment;
import com.example.myapplication10101010.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DropOffServices extends AppCompatActivity {
    private ImageView back_btn;
    private TextView cost;
    private TextView laundry_weight, bed_sheet_weight, comforter_blanket_weight;
    private TextView total_selected_items;
    private MaterialButton proceed_checkout;
    private SwitchCompat customer_provided, select_detergent, select_fabcon, garment_shuffle,
            seperate_white_garments, liquid, powder;
    private LinearLayout add_kg, select_detergent_ll, add_beed_sheet_kg, add_comforter_blanket_kg;
    private EditText kg;
    private double total_cost = 0, price_per_kg = 0, CustomerProvidedPrice = 0, SelectFabconPrice = 0,
            total_price_perKG = 0, LiquidPrice = 0, PowderPrice = 0, bedSheetPrice = 0, blanketPrice = 0, total_bedSheetPrice_perKG = 0
            , total_blanketPrice_perKG = 0;
    private int selectedItemCount = 0;
    private final Map<SwitchCompat, Double> itemPrices = new HashMap<>();
    private ArrayList<String> selectedItemsList = new ArrayList<>();
    private ProgressDialog loading;
    private FirebaseAuth mAuth;
    private DocumentReference priceDocRef;
    private FirebaseFirestore db;
    private double deliveryFee = 40, total_payment = 0;
    double finalKg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_off_services);
        _init();

        db = FirebaseFirestore.getInstance();
        priceDocRef = db.collection("PriceList").document("Items");

        priceDocRef.addSnapshotListener((value, error) -> {
            if(error == null && value != null) {
                if(value.exists()) {
                    if(value.getDouble("Per_kg") != null) {
                        price_per_kg = value.getDouble("Per_kg");
                    }
                    if(value.getDouble("BedSheetWeight") != null) {
                        bedSheetPrice = value.getDouble("BedSheetWeight");
                    }
                    if(value.getDouble("ComforterWeight") != null) {
                        blanketPrice = value.getDouble("ComforterWeight");
                    }
                    if(value.getDouble("CustomerProvided") != null) {
                        CustomerProvidedPrice = value.getDouble("CustomerProvided");
                    }
                    if(value.getDouble("Liquid") != null) {
                        LiquidPrice = value.getDouble("Liquid");
                    }
                    if(value.getDouble("Powder") != null) {
                        PowderPrice = value.getDouble("Powder");
                    }
                    if(value.getDouble("Fabcon") != null) {
                        SelectFabconPrice = value.getDouble("Fabcon");
                    }

                    // Initialize the item prices
                    itemPrices.put(customer_provided, CustomerProvidedPrice);
                    itemPrices.put(liquid, LiquidPrice);
                    itemPrices.put(powder, PowderPrice);
                    itemPrices.put(garment_shuffle, 0.0);
                    itemPrices.put(seperate_white_garments, 0.0);
                    itemPrices.put(select_fabcon, SelectFabconPrice);
                }
            }else {
                Toast.makeText(this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setTitle("Loading");
        loading.setMessage("Please wait...");

        mAuth = FirebaseAuth.getInstance();

        back_btn.setOnClickListener(view -> finish());

        cost.setText(convertToPhilippinePesoD(total_cost));

        add_kg.setOnClickListener(view -> addKgDialog(laundry_weight, price_per_kg));

        add_beed_sheet_kg.setOnClickListener(view -> addBedSheetKgDialog(bed_sheet_weight, bedSheetPrice));

        add_comforter_blanket_kg.setOnClickListener(view -> addBlanketKgDialog(comforter_blanket_weight, blanketPrice));

        select_detergent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // The "select_detergent" switch is checked, so make the layout visible
                select_detergent_ll.setVisibility(View.VISIBLE);
            } else {
                // The "select_detergent" switch is unchecked, so hide the layout
                select_detergent_ll.setVisibility(View.GONE);
            }
        });

        // Add listeners for each SwitchCompat
        customer_provided.setOnCheckedChangeListener((buttonView, isChecked) -> updateSelectedItemsCount(customer_provided, isChecked));
        liquid.setOnCheckedChangeListener((buttonView, isChecked) -> updateSelectedItemsCount(liquid, isChecked));
        powder.setOnCheckedChangeListener((buttonView, isChecked) -> updateSelectedItemsCount(powder, isChecked));
        select_fabcon.setOnCheckedChangeListener((buttonView, isChecked) -> updateSelectedItemsCount(select_fabcon, isChecked));
        garment_shuffle.setOnCheckedChangeListener((buttonView, isChecked) -> updateSelectedItemsCount(garment_shuffle, isChecked));
        seperate_white_garments.setOnCheckedChangeListener((buttonView, isChecked) -> updateSelectedItemsCount(seperate_white_garments, isChecked));

        proceed_checkout.setOnClickListener(view -> {
            loading.show();
            addSelectedItems();
        });
    }

    private void _init() {
        add_beed_sheet_kg = findViewById(R.id.add_beed_sheet_kg);
        add_comforter_blanket_kg = findViewById(R.id.add_comforter_blanket_kg);
        bed_sheet_weight = findViewById(R.id.bed_sheet_weight);
        comforter_blanket_weight = findViewById(R.id.comforter_blanket_weight);
        liquid = findViewById(R.id.liquid);
        powder = findViewById(R.id.powder);
        select_detergent_ll = findViewById(R.id.select_detergent_ll);
        back_btn = findViewById(R.id.back_btn);
        cost = findViewById(R.id.cost);
        add_kg = findViewById(R.id.add_kg);
        laundry_weight = findViewById(R.id.laundry_weight);
        total_selected_items = findViewById(R.id.total_selected_items);
        customer_provided = findViewById(R.id.customer_provided);
        select_detergent = findViewById(R.id.select_detergent);
        select_fabcon = findViewById(R.id.select_fabcon);
        garment_shuffle = findViewById(R.id.garment_shuffle);
        seperate_white_garments = findViewById(R.id.seperate_white_garments);
        proceed_checkout = findViewById(R.id.proceed_checkout);
    }

    @SuppressLint("SetTextI18n")
    private void updateSelectedItemsCount(SwitchCompat itemSwitch, boolean isChecked) {
        if (isChecked) {
            selectedItemCount++;
            total_cost += itemPrices.get(itemSwitch);
            selectedItemsList.add(itemSwitch.getText().toString());
        } else {
            selectedItemCount--;
            total_cost -= itemPrices.get(itemSwitch);
            selectedItemsList.remove(itemSwitch.getText().toString());
        }
        total_selected_items.setText(selectedItemCount + " Selected Items");
        cost.setText(convertToPhilippinePesoD(total_cost));
    }

    private void addSelectedItems() {

        String laundryWeight = laundry_weight.getText().toString();
        String bedSheetWeight = bed_sheet_weight.getText().toString();
        String blanketWeight = comforter_blanket_weight.getText().toString();

        if(laundryWeight.equals("0 kg") || bedSheetWeight.equals("0 kg") || blanketWeight.equals("0 kg")) {
            Toast.makeText(this, "Please add weight or kilogram.", Toast.LENGTH_SHORT).show();
            loading.dismiss();
        }else {
            // Remove spaces and "kg" from the string
            laundryWeight = laundryWeight.replaceAll("\\s+", "").replace("kg", "");
            bedSheetWeight = bedSheetWeight.replaceAll("\\s+", "").replace("kg", "");
            blanketWeight = blanketWeight.replaceAll("\\s+", "").replace("kg", "");
            double weight = Double.parseDouble(laundryWeight);
            double bedSheetsWeight = Double.parseDouble(bedSheetWeight);
            double blanketsWeight = Double.parseDouble(blanketWeight);

            total_payment = total_cost + deliveryFee;

            CollectionReference itemsCollection = FirebaseFirestore.getInstance().collection("ServicesBooked");

            FirebaseUser mUser = mAuth.getCurrentUser();
            assert mUser != null;

            Map<String, Object> selectedItemsMap = new HashMap<>();
            selectedItemsMap.put("ServicesType", "Drop Off");
            selectedItemsMap.put("TotalSelectedItem", selectedItemCount);
            selectedItemsMap.put("SubTotal", total_cost);
            selectedItemsMap.put("TotalCost", total_payment);
            selectedItemsMap.put("DeliveryFee", deliveryFee);
            selectedItemsMap.put("UserID", mUser.getUid());
            selectedItemsMap.put("OrderStatus", "Payment Pending");
            selectedItemsMap.put("Kilogram", weight);
            selectedItemsMap.put("BedSheetWeight", bedSheetsWeight);
            selectedItemsMap.put("ComforterWeight", blanketsWeight);

            for (SwitchCompat itemSwitch : itemPrices.keySet()) {
                String itemName = itemSwitch.getText().toString().replace(" ", "_");
                boolean selected = itemSwitch.isChecked();
                selectedItemsMap.put(itemName, selected);
            }
            itemsCollection.add(selectedItemsMap).addOnSuccessListener(documentReference -> {
                String bookedID = documentReference.getId();
                Map<String, Object> IDMap = new HashMap<>();
                IDMap.put("BookedID", bookedID);
                itemsCollection.document(bookedID).update(IDMap);

                Intent i = new Intent(this, Payment.class);
                i.putExtra("BookedID", bookedID);
                i.putExtra("Total_Cost", total_payment);
                i.putExtra("DeliveryFee", deliveryFee);
                i.putExtra("SubTotal", total_cost);
                startActivity(i);
                finish();
                loading.dismiss();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void addKgDialog(final TextView tv, final double pricePerKg) {
        View addKgDialog = LayoutInflater.from(DropOffServices.this).inflate(R.layout.add_kg_dialog, null);
        AlertDialog.Builder addKgDialogBuilder = new AlertDialog.Builder(DropOffServices.this);

        addKgDialogBuilder.setView(addKgDialog);

        TextView close = addKgDialog.findViewById(R.id.close);
        MaterialButton ok_btn = addKgDialog.findViewById(R.id.ok_btn);
        kg = addKgDialog.findViewById(R.id.kg);

        final AlertDialog addKgDialogs = addKgDialogBuilder.create();

        close.setOnClickListener(view -> addKgDialogs.dismiss());

        ok_btn.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(kg.getText().toString())) {
                if (total_price_perKG != 0) {
                    // Subtract the previous total_price_perKG
                    total_cost -= total_price_perKG;
                }
                finalKg = Double.parseDouble(kg.getText().toString());
                total_price_perKG = pricePerKg * finalKg;
                total_cost += total_price_perKG;
                cost.setText(convertToPhilippinePesoD(total_cost));
                tv.setText(finalKg + " kg");
                addKgDialogs.dismiss();
            } else {
                Toast.makeText(this, "Enter Kilogram!", Toast.LENGTH_SHORT).show();
            }
        });

        Objects.requireNonNull(addKgDialogs.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        addKgDialogs.setCanceledOnTouchOutside(false);

        addKgDialogs.show();
    }

    @SuppressLint("SetTextI18n")
    private void addBedSheetKgDialog(final TextView tv, final double pricePerKg) {
        View addKgDialog = LayoutInflater.from(DropOffServices.this).inflate(R.layout.add_kg_dialog, null);
        AlertDialog.Builder addKgDialogBuilder = new AlertDialog.Builder(DropOffServices.this);

        addKgDialogBuilder.setView(addKgDialog);

        TextView close = addKgDialog.findViewById(R.id.close);
        MaterialButton ok_btn = addKgDialog.findViewById(R.id.ok_btn);
        kg = addKgDialog.findViewById(R.id.kg);

        final AlertDialog addKgDialogs = addKgDialogBuilder.create();

        close.setOnClickListener(view -> addKgDialogs.dismiss());

        ok_btn.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(kg.getText().toString())) {
                if (total_bedSheetPrice_perKG != 0) {
                    // Subtract the previous total_price_perKG
                    total_cost -= total_bedSheetPrice_perKG;
                }
                finalKg = Double.parseDouble(kg.getText().toString());
                total_bedSheetPrice_perKG = pricePerKg * finalKg;
                total_cost += total_bedSheetPrice_perKG;
                cost.setText(convertToPhilippinePesoD(total_cost));
                tv.setText(finalKg + " kg");
                addKgDialogs.dismiss();
            } else {
                Toast.makeText(this, "Enter Kilogram!", Toast.LENGTH_SHORT).show();
            }
        });

        Objects.requireNonNull(addKgDialogs.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        addKgDialogs.setCanceledOnTouchOutside(false);

        addKgDialogs.show();
    }

    @SuppressLint("SetTextI18n")
    private void addBlanketKgDialog(final TextView tv, final double pricePerKg) {
        View addKgDialog = LayoutInflater.from(DropOffServices.this).inflate(R.layout.add_kg_dialog, null);
        AlertDialog.Builder addKgDialogBuilder = new AlertDialog.Builder(DropOffServices.this);

        addKgDialogBuilder.setView(addKgDialog);

        TextView close = addKgDialog.findViewById(R.id.close);
        MaterialButton ok_btn = addKgDialog.findViewById(R.id.ok_btn);
        kg = addKgDialog.findViewById(R.id.kg);

        final AlertDialog addKgDialogs = addKgDialogBuilder.create();

        close.setOnClickListener(view -> addKgDialogs.dismiss());

        ok_btn.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(kg.getText().toString())) {
                if (total_blanketPrice_perKG != 0) {
                    // Subtract the previous total_price_perKG
                    total_cost -= total_blanketPrice_perKG;
                }
                finalKg = Double.parseDouble(kg.getText().toString());
                total_blanketPrice_perKG = pricePerKg * finalKg;
                total_cost += total_blanketPrice_perKG;
                cost.setText(convertToPhilippinePesoD(total_cost));
                tv.setText(finalKg + " kg");
                addKgDialogs.dismiss();
            } else {
                Toast.makeText(this, "Enter Kilogram!", Toast.LENGTH_SHORT).show();
            }
        });

        Objects.requireNonNull(addKgDialogs.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        addKgDialogs.setCanceledOnTouchOutside(false);

        addKgDialogs.show();
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

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}