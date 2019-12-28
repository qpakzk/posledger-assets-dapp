package com.poscoict.assets.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.ModelAndView;

import com.poscoict.assets.exception.CertificateNotExistException;
import com.poscoict.assets.exception.ForbiddenException;
import com.poscoict.assets.exception.ParameterRequiredException;
import com.poscoict.assets.exception.RestResourceException;

public class ExceptionHandleController {

	private static final Logger logger = LogManager.getLogger(ExceptionHandleController.class);
	
	private String exceptionViewName = "common/exception/exception";
	
	//private String authViewName = "common/exception/authority";

	/**
	 * 서버 에러 처리 핸들러
	 * 
	 * @return String
	 */
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ModelAndView internalServerErrorHandler(Exception e) {
		
		ModelAndView model = new ModelAndView(exceptionViewName);
		model.addObject("exception", e.getLocalizedMessage());
		
		StackTraceElement[] element = e.getStackTrace();
		for (int i = 0; i < element.length; i++) {
			logger.error(element[i]);
		}
		
		return model;
	}
	
	/**
	 * 서버 에러 처리 핸들러
	 * 
	 * @return String
	 */
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(RuntimeException.class)
    public ModelAndView runtimeErrorHandler(RuntimeException e) {
		
		ModelAndView model = new ModelAndView(exceptionViewName);
		model.addObject("exception", e.getLocalizedMessage());
		
		StackTraceElement[] element = e.getStackTrace();
		for (int i = 0; i < element.length; i++) {
			logger.error(element[i]);
		}
		return model;
    }
	
	/**
	 * 서버 에러 처리 핸들러
	 * 
	 * @return String
	 */
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(RestClientException.class)
    public ModelAndView restClientErrorHandler(RestClientException e, HttpServletRequest request) {
		logger.error(e);
		
		if (e.getLocalizedMessage().indexOf("Request Access Token is empty, maybe your token is invalid.")>0) {
			// 세션 삭제
			request.getSession().invalidate();
		}
		
		ModelAndView model = new ModelAndView(exceptionViewName);
		model.addObject("exception", e.getLocalizedMessage());
		return model;
    }
	
	/**
	 * 서버 에러 처리 핸들러
	 * 
	 * @return String
	 */
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(MissingServletRequestParameterException.class)
    public ModelAndView missingParameterErrorHandler(MissingServletRequestParameterException e) {
		logger.error(e);
		ModelAndView model = new ModelAndView(exceptionViewName);
		model.addObject("exception", e.getLocalizedMessage());
		return model;
    }
	
	/**
	 * 서버 에러 처리 핸들러
	 * 
	 * @return String
	 */
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(CertificateNotExistException.class)
    public ModelAndView certificateNotExistErrorHandler(CertificateNotExistException e) {
		logger.error(e);
		ModelAndView model = new ModelAndView(exceptionViewName);
		model.addObject("exception", e.getLocalizedMessage());
		return model;
    }
	
	/**
	 * 서버 에러 처리 핸들러
	 * 
	 * @return String
	 */
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(RestResourceException.class)
	@ResponseBody
    public HttpResponse restResourceErrorHandler(RestResourceException e) {
		logger.error(e);
		StackTraceElement[] element = e.getStackTrace();
		for (int i = 0; i < element.length; i++) {
			logger.error(element[i]);
		}
        return new HttpResponse(HttpResponse.fail, e.getLocalizedMessage());
    }
	
	/**
	 * 서버 에러 처리 핸들러
	 * 
	 * @return String
	 */
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(ParameterRequiredException.class)
    public HttpResponse missingParameterErrorHandler(ParameterRequiredException e) {
		logger.error(e);
		StackTraceElement[] element = e.getStackTrace();
		for (int i = 0; i < element.length; i++) {
			logger.error(element[i]);
		}
        return new HttpResponse(HttpResponse.fail, e.getLocalizedMessage());
    }
	
	/**
	* Forbiddedn 에러 처리 핸들러
	*
	* @return HttpResponse
	*/
	@ResponseStatus(value=HttpStatus.FORBIDDEN)
	@ExceptionHandler(ForbiddenException.class)
	@ResponseBody
	public HttpResponse forbiddenErrorHandler(ForbiddenException e) {
		logger.error(e);
		StackTraceElement[] element = e.getStackTrace();
		for (int i = 0; i < element.length; i++) {
			logger.error(element[i]);
		}
		return new HttpResponse(HttpResponse.fail_403, e.getLocalizedMessage());
	}
}
