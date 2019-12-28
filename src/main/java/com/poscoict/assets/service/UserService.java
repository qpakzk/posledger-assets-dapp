package com.poscoict.assets.service;

import com.poscoict.assets.exception.GenericException;
import com.poscoict.assets.model.UserVo;

/**
 * 사용자 서비스 클래스
 * 
 * @author JM
 */
public interface UserService {
	
	/**
	 * 사용자 조회
	 * @param certAddress
	 * @return UserVo
	 * @throws GenericException
	 */
	public UserVo getUserByCertAddress(String certAddress) throws GenericException;

	/**
	 * 특정 사용자 등록 처리
	 * @return void
	 * @throws GenericException
	 */
	public void createUser(UserVo user) throws GenericException;
	
}
