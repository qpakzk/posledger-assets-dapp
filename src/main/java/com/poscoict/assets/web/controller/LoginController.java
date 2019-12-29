package com.poscoict.assets.web.controller;

import javax.servlet.http.HttpServletRequest;

import com.poscoict.assets.chaincode.EERC721;
import com.poscoict.assets.chaincode.ERC721;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poscoict.assets.exception.RestResourceException;
import com.poscoict.assets.model.UserVo;
import com.poscoict.assets.service.UserService;
import com.poscoict.assets.web.ExceptionHandleController;
import com.poscoict.assets.web.HttpResponse;
import com.poscoict.posledger.chain.DateUtil;
import com.poscoict.posledger.chain.sign.certificate.PosCertificateService;
import com.poscoict.posledger.chain.sign.model.PosCertificate;
import com.poscoict.posledger.chain.sign.model.PosCertificateMeta;
import com.poscoict.posledger.chain.wallet.chaincode.ERC20ChaincodeService;

import java.io.FileInputStream;
import java.math.BigInteger;

@Controller
public class LoginController extends ExceptionHandleController {

	private static final Logger logger = LogManager.getLogger(LoginController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private PosCertificateService posCertificateService;

	@Autowired
	private ERC20ChaincodeService erc20ChaincodeService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MessageSourceAccessor message;

	@Autowired
	ERC721 erc721;

	@Autowired
	EERC721 eerc721;

	private String alice;
	private String bob;
	private String carol;
	private String david;
	private final BigInteger tokenId = BigInteger.ZERO;
	private final static String CERT_PASSWARD = "1234";

	/**
	 * 회원 가입
	 *
	 * @return HttpResponse
	 * @throws RestResourceException
	 */
	@RequestMapping(value = "/member/register/action", method = RequestMethod.POST)
	@ResponseBody
	public HttpResponse joinUser(@RequestParam(value = "pushToken", required = false) String pushToken,
								 @RequestParam(value = "userType", required = false) String userType,
								 @RequestParam(value = "epPassword", required = false) String epPassword,
								 @RequestParam(value = "certiPassword", required = false) String certiPassword,
								 @RequestParam("certfile") MultipartFile certfile, HttpServletRequest request) throws RestResourceException {

		if (certfile.isEmpty()) {
			throw new RestResourceException("첨부 인증서 정보가 없습니다.");
		}

		PosCertificate posCertificate = null;

		try {
			posCertificate = (PosCertificate) objectMapper.readValue(certfile.getBytes(), new TypeReference<PosCertificate>() {
			});
		} catch (Exception e) {
			logger.error(e);
			throw new RestResourceException("유효하지 않은 인증서 형식입니다.");
		}

		// 인증서 비밀번호 검증
		boolean result = false;

		try {
			result = posCertificateService.verifyPosCertificatePassword(posCertificate, certiPassword);
		} catch (Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMeta = new PosCertificateMeta();
		;

		if (result) {

			try {
				posCertificateMeta = posCertificateService.getMobilePosCertificateMeta(posCertificate, certiPassword, message.getMessage("application.posledger.challenge.domain"));
			} catch (Exception e) {
				logger.error(e);
				throw new RestResourceException(e.getLocalizedMessage());
			}

			if (posCertificateMeta == null) {
				throw new RestResourceException("블록체인에 저장된 인증서 정보가 없습니다.");
			}

			try {
				UserVo certUser = userService.getUserByCertAddress(posCertificate.getAddress());

				if (certUser != null) throw new RestResourceException("이미 등록된 인증서입니다.");

				//wallet create 체인코드 실행
				boolean createWallet = erc20ChaincodeService.createWallet(posCertificateMeta);

				if (createWallet == true) {
					UserVo user = new UserVo();

					user.setUserId(posCertificateMeta.getOwnerId());
					user.setOrgCode(posCertificateMeta.getOrgCode());
					user.setUserType(posCertificateMeta.getOwnerType());
					user.setCertAddress(posCertificate.getAddress());
					user.setDeviceAddress(posCertificateMeta.getDevices());
					user.setPushToken(pushToken);
					user.setRegistDate(DateUtil.getDateObject());

					userService.createUser(user);

					// 사용자 세션 저장
					request.getSession().setAttribute("joinUserId", posCertificateMeta.getOwnerId());
					request.getSession().setAttribute("joinUserOrgCode", posCertificateMeta.getOrgCode());

				} else {
					throw new RestResourceException("블록체인에 정상적으로 저장되지 않았습니다.");
				}

			} catch (Exception e) {
				logger.error(e);
				throw new RestResourceException(e.getLocalizedMessage());
			}
		}

		return new HttpResponse(HttpResponse.success, posCertificateMeta.getOwnerId());

	}

	@RequestMapping(value = "/erc721/mint", method = RequestMethod.POST)
	@ResponseBody
	public HttpResponse mint(@RequestParam(value = "pushToken", required = false) String pushToken,
							 @RequestParam(value = "userType", required = false) String userType,
							 @RequestParam(value = "epPassword", required = false) String epPassword,
							 @RequestParam(value = "certiPassword", required = false) String certiPassword,
							 @RequestParam("certfile") MultipartFile certfile, HttpServletRequest request) throws RestResourceException, Exception {

		//String fileName = "./certForAlice";
		//MultipartFile certfile = new MockMultipartFile(fileName, new FileInputStream(fileName));
/*
		if (certfile.isEmpty()) {
			throw new RestResourceException("첨부 인증서 정보가 없습니다.");
		}

		PosCertificate posCertificate = null;
		try {
			posCertificate = objectMapper.readValue(certfile.getBytes(), new TypeReference<PosCertificate>(){});
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("유효하지 않은 인증서 형식입니다.");
		}

		// 인증서 비밀번호 검증
		boolean isPassward = false;

		try {
			isPassward = posCertificateService.verifyPosCertificatePassword(posCertificate, CERT_PASSWARD);
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMeta = null;

		if (isPassward) {
			try {
				posCertificateMeta = posCertificateService.getMobilePosCertificateMeta(posCertificate, CERT_PASSWARD, message.getMessage("application.posledger.challenge.domain"));
			} catch (Exception e) {
				logger.error(e);
				throw new RestResourceException(e.getLocalizedMessage());
			}
		}

		try {
			alice = posCertificateMeta.getOwnerKey();
		} catch (NullPointerException e) {
			logger.error(e);
			throw new NullPointerException(e.getLocalizedMessage());
		}

		//erc721.setCaller(alice);
		//boolean result = erc721.mint(tokenId, alice);

		return new HttpResponse(HttpResponse.success, "SUCCESS");
	}
	*/

		if (certfile.isEmpty()) {
			throw new RestResourceException("첨부 인증서 정보가 없습니다.");
		}

		PosCertificate posCertificate = null;

		try {
			posCertificate = (PosCertificate) objectMapper.readValue(certfile.getBytes(), new TypeReference<PosCertificate>() {
			});
		} catch (Exception e) {
			logger.error(e);
			throw new RestResourceException("유효하지 않은 인증서 형식입니다.");
		}

		// 인증서 비밀번호 검증
		boolean result = false;

		try {
			result = posCertificateService.verifyPosCertificatePassword(posCertificate, certiPassword);
		} catch (Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMeta = new PosCertificateMeta();
		;

		if (result) {

			try {
				posCertificateMeta = posCertificateService.getMobilePosCertificateMeta(posCertificate, certiPassword, message.getMessage("application.posledger.challenge.domain"));
			} catch (Exception e) {
				logger.error(e);
				throw new RestResourceException(e.getLocalizedMessage());
			}

			if (posCertificateMeta == null) {
				throw new RestResourceException("블록체인에 저장된 인증서 정보가 없습니다.");
			}

			try {
				UserVo certUser = userService.getUserByCertAddress(posCertificate.getAddress());

				if (certUser != null) throw new RestResourceException("이미 등록된 인증서입니다.");

				//wallet create 체인코드 실행
				boolean createWallet = erc20ChaincodeService.createWallet(posCertificateMeta);

				if (createWallet == true) {
					UserVo user = new UserVo();

					user.setUserId(posCertificateMeta.getOwnerId());
					user.setOrgCode(posCertificateMeta.getOrgCode());
					user.setUserType(posCertificateMeta.getOwnerType());
					user.setCertAddress(posCertificate.getAddress());
					user.setDeviceAddress(posCertificateMeta.getDevices());
					user.setPushToken(pushToken);
					user.setRegistDate(DateUtil.getDateObject());

					userService.createUser(user);

					// 사용자 세션 저장
					request.getSession().setAttribute("joinUserId", posCertificateMeta.getOwnerId());
					request.getSession().setAttribute("joinUserOrgCode", posCertificateMeta.getOrgCode());

				} else {
					throw new RestResourceException("블록체인에 정상적으로 저장되지 않았습니다.");
				}

			} catch (Exception e) {
				logger.error(e);
				throw new RestResourceException(e.getLocalizedMessage());
			}
		}

		return new HttpResponse(HttpResponse.success, posCertificateMeta.getOwnerId());

	}
}