package com.termux.arab.ui;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.termux.arab.R;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * أدوات كلمات المرور الحقيقية - توليد، تحليل، تجزئة
 */
public class PasswordToolsActivity extends AppCompatActivity {

    private EditText passwordInput, lengthInput;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_password_tools);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("🔐 أدوات كلمات المرور");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        passwordInput = findViewById(R.id.password_input);
        lengthInput = findViewById(R.id.gen_length);
        resultText = findViewById(R.id.password_result);
        resultText.setMovementMethod(new ScrollingMovementMethod());

        Button analyzeBtn = findViewById(R.id.btn_analyze);
        Button generateBtn = findViewById(R.id.btn_generate);
        Button hashBtn = findViewById(R.id.btn_hash);
        Button clearBtn = findViewById(R.id.btn_clear_pw);

        analyzeBtn.setOnClickListener(v -> analyzePassword());
        generateBtn.setOnClickListener(v -> generatePassword());
        hashBtn.setOnClickListener(v -> hashPassword());
        clearBtn.setOnClickListener(v -> {
            passwordInput.setText("");
            resultText.setText("");
        });
    }

    private void analyzePassword() {
        String pw = passwordInput.getText().toString();
        if (pw.isEmpty()) {
            passwordInput.setError("أدخل كلمة مرور");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("═══ تحليل كلمة المرور ═══\n\n");

        // الطول
        sb.append("📏 الطول: ").append(pw.length()).append(" حرف\n");

        // القوة
        int score = 0;
        List<String> weaknesses = new ArrayList<>();

        if (pw.length() >= 8) score += 20; else weaknesses.add("أقل من 8 أحرف");
        if (pw.length() >= 12) score += 10; else weaknesses.add("يفضل 12+ حرف");
        if (Pattern.compile("[a-z]").matcher(pw).find()) score += 15; else weaknesses.add("لا توجد أحرف صغيرة");
        if (Pattern.compile("[A-Z]").matcher(pw).find()) score += 15; else weaknesses.add("لا توجد أحرف كبيرة");
        if (Pattern.compile("[0-9]").matcher(pw).find()) score += 15; else weaknesses.add("لا توجد أرقام");
        if (Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]").matcher(pw).find()) score += 20; else weaknesses.add("لا توجد رموز خاصة");
        if (!pw.toLowerCase().matches(".*(password|123456|qwerty|admin|letmein|welcome|monkey|dragon).*")) score += 5;
        else weaknesses.add("⚠️ كلمة مرور شائعة!");

        sb.append("\n📊 درجة القوة: ").append(score).append("/100\n");

        String strength;
        int color;
        if (score >= 80) { strength = "قوية جداً ✅"; color = 0xFF4CAF50; }
        else if (score >= 60) { strength = "قيفة 👍"; color = 0xFF8BC34A; }
        else if (score >= 40) { strength = "متوسطة ⚠️"; color = 0xFFFF9800; }
        else { strength = "ضعيفة ❌"; color = 0xFFFF5722; }

        sb.append("💪 التقييم: ").append(strength).append("\n");

        // وقت الكسر التقديري
        sb.append("\n⏱️ وقت الكسر التقديري:\n");
        long combinations = (long) Math.pow(getCharsetSize(pw), pw.length());
        long secondsPerGuess = 10000000000L; // 10 billion guesses/sec
        long crackTime = combinations / 2 / secondsPerGuess;
        sb.append("  ").append(formatTime(crackTime)).append("\n");

        // الضعف
        if (!weaknesses.isEmpty()) {
            sb.append("\n⚠️ نقاط الضعف:\n");
            for (String w : weaknesses) sb.append("  • ").append(w).append("\n");
        }

        // التوصيات
        sb.append("\n💡 توصيات:\n");
        sb.append("  • استخدم 12+ حرف\n");
        sb.append("  • امزج أحرف كبيرة وصغيرة\n");
        sb.append("  • أضف أرقام ورموز\n");
        sb.append("  • لا تستخدم معلومات شخصية\n");

        resultText.setText(sb.toString());
    }

    private void generatePassword() {
        int length = 16;
        try { length = Integer.parseInt(lengthInput.getText().toString()); } catch (Exception e) {}
        if (length < 4) length = 4;
        if (length > 128) length = 128;

        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String symbols = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        String all = upper + lower + digits + symbols;

        StringBuilder sb = new StringBuilder();
        sb.append("═══ كلمات مرور مولّدة ═══\n\n");

        for (int i = 0; i < 5; i++) {
            StringBuilder pw = new StringBuilder();
            java.util.Random rnd = new java.util.Random();
            // ضمان وجود كل نوع
            pw.append(upper.charAt(rnd.nextInt(upper.length())));
            pw.append(lower.charAt(rnd.nextInt(lower.length())));
            pw.append(digits.charAt(rnd.nextInt(digits.length())));
            pw.append(symbols.charAt(rnd.nextInt(symbols.length())));
            for (int j = 4; j < length; j++) {
                pw.append(all.charAt(rnd.nextInt(all.length())));
            }
            // خلط
            List<Character> chars = new ArrayList<>();
            for (char c : pw.toString().toCharArray()) chars.add(c);
            java.util.Collections.shuffle(chars);
            StringBuilder shuffled = new StringBuilder();
            for (char c : chars) shuffled.append(c);

            sb.append("🔑 ").append(shuffled).append("\n\n");
        }

        resultText.setText(sb.toString());
    }

    private void hashPassword() {
        String pw = passwordInput.getText().toString();
        if (pw.isEmpty()) {
            passwordInput.setError("أدخل كلمة مرور");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("═══ تجزئة كلمة المرور ═══\n\n");

        sb.append("🔍 النص: ").append(pw).append("\n\n");

        sb.append("MD5:\n").append(hash(pw, "MD5")).append("\n\n");
        sb.append("SHA-1:\n").append(hash(pw, "SHA-1")).append("\n\n");
        sb.append("SHA-256:\n").append(hash(pw, "SHA-256")).append("\n\n");
        sb.append("SHA-512:\n").append(hash(pw, "SHA-512")).append("\n");

        resultText.setText(sb.toString());
    }

    private String hash(String input, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] digest = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return "خطأ: " + e.getMessage();
        }
    }

    private int getCharsetSize(String pw) {
        int size = 0;
        if (pw.matches(".*[a-z].*")) size += 26;
        if (pw.matches(".*[A-Z].*")) size += 26;
        if (pw.matches(".*[0-9].*")) size += 10;
        if (pw.matches(".*[^a-zA-Z0-9].*")) size += 32;
        return Math.max(size, 1);
    }

    private String formatTime(long seconds) {
        if (seconds < 1) return "فوري";
        if (seconds < 60) return seconds + " ثانية";
        if (seconds < 3600) return (seconds / 60) + " دقيقة";
        if (seconds < 86400) return (seconds / 3600) + " ساعة";
        if (seconds < 2592000) return (seconds / 86400) + " يوم";
        if (seconds < 31536000) return (seconds / 2592000) + " شهر";
        if (seconds < 3153600000L) return (seconds / 31536000) + " سنة";
        return "قرون! 🔒";
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
