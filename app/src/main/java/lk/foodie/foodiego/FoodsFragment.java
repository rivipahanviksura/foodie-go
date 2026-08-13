package lk.foodie.foodiego;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import lk.foodie.foodiego.AddFoodActivity;
import lk.foodie.foodiego.R;
import lk.foodie.foodiego.adapters.FoodAdapter;
import lk.foodie.foodiego.models.Food;

public class FoodsFragment extends Fragment {

    private RecyclerView rvFoodList;
    private EditText etSearchFood;
    private FoodAdapter foodAdapter;
    private List<Food> foodList = new ArrayList<>();
    private FirebaseFirestore db;
    private Button btnAddFood;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_foods, container, false);

        // Initialize Views
        rvFoodList = view.findViewById(R.id.rvFoodList);
        etSearchFood = view.findViewById(R.id.etSearchFood);
        btnAddFood = view.findViewById(R.id.btnAddFood);

        // Button Click Listener
        btnAddFood.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddFoodActivity.class);
            startActivity(intent);
        });

        // Search Food List
        etSearchFood.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterFoodList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Set up RecyclerView
        rvFoodList.setLayoutManager(new LinearLayoutManager(getContext()));
        foodAdapter = new FoodAdapter(getContext(), foodList);
        rvFoodList.setAdapter(foodAdapter);

        // Fetch food list from Firestore
        fetchFoodList();



        return view;
    }

    private void fetchFoodList() {
        db = FirebaseFirestore.getInstance();
        CollectionReference foodRef = db.collection("foods");

        foodRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
//                foodList.clear(); // Clear the list before adding new items
                List<Food> tempList = new ArrayList<>(); // Temporary list to avoid partial updates
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    Log.d("Firestore", "Document Retrieved: " + documentSnapshot.getData());
                    Log.d("Firestore", "Document Retrieved: " + documentSnapshot.getData()); // Add this

                    String id = documentSnapshot.getId();
                    String name = documentSnapshot.getString("foodName");
                    String description = documentSnapshot.getString("description");
                    String category = documentSnapshot.getString("category");
                    Boolean available = documentSnapshot.getBoolean("available");
                    String imageUrl = documentSnapshot.getString("image");

                    double price = 0.0;
                    Object priceObj = documentSnapshot.get("price");
                    if (priceObj instanceof Number) {
                        price = ((Number) priceObj).doubleValue();
                    }

//                    if (name != null && description != null && category != null && imageUrl != null) {
//                        foodList.add(new Food(id, name, description, price, category, available, imageUrl));
//                    }

                    if (name != null && description != null && category != null) {
                        tempList.add(new Food(id, name, description, price, category, available, imageUrl));
                    }

                }

                // Debug logs to check size before updating adapter
                Log.d("Firestore", "Total Food Items Retrieved: " + tempList.size());

                // If list is empty, Firestore isn't returning data
                if (tempList.isEmpty()) {
                    Log.e("Firestore", "No food items found in database.");
                }

                // Now update the main list and notify adapter
                foodList.clear();
                foodList.addAll(tempList);
                requireActivity().runOnUiThread(() -> {
                    foodAdapter.notifyDataSetChanged();
                    Log.d("RecyclerView", "Adapter notified with " + foodList.size() + " items.");
                });

                // Notify adapter after updating data
//                foodAdapter.notifyDataSetChanged();
//                requireActivity().runOnUiThread(() -> {
//                    foodAdapter.notifyDataSetChanged();
//                    Log.d("RecyclerView", "Adapter notified with " + foodList.size() + " items.");
//                });

            } else {
                Log.e("Firestore", "Error fetching documents", task.getException());
            }
        });
    }

    private void filterFoodList(String query) {
        List<Food> filteredList = new ArrayList<>();
        for (Food food : foodList) {
            if (food.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(food);
            }
        }

        // Update existing adapter instead of creating a new one
        foodAdapter.updateList(filteredList);
    }
}
