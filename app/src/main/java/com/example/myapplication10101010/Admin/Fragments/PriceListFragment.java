package com.example.myapplication10101010.Admin.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication10101010.BottomFragments.Services.DropOffServices;
import com.example.myapplication10101010.FireStoreQuery.DBQuery;
import com.example.myapplication10101010.FireStoreQuery.MyCompleteListener;
import com.example.myapplication10101010.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PriceListFragment extends Fragment {
    View v;
    private TextView kg_tv, customer_provided, liquid, powder, select_fabcon, pillow_case, blanket;
    private MaterialButton saveBtn;
    private EditText priceET;
    private DocumentReference priceDocRef;
    private FirebaseFirestore db;
    private double perKG = 0, customerProvidedPrice = 0, liquidPrice = 0, powderPrice = 0, selectFabconPrice = 0, pillowCasePrice = 0
            , blanketPrice = 0;
    private ProgressDialog loading;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_price_list, container, false);
        _init();

        loading = new ProgressDialog(getContext());
        loading.setTitle("Saving");
        loading.setMessage("Please wait...");
        loading.setCancelable(false);

        db = FirebaseFirestore.getInstance();
        DBQuery.g_firestore = FirebaseFirestore.getInstance();

        priceDocRef = db.collection("PriceList").document("Items");

        kg_tv.setOnClickListener(view -> editDialog(kg_tv));
        customer_provided.setOnClickListener(view -> editDialog(customer_provided));
        liquid.setOnClickListener(view -> editDialog(liquid));
        powder.setOnClickListener(view -> editDialog(powder));
        select_fabcon.setOnClickListener(view -> editDialog(select_fabcon));
        pillow_case.setOnClickListener(view -> editDialog(pillow_case));
        blanket.setOnClickListener(view -> editDialog(blanket));

        getPrice();

        saveBtn.setOnClickListener(view -> {
            loading.show();
            DBQuery.setPrice(
                    perKG,
                    customerProvidedPrice,
                    selectFabconPrice,
                    pillowCasePrice,
                    blanketPrice,
                    liquidPrice,
                    powderPrice,
                    new MyCompleteListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getContext(), "Saved Successfully!", Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                        }
                    }
            );
        });
        return v;
    }

    @SuppressLint("SetTextI18n")
    private void getPrice() {
        priceDocRef.addSnapshotListener((value, error) -> {
            if(error == null && value != null) {
                if(value.exists()) {
                    if(value.getDouble("Per_kg") != null) {
                        double kg_price = value.getDouble("Per_kg");
                        kg_tv.setText("Kilogram - " + convertToPhilippinePesoD(kg_price));
                    }
                    if(value.getDouble("CustomerProvided") != null) {
                        double CustomerProvidedPrice = value.getDouble("CustomerProvided");
                        customer_provided.setText("Customer Provided - " + convertToPhilippinePesoD(CustomerProvidedPrice));
                    }
                    if(value.getDouble("Liquid") != null) {
                        double Liquid = value.getDouble("Liquid");
                        liquid.setText("Liquid - " + convertToPhilippinePesoD(Liquid));
                    }
                    if(value.getDouble("Powder") != null) {
                        double Powder = value.getDouble("Powder");
                        powder.setText("Powder - " + convertToPhilippinePesoD(Powder));
                    }
                    if(value.getDouble("Fabcon") != null) {
                        double SelectFabconPrice = value.getDouble("Fabcon");
                        select_fabcon.setText("Fabcon - " + convertToPhilippinePesoD(SelectFabconPrice));
                    }
                    if(value.getDouble("BedSheetWeight") != null) {
                        double PillowCasePrice = value.getDouble("BedSheetWeight");
                        pillow_case.setText("Beedsheet/Towel/Pillow Cases - " + convertToPhilippinePesoD(PillowCasePrice));
                    }
                    if(value.getDouble("ComforterWeight") != null) {
                        double BlanketPrice = value.getDouble("ComforterWeight");
                        blanket.setText("Comforter/Bulky Blanket - " + convertToPhilippinePesoD(BlanketPrice));
                    }

                }else {
                    double kg_price = 0;
                    double CustomerProvidedPrice = 0;
                    double Liquid = 0;
                    double Powder = 0;
                    double SelectFabconPrice = 0;
                    double PillowCasePrice = 0;
                    double BlanketPrice = 0;
                    double ComforterPrice = 0;

                    kg_tv.setText("Kilogram - " + convertToPhilippinePesoD(kg_price));
                    customer_provided.setText("Customer Provided - " + convertToPhilippinePesoD(CustomerProvidedPrice));
                    liquid.setText("Liquid - " + convertToPhilippinePesoD(Liquid));
                    powder.setText("Powder - " + convertToPhilippinePesoD(Powder));
                    select_fabcon.setText("Fabcon - " + convertToPhilippinePesoD(SelectFabconPrice));
                    pillow_case.setText("Beedsheet/Towel/Pillow Cases - " + convertToPhilippinePesoD(PillowCasePrice));
                    blanket.setText("Comforter/Bulky Blanket - " + convertToPhilippinePesoD(BlanketPrice));
                }
            }else {
                Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void _init() {
        kg_tv = v.findViewById(R.id.kg_tv);
        customer_provided = v.findViewById(R.id.customer_provided);
        liquid = v.findViewById(R.id.liquid);
        powder = v.findViewById(R.id.powder);
        select_fabcon = v.findViewById(R.id.select_fabcon);
        pillow_case = v.findViewById(R.id.pillow_case);
        blanket = v.findViewById(R.id.blanket);
        saveBtn = v.findViewById(R.id.save_btn);
    }

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    private void editDialog(TextView tv) {
        View editDialog = LayoutInflater.from(getContext()).inflate(R.layout.edit_dialog, null);
        AlertDialog.Builder editDialogBuilder = new AlertDialog.Builder(getContext());

        editDialogBuilder.setView(editDialog);

        TextView close = editDialog.findViewById(R.id.close);
        MaterialButton ok_btn = editDialog.findViewById(R.id.ok_btn);
        priceET = editDialog.findViewById(R.id.price_et);

        final AlertDialog editDialogs = editDialogBuilder.create();

        close.setOnClickListener(view -> editDialogs.dismiss());

        ok_btn.setOnClickListener(view -> {
            if(!TextUtils.isEmpty(priceET.getText().toString())) {
                // Get the text from the TextView
                String text = tv.getText().toString();
                // Define the regular expression pattern
                String kgRegexPattern = "Kilogram(?![\\d-])"; // Matches "Kilogram" not followed by a digit or '-'
                String cPRegexPattern = "Customer Provided(?![\\d-])";
                String liquidRegexPattern = "Liquid(?![\\d-])";
                String powderRegexPattern = "Powder(?![\\d-])";
                String sFRegexPattern = "Fabcon(?![\\d-])";
                String pCaseRegexPattern = "Beedsheet/Towel/Pillow Cases(?![\\d-])";
                String blanketRegexPattern = "Comforter/Bulky Blanket(?![\\d-])";

                // Create a Pattern object
                Pattern kgPattern = Pattern.compile(kgRegexPattern);
                Pattern cPPattern = Pattern.compile(cPRegexPattern);
                Pattern liquidPattern = Pattern.compile(liquidRegexPattern);
                Pattern powderPattern = Pattern.compile(powderRegexPattern);
                Pattern sFPattern = Pattern.compile(sFRegexPattern);
                Pattern pCasePattern = Pattern.compile(pCaseRegexPattern);
                Pattern blanketPattern = Pattern.compile(blanketRegexPattern);

                // Create a Matcher object
                Matcher kgMatcher = kgPattern.matcher(text);
                Matcher cPMatcher = cPPattern.matcher(text);
                Matcher liquidMatcher = liquidPattern.matcher(text);
                Matcher powderMatcher = powderPattern.matcher(text);
                Matcher sFMatcher = sFPattern.matcher(text);
                Matcher pCaseMatcher = pCasePattern.matcher(text);
                Matcher blanketMatcher = blanketPattern.matcher(text);

                // Check if the pattern is found in the text
                if (kgMatcher.find()) {
                    // The text contains "Kilogram" but not followed by "- 0"
                    // Handle your logic here
                    perKG = Double.parseDouble(priceET.getText().toString());
                    tv.setText("Kilogram - " + convertToPhilippinePesoD(perKG));
                    editDialogs.dismiss();
                }
                if (cPMatcher.find()) {
                    // The text contains "Kilogram" but not followed by "- 0"
                    // Handle your logic here
                    customerProvidedPrice = Double.parseDouble(priceET.getText().toString());
                    tv.setText("Customer Provided - " + convertToPhilippinePesoD(customerProvidedPrice));
                    editDialogs.dismiss();
                }
                if (liquidMatcher.find()) {
                    // The text contains "Kilogram" but not followed by "- 0"
                    // Handle your logic here
                    liquidPrice = Double.parseDouble(priceET.getText().toString());
                    tv.setText("Liquid - " + convertToPhilippinePesoD(liquidPrice));
                    editDialogs.dismiss();
                }
                if (powderMatcher.find()) {
                    // The text contains "Kilogram" but not followed by "- 0"
                    // Handle your logic here
                    powderPrice = Double.parseDouble(priceET.getText().toString());
                    tv.setText("Powder - " + convertToPhilippinePesoD(powderPrice));
                    editDialogs.dismiss();
                }
                if (sFMatcher.find()) {
                    // The text contains "Kilogram" but not followed by "- 0"
                    // Handle your logic here
                    selectFabconPrice = Double.parseDouble(priceET.getText().toString());
                    tv.setText("Fabcon - " + convertToPhilippinePesoD(selectFabconPrice));
                    editDialogs.dismiss();
                }
                if (pCaseMatcher.find()) {
                    // The text contains "Kilogram" but not followed by "- 0"
                    // Handle your logic here
                    pillowCasePrice = Double.parseDouble(priceET.getText().toString());
                    tv.setText("Beedsheet/Towel/Pillow Cases - " + convertToPhilippinePesoD(pillowCasePrice));
                    editDialogs.dismiss();
                }
                if (blanketMatcher.find()) {
                    // The text contains "Kilogram" but not followed by "- 0"
                    // Handle your logic here
                    blanketPrice = Double.parseDouble(priceET.getText().toString());
                    tv.setText("Comforter/Bulky Blanket - " + convertToPhilippinePesoD(blanketPrice));
                    editDialogs.dismiss();
                }
            }else {
                Toast.makeText(getContext(), "Enter Price!", Toast.LENGTH_SHORT).show();
            }
        });

        Objects.requireNonNull(editDialogs.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        editDialogs.setCanceledOnTouchOutside(false);

        editDialogs.show();
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