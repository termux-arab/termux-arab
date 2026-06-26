package com.termux.arab.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.termux.arab.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * اختبار HTTP/API حقيقي - يرسل طلبات فعلية ويعرض النتائج
 */
public class HttpTesterActivity extends AppCompatActivity {

    private EditText urlInput, headersInput, bodyInput;
    private Spinner methodSpinner;
    private TextView responseCode, responseTime, responseSize, responseText;
    private ScrollView responseScroll;
    private Button sendBtn;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_http_tester);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("🌐 اختبار HTTP");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        urlInput = findViewById(R.id.http_url);
        headersInput = findViewById(R.id.http_headers);
        bodyInput = findViewById(R.id.http_body);
        methodSpinner = findViewById(R.id.http_method);
        responseCode = findViewById(R.id.http_response_code);
        responseTime = findViewById(R.id.http_response_time);
        responseSize = findViewById(R.id.http_response_size);
        responseText = findViewById(R.id.http_response);
        responseScroll = findViewById(R.id.http_scroll);
        sendBtn = findViewById(R.id.http_send);

        executor = Executors.newSingleThreadExecutor();

        String[] methods = {"GET", "POST", "PUT", "DELETE", "HEAD", "PATCH"};
        methodSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, methods));

        sendBtn.setOnClickListener(v -> sendRequest());
    }

    private void sendRequest() {
        String urlStr = urlInput.getText().toString().trim();
        if (urlStr.isEmpty()) {
            urlInput.setError("أدخل الرابط");
            return;
        }
        if (!urlStr.startsWith("http")) urlStr = "https://" + urlStr;

        final String method = methodSpinner.getSelectedItem().toString();
        final String headers = headersInput.getText().toString();
        final String body = bodyInput.getText().toString();
        final String finalUrl = urlStr;

        sendBtn.setEnabled(false);
        sendBtn.setText("جارٍ الإرسال...");
        responseText.setText("🔄 جارٍ الإرسال...\n");
        responseCode.setText("--");
        responseTime.setText("--");
        responseSize.setText("--");

        final long startTime = System.currentTimeMillis();
        final Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                URL url = new URL(finalUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(method);
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(30000);

                // Headers
                if (!headers.isEmpty()) {
                    for (String line : headers.split("\n")) {
                        int colon = line.indexOf(':');
                        if (colon > 0) {
                            conn.setRequestProperty(line.substring(0, colon).trim(), line.substring(colon + 1).trim());
                        }
                    }
                }

                // Body
                if ((method.equals("POST") || method.equals("PUT") || method.equals("PATCH")) && !body.isEmpty()) {
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(body.getBytes("UTF-8"));
                }

                int code = conn.getResponseCode();
                long elapsed = System.currentTimeMillis() - startTime;

                BufferedReader reader;
                if (code >= 200 && code < 400) {
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                } else {
                    reader = new BufferedReader(new InputStreamReader(conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream(), "UTF-8"));
                }

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line).append("\n");
                }
                reader.close();

                final String responseStr = response.toString();
                final int sizeBytes = responseStr.getBytes("UTF-8").length;
                final int finalCode = code;
                final long finalElapsed = elapsed;

                handler.post(() -> {
                    int codeColor;
                    if (finalCode >= 200 && finalCode < 300) codeColor = 0xFF4CAF50;
                    else if (finalCode >= 300 && finalCode < 400) codeColor = 0xFFFF9800;
                    else codeColor = 0xFFFF5722;

                    responseCode.setText(String.valueOf(finalCode));
                    responseCode.setTextColor(codeColor);
                    responseTime.setText(finalElapsed + " ms");
                    responseSize.setText(formatSize(sizeBytes));

                    StringBuilder fullResponse = new StringBuilder();
                    fullResponse.append("═══ Headers ═══\n");
                    for (String key : conn.getHeaderFields().keySet()) {
                        if (key != null) fullResponse.append(key).append(": ").append(conn.getHeaderField(key)).append("\n");
                    }
                    fullResponse.append("\n═══ Body ═══\n");
                    fullResponse.append(responseStr);

                    responseText.setText(fullResponse.toString());
                    sendBtn.setEnabled(true);
                    sendBtn.setText("▶ إرسال");
                });

            } catch (Exception e) {
                final long elapsed = System.currentTimeMillis() - startTime;
                handler.post(() -> {
                    responseCode.setText("ERR");
                    responseCode.setTextColor(0xFFFF5722);
                    responseTime.setText(elapsed + " ms");
                    responseSize.setText("0 B");
                    responseText.setText("❌ خطأ: " + e.getMessage() + "\n\nالنوع: " + e.getClass().getSimpleName());
                    sendBtn.setEnabled(true);
                    sendBtn.setText("▶ إرسال");
                });
            }
        });
    }

    private String formatSize(int bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1048576) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / 1048576.0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) executor.shutdown();
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
