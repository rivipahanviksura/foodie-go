package lk.foodie.foodiego.adapters;

import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import lk.foodie.foodiego.R;
import lk.foodie.foodiego.models.CartModel;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    List<CartModel> list;
    private OnItemDeleteListener onItemDeleteListener;
    public CartAdapter(List<CartModel> list,OnItemDeleteListener onItemDeleteListener) {
        this.list = list;
        this.onItemDeleteListener = onItemDeleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.mycart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartModel cartItem = list.get(position);
        Log.i("cartImg","img"+ list.get(position).getImage());

        Glide.with(holder.itemView.getContext())
                .load(list.get(position).getImage())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);

        holder.name.setText(list.get(position).getName());
        holder.rating.setText(list.get(position).getRating());
        holder.price.setText(String.valueOf(list.get(position).getPrice()));
        holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));

        holder.btnDecrease.setOnClickListener(v -> {
            int quantity = cartItem.getQuantity();
            if (quantity > 1) { // Prevent quantity from going below 1
                quantity--;
                cartItem.setQuantity(quantity);
                holder.tvQuantity.setText(String.valueOf(quantity)); // Update displayed quantity
            }
        });

        holder.btnIncrease.setOnClickListener(v -> {
            int quantity = cartItem.getQuantity();
            quantity++;
            cartItem.setQuantity(quantity);
            holder.tvQuantity.setText(String.valueOf(quantity)); // Update displayed quantity
        });

        holder.deleteButton.setOnClickListener(v -> {
            Log.e("MyCartFragment", "Attempting to delete item at position: " + position);

            if (onItemDeleteListener != null) {
                onItemDeleteListener.onItemDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name,rating,price,tvQuantity;
        Button deleteButton,btnDecrease, btnIncrease;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.detailed_img);
            name = itemView.findViewById(R.id.detailed_name);
            rating = itemView.findViewById(R.id.detailed_rating);
            price = itemView.findViewById(R.id.detailed_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            deleteButton = itemView.findViewById(R.id.btn_delete);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
        }
    }
    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }
}
