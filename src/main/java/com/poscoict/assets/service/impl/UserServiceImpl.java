package com.poscoict.assets.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.poscoict.assets.exception.GenericException;
import com.poscoict.assets.model.UserVo;
import com.poscoict.assets.persistence.UserDao;
import com.poscoict.assets.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
	
	@Autowired
	private UserDao userDao;

	/* (non-Javadoc)
	 * @see com.poscoict.wallet.service.UserService#getUserByCertAddress(java.lang.String)
	 */
	@Override
	public UserVo getUserByCertAddress(String certAddress) throws GenericException {
		UserVo user = null;
		try {
			user = userDao.getUserByCertAddress(certAddress);
		} catch(GenericException e) {
			logger.error(e);
			throw new GenericException(e.getLocalizedMessage());
		}
		return user;
	}
	
	/* (non-Javadoc)
	 * @see com.poscoict.wallet.service.UserService#createUser(com.poscoict.wallet.model.UserVo)
	 */
	@Override
	@Transactional(readOnly=false, isolation=Isolation.READ_UNCOMMITTED, propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void createUser(UserVo user) throws GenericException {
		try {
			userDao.insertUser(user);
		} catch(GenericException e) {
			logger.error(e);
			throw new GenericException(e.getLocalizedMessage());
		}
	}
}
