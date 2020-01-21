package com.poscoict.assets.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poscoict.assets.exception.RestResourceException;
import com.poscoict.assets.model.UserVo;
import com.poscoict.assets.persistence.*;
import com.poscoict.assets.service.UserService;
import com.poscoict.posledger.chain.DateUtil;
import com.poscoict.posledger.chain.assets.chaincode.extension.EERC721;
import com.poscoict.posledger.chain.assets.chaincode.extension.XNFT;
import com.poscoict.posledger.chain.assets.chaincode.extension.XType;
import com.poscoict.posledger.chain.assets.chaincode.standard.BaseNFT;
import com.poscoict.posledger.chain.assets.chaincode.standard.ERC721;
import com.poscoict.posledger.chain.assets.chaincode.util.Manager;
import com.poscoict.posledger.chain.sign.certificate.PosCertificateService;
import com.poscoict.posledger.chain.sign.model.PosCertificate;
import com.poscoict.posledger.chain.sign.model.PosCertificateMeta;
import com.poscoict.posledger.chain.wallet.chaincode.ERC20ChaincodeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static java.lang.Integer.parseInt;

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

    @Autowired
    private XType xType;

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
                req.getSession().setAttribute("ownerId", posCertificateMeta.getOwnerId());
                req.getSession().setAttribute("joinUserOrgCode", posCertificateMeta.getOrgCode());

                SqlRowSet srs = null;
                srs = userDao.getUserByUserId(posCertificateMeta.getOwnerId());
                if(!srs.next())
                    userDao.insert(posCertificateMeta.getOwnerKey(), posCertificateMeta.getOwnerId());

                if(posCertificateMeta.getOwnerId().equals("ADMIN")) {
                    return new RedirectView("/admin");
                }
                //user_sigDao.insert("1FbLcUY39EmYSjtxjpHSVKEeUZQNKAvooa", 1);

            } catch (Exception e) {
                logger.error(e);
                throw new RestResourceException(e.getLocalizedMessage());
            }
        }

        return new RedirectView("/main");
    }

    @PostMapping("/enrollTokenType")
    public String enrollTokenType(HttpServletRequest req) throws JsonProcessingException, ProposalException, InvalidArgumentException {

        logger.info("enrollTokenType ####################");

        String ownerKey = req.getParameter("ownerKey");
        String tokenType = req.getParameter("tokenType");
        int xattrCount = parseInt(req.getParameter("xattrCount"));
        //int uriCount = parseInt(req.getParameter("uriCount"));

        String xattrName = "";
        String xattrType = "";
        String xattrValue = "";

        Map<String, List<String>> xattr = new HashMap<>();

        for(int i=0; i<xattrCount; i++) {
            xattrName = req.getParameter("xattrName" + i);
            xattrType = req.getParameter("xattrType" + i);

            if (xattrType.equals("String"))
                xattrValue = "";
            else if (xattrType.equals("[String]"))
                xattrValue = "[String]";
            else if (xattrType.equals("Integer"))
                xattrValue = "0";
            else if (xattrType.equals("Boolean"))
                xattrValue = "";
            else
                return "FAILURE";

            xattr.put(xattrName, new ArrayList<>(Arrays.asList(xattrType, xattrValue)));
        }

        Manager.setChaincodeId(chaincodeId);
        Manager.setCaller(ownerKey);

        boolean result = xType.enrollTokenType(tokenType, xattr);

        return "/admin";
    }

    @GetMapping("/adminTokenTypesOf")
    public String adminTokenTypesOf(HttpServletRequest req) throws InvalidArgumentException, ProposalException {

        return "/adminTokenTypesOf";
    }

    @ResponseBody
    @RequestMapping("/tokenTypesOf")
    public String tokenTypesOf(HttpServletRequest req) throws InvalidArgumentException, ProposalException {

        String result = "";

        Manager.setChaincodeId(chaincodeId);
        List<String> types = xType.tokenTypesOf();

        for(int i=0; i<types.size(); i++) {
            result += "type " + i + " : " + types.get(i) + "\n";
        }

        logger.info("tokenTypesOf ####################");
        return result;
    }

    @GetMapping("/adminUpdateTokenType")
    public String adminUpdateTokenType(HttpServletRequest req) throws InvalidArgumentException, ProposalException {

        return "/adminUpdateTokenType";
    }

    @PostMapping("/updateTokenType")
    public String updateTokenType(HttpServletRequest req) throws JsonProcessingException, ProposalException, InvalidArgumentException {

        String ownerKey = req.getParameter("ownerKey");

        String tokenType = req.getParameter("tokenType");
        int xattrCount = parseInt(req.getParameter("xattrCount"));

        String xattrName = "";
        String xattrType = "";
        String xattrValue = "";

        Map<String, List<String>> attributes = new HashMap<>();

        for(int i=0; i<xattrCount; i++) {
            xattrName = req.getParameter("xattrName" + i);
            xattrType = req.getParameter("xattrType" + i);
            xattrValue = req.getParameter("xattrValue" + i);

            attributes.put(xattrName, new ArrayList<>(Arrays.asList(xattrType, xattrValue)));
        }

        Manager.setChaincodeId(chaincodeId);
        Manager.setCaller(ownerKey);

        boolean result = xType.updateTokenType(tokenType, attributes);
        return "/adminUpdateTokenType";
    }

    @GetMapping("/adminRetrieveTokenType")
    public String adminRetrieveTokenType(HttpServletRequest req) throws InvalidArgumentException, ProposalException {

        return "/adminRetrieveTokenType";
    }

    @ResponseBody
    @RequestMapping("/retrieveTokenType")
    public String retrieveTokenType(HttpServletRequest req) throws ProposalException, IOException, InvalidArgumentException {

        String tokenType = req.getParameter("tokenType");
        String ownerKey = req.getParameter("ownerKey");

        Manager.setChaincodeId(chaincodeId);
        Manager.setCaller(ownerKey);

        Map<String, List<String>> attributes = xType.retrieveTokenType(tokenType);

        logger.info(attributes.toString());
        List<String> keys = new ArrayList<>(attributes.keySet());

        String result = "";
        for(int i = 0; i<keys.size(); i++) {
            result += "Attributes" + i + " : " + keys.get(i) + "\n";
        }

        logger.info("retrieveTokenType ####################");
        return result;
    }

    @GetMapping("/adminEnrollAttributeOfTokenType")
    public String adminEnrollAttributeOfTokenType(HttpServletRequest req) throws InvalidArgumentException, ProposalException {

        return "/adminEnrollAttributeOfTokenType";
    }

    @ResponseBody
    @RequestMapping("/enrollAttributeOfTokenType")
    public String enrollAttributeOfTokenType(HttpServletRequest req) throws InvalidArgumentException, ProposalException {

        logger.info("enrollAttributeOfTokenType ####################");

        String tokenType = req.getParameter("tokenType");
        String ownerKey = req.getParameter("ownerKey");
        String attribute = (req.getParameter("xattrName"));
        String dataType = req.getParameter("xattrType");
        String initialValue = req.getParameter("initialValue");

        logger.info(tokenType + " " + " " + ownerKey + " " + attribute + " " +dataType + " " + initialValue);
        Manager.setChaincodeId(chaincodeId);
        Manager.setCaller(ownerKey);

        boolean result = xType.enrollAttributeOfTokenType(tokenType, attribute, dataType, initialValue);
        if(result == true)
            return "true";
        else
            return "false";
    }

    @GetMapping("/adminUpdateAttributeOfTokenType")
    public String adminUpdateAttributeOfTokenType(HttpServletRequest req) throws InvalidArgumentException, ProposalException {

        return "/adminUpdateAttributeOfTokenType";
    }

    @ResponseBody
    @RequestMapping("/updateAttributeOfTokenType")
    public String updateAttributeOfTokenType(HttpServletRequest req) throws InvalidArgumentException, ProposalException {

        logger.info("updateAttributeOfTokenType ####################");

        String tokenType = req.getParameter("tokenType");
        String ownerKey = req.getParameter("ownerKey");
        String attribute = (req.getParameter("xattrName"));
        String dataType = req.getParameter("xattrType");
        String initialValue = req.getParameter("initialValue");

        Manager.setChaincodeId(chaincodeId);
        Manager.setCaller(ownerKey);

        List<String> pair = new ArrayList<>(Arrays.asList(dataType, initialValue));
        boolean result = xType.updateAttributeOfTokenType(tokenType, attribute, pair);
        if(result == true)
            return "true";
        else
            return "false";
    }

    @GetMapping("/adminRetrieveAttributeOfTokenType")
    public String adminRetrieveAttributeOfTokenType(HttpServletRequest req) throws InvalidArgumentException, ProposalException {

        return "/adminRetrieveAttributeOfTokenType";
    }

    @PostMapping("/retrieveAttributeOfTokenType")
    public String retrieveAttributeOfTokenType(HttpServletRequest req) throws InvalidArgumentException, ProposalException {

        String tokenType = req.getParameter("tokenType");
        String ownerKey = req.getParameter("ownerKey");
        String attribute = (req.getParameter("attribute"));

        logger.info("retrieveAttributeOfTokenType ####################");

        Manager.setChaincodeId(chaincodeId);
        Manager.setCaller(ownerKey);

        List<String> pair = xType.retrieveAttributeOfTokenType(tokenType, attribute);
        logger.info(pair.toString());

        return pair.toString();
    }

    @GetMapping("/adminDropAttributeTokenType")
    public String adminDropAttributeTokenType(HttpServletRequest req) throws InvalidArgumentException, ProposalException {

        return "/adminDropAttributeTokenType";
    }

    @PostMapping("/dropAttributeTokenType")
    public String dropAttributeTokenType(HttpServletRequest req) throws InvalidArgumentException, ProposalException {

        String tokenType = req.getParameter("tokenType");
        String ownerKey = req.getParameter("ownerKey");
        String attribute = (req.getParameter("attribute"));

        logger.info("dropAttributeTokenType ####################");

        Manager.setChaincodeId(chaincodeId);
        Manager.setCaller(ownerKey);


        boolean result = xType.dropAttributeOfTokenType(tokenType, attribute);
        if(result == true)
            return "true";
        else
            return "false";

    }

    @GetMapping("/adminDropTokenType")
    public String adminDropTokenType(HttpServletRequest req) throws InvalidArgumentException, ProposalException {

        return "/adminDropTokenType";
    }

    @PostMapping("/dropTokenType")
    public String dropTokenType() {

        logger.info("dropTokenType ####################");

        return "dropTokenType";
    }


}
