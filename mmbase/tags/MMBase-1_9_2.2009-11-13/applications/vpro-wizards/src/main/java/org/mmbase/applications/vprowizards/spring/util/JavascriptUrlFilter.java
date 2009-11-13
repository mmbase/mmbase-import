/**
 * 
 */
/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.util;

import java.util.regex.Pattern;

import org.apache.xerces.xni.*;
import org.cyberneko.html.filters.DefaultFilter;

/**
 * @author peter
 *
 */
public class JavascriptUrlFilter extends DefaultFilter {
	private static Pattern javascript = Pattern.compile("javascript\\W*:+",Pattern.CASE_INSENSITIVE);
	
	
	@Override
	public void startElement(QName qname, XMLAttributes attributes, Augmentations augmentations) throws XNIException {
		if(qname.localpart.equalsIgnoreCase("a")){
			for (int i = 0; i < attributes.getLength(); i++) {
				String localName = attributes.getLocalName(i);
				String nonNormalizedValue = attributes.getNonNormalizedValue(i);
				if(localName.equalsIgnoreCase("href") && javascript.matcher(nonNormalizedValue).find()){
					attributes.setValue(i, "javascript:alert('javascript urls zijn niet toegestaan')");
				}
			}
		}
		super.startElement(qname, attributes, augmentations);
	}
}
