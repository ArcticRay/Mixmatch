package com.example.arcticray.mixmatch.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController {
    @RequestMapping(value = "/error", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;
        if (status != null) {
            try {
                code = HttpStatus.valueOf(Integer.parseInt(status.toString()));
            } catch (Exception ignored) {}
        }
        String message = switch (code) {
            case NOT_FOUND    -> "404 – Resource not found";
            case BAD_REQUEST  -> "400 – Bad request";
            default           -> code.value() + " – Something went wrong";
        };
        return ResponseEntity
                .status(code)
                .contentType(MediaType.TEXT_PLAIN)
                .body(message);
    }
}
