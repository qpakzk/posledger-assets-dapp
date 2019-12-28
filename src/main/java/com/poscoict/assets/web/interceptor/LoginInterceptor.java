package com.poscoict.assets.web.interceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.poscoict.assets.model.UserVo;

public class LoginInterceptor extends HandlerInterceptorAdapter {
	
	private static final Logger logger = LogManager.getLogger(LoginInterceptor.class);
	
	//@Autowired
	//private UserService userService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
		try {
			UserVo sessionUser = (UserVo) request.getSession().getAttribute(UserVo.sessionKey);
			
			if (sessionUser == null) {
				logger.info("user session is empty, redirect authorize url");
				response.sendRedirect(request.getContextPath()+"/login");
				return false;
			}
			
			/*
			if (sessionUser==null) {
				String userId = "test02";
				UserVo user = userService.getUser(userId);
				if (user!=null) {
					request.getSession().setAttribute(UserVo.sessionKey, user);
				}
			}
			*/
		} catch(Exception e) {
			logger.error(e);
		}
		return true;
	}
}