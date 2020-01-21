package com.poscoict.assets.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poscoict.assets.persistence.UserDao;
import com.poscoict.posledger.chain.assets.chaincode.standard.*;
import com.poscoict.assets.service.UserService;
import com.poscoict.posledger.chain.assets.chaincode.util.Manager;
import com.poscoict.posledger.chain.sign.certificate.PosCertificateService;
import com.poscoict.posledger.chain.wallet.chaincode.ERC20ChaincodeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class StandardController extends RootController {
    private static final Logger logger = LogManager.getLogger(StandardController.class);

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
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ERC721 erc721;

    @Autowired
    private BaseNFT baseNFT;

    @GetMapping("/standard")
    public String standard() {
        return "standard";
    }

    @ResponseBody
    @PostMapping("/standard/mint")
    public boolean mint(HttpServletRequest request) throws Exception {
        String ownerKey = request.getParameter("ownerKey");
        Manager.setChaincodeId(getChaincodeId());
        Manager.setCaller(ownerKey);

        boolean result = false;
        while (!result) {
            String tokenId = getCounter();
            incrementCounter();
            result = baseNFT.mint(tokenId, ownerKey);
        }

        return result;
    }

    @GetMapping("/standard/burn")
    public String burn() {
        return "burn";
    }

    @ResponseBody
    @PostMapping("/standard/burn")
    public boolean burn(HttpServletRequest request) throws Exception {
        String ownerKey = request.getParameter("ownerKey");
        String tokenId = request.getParameter("tokenId");

        Manager.setChaincodeId(getChaincodeId());
        Manager.setCaller(ownerKey);

        return baseNFT.burn(tokenId);
    }
}
