package com.example.Account.Service.handler;

import com.example.Account.Service.model.ApiError;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .message("Access Denied!")
                .path(request.getRequestURI())
                .build();

        response.setStatus(HttpStatus.FORBIDDEN.value());

        try (PrintWriter writer = response.getWriter()) {
            JsonMapper jsonMapper = new JsonMapper();
            jsonMapper.registerModule(new JavaTimeModule());

            writer.print(jsonMapper.writeValueAsString(apiError));
        }

    }
}
