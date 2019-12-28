package com.poscoict.assets.util.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class PagingTag extends BodyTagSupport {

	private static final long serialVersionUID = -9191819044073858485L;

	/**
	 * 전체 카운트
	 */
	private int totalCount;

	/**
	 * 페이지 번호
	 */
	private int page;

	/**
	 * 페이지 갯수
	 */
	private int pageCount;

	/**
	 * 목록 갯수
	 */
	private int listCount = 10;

	private String pageUrl;

	/**
	 * @return the totalCount
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * @param totalCount the totalCount to set
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * @return the page
	 */
	public int getPage() {
		return page;
	}

	/**
	 * @param page the page to set
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * @return the pageCount
	 */
	public int getPageCount() {
		return pageCount;
	}

	/**
	 * @param pageCount the pageCount to set
	 */
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	/**
	 * @return the listCount
	 */
	public int getListCount() {
		return listCount;
	}

	/**
	 * @param listCount the listCount to set
	 */
	public void setListCount(int listCount) {
		this.listCount = listCount;
	}

	/**
	 * @return the pageUrl
	 */
	public String getPageUrl() {
		return pageUrl;
	}

	/**
	 * @param pageUrl the pageUrl to set
	 */
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public int doStartTag() throws JspTagException {

		StringBuffer results = new StringBuffer();

		try {
			// 첫 페이지
			results.append("<li class=\"\"><a href=\"").append(pageUrl).append("1").append("\" title=\"맨처음\">&lt;&lt;</a></li>");

			int lastPage = (totalCount - 1) / listCount + 1;
			int startPage = (((page - 1) / pageCount) * pageCount) + 1;
			
			// 이전 그룹
			if (startPage > pageCount) {
				results.append("<li class=\"\"><a href=\"").append(pageUrl).append(startPage - 1).append("\" title=\"이전\">&lt;</a></li>");
			} else {
				results.append("<li class=\"disabled\"><a href=\"#\" title=\"이전\">&lt;</a></li>");
			}

			int printPage = 0;

			for (int idx = 0; idx < pageCount; idx++) {
				printPage = startPage + idx;
				if (printPage <= lastPage) {
					if (printPage == page) {
						results.append("<li class=\"active\"><a href=\"").append(pageUrl).append(printPage).append("\">").append(printPage).append("<span class=\"sr-only\">(current)</span></a></li>");
					} else {
						results.append("<li><a href=\"").append(pageUrl).append(printPage).append("\">").append(printPage).append("</a></li>");
					}
				}
			}
			
			// 다음 그룹
			if (lastPage > printPage) {
				results.append("<li class=\"\"><a href=\"").append(pageUrl).append(printPage + 1).append("\" title=\"다음\">&gt;</a></li>");
			} else {
				results.append("<li class=\"disabled\"><a href=\"#\" title=\"다음\">&gt;</a></li>");
			}
			
			// 마지막 페이지
			results.append("<li class=\"\"><a href=\"").append(pageUrl).append(lastPage).append("\" title=\"맨마지막\">&gt;&gt;</a></li>");

			JspWriter out = pageContext.getOut();
			out.print(results.toString());
		} catch (Exception e) {
			throw new JspTagException(e.toString());
		}
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}
}
