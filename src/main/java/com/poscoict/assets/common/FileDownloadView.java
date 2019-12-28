package com.poscoict.assets.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.view.AbstractView;

public class FileDownloadView extends AbstractView {

	private static final Logger logger = LogManager.getLogger(FileDownloadView.class);
	
	public FileDownloadView() {
		setContentType("apllication/download; charset=utf-8");
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest req, HttpServletResponse res) throws Exception {
		File file = (File) model.get("downloadFile");
		res.setContentType(getContentType());
		res.setContentLength((int) file.length());
		res.setHeader("Content-Disposition", "attachment; filename=\"" + java.net.URLEncoder.encode(file.getName(), "utf-8") + "\";");
		res.setHeader("Content-Transfer-Encoding", "binary");
		OutputStream out = res.getOutputStream();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			FileCopyUtils.copy(fis, out);
		} catch (Exception e) {
			res.reset();
			res.setCharacterEncoding("UTF-8");
			res.setContentType("text/html; charset=utf-8");
			PrintWriter printwriter = res.getWriter();
			printwriter.println("<script>alert('파일을 다운로드 할 수 없습니다.'); history.go(-1);</script>");
			printwriter.flush();
			printwriter.close();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.info(e);
				}
			}
		}
		out.flush();
	}

}