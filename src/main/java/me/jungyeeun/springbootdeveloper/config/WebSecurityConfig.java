//package me.jungyeeun.springbootdeveloper.config;
//
//import lombok.RequiredArgsConstructor;
//import me.jungyeeun.springbootdeveloper.service.UserDetailService;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
//
//import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
//
//@RequiredArgsConstructor
//@Configuration
//public class WebSecurityConfig {
//
//    private final UserDetailService userService;
//
//    //스프링 시큐리티 기능 비활성화
//    //일반적으로 정적 리소스에 시큐리티 사용 비활성화
//    @Bean
//    public WebSecurityCustomizer configure(){
//        return (web) -> web.ignoring()
////                .requestMatchers(toH2Console())
////                .requestMatchers("/static/**");
//                .requestMatchers(new AntPathRequestMatcher("/h2-console/**"))
//                .requestMatchers(new AntPathRequestMatcher("/static/**"));
//
//    }
//
//    //특정 http 요청에 대한 웹 기반 보안 구성
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http
//                .authorizeHttpRequests(auth -> auth
//                .requestMatchers(   //"/login", "/signup", "/user"로 요청오면 인증,인가 없이 접근
//                        new AntPathRequestMatcher("/login"),
//                        new AntPathRequestMatcher("/signup"),
//                        new AntPathRequestMatcher("/user")
//                ).permitAll()
//                .anyRequest().authenticated())  //위에서 설정한 url 외의 요청에 대해 설정/별도 인가는 필요x, 인증이 성공된 상태여야 접근
//                .formLogin(formLogin -> formLogin
//                        .loginPage("/login")    //로그인 페이지 경로 설정
//                        .defaultSuccessUrl("/articles") //로그인 완료 시 이동할 경로
//                )
//                .logout(logout -> logout
//                        .logoutSuccessUrl("/login") //로그아웃 완료 시 이동할 경로
//                        .invalidateHttpSession(true)    //로그아웃 이후 세션 삭제할지 여부
//                )
//                .csrf(AbstractHttpConfigurer::disable)  //csrf 비활성화
//                .build();
//    }
//
//
//    //인증 관리자 관련 설정
////    @Bean
////    public AuthenticationManager authenticationManager(HttpSecurity http,
////                                                       BCryptPasswordEncoder bCryptPasswordEncoder,
////                                                       UserDetailService userDetailService) throws Exception {
////        return http.getSharedObject(AuthenticationManagerBuilder.class)
////                .userDetailsService(userService)    //사용자 정보를 가져올 서비스 설정(반드시 UserDetailsService 상속받은 개체)
////                .passwordEncoder(bCryptPasswordEncoder) //비밀번호 암호화위한 인코더 설정
////                .and()
////                .build();
////    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
//
//    //패스워드 인코더로 사용할 빈 등록
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//}
