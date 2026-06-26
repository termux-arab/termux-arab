package com.termux.arab.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.termux.arab.R;
import com.termux.arab.core.LinuxEnv;
import com.termux.arab.core.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * شاشة مدير الحزم - تثبيت/إزالة أدوات Linux
 */
public class PackageManagerActivity extends AppCompatActivity {

    private PackageManager pkgManager;
    private PackageAdapter adapter;
    private EditText searchInput;
    private TextView statsText;
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_package_manager);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("📦 مدير الحزم");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        LinuxEnv env = new LinuxEnv(this);
        env.init();
        pkgManager = new PackageManager(this, env);

        searchInput = findViewById(R.id.pkg_search);
        statsText = findViewById(R.id.pkg_stats);
        RecyclerView rv = findViewById(R.id.pkg_recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<PackageManager.Package> all = pkgManager.getAvailablePackages();
        adapter = new PackageAdapter(new ArrayList<>(all), this::togglePackage);
        rv.setAdapter(adapter);

        updateStats();

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { filter(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });

        setupCategoryFilters();
    }

    private void filter(String query) {
        List<PackageManager.Package> all = pkgManager.getAvailablePackages();
        List<PackageManager.Package> filtered = new ArrayList<>();
        for (PackageManager.Package p : all) {
            boolean matchesCat = currentFilter.equals("all") || p.category.equals(currentFilter);
            boolean matchesQuery = query.isEmpty() ||
                p.name.toLowerCase().contains(query.toLowerCase()) ||
                p.nameAr.contains(query) ||
                p.description.toLowerCase().contains(query.toLowerCase());
            if (matchesCat && matchesQuery) filtered.add(p);
        }
        adapter.updateData(filtered);
    }

    private void setupCategoryFilters() {
        LinearLayout filters = findViewById(R.id.pkg_cat_filters);
        String[] cats = pkgManager.getCategories();
        for (String cat : cats) {
            TextView chip = new TextView(this);
            chip.setText(pkgManager.getCategoryName(cat));
            chip.setPadding(40, 20, 40, 20);
            chip.setBackgroundResource(R.drawable.chip_bg);
            chip.setTextSize(13);
            chip.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            chip.setTextColor(cat.equals(currentFilter) ? 0xFFFFFFFF : 0xFF0F4C2A);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMarginEnd(12);
            chip.setLayoutParams(params);

            chip.setOnClickListener(v -> {
                currentFilter = cat;
                filter(searchInput.getText().toString());
                // تحديث ألوان
                for (int i = 0; i < filters.getChildCount(); i++) {
                    TextView c = (TextView) filters.getChildAt(i);
                    c.setTextColor(0xFF0F4C2A);
                }
                chip.setTextColor(0xFFFFFFFF);
            });
            filters.addView(chip);
        }
    }

    private void togglePackage(PackageManager.Package pkg) {
        if (pkg.installed) {
            // إزالة
            pkgManager.markInstalled(pkg.name, false);
            pkg.installed = false;
            Toast.makeText(this, "🗑️ تمت إزالة " + pkg.name, Toast.LENGTH_SHORT).show();
        } else {
            // تثبيت (محاكاة)
            pkgManager.markInstalled(pkg.name, true);
            pkg.installed = true;
            Toast.makeText(this, "✅ تم تثبيت " + pkg.name + " (" + pkg.size + ")", Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
        updateStats();
    }

    private void updateStats() {
        int total = pkgManager.getAvailablePackages().size();
        int installed = pkgManager.getInstalledCount();
        statsText.setText("📊 " + installed + "/" + total + " حزمة مثبتة");
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }

    // === Adapter ===
    static class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.VH> {
        private final List<PackageManager.Package> packages;
        private final PkgClickListener listener;

        interface PkgClickListener { void onClick(PackageManager.Package pkg); }

        PackageAdapter(List<PackageManager.Package> packages, PkgClickListener listener) {
            this.packages = packages; this.listener = listener;
        }

        void updateData(List<PackageManager.Package> data) {
            packages.clear();
            packages.addAll(data);
            notifyDataSetChanged();
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_package, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            PackageManager.Package p = packages.get(pos);
            h.name.setText((p.popular ? "⭐ " : "📦 ") + p.name);
            h.nameAr.setText(p.nameAr);
            h.desc.setText(p.descriptionAr);
            h.size.setText("📐 " + p.size);

            if (p.installed) {
                h.btn.setText("✅ مثبت");
                h.btn.setBackgroundColor(0xFF4CAF50);
            } else {
                h.btn.setText("⬇ تثبيت");
                h.btn.setBackgroundColor(0xFF1976D2);
            }

            h.btn.setOnClickListener(v -> listener.onClick(p));
        }

        @Override public int getItemCount() { return packages.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView name, nameAr, desc, size;
            Button btn;
            VH(View v) {
                super(v);
                name = v.findViewById(R.id.pkg_name);
                nameAr = v.findViewById(R.id.pkg_name_ar);
                desc = v.findViewById(R.id.pkg_desc);
                size = v.findViewById(R.id.pkg_size);
                btn = v.findViewById(R.id.pkg_btn);
            }
        }
    }
}
