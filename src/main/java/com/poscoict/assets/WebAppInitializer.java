package com.poscoict.assets;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import com.poscoict.assets.config.SpringConfig;
import com.poscoict.assets.config.WebMvcConfiguration;
import com.poscoict.assets.web.decorator.SiteMeshFilter;
import com.poscoict.assets.web.filter.RequestFilter;

public class WebAppInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) {
		
		initializeSpringConfig(servletContext);

		initializeSpringMVCConfig(servletContext);

		registerListener(servletContext);

		registerFilter(servletContext);
	}

	private void initializeSpringConfig(ServletContext servletContext) {
		
		// Create the 'root' Spring application context
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(SpringConfig.class);
		
		// Manage the life cycle of the root application context
		servletContext.addListener(new ContextLoaderListener(rootContext));
	}

	private void initializeSpringMVCConfig(ServletContext servletContext) {
		
		// Create the spring rest servlet's Spring application context
		AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
		dispatcherContext.register(WebMvcConfiguration.class);

		// Register and map the spring rest servlet
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("mvc", new DispatcherServlet(dispatcherContext));
		dispatcher.setLoadOnStartup(2);
		dispatcher.addMapping("/");
	}

	private void registerFilter(ServletContext servletContext) {
		initializeEncodingFilter(servletContext);
		initializeRequestFilter(servletContext);
		initializeSitemeshFilter(servletContext);
	}

	private void registerListener(ServletContext servletContext) {
		servletContext.addListener(RequestContextListener.class);
	}
	
	private void initializeEncodingFilter(ServletContext servletContext) {
		FilterRegistration.Dynamic filterRegistration = servletContext.addFilter("characterEncodingFilter", CharacterEncodingFilter.class);
		filterRegistration.setInitParameter("encoding", "UTF-8");
		filterRegistration.addMappingForUrlPatterns(null, false, "/*");
		filterRegistration.setAsyncSupported(true);
	}
	
	private void initializeSitemeshFilter(ServletContext servletContext) {
		FilterRegistration.Dynamic filterRegistration = servletContext.addFilter("sitemeshFilter", SiteMeshFilter.class);
		filterRegistration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.FORWARD), false, "/*");
	}

	private void initializeRequestFilter(ServletContext servletContext) {
		FilterRegistration.Dynamic filterRegistration = servletContext.addFilter("requestFilter", RequestFilter.class);
		filterRegistration.addMappingForUrlPatterns(null, false, "/*");
		filterRegistration.setInitParameter("excludePatterns", "/assets/**, /fonts/**, /images/**, /css/**, /js/**, /**/*.js, /**/*.jpg, /**/*.png, /**/*.css");
	}
}