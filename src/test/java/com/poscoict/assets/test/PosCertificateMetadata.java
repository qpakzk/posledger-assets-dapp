package com.poscoict.assets.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poscoict.assets.config.SpringConfig;
import com.poscoict.assets.exception.RestResourceException;
import com.poscoict.posledger.chain.sign.model.PosCertificate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;

import com.poscoict.assets.service.UserService;
import com.poscoict.posledger.chain.sign.certificate.PosCertificateService;
import com.poscoict.posledger.chain.sign.model.PosCertificateMeta;
import com.poscoict.posledger.chain.wallet.chaincode.ERC20ChaincodeService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;

@RunWith(SpringRunner.class)
@Configuration
@ComponentScan
@ContextConfiguration(classes = SpringConfig.class)
public class PosCertificateMetadata {

    private static final Logger logger = LogManager.getLogger(PosCertificateMetadata.class);

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


    public PosCertificateMeta getPosCertificateMeta(String cert, String passwd) throws Exception {

        String certiPassword = "1234";
        String fileName = "./certForBob";
        MultipartFile certfile = new MockMultipartFile(fileName, new FileInputStream(fileName));

        if (certfile.isEmpty()) {
            logger.info("첨부 인증서 정보가 없습니다.");
        }

        PosCertificate posCertificate = null;

        try {
            posCertificate = (PosCertificate) objectMapper.readValue(certfile.getBytes(), new TypeReference<PosCertificate>(){});
        } catch(Exception e) {
            logger.error(e);
            throw new RestResourceException("유효하지 않은 인증서 형식입니다.");
        }

        // 인증서 비밀번호 검증
        boolean result = false;

        try {
            result = posCertificateService.verifyPosCertificatePassword(posCertificate, certiPassword);
        } catch(Exception e) {
            logger.error(e);
            throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
        }

        PosCertificateMeta posCertificateMeta = null;// = new PosCertificateMeta();;

        if (result) {

            try {
                posCertificateMeta = posCertificateService.getMobilePosCertificateMeta(posCertificate, certiPassword, message.getMessage("application.posledger.challenge.domain"));
            } catch (Exception e) {
                logger.error(e);
                throw new RestResourceException(e.getLocalizedMessage());
            }
        }

        return posCertificateMeta;
    }
}
