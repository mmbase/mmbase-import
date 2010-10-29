/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.projects.editors.cloudmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class NeededBuilder {
        private static Logger log = Logging.getLoggerInstance(NeededBuilder.class);
	String maintainer;
	String version;
	String name;
	String extend="object";
	boolean relation;
	String status="active";
	String classname = "Dummy";
	String searchage = "1000";
	HashMap<String, String> names_singular = new HashMap<String, String>();
	HashMap<String, String> names_plural = new HashMap<String, String>();
	HashMap<String, String> descriptions = new HashMap<String, String>();
	ArrayList<String> properties = new ArrayList<String>();
	ArrayList<NeededBuilderField> fields = new ArrayList<NeededBuilderField>();

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

	public HashMap<String, String> getSingularNames() {
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

	public HashMap<String, String> getPluralNames() {
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

	public HashMap<String, String> getDescriptions() {
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

	public String getClassName() {
		return classname;
	}

	public void setClassName(String classname) {
		this.classname = classname;	
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



	public void addField(String newname,String newtype,String newstatus,int newsize) {
		log.info("new name="+newname);
		NeededBuilderField field = new NeededBuilderField();
		field.setDBName(newname);
		field.setDBType(newtype);
		field.setDBState(newstatus);
		field.setDBSize(newsize);
		fields.add(field);
	}

 	public Iterator<NeededBuilderField> getFields() {
		return fields.iterator();
	}


    	public NeededBuilderField getField(String field) {
    		Iterator<NeededBuilderField> nbfl=getFields();
		while (nbfl.hasNext()) {
			NeededBuilderField nbf=nbfl.next();
			if (nbf.getDBName().equals(field)) {
				return nbf;
			}
		}
		return null;
    	}


    	public boolean deleteField(String field) {
    		Iterator<NeededBuilderField> nbfl=getFields();
		while (nbfl.hasNext()) {
			NeededBuilderField nbf=nbfl.next();
			if (nbf.getDBName().equals(field)) {
				fields.remove(nbf);	
				return true;
			}
		}
		return false;
    	}

}
