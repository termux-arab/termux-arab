package com.termux.arab.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.termux.arab.R;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * ماسح الشبكات الحقيقي - يفحص المنافذ والأجهزة فعلياً
 */
public class NetworkScannerActivity extends AppCompatActivity {

    private EditText hostInput, portStartInput, portEndInput;
    private Button scanBtn;
    private LinearLayout resultsLayout;
    private ProgressBar progress;
    private TextView statusText;
    private ExecutorService executor;

    // المنافذ الشائعة
    private static final int[] COMMON_PORTS = {21, 22, 23, 25, 53, 80, 110, 143, 443, 445, 993, 995, 8080, 8443, 3306, 5432, 22, 3389, 5900, 6379};
    private static final String[] PORT_NAMES = {"FTP", "SSH", "Telnet", "SMTP", "DNS", "HTTP", "POP3", "IMAP", "HTTPS", "SMB", "IMAPS", "POP3S", "HTTP-Alt", "HTTPS-Alt", "MySQL", "PostgreSQL", "SSH", "RDP", "VNC", "Redis"};

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_network_scanner);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("🌐 ماسح الشبكات");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        hostInput = findViewById(R.id.host_input);
        portStartInput = findViewById(R.id.port_start);
        portEndInput = findViewById(R.id.port_end);
        scanBtn = findViewById(R.id.btn_scan_network);
        resultsLayout = findViewById(R.id.scan_results);
        progress = findViewById(R.id.scan_network_progress);
        statusText = findViewById(R.id.scan_network_status);

        executor = Executors.newFixedThreadPool(20); // 20 مسار متوازي

        scanBtn.setOnClickListener(v -> startScan());
    }

    private void startScan() {
        String host = hostInput.getText().toString().trim();
        if (host.isEmpty()) {
            hostInput.setError("أدخل عنوان المضيف");
            return;
        }

        int startPort = 1, endPort = 1024;
        try {
            startPort = Integer.parseInt(portStartInput.getText().toString().trim());
            endPort = Integer.parseInt(portEndInput.getText().toString().trim());
        } catch (Exception e) {
            startPort = 1; endPort = 1024;
        }

        if (startPort < 1) startPort = 1;
        if (endPort > 65535) endPort = 65535;
        if (startPort > endPort) { int t = startPort; startPort = endPort; endPort = t; }

        resultsLayout.removeAllViews();
        scanBtn.setEnabled(false);
        scanBtn.setText("جارٍ الفحص...");
        progress.setVisibility(View.VISIBLE);
        statusText.setVisibility(View.VISIBLE);
        statusText.setText("📡 فحص " + host + " من " + startPort + " إلى " + endPort);

        addResultCard("🎯", "بدء الفحص", "المضيف: " + host + " | النطاق: " + startPort + "-" + endPort, 0xFF1976D2);

        final String finalHost = host;
        final int finalStart = startPort;
        final int finalEnd = endPort;
        final Handler handler = new Handler(Looper.getMainLooper());
        final int totalPorts = endPort - startPort + 1;
        final int[] scanned = {0};
        final int[] openCount = {0};

        for (int port = finalStart; port <= finalEnd; port++) {
            final int finalPort = port;
            executor.execute(() -> {
                boolean isOpen = scanPort(finalHost, finalPort, 500);
                scanned[0]++;

                if (isOpen) {
                    openCount[0]++;
                    String portName = getPortName(finalPort);
                    handler.post(() -> addResultCard("✅", "منفذ مفتوح: " + finalPort,
                        portName + " | " + finalHost + ":" + finalPort, 0xFF4CAF50));
                }

                int pct = (scanned[0] * 100) / totalPorts;
                handler.post(() -> {
                    progress.setProgress(pct);
                    statusText.setText("📡 تم فحص " + scanned[0] + "/" + totalPorts + " منفذ | مفتوح: " + openCount[0]);
                });

                if (scanned[0] >= totalPorts) {
                    handler.post(() -> {
                        progress.setVisibility(View.GONE);
                        scanBtn.setEnabled(true);
                        scanBtn.setText("🔄 إعادة الفحص");
                        statusText.setText("✅ اكتمل! تم فحص " + totalPorts + " منفذ | " + openCount[0] + " مفتوح");
                        addResultCard("📊", "ملخص الفحص",
                            "المضيف: " + finalHost + "\nالمنافذ المفحوصة: " + totalPorts + "\nالمفتوحة: " + openCount[0],
                            0xFF0F4C2A);
                    });
                }
            });
        }
    }

    private boolean scanPort(String host, int port, int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), timeout);
            socket.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getPortName(int port) {
        for (int i = 0; i < COMMON_PORTS.length; i++) {
            if (COMMON_PORTS[i] == port) return PORT_NAMES[i];
        }
        return "منفذ " + port;
    }

    private void addResultCard(String icon, String title, String desc, int color) {
        CardView card = new CardView(this);
        card.setRadius(12);
        card.setCardElevation(3);
        card.setUseCompatPadding(true);
        card.setCardBackgroundColor(color);

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(32, 20, 32, 20);

        TextView titleTv = new TextView(this);
        titleTv.setText(icon + " " + title);
        titleTv.setTextColor(0xFFFFFFFF);
        titleTv.setTextSize(15);
        titleTv.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        inner.addView(titleTv);

        TextView descTv = new TextView(this);
        descTv.setText(desc);
        descTv.setTextColor(0xFFE0E0E0);
        descTv.setTextSize(12);
        descTv.setPadding(0, 4, 0, 0);
        inner.addView(descTv);

        card.addView(inner);
        resultsLayout.addView(card);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) executor.shutdownNow();
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
