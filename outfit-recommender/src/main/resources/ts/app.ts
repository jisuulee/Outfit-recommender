// 토큰 저장/조회
export const tokenKey = "token";
export function setToken(t: string) { localStorage.setItem(tokenKey, t); }
export function getToken(): string | null { return localStorage.getItem(tokenKey); }
export function clearToken() { localStorage.removeItem(tokenKey); }

// API 호출 도우미
export async function api(path: string, options: RequestInit = {}) {
    const headers = new Headers(options.headers || {});
    headers.set("Content-Type", "application/json");
    const t = getToken();
    if (t) headers.set("Authorization", "Bearer " + t);
    const res = await fetch(path, { ...options, headers });
    if (res.status === 401) {
        alert("로그인이 필요합니다.");
        location.href = "/login.html";
        throw new Error("Unauthorized");
    }
    return res;
}

// 현재 페이지에 따라 모듈 실행
// 각 페이지 하단에서 window.run = 해당함수 로 세팅해 호출하게 함
declare global { interface Window { run?: () => void; } }
document.addEventListener("DOMContentLoaded", () => { if (window.run) window.run(); });

// 간단 네비게이션 바 토글 (필요 시)
export function requireAuth() {
    if (!getToken()) location.href = "/login.html";
}
