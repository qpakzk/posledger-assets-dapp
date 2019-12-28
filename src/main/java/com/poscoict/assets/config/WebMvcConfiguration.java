package com.poscoict.assets.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.poscoict.assets.common.FileDownloadView;
import com.poscoict.assets.web.DownloadView;
import com.poscoict.assets.web.interceptor.LoginInterceptor;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = {"com.poscoict.assets.web.controller"})
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
	
	/**
	 * JSP View Resolver 설정
	 */
	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
		internalResourceViewResolver.setViewClass(JstlView.class);
		internalResourceViewResolver.setPrefix("/WEB-INF/views/");
		internalResourceViewResolver.setSuffix(".jsp");
		internalResourceViewResolver.setOrder(1);
		return internalResourceViewResolver;
	}
	
	@Bean
	public ViewResolver beanNameViewResolver() {
		BeanNameViewResolver beanNameViewResolver = new BeanNameViewResolver();
		beanNameViewResolver.setOrder(0);
		return beanNameViewResolver;
	}
	
	@Bean
	public LoginInterceptor loginInterceptor() {
		return new LoginInterceptor();
	}
	
	@Bean("downloadView")
	public DownloadView downloadView() {
		return new DownloadView();
	}
	
	@Bean("fileDownloadView")
	public FileDownloadView fileDownloadView() {
		return new FileDownloadView();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginInterceptor())
		.addPathPatterns("/**/*")
		.excludePathPatterns("/js/**")
		.excludePathPatterns("/css/**")
		.excludePathPatterns("/fonts/**")
		.excludePathPatterns("/layout/**")
		.excludePathPatterns("/images/**")
		.excludePathPatterns("/**/*.png")
		.excludePathPatterns("/*.pdf")
		.excludePathPatterns("/index")
		.excludePathPatterns("/")
		.excludePathPatterns("/member/register/action");
	}
	
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		// Serving static files using the Servlet container's default Servlet.
		configurer.enable();
	}
}