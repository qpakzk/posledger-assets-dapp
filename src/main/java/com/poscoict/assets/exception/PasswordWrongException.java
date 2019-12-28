package com.poscoict.assets.exception;

/**
 * exception define
 * @author JM
 */
public class PasswordWrongException extends RuntimeException {

	private static final long serialVersionUID = -2188844262349676086L;

	public final static String ERROR_CODE = "user_password_wrong";
	
	private int loginFailCount = 0;

	public PasswordWrongException() {
		super(ERROR_CODE);
	}

	public PasswordWrongException(String s) {
		super(s);
	}
	
	public PasswordWrongException(String s, int loginFailCount) {
		super(s);
		this.setLoginFailCount(loginFailCount);
	}

	public PasswordWrongException(Throwable t) {
		super(t);
	}

	public PasswordWrongException(String s, Throwable t) {
		super(s, t);
	}

	public String getErrorCode() {
		return ERROR_CODE;
	}

	public int getLoginFailCount() {
		return loginFailCount;
	}

	/**
	 * @param loginFailCount the loginFailCount to set
	 */
	public void setLoginFailCount(int loginFailCount) {
		this.loginFailCount = loginFailCount;
	}
}
