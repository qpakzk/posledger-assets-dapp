package com.poscoict.assets.persistence;

import org.springframework.dao.DataAccessException;

import com.poscoict.assets.model.UserVo;

public interface backup_UserDao {

	/**
	 * 사용자 조회
	 * @param certAddress
	 * @return UserVo
	 * @throws DataAccessException
	 */
	public UserVo getUserByCertAddress(String certAddress) throws DataAccessException;
	
	/**
	 * 사용자 등록
	 * @return void
	 * @throws DataAccessException
	 */
	public void insertUser(UserVo user) throws DataAccessException;

}