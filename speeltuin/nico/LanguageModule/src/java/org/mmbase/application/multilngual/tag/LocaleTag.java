package org.mmbase.application.multilngual.tag;

import java.util.Locale;

import javax.servlet.jsp.JspTagException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * @author Nico Klasens
 *
 * Provides Locale (language, country) information  to its body.
 */
public class LocaleTag extends org.mmbase.bridge.jsp.taglib.LocaleTag {

	/** MMBase logging system */
	private static Logger log = Logging.getLoggerInstance(LocaleTag.class.getName());

	/** Locale */
	private Locale locale;

	/** jspvar name */
	private String jspvar = null;
	
	/** returns the locale set in the session as nl.hcs.LOCALE attribute 
	 * @return Locale
	 */
	public Locale getLocale() {
		 return locale;
	}

	/** Set the name of the variable to insert in jsp
	 * @param j Jspvar name 
	 */
	public void setJspvar(String j) {
		 jspvar = j;
	}	
	
	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspTagException {

	   String languageCode = (String)pageContext.getRequest().getAttribute("language");
	   String countryCode = (String)pageContext.getRequest().getAttribute("country");
      
      if ((languageCode == null) || (languageCode.equals("null")) || (countryCode == null) || (countryCode.equals("null"))) {
			locale = org.mmbase.bridge.LocalContext.getCloudContext().getDefaultLocale();
		}
		else {
		   locale = new Locale(languageCode, countryCode);		    
	   }
		log.debug("lang: " + locale.getLanguage() + " country: " + locale.getCountry());
		 
		if (jspvar != null) {
			pageContext.setAttribute(jspvar, locale);
		}
		return EVAL_BODY_BUFFERED;
	}
}
