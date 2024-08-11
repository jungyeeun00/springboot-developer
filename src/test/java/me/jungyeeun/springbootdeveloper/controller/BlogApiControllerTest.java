package me.jungyeeun.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.jungyeeun.springbootdeveloper.domain.Article;
import me.jungyeeun.springbootdeveloper.domain.Comment;
import me.jungyeeun.springbootdeveloper.domain.User;
import me.jungyeeun.springbootdeveloper.dto.AddArticleRequest;
import me.jungyeeun.springbootdeveloper.dto.AddCommentRequest;
import me.jungyeeun.springbootdeveloper.dto.UpdateArticleRequest;
import me.jungyeeun.springbootdeveloper.repository.BlogRepository;
import me.jungyeeun.springbootdeveloper.repository.CommentRepository;
import me.jungyeeun.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.print.attribute.standard.Media;
import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;  //웹 테스트를 위한 mock 생성. 테스트용 mvc 환경 만들어 요청,전송,응답 기능 제공

    @Autowired
    protected ObjectMapper objectMapper;    //직렬화(자바객체->Json변환), 역직렬화 위한 클래스

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    User user;

    @BeforeEach
    public void mockMvcSetUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        blogRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @BeforeEach
    void setSecurityContext() {
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
    }

    @DisplayName("addComment: 댓글 추가에 성공한다")
    @Test
    public void addComment() throws Exception {
        //given
        final String url = "/api/comments";

        Article savedArticle = createDefaultArticle();
        final Long articleId = savedArticle.getId();
        final String content = "content";
        final AddCommentRequest userRequest = new AddCommentRequest(articleId, content);
        final String requestBody = objectMapper.writeValueAsString(userRequest);    //객체를 json으로 직렬화

        Principal principal = Mockito.mock(Principal.class);    //principal 객체에 테스트 유저 들어가도록 모킹
        Mockito.when(principal.getName()).thenReturn("username");

        //when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)  //요청타입
                .principal(principal)
                .content(requestBody)); //요청본문

        //then
        result.andExpect(status().isCreated());

        List<Comment> comments = commentRepository.findAll();

        assertThat(comments.size()).isEqualTo(1);
        assertThat(comments.get(0).getArticle().getId()).isEqualTo(articleId);
        assertThat(comments.get(0).getContent()).isEqualTo(content);
    }

    @DisplayName("addArticle:블로그 글 추가에 성공한다")
    @Test
    public void addArticle() throws Exception {
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);

        final String requestBody = objectMapper.writeValueAsString(userRequest);    //객체를 json으로 직렬화

        Principal principal = Mockito.mock(Principal.class);    //principal 객체에 테스트 유저 들어가도록 모킹
        Mockito.when(principal.getName()).thenReturn("username");

        //요청 전송
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)  //요청타입
                .principal(principal)
                .content(requestBody)); //요청본문

        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles.size()).isEqualTo(1);   //크기가 1인지 검증
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }

    @DisplayName("findAllArticles: 블로그 글 목록 조회에 성공한다")
    @Test
    public void findAllArticles() throws Exception{
        final String url = "/api/articles";
//        final String title = "title";
//        final String content = "content";
        Article savedArticle = createDefaultArticle();

//        blogRepository.save(Article.builder()
//                .title(title)
//                .content(content)
//                .build());

        final ResultActions resultActions = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));   //어떤 타입으로 응답받을지

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(savedArticle.getContent()))
                .andExpect(jsonPath("$[0].title").value(savedArticle.getTitle()));
    }

    @DisplayName("findArticle: 블로그 글 조회에 성공한다")
    @Test
    public void findArticle() throws Exception{
        final String url = "/api/articles/{id}";
//        final String title = "title";
//        final String content = "content";
//
//        Article savedArticle = blogRepository.save(Article.builder()
//                .title(title)
//                .content(content)
//                .build());
        Article savedArticle = createDefaultArticle();

        final ResultActions resultActions = mockMvc.perform(get(url, savedArticle.getId()));

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(savedArticle.getContent()))
                .andExpect(jsonPath("$.title").value(savedArticle.getTitle()));

    }

    @DisplayName("deleteArticle: 블로그 글 삭제에 성공한다")
    @Test
    public void deleteArticle() throws Exception{
        final String url = "/api/articles/{id}";
//        final String title = "title";
//        final String content = "content";
//
//        Article savedArticle = blogRepository.save(Article.builder()
//                .title(title)
//                .content(content)
//                .build());
        Article savedArticle = createDefaultArticle();

        mockMvc.perform(delete(url, savedArticle.getId()))
                .andExpect(status().isOk());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles).isEmpty();
    }

    @DisplayName("updateArticle: 블로그 글 수정에 성공한다")
    @Test
    public void updateArticle() throws Exception {
        final String url = "/api/articles/{id}";
//        final String title = "title";
//        final String content = "content";
//
//        Article savedArticle = blogRepository.save(Article.builder()
//                .title(title)
//                .content(content)
//                .build());
        Article savedArticle = createDefaultArticle();

        final String newTitle = "newTitle";
        final String newContent = "new content";

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);

        ResultActions result = mockMvc.perform(put(url, savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)  //요청타입
                .content(objectMapper.writeValueAsString(request)));    //객체를 json으로 직렬화

        result.andExpect(status().isOk());

        Article article = blogRepository.findById(savedArticle.getId()).get();

        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);
    }

    private Article createDefaultArticle() {
        return blogRepository.save(Article.builder()
                .title("title")
                .author(user.getUsername())
                .content("content")
                .build());
    }
}