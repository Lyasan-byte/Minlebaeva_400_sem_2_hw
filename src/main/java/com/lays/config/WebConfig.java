//package com.lays.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.multipart.support.StandardServletMultipartResolver;
//import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
//import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
//
//@ComponentScan("com.lays.controller")
//@EnableWebMvc
//@Configuration
//public class WebConfig extends WebMvcConfigurationSupport {
//    @Override
//    protected void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
//        configurer.enable();
//    }
//
//    @Bean
//    public StandardServletMultipartResolver multipartResolver() {
//        return new StandardServletMultipartResolver();
//    }
//
//    @Bean
//    public FreeMarkerViewResolver freeMarkerViewResolver() {
//        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
//        resolver.setCache(false);
//        resolver.setSuffix(".ftl");
//        resolver.setPrefix("");
//        resolver.setContentType("text/html;charset=UTF-8");
//        return resolver;
//    }
//
//    @Bean
//    public FreeMarkerConfigurer freeMarkerConfigurer() {
//        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
//        configurer.setTemplateLoaderPaths("WEB-INF/templates/");
//        return configurer;
//    }
//}
