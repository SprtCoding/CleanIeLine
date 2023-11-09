package com.example.myapplication10101010.SideFragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication10101010.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class ServicesFragment extends Fragment {
    private TextView _wash_dry_price, _fabcon_price, _bedsheet_price, _comforter_price, _shipping_fee;
    private FirebaseFirestore db;
    private CollectionReference priceListColRef;
    private double wash_dry_price = 0, detergent_price = 0, bedsheet_price = 0, comforter_price = 0,
    shipping_fee = 40;
    View v;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_services, container, false);
        _init();
        _fireStoreInit();

        priceListColRef.document("Items")
                .addSnapshotListener((value, error) -> {
                   if(error == null && value != null) {
                       if(value.exists()) {
                           if(value.getDouble("Per_kg") != null) {
                               wash_dry_price = value.getDouble("Per_kg");
                               _wash_dry_price.setText(convertToPhilippinePesoD(wash_dry_price) + " Per Kilos");
                           }else {
                               _wash_dry_price.setText(convertToPhilippinePesoD(wash_dry_price) + " Per Kilos");
                           }
                           if(value.getDouble("Fabcon") != null) {
                               detergent_price = value.getDouble("Fabcon");
                               _fabcon_price.setText(convertToPhilippinePesoD(detergent_price));
                           }else {
                               _fabcon_price.setText(convertToPhilippinePesoD(detergent_price));
                           }
                           if(value.getDouble("BedSheetWeight") != null) {
                               bedsheet_price = value.getDouble("BedSheetWeight");
                               _bedsheet_price.setText(convertToPhilippinePesoD(bedsheet_price) + " Per Kilos");
                           }else {
                               _bedsheet_price.setText(convertToPhilippinePesoD(bedsheet_price) + " Per Kilos");
                           }
                           if(value.getDouble("ComforterWeight") != null) {
                               comforter_price = value.getDouble("ComforterWeight");
                               _comforter_price.setText(convertToPhilippinePesoD(comforter_price) + " Per Kilos");
                           }else {
                               _comforter_price.setText(convertToPhilippinePesoD(comforter_price) + " Per Kilos");
                           }
                           _shipping_fee.setText(convertToPhilippinePesoD(shipping_fee) + " Only");
                       }
                   }else {
                       Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                   }
                });

        return v;
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

    private void _fireStoreInit() {
        db = FirebaseFirestore.getInstance();
        priceListColRef = db.collection("PriceList");
    }

    private void _init() {
        _wash_dry_price = v.findViewById(R.id.wash_dry_price);
        _fabcon_price = v.findViewById(R.id.fabcon_price);
        _bedsheet_price = v.findViewById(R.id.bedsheet_price);
        _comforter_price = v.findViewById(R.id.comforter_price);
        _shipping_fee = v.findViewById(R.id.shipping_fee);
    }
}