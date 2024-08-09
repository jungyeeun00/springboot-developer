// 삭제 기능
const deleteButton = document.getElementById('delete-btn'); //html에서 id가 'delete-btn'인 엘리먼트에서

if (deleteButton) {
    deleteButton.addEventListener('click', event => {   //클릭 이벤트가 발생되면
        let id = document.getElementById('article-id').value;
        fetch(`/api/articles/${id}`, {  //해당url에 delete요청 보냄
            method: 'DELETE'
        })
            .then(() => {   //fetch()가 완료되면 연이어 실행되는 메소드
                alert('삭제가 완료되었습니다.');
                location.replace('/articles');
            });
    });
}

// 수정 기능
const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        fetch(`/api/articles/${id}`, {
            method: 'PUT',
            headers: {  //요청형식 지정
                "Content-Type": "application/json",
            },
            body: JSON.stringify({  //html에 입력한 데이터를 json 형식으로 바꿔 보냄
                title: document.getElementById('title').value,
                content: document.getElementById('content').value
            })
        })
            .then(() => {
                alert('수정이 완료되었습니다.');
                location.replace(`/articles/${id}`);
            });
    });
}

// 생성 기능
const createButton = document.getElementById('create-btn');

if (createButton) {
    createButton.addEventListener('click', event => {
        fetch('/api/articles', {
            method: 'POST',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                title: document.getElementById('title').value,
                content: document.getElementById('content').value
            })
        })
            .then(() => {
                alert('등록 완료되었습니다.');
                location.replace('/articles');
            });
    });
}