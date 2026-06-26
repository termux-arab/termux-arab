package com.termux.arab.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.termux.arab.R;
import com.termux.arab.core.ToolRegistry;
import com.termux.arab.model.Tool;
import java.util.List;

public class ToolListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_tool_list);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        String cat = getIntent().getStringExtra("category");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(ToolRegistry.getCategoryName(cat));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(
                new android.graphics.drawable.ColorDrawable(ToolRegistry.getCategoryColor(cat)));
        }

        List<Tool> tools = ToolRegistry.getByCategory(cat);
        RecyclerView rv = findViewById(R.id.tools_recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new ToolAdapter(tools, tool -> {
            Intent i = new Intent(this, ToolRunnerActivity.class);
            i.putExtra("tool_id", tool.id);
            startActivity(i);
        }));
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
