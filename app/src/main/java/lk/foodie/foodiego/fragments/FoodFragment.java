//package lk.foodie.foodiego.fragments;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.EventListener;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.FirebaseFirestoreException;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import lk.foodie.foodiego.AddFoodActivity;
//import lk.foodie.foodiego.R;
//import lk.foodie.foodiego.adapters.FoodAdapter;
//import lk.foodie.foodiego.models.Food;
//
//
//public class FoodFragment extends Fragment {
//    private RecyclerView rvFoodList;
//    private EditText etSearchFood;
//    private FoodAdapter foodAdapter;
//    private List<Food> foodList = new ArrayList<>();
//    private FirebaseFirestore db;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_foods, container, false);
//
//        // Find Button
//        View btnAddFood = view.findViewById(R.id.btnAddFood);
//
//        // Set Click Listener
//        btnAddFood.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), AddFoodActivity.class);
//            startActivity(intent);
//        });
//
//        rvFoodList = view.findViewById(R.id.rvFoodList);
//        etSearchFood = view.findViewById(R.id.etSearchFood);
//
//        rvFoodList.setLayoutManager(new LinearLayoutManager(getContext()));
//        foodAdapter = new FoodAdapter(getContext(), foodList);
//        rvFoodList.setAdapter(foodAdapter);
//
//        db = FirebaseFirestore.getInstance();
//
//
//
//        etSearchFood.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                filterFoodList(charSequence.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {}
//        });
//
//        fetchFoodList();
//
//        return view;
//    }
//    private void fetchFoodList() {
//        Log.d("Firestore", "fetchFoodList() method called"); // Debug Log
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        CollectionReference foodRef = db.collection("foods");
//
//        foodRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Log.d("Firestore", "Successfully connected to Firestore"); // Debug Log
//                foodList.clear();
//                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                    Log.d("Firestore", "Document Retrieved: " + documentSnapshot.getData()); // Log Document Data
//
//                    String id = documentSnapshot.getId();
//                    String name = documentSnapshot.getString("foodName");
//                    String description = documentSnapshot.getString("description");
//                    Double price = documentSnapshot.getDouble("price");
//                    String category = documentSnapshot.getString("category");
//                    Boolean available = documentSnapshot.getBoolean("available");
//                    String imageUrl = documentSnapshot.getString("imageUrl");
//
//                    if (name != null && description != null && price != null && category != null && available != null && imageUrl != null) {
//                        Food food = new Food(id, name, description, price, category, available, imageUrl);
//                        foodList.add(food);
//                    } else {
//                        Log.e("Firestore", "Missing fields in document: " + documentSnapshot.getId());
//                    }
//                }
//                Log.d("Firestore", "Total Foods Loaded: " + foodList.size()); // Log Food Count
//                foodAdapter.notifyDataSetChanged();
//            } else {
//                Log.e("Firestore", "Error fetching documents", task.getException());
//            }
//        });
//    }
//
//
//
//
//    private void filterFoodList(String query) {
//        List<Food> filteredList = new ArrayList<>();
//        for (Food food : foodList) {
//            if (food.getName().toLowerCase().contains(query.toLowerCase())) {
//                filteredList.add(food);
//            }
//        }
//        foodAdapter = new FoodAdapter(getContext(), filteredList);
//        rvFoodList.setAdapter(foodAdapter);
//    }
//    }
//
