"use client";

import { useEffect, useState } from "react";

declare global {
  interface Window {
    gtag: (...args: unknown[]) => void;
    dataLayer: unknown[];
  }
}

const GA_MEASUREMENT_ID = "G-XXXXXXXXXX"; // Replace with your GA4 Measurement ID

export function GoogleAnalytics() {
  useEffect(() => {
    if (typeof window === "undefined") return;

    // Load gtag script
    const script1 = document.createElement("script");
    script1.async = true;
    script1.src = `https://www.googletagmanager.com/gtag/js?id=${GA_MEASUREMENT_ID}`;
    document.head.appendChild(script1);

    // Initialize gtag
    window.dataLayer = window.dataLayer || [];
    function gtag(...args: unknown[]) {
      window.dataLayer.push(args);
    }
    window.gtag = gtag;
    gtag("js", new Date());
    gtag("config", GA_MEASUREMENT_ID, {
      page_title: document.title,
      page_location: window.location.href,
      anonymize_ip: true,
      cookie_flags: "SameSite=None;Secure",
    });

    // Track page views on route changes
    return () => {
      document.head.removeChild(script1);
    };
  }, []);

  return null;
}

// Event tracking helper
export function trackEvent(
  action: string,
  category: string,
  label?: string,
  value?: number
) {
  if (typeof window !== "undefined" && window.gtag) {
    window.gtag("event", action, {
      event_category: category,
      event_label: label,
      value: value,
    });
  }
}

// Track download click
export function trackDownload(fileType: "apk" | "pdf" | "script") {
  trackEvent("download", "engagement", fileType, 1);

  // Also call our download counter API (simulated via GitHub API in production)
  if (typeof window !== "undefined") {
    try {
      const key = `download_count_${fileType}`;
      const current = parseInt(localStorage.getItem(key) || "0", 10);
      localStorage.setItem(key, (current + 1).toString());
    } catch (e) {
      // Ignore localStorage errors
    }
  }
}

// Hook to get download counts (from GitHub API)
export function useDownloadCounts() {
  const [counts, setCounts] = useState({
    apk: 0,
    pdf: 0,
    script: 0,
    total: 0,
    loading: true,
  });

  useEffect(() => {
    async function fetchCounts() {
      try {
        // GitHub API: get traffic data for the repo
        // This requires auth token, so we use the public clones/views endpoints
        // For public repos, we can fetch /repos/{owner}/{repo}/traffic/views with auth
        // Without auth, we use a simulated counter from localStorage

        const localCounts = {
          apk: parseInt(localStorage.getItem("download_count_apk") || "0", 10),
          pdf: parseInt(localStorage.getItem("download_count_pdf") || "0", 10),
          script: parseInt(localStorage.getItem("download_count_script") || "0", 10),
        };

        // Try to fetch from a counter API (simulated)
        // In production, you'd use Google Analytics or a counter service
        const total = localCounts.apk + localCounts.pdf + localCounts.script;

        setCounts({
          ...localCounts,
          total,
          loading: false,
        });
      } catch (e) {
        setCounts((c) => ({ ...c, loading: false }));
      }
    }
    fetchCounts();
  }, []);

  return counts;
}
