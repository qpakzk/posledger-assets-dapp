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

import static org.junit.Assert.*;

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
    private PosCertificateService posCertificateService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSourceAccessor message;

    private String chaincodeId = "assetscc0";

    private final static String CERT_PASSWARD = "1234";

    @Test
    public void mintTest() throws Exception {
        String fileName = "./certForDavid";
        MultipartFile certfile = new MockMultipartFile(fileName, new FileInputStream(fileName));

        PosCertificate posCertificate;
        try {
            posCertificate = objectMapper.readValue(certfile.getBytes(), new TypeReference<PosCertificate>(){});
        } catch(Exception e) {
            logger.error(e);
            throw new RestResourceException("유효하지 않은 인증서 형식입니다.");
        }

        // 인증서 비밀번호 검증
        boolean isPassward;

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

        Map<String, Object> xattr = new HashMap<>();
        int pages = 1000;
        String hash = "c35b21d6ca39aa7cc3b79a705d989f1a6e88b99ab43988d74048799e3db926a3";
        List<String> signers =
                new ArrayList<>(Arrays.asList("1FbLcUY39EmYSjtxjpHSVKEeUZQNKAvooa",
                        "1K1kziRrgtLc8nspFSsnmWMS6A8rVC9AbC",
                        "1Nhemxp7rPGAKqSsXG7xt728cEgEGtMuFZ"));
        String path = "https://www.off-chain-storage.com";
        String merkleroot = "558ad18828f6da6d471cdb1a3443f039a770e03617f163896980d914d643e4bc";

        xattr.put("pages", pages);
        xattr.put("hash", hash);
        xattr.put("signers", signers);

        Map<String, String> uri = new HashMap<>();
        uri.put("path", path);
        uri.put("hash", merkleroot);

        String id = "170";
        String type = "doc";
        boolean result = xnft.mint(id, type, david, xattr, uri);
        assertTrue(result);
    }

    @Test
    public void setXAttrTest() throws Exception {
        String fileName = "./certForDavid";
        MultipartFile certfile = new MockMultipartFile(fileName, new FileInputStream(fileName));

        PosCertificate posCertificate;
        try {
            posCertificate = objectMapper.readValue(certfile.getBytes(), new TypeReference<PosCertificate>(){});
        } catch(Exception e) {
            logger.error(e);
            throw new RestResourceException("유효하지 않은 인증서 형식입니다.");
        }

        // 인증서 비밀번호 검증
        boolean isPassward;

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

        String id = "170";
        String index = "pages";
        int value = 100;

        Manager.setChaincodeId(chaincodeId);
        Manager.setCaller(david);

        boolean result = xnft.setXAttr(id, index, value);
        assertTrue(result);
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
        boolean isPassward;

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
        String type = "doc";
        long balance = eerc721.balanceOf(david, type);
        logger.info("EERC721.balanceOf : {}", balance);
        assertEquals(1, balance);
    }

    @Test
    public void divideTest() throws Exception {
        String fileName = "./certForDavid";
        MultipartFile certfile = new MockMultipartFile(fileName, new FileInputStream(fileName));

        PosCertificate posCertificate;
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

        String id = "160";
        String index = "pages";
        Manager.setCaller(david);
        String[] newIds = { "161", "162" };
        String[] values = {"40", "60"};
        boolean result = eerc721.divide(id, newIds, values, index);
        assertTrue(result);
    }

    @Test
    public void tokenIdsOfAllTest() throws Exception {
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
        List<String> tokenIds = eerc721.tokenIdsOf(david);
        logger.info("EERC721.tokenIdsOf : {}", tokenIds);
        assertEquals("160", tokenIds.get(0));
        assertEquals("161", tokenIds.get(1));
        assertEquals("162", tokenIds.get(2));
    }

    @Test
    public void tokenIdsOfAllActivatedTest() throws Exception {
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
        List<String> tokenIds = eerc721.tokenIdsOf(david, "_");
        logger.info("EERC721.tokenIdsOf : {}", tokenIds);
        assertEquals("161", tokenIds.get(0));
        assertEquals("162", tokenIds.get(1));
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

        String david;
        try {
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        Manager.setChaincodeId(chaincodeId);
        String id = "160";
        String result = eerc721.query(id);

        if(result != null) {
            Map<String, Object> map =
                    objectMapper.readValue(result, new TypeReference<HashMap<String, Object>>(){});

            Map<String, String> uri = (HashMap<String, String>) map.get("uri");

            String path = uri.get("path");
            String origianl_path = "https://www.off-chain-storage.com";
            assertEquals(origianl_path, path);

            String merkleroot = uri.get("hash");
            String original_merkleroot = "558ad18828f6da6d471cdb1a3443f039a770e03617f163896980d914d643e4bc";
            assertEquals(original_merkleroot, merkleroot);

            Map<String, Object> xattr = (HashMap<String, Object>) map.get("xattr");

            List<String> signers = (ArrayList<String>) xattr.get("signers");
            List<String> original_signers =
                    new ArrayList<>(Arrays.asList("1FbLcUY39EmYSjtxjpHSVKEeUZQNKAvooa",
                            "1K1kziRrgtLc8nspFSsnmWMS6A8rVC9AbC",
                            "1Nhemxp7rPGAKqSsXG7xt728cEgEGtMuFZ"));

            assertEquals(3, signers.size());
            assertEquals(original_signers.get(0), signers.get(0));
            assertEquals(original_signers.get(1), signers.get(1));
            assertEquals(original_signers.get(2), signers.get(2));

            String hash = (String) xattr.get("hash");
            String original_hash = "c35b21d6ca39aa7cc3b79a705d989f1a6e88b99ab43988d74048799e3db926a3";
            assertEquals(original_hash, hash);

            int pages = (int) xattr.get("pages");
            int original_pages = 100;
            assertEquals(original_pages, pages);

            boolean activated = (boolean) xattr.get("activated");
            assertFalse(activated);

            String parent = (String) xattr.get("parent");
            assertEquals("", parent);

            List<String> children = (ArrayList<String>) xattr.get("children");
            assertEquals("161", children.get(0));
            assertEquals("162", children.get(1));
        } else {
            logger.error("query fail");
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

        String david;
        try {
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        Manager.setChaincodeId(chaincodeId);

        String[] newIds = { "161", "162" };
        String result = eerc721.query(newIds[0]);

        if(result != null) {
            Map<String, Object> map =
                    objectMapper.readValue(result, new TypeReference<HashMap<String, Object>>(){});

            Map<String, Object> xattr = (HashMap<String, Object>) map.get("xattr");

            int pages = (int) xattr.get("pages");
            String[] values = {"40", "60"};
            assertEquals(Integer.parseInt(values[0]), pages);

            boolean activated = (boolean) xattr.get("activated");
            assertTrue(activated);

            String parent = (String) xattr.get("parent");
            assertEquals("160", parent);
        } else {
            logger.error("query fail");
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

        String david;
        try {
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        Manager.setChaincodeId(chaincodeId);
        String[] newIds = { "161", "162" };
        String result = eerc721.query(newIds[1]);

        if(result != null) {
            Map<String, Object> map =
                    objectMapper.readValue(result, new TypeReference<HashMap<String, Object>>(){});

            Map<String, Object> xattr = (HashMap<String, Object>) map.get("xattr");

            int pages = (int) xattr.get("pages");
            String[] values = {"40", "60"};
            assertEquals(Integer.parseInt(values[1]), pages);

            boolean activated = (boolean) xattr.get("activated");
            assertTrue(activated);

            String parent = (String) xattr.get("parent");
            assertEquals("160", parent);
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

        String david;
        try {
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        String id = "161";

        String index = "signatures";

        List<String> signatures = new ArrayList<>();
        signatures.add("david signature");
        String attr = signatures.toString();

        Manager.setChaincodeId(chaincodeId);
        Manager.setCaller(david);
        boolean result = eerc721.update(id, index, attr);
        assertTrue(result);
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

        String david;
        try {
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        Manager.setChaincodeId(chaincodeId);
        Manager.setCaller(david);
        String[] newIds = { "161", "162" };
        boolean result =eerc721.deactivate(newIds[0]);
        assertTrue(result);
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

        String david;
        try {
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        Manager.setChaincodeId(chaincodeId);
        String[] newIds = { "161", "162" };
        String signaturesStr = xnft.getXAttr(newIds[0], "signatures");
        List<String> signatures = Arrays.asList(signaturesStr.substring(1, signaturesStr.length() -1).split(", "));
        logger.info("XNFT.getXAttr {}", signatures);
        assertEquals("david signature", signatures.get(0));

        boolean activated = Boolean.parseBoolean(xnft.getXAttr(newIds[0], "parent"));
        assertFalse(activated);
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

        String david;
        try {
            david = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        Manager.setChaincodeId(chaincodeId);
        String id = "160";
        List<String> histories = eerc721.queryHistory(id);

        if (histories != null) {
            for (String history : histories) {
                if (history != null) {
                    logger.info(history);
                }
            }
        }
    }
}