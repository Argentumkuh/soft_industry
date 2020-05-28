package com.soft_industry.demo.unit.util;

import com.soft_industry.demo.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.Cookie;

@ExtendWith(SpringExtension.class)
class UtilTest {
    @Mock
    private RequestAttributes attributes;

    @BeforeEach
    void before() {
        MockitoAnnotations.initMocks(this);
        RequestContextHolder.setRequestAttributes(attributes);
        Mockito.when(attributes.getSessionId()).thenReturn(String.valueOf(1));
    }

    @Test
    void checkSession() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "http://localhost:8080/statement");
        Assertions.assertFalse(Util.checkSession(request));
        request.setCookies(new Cookie("JSESSIONID", "1"));
        request.setAttribute("cookie", "JSESSIONID=1");
        Assertions.assertTrue(Util.checkSession(request));
    }
}
