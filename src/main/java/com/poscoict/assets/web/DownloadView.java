package com.poscoict.assets.web;

import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.AbstractView;

public class DownloadView extends AbstractView {
	
	private static final Logger logger = LoggerFactory.getLogger(DownloadView.class);
	
	public DownloadView() {
		setContentType("application/octet-stream; utf-8");
	}
	
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String source = (String) model.get("source");
		
		response.setContentType(getContentType());
        response.setContentLength((int) source.length());

        String fileName = (String) model.get("fileName");
        
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
        response.setHeader("Content-Transfer-Encoding", "binary");
         
        OutputStream out = response.getOutputStream();
        
        try {
        	out.write(source.getBytes());
        } catch (Exception e) {
            logger.error("downloadView error: {}", e);
        } finally {
        	out.flush();
        	out.close();
        }
	}
}
