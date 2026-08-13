package lk.foodie.foodiego.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.foodie.foodiego.OrderConfirmationActivity;
import lk.foodie.foodiego.R;
import lk.foodie.foodiego.adapters.CartAdapter;
import lk.foodie.foodiego.models.CartModel;
import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class MyCartFragment extends Fragment implements CartAdapter.OnItemDeleteListener {

    Button checkoutButton;
    List<CartModel> list;
    CartAdapter cartAdapter;
    RecyclerView recyclerView;
//    private List<CartModel> cartItemList;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private String userId;
    static final int PAYHERE_REQUEST = 11001;
    double totalAmount = 0;
    public MyCartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_cart, container, false);

        recyclerView = view.findViewById(R.id.cart_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        cartAdapter = new CartAdapter(list, this);
        recyclerView.setAdapter(cartAdapter);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        loadCartItems();
//        list.add(new CartModel(R.drawable.s1, "Order 1", "Rs600", "4.2"));
//        list.add(new CartModel(R.drawable.s2, "Order 1", "Rs800", "4.2"));
//        list.add(new CartModel(R.drawable.fav1, "Order 1", "Rs1000", "4.2"));
//        list.add(new CartModel(R.drawable.s1, "Order 1", "Rs600", "4.2"));
//        list.add(new CartModel(R.drawable.s2, "Order 1", "Rs800", "4.2"));
//        list.add(new CartModel(R.drawable.fav1, "Order 1", "Rs1000", "4.2"));
//        cartAdapter = new CartAdapter(list);
//        recyclerView.setAdapter(cartAdapter);

        checkoutButton = view.findViewById(R.id.button);
        checkoutButton.setOnClickListener(v -> {
//            processCheckout();
            initiatePayment(totalAmount);
        });
        return view;
    }

    private void loadCartItems() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid(); // Get the user ID

            firestore.collection("cart").document(uid).collection("items")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            list.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                String imageUrl = document.getString("imageUrl");
                                String name = document.getString("name");
                                Double price = document.getDouble("price");
                                String rating = document.getString("rating");
                                Long quantity = document.getLong("quantity");

                                if (price != null) {
                                    double priceValue = price;// Replace Rs and parse
                                    totalAmount += priceValue; // Add to total amount
                                }

                                // Assuming you have a method to get drawable resource from URL, else use placeholders
                                list.add(new CartModel( id, imageUrl, name, price, rating, quantity != null ? quantity.intValue() : 1));
                            }
                            cartAdapter.notifyDataSetChanged(); // Notify adapter of data change
                            displayTotalAmount(totalAmount);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle the error
                    });
        }
    }

    @Override
    public void onItemDelete(int position) {
        if (list == null || list.size() == 0) {
            Log.e("MyCartFragment", "Cart list is empty.");
            return;
        }

        if (position < 0 || position >= list.size()) {
            Log.e("MyCartFragment", "Invalid position: " + position);
            return;
        }

        CartModel itemToDelete = list.get(position);
        if (itemToDelete == null || itemToDelete.getId() == null || itemToDelete.getId().isEmpty()) {
            Log.e("MyCartFragment", "Item or document ID is null");
            return;
        }

        String itemId = itemToDelete.getId();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        DocumentReference itemRef = db.collection("cart").document(userId).collection("items").document(itemId);

        Log.d("MyCartFragment", "Deleting item: " + itemId);
        itemRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("MyCartFragment", "Item deleted successfully");
                    list.remove(position);
                    cartAdapter.notifyItemRemoved(position);
                    loadCartItems();
                })
                .addOnFailureListener(e -> Log.e("MyCartFragment", "Failed to delete item", e));
    }

    private void displayTotalAmount(double totalAmount) {
        TextView totalAmountTextView = getView().findViewById(R.id.total_amount_text_view);
        totalAmountTextView.setText("Rs " + totalAmount);
    }

    private void processCheckout() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();
        String orderId = firestore.collection("orders").document().getId();
        String customerName = currentUser.getDisplayName();

        firestore.collection("cart").document(uid).collection("items")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        List<CartModel> cartItems = new ArrayList<>();
                        double totalAmount = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String imageUrl = document.getString("imageUrl");
                            String name = document.getString("name");
                            Double price = document.getDouble("price");
                            String rating = document.getString("rating");
                            Long quantity = document.getLong("quantity");

                            if (price != null) {
                                totalAmount += price; // Calculate total price
                            }

                            cartItems.add(new CartModel(id, imageUrl, name, price, rating,quantity != null ? quantity.intValue() : 1));
                        }

                        // Create order data
                        Map<String, Object> orderData = new HashMap<>();
                        orderData.put("customerName", customerName);
                        orderData.put("orderId", orderId);
                        orderData.put("orderStatus", "Pending");
                        orderData.put("orderTimestamp", com.google.firebase.Timestamp.now());
                        orderData.put("totalAmount", totalAmount);

                        firestore.collection("orders").document(orderId)
                                .set(orderData)
                                .addOnSuccessListener(aVoid -> {
                                    // Move cart items to order subcollection
                                    moveCartItemsToOrder(uid, orderId, cartItems);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Order creation failed", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void moveCartItemsToOrder(String userId, String orderId, List<CartModel> cartItems) {
        for (CartModel item : cartItems) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("imageUrl", item.getImage());
            itemData.put("name", item.getName());
            itemData.put("price", item.getPrice());
            itemData.put("rating", item.getRating());
            itemData.put("quantity", item.getQuantity());

            firestore.collection("orders").document(orderId)
                    .collection("items")
                    .document(item.getId())
                    .set(itemData);
        }

        clearUserCart(userId);
        Intent intent = new Intent(getActivity(), OrderConfirmationActivity.class);
        startActivity(intent);
    }
    private void clearUserCart(String userId) {
        firestore.collection("cart").document(userId).collection("items")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        document.getReference().delete(); // Delete each cart item
                    }
                    list.clear(); // Clear local cart list
                    displayTotalAmount(0);
                    cartAdapter.notifyDataSetChanged(); // Refresh RecyclerView
                    Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to clear cart", Toast.LENGTH_SHORT).show();
                });
    }

    private int getImageResourceByUrl(String imageUrl) {
        // Implement your logic to get a drawable resource ID from the URL or load it using Glide/Picasso
        // For example, return a placeholder image resource if needed
        return R.drawable.placeholder_image; // Replace with actual logic
    }
    private void initiatePayment(double totalAmount) {
        InitRequest req = new InitRequest();
        req.setMerchantId("1227419");       // Merchant ID
        req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
        req.setAmount(totalAmount);             // Final Amount to be charged
        req.setOrderId("230000123");        // Unique Reference ID
        req.setItemsDescription("Foodie Go");  // Item description title
        req.setCustom1("This is the custom message 1");
        req.setCustom2("This is the custom message 2");
        req.getCustomer().setFirstName("Rivipahan");
        req.getCustomer().setLastName("Viksura");
        req.getCustomer().setEmail("rivipaha@gmail.com");
        req.getCustomer().setPhone("+94769112321");
        req.getCustomer().getAddress().setAddress("No.1, Galle Road");
        req.getCustomer().getAddress().setCity("Colombo");
        req.getCustomer().getAddress().setCountry("Sri Lanka");

        Intent intent = new Intent(getActivity(), PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
        startActivityForResult(intent, PAYHERE_REQUEST); //unique request ID e.g. "11001"
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
            if (resultCode == Activity.RESULT_OK) {
                String msg;
                if (response != null)
                    if (response.isSuccess()) {
                        msg = "Activity result:" + response.getData().toString();
                        processCheckout();
//                        saveTransactionToFirestore();
//                        clearUserCart();
//
//                        Intent intent = new Intent(StudentCartActivity.this, StudentHomeActivity.class); // Change MainActivity to your home activity
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
//                        startActivity(intent);
//                        finish();
                    } else
                        msg = "Result:" + response.toString();
                else
                    msg = "Result: no response";

            } else if (resultCode == Activity.RESULT_CANCELED) {

                if (response != null) {

                } else {


                }
            }
        }
    }
}