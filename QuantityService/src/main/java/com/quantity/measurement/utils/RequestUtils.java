package com.quantity.measurement.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtils {

    public static String getHeader(String name) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) return null;

        HttpServletRequest request = attributes.getRequest();
        return request.getHeader(name);
    }

    public static Long getUserId() {
        String userId = getHeader("X-User-Id");
        return userId != null ? Long.parseLong(userId) : null;
    }

    public static String getEmail() {
        return getHeader("X-User-Email");
    }
}