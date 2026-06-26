package com.termux.arab.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.termux.arab.R;
import com.termux.arab.core.CommandExecutor;
import java.util.ArrayList;
import java.util.List;

public class TerminalActivity extends AppCompatActivity {
    private CommandExecutor executor;
    private TextView output;
    private EditText input;
    private ScrollView scroll;
    private List<String> history = new ArrayList<>();
    private int historyIndex = -1;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_terminal);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("💻 الطرفية");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        executor = new CommandExecutor();
        output = findViewById(R.id.terminal_output);
        input = findViewById(R.id.terminal_input);
        scroll = findViewById(R.id.terminal_scroll);
        Button sendBtn = findViewById(R.id.btn_send);

        // رسالة ترحيب
        output.setText("┌─────────────────────────────────┐\n");
        output.append("│  تيرمكس العرب v1.0.0            │\n");
        output.append("│  مرحباً بك!                     │\n");
        output.append("│  اكتب help للمساعدة             │\n");
        output.append("└─────────────────────────────────┘\n\n");
        output.append("$ ");

        sendBtn.setOnClickListener(v -> executeCommand());

        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                executeCommand();
                return true;
            }
            return false;
        });

        // تاريخ الأوامر
        input.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP && !history.isEmpty()) {
                    if (historyIndex < history.size() - 1) historyIndex++;
                    input.setText(history.get(history.size() - 1 - historyIndex));
                    input.setSelection(input.getText().length());
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && !history.isEmpty()) {
                    if (historyIndex > 0) { historyIndex--; input.setText(history.get(history.size() - 1 - historyIndex)); }
                    else { historyIndex = -1; input.setText(""); }
                    input.setSelection(input.getText().length());
                    return true;
                }
            }
            return false;
        });
    }

    private void executeCommand() {
        String cmd = input.getText().toString().trim();
        if (cmd.isEmpty()) return;

        history.add(cmd);
        historyIndex = -1;
        output.append(cmd + "\n");
        input.setText("");

        if (cmd.equals("clear") || cmd.equals("امسح")) {
            output.setText("$ ");
            return;
        }
        if (cmd.equals("help") || cmd.equals("مساعدة")) {
            output.append("━━━ الأوامر المتاحة ━━━\n");
            output.append("help / مساعدة - عرض هذه القائمة\n");
            output.append("clear / امسح - مسح الشاشة\n");
            output.append("ls / ملفات - عرض الملفات\n");
            output.append("pwd / حالية - المسار الحالي\n");
            output.append("date / وقت - التاريخ والوقت\n");
            output.append("whoami / من_أنا - اسم المستخدم\n");
            output.append("exit / خروج - الخروج\n\n");
            output.append("━━━ أوامر Linux ━━━\n");
            output.append("echo, cat, grep, find, ps, df, free, etc.\n\n");
            output.append("$ ");
            return;
        }
        if (cmd.equals("exit") || cmd.equals("خروج")) {
            finish();
            return;
        }

        executor.execute(cmd, this, new CommandExecutor.Listener() {
            @Override
            public void onOutput(String line) {
                runOnUiThread(() -> {
                    output.append(line + "\n");
                    scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
                });
            }
            @Override
            public void onComplete(int code) {
                runOnUiThread(() -> {
                    output.append("$ ");
                    scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) executor.shutdown();
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
