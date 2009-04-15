/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.beans;

import com.finalist.cmsc.beans.NodeBean;

/**
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
public class NodetypeBean extends NodeBean {

    private static final long serialVersionUID = -4892877864883371932L;

    public String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
