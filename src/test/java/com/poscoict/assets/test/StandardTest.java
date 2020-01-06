package com.poscoict.assets.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poscoict.posledger.chain.assets.chaincode.BaseNFT;
import com.poscoict.posledger.chain.assets.chaincode.ERC721;
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

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@Configuration
@ComponentScan
@ContextConfiguration(classes = SpringConfig.class)
public class StandardTest {
    private static final Logger logger = LogManager.getLogger(StandardTest.class);

    @Autowired
    private ERC721 erc721;

    @Autowired
    private BaseNFT baseNFT;

    @Autowired
    private PosCertificateService posCertificateService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSourceAccessor message;

    private String alice;
    private String bob;
    private String carol;
    private String david;
    private final BigInteger tokenId = BigInteger.ZERO;
    private final static String CERT_PASSWARD = "1234";

    @Test
    public void mintTest() throws Exception {
        String fileName = "./certForAlice";
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
            alice = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        baseNFT.setCaller(alice);
        boolean result = baseNFT.mint(tokenId, alice);
        assertEquals(result, true);
    }

    @Test
    public void getTypeTest() throws Exception {
        String fileName = "./certForAlice";
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
            alice = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        String type = baseNFT.getType(tokenId);
        assertEquals(type, "base");
    }

    @Test
    public void balanceOfTest() throws Exception {
        String fileName = "./certForAlice";
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
            alice = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        BigInteger balance = erc721.balanceOf(alice);
        assertEquals(balance, BigInteger.ONE);
    }

    @Test
    public void ownerOfTest() throws Exception {
        String fileName = "./certForAlice";
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
            alice = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        String owner = erc721.ownerOf(tokenId);
        assertEquals(owner, alice);
    }


    @Test
    public void transferFromTest() throws Exception {
        String fileNameForAlice = "./certForAlice";
        MultipartFile certfileForAlice = new MockMultipartFile(fileNameForAlice, new FileInputStream(fileNameForAlice));

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
            isPasswardForAlice = posCertificateService.verifyPosCertificatePassword(posCertificateForAlice, CERT_PASSWARD);
        } catch(Exception e) {
            logger.error(e);
            throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
        }

        PosCertificateMeta posCertificateMetaForAlice = null;

        if (isPasswardForAlice) {
            try {
                posCertificateMetaForAlice = posCertificateService.getMobilePosCertificateMeta(posCertificateForAlice, CERT_PASSWARD, message.getMessage("application.posledger.challenge.domain"));
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


        String fileNameForBob = "./certForBob";
        MultipartFile certfileForBob = new MockMultipartFile(fileNameForBob, new FileInputStream(fileNameForBob));

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
            isPasswardForBob = posCertificateService.verifyPosCertificatePassword(posCertificateForBob, CERT_PASSWARD);
        } catch(Exception e) {
            logger.error(e);
            throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
        }

        PosCertificateMeta posCertificateMetaForBob = null;

        if (isPasswardForBob) {
            try {
                posCertificateMetaForBob = posCertificateService.getMobilePosCertificateMeta(posCertificateForBob, CERT_PASSWARD, message.getMessage("application.posledger.challenge.domain"));
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
        assertEquals(result, true);
    }

    @Test
    public void afterThatBalanceOfTest() throws Exception {
        String fileName = "./certForAlice";
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
            alice = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        erc721.setCaller(alice);
        BigInteger result = erc721.balanceOf(alice);
        assertEquals(result, BigInteger.ZERO);
    }

    @Test
    public void afterThatOwnerOfTest() throws Exception {
        String fileName = "./certForBob";
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
            bob = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        String result = erc721.ownerOf(tokenId);
        assertEquals(result, bob);
    }

    @Test
    public void approveTest() throws Exception {
        String fileNameForBob = "./certForBob";
        MultipartFile certfileForBob = new MockMultipartFile(fileNameForBob, new FileInputStream(fileNameForBob));

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
            isPasswardForBob = posCertificateService.verifyPosCertificatePassword(posCertificateForBob, CERT_PASSWARD);
        } catch(Exception e) {
            logger.error(e);
            throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
        }

        PosCertificateMeta posCertificateMetaForBob = null;

        if (isPasswardForBob) {
            try {
                posCertificateMetaForBob = posCertificateService.getMobilePosCertificateMeta(posCertificateForBob, CERT_PASSWARD, message.getMessage("application.posledger.challenge.domain"));
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

        String fileNameForCarol = "./certForCarol";
        MultipartFile certfileForCarol = new MockMultipartFile(fileNameForCarol, new FileInputStream(fileNameForCarol));

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
            isPasswardForCarol = posCertificateService.verifyPosCertificatePassword(posCertificateForCarol, CERT_PASSWARD);
        } catch(Exception e) {
            logger.error(e);
            throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
        }

        PosCertificateMeta posCertificateMetaForCarol = null;

        if (isPasswardForCarol) {
            try {
                posCertificateMetaForCarol = posCertificateService.getMobilePosCertificateMeta(posCertificateForCarol, CERT_PASSWARD, message.getMessage("application.posledger.challenge.domain"));
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
        assertEquals(result, true);
    }

    @Test
    public void getApprovedTest() throws Exception {
        String fileName = "./certForCarol";
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
            carol = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        String result = erc721.getApproved(tokenId);
        assertEquals(result, carol);
    }

    @Test
    public void setApprovalForAllTest() throws Exception {
        String fileNameForBob = "./certForBob";
        MultipartFile certfileForBob = new MockMultipartFile(fileNameForBob, new FileInputStream(fileNameForBob));

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
            isPasswardForBob = posCertificateService.verifyPosCertificatePassword(posCertificateForBob, CERT_PASSWARD);
        } catch(Exception e) {
            logger.error(e);
            throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
        }

        PosCertificateMeta posCertificateMetaForBob = null;

        if (isPasswardForBob) {
            try {
                posCertificateMetaForBob = posCertificateService.getMobilePosCertificateMeta(posCertificateForBob, CERT_PASSWARD, message.getMessage("application.posledger.challenge.domain"));
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

        String fileNameForDavid = "./certForDavid";
        MultipartFile certfileForDavid = new MockMultipartFile(fileNameForDavid, new FileInputStream(fileNameForDavid));

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
            isPasswardForDavid = posCertificateService.verifyPosCertificatePassword(posCertificateForDavid, CERT_PASSWARD);
        } catch(Exception e) {
            logger.error(e);
            throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
        }

        PosCertificateMeta posCertificateMetaForDavid = null;

        if (isPasswardForDavid) {
            try {
                posCertificateMetaForDavid = posCertificateService.getMobilePosCertificateMeta(posCertificateForDavid, CERT_PASSWARD, message.getMessage("application.posledger.challenge.domain"));
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
        boolean result = erc721.setApprovalForAll(david,true);
        assertEquals(result, true); //(result).isEqualTo(true);
    }

    @Test
    public void isApprovedForAllTest() throws Exception {
        String fileNameForBob = "./certForBob";
        MultipartFile certfileForBob = new MockMultipartFile(fileNameForBob, new FileInputStream(fileNameForBob));

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
            isPasswardForBob = posCertificateService.verifyPosCertificatePassword(posCertificateForBob, CERT_PASSWARD);
        } catch(Exception e) {
            logger.error(e);
            throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
        }

        PosCertificateMeta posCertificateMetaForBob = null;

        if (isPasswardForBob) {
            try {
                posCertificateMetaForBob = posCertificateService.getMobilePosCertificateMeta(posCertificateForBob, CERT_PASSWARD, message.getMessage("application.posledger.challenge.domain"));
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

        String fileNameForDavid = "./certForDavid";
        MultipartFile certfileForDavid = new MockMultipartFile(fileNameForDavid, new FileInputStream(fileNameForDavid));

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
            isPasswardForDavid = posCertificateService.verifyPosCertificatePassword(posCertificateForDavid, CERT_PASSWARD);
        } catch(Exception e) {
            logger.error(e);
            throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
        }

        PosCertificateMeta posCertificateMetaForDavid = null;

        if (isPasswardForDavid) {
            try {
                posCertificateMetaForDavid = posCertificateService.getMobilePosCertificateMeta(posCertificateForDavid, CERT_PASSWARD, message.getMessage("application.posledger.challenge.domain"));
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
        assertEquals(result, true);
    }

    @Test
    public void mintForBurnTest() throws Exception {
        String fileName = "./certForCarol";
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
            carol = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        baseNFT.setCaller(carol);
        boolean result = baseNFT.mint(BigInteger.ONE, carol);
        assertEquals(result, true);
    }

    @Test
    public void burnTest() throws Exception {
        String fileName = "./certForCarol";
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
            carol = posCertificateMeta.getOwnerKey();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

        baseNFT.setCaller(carol);
        boolean result = baseNFT.burn(BigInteger.ONE);
        assertEquals(result, true);
    }
}