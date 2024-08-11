const token = searchParam('token')

if (token) {
    localStorage.setItem("access_token", token)     //파라미터로 받은 토큰을 로컬스토리지에 저장
}

function searchParam(key) {
    return new URLSearchParams(location.search).get(key);
}