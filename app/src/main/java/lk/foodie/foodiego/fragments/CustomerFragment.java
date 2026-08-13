package lk.foodie.foodiego.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import lk.foodie.foodiego.R;
import lk.foodie.foodiego.adapters.CustomerAdapter;
import lk.foodie.foodiego.models.User;

public class CustomerFragment extends Fragment {

    private RecyclerView recyclerView;
    private CustomerAdapter adapter;
    private List<User> userList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customers, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        adapter = new CustomerAdapter(getContext(), userList);
        recyclerView.setAdapter(adapter);

        loadUsers();

        return view;
    }

    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("user") // Firestore collection name
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            return;
                        }

                        userList.clear();
                        for (QueryDocumentSnapshot document : value) {
                            User user = document.toObject(User.class);
                            userList.add(user);
                        }
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}
