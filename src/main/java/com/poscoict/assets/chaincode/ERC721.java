package com.poscoict.assets.chaincode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poscoict.posledger.chain.chaincode.executor.ChaincodeProxy;
import com.poscoict.posledger.chain.fabric.FabricService;
import com.poscoict.posledger.chain.model.ChaincodeRequest;
import org.hyperledger.fabric.sdk.ProposalResponse;
import java.math.BigInteger;
import java.util.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;

@Resource
public class ERC721 {
    private static final Logger logger = LogManager.getLogger(ERC721.class);

    private static String chaincodeId = "assetscc";

    private static final String MINT_FUNCTION_NAME = "mint";

    private static final String BALANCE_OF_FUNCTION_NAME = "balanceOf";

    private static final String OWNER_OF_FUNCTION_NAME = "ownerOf";

    private static final String TRANSFER_FROM_FUNCTION_NAME = "transferFrom";

    private static final String APPROVE_FUNCTION_NAME = "approve";

    private static final String SET_APPROVAL_FOR_ALL_FUNCTION_NAME = "setApprovalForAll";

    private static final String GET_APPROVED_FUNCTION_NAME = "getApproved";

    private static final String IS_APPROVED_FOR_ALL_FUNCTION_NAME = "isApprovedForAll";

    private static final String SUCCESS = "SUCCESS";

    private ChaincodeProxy chaincodeProxy;

    private ObjectMapper objectMapper;

    private FabricService fabricService;

    public ERC721() {}

    public ERC721(ChaincodeProxy chaincodeProxy, ObjectMapper objectMapper) {
        this.chaincodeProxy = chaincodeProxy;
        this.objectMapper = objectMapper;
    }

    public ERC721(ChaincodeProxy chaincodeProxy, ObjectMapper objectMapper, FabricService fabricService) {
        this.chaincodeProxy = chaincodeProxy;
        this.objectMapper = objectMapper;
        this.fabricService = fabricService;
    }

    public ERC721(ChaincodeProxy chaincodeProxy) {
        this.chaincodeProxy = chaincodeProxy;
    }


    private String caller;

    public void setCaller(String caller) {
        this.caller = caller;
    }


    public boolean mint(BigInteger tokenId, String owner) throws Exception {
        logger.info("---------------- mint SDK called ----------------");

        String status = null;
        boolean result = false;

        try {
            if (!caller.equals(owner)) {
                return false;
            }

            ChaincodeRequest chaincodeRequest = new ChaincodeRequest();
            chaincodeRequest.setFunctionName(MINT_FUNCTION_NAME);
            chaincodeRequest.setChaincodeName(chaincodeId);

            chaincodeRequest.setArgs(new String[] { tokenId.toString(), owner });
            Collection<ProposalResponse> responses = chaincodeProxy.sendTransaction(chaincodeRequest);

            for (ProposalResponse response : responses) {
                if (response.getChaincodeActionResponsePayload() != null) {
                    status = response.getStatus().toString();
                }
            }

            if (status != null && status.equals(SUCCESS)) {
                result = true;
            }

        } catch (Exception e) {
            logger.error(e);
            throw new Exception(e.getLocalizedMessage());
        }

        return result;
    }

    public BigInteger balanceOf(String owner) throws Exception {
        logger.info("---------------- balanceOf SDK called ----------------");

        BigInteger balanceBigInt = null;

        try {
            ChaincodeRequest chaincodeRequest = new ChaincodeRequest();
            chaincodeRequest.setFunctionName(BALANCE_OF_FUNCTION_NAME);
            chaincodeRequest.setChaincodeName(chaincodeId);
            chaincodeRequest.setArgs(new String[] { owner });

            Collection<ProposalResponse> responses = chaincodeProxy.queryByChainCode(chaincodeRequest);

            for (ProposalResponse response : responses) {
                if (response.getChaincodeActionResponsePayload() != null) {
                    String balance =  response.getMessage();
                    int balanceInt = Integer.parseInt(balance);
                    balanceBigInt = BigInteger.valueOf(balanceInt);
                }
            }

        } catch (Exception e) {
            logger.error(e);
            throw new Exception(e.getLocalizedMessage());
        }

        logger.info("balance: " + balanceBigInt.toString());
        return balanceBigInt;
    }

    public String ownerOf(BigInteger tokenId) throws Exception {
        logger.info("---------------- ownerOf SDK called ----------------");

        String owner = null;

        try {
            ChaincodeRequest chaincodeRequest = new ChaincodeRequest();
            chaincodeRequest.setFunctionName(OWNER_OF_FUNCTION_NAME);
            chaincodeRequest.setChaincodeName(chaincodeId);
            chaincodeRequest.setArgs(new String[] { tokenId.toString() });

            Collection<ProposalResponse> responses = chaincodeProxy.queryByChainCode(chaincodeRequest);

            for (ProposalResponse response : responses) {
                if (response.getChaincodeActionResponsePayload() != null) {
                    owner =  response.getMessage();
                }
            }

        } catch (Exception e) {
            logger.error(e);
            throw new Exception(e.getLocalizedMessage());
        }

        logger.info("owner: " + owner);
        return owner;
    }


    public boolean transferFrom(String from, String to, BigInteger tokenId) throws Exception {
        logger.info("---------------- transferFrom SDK called ----------------");

        String status = null;
        boolean result = false;
        try {
            String owner = ownerOf(tokenId);
            String approved = getApproved(tokenId);
            if(!(caller.equals(owner) || isApprovedForAll(owner, caller) || caller.equals(approved))) {
                return false;
            }

            ChaincodeRequest chaincodeRequest = new ChaincodeRequest();
            chaincodeRequest.setFunctionName(TRANSFER_FROM_FUNCTION_NAME);
            chaincodeRequest.setChaincodeName(chaincodeId);

            chaincodeRequest.setArgs(new String[] { from, to, tokenId.toString() });
            Collection<ProposalResponse> responses = chaincodeProxy.sendTransaction(chaincodeRequest);

            for (ProposalResponse response : responses) {
                if (response.getChaincodeActionResponsePayload() != null) {
                    status = (response.getStatus()).toString();
                }
            }

            if (status != null && status.equals(SUCCESS)) {
                result = true;
            }

        } catch (Exception e) {
            logger.error(e);
            throw new Exception(e.getLocalizedMessage());
        }

        return result;
    }

    public boolean approve(String approved, BigInteger tokenId) throws Exception {
        logger.info("---------------- approve SDK called ----------------");

        String status = null;
        boolean result = false;
        try {
            String owner = ownerOf(tokenId);
            if(!(caller.equals(owner) || isApprovedForAll(owner, caller))) {
                return false;
            }

            ChaincodeRequest chaincodeRequest = new ChaincodeRequest();
            chaincodeRequest.setFunctionName(APPROVE_FUNCTION_NAME);
            chaincodeRequest.setChaincodeName(chaincodeId);

            chaincodeRequest.setArgs(new String[] { approved, tokenId.toString() });
            Collection<ProposalResponse> responses = chaincodeProxy.sendTransaction(chaincodeRequest);

            for (ProposalResponse response : responses) {
                if (response.getChaincodeActionResponsePayload() != null) {
                    status = (response.getStatus()).toString();
                }
            }

            if (status != null && status.equals(SUCCESS)) {
                result = true;
            }

        } catch (Exception e) {
            logger.error(e);
            throw new Exception(e.getLocalizedMessage());
        }

        return result;
    }

    public boolean setApprovalForAll(String operator, boolean approved) throws Exception {
        logger.info("---------------- setApprovalForAll SDK called ----------------");

        String status = null;
        boolean result = false;
        try {
            if (caller.equals(operator)) {
                return false;
            }

            ChaincodeRequest chaincodeRequest = new ChaincodeRequest();
            chaincodeRequest.setFunctionName(SET_APPROVAL_FOR_ALL_FUNCTION_NAME);
            chaincodeRequest.setChaincodeName(chaincodeId);

            chaincodeRequest.setArgs(new String[] { caller, operator, Boolean.toString(approved) });
            Collection<ProposalResponse> responses = chaincodeProxy.sendTransaction(chaincodeRequest);

            for (ProposalResponse response : responses) {
                if (response.getChaincodeActionResponsePayload() != null) {
                    status = (response.getStatus()).toString();
                }
            }

            if (status != null && status.equals(SUCCESS)) {
                result = true;
            }

        } catch (Exception e) {
            logger.error(e);
            throw new Exception(e.getLocalizedMessage());
        }

        return result;
    }

    public String getApproved(BigInteger tokenId) throws Exception {
        logger.info("---------------- getApproved SDK called ----------------");

        String approved = null;

        try {
            ChaincodeRequest chaincodeRequest = new ChaincodeRequest();
            chaincodeRequest.setFunctionName(GET_APPROVED_FUNCTION_NAME);
            chaincodeRequest.setChaincodeName(chaincodeId);
            chaincodeRequest.setArgs(new String[] { tokenId.toString() });

            Collection<ProposalResponse> responses = chaincodeProxy.queryByChainCode(chaincodeRequest);

            for (ProposalResponse response : responses) {
                if (response.getChaincodeActionResponsePayload() != null) {
                    approved =  response.getMessage();
                }
            }

        } catch (Exception e) {
            logger.error(e);
            throw new Exception(e.getLocalizedMessage());
        }

        return approved;
    }

    public boolean isApprovedForAll(String owner, String operator) throws Exception {
        logger.info("---------------- isApprovedForAll SDK called ----------------");

        boolean result = false;

        try {
            ChaincodeRequest chaincodeRequest = new ChaincodeRequest();
            chaincodeRequest.setFunctionName(IS_APPROVED_FOR_ALL_FUNCTION_NAME);
            chaincodeRequest.setChaincodeName(chaincodeId);
            chaincodeRequest.setArgs(new String[] { owner, operator });

            Collection<ProposalResponse> responses = chaincodeProxy.queryByChainCode(chaincodeRequest);

            for (ProposalResponse response : responses) {
                if (response.getChaincodeActionResponsePayload() != null) {
                    result = Boolean.parseBoolean(response.getMessage());
                }
            }

        } catch (Exception e) {
            logger.error(e);
            throw new Exception(e.getLocalizedMessage());
        }

        return result;
    }

    public static void main(String args[])  {
        /*
         * main method is not used
         */
    }
}