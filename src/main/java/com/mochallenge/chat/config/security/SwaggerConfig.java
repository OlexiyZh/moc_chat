package com.mochallenge.chat.config.security;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private String CONTACT_NAME = "Oleksii Zhylenko";
    private String CONTACT_URL = "https://github.com/OlexiyZh/moc_chat";
    private String CONTACT_EMAIL;
    private String TITLE = "MoC Chat";
    private String DESCRIPTION = "Chat application for Backend Challenge";
    private String VERSION = "1.0.0";
    private String TERMS_OF_SERVICE_URL = CONTACT_URL;
    private String LICENSE = CONTACT_URL;
    private String LICENSE_URL = CONTACT_URL;
    private static final String BASE_PACKAGE_TO_SCAN = "com.mochallenge.chat.controller";

    @Bean
    public Docket api() {

        Contact contact = new Contact(
                this.CONTACT_NAME,
                this.CONTACT_URL,
                this.CONTACT_EMAIL);

        ApiInfo apiInfo = new ApiInfo(
                this.TITLE,
                this.DESCRIPTION,
                this.VERSION,
                this.TERMS_OF_SERVICE_URL,
                contact,
                this.LICENSE,
                this.LICENSE_URL,
                Collections.emptySet());

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .select()
                .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE_TO_SCAN))
                .build();
    }
}
