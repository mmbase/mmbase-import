/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.projects.editors.cloudmodel;

/**
 */
public class NeededRelDef {
	String source;
	String target;
	String direction;
	String guisourcename; 
	String guitargetname; 
	String buildername; 

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;	
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;	
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;	
	}

	public String getGuiSourceName() {
		return guisourcename;
	}

	public void setGuiSourceName(String guisourcename) {
		this.guisourcename = guisourcename;	
	}

	public String getGuiTargetName() {
		return guitargetname;
	}

	public void setGuiTargetName(String guitargetname) {
		this.guitargetname = guitargetname;	
	}

	public String getBuilderName() {
		return buildername;
	}

	public void setBuilderName(String buildername) {
		this.buildername = buildername;	
	}

}
