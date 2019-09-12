package janche.config;

import janche.restResult.ResultCode;
import janche.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户未登录时的处理
 * @author lirong
 * @date 2019-8-8 17:37:27
 */
@Component("securityAuthenticationEntryPoint")
@Slf4j
public class SecurityAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
		log.info("尚未登录:" + authException.getMessage());

		// 若是在其他客户端退出，则交由前端跳转，否则客户端会出现循环重定向 直到 客户端在认证中心申请的access_token过期，才会再次跳转到认证中心
		if (authException.getMessage().contains("其他客户端")){
			ResponseUtils.renderJson(request, response, ResultCode.OTHER_CLIENT_LOGOUT, null);
		}
		response.sendRedirect(request.getContextPath() + "/login");
	}
}
