/*===============================================
 *Copyright(c) 2014 POSCO/POSDATA
 *
 *@ProcessChain   : FEMS
 *@File           : GenericException.java
 *@FileName       : GenericException
 *
 *Change history
 *@LastModifier : 이재만
 *@수정 날짜; SCR_NO; 수정자; 수정내용
 * 2014-04-10; 이재만; Initial Version
 ===============================================*/

package com.poscoict.assets.exception;

/**
 * 일반적인 상황에서 발생하는 exception
 * @author 이재만
 */
public class GenericException extends RuntimeException {

	private static final long serialVersionUID = 4806747337714843894L;

	private String errorCode = "generic_exception";

	public GenericException() {
		this.setErrorCode(this.errorCode);
	}

	public GenericException(String s) {
		super(s);
	}

	public GenericException(String code, String message) {
		super(message);
		this.setErrorCode(code);
	}
	
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return super.getMessage();
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getLocalizedMessage()
	 */
	@Override
	public String getLocalizedMessage() {
		return super.getLocalizedMessage();
	}
}
