package com.poscoict.assets.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poscoict.posledger.chain.assets.chaincode.extension.*;
import com.poscoict.assets.config.SpringConfig;
import com.poscoict.assets.exception.RestResourceException;
import com.poscoict.posledger.chain.assets.chaincode.util.Manager;
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
import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@Configuration
@ComponentScan
@ContextConfiguration(classes = SpringConfig.class)

public class TokenTypeTest {
    private static final Logger logger = LogManager.getLogger(TokenTypeTest.class);

    @Autowired
    private XType xType;

    @Autowired
    private PosCertificateService posCertificateService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSourceAccessor message;

    private String chaincodeId = "assetscc0";

    private final static String CERT_PASSWARD = "1234";

    @Test
    public void enrollDocTokenTypeTest() throws Exception {
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

        String david;
        try {
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        Manager.setChaincodeId(chaincodeId);
        Manager.setCaller(david);

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

        boolean result = xType.enrollTokenType(docType, xattr);
        assertEquals(result, true);
    }

    @Test
    public void enrollSigTokenTypeTest() throws Exception {
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

        String david;
        try {
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        Manager.setChaincodeId(chaincodeId);
        Manager.setCaller(david);

        String sigType = "sig";
        Map<String, List<String>> xattr = new HashMap<>();
        List<String> hash = new ArrayList<>(Arrays.asList("String", ""));
        xattr.put("hash", hash);

        boolean result = xType.enrollTokenType(sigType, xattr);
        assertEquals(result, true);
    }

    @Test
    public void enrollDummyTokenTypeTest() throws Exception {
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

        String david;
        try {
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        Manager.setChaincodeId(chaincodeId);
        Manager.setCaller(david);

        String dummyType = "dummy";
        Map<String, List<String>> xattr = new HashMap<>();
        List<String> hash = new ArrayList<>(Arrays.asList("Integer", ""));
        xattr.put("hash", hash);

        boolean result = xType.enrollTokenType(dummyType, xattr);
        assertEquals(result, true);
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

        Manager.setChaincodeId(chaincodeId);
        List<String> types = xType.tokenTypesOf();
        logger.info(types.toString());
        assertEquals(types.size(), 3);
        assertEquals(types.get(0), "dummy");
        assertEquals(types.get(1), "sig");
        assertEquals(types.get(2), "doc");
    }

    @Test
    public void updateDummyTokenTypeTest() throws Exception {
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

        String david;
        try {
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        Manager.setChaincodeId(chaincodeId);
        Manager.setCaller(david);

        String dummyType = "dummy";
        Map<String, List<String>> attributes = new HashMap<>();
        List<String> attr1 = new ArrayList<>(Arrays.asList("Integer", Integer.toString(0)));
        attributes.put("attr1", attr1);

        List<String> attr2 = new ArrayList<>(Arrays.asList("String", ""));
        attributes.put("attr2", attr2);

        boolean result = xType.updateTokenType(dummyType, attributes);
        assertEquals(result, true);
    }

    @Test
    public void retrieveDummyTokenTypeTest() throws Exception {
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

        Manager.setChaincodeId(chaincodeId);
        String dummyType = "dummy";
        Map<String, List<String>> attributes = xType.retrieveTokenType(dummyType);

        logger.info(attributes.toString());
        assertEquals(attributes.containsKey("attr1"), true);
        List<String> attr1 = attributes.get("attr1");
        assertEquals(attr1.get(0), "Integer");
        assertEquals(attr1.get(1), Integer.toString(0));
    }

    @Test
    public void dropDummyTokenTypeTest() throws Exception {
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

        String david;
        try {
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        Manager.setChaincodeId(chaincodeId);
        Manager.setCaller(david);

        String dummyType = "dummy";
        boolean result = xType.dropTokenType(dummyType);

        assertEquals(result, true);
    }

    @Test
    public void enrollDateAttributeOfDocTokenTypeTest() throws Exception {
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

        String david;
        try {
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        Manager.setChaincodeId(chaincodeId);
        Manager.setCaller(david);

        String docType = "doc";
        String attribute = "date";
        String dataType = "Integer";
        String initialValue = Integer.toString(20200110);
        boolean result = xType.enrollAttributeOfTokenType(docType,attribute, dataType, initialValue);
        assertEquals(result, true);
    }

    @Test
    public void updateDateAttributeOfDocTokenTypeTest() throws Exception {
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

        String david;
        try {
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        Manager.setChaincodeId(chaincodeId);
        Manager.setCaller(david);

        String docType = "doc";
        String attribute = "date";
        String dataType = "String";
        String initialValue = "2020-01-10";
        List<String> pair = new ArrayList<>(Arrays.asList(dataType, initialValue));
        boolean result = xType.updateAttributeOfTokenType(docType,attribute, pair);
        assertEquals(result, true);
    }

    @Test
    public void retrieveDateAttributeOfDocTokenTypeTest() throws Exception {
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

        String david;
        try {
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        Manager.setChaincodeId(chaincodeId);
        Manager.setCaller(david);

        String docType = "doc";
        String attribute = "date";
        String dataType = "String";
        String initialValue = "2020-01-10";
        List<String> pair = xType.retrieveAttributeOfTokenType(docType, attribute);
        logger.info(pair.toString());
        assertEquals(dataType, pair.get(0));
        assertEquals(initialValue, pair.get(1));
    }

    @Test
    public void dropDateAttributeOfDocTokenTypeTest() throws Exception {
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

        String david;
        try {
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        Manager.setChaincodeId(chaincodeId);
        Manager.setCaller(david);

        String docType = "doc";
        String attribute = "date";
        boolean result = xType.dropAttributeOfTokenType(docType,attribute);
        assertEquals(result, true);
    }
}
