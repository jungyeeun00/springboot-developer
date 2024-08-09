package me.jungyeeun.springbootdeveloper.config.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import me.jungyeeun.springbootdeveloper.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {    //토큰 생성, 유효성 검사, 필요한 정보 가져오는 클래스

    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    //토큰 생성
    private String makeToken(Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)   //header type:JWT
                .setIssuer(jwtProperties.getIssuer())   //iss 발급자 : properties 파일에서 설정한 값(ajufresh@gmail.com)
                .setIssuedAt(now)   //iat 발급 일시 : 현재시간
                .setExpiration(expiry)  //exp 만료 일시 : expiry 멤버 변수값
                .setSubject(user.getEmail())    //sub 토큰 제목 : 유저의 이메일
                .claim("id", user.getId())  //클레임 id : 유저 id
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())   //서명 : 비밀값과 함께 해시값을 HS256방식으로 암호화
                .compact();
    }

    //토큰 유효성 검증
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())    //비밀값으로 복호화
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //토큰 기반 인증 정보 가져옴
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);   //프로퍼티의 비밀값으로 토큰 복호화 후 클레임 가져옴
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities), token, authorities);
    }

    //토큰 기반으로 유저ID 가져옴
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()    //클레임 조회
                .setSigningKey(jwtProperties.getSecretKey())    //비밀값으로 복호화
                .parseClaimsJws(token)
                .getBody();
    }
}
