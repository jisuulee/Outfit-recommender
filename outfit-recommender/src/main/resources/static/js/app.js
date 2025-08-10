(function(){
    const key = 'token';
    window.setToken   = (t) => localStorage.setItem(key, t);
    window.getToken   = ()  => localStorage.getItem(key);
    window.clearToken = ()  => localStorage.removeItem(key);

    window.api = async (path, options = {}) => {
        const headers = new Headers(options.headers || {});
        headers.set('Content-Type', 'application/json');
        const t = window.getToken();
        if (t) headers.set('Authorization', 'Bearer ' + t);
        const res = await fetch(path, { ...options, headers });
        if (res.status === 401) {
            alert('로그인이 필요합니다.');
            location.href = '/login';
            throw new Error('Unauthorized');
        }
        return res;
    };
})();
