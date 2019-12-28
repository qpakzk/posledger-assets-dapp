package com.poscoict.assets.util.tags;

import java.text.DecimalFormat;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringUtils;

/**
 * number format custom tag
 */
public class FormatNumberTag extends BodyTagSupport {

    private static final long serialVersionUID = -1946662271953931844L;

    private int value;

    private String pattern;

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}


	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public int doStartTag() throws JspTagException {

        try {
            JspWriter out = pageContext.getOut();

            if (StringUtils.isEmpty(pattern)) {
            	pattern = "###,###";
            }

            DecimalFormat df = new DecimalFormat(pattern);

            out.print(df.format(value));

        } catch (Exception e) {
            throw new JspTagException(e.toString());
        }

        return SKIP_BODY;
    }
}