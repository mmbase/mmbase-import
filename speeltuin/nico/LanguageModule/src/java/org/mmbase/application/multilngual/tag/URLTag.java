package org.mmbase.application.multilngual.tag;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.jsp.taglib.LocaleTag;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.Locale;

/**
 * @author Ronald Kramp
 *
 * Provides language, country information to the url.
 */
public class URLTag extends org.mmbase.bridge.jsp.taglib.pageflow.UrlTag {

	/** MMBase logging system */
	private static Logger log = Logging.getLoggerInstance(URLTag.class.getName());

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspTagException {
		super.doStartTag();	
		
		Locale locale;
		Locale systemLocale = org.mmbase.bridge.LocalContext.getCloudContext().getDefaultLocale();
      
		LocaleTag localeTag = (LocaleTag)findParentTag("org.mmbase.bridge.jsp.taglib.LocaleTag", null,	false);

		if (localeTag != null) {
			locale = localeTag.getLocale();
		} 
		else {
			locale = systemLocale;
		}
      	
		addParameter("language", locale.getLanguage());
		addParameter("country", locale.getCountry());
		return EVAL_BODY_BUFFERED;
	}
}
