package com.talet.talet.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talet.talet.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTRequestFilter extends OncePerRequestFilter {

    private final JWTTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;
    private final RedisUtil redisUtil;
    private final PathMatcher pathMatcher;
    private final List<String> EXCLUDE_URLS = List.of(
            "/auth/google",
            "/auth/apple",
            "/book/all",
            "/admin/login",
            "/auth/admin/login",
            "/admin/book/add",
            "/book/find/tag/**",
            "/book/ranking",
            "/images/**",
            "/voices/**",
            "/tts/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            // 헤더가 없을 때
            boolean isExcluded = EXCLUDE_URLS.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
            boolean isBookTagRequest = requestURI.matches("^/book/find/tag/\\d+$");
            if (isExcluded || isBookTagRequest) {
                filterChain.doFilter(request, response);
                return;
            }
            setErrorResponse(response, ErrorEnum.AUTH_UNAUTHORIZED);
            return;
        }
        String token = jwtTokenUtil.getPureToken(header);
        String identifier;
        try {
            identifier = jwtTokenUtil.getIdentifierFromToken(token);
        } catch (ExpiredJwtException e) {
            // 오류
            setErrorResponse(response, ErrorEnum.AUTH_TOKEN_EXPIRED);
            return;
        } catch (Exception e) {
            // 오류
            setErrorResponse(response, ErrorEnum.AUTH_TOKEN_INVALID);
            return;
        }
        String tokenType = jwtTokenUtil.getTokenType(token);
        try {
            RedisTokenType type = RedisTokenType.valueOf(tokenType);

            switch (type) {
                case SIGN_UP_TOKEN -> {
                    if (!"/auth/apple/sign-up".equals(request.getRequestURI()) && !"/auth/google/sign-up".equals(request.getRequestURI())) {
                        setErrorResponse(response, ErrorEnum.AUTH_FORBIDDEN);
                        return;
                    }
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(identifier, null, List.of());
                    SecurityContextHolder.clearContext();
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
                case ACCESS_TOKEN ->  {
                    if (!redisUtil.validateToken(RedisTokenType.ACCESS_TOKEN, identifier, token)) {
                        setErrorResponse(response, ErrorEnum.AUTH_TOKEN_EXPIRED);
                        return;
                    }
                    UserDetails userDetails = userDetailsService.loadUserByUsername(identifier);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.clearContext();
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
                case REFRESH_TOKEN ->  {
                    if (!redisUtil.validateToken(RedisTokenType.REFRESH_TOKEN, identifier, token)) {
                        setErrorResponse(response, ErrorEnum.AUTH_TOKEN_EXPIRED);
                        return;
                    }
                    if("/auth/refresh".equals(request.getRequestURI()) || "/auth/validate".equals(request.getRequestURI())) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(identifier);
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.clearContext();
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } else {
                        // 리프레쉬 토큰으로 접근은 금지
                        setErrorResponse(response, ErrorEnum.AUTH_FORBIDDEN);
                        return;
                    }
                }
                case ADMIN_TOKEN -> {
                    if (!redisUtil.validateToken(RedisTokenType.ADMIN_TOKEN, identifier, token)) {
                        setErrorResponse(response, ErrorEnum.AUTH_TOKEN_EXPIRED);
                        return;
                    }
                    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(identifier, null, authorities);
                    SecurityContextHolder.clearContext();
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (IllegalArgumentException e) {
            return;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("[디버그] authentication: {}", authentication);
        log.debug("[디버그] isAuthenticated: {}", authentication.isAuthenticated());
        log.debug("[디버그] principal: {}", authentication.getPrincipal());
        log.debug("[디버그] authorities: {}", authentication.getAuthorities());
        filterChain.doFilter(request, response);
    }

    private void setErrorResponse(HttpServletResponse response, ErrorEnum error) throws IOException {
        String code = error.getCode();
        HttpStatus status = error.getStatus();
        String message = error.getMessage();
        log.error(message);
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        TaletApiResponse<Object> errorResponse = TaletApiResponse.error(code, status, message);
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}
