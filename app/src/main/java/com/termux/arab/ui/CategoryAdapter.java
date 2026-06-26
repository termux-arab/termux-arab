package com.termux.arab.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.termux.arab.R;
import com.termux.arab.core.ToolRegistry;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {
    private final String[] cats;
    private final ClickListener listener;

    interface ClickListener { void onClick(String cat); }

    CategoryAdapter(String[] cats, ClickListener listener) {
        this.cats = cats; this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        String cat = cats[pos];
        h.icon.setText(ToolRegistry.getCategoryIcon(cat));
        h.name.setText(ToolRegistry.getCategoryName(cat));
        h.count.setText(ToolRegistry.getByCategory(cat).size() + " أداة");
        h.card.setBackgroundColor(ToolRegistry.getCategoryColor(cat));
        h.itemView.setOnClickListener(v -> listener.onClick(cat));
    }

    @Override public int getItemCount() { return cats.length; }

    static class VH extends RecyclerView.ViewHolder {
        TextView icon, name, count;
        LinearLayout card;
        VH(View v) {
            super(v);
            icon = v.findViewById(R.id.cat_icon);
            name = v.findViewById(R.id.cat_name);
            count = v.findViewById(R.id.cat_count);
            card = v.findViewById(R.id.cat_card);
        }
    }
}
