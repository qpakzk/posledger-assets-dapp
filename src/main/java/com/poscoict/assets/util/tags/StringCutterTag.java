package com.poscoict.assets.util.tags;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringUtils;

/**
 * string cutter custom tag
 */
public class StringCutterTag extends BodyTagSupport {

	private static final long serialVersionUID = -1946662271953931844L;

	private String title;

	private String suffix;

	private int length;

	private boolean escapeXml;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isEscapeXml() {
		return escapeXml;
	}

	public void setEscapeXml(boolean escapeXml) {
		this.escapeXml = escapeXml;
	}

	public int doStartTag() throws JspTagException {

		try {
			JspWriter out = pageContext.getOut();

			String replaceTitle = "";
			if (StringUtils.isNotEmpty(title)) {
				if (isEscapeXml()) {
					title = StringUtils.replace(title, "&", "&amp;");
					title = StringUtils.replace(title, "<", "&lt;");
					title = StringUtils.replace(title, ">", "&gt;");
				}

				if (title.length()>length) {
					replaceTitle = StringUtils.substring(title, 0, length) + suffix;
				} else {
					replaceTitle = title;
				}
			} else {
				title = "&nbsp;";
			}

			out.print(replaceTitle);

		} catch (Exception e) {
			throw new JspTagException(e.toString());
		}
		return SKIP_BODY;
	}
}