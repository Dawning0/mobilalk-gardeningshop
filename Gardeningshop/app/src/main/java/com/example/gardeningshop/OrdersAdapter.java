package com.example.gardeningshop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
    private List<Order> ordersList;
    private OrderClickListener orderClickListener;

    public OrdersAdapter(List<Order> ordersList, OrderClickListener orderClickListener) {
        this.ordersList = ordersList;
        this.orderClickListener = orderClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = ordersList.get(position);
        holder.orderEmail.setText(order.getEmail());
        holder.orderDate.setText(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(order.getTimestamp()));

        holder.itemView.setOnClickListener(v -> orderClickListener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderEmail, orderDate;

        public ViewHolder(View itemView) {
            super(itemView);
            orderEmail = itemView.findViewById(R.id.orderEmail);
            orderDate = itemView.findViewById(R.id.orderDate);
        }
    }

    public interface OrderClickListener {
        void onOrderClick(Order order);
    }
}