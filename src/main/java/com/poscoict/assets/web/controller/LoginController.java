package com.poscoict.assets.web.controller;

import javax.servlet.http.HttpServletRequest;

import com.poscoict.assets.chaincode.EERC721;
import com.poscoict.assets.chaincode.ERC721;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

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
	//private final BigInteger tokenId = BigInteger.ZERO;
	private final static String CERT_PASSWARD = "1234";

	//private final BigInteger tokenIdForEERC721 = BigInteger.ONE;
	//private String type = "doc";
	//private int pages = 100;
	private String hash = "c35b21d6ca39aa7cc3b79a705d989f1a6e88b99ab43988d74048799e3db926a3";
	private String signers;
	private String path = "https://www.off-chain-storage.com";
	private String merkleroot = "558ad18828f6da6d471cdb1a3443f039a770e03617f163896980d914d643e4bc";

	//private BigInteger[] newtokenIdForEERC721s = { BigInteger.valueOf(2), BigInteger.valueOf(3) };
	//private String values[] = {"40", "60"};



    @RequestMapping(value = "/index", method = RequestMethod.GET)
	@ResponseBody
	public String index() {

		logger.info("index #############################");
		//return new HttpResponse(HttpResponse.success, "SUCCESS");
		return "SUCCESS";
    }


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


	/*
	 *
	 *
	 * ERC721
	 * mint, balanceOf, ownerOf, transferFrom, approve, getApproved, setApprovalForAll, isApprovedForAll
	 *
	 */
	@RequestMapping(value = "/erc721/mint", method = RequestMethod.POST)
	@ResponseBody
	public HttpResponse mint(@RequestParam(value = "pushToken", required = false) String pushToken,
							 @RequestParam(value = "userType", required = false) String userType,
							 @RequestParam(value = "epPassword", required = false) String epPassword,
							 @RequestParam(value = "certiPassword", required = false) String certiPassword,
							 @RequestParam("certfile") MultipartFile certfile, HttpServletRequest request,
							 @RequestParam(value = "tokenId", required = false) BigInteger tokenId) throws RestResourceException, Exception {


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
			isPassward = posCertificateService.verifyPosCertificatePassword(posCertificate, certiPassword);
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMeta = null;

		if (isPassward) {
			try {
				posCertificateMeta = posCertificateService.getMobilePosCertificateMeta(posCertificate, certiPassword, message.getMessage("application.posledger.challenge.domain"));
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

		erc721.setCaller(alice);
		boolean result = erc721.mint(tokenId, alice);

		return new HttpResponse(HttpResponse.success, String.valueOf(result));
	}

	@RequestMapping(value = "/erc721/balanceOf", method = RequestMethod.POST)
	@ResponseBody
	public HttpResponse balanceOf(@RequestParam(value = "pushToken", required = false) String pushToken,
							 @RequestParam(value = "userType", required = false) String userType,
							 @RequestParam(value = "epPassword", required = false) String epPassword,
							 @RequestParam(value = "certiPassword", required = false) String certiPassword,
							 @RequestParam("certfile") MultipartFile certfile, HttpServletRequest request) throws RestResourceException, Exception {


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
			isPassward = posCertificateService.verifyPosCertificatePassword(posCertificate, certiPassword);
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMeta = null;

		if (isPassward) {
			try {
				posCertificateMeta = posCertificateService.getMobilePosCertificateMeta(posCertificate, certiPassword, message.getMessage("application.posledger.challenge.domain"));
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

		BigInteger balance = erc721.balanceOf(alice);
		return new HttpResponse(HttpResponse.success, String.valueOf(balance));
	}

	@RequestMapping(value = "/erc721/ownerOf", method = RequestMethod.POST)
	@ResponseBody
	public HttpResponse ownerOf(@RequestParam(value = "pushToken", required = false) String pushToken,
							 @RequestParam(value = "userType", required = false) String userType,
							 @RequestParam(value = "epPassword", required = false) String epPassword,
							 @RequestParam(value = "certiPassword", required = false) String certiPassword,
							 @RequestParam("certfile") MultipartFile certfile, HttpServletRequest request,
							 @RequestParam(value = "tokenId", required = false) BigInteger tokenId) throws RestResourceException, Exception {


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
			isPassward = posCertificateService.verifyPosCertificatePassword(posCertificate, certiPassword);
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMeta = null;

		if (isPassward) {
			try {
				posCertificateMeta = posCertificateService.getMobilePosCertificateMeta(posCertificate, certiPassword, message.getMessage("application.posledger.challenge.domain"));
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

		String owner = erc721.ownerOf(tokenId);
		return new HttpResponse(HttpResponse.success, owner);
	}

	@RequestMapping(value = "/erc721/transferFrom", method = RequestMethod.POST)
	@ResponseBody
	public HttpResponse transferFrom(@RequestParam(value = "pushToken", required = false) String pushToken,
							 @RequestParam(value = "userType", required = false) String userType,
							 @RequestParam(value = "epPassword", required = false) String epPassword,
							 @RequestParam(value = "certiPassword", required = false) String certiPassword,
							 @RequestParam("certfileForAlice") MultipartFile certfileForAlice, HttpServletRequest requestForAlice,
							 @RequestParam("certfileForBob") MultipartFile certfileForBob, HttpServletRequest requestForBob,
							 @RequestParam(value = "tokenId", required = false) BigInteger tokenId) throws RestResourceException, Exception {


		PosCertificate posCertificateForAlice = null;
		try {
			posCertificateForAlice = objectMapper.readValue(certfileForAlice.getBytes(), new TypeReference<PosCertificate>(){});
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("유효하지 않은 인증서 형식입니다.");
		}

		// 인증서 비밀번호 검증
		boolean isPasswardForAlice = false;

		try {
			isPasswardForAlice = posCertificateService.verifyPosCertificatePassword(posCertificateForAlice, certiPassword);
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMetaForAlice = null;

		if (isPasswardForAlice) {
			try {
				posCertificateMetaForAlice = posCertificateService.getMobilePosCertificateMeta(posCertificateForAlice, certiPassword, message.getMessage("application.posledger.challenge.domain"));
			} catch (Exception e) {
				logger.error(e);
				throw new RestResourceException(e.getLocalizedMessage());
			}
		}

		try {
			alice = posCertificateMetaForAlice.getOwnerKey();
		} catch (NullPointerException e) {
			logger.error(e);
			throw new NullPointerException(e.getLocalizedMessage());
		}


		PosCertificate posCertificateForBob = null;
		try {
			posCertificateForBob = objectMapper.readValue(certfileForBob.getBytes(), new TypeReference<PosCertificate>(){});
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("유효하지 않은 인증서 형식입니다.");
		}

		// 인증서 비밀번호 검증
		boolean isPasswardForBob = false;

		try {
			isPasswardForBob = posCertificateService.verifyPosCertificatePassword(posCertificateForBob, certiPassword);
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMetaForBob = null;

		if (isPasswardForBob) {
			try {
				posCertificateMetaForBob = posCertificateService.getMobilePosCertificateMeta(posCertificateForBob, certiPassword, message.getMessage("application.posledger.challenge.domain"));
			} catch (Exception e) {
				logger.error(e);
				throw new RestResourceException(e.getLocalizedMessage());
			}
		}

		try {
			bob = posCertificateMetaForBob.getOwnerKey();
		} catch (NullPointerException e) {
			logger.error(e);
			throw new NullPointerException(e.getLocalizedMessage());
		}

		erc721.setCaller(alice);
		boolean result = erc721.transferFrom(alice, bob, tokenId);

		return new HttpResponse(HttpResponse.success, String.valueOf(result));
	}

	@RequestMapping(value = "/erc721/approve", method = RequestMethod.POST)
	@ResponseBody
	public HttpResponse approve(@RequestParam(value = "pushToken", required = false) String pushToken,
							 @RequestParam(value = "userType", required = false) String userType,
							 @RequestParam(value = "epPassword", required = false) String epPassword,
							 @RequestParam(value = "certiPassword", required = false) String certiPassword,
							 @RequestParam("certfileForBob") MultipartFile certfileForBob, HttpServletRequest requestForBob,
							 @RequestParam("certfileForCarol") MultipartFile certfileForCarol, HttpServletRequest requestForCarol,
							 @RequestParam(value = "tokenId", required = false) BigInteger tokenId) throws RestResourceException, Exception {


		PosCertificate posCertificateForBob = null;
		try {
			posCertificateForBob = objectMapper.readValue(certfileForBob.getBytes(), new TypeReference<PosCertificate>(){});
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("유효하지 않은 인증서 형식입니다.");
		}

		// 인증서 비밀번호 검증
		boolean isPasswardForBob = false;

		try {
			isPasswardForBob = posCertificateService.verifyPosCertificatePassword(posCertificateForBob, certiPassword);
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMetaForBob = null;

		if (isPasswardForBob) {
			try {
				posCertificateMetaForBob = posCertificateService.getMobilePosCertificateMeta(posCertificateForBob, certiPassword, message.getMessage("application.posledger.challenge.domain"));
			} catch (Exception e) {
				logger.error(e);
				throw new RestResourceException(e.getLocalizedMessage());
			}
		}

		try {
			bob = posCertificateMetaForBob.getOwnerKey();
		} catch (NullPointerException e) {
			logger.error(e);
			throw new NullPointerException(e.getLocalizedMessage());
		}


		PosCertificate posCertificateForCarol = null;
		try {
			posCertificateForCarol = objectMapper.readValue(certfileForCarol.getBytes(), new TypeReference<PosCertificate>(){});
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("유효하지 않은 인증서 형식입니다.");
		}

		// 인증서 비밀번호 검증
		boolean isPasswardForCarol = false;

		try {
			isPasswardForCarol = posCertificateService.verifyPosCertificatePassword(posCertificateForCarol, certiPassword);
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMetaForCarol = null;

		if (isPasswardForCarol) {
			try {
				posCertificateMetaForCarol = posCertificateService.getMobilePosCertificateMeta(posCertificateForCarol, certiPassword, message.getMessage("application.posledger.challenge.domain"));
			} catch (Exception e) {
				logger.error(e);
				throw new RestResourceException(e.getLocalizedMessage());
			}
		}

		try {
			carol = posCertificateMetaForCarol.getOwnerKey();
		} catch (NullPointerException e) {
			logger.error(e);
			throw new NullPointerException(e.getLocalizedMessage());
		}

		erc721.setCaller(bob);
		boolean result = erc721.approve(carol, tokenId);

		return new HttpResponse(HttpResponse.success, String.valueOf(result));
	}

	@RequestMapping(value = "/erc721/setApprovalForAll", method = RequestMethod.POST)
	@ResponseBody
	public HttpResponse setApprovalForAll(@RequestParam(value = "pushToken", required = false) String pushToken,
							 @RequestParam(value = "userType", required = false) String userType,
							 @RequestParam(value = "epPassword", required = false) String epPassword,
							 @RequestParam(value = "certiPassword", required = false) String certiPassword,
						     @RequestParam("certfileForBob") MultipartFile certfileForBob, HttpServletRequest requestForBob,
						     @RequestParam("certfileForDavid") MultipartFile certfileForDavid, HttpServletRequest requestForDavid,
						     @RequestParam(value = "approved", required = false) boolean approved) throws RestResourceException, Exception {


		PosCertificate posCertificateForBob = null;
		try {
			posCertificateForBob = objectMapper.readValue(certfileForBob.getBytes(), new TypeReference<PosCertificate>(){});
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("유효하지 않은 인증서 형식입니다.");
		}

		// 인증서 비밀번호 검증
		boolean isPasswardForBob = false;

		try {
			isPasswardForBob = posCertificateService.verifyPosCertificatePassword(posCertificateForBob, certiPassword);
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMetaForBob = null;

		if (isPasswardForBob) {
			try {
				posCertificateMetaForBob = posCertificateService.getMobilePosCertificateMeta(posCertificateForBob, certiPassword, message.getMessage("application.posledger.challenge.domain"));
			} catch (Exception e) {
				logger.error(e);
				throw new RestResourceException(e.getLocalizedMessage());
			}
		}

		try {
			bob = posCertificateMetaForBob.getOwnerKey();
		} catch (NullPointerException e) {
			logger.error(e);
			throw new NullPointerException(e.getLocalizedMessage());
		}


		PosCertificate posCertificateForDavid = null;
		try {
			posCertificateForDavid = objectMapper.readValue(certfileForDavid.getBytes(), new TypeReference<PosCertificate>(){});
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("유효하지 않은 인증서 형식입니다.");
		}

		// 인증서 비밀번호 검증
		boolean isPasswardForDavid = false;

		try {
			isPasswardForDavid = posCertificateService.verifyPosCertificatePassword(posCertificateForDavid, certiPassword);
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMetaForDavid = null;

		if (isPasswardForDavid) {
			try {
				posCertificateMetaForDavid = posCertificateService.getMobilePosCertificateMeta(posCertificateForDavid, certiPassword, message.getMessage("application.posledger.challenge.domain"));
			} catch (Exception e) {
				logger.error(e);
				throw new RestResourceException(e.getLocalizedMessage());
			}
		}

		try {
			david = posCertificateMetaForDavid.getOwnerKey();
		} catch (NullPointerException e) {
			logger.error(e);
			throw new NullPointerException(e.getLocalizedMessage());
		}

		erc721.setCaller(bob);
		boolean result = erc721.setApprovalForAll(david, approved);

		return new HttpResponse(HttpResponse.success, String.valueOf(result));
	}

	@RequestMapping(value = "/erc721/getApproved", method = RequestMethod.POST)
	@ResponseBody
	public HttpResponse getApproved(@RequestParam(value = "pushToken", required = false) String pushToken,
							 @RequestParam(value = "userType", required = false) String userType,
							 @RequestParam(value = "epPassword", required = false) String epPassword,
							 @RequestParam(value = "certiPassword", required = false) String certiPassword,
							 @RequestParam("certfile") MultipartFile certfile, HttpServletRequest request,
							@RequestParam(value = "tokenId", required = false) BigInteger tokenId) throws RestResourceException, Exception {


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
			isPassward = posCertificateService.verifyPosCertificatePassword(posCertificate, certiPassword);
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMeta = null;

		if (isPassward) {
			try {
				posCertificateMeta = posCertificateService.getMobilePosCertificateMeta(posCertificate, certiPassword, message.getMessage("application.posledger.challenge.domain"));
			} catch (Exception e) {
				logger.error(e);
				throw new RestResourceException(e.getLocalizedMessage());
			}
		}

		try {
			carol = posCertificateMeta.getOwnerKey();
		} catch (NullPointerException e) {
			logger.error(e);
			throw new NullPointerException(e.getLocalizedMessage());
		}

		String result = erc721.getApproved(tokenId);

		return new HttpResponse(HttpResponse.success, result);
	}

	@RequestMapping(value = "/erc721/isApprovedForAll", method = RequestMethod.POST)
	@ResponseBody
	public HttpResponse isApprovedForAll(@RequestParam(value = "pushToken", required = false) String pushToken,
							 @RequestParam(value = "userType", required = false) String userType,
							 @RequestParam(value = "epPassword", required = false) String epPassword,
							 @RequestParam(value = "certiPassword", required = false) String certiPassword,
							 @RequestParam("certfileForBob") MultipartFile certfileForBob, HttpServletRequest requestForBob,
							 @RequestParam("certfileForDavid") MultipartFile certfileForDavid, HttpServletRequest requestForDavid) throws RestResourceException, Exception {


		PosCertificate posCertificateForBob = null;
		try {
			posCertificateForBob = objectMapper.readValue(certfileForBob.getBytes(), new TypeReference<PosCertificate>(){});
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("유효하지 않은 인증서 형식입니다.");
		}

		// 인증서 비밀번호 검증
		boolean isPasswardForBob = false;

		try {
			isPasswardForBob = posCertificateService.verifyPosCertificatePassword(posCertificateForBob, certiPassword);
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMetaForBob = null;

		if (isPasswardForBob) {
			try {
				posCertificateMetaForBob = posCertificateService.getMobilePosCertificateMeta(posCertificateForBob, certiPassword, message.getMessage("application.posledger.challenge.domain"));
			} catch (Exception e) {
				logger.error(e);
				throw new RestResourceException(e.getLocalizedMessage());
			}
		}

		try {
			bob = posCertificateMetaForBob.getOwnerKey();
		} catch (NullPointerException e) {
			logger.error(e);
			throw new NullPointerException(e.getLocalizedMessage());
		}


		PosCertificate posCertificateForDavid = null;
		try {
			posCertificateForDavid = objectMapper.readValue(certfileForDavid.getBytes(), new TypeReference<PosCertificate>(){});
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("유효하지 않은 인증서 형식입니다.");
		}

		// 인증서 비밀번호 검증
		boolean isPasswardForDavid = false;

		try {
			isPasswardForDavid = posCertificateService.verifyPosCertificatePassword(posCertificateForDavid, certiPassword);
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMetaForDavid = null;

		if (isPasswardForDavid) {
			try {
				posCertificateMetaForDavid = posCertificateService.getMobilePosCertificateMeta(posCertificateForDavid, certiPassword, message.getMessage("application.posledger.challenge.domain"));
			} catch (Exception e) {
				logger.error(e);
				throw new RestResourceException(e.getLocalizedMessage());
			}
		}

		try {
			david = posCertificateMetaForDavid.getOwnerKey();
		} catch (NullPointerException e) {
			logger.error(e);
			throw new NullPointerException(e.getLocalizedMessage());
		}

		boolean result = erc721.isApprovedForAll(bob, david);

		return new HttpResponse(HttpResponse.success, String.valueOf(result));
	}


	/*
	 *
	 *
	 * EERC721
	 * mint, balanceOf, divide, update, deactivate, query, queryHistory
	 *
	 */
	@RequestMapping(value = "/eerc721/mint", method = RequestMethod.POST)
	@ResponseBody
	public HttpResponse mintForEERC721(@RequestParam(value = "pushToken", required = false) String pushToken,
							 @RequestParam(value = "userType", required = false) String userType,
							 @RequestParam(value = "epPassword", required = false) String epPassword,
							 @RequestParam(value = "certiPassword", required = false) String certiPassword,
							 @RequestParam("certfile") MultipartFile certfile, HttpServletRequest request,
						     @RequestParam(value = "tokenId", required = false) BigInteger tokenId,
						     @RequestParam(value = "type", required = false) String type,
						     @RequestParam(value = "pages", required = false) int pages) throws RestResourceException, Exception {

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
			isPassward = posCertificateService.verifyPosCertificatePassword(posCertificate, certiPassword);
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMeta = null;

		if (isPassward) {
			try {
				posCertificateMeta = posCertificateService.getMobilePosCertificateMeta(posCertificate, certiPassword, message.getMessage("application.posledger.challenge.domain"));
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

		signers = alice;
		eerc721.setCaller(alice);
		boolean result = eerc721.mint(tokenId, type, alice, pages, hash, signers, path, merkleroot);

		return new HttpResponse(HttpResponse.success, String.valueOf(result));
	}

	@RequestMapping(value = "/eerc721/balanceOf", method = RequestMethod.POST)
	@ResponseBody
	public HttpResponse balanceOfForEERC721(@RequestParam(value = "pushToken", required = false) String pushToken,
								  @RequestParam(value = "userType", required = false) String userType,
								  @RequestParam(value = "epPassword", required = false) String epPassword,
								  @RequestParam(value = "certiPassword", required = false) String certiPassword,
								  @RequestParam("certfile") MultipartFile certfile, HttpServletRequest request,
								  @RequestParam(value = "type", required = false) String type) throws RestResourceException, Exception {

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
			isPassward = posCertificateService.verifyPosCertificatePassword(posCertificate, certiPassword);
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMeta = null;

		if (isPassward) {
			try {
				posCertificateMeta = posCertificateService.getMobilePosCertificateMeta(posCertificate, certiPassword, message.getMessage("application.posledger.challenge.domain"));
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

		BigInteger balance = eerc721.balanceOf(alice, type);
		return new HttpResponse(HttpResponse.success, String.valueOf(balance));
	}

	@RequestMapping(value = "/eerc721/divide", method = RequestMethod.POST)
	@ResponseBody
	public HttpResponse divideForEERC721(@RequestParam(value = "pushToken", required = false) String pushToken,
											@RequestParam(value = "userType", required = false) String userType,
											@RequestParam(value = "epPassword", required = false) String epPassword,
											@RequestParam(value = "certiPassword", required = false) String certiPassword,
											@RequestParam("certfile") MultipartFile certfile, HttpServletRequest request,
										 	@RequestParam(value = "tokenId", required = false) BigInteger tokenId,
										 	@RequestParam(value = "firstNewTokenId", required = false) BigInteger firstNewTokenId,
										 	@RequestParam(value = "secondNewTokenId", required = false) BigInteger secondNewTokenId,
										 	@RequestParam(value = "firstValue", required = false) String firstValue,
										 	@RequestParam(value = "secondValue", required = false) String secondValue) throws RestResourceException, Exception {


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
			isPassward = posCertificateService.verifyPosCertificatePassword(posCertificate, certiPassword);
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMeta = null;

		if (isPassward) {
			try {
				posCertificateMeta = posCertificateService.getMobilePosCertificateMeta(posCertificate, certiPassword, message.getMessage("application.posledger.challenge.domain"));
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

		String index = "pages";
		BigInteger[] newtokenIdForEERC721s = { firstNewTokenId, secondNewTokenId };
		String[] values = { firstValue, secondValue };
		eerc721.setCaller(alice);
		boolean result = eerc721.divide(tokenId, newtokenIdForEERC721s, values, index);

		return new HttpResponse(HttpResponse.success, String.valueOf(result));
	}

	@RequestMapping(value = "/eerc721/query", method = RequestMethod.POST)
	@ResponseBody
	public HttpResponse queryForEERC721(@RequestParam(value = "pushToken", required = false) String pushToken,
											@RequestParam(value = "userType", required = false) String userType,
											@RequestParam(value = "epPassword", required = false) String epPassword,
											@RequestParam(value = "certiPassword", required = false) String certiPassword,
											@RequestParam("certfile") MultipartFile certfile, HttpServletRequest request,
											@RequestParam(value = "tokenId", required = false) BigInteger tokenId) throws RestResourceException, Exception {


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
			isPassward = posCertificateService.verifyPosCertificatePassword(posCertificate, certiPassword);
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMeta = null;

		if (isPassward) {
			try {
				posCertificateMeta = posCertificateService.getMobilePosCertificateMeta(posCertificate, certiPassword, message.getMessage("application.posledger.challenge.domain"));
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

		String result = eerc721.query(tokenId);

		return new HttpResponse(HttpResponse.success, result);
	}

	@RequestMapping(value = "/eerc721/update", method = RequestMethod.POST)
	@ResponseBody
	public HttpResponse updateForEERC721(@RequestParam(value = "pushToken", required = false) String pushToken,
											@RequestParam(value = "userType", required = false) String userType,
											@RequestParam(value = "epPassword", required = false) String epPassword,
											@RequestParam(value = "certiPassword", required = false) String certiPassword,
											@RequestParam("certfile") MultipartFile certfile, HttpServletRequest request,
										 	@RequestParam(value = "tokenId", required = false) BigInteger tokenId,
										 	@RequestParam(value = "index", required = false) String index,
										 	@RequestParam(value = "attr", required = false) String attr) throws RestResourceException, Exception {

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
			isPassward = posCertificateService.verifyPosCertificatePassword(posCertificate, certiPassword);
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMeta = null;

		if (isPassward) {
			try {
				posCertificateMeta = posCertificateService.getMobilePosCertificateMeta(posCertificate, certiPassword, message.getMessage("application.posledger.challenge.domain"));
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

		//String attr = alice +" SigId";
		//String index = "sigIds";

		eerc721.setCaller(alice);
		boolean result = eerc721.update(tokenId, index, attr);

		return new HttpResponse(HttpResponse.success, String.valueOf(result));
	}

	@RequestMapping(value = "/eerc721/deactivate", method = RequestMethod.POST)
	@ResponseBody
	public HttpResponse deactivateForEERC721(@RequestParam(value = "pushToken", required = false) String pushToken,
											@RequestParam(value = "userType", required = false) String userType,
											@RequestParam(value = "epPassword", required = false) String epPassword,
											@RequestParam(value = "certiPassword", required = false) String certiPassword,
											@RequestParam("certfile") MultipartFile certfile, HttpServletRequest request,
										    @RequestParam(value = "tokenId", required = false) BigInteger tokenId) throws RestResourceException, Exception {

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
			isPassward = posCertificateService.verifyPosCertificatePassword(posCertificate, certiPassword);
		} catch(Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMeta = null;

		if (isPassward) {
			try {
				posCertificateMeta = posCertificateService.getMobilePosCertificateMeta(posCertificate, certiPassword, message.getMessage("application.posledger.challenge.domain"));
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

		eerc721.setCaller(alice);
		boolean result = eerc721.deactivate(tokenId);

		return new HttpResponse(HttpResponse.success, String.valueOf(result));
	}

	@RequestMapping(value = "/eerc721/queryHistory", method = RequestMethod.POST)
	@ResponseBody
	public HttpResponse queryHistoryForEERC721(@RequestParam(value = "pushToken", required = false) String pushToken,
											@RequestParam(value = "userType", required = false) String userType,
											@RequestParam(value = "epPassword", required = false) String epPassword,
											@RequestParam(value = "certiPassword", required = false) String certiPassword,
											@RequestParam("certfile") MultipartFile certfile, HttpServletRequest request,
										    @RequestParam(value = "tokenId", required = false) BigInteger tokenId) throws RestResourceException, Exception {


		PosCertificate posCertificate = null;
		try {
			posCertificate = objectMapper.readValue(certfile.getBytes(), new TypeReference<PosCertificate>() {
			});
		} catch (Exception e) {
			logger.error(e);
			throw new RestResourceException("유효하지 않은 인증서 형식입니다.");
		}

		// 인증서 비밀번호 검증
		boolean isPassward = false;

		try {
			isPassward = posCertificateService.verifyPosCertificatePassword(posCertificate, certiPassword);
		} catch (Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMeta = null;

		if (isPassward) {
			try {
				posCertificateMeta = posCertificateService.getMobilePosCertificateMeta(posCertificate, certiPassword, message.getMessage("application.posledger.challenge.domain"));
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

		List<String> histories = eerc721.queryHistory(tokenId);
		String result = "";
		if (histories != null) {
			for (String history : histories) {
				result += history;
			}
		}

		return new HttpResponse(HttpResponse.success, result);
	}

}