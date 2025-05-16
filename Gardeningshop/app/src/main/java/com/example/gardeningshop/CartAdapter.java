package com.example.gardeningshop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<GardenItem> cartItems;
    private OnCartUpdatedListener cartUpdatedListener;

    public CartAdapter(List<GardenItem> cartItems, OnCartUpdatedListener cartUpdatedListener) {
        this.cartItems = cartItems;
        this.cartUpdatedListener = cartUpdatedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GardenItem item = cartItems.get(position);
        holder.itemName.setText(item.getName());
        holder.itemPrice.setText(item.getPrice());
        holder.itemImage.setImageResource(item.getImageResId());
        holder.itemQuantity.setText(String.valueOf(item.getQuantity()));

        holder.increaseQuantity.setVisibility(View.VISIBLE);
        holder.decreaseQuantity.setVisibility(View.VISIBLE);
        holder.addToCartButton.setVisibility(View.GONE);

        holder.increaseQuantity.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            holder.itemQuantity.setText(String.valueOf(item.getQuantity()));
            cartUpdatedListener.onCartUpdated();
        });

        holder.decreaseQuantity.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                holder.itemQuantity.setText(String.valueOf(item.getQuantity()));
            } else {
                CartManager.getInstance().removeItem(item);

                cartItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItems.size());

            }
            cartUpdatedListener.onCartUpdated();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice, itemQuantity;
        ImageView itemImage;
        Button increaseQuantity, decreaseQuantity, addToCartButton;

        ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            increaseQuantity = itemView.findViewById(R.id.increaseQuantity);
            decreaseQuantity = itemView.findViewById(R.id.decreaseQuantity);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }

    public interface OnCartUpdatedListener {
        void onCartUpdated();
    }
}