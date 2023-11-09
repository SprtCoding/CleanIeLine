package com.example.myapplication10101010.FireStoreQuery;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DBQuery {
    public static FirebaseFirestore g_firestore;

    public static void createUserData(String email, String name, String id, String accountType, MyCompleteListener myCompleteListener) {
        Map<String, Object> userData = new HashMap<>();

        userData.put("USER_ID", id);
        userData.put("EMAIL_ID", email);
        userData.put("NAME", name);
        userData.put("ACCOUNT_TYPE", accountType);

        DocumentReference userDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userDoc.set(userData)
                .addOnSuccessListener(unused -> {
                    myCompleteListener.onSuccess();
                }).addOnFailureListener(e -> {
                    myCompleteListener.onFailure(e);
                });
    }

    public static void setPrice(double per_kg, double customerProvidedPrice, double selectFabconPrice ,
                                double pillowCasePrice, double blanketPrice, double liquid, double powder, MyCompleteListener myCompleteListener) {
        Map<String, Object> priceData = new HashMap<>();

        priceData.put("Per_kg", per_kg);
        priceData.put("CustomerProvided", customerProvidedPrice);
        priceData.put("Fabcon", selectFabconPrice);
        priceData.put("BedSheetWeight", pillowCasePrice);
        priceData.put("ComforterWeight", blanketPrice);
        priceData.put("Liquid", liquid);
        priceData.put("Powder", powder);

        DocumentReference priceDoc = g_firestore.collection("PriceList").document("Items");

        priceDoc.set(priceData)
                .addOnSuccessListener(unused -> {
                    myCompleteListener.onSuccess();
                }).addOnFailureListener(myCompleteListener::onFailure);
    }
}
