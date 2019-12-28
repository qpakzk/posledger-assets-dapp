package com.poscoict.assets.util.tags;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringUtils;

/**
 * encoding custom tag
 */
public class EncoderTag extends BodyTagSupport {

	private static final long serialVersionUID = 383618160678399674L;

	private String value;

    private String enc;

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the enc
	 */
	public String getEnc() {
		return enc;
	}

	/**
	 * @param enc the enc to set
	 */
	public void setEnc(String enc) {
		this.enc = enc;
	}

	public int doStartTag() throws JspTagException {

        try {
            JspWriter out = pageContext.getOut();

            String encodedString = "";

            if (StringUtils.isNotEmpty(value)) {
            	encodedString = java.net.URLEncoder.encode(value, enc);
            }

            out.print(encodedString);

        } catch (Exception e) {
            throw new JspTagException(e.toString());
        }
        return SKIP_BODY;
    }
}