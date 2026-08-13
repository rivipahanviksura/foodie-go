package lk.foodie.foodiego.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import lk.foodie.foodiego.R;
import lk.foodie.foodiego.models.Food;
import lk.foodie.foodiego.models.HomeHorModel;
import lk.foodie.foodiego.models.HomeVerModel;

public class HomeHorAdapter extends RecyclerView.Adapter<HomeHorAdapter.ViewHolder> {

    UpdateVerticalRec updateVerticalRec;
    Activity activity;
    ArrayList<HomeHorModel> list;

    int row_index = -1;

    boolean check = true;
    boolean select = true;
//    int row_index = -1;

    public HomeHorAdapter(UpdateVerticalRec updateVerticalRec, Activity activity, ArrayList<HomeHorModel> list) {
        this.updateVerticalRec = updateVerticalRec;
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_horizontal_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        HomeHorModel model = list.get(holder.getAdapterPosition());
        holder.imageView.setImageResource(list.get(position).getImage());
        holder.name.setText(list.get(position).getName());

        holder.cardView.setOnClickListener(v -> {
            row_index = position;
            notifyDataSetChanged();
//            fetchFoodItemsFromFirestore(model.getName());
        });

        holder.cardView.setBackgroundResource(row_index == position ? R.drawable.change_bg : R.drawable.default_bg);
    }

//    private void fetchFoodItemsFromFirestore(String category) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("foods").whereEqualTo("category", category)
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    ArrayList<HomeVerModel> foodList = new ArrayList<>();
//                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                        Food food = document.toObject(Food.class);
//                        foodList.add(new HomeVerModel(
////                                null, // Placeholder image
////                                food.getName(),
////                                "10:00-22:00",
////                                "4.5",
////                                "Min - Rs" + food.getPrice()
////                        ));
////                    }
//                    updateVerticalRec.callBack(row_index, foodList);
//                });
//    }
//        if (check) {
//            ArrayList<HomeVerModel> homeVerModels = new ArrayList<>();
//            homeVerModels.add(new HomeVerModel(R.drawable.pizza1, "Pizza 1", "10:00-22:00", "4.5", "Min - Rs2999"));
//            homeVerModels.add(new HomeVerModel(R.drawable.pizza2, "Pizza 2", "10:00-22:00", "4.5", "Min - Rs2999"));
//            homeVerModels.add(new HomeVerModel(R.drawable.pizza3, "Pizza 3", "10:00-22:00", "4.5", "Min - Rs2999"));
//            homeVerModels.add(new HomeVerModel(R.drawable.pizza4, "Pizza 4", "10:00-22:00", "4.5", "Min - Rs2999"));
//
//            updateVerticalRec.callBack(position, homeVerModels);
//            check = false;
//        }
//            holder.cardView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    row_index = position;
//                    notifyDataSetChanged();
//
//                    if (position == 0){
//                        ArrayList<HomeVerModel> homeVerModels = new ArrayList<>();
//
//                        homeVerModels.add(new HomeVerModel(R.drawable.pizza1,"Pizza 1","10:00-22:00","4.5","Min - Rs2999"));
//                        homeVerModels.add(new HomeVerModel(R.drawable.pizza2,"Pizza 2","10:00-22:00","4.5","Min - Rs2999"));
//                        homeVerModels.add(new HomeVerModel(R.drawable.pizza3,"Pizza 3","10:00-22:00","4.5","Min - Rs2999"));
//                        homeVerModels.add(new HomeVerModel(R.drawable.pizza4,"Pizza 4","10:00-22:00","4.5","Min - Rs2999"));
//
//                        updateVerticalRec.callBack(position,homeVerModels);
//                    } else if (position == 1) {
//                        ArrayList<HomeVerModel> homeVerModels = new ArrayList<>();
//
//                        homeVerModels.add(new HomeVerModel(R.drawable.burger2,"Burger 1","10:00-22:00","4.5","Min - Rs2999"));
//                        homeVerModels.add(new HomeVerModel(R.drawable.burger2,"Burger 2","10:00-22:00","4.5","Min - Rs2999"));
//                        homeVerModels.add(new HomeVerModel(R.drawable.burger4,"Burger 3","10:00-22:00","4.5","Min - Rs2999"));
//
//                    } else if (position == 2) {
//                        ArrayList<HomeVerModel> homeVerModels = new ArrayList<>();
//
//                        homeVerModels.add(new HomeVerModel(R.drawable.fries1,"Fries 1","10:00-22:00","4.5","Min - Rs2999"));
//                        homeVerModels.add(new HomeVerModel(R.drawable.fries2,"Fries 2","10:00-22:00","4.5","Min - Rs2999"));
//                        homeVerModels.add(new HomeVerModel(R.drawable.fries3,"Fries 3","10:00-22:00","4.5","Min - Rs2999"));
//                        homeVerModels.add(new HomeVerModel(R.drawable.fries4,"Fries 4","10:00-22:00","4.5","Min - Rs2999"));
//
//                    } else if (position == 3) {
//                        ArrayList<HomeVerModel> homeVerModels = new ArrayList<>();
//
//                        homeVerModels.add(new HomeVerModel(R.drawable.icecream1,"Ice cream 1","10:00-22:00","4.5","Min - Rs2999"));
//                        homeVerModels.add(new HomeVerModel(R.drawable.icecream2,"Ice cream 2","10:00-22:00","4.5","Min - Rs2999"));
//                        homeVerModels.add(new HomeVerModel(R.drawable.icecream3,"Ice cream 3","10:00-22:00","4.5","Min - Rs2999"));
//
//                    } else if (position == 4) {
//                        ArrayList<HomeVerModel> homeVerModels = new ArrayList<>();
//
//                        homeVerModels.add(new HomeVerModel(R.drawable.sandwich1,"Sandwich 1","10:00-22:00","4.5","Min - Rs2999"));
//                        homeVerModels.add(new HomeVerModel(R.drawable.sandwich2,"Sandwich 2","10:00-22:00","4.5","Min - Rs2999"));
//                        homeVerModels.add(new HomeVerModel(R.drawable.sandwich3,"Sandwich 3","10:00-22:00","4.5","Min - Rs2999"));
//                        homeVerModels.add(new HomeVerModel(R.drawable.sandwich4,"Sandwich 4","10:00-22:00","4.5","Min - Rs2999"));
//
//                    }
//                }
//            });
//
//            if (select){
//                if (position == 0){
//                    holder.cardView.setBackgroundResource(R.drawable.change_bg);
//                    select = false;
//                }
//            }
//            else {
//                if (row_index == position){
//                    holder.cardView.setBackgroundResource(R.drawable.change_bg);
//                }else {
//                    holder.cardView.setBackgroundResource(R.drawable.default_bg);
//                }
//            }
//        }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.hor_img);
            name = itemView.findViewById(R.id.hor_text);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
