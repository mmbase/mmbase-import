/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.projects.editors.cloudmodel;

/**
 */
public class NeededBuilder {
	String maintainer;
	String version;
	String name;

	public String getMaintainer() {
		return maintainer;
	}

	public void setMaintainer(String maintainer) {
		this.maintainer = maintainer;	
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;	
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;	
	}
}
