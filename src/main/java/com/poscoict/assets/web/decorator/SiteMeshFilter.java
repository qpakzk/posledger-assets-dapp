/*===============================================
 *Copyright(c) 2014 POSCO/POSDATA
 *
 *@ProcessChain   : FEMS
 *@File           : RequestFilter.java
 *@FileName       : RequestFilter
 *
 *Change history
 *@LastModifier : 이재만
 *@수정 날짜; SCR_NO; 수정자; 수정내용
 * 2014-04-10; 이재만; Initial Version
 ===============================================*/

package com.poscoict.assets.web.decorator;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;
import org.sitemesh.content.tagrules.html.DivExtractingTagRuleBundle;

/**
 * <pre>
 * Meta 태그 선택자 기능 확장
 * </pre>
 * 
 * @author : 이재만
 */
public class SiteMeshFilter extends ConfigurableSiteMeshFilter {
	@Override
	protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
		builder.addTagRuleBundle(new DivExtractingTagRuleBundle());
		builder.setCustomDecoratorSelector(new MetaTagDecoratorSelector(builder.getDecoratorSelector()));
	}
}
