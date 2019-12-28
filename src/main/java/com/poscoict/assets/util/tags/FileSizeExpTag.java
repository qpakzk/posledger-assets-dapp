package com.poscoict.assets.util.tags;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * @author will
 * date compare custom tag
 */
public class FileSizeExpTag extends BodyTagSupport {

	private static final long serialVersionUID = 3368526150623417822L;

	/**
	 * 용량(bit)
	 */
	private int value;

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

	public int doStartTag() throws JspTagException{
		String size = "";
		
		try {
			JspWriter out = pageContext.getOut();
			
		        if(value < 1024) {
		        	size = value + " bytes";
		        } else if(value < 1048576) {
		        	size = (Math.round(((value*10) / 1024))/10) + " KB";
		        } else {
		        	size = (Math.round(((value*10) / 1048576))/10) + " MB";
		        }
			
			out.print(size);
			
		} catch (Exception e) {
            throw new JspTagException(e.toString());
        }
		
		return SKIP_BODY;
	}
}