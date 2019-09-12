package janche.controller;

import janche.domain.SsoUser;
import janche.restResult.RestResult;
import janche.restResult.ResultGenerator;
import janche.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
public class TestController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${auth-server}")
    public String auth_server;

    @GetMapping("/normal")
    public String normal( ) {
        return "normal permission test success !!!";
    }

    @GetMapping("/medium")
    public String medium() {
        return "hasAnyAuthority permission test success !!!";
    }

    @GetMapping("/admin")
    public String admin() {
        return "hasPermission permission test success !!!";
    }

    @GetMapping("/user")
    public RestResult getLoginUser(){

        String url = auth_server + "/user/oauth/sso";
        String tokenValue = SecurityUtils.getJwtToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + tokenValue);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        SsoUser user = restTemplate.postForObject(url, entity, SsoUser.class);
        return ResultGenerator.genSuccessResult(user);
    }
}
