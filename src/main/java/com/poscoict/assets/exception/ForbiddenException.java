package com.poscoict.assets.exception;

/**
 * 세션 만료 상황에서 발생하는 exception
 */
public class ForbiddenException extends RuntimeException {

	private static final long serialVersionUID = 4806747337714843894L;
	
	public final static String ERROR_CODE = "403";
	
	public ForbiddenException() {
		super(ERROR_CODE);
	}
	
	public ForbiddenException(String s) {
		super(s);
	}
	
	public String getErrorCode() {
		return ERROR_CODE;
	}

}