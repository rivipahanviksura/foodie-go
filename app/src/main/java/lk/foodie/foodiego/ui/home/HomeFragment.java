package lk.foodie.foodiego.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import lk.foodie.foodiego.R;
import lk.foodie.foodiego.adapters.HomeHorAdapter;
import lk.foodie.foodiego.adapters.HomeVerAdapter;
import lk.foodie.foodiego.adapters.UpdateVerticalRec;
import lk.foodie.foodiego.databinding.FragmentHomeBinding;
import lk.foodie.foodiego.models.HomeHorModel;
import lk.foodie.foodiego.models.HomeVerModel;

public class HomeFragment extends Fragment implements UpdateVerticalRec {

    private RecyclerView homeHorizontalRec, homeVerticalRec;
    private ArrayList<HomeHorModel> homeHorModelList;
    private HomeHorAdapter homeHorAdapter;

    //vertical
    private ArrayList<HomeVerModel> homeVerModelList;
    private HomeVerAdapter homeVerAdapter;
    private ArrayList<HomeVerModel> filteredList;
    private TextView textView7;
    private EditText searchEditText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        textView7 = root.findViewById(R.id.textView7);
        searchEditText = root.findViewById(R.id.editText4);
        setUserFullName();

        homeHorizontalRec = root.findViewById(R.id.home_hor_rec);
        homeVerticalRec = root.findViewById(R.id.home_ver_rec);
        fetchFoods();

        //Horizontal RecyclerView
        homeHorModelList = new ArrayList<>();
        homeVerModelList = new ArrayList<>();
        filteredList = new ArrayList<>();

        homeHorAdapter = new HomeHorAdapter(this, getActivity(), homeHorModelList);
        homeVerAdapter = new HomeVerAdapter(getActivity(), homeVerModelList);

        homeHorizontalRec.setAdapter(homeHorAdapter);
        homeHorizontalRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        homeHorizontalRec.setHasFixedSize(true);
        homeHorizontalRec.setNestedScrollingEnabled(false);

        //Vertical RecyclerView
        homeVerticalRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        homeVerticalRec.setAdapter(homeVerAdapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFoodList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        return root;
    }

    private void fetchFoods() {
        db.collection("foods")
//                .whereEqualTo("category", category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        homeVerModelList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            HomeVerModel food = new HomeVerModel(
                                    document.getString("image"),
                                    document.getString("foodName"),
                                    document.getString("category"),
                                    document.getString("description"),
                                    document.getBoolean("available"),
                                    document.getDouble("price")
                            );
                            homeVerModelList.add(food);

                            Log.i("FoodiegoFoods", "items" + food);
                        }
                        homeVerAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "Failed to load foods", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterFoodList(String query) {
        filteredList.clear();
        for (HomeVerModel item : homeVerModelList) {
            if (item.getFoodName().toLowerCase().contains(query.toLowerCase())){
                filteredList.add(item);
            }
        }
//        if (query.isEmpty()) {
//            filteredList.addAll(homeVerModelList);
//        } else {
//            for (HomeVerModel food : homeVerModelList) {
//                if (food.getFoodName().toLowerCase().contains(query.toLowerCase())) {
//                    filteredList.add(food);
//                }
//            }
//        }
        homeVerAdapter.updateList(filteredList);
    }

    @Override
    public void callBack(int position, ArrayList<HomeVerModel> list) {

//        homeVerAdapter = new HomeVerAdapter(getContext(), list);
//        homeVerticalRec.setAdapter(homeVerAdapter);
//        homeVerAdapter.notifyDataSetChanged();
        homeVerAdapter.updateList(filteredList);
    }

    private void setUserFullName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getDisplayName() != null) {
            textView7.setText("Hello " + currentUser.getDisplayName());
        } else {
            textView7.setText("Hello User");
        }
    }

}
