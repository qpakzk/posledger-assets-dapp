package com.poscoict.assets.exception;

/**
 * @author JM
 */
public class RestResourceException extends RuntimeException {

	private static final long serialVersionUID = 4806747337714843894L;

	public final static String ERROR_CODE = "resource_error";
	
	private String model;

	public RestResourceException() {
		super(ERROR_CODE);
	}

	public RestResourceException(String s) {
		super(s);
	}
	
	public RestResourceException(String s, String model) {
		super(s);
		this.setModel(model);
	}

	public RestResourceException(Throwable t) {
		super(t);
	}

	public RestResourceException(String s, Throwable t) {
		super(s, t);
	}

	public String getErrorCode() {
		return ERROR_CODE;
	}
	
	public String getModel() {
		return this.model;
	}
	
	public void setModel(String model) {
		this.model = model;
	}

	public void setLoginFailCount(String model) {
		this.model = model;
	}
}
