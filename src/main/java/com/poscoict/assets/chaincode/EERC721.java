package com.poscoict.assets.chaincode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poscoict.posledger.chain.chaincode.executor.ChaincodeProxy;
import com.poscoict.posledger.chain.fabric.FabricService;
import com.poscoict.posledger.chain.model.ChaincodeRequest;
import org.hyperledger.fabric.sdk.ProposalResponse;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

@Resource
public class EERC721 {
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(EERC721.class);

    private static String chaincodeId = "assetscc";


    private static final String MINT_FUNCTION_NAME = "mint";

    private static final String BALANCE_OF_FUNCTION_NAME = "balanceOf";

    private static final String TOKEN_IDS_OF_FUNCTION_NAME = "tokenIdsOf";

    private static final String DIVIDE_FUNCTION_NAME = "divide";

    private static final String UPDATE_FUNCTION_NAME = "setXAttr";

    private static final String DEACTIVATE_FUNCTION_NAME = "deactivate";

    private static final String QUERY_FUNCTION_NAME = "query";

    private static final String QUERY_HISTORY_FUNCTION_NAME = "queryHistory";

    private static final String SUCCESS = "SUCCESS";

    private ChaincodeProxy chaincodeProxy;

    private ObjectMapper objectMapper;

    private FabricService fabricService;

    @Autowired
    private ERC721 erc721;

    public EERC721() {}

    public EERC721(ChaincodeProxy chaincodeProxy, ObjectMapper objectMapper) {
        this.chaincodeProxy = chaincodeProxy;
        this.objectMapper = objectMapper;
    }

    public EERC721(ChaincodeProxy chaincodeProxy, ObjectMapper objectMapper, FabricService fabricService) {
        this.chaincodeProxy = chaincodeProxy;
        this.objectMapper = objectMapper;
        this.fabricService = fabricService;
    }

    public EERC721(ChaincodeProxy chaincodeProxy) {
        this.chaincodeProxy = chaincodeProxy;
    }


    private String caller;

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public boolean mint(BigInteger tokenId, String type, String owner, int pages, String hash, String signers, String path, String merkleroot) throws Exception {
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

            chaincodeRequest.setArgs(new String[] { tokenId.toString(), type, owner, Integer.toString(pages), hash, signers, path, merkleroot });
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

    public BigInteger balanceOf(String owner, String type) throws Exception {
        logger.info("---------------- balanceOf SDK called ----------------");

        BigInteger balanceBigInt = null;

        try {
            ChaincodeRequest chaincodeRequest = new ChaincodeRequest();
            chaincodeRequest.setFunctionName(BALANCE_OF_FUNCTION_NAME);
            chaincodeRequest.setChaincodeName(chaincodeId);
            chaincodeRequest.setArgs(new String[] { owner, type });

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

    public List<BigInteger> tokenIdsOf(String owner) throws Exception {
        logger.info("---------------- tokenIdsOf SDK called ----------------");

        List<BigInteger> tokenIds = new ArrayList<>();
        String result = null;

        try {
            ChaincodeRequest chaincodeRequest = new ChaincodeRequest();
            chaincodeRequest.setFunctionName(TOKEN_IDS_OF_FUNCTION_NAME);
            chaincodeRequest.setChaincodeName(chaincodeId);
            chaincodeRequest.setArgs(new String[] { owner });

            Collection<ProposalResponse> responses = chaincodeProxy.queryByChainCode(chaincodeRequest);

            for (ProposalResponse response : responses) {
                if (response.getChaincodeActionResponsePayload() != null) {
                    result =  response.getMessage();
                }
            }

            result = result.substring(1);
            result = result.substring(0, result.length() - 1);

            String[] string = result.split(", ");
            BigInteger[] bigInt = new BigInteger[string.length];
            for (int i = 0; i < string.length; i++) {
                int n = Integer.parseInt(string[i]);
                bigInt[i] = BigInteger.valueOf(n);
            }
            tokenIds = Arrays.asList(bigInt);

        } catch (Exception e) {
            logger.error(e);
            throw new Exception(e.getLocalizedMessage());
        }

        logger.info("balance: " + result);
        return tokenIds;
    }

    public boolean deactivate(BigInteger tokenId) throws Exception {
        logger.info("---------------- deactivate SDK called ----------------");

        String status = null;
        boolean result = false;

        try {
            String owner = erc721.ownerOf(tokenId);
            if(!(caller.equals(owner) || erc721.isApprovedForAll(owner, caller))) {
                return false;
            }

            ChaincodeRequest chaincodeRequest = new ChaincodeRequest();
            chaincodeRequest.setFunctionName(DEACTIVATE_FUNCTION_NAME);
            chaincodeRequest.setChaincodeName(chaincodeId);

            chaincodeRequest.setArgs(new String[] { tokenId.toString() });
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

    public boolean divide(BigInteger tokenId, BigInteger[] newIds, String[] values, String index) throws Exception {
        logger.info("---------------- divide SDK called ----------------");

        String status = null;
        boolean result = false;

        try {
            String owner = erc721.ownerOf(tokenId);
            if(!(caller.equals(owner) || erc721.isApprovedForAll(owner, caller))) {
                return false;
            }

            ChaincodeRequest chaincodeRequest = new ChaincodeRequest();
            chaincodeRequest.setFunctionName(DIVIDE_FUNCTION_NAME);
            chaincodeRequest.setChaincodeName(chaincodeId);

            chaincodeRequest.setArgs(new String[] { tokenId.toString(),  Arrays.toString(newIds), Arrays.toString(values), index });
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

    public boolean update(BigInteger tokenId, String index, String attr) throws Exception {
        logger.info("---------------- update SDK called ----------------");

        String status = null;
        boolean result = false;

        try {
            String owner = erc721.ownerOf(tokenId);
            if(!(caller.equals(owner) || erc721.isApprovedForAll(owner, caller))) {
                return false;
            }

            ChaincodeRequest chaincodeRequest = new ChaincodeRequest();
            chaincodeRequest.setFunctionName(UPDATE_FUNCTION_NAME);
            chaincodeRequest.setChaincodeName(chaincodeId);

            chaincodeRequest.setArgs(new String[] { tokenId.toString(), index, attr });
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

    public String query(BigInteger tokenId) throws Exception {
        logger.info("---------------- query SDK called ----------------");

        String result = null;

        try {
            ChaincodeRequest chaincodeRequest = new ChaincodeRequest();
            chaincodeRequest.setFunctionName(QUERY_FUNCTION_NAME);
            chaincodeRequest.setChaincodeName(chaincodeId);
            chaincodeRequest.setArgs(new String[] { tokenId.toString() });

            Collection<ProposalResponse> responses = chaincodeProxy.queryByChainCode(chaincodeRequest);

            for (ProposalResponse response : responses) {
                if (response.getChaincodeActionResponsePayload() != null) {
                    result =  response.getMessage();
                }
            }

        } catch (Exception e) {
            logger.error(e);
            throw new Exception(e.getLocalizedMessage());
        }

        logger.info("query: " + result);
        return result;
    }

    public List<String> queryHistory(BigInteger tokenId) throws Exception {
        logger.info("---------------- queryHistory SDK called ----------------");

        List<String> histories = new ArrayList<>();
        String result = null;

        try {
            ChaincodeRequest chaincodeRequest = new ChaincodeRequest();
            chaincodeRequest.setFunctionName(QUERY_HISTORY_FUNCTION_NAME);
            chaincodeRequest.setChaincodeName(chaincodeId);
            chaincodeRequest.setArgs(new String[] { tokenId.toString() });

            Collection<ProposalResponse> responses = chaincodeProxy.queryByChainCode(chaincodeRequest);

            for (ProposalResponse response : responses) {
                if (response.getChaincodeActionResponsePayload() != null) {
                    result =  response.getMessage();
                }
            }

            result = result.substring(1);
            result = result.substring(0, result.length() - 1);

            histories = Arrays.asList(result.split(", "));
        } catch (Exception e) {
            logger.error(e);
            throw new Exception(e.getLocalizedMessage());
        }

        logger.info("query history: " + result);
        return histories;
    }
}
