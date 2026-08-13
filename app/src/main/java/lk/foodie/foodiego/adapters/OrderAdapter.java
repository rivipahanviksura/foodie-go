package lk.foodie.foodiego.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lk.foodie.foodiego.R;
import lk.foodie.foodiego.models.Order;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orderList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView tvOrderId, tvCustomerName, tvOrderItems, tvOrderStatus, tvTotalAmount, tvOrderTimestamp;
        public Button btnApproveOrder, btnOpenMap;

        public OrderViewHolder(View view) {
            super(view);
            tvOrderId = view.findViewById(R.id.tvOrderId);
            tvCustomerName = view.findViewById(R.id.tvCustomerName);
            tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
            tvOrderStatus = view.findViewById(R.id.tvOrderStatus);
            tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
            tvOrderTimestamp = view.findViewById(R.id.tvOrderTimestamp);
            btnApproveOrder = view.findViewById(R.id.btnApproveOrder);
            btnOpenMap = itemView.findViewById(R.id.btnOpenMap);
        }
    }

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvOrderId.setText("Oreder ID: " + order.getOrderId());
        holder.tvCustomerName.setText(order.getCustomerName());
        holder.tvOrderStatus.setText(order.getOrderStatus());
        holder.tvTotalAmount.setText(order.getTotalAmount());
        holder.tvOrderTimestamp.setText(order.getOrderTimestamp());
        holder.btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchUserLocation(order.getCustomerName());
            }
        });

        StringBuilder itemsString = new StringBuilder("Items: ");
        if (order.getItems()==null){
            Log.i("items","No Order Items");
        }
        for (String item : order.getItems()) {
            itemsString.append(item).append(", ");
        }
        if (itemsString.length() > 7) {
            itemsString.setLength(itemsString.length() - 2); // Remove last comma
        }
        Log.i("items","items"+itemsString);
        holder.tvOrderItems.setText(itemsString.toString());

        if (order.getOrderStatus().equalsIgnoreCase("Pending")) {
            holder.tvOrderStatus.setTextColor(Color.RED);
        } else if (order.getOrderStatus().equalsIgnoreCase("Approved")) {
            holder.tvOrderStatus.setTextColor(Color.GREEN);
            holder.btnApproveOrder.setVisibility(View.GONE);
        }

        holder.btnApproveOrder.setOnClickListener(v -> {
            String orderId = order.getOrderId();

            db.collection("orders").document(orderId)
                    .update("orderStatus", "Approved")
                    .addOnSuccessListener(aVoid -> {
                        order.setOrderStatus("Approved");
                        notifyItemChanged(position);
                        Toast.makeText(v.getContext(), "Order Approved!", Toast.LENGTH_SHORT).show();// Notify adapter to refresh UI
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(v.getContext(), "Failed to Approve Order", Toast.LENGTH_SHORT).show()
                    );
        });
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }
    private void fetchUserLocation(String customerName) {
        db.collection("user")
                .whereEqualTo("fullName", customerName) // Filter by fullName
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                String location = document.getString("location");
                                GeoPoint geoPoint = document.getGeoPoint("location");

                                if (geoPoint != null) {
                                    String locationString = geoPoint.getLatitude() + "," + geoPoint.getLongitude();

                                    Log.d("UserLocation", "Customer Name: " + customerName + ", Location: " + locationString);

                                    openGoogleMaps(geoPoint.getLatitude(), geoPoint.getLongitude());
                                }
                            }
                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    private void openGoogleMaps(double latitude, double longitude) {
//        String uri = "geo:" + latitude + "," + longitude;
        String uri = "google.navigation:q=" + latitude + "," + longitude + "&mode=d";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps"); // Ensure the intent opens Google Maps

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Log.e("Maps", "No application can handle the maps intent");
            Toast.makeText(context, "Google Maps is not installed.", Toast.LENGTH_SHORT).show();
        }
    }


}

