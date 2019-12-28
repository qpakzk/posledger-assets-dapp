/*===============================================
 *Copyright(c) 2014 POSCO/POSDATA
 *
 *@ProcessChain   : FEMS
 *@File           : HttpRequestWrapper.java
 *@FileName       : HttpRequestWrapper
 *
 *Change history
 *@LastModifier : 이재만
 *@수정 날짜; SCR_NO; 수정자; 수정내용
 * 2014-04-10; 이재만; Initial Version
 ===============================================*/

package com.poscoict.assets.web.decorator;

import java.io.IOException;

import org.sitemesh.DecoratorSelector;
import org.sitemesh.content.Content;
import org.sitemesh.webapp.WebAppContext;

/**
 * sitemesh3 meta 태그 decorator selector
 * 
 * @author : 이재만
 */
public class MetaTagDecoratorSelector implements DecoratorSelector<WebAppContext> {

	private final DecoratorSelector<WebAppContext> fallbackSelector;

	public MetaTagDecoratorSelector(DecoratorSelector<WebAppContext> fallbackSelector) {
		this.fallbackSelector = fallbackSelector;
	}

	@Override
	public String[] selectDecoratorPaths(Content content, WebAppContext context) throws IOException {

		String decorator = content.getExtractedProperties().getChild("meta").getChild("decorator").getValue();

		if (decorator != null) {
			return decorator.split(",");
		} else {
			// Otherwise, fallback to the standard configuration.
			return fallbackSelector.selectDecoratorPaths(content, context);
		}
	}
}