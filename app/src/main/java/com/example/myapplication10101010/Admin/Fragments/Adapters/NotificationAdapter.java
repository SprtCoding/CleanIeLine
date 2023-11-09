package com.example.myapplication10101010.Admin.Fragments.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication10101010.Admin.Fragments.Model.NotificationList;
import com.example.myapplication10101010.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    Context context;
    List<NotificationList> notificationLists;

    public NotificationAdapter(Context context, List<NotificationList> notificationLists) {
        this.context = context;
        this.notificationLists = notificationLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list,parent,false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationList notifications = notificationLists.get(position);

        CollectionReference userColRef = FirebaseFirestore.getInstance().collection("USERS");

        userColRef.document(notifications.getFrom())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("NAME");
                        holder.name.setText(name);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show());

        // Define the date format
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
        String formattedDate = dateFormat.format(notifications.getDate());

        holder.details.setText(notifications.getDetails());
        holder.date.setText(formattedDate);

    }

    @Override
    public int getItemCount() {
        return notificationLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_pic;
        TextView name, details, date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_pic = itemView.findViewById(R.id.profile_pic);
            name = itemView.findViewById(R.id.name);
            details = itemView.findViewById(R.id.details);
            date = itemView.findViewById(R.id.date_time);

        }
    }
}
