package com.expensetracker.app.dto;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "com.expensetracker.app.interfaces")
public class SuccessResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Apply to all controller responses in the interfaces package
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        // Don't wrap known error bodies
        if (body instanceof ApiError) {
            return body;
        }

        // If already wrapped, return as-is
        if (body instanceof ApiResponse) {
            return body;
        }

        // Only wrap for 2xx
        int status = 200;
        if (response instanceof ServletServerHttpResponse servletResp) {
            status = servletResp.getServletResponse().getStatus();
        }
        if (status >= 200 && status < 300) {
            return new ApiResponse<>("success", body);
        }

        return body;
    }
}