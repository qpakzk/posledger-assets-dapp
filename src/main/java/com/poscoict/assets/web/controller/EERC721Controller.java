package com.poscoict.assets.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poscoict.posledger.chain.assets.chaincode.EERC721;
import com.poscoict.assets.exception.RestResourceException;
import com.poscoict.assets.service.UserService;
import com.poscoict.assets.web.ExceptionHandleController;
import com.poscoict.assets.web.HttpResponse;
import com.poscoict.posledger.chain.sign.certificate.PosCertificateService;
import com.poscoict.posledger.chain.sign.model.PosCertificate;
import com.poscoict.posledger.chain.sign.model.PosCertificateMeta;
import com.poscoict.posledger.chain.wallet.chaincode.ERC20ChaincodeService;
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

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.List;

@Controller
public class EERC721Controller extends ExceptionHandleController {
    private static final Logger logger = LogManager.getLogger(EERC721Controller.class);

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
    private EERC721 eerc721;

    @RequestMapping(value = "/eerc721/mint", method = RequestMethod.POST)
    @ResponseBody
    public HttpResponse mint(@RequestParam String certiPassword,
                             @RequestParam MultipartFile certfile, HttpServletRequest request,
                             @RequestParam BigInteger tokenId,
                             @RequestParam String type,
                             @RequestParam int pages) throws Exception {

        String hash = "c35b21d6ca39aa7cc3b79a705d989f1a6e88b99ab43988d74048799e3db926a3";
        String signers;
        String path = "https://www.off-chain-storage.com";
        String merkleroot = "558ad18828f6da6d471cdb1a3443f039a770e03617f163896980d914d643e4bc";

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

        String caller;
        try {
            caller = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        signers = caller;
        eerc721.setCaller(caller);
        boolean result = eerc721.mint(tokenId, type, caller, pages, hash, signers, path, merkleroot);

        return new HttpResponse(HttpResponse.success, String.valueOf(result));
    }

    @RequestMapping(value = "/eerc721/balanceOf", method = RequestMethod.POST)
    @ResponseBody
    public HttpResponse balanceOf(@RequestParam String certiPassword,
                                  @RequestParam MultipartFile certfile, HttpServletRequest request,
                                  @RequestParam String owner,
                                  @RequestParam String type) throws Exception {

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

        BigInteger balance = eerc721.balanceOf(owner, type);
        return new HttpResponse(HttpResponse.success, String.valueOf(balance));
    }

    @RequestMapping(value = "/eerc721/tokenIdsOf", method = RequestMethod.POST)
    @ResponseBody
    public HttpResponse tokenIdsOf(@RequestParam String certiPassword,
                                   @RequestParam MultipartFile certfile, HttpServletRequest request,
                                   @RequestParam String owner) throws Exception {

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

        List<BigInteger> tokenIds = eerc721.tokenIdsOf(owner);
        return new HttpResponse(HttpResponse.success, tokenIds.toString());
    }

    @RequestMapping(value = "/eerc721/divide", method = RequestMethod.POST)
    @ResponseBody
    public HttpResponse divide(@RequestParam String certiPassword,
                               @RequestParam MultipartFile certfile, HttpServletRequest request,
                               @RequestParam BigInteger tokenId,
                               @RequestParam BigInteger firstNewTokenId,
                               @RequestParam BigInteger secondNewTokenId,
                               @RequestParam String firstValue,
                               @RequestParam String secondValue) throws Exception {


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

        String caller;
        try {
            caller = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        String index = "pages";
        BigInteger[] newtokenIdForEERC721s = { firstNewTokenId, secondNewTokenId };
        String[] values = { firstValue, secondValue };
        eerc721.setCaller(caller);
        boolean result = eerc721.divide(tokenId, newtokenIdForEERC721s, values, index);

        return new HttpResponse(HttpResponse.success, String.valueOf(result));
    }

    @RequestMapping(value = "/eerc721/query", method = RequestMethod.POST)
    @ResponseBody
    public HttpResponse query(@RequestParam String certiPassword,
                              @RequestParam MultipartFile certfile, HttpServletRequest request,
                              @RequestParam BigInteger tokenId) throws Exception {


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

        String result = eerc721.query(tokenId);

        return new HttpResponse(HttpResponse.success, result);
    }

    @RequestMapping(value = "/eerc721/update", method = RequestMethod.POST)
    @ResponseBody
    public HttpResponse update(@RequestParam String certiPassword,
                               @RequestParam MultipartFile certfile, HttpServletRequest request,
                               @RequestParam BigInteger tokenId,
                               @RequestParam String index,
                               @RequestParam String attr) throws Exception {

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

        String caller;
        try {
            caller = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        eerc721.setCaller(caller);
        boolean result = eerc721.update(tokenId, index, attr);

        return new HttpResponse(HttpResponse.success, String.valueOf(result));
    }

    @RequestMapping(value = "/eerc721/deactivate", method = RequestMethod.POST)
    @ResponseBody
    public HttpResponse deactivate(@RequestParam String certiPassword,
                                   @RequestParam MultipartFile certfile, HttpServletRequest request,
                                   @RequestParam BigInteger tokenId) throws Exception {

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

        String caller;
        try {
            caller = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        eerc721.setCaller(caller);
        boolean result = eerc721.deactivate(tokenId);

        return new HttpResponse(HttpResponse.success, String.valueOf(result));
    }

    @RequestMapping(value = "/eerc721/queryHistory", method = RequestMethod.POST)
    @ResponseBody
    public HttpResponse queryHistory(@RequestParam String certiPassword,
                                     @RequestParam MultipartFile certfile, HttpServletRequest request,
                                     @RequestParam BigInteger tokenId) throws Exception {

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