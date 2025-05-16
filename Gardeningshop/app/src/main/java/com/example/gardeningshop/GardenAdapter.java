package com.example.gardeningshop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GardenAdapter extends RecyclerView.Adapter<GardenAdapter.ViewHolder> {

    private List<GardenItem> items;

    public GardenAdapter(List<GardenItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GardenItem item = items.get(position);
        holder.itemName.setText(item.getName());
        holder.itemPrice.setText(item.getPrice());
        holder.itemImage.setImageResource(item.getImageResId());
        holder.itemQuantity.setText(String.valueOf(item.getQuantity()));

        holder.increaseQuantity.setVisibility(View.GONE);
        holder.decreaseQuantity.setVisibility(View.GONE);
        holder.addToCartButton.setVisibility(View.VISIBLE);

        holder.addToCartButton.setOnClickListener(v -> {
            GardenItem cartItem = new GardenItem(
                    item.getName(),
                    item.getPrice(),
                    item.getImageResId(),
                    item.getQuantity()
            );

            CartManager.getInstance().addItem(cartItem);

            Toast.makeText(v.getContext(),
                    item.getName() + " added to cart",
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
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
}
