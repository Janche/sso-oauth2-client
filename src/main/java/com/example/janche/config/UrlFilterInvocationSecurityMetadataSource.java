package com.example.janche.config;

import com.example.janche.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author lirong
 * @ClassName: UrlFilterInvocationSecurityMetadataSource
 * @Description: TODO
 * @date 2019-07-10 14:36
 */
@Component("urlFilterInvocationSecurityMetadataSource")
@Slf4j
public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Autowired
    private RedisTemplate redisTemplate;

    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {

        HttpServletRequest request = ((FilterInvocation) o).getHttpRequest();
        HttpServletResponse response = ((FilterInvocation) o).getHttpResponse();

        // 判断此用户有没有在其他客户端退出
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();
        String isLogin = (String) redisTemplate.opsForValue().get(Constant.REDIS_PERM_KEY_PREFIX + username);
        if(StringUtils.isEmpty(isLogin)){
            return SecurityConfig.createList("ROLE_LOGIN");
        }

        // 判断认证中心此用户是否被注销
        List<Map<String, String[]>> menuMap = (List<Map<String, String[]>>) redisTemplate.opsForValue().get(Constant.REDIS_PERM_KEY_PREFIX);
        if (null != menuMap) {
            for (Map<String, String[]> map : menuMap) {
                for (String url : map.keySet()) {
                    String[] split = url.split(":");
                    AntPathRequestMatcher antPathMatcher = new AntPathRequestMatcher(split[0], split[1]);
                    if(antPathMatcher.matches(request)){
                        return SecurityConfig.createList(map.get(url));
                    }
                }
            }
        }
        //没有匹配上的资源，都是登录访问
        return SecurityConfig.createList("ROLE_LOGIN");
    }

    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    public boolean supports(Class<?> aClass) {
        return false;
    }
}
