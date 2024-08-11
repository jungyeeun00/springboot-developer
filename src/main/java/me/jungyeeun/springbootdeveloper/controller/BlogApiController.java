package me.jungyeeun.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.jungyeeun.springbootdeveloper.domain.Article;
import me.jungyeeun.springbootdeveloper.domain.Comment;
import me.jungyeeun.springbootdeveloper.dto.*;
import me.jungyeeun.springbootdeveloper.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController //body에 객체 데이터를 json형식으로 반환
public class BlogApiController {

    private final BlogService blogService;

    @PostMapping("/api/comments")
    public ResponseEntity<AddCommentResponse> addComment(@RequestBody AddCommentRequest request, Principal principal) {
        Comment savedComment = blogService.addComment(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(new AddCommentResponse(savedComment));
    }

    @PostMapping("/api/articles")
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request, Principal principal) { //요청할 때 응답 값을 addarticlerequest에 매핑, 현재 인증 정보를 가져오는 객체
        Article savedArticle = blogService.save(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);    //created를 응답하고 테이블에 저장된 객체 반환
    }

    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles(){
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new)  //ArticleResponse의 생성자 메소드 실행
                .toList();

        return ResponseEntity.ok()
                .body(articles);
    }

    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id){  //{id}에 해당하는 값이 들어옴
        Article article = blogService.findById(id);

        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id){
        blogService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable long id,
                                                 @RequestBody UpdateArticleRequest request) {
        Article updateArticle = blogService.update(id, request);

        return ResponseEntity.ok()
                .body(updateArticle);   //응답값은 body에 담아 전송
    }
}
