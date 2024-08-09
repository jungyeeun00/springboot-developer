package me.jungyeeun.springbootdeveloper.config.jwt;

import io.jsonwebtoken.Jwts;
import me.jungyeeun.springbootdeveloper.domain.User;
import me.jungyeeun.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class TokenProviderTest {
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProperties jwtProperties;

    @DisplayName("generateToken(): 유저 정보와 만료 기간 전달해 토큰 생성")
    @Test
    void generateToken() {
        //테스트 유저 생성
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        //토큰 생성
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        //jwt라이브러리 사용해 토큰 복호화.
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        //토큰 만들때 claim으로 넣어둔 id값이 테스트 유저id와 동일한지 확인
        assertThat(userId).isEqualTo(testUser.getId());
    }

    @DisplayName("validToken(): 만료된 토큰인 때에 유효성 검증 실패")
    @Test
    void validToken_invalidToken() {
        //이미 만료된 토큰 생성
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        //유효한 토큰인지 검증 후 결과값 반환
        boolean result = tokenProvider.validToken(token);

        //반환값이 유효하지 않은 토큰임을 확인
        assertThat(result).isFalse();
    }

    @DisplayName("validToken():유효한 토큰인 때에 유효성 검증에 성공")
    @Test
    void validToken_validToken() {
        //만료되지 않은 토큰 생성.
        String token = JwtFactory.withDefaultValues().createToken(jwtProperties);

        boolean result = tokenProvider.validToken(token);

        assertThat(result).isTrue();
    }

    @DisplayName("getAuthentication(): 토큰 기반으로 인증 정보를 가져올 수 있다")
    @Test
    void getAuthentication() {
        //토큰 생성. 토큰 제목은 "useremail.com"
        String userEmail = "user@email.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);

        //인증객체 반환
        Authentication authentication = tokenProvider.getAuthentication(token);

        //반환받은 인증 객체 유저 이름이 위의 subject와 같은지 확인
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
    }

    @DisplayName("getUserId(): 토큰으로 유저 id를 가져올 수 있다")
    @Test
    void getUserId() {
        //토큰 생성.클레임 추가("id", 1)
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);

        //유저 id반환
        Long userIdByToken = tokenProvider.getUserId(token);

        //반환받은 유저id가 위의 유저id과 같은지 확인
        assertThat(userIdByToken).isEqualTo(userId);
    }
}
