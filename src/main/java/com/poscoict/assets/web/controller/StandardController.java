package com.poscoict.assets.web.controller;

import com.poscoict.assets.persistence.UserDao;
import com.poscoict.posledger.chain.assets.chaincode.standard.*;
import com.poscoict.posledger.chain.assets.chaincode.util.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class StandardController extends RootController {
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

    @GetMapping("/standard/getType")
    public String getType() {
        return "getType";
    }

    @ResponseBody
    @PostMapping("/standard/getType")
    public String getType(HttpServletRequest request) throws Exception {
        String tokenId = request.getParameter("tokenId");

        Manager.setChaincodeId(getChaincodeId());
        return baseNFT.getType(tokenId);
    }


    @GetMapping("/standard/balanceOf")
    public String balanceOf() {
        return "balanceOf";
    }

    @PostMapping("/standard/balanceOf")
    @ResponseBody
    public String balanceOf(HttpServletRequest request) throws Exception {
        String ownerId = request.getParameter("ownerId");
        Map<String, Object> userMap = userDao.getOwnerKey(ownerId);
        String ownerKey = (String) userMap.get("ownerKey");

        Manager.setChaincodeId(getChaincodeId());
        long balance = erc721.balanceOf(ownerKey);
        return Long.toString(balance);
    }

    @GetMapping("/standard/ownerOf")
    public String ownerOf() {
        return "ownerOf";
    }

    @PostMapping("/standard/ownerOf")
    @ResponseBody
    public String ownerOf(HttpServletRequest request) throws Exception {
        String tokenId = request.getParameter("tokenId");
        Manager.setChaincodeId(getChaincodeId());
        String ownerKey = erc721.ownerOf(tokenId);

        Map<String, Object> userMap = userDao.getOwnerId(ownerKey);
        return (String) userMap.get("ownerId");
    }

    @GetMapping("/standard/transferFrom")
    public String transferFrom() {
        return "transferFrom";
    }

    @PostMapping("/standard/transferFrom")
    @ResponseBody
    public boolean transferFrom(HttpServletRequest request) throws Exception {
        String caller = request.getParameter("ownerKey");
        String from = request.getParameter("from");
        Map<String, Object> fromMap = userDao.getOwnerKey(from);
        String fromOwnerKey = (String) fromMap.get("ownerKey");

        String to = request.getParameter("to");
        Map<String, Object> toMap = userDao.getOwnerKey(to);
        String toOwnerKey = (String) toMap.get("ownerKey");

        String tokenId = request.getParameter("tokenId");

        Manager.setChaincodeId(getChaincodeId());
        Manager.setCaller(caller);
        return  erc721.transferFrom(fromOwnerKey, toOwnerKey, tokenId);
    }

    @GetMapping("/standard/approve")
    public String approve() {
        return "approve";
    }

    @PostMapping(value = "/standard/approve")
    @ResponseBody
    public boolean approve(HttpServletRequest request) throws Exception {
        String caller = request.getParameter("ownerKey");
        String approvedId = request.getParameter("approvedId");
        Map<String, Object> userMap = userDao.getOwnerKey(approvedId);
        String approvedOwnerKey = (String) userMap.get("ownerKey");
        String tokenId = request.getParameter("tokenId");

        Manager.setChaincodeId(getChaincodeId());
        Manager.setCaller(caller);
        return erc721.approve(approvedOwnerKey, tokenId);
    }

    @GetMapping("/standard/setApprovalForAll")
    public String setApprovalForAll() {
        return "setApprovalForAll";
    }

    @PostMapping("/standard/setApprovalForAll")
    @ResponseBody
    public boolean setApprovalForAll(HttpServletRequest request) throws Exception {
        String caller = request.getParameter("ownerKey");
        String operatorId = request.getParameter("operatorId");
        Map<String, Object> operatorMap = userDao.getOwnerKey(operatorId);
        String operatorOwnerKey = (String) operatorMap.get("ownerKey");
        boolean approved = Boolean.parseBoolean(request.getParameter("approved"));

        Manager.setChaincodeId(getChaincodeId());
        Manager.setCaller(caller);
        return erc721.setApprovalForAll(operatorOwnerKey, approved);
    }

    @GetMapping("/standard/getApproved")
    public String getApproved() {
        return "getApproved";
    }

    @PostMapping("/standard/getApproved")
    @ResponseBody
    public String getApproved(HttpServletRequest request) throws Exception {
        String tokenId = request.getParameter("tokenId");
        Manager.setChaincodeId(getChaincodeId());

        String approvedOwnerKey = erc721.getApproved(tokenId);
        Map<String, Object> approvedMap = userDao.getOwnerId(approvedOwnerKey);
        return (String) approvedMap.get("ownerId");
    }

    @GetMapping("/standard/isApprovedForAll")
    public String isApprovedForAll() {
        return "isApprovedForAll";
    }

    @PostMapping("/standard/isApprovedForAll")
    @ResponseBody
    public boolean isApprovedForAll(HttpServletRequest request) throws Exception {
        String ownerId = request.getParameter("ownerId");
        Map<String, Object> ownerMap = userDao.getOwnerKey(ownerId);
        String ownerOwnerKey = (String) ownerMap.get("ownerKey");

        String operatorId = request.getParameter("operatorId");
        Map<String, Object> operatorMap = userDao.getOwnerKey(operatorId);
        String operatorOwnerKey = (String) operatorMap.get("ownerKey");

        Manager.setChaincodeId(getChaincodeId());
        return erc721.isApprovedForAll(ownerOwnerKey, operatorOwnerKey);
    }
}
