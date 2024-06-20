package com.ssd.bidflap.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // handler 종류 확인 => HandlerMethod 타입인지 체크
        // HandlerMethod가 아니면 그대로 진행
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 형변환 하기
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // @MySequred 받아오기
        Auth auth = handlerMethod.getMethodAnnotation(Auth.class);

        // method에 @MySequred가 없는 경우, 즉 인증이 필요 없는 요청
        if (auth == null) {
            return true;
        }

        // @MySequred가 있는 경우 세션이 있는지 체크
        HttpSession session = request.getSession();
        if (session == null) {
            response.sendRedirect("/auth/login?message=" + URLEncoder.encode("로그인 후 이용해주세요.", StandardCharsets.UTF_8));
            return false;
        }

        // 세션이 존재하면 유효한 유저인지 확인
        String nickname = (String) session.getAttribute("loggedInMember");
        if (nickname == null) {
            response.sendRedirect("/auth/login?message=" + URLEncoder.encode("로그인 후 이용해주세요.", StandardCharsets.UTF_8));
            return false;
        }

        // 접근 허가
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

}