package com.poscoict.assets.web.controller;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.poscoict.assets.model.UserDocVo;
import com.poscoict.assets.model.UserSigVo;
import com.poscoict.assets.persistence.*;
import com.poscoict.posledger.chain.assets.chaincode.extension.EERC721;
import com.poscoict.posledger.chain.assets.chaincode.extension.XNFT;
import com.poscoict.posledger.chain.assets.chaincode.standard.BaseNFT;
import com.poscoict.posledger.chain.assets.chaincode.standard.ERC721;
import com.poscoict.posledger.chain.assets.chaincode.util.Manager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poscoict.assets.exception.RestResourceException;
import com.poscoict.assets.model.UserVo;
import com.poscoict.assets.service.UserService;
import com.poscoict.assets.web.ExceptionHandleController;
import com.poscoict.assets.web.HttpResponse;
import com.poscoict.posledger.chain.DateUtil;
import com.poscoict.posledger.chain.sign.certificate.PosCertificateService;
import com.poscoict.posledger.chain.sign.model.PosCertificate;
import com.poscoict.posledger.chain.sign.model.PosCertificateMeta;
import com.poscoict.posledger.chain.wallet.chaincode.ERC20ChaincodeService;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.view.RedirectView;
import sun.misc.BASE64Decoder;

import java.awt.*;
import com.itextpdf.text.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;

@Controller
public class SignatureServiceController extends ExceptionHandleController {

	private static final Logger logger = LogManager.getLogger(SignatureServiceController.class);

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
	private ERC721 erc721;

	@Autowired
	private BaseNFT baseNFT;

	@Autowired
	private EERC721 eerc721;

	@Autowired
	private XNFT xnft;

	public static JdbcTemplate jdbcTemplate;

	@Autowired
	private UserDao userDao;
	@Autowired
	private SigDao sigDao;
	@Autowired
	private DocDao docDao;
	@Autowired
	private UserSigDao user_sigDao;
	@Autowired
	private UserDocDao user_docDao;
	@Autowired
	private TokenDao tokenDao;
//	@Autowired
//	public MerkleTree merkleTree;

	private String chaincodeId = "assetscc0";

	private int tokenId = 1;

	@PostMapping("/oauth/token")
	public RedirectView token(HttpServletRequest req, MultipartHttpServletRequest mre) throws Exception {

		String certiPassword = req.getParameter("certiPassword");
		MultipartFile certfile = mre.getFile("certfile");


		if (certfile.isEmpty()) {
			throw new RestResourceException("첨부 인증서 정보가 없습니다.");
		}

		PosCertificate posCertificate = null;

		try {
			posCertificate = (PosCertificate) objectMapper.readValue(certfile.getBytes(), new TypeReference<PosCertificate>() {
			});
		} catch (Exception e) {
			logger.error(e);
			throw new RestResourceException("유효하지 않은 인증서 형식입니다.");
		}

		// 인증서 비밀번호 검증
		boolean result = false;

		try {
			result = posCertificateService.verifyPosCertificatePassword(posCertificate, certiPassword);
		} catch (Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMeta = new PosCertificateMeta();

		if (result) {

			try {
				posCertificateMeta = posCertificateService.getMobilePosCertificateMeta(posCertificate, certiPassword, message.getMessage("application.posledger.challenge.domain"));
			} catch (Exception e) {
				logger.error(e);
				throw new RestResourceException(e.getLocalizedMessage());
			}

			if (posCertificateMeta == null) {
				throw new RestResourceException("블록체인에 저장된 인증서 정보가 없습니다.");
			}

			try {
				UserVo user = new UserVo();

				user.setUserId(posCertificateMeta.getOwnerId());
				user.setOrgCode(posCertificateMeta.getOrgCode());
				user.setUserType(posCertificateMeta.getOwnerType());
				user.setCertAddress(posCertificate.getAddress());
				user.setDeviceAddress(posCertificateMeta.getDevices());
				user.setPushToken("");
				user.setRegistDate(DateUtil.getDateObject());

				//userService.createUser(user);

				// 사용자 세션 저장
				req.getSession().setAttribute("sessionUser", posCertificateMeta.getOwnerKey());
				req.getSession().setAttribute("joinUserId", posCertificateMeta.getOwnerId());
				req.getSession().setAttribute("joinUserOrgCode", posCertificateMeta.getOrgCode());

				if(posCertificateMeta.getOwnerId().equals("ADMIN")) {
					return new RedirectView("/admin");
				}

				SqlRowSet srs = null;
				srs = userDao.getUserByUserId(posCertificateMeta.getOwnerId());
				if(!srs.next())
					userDao.insert(posCertificateMeta.getOwnerKey(), posCertificateMeta.getOwnerId());
				//user_sigDao.insert("1FbLcUY39EmYSjtxjpHSVKEeUZQNKAvooa", 1);

			} catch (Exception e) {
				logger.error(e);
				throw new RestResourceException(e.getLocalizedMessage());
			}
		}

		return new RedirectView("/main");
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@ResponseBody
	public String login() {

		logger.info("login ####################");
		return "login";
	}

	@GetMapping("/welcome")
	public String welcome() {

		logger.info("welcome ####################");
		return "welcome";
	}

	@GetMapping("/main")
	public String main() {

		logger.info("main ####################");
		return "main";
	}

	@GetMapping("/addUser")
	public String addUser() {

		logger.info("addUser ####################");
		return "addUser";
	}

	@GetMapping("/")
	public String rootIndex() {

		logger.info("index ####################");
		return "index";
	}

	@GetMapping("/index")
	public String index() {

		logger.info("index ####################");
		return "index";
	}

	@GetMapping("/admin")
	public String admin() {

		logger.info("admin ####################");
		return "admin";
	}

	@GetMapping("/signUpForm")
	public String signUpForm() {

		logger.info("signUpForm ####################");
		return "signUpForm";
	}

	@ResponseBody
	@RequestMapping("/img")
	public RedirectView img (HttpServletRequest req) throws Exception {

		String owner = req.getParameter("owner");
		String signer = req.getParameter("signer");
		String strImg = req.getParameter("strImg");

		logger.info(" > " + owner);
		logger.info(" > " + signer);
		logger.info(" > " + strImg);

		String folder = req.getServletContext().getRealPath("/");// + uploadpath;
		String fullpath = "";
		String[] strParts = strImg.split(",");
		String rstStrImg = strParts[1];
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");
		String filenm = sdf.format(new Date()).toString() + "_" + signer;

		BufferedImage image = null;
		byte[] byteImg;

		/*
		 * create sig image
		 */
		BASE64Decoder decoder = new BASE64Decoder();
		byteImg = decoder.decodeBuffer(rstStrImg);
		ByteArrayInputStream bis = new ByteArrayInputStream(byteImg);
		image = ImageIO.read(bis);

		// image resize
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		int newWidth = 200;
		int newHeight = 50;
		double  widthtRatio = (double)newWidth/(double)imageWidth;
		double heightRatio = (double)newHeight/(double)imageHeight;
		int w = (int)(imageWidth * widthtRatio);
		int h = (int)(imageWidth * heightRatio);

		java.awt.Image resizeImage = image.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
		BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = newImage.getGraphics();
		g.drawImage(resizeImage, 0, 0, null);
		g.dispose();
		bis.close();

		fullpath = folder + filenm;
		File folderObj = new File(folder);
		if(!folderObj.isDirectory())
			folderObj.mkdir();
		File outputFile = new File(fullpath);
		if(outputFile.exists())
			outputFile.delete();
		ImageIO.write(newImage, "png", outputFile);
		logger.info("fullpath >>> " + fullpath);

		/*
		 * create image hash
		 */
		String sigId = "";
		int buff = 16384;
		try {
			RandomAccessFile file = new RandomAccessFile(fullpath, "r");

			MessageDigest hashSum = MessageDigest.getInstance("SHA-256");

			byte[] buffer = new byte[buff];
			byte[] partialHash = null;

			long read = 0;

			// calculate the hash of the hole file for the test
			long offset = file.length();
			int unitsize;
			while (read < offset) {
				unitsize = (int) (((offset - read) >= buff) ? buff : (offset - read));
				file.read(buffer, 0, unitsize);

				hashSum.update(buffer, 0, unitsize);

				read += unitsize;
			}

			file.close();
			partialHash = new byte[hashSum.getDigestLength()];
			partialHash = hashSum.digest();

			StringBuffer sb = new StringBuffer();
			for(int i = 0 ; i < partialHash.length ; i++){
				sb.append(Integer.toString((partialHash[i]&0xff) + 0x100, 16).substring(1));
			}
			sigId = sb.toString();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		logger.info(sigId);

		//String owner = signer;

		// insert tokenId into DB
		int tokenNum;
		Map<String, Object> testMapForToken = tokenDao.getTokenNum();
		tokenDao.insert(tokenId);

		// insert sig's info into DB
		sigDao.insert(sigId, filenm, tokenId-1);
		Map<String, Object> testMap = sigDao.getSigBySigid(sigId);
		int sigNum = (int)testMap.get("sigNum");

		// insert key for user and sig into DB
		user_sigDao.insert(signer, sigNum);

		// create merkleRoot for off-chain data verification
		String merkleLeaf[] = new String[3];
		merkleLeaf[0] = sigId;
		merkleLeaf[1] = filenm;
		merkleLeaf[2] = valueOf(tokenId-1);

//		String merkleRoot = merkleTree.merkleRoot(merkleLeaf, 0, merkleLeaf.length-1);
//		logger.info(merkleRoot);

		// mint SigNFT
		//mintSigNFT mintNFT = new mintSigNFT();
		//mintNFT.mint(tokenNum, owner, sigId, filenm, merkleRoot);

		Manager.setChaincodeId(chaincodeId);
		Manager.setCaller(owner);

		Map<String, Object> xattr = new HashMap<>();

		String hash = sigId;
		String path = fullpath;
		String merkleroot = sigId;

		xattr.put("hash", hash);

		Map<String, String> uri = new HashMap<>();
		uri.put("path", path);
		uri.put("hash", merkleroot);


		xnft.mint(valueOf(tokenId-1), "sig", owner, xattr, uri);
		tokenId++;

		return new RedirectView("main");
	}

	@GetMapping("/mysign")
	public String mysign(HttpServletRequest req, Model model) throws Exception{

		String ownerKey = req.getParameter("ownerKey");
		String sigId = "";

		Map<String, Object> testMap;// = (user_sigDao.getUserSig(userId));

		/*
		sigId = (String)testMap.get("sigid");
		model.addAttribute("sigId", sigId);
		*/


		/*
		 * check my signature images
		 */
		List<UserSigVo> user_sig = user_sigDao.listForBeanPropertyRowMapper(ownerKey);
		if(user_sig.size() > 0) {
			logger.info(valueOf(user_sig.get(0).getUserid()));
			String pathList[] = new String[user_sig.size()];

			for (int i = 0; i < user_sig.size(); i++) {
				testMap = sigDao.getSigBySigNum(user_sig.get(i).getSignum());
				pathList[i] = (String) testMap.get("path");

			}

			model.addAttribute("path", pathList);
		}

		return "mysign";
	}

	@ResponseBody
	@PostMapping("/upload")
	public RedirectView upload(HttpServletRequest req, MultipartHttpServletRequest mre) throws IllegalStateException, IOException, Exception{

		String owner = req.getParameter("ownerKey");
		String count = req.getParameter("count");
		String[] user = null;

		// if signers for document are not only one
		if(!count.equals("")) {

			Map<String, Object> testMap = null;
			user = new String[parseInt(count)];
			for(int i=0; i<user.length; i++) {
				try {
					testMap = userDao.getUser(req.getParameter("ID"+i));
				} catch (RuntimeException e) {
					if(testMap == null)
						return new RedirectView("main");
				}
				user[i] = (req.getParameter("ID"+i));
				logger.info(user[i]);
			}
		}

		logger.info(owner);

		Document document = new Document(PageSize.A4);
		MultipartFile mf = mre.getFile("file");
		MultipartFile mf2 = mre.getFile("file");

		if(mf.getSize() != 0)
			logger.info("failure");

		String uploadPath = "";
		String path = "";
		//String original = mf.getOriginalFilename();
		String original = "";
		File convFile = null;
		InputStream is = null;

		try {

			/*
			 * getting hash of document
			 */
			final MessageDigest md = MessageDigest.getInstance("SHA-512");

			//	RandomAccessFile file = new RandomAccessFile("/home/yoongdoo0819/dSignature-server/"+mf.getOriginalFilename(), "r");
			logger.info(mf.getOriginalFilename());

			convFile = new File("C:\\Users\\Administrator\\Desktop\\temp\\posledger-assets-dapp\\target\\assets\\"+mf.getOriginalFilename());
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);	// absolute path needed
			fos.write(mf.getBytes());
			fos.close();

			is = new FileInputStream(convFile);	// absolute path needed
			byte[] buffer = new byte[1024];
			int readBytes = 0;

			while ((readBytes = is.read(buffer)) > -1) {
				md.update(buffer, 0, readBytes);
			}

			StringBuilder builder = new StringBuilder();
			byte[] digest = md.digest();
			for(byte b : digest) {
				builder.append(Integer.toHexString(0xff & b));
			}

			// hash of document
			original = builder.toString();


		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		logger.info(original);

		uploadPath = path + mf.getOriginalFilename();//+ original;
		logger.info(uploadPath);


		// insert tokenId into DB
		tokenDao.insert(tokenId);

		// insert document's info into DB
		docDao.insert(original, mf.getOriginalFilename(), tokenId-1, owner);

		// insert key for user and document into DB
		int docNum;
		Map<String, Object> testMap = docDao.getDocNum();
		docNum = parseInt(String.valueOf(testMap.get("auto_increment")));

		user_docDao.insert(owner, --docNum);

		// create merkleRoot for off-chain data verification
		String merkleLeaf[] = new String[4];
		merkleLeaf[0] = original;
		merkleLeaf[1] = mf.getOriginalFilename();
		merkleLeaf[2] = valueOf(tokenId-1);

		String merkleRoot = "HASH";//MerkleTree.merkleRoot(merkleLeaf, 0, merkleLeaf.length-1);
		logger.info(merkleRoot);

		Manager.setChaincodeId(chaincodeId);
		Manager.setCaller(owner);

		Map<String, Object> xattr = new HashMap<>();
		int pages = 100;
		String hash = "c35b21d6ca39aa7cc3b79a705d989f1a6e88b99ab43988d74048799e3db926a3";
		List<String> signers = new ArrayList<>();
		signers.add(owner);

		// if signers for document are not only one
		if(user != null) {
			Map<String, Object> userMap;
			for(int i=0; i<user.length; i++) {
				userMap = userDao.getUser(user[i]);
				user_docDao.insert((String)userMap.get("ownerKey"), docNum);
				signers.add((String)userMap.get("ownerKey"));
			}
		}

		xattr.put("pages", pages);
		xattr.put("hash", hash);
		xattr.put("signers", signers);

		Map<String, String> uri = new HashMap<>();
		path = mf.getOriginalFilename();
		String merkleroot = "558ad18828f6da6d471cdb1a3443f039a770e03617f163896980d914d643e4bc";

		uri.put("path", path);
		uri.put("hash", merkleroot);

		String type = "doc";
		boolean result = xnft.mint(valueOf(tokenId-1), type, owner, xattr, uri);
		tokenId++;
		return new RedirectView("main"); //null;//"redirect:/main";
	}

	@GetMapping("/mydoclist")
	public String mydoclist(HttpServletRequest req, Model model) throws Exception{

		String ownerKey = req.getParameter("ownerKey");
		String docId[];
		String docPath[];
		String docNum[];
		String tokenId[];
		String sigId = "";
		String queryResult = null;
		String signersResult = "";
		String XAttr;
		String sigStatus[];
		int sigNum;

		//queryNFT querynft = new queryNFT();

		List<UserDocVo> docList = user_docDao.listForBeanPropertyRowMapper(ownerKey);
		docId = new String[docList.size()];
		docNum = new String[docList.size()];
		docPath = new String[docList.size()];
		tokenId = new String[docList.size()];
		sigStatus = new String[docList.size()];

		Manager.setChaincodeId(chaincodeId);

		for(int i=0; i<docList.size(); i++) {

			Map<String, Object> testMap = docDao.getDocByDocNum(docList.get(i).getDocnum());
			docId[i] = (String)testMap.get("docid");
			docNum[i] = valueOf(testMap.get("docnum"));
			docPath[i] = (String)testMap.get("path");
			tokenId[i] = valueOf(testMap.get("doctokenid"));

			logger.info("tokenId >>>>> " + tokenId[i]);
			String result = eerc721.query(tokenId[i]);

			logger.info(result);
			if(result != null) {
				Map<String, Object> map =
						objectMapper.readValue(result, new TypeReference<HashMap<String, Object>>() {});

				Map<String, Object> xattr = (HashMap<String, Object>) map.get("xattr");
				List<String> signers = (ArrayList<String>) xattr.get("signers");
				List<String> signatures = (ArrayList<String>) xattr.get("signatures");

				logger.info(signers.get(0));
				logger.info(valueOf(signers.size()));
				logger.info(valueOf(signatures.size()));
				if(signers.size() == signatures.size())
					sigStatus[i] = "true";
				else
					sigStatus[i] = "false";
			}
		}

		model.addAttribute("docIdList", docId);
		model.addAttribute("docNumList", docNum);
		model.addAttribute("docPathList", docPath);
		model.addAttribute("tokenIdList", tokenId);
		model.addAttribute("ownerKey", ownerKey);
		model.addAttribute("sigStatus", sigStatus);

		return "myDocList";
	}

	@GetMapping("/mydoc")
	public String mydoc(HttpServletRequest req, Model model) throws Exception{

		String ownerKey = req.getParameter("ownerKey");
		String docId = req.getParameter("docid");
		int tokenId = parseInt(req.getParameter("tokenid"));
		int docNum = parseInt(req.getParameter("docnum"));
		String docPath = "";
		String sigId = null;

		/*
		 * get my document
		 */
		Map<String, Object> docTestMap = docDao.getDocByDocIdAndNum(docId, docNum);
		docPath = (String) docTestMap.get("path");
		model.addAttribute("docPath", docPath);

		logger.info("#######################################" + ownerKey);

		/*
		 * get my signature image
		 */
		Map<String, Object> sigTestMap;// = (user_sigDao.getUserSig(userId));
		List<UserSigVo> user_sig = user_sigDao.listForBeanPropertyRowMapper(ownerKey);

		for (int i = 0; i < user_sig.size(); i++) {
			sigTestMap = sigDao.getSigBySigNum(user_sig.get(i).getSignum());
			sigId = (String) sigTestMap.get("sigid");    // only one sigId
		}

		model.addAttribute("docNum", docNum);
		model.addAttribute("docId", docId);
		model.addAttribute("tokenId", tokenId);
		model.addAttribute("sigId", sigId);

		return "mydoc";
	}

	@ResponseBody
	@RequestMapping("/checkInfo")
	public String checkInfo (/*@RequestBody String test,*/ HttpServletRequest req, String tokenId) throws Exception {

		logger.info(" > " + tokenId);
		//String uploadpath="uploadfile\\";

		String ownerKey="";
		String queryResult = null;
		String result = "";
		String XAttr;
		String signers="";
		String tokenIds;
		String owner="";
		String hash="";
		//String signersArray[];
		String tokenIdsArray[];
		int sigNum;


		/*
		 * get document info from blockchain
		 */
		//queryNFT querynft = new queryNFT();
		//queryResult = querynft.query(tokenId);

		Manager.setChaincodeId(chaincodeId);

		queryResult = eerc721.query(tokenId);
		logger.info("tokenId >>> " + tokenId);
		logger.info(queryResult);

		if(queryResult != null) {
			Map<String, Object> map =
					objectMapper.readValue(queryResult, new TypeReference<HashMap<String, Object>>() {});

			ownerKey = (String)map.get("owner");
			Map<String, Object> xattr = (HashMap<String, Object>) map.get("xattr");
			//List<String> signers = (ArrayList<String>) xattr.get("signers");
			List<String> signatures = (ArrayList<String>) xattr.get("signatures");

			for(int i=0; i<signatures.size(); i++) {
				signers += signatures.get(i);
				if (i + 1 < signatures.size())
					signers += ", ";
			}
			hash = (String) xattr.get("hash");
		}


		result += "owner : " + ownerKey + "\n";
		result += "hash : " + hash + "\n";
		result += "signatures : " + signers;

		logger.info(queryResult);
		return result;
	}

	@ResponseBody
	@RequestMapping("/doSign")
	public RedirectView doSign(HttpServletRequest req, Model model) throws Exception{

		int docNum = parseInt(String.valueOf(req.getParameter("docNum")));
		String docId = req.getParameter("docId");
		String sigId = req.getParameter("sigId");
		String signer = req.getParameter("signer");
		String docTokenId = req.getParameter("tokenId");
		String signature="";

		List<UserSigVo> user_sig = user_sigDao.listForBeanPropertyRowMapper(signer);
		if(user_sig.size() == 0)
			return new RedirectView("main");

		/*
		 * sign the document
		 */
		Map<String, Object> testMap = sigDao.getSigBySigid(sigId);
		String sigTokenId = valueOf((int)testMap.get("sigtokenid"));

		String index = "signatures";

		Map<String, Object> sigTestMap;// = (user_sigDao.getUserSig(userId));
		for (int i = 0; i < user_sig.size(); i++) {
			sigTestMap = sigDao.getSigBySigNum(user_sig.get(i).getSignum());
			signature = (String) sigTestMap.get("sigid");    // only one sigId
		}

		List<String> signatures = new ArrayList<>();
		signatures.add(signature);
		String attr = signatures.toString();

		Manager.setChaincodeId(chaincodeId);
		Manager.setCaller(signer);
		boolean result = eerc721.update(docTokenId, index, attr);

		return new RedirectView("main");
	}

	@ResponseBody
	@RequestMapping("/checkStatus")
	public String[] checkStatus (/*@RequestBody String test,*/ HttpServletRequest req, String tokenId) throws Exception {

		logger.info(" > " + tokenId);
		//String uploadpath="uploadfile\\";

		int numOfProperty = 3;
		String queryResult = null;
		String signersResult[] = new String[numOfProperty];
		String XAttr;
		String tokenIds;
		//String signersArray[];
		//String tokenIdsArray[];
		int sigNum;

		//queryNFT querynft = new queryNFT();
		//queryResult = querynft.query(tokenId);

		for(int i=0; i<signersResult.length; i++) {
			signersResult[i] = "";
		}
		signersResult[0] = "All participants : ";
		signersResult[1] = "Current signatures : ";

		/*
		 * check current signing status for the document
		 */
		Manager.setChaincodeId(chaincodeId);

		queryResult = eerc721.query(tokenId);
		logger.info("tokenId >>> " + tokenId);
		logger.info(queryResult);

		if(queryResult != null) {
			Map<String, Object> map =
					objectMapper.readValue(queryResult, new TypeReference<HashMap<String, Object>>() {});


			Map<String, Object> xattr = (HashMap<String, Object>) map.get("xattr");
			List<String> signers = (ArrayList<String>) xattr.get("signers");
			List<String> signatures = (ArrayList<String>) xattr.get("signatures");


			/*
			 * All participants
			 */
			for(int i=0; i<signatures.size(); i++) {
				signersResult[0] += signatures.get(i);
				if (i + 1 < signatures.size())
					signersResult[0] += " - ";
			}

			if (signers.size() == signatures.size())
				signersResult[2] = "true";
			else
				signersResult[2] = "false";

			/*
			 * who are current signers?
			 */
			Map<String, Object> sigTestMap;
			Map<String, Object> user_sigTestMap;
			if (signatures != null) {
				for (int i = 0; i < signatures.size(); i++) {
					sigTestMap = sigDao.getSigBySigid((String)signatures.get(i));
					sigNum = (int) sigTestMap.get("signum");
					logger.info("sigNum " + valueOf(sigNum) );

					user_sigTestMap = user_sigDao.getUserid(sigNum);
					signersResult[1] += (String) user_sigTestMap.get("ownerKey");
					if(i+1 < signatures.size()) {
						signersResult[1] += " - ";
					}
				}
			}
		}

		logger.info(queryResult);
		return signersResult;
	}

	@GetMapping("/queryDoc")
	public String queryDoc(HttpServletRequest req, Model model) throws Exception{

		//String userId = req.getParameter("userid");
		String docId = req.getParameter("docid");
		int docNum = parseInt(req.getParameter("docnum"));
		String docPath = "";
		int signum;
		String userId[];
		String sigPathList[];
		String queryResult="";
		String tokenId = req.getParameter("tokenid");
		List<String> signers = null;
		String XAttr = "";
		//model.addAttribute("docList", user_docDao.listForBeanPropertyRowMapper(docId));

		Map<String, Object> docTestMap = docDao.getDocByDocIdAndNum(docId, docNum);
		List<UserDocVo> userList = user_docDao.listForBeanPropertyRowMapperByDocNum((int)docTestMap.get("docnum"));

		sigPathList = new String[userList.size()];
		userId = new String[userList.size()];

		//queryNFT querynft = new queryNFT();
		//queryResult = querynft.query(tokenId);

		Manager.setChaincodeId(chaincodeId);

		queryResult = eerc721.query(tokenId);
		logger.info("tokenId >>> " + tokenId);
		logger.info(queryResult);

		if(queryResult != null) {
			Map<String, Object> map =
					objectMapper.readValue(queryResult, new TypeReference<HashMap<String, Object>>() {
					});


			Map<String, Object> xattr = (HashMap<String, Object>) map.get("xattr");
			signers = (ArrayList<String>) xattr.get("signers");
			List<String> signatures = (ArrayList<String>) xattr.get("signatures");


			// get signature paths for signers
			Map<String, Object> sigTestMap;
			if(signatures != null) {
				for (int i = 0; i < signatures.size(); i++) {
					sigTestMap = sigDao.getSigBySigid((String)signatures.get(i));
					sigPathList[i] = (String) sigTestMap.get("path");

				}
			}

			// get document path
			docPath = (String) docTestMap.get("path");
		}

		Document document = new Document(PageSize.A4);
		try {

			// existing pdf
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("C:\\Users\\Administrator\\Desktop\\temp\\posledger-assets-dapp\\target\\assets\\final.pdf")); // absolute path needed
			document.open();
			PdfContentByte cb = writer.getDirectContent();

			// Load existing PDF
			PdfReader reader = new PdfReader(docPath);	// absolute path needed
			for(int i=1; i<=reader.getNumberOfPages(); i++) {
				PdfImportedPage page = writer.getImportedPage(reader, i);

				// Copy first page of existing PDF into output PDF
				document.newPage();
				cb.addTemplate(page, 0, 0);

			}

			// Add your new data / text here
			// for example...
			Paragraph title1 = new Paragraph("Signatures");

			Chapter chapter1 = new Chapter(title1, 1);
			chapter1.setNumberDepth(0);

			Section section[] = new Section[sigPathList.length];
			File f;

			/*
			 * insert signatures into PDF document
			 */
			for(int i=0; i<sigPathList.length; i++) {
				section[i] = chapter1.addSection(new Paragraph(signers.get(i)));
				f = new File("C:\\Users\\Administrator\\Desktop\\temp\\posledger-assets-dapp\\target\\assets\\"+sigPathList[i]);    // absolute path needed

				if(f.isFile()) {
					Image section1Image = Image.getInstance("C:\\Users\\Administrator\\Desktop\\temp\\posledger-assets-dapp\\target\\assets\\"+sigPathList[i]);   // absolute path needed
					section[i].add(section1Image);
				}
			}

			document.add(chapter1);
			document.close();


		} catch (RuntimeException e) {

		}

		model.addAttribute("finalDocPath", "final.pdf");
		return "finalDoc";
	}

	/**
	 * 회원 가입
	 *
	 * @return HttpResponse
	 * @throws RestResourceException
	 */
	@RequestMapping(value = "/member/register/action", method = RequestMethod.POST)
	@ResponseBody
	public HttpResponse joinUser(@RequestParam(value = "pushToken", required = false) String pushToken,
								 @RequestParam(value = "userType", required = false) String userType,
								 @RequestParam(value = "epPassword", required = false) String epPassword,
								 @RequestParam(value = "certiPassword", required = false) String certiPassword,
								 @RequestParam("certfile") MultipartFile certfile, HttpServletRequest request) throws RestResourceException {

		if (certfile.isEmpty()) {
			throw new RestResourceException("첨부 인증서 정보가 없습니다.");
		}

		PosCertificate posCertificate = null;

		try {
			posCertificate = (PosCertificate) objectMapper.readValue(certfile.getBytes(), new TypeReference<PosCertificate>() {
			});
		} catch (Exception e) {
			logger.error(e);
			throw new RestResourceException("유효하지 않은 인증서 형식입니다.");
		}

		// 인증서 비밀번호 검증
		boolean result = false;

		try {
			result = posCertificateService.verifyPosCertificatePassword(posCertificate, certiPassword);
		} catch (Exception e) {
			logger.error(e);
			throw new RestResourceException("인증서 비밀번호를 확인해주세요.");
		}

		PosCertificateMeta posCertificateMeta = new PosCertificateMeta();

		if (result) {

			try {
				posCertificateMeta = posCertificateService.getMobilePosCertificateMeta(posCertificate, certiPassword, message.getMessage("application.posledger.challenge.domain"));
			} catch (Exception e) {
				logger.error(e);
				throw new RestResourceException(e.getLocalizedMessage());
			}

			if (posCertificateMeta == null) {
				throw new RestResourceException("블록체인에 저장된 인증서 정보가 없습니다.");
			}

			try {
				UserVo certUser = userService.getUserByCertAddress(posCertificate.getAddress());

				if (certUser != null) throw new RestResourceException("이미 등록된 인증서입니다.");

				//wallet create 체인코드 실행
				boolean createWallet = erc20ChaincodeService.createWallet(posCertificateMeta);

				if (createWallet == true) {
					UserVo user = new UserVo();

					user.setUserId(posCertificateMeta.getOwnerId());
					user.setOrgCode(posCertificateMeta.getOrgCode());
					user.setUserType(posCertificateMeta.getOwnerType());
					user.setCertAddress(posCertificate.getAddress());
					user.setDeviceAddress(posCertificateMeta.getDevices());
					user.setPushToken(pushToken);
					user.setRegistDate(DateUtil.getDateObject());

					userService.createUser(user);

					// 사용자 세션 저장
					request.getSession().setAttribute("joinUserId", posCertificateMeta.getOwnerId());
					request.getSession().setAttribute("joinUserOrgCode", posCertificateMeta.getOrgCode());

				} else {
					throw new RestResourceException("블록체인에 정상적으로 저장되지 않았습니다.");
				}

			} catch (Exception e) {
				logger.error(e);
				throw new RestResourceException(e.getLocalizedMessage());
			}
		}

		return new HttpResponse(HttpResponse.success, posCertificateMeta.getOwnerId());
	}
}