package org.mmbase.application.multilngual.tag;

import java.util.Locale;

import javax.servlet.jsp.JspTagException;

import org.mmbase.application.multilngual.module.TranslationModule;

import org.mmbase.bridge.Field;
import org.mmbase.module.Module;
import org.mmbase.bridge.jsp.taglib.LocaleTag;

import org.mmbase.util.Casting;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Nico Klasens
 *
 * This tag extends the mmbase field tag to translate the field String values 
 * to different languages
 */
public class FieldTag extends org.mmbase.bridge.jsp.taglib.FieldTag {

	/** MMBase logging system */
	private static Logger log =
		Logging.getLoggerInstance(FieldTag.class.getName());

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 * 
	 * handles all fields with type text
	 */
	public int doStartTag() throws JspTagException {
		Locale locale;
		Locale systemLocale =
			org.mmbase.bridge.LocalContext.getCloudContext().getDefaultLocale();

		LocaleTag localeTag =
			(LocaleTag) findParentTag("org.mmbase.bridge.jsp.taglib.LocaleTag", null,	false);
		if (localeTag != null) {
			locale = localeTag.getLocale();
		} else {
			locale = systemLocale;
		}

		String userlang = locale.getLanguage();
		String systemlang = systemLocale.getLanguage();

		if (userlang != null && !userlang.equals(systemlang)) {
			node = null;
			setFieldVar(name); // set field and node
			boolean doTranslateLookup = true;

			if (field == null) {
				if ( getReferid() != null) {
					doTranslateLookup = false;
			 	}
			}
			else {
				doTranslateLookup = isFieldofStringType();
			}

			if (doTranslateLookup) {
				String numberFieldname;
				String nodeFieldname;

				int dotIndex = name.indexOf(".");
				if (dotIndex > -1) {
					//node is a clusternode
					numberFieldname =	name.substring(0, dotIndex) + ".number";
					nodeFieldname = name.substring(dotIndex + 1);
				}
				else {
					numberFieldname = "number";
					nodeFieldname = name;
				}
				
				int nodeNumber = node.getIntValue(numberFieldname);

				// get the field from the transalation table
				log.debug(
					"Fieldname: " + nodeFieldname
						+ " numberfield: " + numberFieldname
						+ " Node: " + nodeNumber
						+ " Language: " + locale.getLanguage()
						+ " Country: " + locale.getCountry());

				String value =  Casting.toString(node.getValue(name));

				TranslationModule tmodule = (TranslationModule)Module.getModule("TRANSLATIONS");
					
				String translation = tmodule.lookupTranslation(nodeNumber, nodeFieldname, 
										locale.getLanguage(), locale.getCountry());
				log.debug("translation: " + translation);				
				
				if (translation != null && !"".equals(translation)) {
					value = translation;				
				}
				
				log.debug("value of " + fieldName + ": " + value);
				helper.setValue(value);
				helper.setPageContext(pageContext);
				helper.setJspvar();
				if (getId() != null) {
					 getContextTag().register(getId(), helper.getValue());
				}
				
				return EVAL_BODY_BUFFERED;
			}
		} 
		return super.doStartTag();
	}

	/**
	 * @return is field of type text
	 */
	private boolean isFieldofStringType() {
		switch (field.getType()) {
			case Field.TYPE_BYTE :
			case Field.TYPE_INTEGER :
			case Field.TYPE_NODE :
			case Field.TYPE_DOUBLE :
			case Field.TYPE_LONG :
			case Field.TYPE_FLOAT :
				return false;
			default :
				return true;
		}
	}




}
