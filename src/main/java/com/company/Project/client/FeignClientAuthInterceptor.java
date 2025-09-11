//package com.company.Project.client;
//
//import feign.RequestInterceptor;
//import feign.RequestTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class FeignClientAuthInterceptor implements RequestInterceptor {
//
//    private final TokenProvider tokenProvider;
//
//    public FeignClientAuthInterceptor(TokenProvider tokenProvider) {
//        this.tokenProvider = tokenProvider;
//    }
//
//    @Override
//    public void apply(RequestTemplate template) {
//        String token = tokenProvider.getToken();
//        template.header("Authorization", "Bearer " + token);
//    }
//
//
//}
