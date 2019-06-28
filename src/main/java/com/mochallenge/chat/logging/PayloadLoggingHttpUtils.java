package com.mochallenge.chat.logging;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class PayloadLoggingHttpUtils {

    private static final Predicate<String> HTTP_HEADER_ACCEPT_CHARSET_PREDICATE = x -> !"accept-charset".equalsIgnoreCase(x);

    private static final List<MediaType> VISIBLE_TYPES = Arrays.asList(
            MediaType.valueOf("text/*"),
            MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.valueOf("application/*+json"),
            MediaType.valueOf("application/*+xml"),
            MediaType.MULTIPART_FORM_DATA
    );

    public static String getServicePathWithQueryString(HttpServletRequest request) {
        String servicePath = getServicePath(request);
        String queryString = request.getQueryString();
        return queryString == null ? servicePath : servicePath + "?" + queryString;
    }

    public static String getServicePath(HttpServletRequest request) {
        return request.getServletPath() + Strings.nullToEmpty(request.getPathInfo());
    }

    public static String getContentAsString(byte[] content, String contentType, String contentEncoding) {
        String contentString = "";
        if (content.length > 0) {
            MediaType mediaType = MediaType.valueOf(contentType);
            boolean isVisible = VISIBLE_TYPES.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
            if (isVisible) {
                try {
                    contentString = CharMatcher.breakingWhitespace().removeFrom(new String(content, contentEncoding));
                    contentString = CharMatcher.breakingWhitespace().removeFrom(new String(content, contentEncoding));
                } catch (UnsupportedEncodingException e) {
                    log.info("Error during parsing content");
                }
            }
        }
        return contentString;
    }

    public static String formatRequestHttpHeaders(HttpServletRequest request) {
        return formatHttpHeaders(Collections.list(request.getHeaderNames()), (hn) -> Collections.list(request.getHeaders(hn)));
    }

    public static String formatResponseHttpHeaders(HttpServletResponse response) {
        return formatHttpHeaders(response.getHeaderNames(), (hn) -> response.getHeaders(hn));
    }

    private static String formatHttpHeaders(Collection<String> headerNames,
                                            Function<String, Collection<String>> getHeadersFunction) {

        Map<String, List<String>> httpHeaders =
                filterAndTransform(headerNames, x -> getHeadersFunction.apply(x), HTTP_HEADER_ACCEPT_CHARSET_PREDICATE);

        return httpHeaders != null && httpHeaders.size() != 0 ?
                Joiner.on(',').withKeyValueSeparator(":").join(httpHeaders) : "";
    }

    private static Map<String, List<String>> filterAndTransform(Collection<String> names,
                                                                Function<String, Collection<String>> valuesProvider,
                                                                Predicate<String> namePredicate) {
        return names.stream().filter(namePredicate)
                .collect(Collectors.toMap(Function.identity(), x -> new ArrayList<>(valuesProvider.apply(x))));
    }
}
