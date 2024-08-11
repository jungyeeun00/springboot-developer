// 삭제 기능
const deleteButton = document.getElementById('delete-btn'); //html에서 id가 'delete-btn'인 엘리먼트에서

if (deleteButton) {
    deleteButton.addEventListener('click', event => {   //클릭 이벤트가 발생되면
        let id = document.getElementById('article-id').value;
        function success(){
            alert('삭제가 완료되었습니다.');
            location.replace('/articles');
        }

        function fail(){
            alert('삭제 실패했습니다');
            location.replace('/articles');
        }

        httpRequest("DELETE", "/api/articles/" + id, null, success, fail);
    });
}

// 수정 기능
const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        body = JSON.stringify({  //html에 입력한 데이터를 json 형식으로 바꿔 보냄
            title: document.getElementById('title').value,
            content: document.getElementById('content').value
        });

        function success(){
            alert('수정 완료되었습니다.');
            location.replace('/articles/' + id);
        }

        function fail(){
            alert('수정 실패했습니다');
            location.replace('/articles/' + id);
        }

        httpRequest("PUT", "/api/articles/" + id, null, success, fail);
    });
}

// 생성 기능
const createButton = document.getElementById('create-btn');

if (createButton) {
    //등록 버튼 클릭 시 /api/articles로 요청 보냄
    createButton.addEventListener('click', event => {
        body = JSON.stringify({
            title: document.getElementById("title").value,
            content: document.getElementById("content").value,
        });

        function success() {
            alert('등록 완료되었습니다.');
            location.replace('/articles');
        }

        function fail() {
            alert('등록 실패했습니다.');
            location.replace('/articles');
        }

        httpRequest("POST", "/api/articles", body, success, fail);
    });
}
    //쿠키 가져오는 함수
    function getCookie(key){
        var result = null;
        var cookie = document.cookie.split(";");
        cookie.some(function (item) {
            item = item.replace(" ", "");

            var dic = item.split("=");

            if (key === dic[0]) {
                result = dic[1];
                return true;
            }
        });

        return result;
    }

    //HTTP 요청 보내는 함수
function httpRequest(method, url, body, success, fail) {
    fetch(url, {
        method: method,
        headers: {
            //로컬 스토리지에서 엑세스 토큰 값을 가져와 헤더에 추가
            Authorization: "Bearer " + localStorage.getItem('access_token'),
            "Content-Type": "application/json",
        },
        body: body,
    }).then((response) => {
        console.log(response);
        if (response.status === 200 || response.status === 201) {
            return success();
        }
        const refresh_token = getCookie("refresh_token");
        if (response.status === 401 && refresh_token) {
            //응답 권한이 없다는 에러 발생시 리프레시 토큰과 함께 새로운 액세스 토큰 요청
            fetch("/api/token", {
                method: "POST",
                headers: {
                    Authorization: "Bearer " + localStorage.getItem("access_token"),
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    refreshToken: getCookie("refresh_token"),
                }),
            })
                .then((res) => {
                    if (res.ok) {
                        return res.json();
                    }
                })
                .then((result) => {
                    //재발급이 성공하면 로컬 스토리지값을 새로운 액세스 토큰으로 교체
                    localStorage.setItem("access_token", result.accessToken);
                    httpRequest(method, url, body, success, fail);
                })
                .catch((error) => fail());
        } else {
            return fail();
        }
    });
}

//댓글 생성 기능
const commentCreateButton = document.getElementById('comment-create-btn');

if (commentCreateButton) {
    commentCreateButton.addEventListener('click', event => {
        articleId = document.getElementById('article-id').value;

        body = JSON.stringify({
            articleId: articleId,
            content: document.getElementById('content').value
        });
        function success() {
            alert('등록 완료되었습니다');
            location.replace('/articles/' + articleId);
        };
        function fail() {
            alert('등록 실패하였습니다');
            location.replace('/articles/' + articleId);
        };

        httpRequest('POST', '/api/comments', body, success, fail);
    })
}