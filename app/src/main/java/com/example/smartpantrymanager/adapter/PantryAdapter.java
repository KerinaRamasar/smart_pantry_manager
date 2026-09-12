package com.example.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.PantryItem;

import java.util.List;

/**
 * Feeds a list of PantryItem objects into a RecyclerView.
 * Uses a listener interface so the Activity decides what happens
 * when Edit/Delete is tapped, keeping this class focused only on display.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    // Callback interface - the hosting Activity implements this
    public interface OnPantryItemActionListener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private List<PantryItem> items;
    private final OnPantryItemActionListener listener;

    public PantryAdapter(List<PantryItem> items, OnPantryItemActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    // Call this after the underlying list changes (e.g. after add/edit/delete)
    public void updateData(List<PantryItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.name.setText(item.getName());

        String expiryText = (item.getExpiryDate() == null || item.getExpiryDate().isEmpty())
                ? "" : " - Expires: " + item.getExpiryDate();
        holder.quantity.setText(item.getQuantity() + " " + item.getUnit() + expiryText);

        holder.editButton.setOnClickListener(v -> listener.onEdit(item));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView name, quantity;
        View editButton, deleteButton;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textItemName);
            quantity = itemView.findViewById(R.id.textItemQuantity);
            editButton = itemView.findViewById(R.id.buttonEdit);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
        }
    }
}