package com.sweetfun.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetfun.annotation.RequireToken;
import com.sweetfun.response.Result;
import com.sweetfun.utils.JwtUtils;
import com.sweetfun.utils.UserContext;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    private final ObjectMapper objectMapper;

    public TokenInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod method = (HandlerMethod) handler;
        RequireToken tokenAnnotation = method.getMethodAnnotation(RequireToken.class);
        if (tokenAnnotation == null) {
            tokenAnnotation = method.getBeanType().getAnnotation(RequireToken.class);
        }
        if (tokenAnnotation != null && tokenAnnotation.required()) {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                Result<Object> error = Result.error(HttpStatus.UNAUTHORIZED.value(), "缺少或无效的token");
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write(objectMapper.writeValueAsString(error));
                return false;
            }
            token = token.replace("Bearer ", "");
            try {
                Claims claims = jwtUtils.parseToken(token);
                Long userId = Long.valueOf(claims.getSubject());
                UserContext.setUserId(userId);
            } catch (Exception exception) {
                Result<Object> error = Result.error(HttpStatus.UNAUTHORIZED.value(), "Token解析失败");
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write(objectMapper.writeValueAsString(error));
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }

}






















