package com.poscoict.assets.persistence.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.poscoict.assets.model.UserVo;
import com.poscoict.assets.persistence.UserDao;

@Repository
public class UserDaoImpl implements UserDao  {
	
	@Autowired
	private SqlSession sqlSession;

	/* (non-Javadoc)
	 * @see com.poscoict.wavllet.persistence.UserDao#getUserByCertAddress(java.lang.String)
	 */
	@Override
	public UserVo getUserByCertAddress(String certAddress) throws DataAccessException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("certAddress", certAddress);
		return sqlSession.selectOne("userMapper.getUserByCertAddress", map);
	}
	
	/* (non-Javadoc)
	 * @see com.poscoict.wallet.persistence.UserDao#insertUser(com.poscoict.wallet.model.UserVo)
	 */
	@Override
	public void insertUser(UserVo user) throws DataAccessException {
		sqlSession.insert("userMapper.insertUser", user);
	}

}