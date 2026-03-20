package com.scriptrunner.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthCookieProvider {
    private static final String COOKIE_NAME = "script_runner_access_token";
    private static final int MAX_AGE_IN_SECONDS = 60 * 60 * 8;

    public void addAccessTokenCookie(HttpServletResponse response, String token){
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(MAX_AGE_IN_SECONDS);
        cookie.setAttribute("SameSite", "Strict");

        response.addCookie(cookie);
    }

    public void clearAccessTokenCookie(HttpServletResponse response){
        Cookie cookie = new Cookie(COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }
}
