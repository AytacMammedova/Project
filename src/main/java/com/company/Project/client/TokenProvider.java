//package com.company.Project.client;
//
//
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//
//import java.util.Map;
//
//@Component
//public class TokenProvider {
//
//    private String token;
//
//    public String getToken() {
//        if (token == null || isExpired(token)) {
//            token = fetchTokenFromPaymentService();
//        }
//        return token;
//    }
//
//    private boolean isExpired(String token) {
//        // Sadə check: həqiqi implementasiyada JWT expiration parse edə bilərsən
//        return false;
//    }
//
//    private String fetchTokenFromPaymentService() {
//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        // Payment service login üçün service account username və password
//        Map<String, String> body = Map.of(
//                "username", "service_account",
//                "password", "service_password"
//        );
//
//        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
//
//        ResponseEntity<Map> response = restTemplate.postForEntity(
//                "http://localhost:8081/auth/login",
//                request,
//                Map.class
//        );
//
//        return (String) response.getBody().get("token"); // Payment service login JWT qaytarmalıdır
//    }
//}
//
