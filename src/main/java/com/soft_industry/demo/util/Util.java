package com.soft_industry.demo.util;

import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

public class Util {
    public static boolean checkSession(HttpServletRequest request) {
        String sessionId = Collections.list(request.getHeaders("cookie")).stream()
                .filter(cookie -> cookie.contains("JSESSIONID"))
                .findFirst().map(cookie -> cookie.substring(11))
                .orElse("");
        String currentSessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        return sessionId.equals(currentSessionId);
    }
}
