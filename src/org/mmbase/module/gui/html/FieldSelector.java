package org.mmbase.module.gui.html;

import java.util.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

/**
 * The FieldSelector class offers the possibility to edit fields of a selected
 * object.  
 *
 * @author Daniel Ockeloen
 * @author Hans Speijer
 */
public class FieldSelector implements CommandHandlerInterface {
	
	StateManager stateMngr;

	/**
	 * Constructor to setup reference to the StateManager.
	 */
	public FieldSelector(StateManager stateMngr) {
		this.stateMngr=stateMngr;
	}

	/**
	 * General List pages coming from MMEdit.
	 */
	public Vector getList(scanpage sp, StringTagger args, StringTokenizer commands) {
		String token;
		String userName=HttpAuth.getRemoteUser(sp);
		EditState state = stateMngr.getEditState(userName);

		token=commands.nextToken();
		if (token.equals("GETEDITFIELDS")) {
			return(getEditFields(state,args));
		} else if (token.equals("GETPOSRELATIONS")) {
			return(getPosRelations(state,args));
		} else if (token.equals("GETTYPES")) {
			return(getTypes(state,args));
		} else if (token.equals("GETRELDEFS")) {
			return(getRelDefs(state,args));
		}
		return(null);
	}

	Vector getTypes(EditState ed,StringTagger args) {
		Vector results=new Vector();
		Enumeration h=stateMngr.mmBase.getTypeDef().search("");
		results=new Vector();
		for (;h.hasMoreElements();) {
			MMObjectNode node=(MMObjectNode)h.nextElement();
			results.addElement(""+node.getIntValue("number"));
			results.addElement(node.getStringValue("name"));
		}
		args.setValue("ITEMS","2");
		return(results);
	}


	Vector getRelDefs(EditState ed,StringTagger args) {
		Vector results=new Vector();
		Enumeration h=stateMngr.mmBase.getRelDef().search("");
		results=new Vector();
		for (;h.hasMoreElements();) {
			MMObjectNode node=(MMObjectNode)h.nextElement();
			results.addElement(""+node.getIntValue("number"));
			if (node.getIntValue("dir")==1) {
				results.addElement(node.getStringValue("sguiname")+"/"+node.getStringValue("dguiname"));
			} else {
				results.addElement(node.getStringValue("dguiname"));
			}
		}
		args.setValue("ITEMS","2");
		return(results);
	}

	Vector getPosRelations(EditState ed,StringTagger args) {
		MMObjectBuilder bul=ed.getBuilder();
		MMObjectNode node=ed.getEditNode();
		int n1=node.getIntValue("snumber");
		int n2=node.getIntValue("dnumber");
		Vector results=new Vector();
		Enumeration h=stateMngr.mmBase.getTypeRel().getAllowedRelations(bul.getNode(n1),bul.getNode(n2));
		for (;h.hasMoreElements();) {
			MMObjectNode n=(MMObjectNode)h.nextElement();
			int r=n.getIntValue("rnumber");	
			results.addElement(""+r);	
			MMObjectNode node2=stateMngr.mmBase.getRelDef().getNode(r);
			results.addElement(node2.getGUIIndicator());	
		}
		args.setValue("ITEMS","2");
		return(results);
	}

	/**
	 * Builds a list of editable fields for the field editor.
	 * Item1 = Name of the field (GUIName)
	 * Item2 = Type of the field (GUIType)
	 * Item3 = Database name of the field (DBName)
	 * Item4 = Current Search Value of the field
	 */
	Vector getEditFields(EditState ed,StringTagger args) {
		Vector results=new Vector();
		MMObjectBuilder obj=ed.getBuilder();
		String key,val;
		Vector tempresults=obj.getEditFields();
		FieldDefs def;
		for (Enumeration h=tempresults.elements();h.hasMoreElements();) {
			def=(FieldDefs)h.nextElement();
			results.addElement(def.getGUIName());
			results.addElement(def.getGUIType());
			key=def.getDBName();
			val=ed.getSearchValue(key);
			if (val==null) val="";
			results.addElement(key);
			results.addElement(val);
		}
		args.setValue("ITEMS","4");
		return(results);
	}

	/**
	 * Replace/Trigger commands
	 */
	public String replace(scanpage sp, StringTokenizer cmds) {
		return "Command not defined (FieldSelector)";
	}
	
	/**
	 * The hook that passes all form related pages to the correct handler
	 */
	public boolean process(scanpage sp, StringTokenizer command, Hashtable cmds, Hashtable vars) {
		return false;
	}

}
