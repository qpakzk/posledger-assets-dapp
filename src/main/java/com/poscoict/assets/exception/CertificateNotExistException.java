package com.poscoict.assets.exception;

/**
 * @author JM
 */
public class CertificateNotExistException extends RuntimeException {

	private static final long serialVersionUID = 4806747337714843894L;

	public final static String ERROR_CODE = "certificate_not_exist";
	
	private String model;

	public CertificateNotExistException() {
		super(ERROR_CODE);
	}

	public CertificateNotExistException(String s) {
		super(s);
	}
	
	public CertificateNotExistException(String s, String model) {
		super(s);
		this.setModel(model);
	}

	public CertificateNotExistException(Throwable t) {
		super(t);
	}

	public CertificateNotExistException(String s, Throwable t) {
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
