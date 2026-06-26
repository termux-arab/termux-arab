package com.termux.arab.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.termux.arab.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * مراقب النظام الحقيقي - CPU/RAM/Storage/Battery/Network
 */
public class SystemMonitorActivity extends AppCompatActivity {

    private Handler handler;
    private boolean running = true;
    private TextView cpuUsage, ramUsage, ramTotal, ramFree, storageInfo, batteryInfo,
        networkIp, networkMac, uptimeText, cpuCores, cpuInfo;
    private ProgressBar cpuBar, ramBar;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_system_monitor);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("📊 مراقب النظام");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        cpuUsage = findViewById(R.id.cpu_usage_text);
        cpuBar = findViewById(R.id.cpu_bar);
        ramUsage = findViewById(R.id.ram_usage_text);
        ramBar = findViewById(R.id.ram_bar);
        ramTotal = findViewById(R.id.ram_total);
        ramFree = findViewById(R.id.ram_free);
        storageInfo = findViewById(R.id.storage_info);
        batteryInfo = findViewById(R.id.battery_info);
        networkIp = findViewById(R.id.network_ip);
        networkMac = findViewById(R.id.network_mac);
        uptimeText = findViewById(R.id.uptime_text);
        cpuCores = findViewById(R.id.cpu_cores);
        cpuInfo = findViewById(R.id.cpu_info);

        handler = new Handler(Looper.getMainLooper());

        // تحديث مرة واحدة للمعلومات الثابتة
        updateStaticInfo();

        // تحديث دوري للمعلومات المتغيرة
        startMonitoring();
    }

    private void updateStaticInfo() {
        // CPU info
        cpuCores.setText("cores: " + Runtime.getRuntime().availableProcessors());
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/cpuinfo"));
            String line = reader.readLine();
            if (line != null) cpuInfo.setText(line.replace("Hardware\t: ", "").replace("model name\t: ", ""));
            reader.close();
        } catch (Exception e) {
            cpuInfo.setText(android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL);
        }

        // RAM total
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        ramTotal.setText("الكل: " + formatSize(mi.totalMem));
        ramFree.setText("المتاحة: " + formatSize(mi.availMem));

        // Storage
        StatFs stat = new StatFs(getFilesDir().getAbsolutePath());
        long total = stat.getTotalBytes();
        long free = stat.getFreeBytes();
        long used = total - free;
        storageInfo.setText(formatSize(used) + " / " + formatSize(total) + " (" + (used * 100 / total) + "%)");

        // Network
        String ip = getLocalIp();
        String mac = getMacAddress();
        networkIp.setText(ip != null ? ip : "غير متاح");
        networkMac.setText(mac != null ? mac : "غير متاح");

        // Battery
        int level = getBatteryLevel();
        batteryInfo.setText(level + "%");

        // Uptime
        long uptime = android.os.SystemClock.elapsedRealtime() / 1000;
        long hours = uptime / 3600;
        long mins = (uptime % 3600) / 60;
        uptimeText.setText(hours + " ساعة " + mins + " دقيقة");
    }

    private void startMonitoring() {
        Runnable monitor = new Runnable() {
            @Override
            public void run() {
                if (!running) return;

                // CPU usage (تقريبي)
                int cpu = getCpuUsage();
                cpuUsage.setText(cpu + "%");
                cpuBar.setProgress(cpu);

                // RAM usage
                ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                am.getMemoryInfo(mi);
                int ramPct = (int) ((mi.totalMem - mi.availMem) * 100 / mi.totalMem);
                ramUsage.setText(ramPct + "% (" + formatSize(mi.totalMem - mi.availMem) + ")");
                ramBar.setProgress(ramPct);

                handler.postDelayed(this, 2000);
            }
        };
        handler.post(monitor);
    }

    private int getCpuUsage() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/loadavg"));
            String line = reader.readLine();
            reader.close();
            String[] parts = line.split(" ");
            double load = Double.parseDouble(parts[0]);
            int cores = Runtime.getRuntime().availableProcessors();
            return Math.min(100, (int) (load / cores * 100));
        } catch (Exception e) {
            return 0;
        }
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1048576) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1073741824) return String.format("%.1f MB", bytes / 1048576.0);
        return String.format("%.1f GB", bytes / 1073741824.0);
    }

    private String getLocalIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback()) continue;
                Enumeration<InetAddress> addrs = iface.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (!addr.isLoopbackAddress() && addr.getHostAddress().contains(".")) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {}
        return null;
    }

    private String getMacAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback()) continue;
                byte[] mac = iface.getHardwareAddress();
                if (mac != null) {
                    StringBuilder sb = new StringBuilder();
                    for (byte b : mac) sb.append(String.format("%02X:", b));
                    if (sb.length() > 0) sb.setLength(sb.length() - 1);
                    return sb.toString();
                }
            }
        } catch (Exception e) {}
        return null;
    }

    private int getBatteryLevel() {
        try {
            android.content.IntentFilter filter = new android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED);
            android.content.Intent battery = registerReceiver(null, filter);
            int level = battery.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1);
            int scale = battery.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1);
            return (int) (level * 100.0 / scale);
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    protected void onDestroy() {
        running = false;
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
