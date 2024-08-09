package me.jungyeeun.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.jungyeeun.springbootdeveloper.domain.RefreshToken;
import me.jungyeeun.springbootdeveloper.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {  //전달받은 리프레시 토큰으로 리프레시 토큰 객체를 검색해서 전달
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }
}
