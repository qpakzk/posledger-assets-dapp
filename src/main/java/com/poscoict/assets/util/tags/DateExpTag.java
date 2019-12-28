package com.poscoict.assets.util.tags;

import java.util.Date;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringUtils;

import com.poscoict.posledger.chain.DateUtil;

/**
 * date expression custom tag
 */
public class DateExpTag extends BodyTagSupport {

    private static final long serialVersionUID = -1946662271953931844L;

    private String value;

    private String format;

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
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	public int doStartTag() throws JspTagException {

        try {
            JspWriter out = pageContext.getOut();

            if (StringUtils.isNotEmpty(value)) {

				Date date = null;

				if(value.trim().length()==4)
					date = DateUtil.stringToDate(value, "yyyy");
				else if(value.trim().length()==6)
					date = DateUtil.stringToDate(value, "yyyyMM");
				else if(value.trim().length()==8)
					date = DateUtil.stringToDate(value, "yyyyMMdd");
				else if(value.trim().length()==10)
					date = DateUtil.stringToDate(value, "yyyyMMddHH");
				else if(value.trim().length()==14)
					date = DateUtil.stringToDate(value, "yyyyMMddHHmmss");
				else if(value.trim().length()==17)
					date = DateUtil.stringToDate(value, "yyyyMMddHHmmssSSS");

				out.print(DateUtil.formatDate(date, format));
			} else {
				out.print("-");
			}

        } catch (Exception e) {
            throw new JspTagException(e.toString());
        }

        return SKIP_BODY;
    }
}