package com.example.ll.finalproject.app.security.handler;

import com.example.ll.finalproject.app.base.dto.RsData;
import com.example.ll.finalproject.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@Slf4j
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        RsData rs = RsData.of("F-AccessDenied", "인증 실패", null);

        response.setCharacterEncoding("UTF-8");
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(403);
        response.getWriter().append(Util.json.toStr(rs));
    }
}
