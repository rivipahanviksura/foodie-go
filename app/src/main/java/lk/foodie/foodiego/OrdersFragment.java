package lk.foodie.foodiego;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lk.foodie.foodiego.adapters.OrderAdapter;
import lk.foodie.foodiego.models.Order;

public class OrdersFragment extends Fragment {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private FirebaseFirestore db;
    private ListenerRegistration registration;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_orders, container, false);

        createNotificationChannel();

        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.rvOrdersList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(getContext(),orderList);
        recyclerView.setAdapter(orderAdapter);

        fetchOrdersData();

        return view;
    }

private void fetchOrdersData() {
    registration = db.collection("orders")
            .orderBy("orderTimestamp", Query.Direction.ASCENDING)
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e("Firestore", "Error fetching orders", error);
                        return;
                    }

                    if (value != null) {
                        orderList.clear();
                        for (QueryDocumentSnapshot orderDoc : value) {
                            String orderId = orderDoc.getId();
                            String customerName = orderDoc.getString("customerName");
                            String orderStatus = orderDoc.getString("orderStatus");

                            if (!orderList.stream().anyMatch(order -> order.getOrderId().equals(orderId))) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    if (ContextCompat.checkSelfPermission(getContext(),
                                            android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                        sendNotification(orderId, customerName);
                                    } else {
                                        // Request the permission
                                        requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                                                NOTIFICATION_PERMISSION_REQUEST_CODE);
                                    }
                                } else {
                                sendNotification(orderId, customerName);
                            }
                        }
                            db.collection("orders").document(orderId).collection("items").get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful() && task.getResult() != null) {
                                                List<String> orderItems = new ArrayList<>();
                                                for (QueryDocumentSnapshot itemDoc : task.getResult()) {
                                                    String itemName = itemDoc.getString("name");
                                                    orderItems.add(itemName);
                                                }

                                                String totalAmountString = "0.00";
                                                Object totalAmountObj = orderDoc.get("totalAmount");
                                                if (totalAmountObj instanceof Double) {
                                                    totalAmountString = String.valueOf((Double) totalAmountObj);
                                                } else if (totalAmountObj instanceof String) {
                                                    totalAmountString = (String) totalAmountObj;
                                                }

                                                String orderTimestampString = "";
                                                Object orderTimestampObj = orderDoc.get("orderTimestamp");

                                                if (orderTimestampObj instanceof Timestamp) {
                                                    // Convert Firestore Timestamp to String
                                                    Timestamp timestamp = (Timestamp) orderTimestampObj;
                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                                    orderTimestampString = sdf.format(timestamp.toDate());
                                                } else if (orderTimestampObj instanceof String) {
                                                    orderTimestampString = (String) orderTimestampObj;
                                                } else if (orderTimestampObj instanceof Long) {
                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                                    orderTimestampString = sdf.format(new java.util.Date((Long) orderTimestampObj));
                                                }

                                                Order order = new Order(orderId, customerName, orderItems, orderStatus, totalAmountString, orderTimestampString);
                                                orderList.add(order);

                                                orderAdapter.notifyDataSetChanged();
                                            } else {
                                                Log.e("Firestore", "Error fetching items", task.getException());
                                            }
                                        }
                                    });
                        }
                    }
                }
            });
}
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Order Notifications";
            String description = "Channel for order notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("order_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(String orderId, String customerName) {
        Intent intent = new Intent(getContext(), OrdersFragment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "order_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("New Order Received")
                .setContentText("Order ID: " + orderId + " from " + customerName)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, handle any pending notifications if necessary
                // You can re-fetch orders or send pending notifications here
            } else {
                // Permission denied
                Log.e("OrdersFragment", "Notification permission denied");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (registration != null) {
            registration.remove();
        }
    }
}