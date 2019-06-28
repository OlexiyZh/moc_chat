package com.mochallenge.chat.controller;

import java.time.LocalDate;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ImmutableMap;
import com.mochallenge.chat.exception.ChatValidationException;
import com.mochallenge.chat.exception.ObjectNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ChatErrorHandlingController {

    protected static final String TIMESTAMP_FIELD_NAME = "timestamp";
    protected static final String EXCEPTION_FIELD_NAME = "exception";
    protected static final String ERROR_FIELD_NAME = "error";
    protected static final String MESSAGE_FIELD_NAME = "message";


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Map handleJsonSchemaValidationExceptions(MethodArgumentNotValidException exc, HttpServletResponse response) {

        return buildError(exc, response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseBody
    public Map handleObjectNotFoundException(ObjectNotFoundException exc, HttpServletResponse response) {

        return buildError(exc, response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ChatValidationException.class)
    @ResponseBody
    public Map handleChatValidationException(ChatValidationException exc, HttpServletResponse response) {

        return buildError(exc, response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Map handleRuntimeException(RuntimeException exc, HttpServletResponse response) {
        return buildError(exc, response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map buildError(Exception exc, HttpServletResponse response, HttpStatus httpStatus) {
        log.error(exc.getMessage(), exc);
        response.setStatus(httpStatus.value());

        ImmutableMap.Builder<Object, Object> builder = ImmutableMap.builder()
                .put(TIMESTAMP_FIELD_NAME, LocalDate.now())
                .put(ERROR_FIELD_NAME, httpStatus.getReasonPhrase())
                .put(EXCEPTION_FIELD_NAME, exc.getClass().getCanonicalName())
                .put(MESSAGE_FIELD_NAME, exc.getMessage())
                ;

        return builder.build();
    }
}
