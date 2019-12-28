package com.poscoict.assets.model;

import java.util.Date;

public class UserVo implements java.io.Serializable {

	private static final long serialVersionUID = -4660425638334687548L;
	
	public static final String sessionKey = "sessionUser";
		
	/**
	 * 사용자 ID
	 */
	private String userId;
	
	/**
	 * 조직 코드
	 */
	private String orgCode;
	
	/**
	 * 사용자유형
	 */
	private String userType;
	
	/**
	 * 인증서 주소
	 */
	private String certAddress;
	
	/**
	 * 모바일 주소
	 */
	private String deviceAddress;
	
	/**
	 * 푸쉬 토큰
	 */
	private String pushToken;
	
	/**
	 * 등록일시
	 */
	private Date registDate;
	
	/**
	 * 변경일시
	 */
	private Date modifyDate;
	
	/**
	 * 최종로그인일시
	 */
	private Date lastLoginDate;

	/**
	 * 사용자 이름
	 */
	private String userName;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getCertAddress() {
		return certAddress;
	}

	public void setCertAddress(String certAddress) {
		this.certAddress = certAddress;
	}

	public String getDeviceAddress() {
		return deviceAddress;
	}

	public void setDeviceAddress(String deviceAddress) {
		this.deviceAddress = deviceAddress;
	}

	public String getPushToken() {
		return pushToken;
	}

	public void setPushToken(String pushToken) {
		this.pushToken = pushToken;
	}

	public Date getRegistDate() {
		return registDate;
	}

	public void setRegistDate(Date registDate) {
		this.registDate = registDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserVo [userId=");
		builder.append(userId);
		builder.append(", orgCode=");
		builder.append(orgCode);
		builder.append(", userType=");
		builder.append(userType);
		builder.append(", certAddress=");
		builder.append(certAddress);
		builder.append(", deviceAddress=");
		builder.append(deviceAddress);
		builder.append(", pushToken=");
		builder.append(pushToken);
		builder.append(", registDate=");
		builder.append(registDate);
		builder.append(", modifyDate=");
		builder.append(modifyDate);
		builder.append(", lastLoginDate=");
		builder.append(lastLoginDate);
		builder.append(", userName=");
		builder.append(userName);
		builder.append("]");
		return builder.toString();
	}

}
