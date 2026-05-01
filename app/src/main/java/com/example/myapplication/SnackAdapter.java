package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.Snack;

import java.util.List;

public class SnackAdapter extends RecyclerView.Adapter<SnackAdapter.SnackViewHolder> {

    private final List<Snack> snacks;
    private final OnSnackQuantityChangeListener listener;

    public interface OnSnackQuantityChangeListener {
        void onQuantityChanged();
    }

    public SnackAdapter(List<Snack> snacks, OnSnackQuantityChangeListener listener) {
        this.snacks = snacks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SnackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_snack, parent, false);
        return new SnackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SnackViewHolder holder, int position) {
        Snack snack = snacks.get(position);
        holder.name.setText(snack.getName());
        holder.price.setText(String.format("$%.2f", snack.getPrice()));
        holder.quantity.setText(String.valueOf(snack.getQuantity()));

        int resId = holder.itemView.getContext().getResources().getIdentifier(snack.getImage(), "drawable", holder.itemView.getContext().getPackageName());
        if (resId != 0) {
            holder.image.setImageResource(resId);
        } else {
            holder.image.setImageResource(R.drawable.image);
        }

        holder.plus.setOnClickListener(v -> {
            snack.setQuantity(snack.getQuantity() + 1);
            holder.quantity.setText(String.valueOf(snack.getQuantity()));
            if (listener != null) listener.onQuantityChanged();
        });

        holder.minus.setOnClickListener(v -> {
            if (snack.getQuantity() > 0) {
                snack.setQuantity(snack.getQuantity() - 1);
                holder.quantity.setText(String.valueOf(snack.getQuantity()));
                if (listener != null) listener.onQuantityChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return snacks.size();
    }

    public static class SnackViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price, quantity;
        ImageButton plus, minus;

        public SnackViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.snack_image);
            name = itemView.findViewById(R.id.snack_name);
            price = itemView.findViewById(R.id.snack_price);
            quantity = itemView.findViewById(R.id.snack_quantity);
            plus = itemView.findViewById(R.id.btn_plus);
            minus = itemView.findViewById(R.id.btn_minus);
        }
    }
}
