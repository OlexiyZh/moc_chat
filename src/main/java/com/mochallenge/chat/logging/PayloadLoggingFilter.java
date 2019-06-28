package com.mochallenge.chat.logging;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PayloadLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
        } else {
            doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain);
        }
    }

    private static ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper) request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

    private static ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }

    protected void doFilterWrapped(ContentCachingRequestWrapper request,
                                   ContentCachingResponseWrapper response,
                                   FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        }
        finally {
            if (log.isInfoEnabled()) {
                logInbound(request);
                logOutbound(response);
            }
            response.copyBodyToResponse();
        }
    }

    private static void logInbound(ContentCachingRequestWrapper request) {
        String method = request.getMethod();
        String path = PayloadLoggingHttpUtils.getServicePathWithQueryString(request);
        String headers = PayloadLoggingHttpUtils.formatRequestHttpHeaders(request);
        String payload = PayloadLoggingHttpUtils.getContentAsString(request.getContentAsByteArray(),
                                                                    request.getContentType(),
                                                                    request.getCharacterEncoding());
        log.info("Inbound  -> method='{}' path='{}' headers='{}' payload='{}'", new Object[]{method, path, headers, payload});
    }

    private static void logOutbound(ContentCachingResponseWrapper response){
        int httpStatusCode = response.getStatusCode();
        String headers = PayloadLoggingHttpUtils.formatResponseHttpHeaders(response);
        String payload = PayloadLoggingHttpUtils.getContentAsString(response.getContentAsByteArray(),
                                                                    response.getContentType(),
                                                                    response.getCharacterEncoding());

        log.info("Outbound <- httpStatusCode='{}' headers='{}' payload='{}'", new Object[]{httpStatusCode, headers, payload});
    }






}