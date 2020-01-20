package com.poscoict.assets.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poscoict.assets.exception.RestResourceException;
import com.poscoict.assets.model.UserVo;
import com.poscoict.assets.persistence.*;
import com.poscoict.assets.service.UserService;
import com.poscoict.posledger.chain.DateUtil;
import com.poscoict.posledger.chain.assets.chaincode.extension.EERC721;
import com.poscoict.posledger.chain.assets.chaincode.extension.XNFT;
import com.poscoict.posledger.chain.assets.chaincode.standard.BaseNFT;
import com.poscoict.posledger.chain.assets.chaincode.standard.ERC721;
import com.poscoict.posledger.chain.sign.certificate.PosCertificateService;
import com.poscoict.posledger.chain.sign.model.PosCertificate;
import com.poscoict.posledger.chain.sign.model.PosCertificateMeta;
import com.poscoict.posledger.chain.wallet.chaincode.ERC20ChaincodeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AdminController {

    private static final Logger logger = LogManager.getLogger(SignatureServiceController.class);

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
    private ERC721 erc721;

    @Autowired
    private BaseNFT baseNFT;

    @Autowired
    private EERC721 eerc721;

    @Autowired
    private XNFT xnft;

    public static JdbcTemplate jdbcTemplate;

    @Autowired
    private UserDao userDao;
    @Autowired
    private SigDao sigDao;
    @Autowired
    private DocDao docDao;
    @Autowired
    private UserSigDao user_sigDao;
    @Autowired
    private UserDocDao user_docDao;
    @Autowired
    private TokenDao tokenDao;
//	@Autowired
//	public MerkleTree merkleTree;

    private String chaincodeId = "assetscc0";

    private int tokenId = 1;

    @PostMapping("/oauth/token")
    public RedirectView token(HttpServletRequest req, MultipartHttpServletRequest mre) throws Exception {

        String certiPassword = req.getParameter("certiPassword");
        MultipartFile certfile = mre.getFile("certfile");


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
                UserVo user = new UserVo();

                user.setUserId(posCertificateMeta.getOwnerId());
                user.setOrgCode(posCertificateMeta.getOrgCode());
                user.setUserType(posCertificateMeta.getOwnerType());
                user.setCertAddress(posCertificate.getAddress());
                user.setDeviceAddress(posCertificateMeta.getDevices());
                user.setPushToken("");
                user.setRegistDate(DateUtil.getDateObject());

                //userService.createUser(user);

                // 사용자 세션 저장
                req.getSession().setAttribute("sessionUser", posCertificateMeta.getOwnerKey());
                req.getSession().setAttribute("joinUserId", posCertificateMeta.getOwnerId());
                req.getSession().setAttribute("joinUserOrgCode", posCertificateMeta.getOrgCode());

                if(posCertificateMeta.getOwnerId().equals("ADMIN")) {
                    return new RedirectView("/admin");
                }

                SqlRowSet srs = null;
                srs = userDao.getUserByUserId(posCertificateMeta.getOwnerId());
                if(!srs.next())
                    userDao.insert(posCertificateMeta.getOwnerKey(), posCertificateMeta.getOwnerId());
                //user_sigDao.insert("1FbLcUY39EmYSjtxjpHSVKEeUZQNKAvooa", 1);

            } catch (Exception e) {
                logger.error(e);
                throw new RestResourceException(e.getLocalizedMessage());
            }
        }

        return new RedirectView("/main");
    }

    @GetMapping("/tokenTypesOf")
    public String tokenTypesOf() {

        logger.info("tokenTypesOf ####################");
        return "tokenTypesOf";
    }

    @GetMapping("/updateTokenType")
    public String updateTokenType() {

        logger.info("updateTokenType ####################");
        return "updateTokenType";
    }

    @GetMapping("/retrieveTokenType")
    public String retrieveTokenType() {

        logger.info("retrieveTokenType ####################");
        return "retrieveTokenType";
    }

    @GetMapping("/enrollAttributeOfTokenType")
    public String enrollAttributeOfTokenType() {

        logger.info("enrollAttributeOfTokenType ####################");
        return "enrollAttributeOfTokenType";
    }

    @GetMapping("/updateAttributeOfTokenType")
    public String updateAttributeOfTokenType() {

        logger.info("updateAttributeOfTokenType ####################");
        return "updateAttributeOfTokenType";
    }

    @GetMapping("/retrieveAttributeOfTokenType")
    public String retrieveAttributeOfTokenType() {

        logger.info("retrieveAttributeOfTokenType ####################");
        return "retrieveAttributeOfTokenType";
    }

    @GetMapping("/dropAttributeTokenType")
    public String dropAttributeTokenType() {

        logger.info("dropAttributeTokenType ####################");
        return "dropAttributeTokenType";
    }

    @GetMapping("/dropTokenType")
    public String dropTokenType() {

        logger.info("dropTokenType ####################");
        return "dropTokenType";
    }


}
