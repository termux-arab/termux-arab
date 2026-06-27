"use client";

import { useEffect, useState } from "react";
import { Eye, Download, Users, TrendingUp } from "lucide-react";

interface Stats {
  visitors: number;
  downloads: number;
  stars: number;
  forks: number;
}

// Simulated stats - in production replace with real API
// Uses localStorage for visitor count persistence
export function LiveStats() {
  const [stats, setStats] = useState<Stats>({
    visitors: 0,
    downloads: 0,
    stars: 0,
    forks: 0,
  });

  useEffect(() => {
    async function fetchStats() {
      try {
        // Fetch GitHub stars/forks (public API, no auth needed)
        const response = await fetch(
          "https://api.github.com/repos/termux-arab/termux-arab"
        );
        const data = await response.json();

        // Get visitor count from localStorage (simulated)
        // In production, replace with Google Analytics API or counter service
        const visitorKey = "termux_visitor_count";
        const today = new Date().toDateString();
        const lastVisit = localStorage.getItem("termux_last_visit");

        let visitors = parseInt(localStorage.getItem(visitorKey) || "1247", 10);
        if (lastVisit !== today) {
          // Simulate new visitors each day
          visitors += Math.floor(Math.random() * 5) + 1;
          localStorage.setItem(visitorKey, visitors.toString());
          localStorage.setItem("termux_last_visit", today);
        }

        // Get download count from localStorage (accumulated)
        const downloads = parseInt(localStorage.getItem("total_downloads") || "348", 10);

        setStats({
          visitors,
          downloads,
          stars: data.stargazers_count || 0,
          forks: data.forks_count || 0,
        });
      } catch (e) {
        // Fallback to localStorage only
        const visitors = parseInt(localStorage.getItem("termux_visitor_count") || "1247", 10);
        const downloads = parseInt(localStorage.getItem("total_downloads") || "348", 10);
        setStats({
          visitors,
          downloads,
          stars: 0,
          forks: 0,
        });
      }
    }
    fetchStats();
  }, []);

  const formatNumber = (n: number) => {
    if (n >= 1000000) return (n / 1000000).toFixed(1) + "M";
    if (n >= 1000) return (n / 1000).toFixed(1) + "K";
    return n.toString();
  };

  const items = [
    { icon: Eye, label: "زائر", value: formatNumber(stats.visitors), color: "text-blue-500" },
    { icon: Download, label: "تحميل", value: formatNumber(stats.downloads), color: "text-emerald-500" },
    { icon: Users, label: "نجمة", value: formatNumber(stats.stars), color: "text-amber-500" },
    { icon: TrendingUp, label: "fork", value: formatNumber(stats.forks), color: "text-purple-500" },
  ];

  return (
    <div className="grid grid-cols-2 md:grid-cols-4 gap-3 max-w-3xl mx-auto">
      {items.map((item, i) => (
        <div
          key={i}
          className="bg-white/10 backdrop-blur rounded-xl p-4 border border-white/10 text-center"
        >
          <item.icon className={`w-6 h-6 mx-auto mb-2 ${item.color}`} />
          <div className="text-2xl md:text-3xl font-bold text-white">{item.value}</div>
          <div className="text-xs text-emerald-200">{item.label}</div>
        </div>
      ))}
    </div>
  );
}
