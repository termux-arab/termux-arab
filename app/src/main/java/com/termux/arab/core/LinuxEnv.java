package com.termux.arab.core;

import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * تهيئة بيئة Linux محاكاة داخل التطبيق
 * - ينشئ مجلدات home/usr/bin
 * - يثبّت أوامر مدمجة (بديل BusyBox)
 * - يهيئ متغيرات البيئة
 */
public class LinuxEnv {

    private final Context ctx;
    private File rootDir, homeDir, binDir, tmpDir, etcDir;

    public LinuxEnv(Context ctx) {
        this.ctx = ctx;
    }

    public void init() {
        rootDir = new File(ctx.getFilesDir(), "linux");
        homeDir = new File(rootDir, "home");
        binDir = new File(rootDir, "usr/bin");
        tmpDir = new File(rootDir, "tmp");
        etcDir = new File(rootDir, "etc");

        rootDir.mkdirs();
        homeDir.mkdirs();
        binDir.mkdirs();
        tmpDir.mkdirs();
        etcDir.mkdirs();

        // إنشاء ملفات الإعداد
        createPasswd();
        createGroup();
        createProfile();

        // تثبيت الأوامر المدمجة (Java implementations)
        installBuiltins();
    }

    private void createPasswd() {
        File f = new File(etcDir, "passwd");
        if (!f.exists()) {
            writeFile(f, "root:x:0:0:root:/home:/system/bin/sh\n");
        }
    }

    private void createGroup() {
        File f = new File(etcDir, "group");
        if (!f.exists()) {
            writeFile(f, "root:x:0:\n");
        }
    }

    private void createProfile() {
        File f = new File(etcDir, "profile");
        if (!f.exists()) {
            writeFile(f,
                "# Profile - تيرمكس العرب\n" +
                "export PATH=" + binDir.getAbsolutePath() + ":/system/bin:/system/xbin\n" +
                "export HOME=" + homeDir.getAbsolutePath() + "\n" +
                "export TMPDIR=" + tmpDir.getAbsolutePath() + "\n" +
                "export TERM=xterm-256color\n" +
                "export LANG=en_US.UTF-8\n" +
                "export SHELL=/system/bin/sh\n" +
                "export USER=root\n" +
                "export LOGNAME=root\n" +
                "# Alias\n" +
                "alias ll='ls -la'\n" +
                "alias la='ls -a'\n" +
                "alias ..='cd ..'\n" +
                "alias grep='grep --color=auto'\n"
            );
        }
    }

    /**
     * تثبيت أوامر Linux المدمجة
     * كل أمر هو سكربت shell يستخدم /system/bin
     */
    private void installBuiltins() {
        // كل أمر = سكربت في binDir
        Map<String, String> commands = new HashMap<>();

        // === أوامر أساسية تعمل عبر /system/bin ===
        commands.put("ls", "#!/system/bin/sh\n/system/bin/ls $@");
        commands.put("cat", "#!/system/bin/sh\n/system/bin/cat $@");
        commands.put("cp", "#!/system/bin/sh\n/system/bin/cp $@");
        commands.put("mv", "#!/system/bin/sh\n/system/bin/mv $@");
        commands.put("rm", "#!/system/bin/sh\n/system/bin/rm $@");
        commands.put("mkdir", "#!/system/bin/sh\n/system/bin/mkdir $@");
        commands.put("rmdir", "#!/system/bin/sh\n/system/bin/rmdir $@");
        commands.put("touch", "#!/system/bin/sh\n/system/bin/touch $@");
        commands.put("echo", "#!/system/bin/sh\n/system/bin/echo $@");
        commands.put("printf", "#!/system/bin/sh\n/system/bin/printf $@");
        commands.put("pwd", "#!/system/bin/sh\n/system/bin/pwd $@");
        commands.put("cd", "#!/system/bin/sh\ncd $@ 2>/dev/null; /system/bin/pwd");
        commands.put("date", "#!/system/bin/sh\n/system/bin/date $@");
        commands.put("whoami", "#!/system/bin/sh\necho root");
        commands.put("id", "#!/system/bin/sh\necho 'uid=0(root) gid=0(root)'");
        commands.put("uname", "#!/system/bin/sh\necho 'Linux android aarch64'");
        commands.put("hostname", "#!/system/bin/sh\n/system/bin/getprop ro.product.model");
        commands.put("uptime", "#!/system/bin/sh\n/system/bin/uptime 2>/dev/null || cat /proc/uptime");
        commands.put("env", "#!/system/bin/sh\n/system/bin/env $@");
        commands.put("printenv", "#!/system/bin/sh\n/system/bin/printenv $@");
        commands.put("which", "#!/system/bin/sh\nfor cmd in $@; do\n  for p in " + binDir.getAbsolutePath() + " /system/bin /system/xbin; do\n    if [ -f \"$p/$cmd\" ]; then echo \"$p/$cmd\"; break; fi\n  done\ndone");
        commands.put("head", "#!/system/bin/sh\n/system/bin/head $@");
        commands.put("tail", "#!/system/bin/sh\n/system/bin/tail $@");
        commands.put("wc", "#!/system/bin/sh\n/system/bin/wc $@");
        commands.put("sort", "#!/system/bin/sh\n/system/bin/sort $@");
        commands.put("uniq", "#!/system/bin/sh\n/system/bin/uniq $@");
        commands.put("cut", "#!/system/bin/sh\n/system/bin/cut $@");
        commands.put("tr", "#!/system/bin/sh\n/system/bin/tr $@");
        commands.put("sed", "#!/system/bin/sh\n/system/bin/sed $@ 2>/dev/null || echo 'sed: not available'");
        commands.put("grep", "#!/system/bin/sh\n/system/bin/grep $@ 2>/dev/null || /system/bin/toybox grep $@ 2>/dev/null || echo 'grep: not available'");
        commands.put("find", "#!/system/bin/sh\n/system/bin/find $@ 2>/dev/null || /system/bin/toybox find $@ 2>/dev/null || echo 'find: not available'");
        commands.put("df", "#!/system/bin/sh\n/system/bin/df $@");
        commands.put("du", "#!/system/bin/sh\n/system/bin/du $@ 2>/dev/null || echo 'du: not available'");
        commands.put("free", "#!/system/bin/sh\ncat /proc/meminfo | head -5");
        commands.put("top", "#!/system/bin/sh\n/system/bin/top -n 1 $@ 2>/dev/null || /system/bin/toybox top $@ 2>/dev/null || ps");
        commands.put("ps", "#!/system/bin/sh\n/system/bin/ps $@");
        commands.put("kill", "#!/system/bin/sh\n/system/bin/kill $@");
        commands.put("killall", "#!/system/bin/sh\n/system/bin/killall $@ 2>/dev/null || /system/bin/toybox killall $@");
        commands.put("sleep", "#!/system/bin/sh\n/system/bin/sleep $@");
        commands.put("true", "#!/system/bin/sh\nexit 0");
        commands.put("false", "#!/system/bin/sh\nexit 1");
        commands.put("test", "#!/system/bin/sh\n/system/bin/test $@");
        commands.put("expr", "#!/system/bin/sh\n/system/bin/expr $@");
        commands.put("basename", "#!/system/bin/sh\n/system/bin/basename $@");
        commands.put("dirname", "#!/system/bin/sh\n/system/bin/dirname $@");
        commands.put("realpath", "#!/system/bin/sh\n/system/bin/realpath $@ 2>/dev/null || readlink -f $@");
        commands.put("readlink", "#!/system/bin/sh\n/system/bin/readlink $@");
        commands.put("ln", "#!/system/bin/sh\n/system/bin/ln $@");
        commands.put("chmod", "#!/system/bin/sh\n/system/bin/chmod $@");
        commands.put("chown", "#!/system/bin/sh\n/system/bin/chown $@");
        commands.put("stat", "#!/system/bin/sh\n/system/bin/stat $@ 2>/dev/null || /system/bin/toybox stat $@");
        commands.put("file", "#!/system/bin/sh\n/system/bin/file $@ 2>/dev/null || echo '$1: data'");
        commands.put("tar", "#!/system/bin/sh\n/system/bin/tar $@");
        commands.put("gzip", "#!/system/bin/sh\n/system/bin/gzip $@");
        commands.put("gunzip", "#!/system/bin/sh\n/system/bin/gunzip $@");
        commands.put("zcat", "#!/system/bin/sh\n/system/bin/zcat $@");
        commands.put("md5sum", "#!/system/bin/sh\n/system/bin/md5sum $@ 2>/dev/null || /system/bin/toybox md5sum $@");
        commands.put("sha1sum", "#!/system/bin/sh\n/system/bin/sha1sum $@ 2>/dev/null || /system/bin/toybox sha1sum $@");
        commands.put("sha256sum", "#!/system/bin/sh\n/system/bin/sha256sum $@ 2>/dev/null || /system/bin/toybox sha256sum $@");
        commands.put("base64", "#!/system/bin/sh\n/system/bin/base64 $@");
        commands.put("xxd", "#!/system/bin/sh\n/system/bin/xxd $@ 2>/dev/null || /system/bin/toybox xxd $@ 2>/dev/null || /system/bin/od -A x -t x1z $@");
        commands.put("diff", "#!/system/bin/sh\n/system/bin/diff $@ 2>/dev/null || echo 'diff: not available'");
        commands.put("patch", "#!/system/bin/sh\n/system/bin/patch $@ 2>/dev/null || echo 'patch: not available'");
        commands.put("tee", "#!/system/bin/sh\n/system/bin/tee $@");
        commands.put("xargs", "#!/system/bin/sh\n/system/bin/xargs $@");
        commands.put("yes", "#!/system/bin/sh\n/system/bin/yes $@");
        commands.put("seq", "#!/system/bin/sh\n/system/bin/seq $@");
        commands.put("nl", "#!/system/bin/sh\n/system/bin/nl $@ 2>/dev/null || /system/bin/toybox nl $@");
        commands.put("rev", "#!/system/bin/sh\n/system/bin/rev $@ 2>/dev/null || /system/bin/toybox rev $@");
        commands.put("fold", "#!/system/bin/sh\n/system/bin/fold $@");
        commands.put("expand", "#!/system/bin/sh\n/system/bin/expand $@");
        commands.put("unexpand", "#!/system/bin/sh\n/system/bin/unexpand $@");
        commands.put("tac", "#!/system/bin/sh\n/system/bin/tac $@ 2>/dev/null || /system/bin/toybox tac $@");
        commands.put("strings", "#!/system/bin/sh\n/system/bin/strings $@ 2>/dev/null || echo 'strings: not available'");
        commands.put("od", "#!/system/bin/sh\n/system/bin/od $@");
        commands.put("hexdump", "#!/system/bin/sh\n/system/bin/hexdump $@ 2>/dev/null || od -A x -t x1z $@");
        commands.put("clear", "#!/system/bin/sh\n/system/bin/clear");
        commands.put("reset", "#!/system/bin/sh\n/system/bin/reset 2>/dev/null || printf '\\033c'");
        commands.put("history", "#!/system/bin/sh\ncat " + homeDir.getAbsolutePath() + "/.bash_history 2>/dev/null | tail -50 | nl");
        commands.put("exit", "#!/system/bin/sh\nexit 0");
        commands.put("logout", "#!/system/bin/sh\nexit 0");
        commands.put("help", "#!/system/bin/sh\necho '═══ تيرمكس العرب - الأوامر المتاحة ═══'\necho ''\necho 'أوامر الملفات: ls cd cp mv rm mkdir touch pwd find'\necho 'عرض المحتوى: cat head tail less more wc nl tac rev'\necho 'البحث: grep find which locate'\necho 'النص: sed awk tr cut sort uniq paste fold expand'\necho 'الضغط: tar gzip gunzip zcat zip unzip'\necho 'التجزئة: md5sum sha1sum sha256sum base64 xxd'\necho 'النظام: ps top free df du kill killall uptime uname hostname whoami id env'\necho 'الوقت: date cal uptime sleep'\necho 'الشبكة: ping curl wget nc nslookup netstat ss'\necho 'المستخدم: whoami id groups passwd su sudo'\necho 'متنوع: echo printf clear reset history exit help man'\necho ''\necho 'استخدم: help <command> للمزيد'");
        commands.put("man", "#!/system/bin/sh\nif [ -z \"$1\" ]; then echo 'Usage: man <command>'; else echo 'MAN PAGE: $1'; echo '================'; $1 --help 2>/dev/null || $1 -h 2>/dev/null || echo 'No help available for $1'; fi");
        commands.put("cal", "#!/system/bin/sh\n/system/bin/cal $@ 2>/dev/null || /system/bin/toybox cal $@ 2>/dev/null || echo 'cal: not available'");
        commands.put("groups", "#!/system/bin/sh\necho 'root'");
        commands.put("passwd", "#!/system/bin/sh\necho 'passwd: password cannot be changed on this system'");
        commands.put("su", "#!/system/bin/sh\necho 'su: already running as root'");
        commands.put("sudo", "#!/system/bin/sh\n$@");
        commands.put("less", "#!/system/bin/sh\n/system/bin/cat $@");
        commands.put("more", "#!/system/bin/sh\n/system/bin/cat $@");
        commands.put("vi", "#!/system/bin/sh\necho 'vi: not available. Use: cat > file'");
        commands.put("vim", "#!/system/bin/sh\necho 'vim: not available. Use: cat > file'");
        commands.put("nano", "#!/system/bin/sh\necho 'nano: not available. Use: cat > file'");
        commands.put("ping", "#!/system/bin/sh\n/system/bin/ping $@");
        commands.put("ping6", "#!/system/bin/sh\n/system/bin/ping6 $@ 2>/dev/null || /system/bin/toybox ping6 $@");
        commands.put("nslookup", "#!/system/bin/sh\n/system/bin/nslookup $@ 2>/dev/null || /system/bin/toybox nslookup $@ 2>/dev/null || echo 'nslookup: not available'");
        commands.put("netstat", "#!/system/bin/sh\n/system/bin/netstat $@ 2>/dev/null || /system/bin/toybox netstat $@ 2>/dev/null || cat /proc/net/tcp");
        commands.put("ss", "#!/system/bin/sh\n/system/bin/ss $@ 2>/dev/null || /system/bin/toybox ss $@ 2>/dev/null || cat /proc/net/tcp");
        commands.put("ifconfig", "#!/system/bin/sh\n/system/bin/ifconfig $@ 2>/dev/null || /system/bin/toybox ifconfig $@ 2>/dev/null || ip addr");
        commands.put("ip", "#!/system/bin/sh\n/system/bin/ip $@ 2>/dev/null || /system/bin/toybox ip $@");
        commands.put("route", "#!/system/bin/sh\n/system/bin/route $@ 2>/dev/null || /system/bin/toybox route $@ 2>/dev/null || cat /proc/net/route");
        commands.put("arp", "#!/system/bin/sh\n/system/bin/arp $@ 2>/dev/null || /system/bin/toybox arp $@ 2>/dev/null || cat /proc/net/arp");
        commands.put("curl", "#!/system/bin/sh\n/system/bin/curl $@ 2>/dev/null || /system/bin/toybox curl $@ 2>/dev/null || echo 'curl: not available'");
        commands.put("wget", "#!/system/bin/sh\n/system/bin/wget $@ 2>/dev/null || /system/bin/toybox wget $@ 2>/dev/null || echo 'wget: not available'");
        commands.put("nc", "#!/system/bin/sh\n/system/bin/nc $@ 2>/dev/null || /system/bin/toybox nc $@ 2>/dev/null || echo 'nc: not available'");
        commands.put("telnet", "#!/system/bin/sh\n/system/bin/telnet $@ 2>/dev/null || echo 'telnet: not available'");
        commands.put("ssh", "#!/system/bin/sh\necho 'ssh: not available. Install openssh via Package Manager'");
        commands.put("scp", "#!/system/bin/sh\necho 'scp: not available'");
        commands.put("ftp", "#!/system/bin/sh\necho 'ftp: not available'");
        commands.put("python", "#!/system/bin/sh\necho 'python: install via Package Manager'");
        commands.put("python3", "#!/system/bin/sh\necho 'python3: install via Package Manager'");
        commands.put("node", "#!/system/bin/sh\necho 'node: install via Package Manager'");
        commands.put("git", "#!/system/bin/sh\necho 'git: install via Package Manager'");
        commands.put("make", "#!/system/bin/sh\necho 'make: not available'");
        commands.put("gcc", "#!/system/bin/sh\necho 'gcc: not available'");
        commands.put("cc", "#!/system/bin/sh\necho 'cc: not available'");
        commands.put("perl", "#!/system/bin/sh\necho 'perl: not available'");
        commands.put("ruby", "#!/system/bin/sh\necho 'ruby: not available'");
        commands.put("java", "#!/system/bin/sh\necho 'java: not available'");
        commands.put("awk", "#!/system/bin/sh\n/system/bin/awk $@ 2>/dev/null || /system/bin/toybox awk $@ 2>/dev/null || echo 'awk: not available'");
        commands.put("paste", "#!/system/bin/sh\n/system/bin/paste $@ 2>/dev/null || /system/bin/toybox paste $@");
        commands.put("comm", "#!/system/bin/sh\n/system/bin/comm $@ 2>/dev/null || /system/bin/toybox comm $@");
        commands.put("column", "#!/system/bin/sh\n/system/bin/column $@ 2>/dev/null || echo 'column: not available'");
        commands.put("fmt", "#!/system/bin/sh\n/system/bin/fmt $@ 2>/dev/null || echo 'fmt: not available'");
        commands.put("shuf", "#!/system/bin/sh\n/system/bin/shuf $@ 2>/dev/null || /system/bin/toybox shuf $@ 2>/dev/null || echo 'shuf: not available'");
        commands.put("split", "#!/system/bin/sh\n/system/bin/split $@ 2>/dev/null || /system/bin/toybox split $@");
        commands.put("csplit", "#!/system/bin/sh\necho 'csplit: not available'");
        commands.put("dd", "#!/system/bin/sh\n/system/bin/dd $@");
        commands.put("sync", "#!/system/bin/sh\n/system/bin/sync");
        commands.put("mount", "#!/system/bin/sh\n/system/bin/mount $@ 2>/dev/null || /system/bin/toybox mount $@ 2>/dev/null || echo 'mount: requires root'");
        commands.put("umount", "#!/system/bin/sh\n/system/bin/umount $@ 2>/dev/null || echo 'umount: requires root'");
        commands.put("dmesg", "#!/system/bin/sh\n/system/bin/dmesg $@ 2>/dev/null || /system/bin/toybox dmesg $@");
        commands.put("logcat", "#!/system/bin/sh\n/system/bin/logcat $@");
        commands.put("getprop", "#!/system/bin/sh\n/system/bin/getprop $@");
        commands.put("setprop", "#!/system/bin/sh\necho 'setprop: requires root'");
        commands.put("am", "#!/system/bin/sh\n/system/bin/am $@");
        commands.put("pm", "#!/system/bin/sh\n/system/bin/pm $@");
        commands.put("settings", "#!/system/bin/sh\n/system/bin/settings $@");
        commands.put("cmd", "#!/system/bin/sh\n/system/bin/cmd $@");
        commands.put("input", "#!/system/bin/sh\n/system/bin/input $@");
        commands.put("wm", "#!/system/bin/sh\n/system/bin/wm $@");
        commands.put("screencap", "#!/system/bin/sh\n/system/bin/screencap $@");
        commands.put("screenrecord", "#!/system/bin/sh\n/system/bin/screenrecord $@ 2>/dev/null || echo 'screenrecord: not available'");
        commands.put("monkey", "#!/system/bin/sh\n/system/bin/monkey $@ 2>/dev/null || echo 'monkey: not available'");
        commands.put("dumpsys", "#!/system/bin/sh\n/system/bin/dumpsys $@");
        commands.put("bugreport", "#!/system/bin/sh\n/system/bin/dumpsys > " + tmpDir.getAbsolutePath() + "/bugreport.txt\necho 'bugreport saved to /tmp/bugreport.txt'");
        commands.put("zip", "#!/system/bin/sh\n/system/bin/zip $@ 2>/dev/null || /system/bin/toybox zip $@ 2>/dev/null || echo 'zip: not available'");
        commands.put("unzip", "#!/system/bin/sh\n/system/bin/unzip $@ 2>/dev/null || /system/bin/toybox unzip $@ 2>/dev/null || echo 'unzip: not available'");
        commands.put("bzip2", "#!/system/bin/sh\necho 'bzip2: not available'");
        commands.put("xz", "#!/system/bin/sh\necho 'xz: not available'");
        commands.put("7z", "#!/system/bin/sh\necho '7z: not available'");
        commands.put("dmseg", "#!/system/bin/sh\n/system/bin/dmesg $@");
        commands.put("vmstat", "#!/system/bin/sh\ncat /proc/vmstat | head -20");
        commands.put("iostat", "#!/system/bin/sh\necho 'iostat: not available'");
        commands.put("mpstat", "#!/system/bin/sh\necho 'mpstat: not available'");
        commands.put("lsof", "#!/system/bin/sh\necho 'lsof: not available'");
        commands.put("strace", "#!/system/bin/sh\necho 'strace: requires root'");
        commands.put("ltrace", "#!/system/bin/sh\necho 'ltrace: not available'");
        commands.put("gdb", "#!/system/bin/sh\necho 'gdb: not available'");

        for (Map.Entry<String, String> entry : commands.entrySet()) {
            File cmdFile = new File(binDir, entry.getKey());
            if (!cmdFile.exists()) {
                writeFile(cmdFile, entry.getValue());
                cmdFile.setExecutable(true);
            }
        }
    }

    private void writeFile(File f, String content) {
        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(content.getBytes("UTF-8"));
            fos.close();
        } catch (Exception e) {
            // تجاهل
        }
    }

    public String[] getEnvArray() {
        return new String[] {
            "HOME=" + homeDir.getAbsolutePath(),
            "PATH=" + binDir.getAbsolutePath() + ":/system/bin:/system/xbin",
            "TMPDIR=" + tmpDir.getAbsolutePath(),
            "TERM=xterm-256color",
            "LANG=en_US.UTF-8",
            "SHELL=/system/bin/sh",
            "USER=root",
            "LOGNAME=root",
            "PREFIX=" + rootDir.getAbsolutePath() + "/usr"
        };
    }

    public File getHomeDir() { return homeDir; }
    public File getBinDir() { return binDir; }
    public File getRootDir() { return rootDir; }
    public File getTmpDir() { return tmpDir; }

    public int getInstalledCommandCount() {
        if (binDir == null || !binDir.exists()) return 0;
        return binDir.listFiles() != null ? binDir.listFiles().length : 0;
    }

    public boolean isCommandAvailable(String cmd) {
        return new File(binDir, cmd).exists() ||
               new File("/system/bin", cmd).exists() ||
               new File("/system/xbin", cmd).exists();
    }

    public String[] getAvailableCommands() {
        if (binDir == null || !binDir.exists()) return new String[0];
        File[] files = binDir.listFiles();
        String[] names = new String[files.length];
        for (int i = 0; i < files.length; i++) names[i] = files[i].getName();
        java.util.Arrays.sort(names);
        return names;
    }
}
