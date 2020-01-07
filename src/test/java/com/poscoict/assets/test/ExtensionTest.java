package com.poscoict.assets.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poscoict.posledger.chain.assets.chaincode.extension.*;
import com.poscoict.assets.config.SpringConfig;
import com.poscoict.assets.exception.RestResourceException;
import com.poscoict.posledger.chain.sign.certificate.PosCertificateService;
import com.poscoict.posledger.chain.sign.model.PosCertificate;
import com.poscoict.posledger.chain.sign.model.PosCertificateMeta;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@Configuration
@ComponentScan
@ContextConfiguration(classes = SpringConfig.class)
public class ExtensionTest {
    private static final Logger logger = LogManager.getLogger(ExtensionTest.class);

    @Autowired
    private EERC721 eerc721;

    @Autowired
    private XNFT xnft;

    @Autowired
    private XType xType;

    @Autowired
    private PosCertificateService posCertificateService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSourceAccessor message;

    private String david;
    private final BigInteger tokenIdForEERC721 = BigInteger.valueOf(160);
    private String type = "doc";
    private int pages = 100;
    private String hash = "c35b21d6ca39aa7cc3b79a705d989f1a6e88b99ab43988d74048799e3db926a3";
    private List<String> signers =
            new ArrayList<>(Arrays.asList("1FbLcUY39EmYSjtxjpHSVKEeUZQNKAvooa",
                                            "1K1kziRrgtLc8nspFSsnmWMS6A8rVC9AbC",
                                            "1Nhemxp7rPGAKqSsXG7xt728cEgEGtMuFZ"));
    private String path = "https://www.off-chain-storage.com";
    private String merkleroot = "558ad18828f6da6d471cdb1a3443f039a770e03617f163896980d914d643e4bc";

    private BigInteger[] newtokenIdForEERC721s = { BigInteger.valueOf(161), BigInteger.valueOf(162) };
    private String[] values = {"40", "60"};

    private final static String CERT_PASSWARD = "1234";

    @Test
    public void registerTokenType() throws Exception {
        String fileName = "./certForDavid";
        MultipartFile certfile = new MockMultipartFile(fileName, new FileInputStream(fileName));

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
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        xType.setCaller(david);

        String docType = "doc";
        Map<String, List<String>> xattr = new HashMap<>();
        List<String> hash = new ArrayList<>(Arrays.asList("String", ""));
        xattr.put("hash", hash);

        List<String> pages = new ArrayList<>(Arrays.asList("Integer", "0"));
        xattr.put("pages", pages);

        List<String> signers = new ArrayList<>(Arrays.asList("[String]", "[]"));
        xattr.put("signers", signers);

        List<String> signatures = new ArrayList<>(Arrays.asList("[String]", "[]"));
        xattr.put("signatures", signatures);

        boolean checkForDoc = xType.registerTokenType(david, docType, xattr);
        String sigType = "sig";
        Map<String, List<String>> xattr1 = new HashMap<>();
        List<String> hash1 = new ArrayList<>(Arrays.asList("String", ""));
        xattr1.put("hash", hash1);

        boolean checkForSig = xType.registerTokenType(david, sigType, xattr1);

        boolean result = checkForDoc && checkForSig;
        assertEquals(result, true);
    }

    @Test
    public void getTokenTypeTest() throws Exception {
        String fileName = "./certForDavid";
        MultipartFile certfile = new MockMultipartFile(fileName, new FileInputStream(fileName));

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
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        Map<String, List<String>> xattr = xType.getTokenType(type);

        assertEquals(xattr.containsKey("hash"), true);
        List<String> hash = xattr.get("hash");
        assertEquals(hash.get(0), "String");
        assertEquals(hash.get(1), "");
    }

    @Test
    public void tokenTypesOfTest() throws Exception {
        String fileName = "./certForDavid";
        MultipartFile certfile = new MockMultipartFile(fileName, new FileInputStream(fileName));

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
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        List<String> types = xType.tokenTypesOf();

        assertEquals(types.size(), 2);
        assertEquals(types.get(0), "sig");
        assertEquals(types.get(1), "doc");
    }

    @Test
    public void mintTest() throws Exception {
        String fileName = "./certForDavid";
        MultipartFile certfile = new MockMultipartFile(fileName, new FileInputStream(fileName));

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
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        xnft.setCaller(david);

        Map<String, Object> xattr = new HashMap<>();
        xattr.put("pages", pages);
        xattr.put("hash", hash);
        xattr.put("signers", signers);

        Map<String, String> uri = new HashMap<>();
        uri.put("path", path);
        uri.put("hash", merkleroot);

        boolean result = xnft.mint(tokenIdForEERC721, type, david, xattr, uri);
        assertEquals(result, true);
    }

    @Test
    public void balanceOfTest() throws Exception {
        String fileName = "./certForDavid";
        MultipartFile certfile = new MockMultipartFile(fileName, new FileInputStream(fileName));

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
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        BigInteger balance = eerc721.balanceOf(david, type);
        assertEquals(balance, BigInteger.ONE);
    }

    @Test
    public void divideTest() throws Exception {
        String fileName = "./certForDavid";
        MultipartFile certfile = new MockMultipartFile(fileName, new FileInputStream(fileName));

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
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        String index = "pages";
        eerc721.setCaller(david);
        boolean result = eerc721.divide(tokenIdForEERC721, newtokenIdForEERC721s, values, index);
        assertEquals(result, true);
    }

    @Test
    public void tokenIdsOfTest() throws Exception {
        String fileName = "./certForDavid";
        MultipartFile certfile = new MockMultipartFile(fileName, new FileInputStream(fileName));

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
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        List<BigInteger> tokenIds = eerc721.tokenIdsOf(david);

        assertEquals(tokenIds.get(0), BigInteger.valueOf(160));
        assertEquals(tokenIds.get(1), BigInteger.valueOf(161));
        assertEquals(tokenIds.get(2), BigInteger.valueOf(162));
    }

    @Test
    public void queryTest() throws Exception {
        String fileName = "./certForDavid";
        MultipartFile certfile = new MockMultipartFile(fileName, new FileInputStream(fileName));

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
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        String result = eerc721.query(tokenIdForEERC721);

        if(result != null) {
            Map<String, Object> map =
                    objectMapper.readValue(result, new TypeReference<HashMap<String, Object>>(){});

            String type = (String) map.get("type");
            String owner = (String) map.get("owner");
            String approvee = (String) map.get("approvee");
            Map<String, Object> xattrMap = (HashMap<String, Object>) map.get("xattr");

            List<String> signers = (ArrayList<String>) xattrMap.get("signers");
            String hash = (String) xattrMap.get("hash");
            int pages = (int) xattrMap.get("pages");

            Map<String, String> uriMap = (HashMap<String, String>) map.get("uri");
            String path = uriMap.get("path");
            String merkleroot = uriMap.get("hash");

            assertEquals(david, owner);
            if(david.equals(owner)) {
                logger.info("query owner true");
            }else {
                logger.info("query owner fail");
            }

            assertEquals(this.type, type);
            if(this.type.equals(type)) {
                logger.info("query type true");
            }else {
                logger.info("query type fail");
            }

            assertEquals(this.signers.get(0), signers.get(0));
            if(this.signers.get(0).equals(signers.get(0))) {
                logger.info("query signers.get(0) true");
            }else {
                logger.info("query signers.get(0) fail");
            }

            assertEquals(this.signers.get(1), signers.get(1));
            if(this.signers.get(1).equals(signers.get(1))) {
                logger.info("query signers.get(1) true");
            }else {
                logger.info("query signers.get(1) fail");
            }

            assertEquals(this.signers.get(2), signers.get(2));
            if(this.signers.get(2).equals(signers.get(2))) {
                logger.info("query signers.get(2) true");
            }else {
                logger.info("query signers.get(2) fail");
            }

            assertEquals(this.hash, hash);
            if(this.hash.equals(hash)) {
                logger.info("query hash true");
            }else {
                logger.info("query hash fail");
            }

            assertEquals(this.pages, pages);
            if(this.pages == pages) {
                logger.info("query pages true");
            }else {
                logger.info("query pages fail");
            }

            assertEquals(this.path, path);
            if(this.path.equals(path)) {
                logger.info("query path true");
            }else {
                logger.info("query path fail");
            }

            assertEquals(this.merkleroot, merkleroot);
            if(this.merkleroot.equals(merkleroot)) {
                logger.info("query merkleroot true");
            }else {
                logger.info("query merkleroot fail");
            }
        } else {
            logger.info("query fail");
        }
    }

    @Test
    public void queryNewToken0Test() throws Exception {
        String fileName = "./certForDavid";
        MultipartFile certfile = new MockMultipartFile(fileName, new FileInputStream(fileName));

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
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        String result = eerc721.query(newtokenIdForEERC721s[0]);

        if(result != null) {
            Map<String, Object> map =
                    objectMapper.readValue(result, new TypeReference<HashMap<String, Object>>(){});

            String type = (String) map.get("type");
            String owner = (String) map.get("owner");
            String approvee = (String) map.get("approvee");
            Map<String, Object> xattrMap = (HashMap<String, Object>) map.get("xattr");

            List<String> signers = (ArrayList<String>) xattrMap.get("signers");
            String hash = (String) xattrMap.get("hash");
            int pages = (int) xattrMap.get("pages");

            Map<String, String> uriMap = (HashMap<String, String>) map.get("uri");
            String path = uriMap.get("path");
            String merkleroot = uriMap.get("hash");

            assertEquals(david, owner);
            if(david.equals(owner)) {
                logger.info("query owner true");
            }else {
                logger.info("query owner fail");
            }

            assertEquals(this.type, type);
            if(this.type.equals(type)) {
                logger.info("query type true");
            }else {
                logger.info("query type fail");
            }

            assertEquals(this.signers.get(0), signers.get(0));
            if(this.signers.get(0).equals(signers.get(0))) {
                logger.info("query signers.get(0) true");
            }else {
                logger.info("query signers.get(0) fail");
            }

            assertEquals(this.signers.get(1), signers.get(1));
            if(this.signers.get(1).equals(signers.get(1))) {
                logger.info("query signers.get(1) true");
            }else {
                logger.info("query signers.get(1) fail");
            }

            assertEquals(this.signers.get(2), signers.get(2));
            if(this.signers.get(2).equals(signers.get(2))) {
                logger.info("query signers.get(2) true");
            }else {
                logger.info("query signers.get(2) fail");
            }

            assertEquals(this.hash, hash);
            if(this.hash.equals(hash)) {
                logger.info("query hash true");
            }else {
                logger.info("query hash fail");
            }

            int value = Integer.parseInt(this.values[0]);
            assertEquals(value, pages);
            if(value == pages) {
                logger.info("query pages true");
            }else {
                logger.info("query pages fail");
            }

            assertEquals(this.path, path);
            if(this.path.equals(path)) {
                logger.info("query path true");
            }else {
                logger.info("query path fail");
            }

            assertEquals(this.merkleroot, merkleroot);
            if(this.merkleroot.equals(merkleroot)) {
                logger.info("query merkleroot true");
            }else {
                logger.info("query merkleroot fail");
            }
        } else {
            logger.info("query fail");
        }
    }

    @Test
    public void queryNewToken1Test() throws Exception {
        String fileName = "./certForDavid";
        MultipartFile certfile = new MockMultipartFile(fileName, new FileInputStream(fileName));

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
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        String result = eerc721.query(newtokenIdForEERC721s[1]);

        if(result != null) {
            Map<String, Object> map =
                    objectMapper.readValue(result, new TypeReference<HashMap<String, Object>>(){});

            String type = (String) map.get("type");
            String owner = (String) map.get("owner");
            String approvee = (String) map.get("approvee");
            Map<String, Object> xattrMap = (HashMap<String, Object>) map.get("xattr");

            List<String> signers = (ArrayList<String>) xattrMap.get("signers");
            String hash = (String) xattrMap.get("hash");
            int pages = (int) xattrMap.get("pages");

            Map<String, String> uriMap = (HashMap<String, String>) map.get("uri");
            String path = uriMap.get("path");
            String merkleroot = uriMap.get("hash");

            assertEquals(david, owner);
            if(david.equals(owner)) {
                logger.info("query owner true");
            }else {
                logger.info("query owner fail");
            }

            assertEquals(this.type, type);
            if(this.type.equals(type)) {
                logger.info("query type true");
            }else {
                logger.info("query type fail");
            }

            assertEquals(this.signers.get(0), signers.get(0));
            if(this.signers.get(0).equals(signers.get(0))) {
                logger.info("query signers.get(0) true");
            }else {
                logger.info("query signers.get(0) fail");
            }

            assertEquals(this.signers.get(1), signers.get(1));
            if(this.signers.get(1).equals(signers.get(1))) {
                logger.info("query signers.get(1) true");
            }else {
                logger.info("query signers.get(1) fail");
            }

            assertEquals(this.signers.get(2), signers.get(2));
            if(this.signers.get(2).equals(signers.get(2))) {
                logger.info("query signers.get(2) true");
            }else {
                logger.info("query signers.get(2) fail");
            }

            assertEquals(this.hash, hash);
            if(this.hash.equals(hash)) {
                logger.info("query hash true");
            }else {
                logger.info("query hash fail");
            }

            int value = Integer.parseInt(this.values[1]);
            assertEquals(value, pages);
            if(value == pages) {
                logger.info("query pages true");
            }else {
                logger.info("query pages fail");
            }

            assertEquals(this.path, path);
            if(this.path.equals(path)) {
                logger.info("query path true");
            }else {
                logger.info("query path fail");
            }

            assertEquals(this.merkleroot, merkleroot);
            if(this.merkleroot.equals(merkleroot)) {
                logger.info("query merkleroot true");
            }else {
                logger.info("query merkleroot fail");
            }
        } else {
            logger.info("query fail");
        }
    }

    @Test
    public void updateTest() throws Exception {
        String fileName = "./certForDavid";
        MultipartFile certfile = new MockMultipartFile(fileName, new FileInputStream(fileName));

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
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        String attr = david +" SigId";
        String index = "sigIds";

        eerc721.setCaller(david);
        boolean result = eerc721.update(tokenIdForEERC721, index, attr);
        assertEquals(result, true);
    }

    @Test
    public void deactivateTest() throws Exception {
        String fileName = "./certForDavid";
        MultipartFile certfile = new MockMultipartFile(fileName, new FileInputStream(fileName));

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
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        eerc721.setCaller(david);
        boolean result =eerc721.deactivate(tokenIdForEERC721);
        assertEquals(result, true);
    }

    @Test
    public void afterUpdateAndDeactivateQueryTest() throws Exception {
        String fileName = "./certForDavid";
        MultipartFile certfile = new MockMultipartFile(fileName, new FileInputStream(fileName));

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
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        String result = eerc721.query(tokenIdForEERC721);

        if(result != null) {
            Map<String, Object> map =
                    objectMapper.readValue(result, new TypeReference<HashMap<String, Object>>(){});

            String type = (String) map.get("type");
            String owner = (String) map.get("owner");
            String approvee = (String) map.get("approvee");
            Map<String, Object> xattrMap = (HashMap<String, Object>) map.get("xattr");

            List<String> signers = (ArrayList<String>) xattrMap.get("signers");
            String hash = (String) xattrMap.get("hash");
            int pages = (int) xattrMap.get("pages");

            Map<String, String> uriMap = (HashMap<String, String>) map.get("uri");
            String path = uriMap.get("path");
            String merkleroot = uriMap.get("hash");

            assertEquals(david, owner);
            if(david.equals(owner)) {
                logger.info("query owner true");
            }else {
                logger.info("query owner fail");
            }

            assertEquals(this.type, type);
            if(this.type.equals(type)) {
                logger.info("query type true");
            }else {
                logger.info("query type fail");
            }

            assertEquals(this.signers.get(0), signers.get(0));
            if(this.signers.get(0).equals(signers.get(0))) {
                logger.info("query signers.get(0) true");
            }else {
                logger.info("query signers.get(0) fail");
            }

            assertEquals(this.signers.get(1), signers.get(1));
            if(this.signers.get(1).equals(signers.get(1))) {
                logger.info("query signers.get(1) true");
            }else {
                logger.info("query signers.get(1) fail");
            }

            assertEquals(this.signers.get(2), signers.get(2));
            if(this.signers.get(2).equals(signers.get(2))) {
                logger.info("query signers.get(2) true");
            }else {
                logger.info("query signers.get(2) fail");
            }

            assertEquals(this.hash, hash);
            if(this.hash.equals(hash)) {
                logger.info("query hash true");
            }else {
                logger.info("query hash fail");
            }

            assertEquals(this.pages, pages);
            if(this.pages == pages) {
                logger.info("query pages true");
            }else {
                logger.info("query pages fail");
            }

            assertEquals(this.path, path);
            if(this.path.equals(path)) {
                logger.info("query path true");
            }else {
                logger.info("query path fail");
            }

            assertEquals(this.merkleroot, merkleroot);
            if(this.merkleroot.equals(merkleroot)) {
                logger.info("query merkleroot true");
            }else {
                logger.info("query merkleroot fail");
            }
        } else {
            logger.info("query fail");
        }
    }

    @Test
    public void queryHistoryTest() throws Exception {
        String fileName = "./certForDavid";
        MultipartFile certfile = new MockMultipartFile(fileName, new FileInputStream(fileName));

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
            isPassward = posCertificateService.verifyPosCertificatePassword(posCertificate, CERT_PASSWARD);
        } catch (Exception e) {
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
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        List<String> histories = eerc721.queryHistory(tokenIdForEERC721);

        if (histories != null) {
            for (String history : histories) {
                if (history != null) {
                    logger.info(history);
                }
            }
        }
    }
}