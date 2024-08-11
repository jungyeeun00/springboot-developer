package me.jungyeeun.springbootdeveloper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.jungyeeun.springbootdeveloper.domain.Article;
import me.jungyeeun.springbootdeveloper.domain.Comment;
import me.jungyeeun.springbootdeveloper.dto.AddArticleRequest;
import me.jungyeeun.springbootdeveloper.dto.AddCommentRequest;
import me.jungyeeun.springbootdeveloper.dto.UpdateArticleRequest;
import me.jungyeeun.springbootdeveloper.repository.BlogRepository;
import me.jungyeeun.springbootdeveloper.repository.CommentRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor //final이 붙거나 @notnull이 붙은 필드의 생성자 추가
@Service
public class BlogService {

    private final BlogRepository blogRepository;
    private final CommentRepository commentRepository;

    public Comment addComment(AddCommentRequest request, String userName) {
        Article article = blogRepository.findById(request.getArticleId())
                .orElseThrow(() -> new IllegalArgumentException("not found : " + request.getArticleId()));

        return commentRepository.save(request.toEntity(userName, article));
    }

    public Article save(AddArticleRequest request, String userName) {
        return blogRepository.save(request.toEntity(userName));
    }

    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    public Article findById(long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

//    public void delete(long id) {
//        blogRepository.deleteById(id);
//    }
//
//    @Transactional  //메서드를 하나의 트랜잭션으로 묶음. 중간에 에러가 발생해도 제대로 된 값 수정 보장
//    public Article update(long id, UpdateArticleRequest request) {
//        Article article = blogRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("not found :" + id));
//
//        article.update(request.getTitle(), request.getContent());
//
//        return article;
//    }

    public void delete(long id) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found" + id)); //본인 글 아닐 경우 예외 발생

        authorizeArticleAuthor(article);
        blogRepository.delete(article);
    }

    @Transactional
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found" + id)); //본인 글 아닐 경우 예외 발생

        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());

        return article;
    }

    //게시글을 작성한 유저인지 확인
    private static void authorizeArticleAuthor(Article article) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!article.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }
    }
}