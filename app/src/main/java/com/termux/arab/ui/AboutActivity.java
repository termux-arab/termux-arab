package com.termux.arab.ui;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.termux.arab.R;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_about);
        getWindow().getDecorView().setLayoutDirection(android.view.View.LAYOUT_DIRECTION_RTL);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("ℹ️ حول التطبيق");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
