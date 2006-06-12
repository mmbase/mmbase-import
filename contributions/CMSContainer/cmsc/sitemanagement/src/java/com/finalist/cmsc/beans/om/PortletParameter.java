/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.beans.om;

import net.sf.mmapps.commons.beans.NodeBean;

/**
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
@SuppressWarnings("serial")
public class PortletParameter extends NodeBean {

	private String key;

	private String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
