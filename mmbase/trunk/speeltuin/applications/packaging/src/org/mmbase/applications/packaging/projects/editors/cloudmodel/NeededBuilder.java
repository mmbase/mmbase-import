/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.projects.editors.cloudmodel;

import java.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;

/**
 */
public class NeededBuilder {
        private static Logger log = Logging.getLoggerInstance(NeededBuilder.class);
	String maintainer;
	String version;
	String name;
	String extend="object";
	boolean relation;
	String status="active";
	String classfile = "Dummy";
	String searchage = "1000";
	HashMap names_singular = new HashMap();
	HashMap names_plural = new HashMap();
	HashMap descriptions = new HashMap();
	ArrayList properties = new ArrayList();
	ArrayList fields = new ArrayList();

	public String getMaintainer() {
		return maintainer;
	}

	public void setMaintainer(String maintainer) {
		this.maintainer = maintainer;	
	}

	public String getSearchAge() {
		return searchage;
	}

	public void setSearchAge(String searchage) {
		this.searchage = searchage;	
	}

	public void setSingularName(String language,String name) {
	     names_singular.put(language,name);
	}

	public HashMap getSingularNames() {
	     return names_singular;
	}

	public String getSingularName(String language) {
	     Object o=names_singular.get(language);
	     if (o!=null) return (String)o;
	     return "";
	}


	public void setPluralName(String language,String name) {
	     names_plural.put(language,name);
	}

	public HashMap getPluralNames() {
	     return names_plural;
	}

	public String getPluralName(String language) {
	     Object o=names_plural.get(language);
	     if (o!=null) return (String)o;
	     return "";
	}



	public void setDescription(String language,String name) {
	     descriptions.put(language,name);
	}

	public HashMap getDescriptions() {
	     return descriptions;
	}

	public String getDescription(String language) {
	     Object o=descriptions.get(language);
	     if (o!=null) return (String)o;
	     return "";
	}

	public String getExtends() {
		return extend;
	}

	public void setExtends(String extend) {
		this.extend = extend;	
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;	
	}
 
	public void addField(NeededBuilderField field) {
		fields.add(field);
	}

 	public Iterator getFields() {
		return fields.iterator();
	}

}
