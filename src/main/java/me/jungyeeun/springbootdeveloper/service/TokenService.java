package me.jungyeeun.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.jungyeeun.springbootdeveloper.config.jwt.TokenProvider;
import me.jungyeeun.springbootdeveloper.domain.User;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    //전달받은 리프레시 토큰으로 토큰 유효성 검사 진행
    public String createNewAccessToken(String refreshToken) {
        //토큰 유효성 검사 실패시 예외 발생
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }

        //유효한 토큰이면 사용자id찾음
        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        //사용자id로 사용자 찾음
        User user = userService.findById(userId);
        //새로운 엑세스 토큰 생성
        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}
