package com.termux.arab.core;

import com.termux.arab.model.Tool;
import java.util.ArrayList;
import java.util.List;

public class ToolRegistry {

    public static List<Tool> getAllTools() {
        List<Tool> tools = new ArrayList<>();
        // اختبار الاختراق
        addTool(tools, "nmap", "ماسح الشبكات", "Nmap", "مسح الشبكات والمنافذ", "nmap", "nmap", "📡", "pentest", 0, false, true, "nmap -sV 192.168.1.1");
        addTool(tools, "metasploit", "ميتاسبلويت", "Metasploit", "إطار اختبار اختراق شامل", "metasploit-framework", "msfconsole", "⚡", "pentest", 2, false, false, "msfconsole");
        addTool(tools, "sqlmap", "اختبار حقن SQL", "SQLMap", "أداة حقن SQL التلقائية", "sqlmap", "sqlmap", "💉", "pentest", 2, false, true, "sqlmap -u http://target.com?id=1");
        addTool(tools, "hydra", "هيدرا", "Hydra", "تخمين كلمات المرور", "hydra", "hydra", "🔑", "pentest", 2, false, true, "hydra -l admin -P pass.txt ssh://target");
        addTool(tools, "john", "جون ذا ريبر", "John", "كسر كلمات المرور", "john", "john", "🔓", "pentest", 2, false, false, "john hashes.txt");
        addTool(tools, "nikto", "ماسح الويب", "Nikto", "ماسح ثغرات الويب", "nikto", "nikto", "🌐", "pentest", 1, false, true, "nikto -h http://target.com");
        addTool(tools, "netcat", "نت كات", "Netcat", "أداة الشبكات الشاملة", "netcat-openbsd", "nc", "🔗", "pentest", 0, false, false, "nc -lvnp 4444");
        addTool(tools, "searchsploit", "باحث الثغرات", "SearchSploit", "البحث في Exploit-DB", "exploitdb", "searchsploit", "🔍", "pentest", 0, false, true, "searchsploit apache 2.4");
        addTool(tools, "radare2", "محلل الباينري", "Radare2", "الهندسة العكسية", "radare2", "r2", "🔬", "pentest", 1, false, false, "r2 binary");
        addTool(tools, "binwalk", "محلل البرامج الثابتة", "Binwalk", "تحليل الفيرموير", "binwalk", "binwalk", "🔧", "pentest", 0, false, false, "binwalk firmware.bin");

        // الشبكات
        addTool(tools, "ping", "اختبار الاتصال", "Ping", "اختبار الاتصال بمضيف", "inetutils", "ping", "📡", "network", 0, false, true, "ping google.com");
        addTool(tools, "traceroute", "تتبع المسار", "Traceroute", "تتبع مسار الحزم", "traceroute", "traceroute", "🗺️", "network", 0, false, true, "traceroute google.com");
        addTool(tools, "dig", "استعلام DNS", "Dig", "استعلام DNS متقدم", "bind", "dig", "🗂️", "network", 0, false, true, "dig example.com");
        addTool(tools, "curl", "عميل HTTP", "Curl", "أداة HTTP متعددة", "curl", "curl", "🔄", "network", 0, false, true, "curl https://example.com");
        addTool(tools, "wget", "منزّل الملفات", "Wget", "تنزيل ملفات", "wget", "wget", "⬇️", "network", 0, false, true, "wget https://example.com/file.zip");
        addTool(tools, "nmap-net", "Nmap شبكات", "Nmap", "ماسح الشبكات الكامل", "nmap", "nmap", "📡", "network", 0, false, true, "nmap -sn 192.168.1.0/24");
        addTool(tools, "masscan", "ماسح سريع", "Masscan", "ماسح منافذ سريع", "masscan", "masscan", "⚡", "network", 1, true, true, "masscan 10.0.0.0/8 -p80");
        addTool(tools, "tcpdump", "التقاط الحزم", "Tcpdump", "التقاط حزم الشبكة", "tcpdump", "tcpdump", "📦", "network", 1, true, false, "tcpdump -i wlan0");
        addTool(tools, "netstat", "حالة الشبكة", "Netstat", "عرض الاتصالات", "net-tools", "netstat", "📊", "network", 0, false, false, "netstat -tulpn");

        // الويب
        addTool(tools, "nikto-web", "نيكتو ويب", "Nikto", "ماسح ثغرات الويب", "nikto", "nikto", "🌐", "web", 1, false, true, "nikto -h http://target.com");
        addTool(tools, "dirb", "ديرب", "Dirb", "تخمين المجلدات", "dirb", "dirb", "📁", "web", 0, false, true, "dirb http://target.com");
        addTool(tools, "gobuster", "جو باستر", "Gobuster", "تخمين سريع للمسارات", "gobuster", "gobuster", "🚀", "web", 0, false, true, "gobuster dir -u http://target.com -w wordlist.txt");
        addTool(tools, "wpscan", "ماسح ووردبريس", "WPScan", "ماسح WordPress", "wpscan", "wpscan", "📝", "web", 1, false, true, "wpscan --url http://target.com");
        addTool(tools, "whatweb", "وات ويب", "WhatWeb", "تحديد تقنيات المواقع", "whatweb", "whatweb", "💡", "web", 0, false, true, "whatweb http://target.com");
        addTool(tools, "commix", "كوميكس", "Commix", "اختبار حقن الأوامر", "commix", "commix", "💻", "web", 2, false, true, "commix --url='http://target.com'");

        // كلمات المرور
        addTool(tools, "john-pw", "جون", "John", "كسر كلمات المرور", "john", "john", "🔓", "password", 2, false, true, "john --wordlist=rockyou.txt hash.txt");
        addTool(tools, "hashcat", "هاش كات", "Hashcat", "كسر بالـ GPU", "hashcat", "hashcat", "🔥", "password", 2, false, true, "hashcat -m 0 hash.txt word.txt");
        addTool(tools, "hydra-pw", "هيدرا كلمات", "Hydra", "تخمين عبر الشبكة", "hydra", "hydra", "🔑", "password", 2, false, true, "hydra -l admin -P pass.txt ssh://target");
        addTool(tools, "crunch", "مولّد كلمات", "Crunch", "توليد قوائم كلمات", "crunch", "crunch", "🎲", "password", 0, false, false, "crunch 8 8 -o wordlist.txt");

        // الاستطلاع
        addTool(tools, "theharvester", "هارفيستر", "TheHarvester", "جمع الإيميلات والنطاقات", "theharvester", "theHarvester", "📧", "recon", 0, false, true, "theHarvester -d target.com -b all");
        addTool(tools, "dnsenum", "دي أن إس إينوم", "Dnsenum", "تعداد DNS", "dnsenum", "dnsenum", "🗂️", "recon", 0, false, true, "dnsenum target.com");
        addTool(tools, "whois", "معلومات النطاق", "Whois", "معلومات تسجيل النطاق", "whois", "whois", "ℹ️", "recon", 0, false, true, "whois example.com");
        addTool(tools, "sublist3r", "ساب ليستر", "Sublist3r", "تعداد النطاقات الفرعية", "sublist3r", "sublist3r", "🌐", "recon", 0, false, true, "sublist3r -d target.com");
        addTool(tools, "shodan", "شودان", "Shodan", "بحث في Shodan", "shodan", "shodan", "🔍", "recon", 0, false, true, "shodan search apache");

        // الواي فاي
        addTool(tools, "aircrack", "إيركراك", "Aircrack-ng", "اختراق الواي فاي", "aircrack-ng", "aircrack-ng", "📶", "wifi", 2, true, false, "aircrack-ng -w wordlist.cap");
        addTool(tools, "reaver", "ريفر", "Reaver", "اختبار WPS", "reaver", "reaver", "🔑", "wifi", 2, true, true, "reaver -i wlan0mon -b MAC");
        addTool(tools, "wifite", "واي فايت", "Wifite", "أتمتة اختبار WiFi", "wifite", "wifite", "⚡", "wifi", 2, true, false, "wifite");

        // التطوير
        addTool(tools, "python", "بايثون", "Python", "لغة Python", "python", "python", "🐍", "dev", 0, false, false, "python script.py");
        addTool(tools, "nodejs", "نود جي إس", "Node.js", "بيئة JavaScript", "nodejs", "node", "⚡", "dev", 0, false, false, "node app.js");
        addTool(tools, "git", "جيت", "Git", "التحكم بالإصدارات", "git", "git", "📂", "dev", 0, false, false, "git status");
        addTool(tools, "vim", "فيم", "Vim", "محرر متقدم", "vim", "vim", "📝", "dev", 0, false, false, "vim file.txt");
        addTool(tools, "nano", "نانو", "Nano", "محرر سهل", "nano", "nano", "✏️", "dev", 0, false, false, "nano file.txt");
        addTool(tools, "tmux", "تي موكس", "Tmux", "مدير نوافذ طرفية", "tmux", "tmux", "🖥️", "dev", 0, false, false, "tmux");
        addTool(tools, "clang", "مترجم C", "Clang", "مترجم C/C++", "clang", "clang", "⚙️", "dev", 0, false, false, "clang -o prog prog.c");

        // التحليل الجنائي
        addTool(tools, "binwalk-for", "بين ووك", "Binwalk", "تحليل الفيرموير", "binwalk", "binwalk", "🔧", "forensics", 0, false, false, "binwalk firmware.bin");
        addTool(tools, "foremost", "فورموست", "Foremost", "استرجاع ملفات محذوفة", "foremost", "foremost", "♻️", "forensics", 0, false, false, "foremost -i image.dd");
        addTool(tools, "testdisk", "تست ديسك", "TestDisk", "استرجاع أقسام", "testdisk", "testdisk", "💾", "forensics", 1, false, false, "testdisk");
        addTool(tools, "volatility", "فولاتيليتي", "Volatility", "تحليل ذاكرة RAM", "volatility", "volatility", "🧠", "forensics", 0, false, false, "volatility -f mem.dump");

        return tools;
    }

    private static void addTool(List<Tool> list, String id, String name, String nameEn,
            String desc, String pkg, String cmd, String icon, String cat,
            int risk, boolean root, boolean args, String example) {
        Tool t = new Tool();
        t.id = id; t.name = name; t.nameEn = nameEn; t.description = desc;
        t.packageName = pkg; t.command = cmd; t.icon = icon; t.category = cat;
        t.riskLevel = risk; t.requiresRoot = root; t.needsArgs = args; t.example = example;
        list.add(t);
    }

    public static List<Tool> getByCategory(String category) {
        List<Tool> all = getAllTools();
        List<Tool> filtered = new ArrayList<>();
        for (Tool t : all) {
            if (t.category.equals(category)) filtered.add(t);
        }
        return filtered;
    }

    public static String[] getCategories() {
        return new String[]{"pentest", "network", "web", "password", "recon", "wifi", "dev", "forensics"};
    }

    public static String getCategoryName(String cat) {
        switch (cat) {
            case "pentest": return "💀 اختبار الاختراق";
            case "network": return "🌐 الشبكات";
            case "web": return "🌍 الويب";
            case "password": return "🔐 كلمات المرور";
            case "recon": return "🕵️ الاستطلاع";
            case "wifi": return "📶 الواي فاي";
            case "dev": return "💻 التطوير";
            case "forensics": return "🔍 التحليل الجنائي";
            default: return cat;
        }
    }

    public static String getCategoryIcon(String cat) {
        switch (cat) {
            case "pentest": return "💀";
            case "network": return "🌐";
            case "web": return "🌍";
            case "password": return "🔐";
            case "recon": return "🕵️";
            case "wifi": return "📶";
            case "dev": return "💻";
            case "forensics": return "🔍";
            default: return "📱";
        }
    }

    public static int getCategoryColor(String cat) {
        switch (cat) {
            case "pentest": return 0xFFD32F2F;
            case "network": return 0xFF1976D2;
            case "web": return 0xFF7B1FA2;
            case "password": return 0xFFFF5722;
            case "recon": return 0xFF388E3C;
            case "wifi": return 0xFF795548;
            case "dev": return 0xFF3F51B5;
            case "forensics": return 0xFF5C6BC0;
            default: return 0xFF0F4C2A;
        }
    }
}
