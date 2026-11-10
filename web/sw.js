// Deixa o app abrir sem internet (usado só quando instalado como PWA, no iPhone).
const CACHE = "controle-alojamento-v1";
const ARQ = ["./", "./index.html", "./jspdf.umd.min.js", "./manifest.webmanifest", "./icon-192.png", "./icon-512.png"];
self.addEventListener("install", (e) => { e.waitUntil(caches.open(CACHE).then((c) => c.addAll(ARQ))); self.skipWaiting(); });
self.addEventListener("activate", (e) => { e.waitUntil(caches.keys().then((ks) => Promise.all(ks.filter((k) => k !== CACHE).map((k) => caches.delete(k))))); self.clients.claim(); });
self.addEventListener("fetch", (e) => { e.respondWith(fetch(e.request).catch(() => caches.match(e.request))); });
