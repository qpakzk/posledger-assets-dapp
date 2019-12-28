package com.poscoict.assets.web.filter;

import java.io.IOException;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class RequestFilter implements Filter {

	private static final Logger logger = LogManager.getLogger(RequestFilter.class);

	protected FilterConfig filterConfig;
	
	private String excludePatterns;
	
	private boolean isMatchExcludePatterns(String uri) {
		boolean ret = false;
		if (StringUtils.isNotEmpty(excludePatterns)) {
			String[] patterns = StringUtils.split(excludePatterns, ",");
			if (patterns!=null && patterns.length>0) {
				for (int i=0; i<patterns.length; i++) {
					PathMatcher matcher = new AntPathMatcher();
					if (matcher.match(patterns[i].trim(), uri)) {
						ret = true;
						break;
					}
				}
			}
		}
		return ret;
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info("request filter initilized..");
		this.excludePatterns = filterConfig.getInitParameter("excludePatterns");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		
		String currentPath = request.getServletPath();
		
		if (isMatchExcludePatterns(currentPath)) {
		    chain.doFilter(request, res);
		    return;
		}
		
		request.setAttribute("currentPath", currentPath);
		
		//logger.info("current path : " + currentPath);
		
		StringBuffer currentUrl = new StringBuffer(request.getRequestURL());
		if (StringUtils.isNotEmpty(request.getQueryString())) {
			currentUrl.append("?").append(request.getQueryString());
		}
		
		request.setAttribute("currentUrl", currentUrl);
		request.setAttribute("ctx", request.getContextPath());
		request.setAttribute("encodedUrl", new String(Base64.encodeBase64(currentUrl.toString().getBytes())));
		
		// 도메인 설정
		//String requestDomain = request.getServerName();
		String domain = new URL(request.getRequestURL().toString()).getHost();
		
		request.setAttribute("domain", "http://" + domain);
		
		logger.info("domain : " + domain);
		logger.info("request url : " + request.getRequestURL());
		logger.info("request & current url : " + currentUrl);
		logger.info("request client address : " + request.getRemoteAddr());
		
		chain.doFilter(req, res);
	}

	public void destroy() {
		logger.info("request filter destroyed..");
	}
}
