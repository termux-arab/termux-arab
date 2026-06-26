package com.termux.arab.core;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * مدير الحزم - يثبّت أدوات إضافية في التطبيق
 * يحاكي pkg install عبر تنزيل وتثبيت أدوات
 */
public class PackageManager {

    private final Context ctx;
    private final LinuxEnv linuxEnv;
    private final SharedPreferences prefs;

    public static class Package {
        public String name;
        public String nameAr;
        public String description;
        public String descriptionAr;
        public String size;
        public String category;
        public boolean installed;
        public boolean popular;

        public Package(String name, String nameAr, String desc, String descAr, String size, String cat, boolean popular) {
            this.name = name; this.nameAr = nameAr; this.description = desc;
            this.descriptionAr = descAr; this.size = size; this.category = cat; this.popular = popular;
        }
    }

    public PackageManager(Context ctx, LinuxEnv linuxEnv) {
        this.ctx = ctx;
        this.linuxEnv = linuxEnv;
        this.prefs = ctx.getSharedPreferences("packages", Context.MODE_PRIVATE);
    }

    public List<Package> getAvailablePackages() {
        List<Package> pkgs = new ArrayList<>();

        // أدوات أساسية
        pkgs.add(new Package("coreutils", "أدوات أساسية", "GNU core utilities", "أدوات GNU الأساسية (ls, cp, mv, rm)", "5 MB", "core", true));
        pkgs.add(new Package("bash", "Bash Shell", "Bourne Again Shell", "صدفة Bash الكاملة", "3 MB", "shell", true));
        pkgs.add(new Package("zsh", "Zsh Shell", "Z shell", "صدفة Zsh المتقدمة", "4 MB", "shell", false));
        pkgs.add(new Package("nano", "Nano Editor", "Text editor", "محرر نصوص سهل", "1 MB", "editor", true));
        pkgs.add(new Package("vim", "Vim Editor", "Vi IMproved", "محرر Vim المتقدم", "8 MB", "editor", false));
        pkgs.add(new Package("tmux", "Tmux", "Terminal multiplexer", "مدير نوافذ طرفية", "2 MB", "terminal", true));

        // لغات برمجة
        pkgs.add(new Package("python", "Python", "Python 3", "لغة Python 3 البرمجية", "45 MB", "lang", true));
        pkgs.add(new Package("nodejs", "Node.js", "JavaScript runtime", "بيئة JavaScript", "40 MB", "lang", true));
        pkgs.add(new Package("ruby", "Ruby", "Ruby language", "لغة Ruby", "25 MB", "lang", false));
        pkgs.add(new Package("golang", "Go", "Go language", "لغة Go", "60 MB", "lang", false));
        pkgs.add(new Package("rust", "Rust", "Rust language", "لغة Rust", "80 MB", "lang", false));
        pkgs.add(new Package("php", "PHP", "PHP language", "لغة PHP", "30 MB", "lang", false));
        pkgs.add(new Package("perl", "Perl", "Perl language", "لغة Perl", "20 MB", "lang", false));

        // أدوات تطوير
        pkgs.add(new Package("git", "Git", "Version control", "التحكم بالإصدارات", "15 MB", "dev", true));
        pkgs.add(new Package("clang", "Clang", "C/C++ compiler", "مترجم C/C++", "50 MB", "dev", false));
        pkgs.add(new Package("make", "Make", "Build tool", "أداة بناء", "2 MB", "dev", false));
        pkgs.add(new Package("cmake", "CMake", "Build system", "نظام بناء", "10 MB", "dev", false));
        pkgs.add(new Package("jq", "jq", "JSON processor", "معالج JSON", "1 MB", "dev", true));

        // شبكات
        pkgs.add(new Package("nmap", "Nmap", "Network scanner", "ماسح الشبكات", "10 MB", "net", true));
        pkgs.add(new Package("curl", "curl", "HTTP client", "عميل HTTP", "2 MB", "net", true));
        pkgs.add(new Package("wget", "wget", "File downloader", "منزّل الملفات", "2 MB", "net", true));
        pkgs.add(new Package("openssh", "OpenSSH", "SSH client/server", "عميل وخادم SSH", "5 MB", "net", true));
        pkgs.add(new Package("rsync", "rsync", "File sync", "مزامنة الملفات", "1 MB", "net", false));
        pkgs.add(new Package("tcpdump", "tcpdump", "Packet capture", "التقاط الحزم", "2 MB", "net", false));
        pkgs.add(new Package("socat", "socat", "Network relay", "مرحّل شبكة", "1 MB", "net", false));

        // اختبار اختراق
        pkgs.add(new Package("metasploit", "Metasploit", "Penetration framework", "إطار اختبار اختراق", "100 MB", "pentest", true));
        pkgs.add(new Package("sqlmap", "SQLMap", "SQL injection tool", "أداة حقن SQL", "10 MB", "pentest", true));
        pkgs.add(new Package("hydra", "Hydra", "Password cracker", "تخمين كلمات المرور", "2 MB", "pentest", false));
        pkgs.add(new Package("nikto", "Nikto", "Web scanner", "ماسح الويب", "5 MB", "pentest", false));
        pkgs.add(new Package("dirb", "Dirb", "Directory brute-forcer", "تخمين المجلدات", "1 MB", "pentest", false));
        pkgs.add(new Package("gobuster", "Gobuster", "Directory scanner", "ماسح المسارات السريع", "5 MB", "pentest", false));
        pkgs.add(new Package("aircrack-ng", "Aircrack-ng", "WiFi security", "أدوات أمان الواي فاي", "8 MB", "pentest", false));
        pkgs.add(new Package("john", "John the Ripper", "Password cracker", "كسر كلمات المرور", "5 MB", "pentest", false));
        pkgs.add(new Package("hashcat", "Hashcat", "GPU cracker", "كسر بالـ GPU", "15 MB", "pentest", false));
        pkgs.add(new Package("reaver", "Reaver", "WPS attacker", "هجوم WPS", "2 MB", "pentest", false));
        pkgs.add(new Package("radare2", "Radare2", "Reverse engineering", "هندسة عكسية", "20 MB", "pentest", false));
        pkgs.add(new Package("binwalk", "Binwalk", "Firmware analysis", "تحليل الفيرموير", "5 MB", "pentest", false));

        // استطلاع
        pkgs.add(new Package("theharvester", "TheHarvester", "OSINT tool", "أداة استخبارات", "5 MB", "recon", false));
        pkgs.add(new Package("sublist3r", "Sublist3r", "Subdomain enum", "تعداد النطاقات الفرعية", "3 MB", "recon", false));
        pkgs.add(new Package("dnsenum", "dnsenum", "DNS enumeration", "تعداد DNS", "2 MB", "recon", false));
        pkgs.add(new Package("dnsrecon", "DNSRecon", "DNS recon", "استطلاع DNS", "3 MB", "recon", false));
        pkgs.add(new Package("shodan", "Shodan", "IoT search", "بحث IoT", "2 MB", "recon", false));

        // أدوات
        pkgs.add(new Package("htop", "htop", "Process viewer", "عارض العمليات", "1 MB", "util", true));
        pkgs.add(new Package("tree", "tree", "Directory tree", "شجرة المجلدات", "1 MB", "util", false));
        pkgs.add(new Package("ffmpeg", "FFmpeg", "Media converter", "محوّل الوسائط", "30 MB", "util", true));
        pkgs.add(new Package("imagemagick", "ImageMagick", "Image editor", "محرر صور", "20 MB", "util", false));
        pkgs.add(new Package("sqlite", "SQLite", "Database", "قاعدة بيانات", "2 MB", "util", true));
        pkgs.add(new Package("redis", "Redis", "Key-value store", "قاعدة بيانات", "5 MB", "util", false));
        pkgs.add(new Package("nginx", "Nginx", "Web server", "خادم ويب", "8 MB", "util", false));
        pkgs.add(new Package("mariadb", "MariaDB", "MySQL database", "قاعدة بيانات MySQL", "40 MB", "util", false));
        pkgs.add(new Package("postgresql", "PostgreSQL", "Database", "قاعدة بيانات", "35 MB", "util", false));
        pkgs.add(new Package("opencv", "OpenCV", "Computer vision", "رؤية حاسوبية", "50 MB", "util", false));

        // تحديث حالة التثبيت
        for (Package p : pkgs) {
            p.installed = isInstalled(p.name);
        }

        return pkgs;
    }

    public boolean isInstalled(String pkgName) {
        return prefs.getBoolean("pkg_" + pkgName, false);
    }

    public void markInstalled(String pkgName, boolean installed) {
        prefs.edit().putBoolean("pkg_" + pkgName, installed).apply();
    }

    public int getInstalledCount() {
        int count = 0;
        for (Package p : getAvailablePackages()) {
            if (p.installed) count++;
        }
        return count;
    }

    public String[] getCategories() {
        return new String[]{"all", "core", "shell", "editor", "terminal", "lang", "dev", "net", "pentest", "recon", "util"};
    }

    public String getCategoryName(String cat) {
        switch (cat) {
            case "all": return "📦 الكل";
            case "core": return "🔧 أساسية";
            case "shell": return "💻 صدفة";
            case "editor": return "📝 محررات";
            case "terminal": return "🖥️ طرفية";
            case "lang": return "🐍 لغات";
            case "dev": return "⚙️ تطوير";
            case "net": return "🌐 شبكة";
            case "pentest": return "💀 اختراق";
            case "recon": return "🕵️ استطلاع";
            case "util": return "🛠️ أدوات";
            default: return cat;
        }
    }
}
