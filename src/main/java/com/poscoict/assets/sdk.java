package com.poscoict.assets;

import com.poscoict.assets.model.UserSigVo;
import com.poscoict.posledger.chain.assets.chaincode.extension.XNFT;
import com.poscoict.posledger.chain.assets.chaincode.util.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;

public class sdk {
//    @Autowired
//    private XNFT xnft;
//
//    private List<String> toList(String string) {
//        return new ArrayList<>(Arrays.asList(string.substring(1, string.length() - 1).split(", ")));
//    }
//
//    public boolean sign(String sigId, String signer, String signature) {
//        List<String> signers = toList(xnft.getXAttr(docTokenId, "signers"));
//        String caller = Manager.getCaller();
//        boolean isSigner = false;
//
//        for (String s: signers) {
//            if (caller.equals(s)) {
//                isSigner = true;
//                break;;
//            }
//        }
//
//        if (!isSigner) {
//            return false;
//        }
//        List<UserSigVo> user_sig = user_sigDao.listForBeanPropertyRowMapper(signer);
//        if(user_sig.size() == 0)
//            return new RedirectView("main");
//
//        /*
//         * sign the document
//         */
//        Map<String, Object> testMap = sigDao.getSigBySigid(sigId);
//        String sigTokenId = valueOf((int)testMap.get("sigtokenid"));
//
//        String index = "signatures";
//
//        Map<String, Object> sigTestMap;// = (user_sigDao.getUserSig(userId));
//        for (int i = 0; i < user_sig.size(); i++) {
//            sigTestMap = sigDao.getSigBySigNum(user_sig.get(i).getSignum());
//            signature = (String) sigTestMap.get("sigid");    // only one sigId
//        }
//
//        List<String> signatures = new ArrayList<>();
//        signatures.add(signature);
//        String attr = signatures.toString();
//
//        Manager.setChaincodeId(chaincodeId);
//        Manager.setCaller(signer);
//        boolean result = xnft.setXAttr(docTokenId, index, attr);
//    }
}
