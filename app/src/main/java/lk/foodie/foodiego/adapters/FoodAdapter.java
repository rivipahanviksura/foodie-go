package lk.foodie.foodiego.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import lk.foodie.foodiego.R;
import lk.foodie.foodiego.models.Food;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private Context context;
    private List<Food> foodList;

    public FoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.update_foods, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);

        holder.textFoodName.setText(food.getName());
        holder.textFoodDescription.setText(food.getDescription());
        holder.textFoodPrice.setText("$" + food.getPrice());

        Log.d("FoodAdapter", "Loading Food: " + food.getName());

        // Load image using Glide
//        Glide.with(context)
//                .load(food.getImageUrl())
//                .placeholder(R.drawable.placeholder_image) // Placeholder image
//                .error(R.drawable.error_image) // Error image
//                .into(holder.imageFood);

        if (food.getImageUrl() != null && !food.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(food.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.imageFood);
        } else {
            holder.imageFood.setImageResource(R.drawable.placeholder_image);
        }
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView textFoodName, textFoodDescription, textFoodPrice;
        ImageView imageFood;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            textFoodName = itemView.findViewById(R.id.text_food_name);
            textFoodDescription = itemView.findViewById(R.id.text_food_description);
            textFoodPrice = itemView.findViewById(R.id.text_food_price);
            imageFood = itemView.findViewById(R.id.image_food);
        }
    }

    public void updateList(List<Food> newList) {
        foodList.clear();
        foodList.addAll(newList);
        notifyDataSetChanged();
    }
}
