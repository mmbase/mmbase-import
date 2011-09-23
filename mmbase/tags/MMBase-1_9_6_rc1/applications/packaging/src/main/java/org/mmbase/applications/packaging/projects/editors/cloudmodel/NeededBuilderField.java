/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.projects.editors.cloudmodel;

import java.util.HashMap;
import java.util.Map;

public class NeededBuilderField {

    Map<String, String> descriptions = new HashMap<String, String>();
	Map<String, String> guinames = new HashMap<String, String>();
	String guitype;
	int editorinputpos = -1;
	int editorlistpos = -1;
	int editorsearchpos = -1;
	String dbname;
	String dbtype;
	boolean dbkey;
	int dbsize = -1;
	boolean dbnotnull;
	String dbstate;

	public String getDBName() {
		return dbname;
	}

	public void setDBName(String dbname) {
		this.dbname = dbname;	
	}

	public boolean getDBKey() {
		return dbkey;
	}

	public void setDBKey(boolean dbkey) {
		this.dbkey = dbkey;	
	}

	public boolean getDBNotNull() {
		return dbnotnull;
	}

	public void setDBNotNull(boolean dbnotnull) {
		this.dbnotnull = dbnotnull;	
	}

	public String getDBType() {
		return dbtype;
	}

	public void setDBType(String dbtype) {
		this.dbtype = dbtype;	
	}

	public String getDBState() {
		return dbstate;
	}

	public void setDBState(String dbstate) {
		this.dbstate = dbstate;	
	}

	public int getDBSize() {
		return dbsize;
	}

	public void setDBSize(int dbsize) {
		this.dbsize = dbsize;	
	}


	public String getGuiType() {
		return guitype;
	}

	public void setGuiType(String guitype) {
		this.guitype = guitype;	
	}

	public int getEditorInputPos() {
		return editorinputpos;
	}

	public void setEditorInputPos(int editorinputpos) {
		this.editorinputpos = editorinputpos;	
	}

	public int getEditorListPos() {
		return editorlistpos;
	}

	public void setEditorListPos(int editorlistpos) {
		this.editorlistpos = editorlistpos;	
	}

	public int getEditorSearchPos() {
		return editorsearchpos;
	}

	public void setEditorSearchPos(int editorsearchpos) {
		this.editorsearchpos = editorsearchpos;	
	}

	public void setDescription(String language,String description) {
		descriptions.put(language,description);
	}

	public Map<String, String> getDescriptions() {
		return descriptions;
	}

        public String getDescription(String language) {
             Object o=descriptions.get(language);
             if (o!=null) return (String)o;
             return "";
        }

        public String getGuiName(String language) {
             Object o=guinames.get(language);
             if (o!=null) return (String)o;
             return "";
        }

	public void setGuiName(String language,String description) {
		guinames.put(language,description);
	}

	public Map<String, String> getGuiNames() {
		return guinames;
	}

}
