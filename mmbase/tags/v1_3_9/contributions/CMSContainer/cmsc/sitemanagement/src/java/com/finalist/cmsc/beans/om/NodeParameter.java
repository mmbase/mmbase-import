/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.beans.om;

import net.sf.mmapps.commons.beans.NodeBean;

import org.mmbase.bridge.Node;


/**
 * @author Wouter Heijke
 */
@SuppressWarnings("serial")
public class NodeParameter extends NodeBean {

	private String key;

	private String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValueAsString() {
		return value;
	}

	public void setValue(Node value) {
        if (value != null) {
            this.value = String.valueOf(value.getNumber());
        }
	}
}
