/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.projects.editors.cloudmodel;

/**
 */
public class AllowedRelation {
	String from;
	String to;
	String type;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;	
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;	
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;	
	}

}
