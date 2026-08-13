package lk.foodie.foodiego.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.foodie.foodiego.R;
import lk.foodie.foodiego.models.HomeVerModel;

public class HomeVerAdapter extends RecyclerView.Adapter<HomeVerAdapter.ViewHolder> {

    private BottomSheetDialog bottomSheetDialog;
    Context context;
    ArrayList<HomeVerModel> list;
    private ArrayList<HomeVerModel> fullList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;


    public HomeVerAdapter(Context context, ArrayList<HomeVerModel> list) {
        this.context = context;
        this.list = list;
        this.fullList = new ArrayList<>(list);
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_vertical_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final HomeVerModel food = list.get(position);
        final String fullName = list.get(position).getFoodName();
        final String category = list.get(position).getCategory();
        final String description = list.get(position).getDescription();
        final String imageUrl = list.get(position).getImageUrl();
        final Boolean available = list.get(position).getAvailable();
        final double price = list.get(position).getPrice();

        final String availableStr = String.valueOf(list.get(position).getAvailable());
        final String priceStr = String.valueOf(list.get(position).getPrice());

//        holder.imageView.setImageResource(list.get(position).getImage());
        holder.name.setText(list.get(position).getFoodName());
        holder.timing.setText(list.get(position).getCategory());
//        holder.rating.setText(String.valueOf(list.get(position).getAvailable()));
        holder.rating.setText(list.get(position).getAvailable() ? "Available" : "Not Available");
//        holder.price.setText(String.valueOf(list.get(position).getPrice()));
        holder.price.setText(String.format("Rs.%.2f", list.get(position).getPrice()));
        Glide.with(context).load(imageUrl).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog = new BottomSheetDialog(context,R.style.BottomSheetTheme);

                View sheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_layout,null);
                sheetView.findViewById(R.id.add_to_cart).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context,"Added to a Cart",Toast.LENGTH_SHORT).show();
                        addToCart(food);
                        bottomSheetDialog.dismiss();
                    }
                });

                ImageView bottomImg = sheetView.findViewById(R.id.bottom_img);
                TextView bottomName =sheetView.findViewById(R.id.bottom_name);
                TextView bottomPrice =sheetView.findViewById(R.id.bottom_price);
                TextView bottomRating =sheetView.findViewById(R.id.bottom_rating);
                Glide.with(context).load(imageUrl).into(bottomImg);

                bottomName.setText(fullName);
                bottomPrice.setText(priceStr);
                bottomRating.setText(category);
//                bottomImg.setImageResource(mImage);

                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.show();
            }
        });

    }


    @Override
    public int getItemCount() {

        return list.size();
    }

//    public void filterList(String query) {
//        list.clear();
//        if (query.isEmpty()) {
//            list.addAll(fullList); // Reset to full list
//        } else {
//            for (HomeVerModel item : fullList) {
//                if (item.getFoodName().toLowerCase().contains(query.toLowerCase())) {
//                    list.add(item);
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }

    public void updateList(List<HomeVerModel> newList) {
//        list.clear();
//        list.addAll(newList);
//        notifyDataSetChanged();
        list = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    private void addToCart(HomeVerModel food) {
        String userId = auth.getCurrentUser().getUid();
        CollectionReference cartRef = db.collection("cart").document(userId).collection("items");

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("name", food.getFoodName());
        cartItem.put("price", food.getPrice());
        cartItem.put("rating", food.getCategory());
        cartItem.put("imageUrl", food.getImageUrl());
        cartItem.put("quantity", 1);

        cartRef.add(cartItem).addOnSuccessListener(documentReference -> {
            Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        }).addOnFailureListener(e -> Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name,timing,rating,price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ver_img);
            name = itemView.findViewById(R.id.name);
            timing = itemView.findViewById(R.id.timing);
            rating = itemView.findViewById(R.id.rating);
            price = itemView.findViewById(R.id.price);
        }
    }
}
