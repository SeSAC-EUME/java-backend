package com.project.eume.config.security.oauth2.handler;

import com.project.eume.exceptions.dto.WarnLogData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Value("${spring.security.oauth2.uri.base}")
    private String REDIRECT_URL;

    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException exception
    ) throws IOException {
        // 콘솔에 직접 출력 (디버깅용)
        System.out.println("\n========== OAuth2 Authentication Failure ==========");
        System.out.println("Exception Type: " + exception.getClass().getName());
        System.out.println("Exception Message: " + exception.getMessage());
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Request URL: " + request.getRequestURL());
        System.out.println("Query String: " + request.getQueryString());
        System.out.println("Remote Address: " + request.getRemoteAddr());

        // OAuth2 관련 상세 정보
        if (exception instanceof OAuth2AuthenticationException oauth2Exception) {
            System.out.println("OAuth2 Error Code: " + oauth2Exception.getError().getErrorCode());
            System.out.println("OAuth2 Error Description: " + oauth2Exception.getError().getDescription());
            System.out.println("OAuth2 Error URI: " + oauth2Exception.getError().getUri());
        }

        // Cause 체인 출력
        Throwable cause = exception.getCause();
        int depth = 1;
        while (cause != null) {
            System.out.println("Cause [" + depth + "] Type: " + cause.getClass().getName());
            System.out.println("Cause [" + depth + "] Message: " + cause.getMessage());
            cause = cause.getCause();
            depth++;
        }

        // 스택 트레이스 출력
        System.out.println("Full Stack Trace:");
        exception.printStackTrace(System.out);
        System.out.println("====================================================\n");

        String errorCode = mapExceptionToErrorCode(exception);
        String redirectUrl = UriComponentsBuilder.fromUriString(REDIRECT_URL)
            .queryParam("error", errorCode)
            .build().toUriString();
        System.out.println("Redirecting to: " + redirectUrl + " with errorCode: " + errorCode);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private String mapExceptionToErrorCode(AuthenticationException e) {
        if (e instanceof OAuth2AuthenticationException) {
            String msg = e.getMessage();
            if (msg.contains("지원하지 않는")) return "PROVIDER_NOT_SUPPORTED";
            if (msg.contains("동의")) return "CONSENT_REQUIRED";
        }
        return "OAUTH2_ERROR";
    }
}
