package me.jungyeeun.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.jungyeeun.springbootdeveloper.config.jwt.JwtFactory;
import me.jungyeeun.springbootdeveloper.config.jwt.JwtProperties;
import me.jungyeeun.springbootdeveloper.domain.RefreshToken;
import me.jungyeeun.springbootdeveloper.domain.User;
import me.jungyeeun.springbootdeveloper.dto.CreateAccessTokenRequest;
import me.jungyeeun.springbootdeveloper.repository.RefreshTokenRepository;
import me.jungyeeun.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.JsonPath;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TokenApiControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        userRepository.deleteAll();
    }

    @DisplayName("createdNewAccessToken: 새로운 토큰 발급")
    @Test
    void createNewAccessToken() throws Exception {
        //given
        final String url = "/api/token";
        //테스트 유저 생성
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());
        //리프레시 토큰 만들어
        String refreshToken = JwtFactory.builder()
                .claims(Map.of("id", testUser.getId()))
                .build()
                .createToken(jwtProperties);
        //db에 저장
        refreshTokenRepository.save(new RefreshToken(testUser.getId(), refreshToken));
        //요청 본문에 리프레시 토큰 포함해 요청 객체 생성
        CreateAccessTokenRequest request = new CreateAccessTokenRequest();
        request.setRefreshToken(refreshToken);
        final String requestBody = objectMapper.writeValueAsString(request);

        //when
        //토큰 추가API 요청
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)  //요청 타입 JSON
                .content(requestBody)); //만들어둔 객체를 요청 본문으로 보냄

        //then
        resultActions
                .andExpect(status().isCreated())    //응답 코드 iscreated인지 확인
                .andExpect(jsonPath("$.accessToken").isNotEmpty()); //응답으로 온 엑세스 토큰 비어있지 않은지 확인
    }
}
