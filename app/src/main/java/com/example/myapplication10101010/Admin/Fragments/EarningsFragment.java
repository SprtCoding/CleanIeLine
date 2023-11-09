package com.example.myapplication10101010.Admin.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication10101010.Admin.Fragments.Model.EarningsData;
import com.example.myapplication10101010.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EarningsFragment extends Fragment {
    View v;
    private FirebaseAuth mAuth;
    private FirebaseUser mUSer;
    private FirebaseFirestore db;
    private PieChart pieChart;
    Task<DocumentSnapshot> userColRef;
    private TextView total_earnings, name;
    private CollectionReference bookedColRef;
    private double total_earn = 0;
    ArrayList<EarningsData> earningsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_earnings, container, false);
        _init();

        mAuth = FirebaseAuth.getInstance();
        mUSer = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        bookedColRef = db.collection("ServicesBooked");

        bookedColRef.whereEqualTo("OrderStatus", "Order Completed")
                .orderBy("DateTimePlaced", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if(error == null && value != null) {
                        if(!value.isEmpty()) {
                            HashMap<Integer, Double> weeklyEarningsMap = new HashMap<>(); // Map to store earnings per week
                            for(QueryDocumentSnapshot doc : value) {
                                Date date = doc.getDate("DateTimePlaced");
                                double earnings = doc.getDouble("TotalCost");
                                total_earn += earnings;
                                total_earnings.setText(convertToPhilippinePesoD(total_earn));

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);

                                if (weeklyEarningsMap.containsKey(weekOfYear)) {
                                    double currentEarnings = weeklyEarningsMap.get(weekOfYear);
                                    weeklyEarningsMap.put(weekOfYear, currentEarnings + earnings);
                                } else {
                                    weeklyEarningsMap.put(weekOfYear, earnings);
                                }
                            }
                            // Prepare data for the PieChart
                            ArrayList<PieEntry> pieEntries = new ArrayList<>();
                            for (Map.Entry<Integer, Double> entry : weeklyEarningsMap.entrySet()) {
                                pieEntries.add(new PieEntry(entry.getValue().floatValue(), "Week " + entry.getKey()));
                            }

                            // Populate the PieChart
                            PieDataSet dataSet = new PieDataSet(pieEntries, "Weekly Earnings");
                            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                            dataSet.setValueTextColor(Color.WHITE);
                            dataSet.setValueTextSize(16f);
                            PieData pieData = new PieData(dataSet);
                            pieChart.setData(pieData);
                            pieChart.getDescription().setEnabled(false);
                            pieChart.invalidate(); // Refresh the chart
                            pieChart.animate();
                        }else {
                            total_earnings.setText(convertToPhilippinePesoD(total_earn));
                        }
                    }else {
                        Log.d("FIREBASE ERROR:" , error.getMessage());
                        Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        if(mUSer != null) {
            userColRef = db.collection("USERS")
                    .document(mUSer.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.exists()) {
                            String _name = documentSnapshot.getString("NAME");
                            name.setText(_name);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        return v;
    }

    private void createPieChart() {
        //ArrayList<PieEntry> entries = new ArrayList<>();

//
//        entries.add(new PieEntry((float) total, "Earnings"));
//        entries.add(new PieEntry((float) total, "Earnings"));
//
//        PieDataSet dataSet = new PieDataSet(entries, "Earnings");
//        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//        dataSet.setValueTextColor(Color.BLACK);
//        dataSet.setValueTextSize(16f);
//
//        PieData data = new PieData(dataSet);
//
//        pieChart.setData(data);
//        pieChart.getDescription().setEnabled(false);
//        pieChart.setCenterText("Total Earnings");
//        pieChart.invalidate();
//        pieChart.animate();
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
        total_earnings = v.findViewById(R.id.total_earnings);
        name = v.findViewById(R.id.name);
        pieChart = v.findViewById(R.id.pieChart);
    }
}