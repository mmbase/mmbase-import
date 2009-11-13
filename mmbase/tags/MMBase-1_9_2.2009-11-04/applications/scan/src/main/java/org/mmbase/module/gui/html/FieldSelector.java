/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.gui.html;

import java.util.*;

import org.mmbase.util.*;
import org.mmbase.module.ParseException;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

/**
 * The FieldSelector class offers the possibility to edit fields of a selected
 * object.
 *
 * @application SCAN
 * @author Daniel Ockeloen
 * @author Hans Speijer
 * @version $Id$
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
    public List<String> getList(PageInfo sp, StringTagger args, StringTokenizer commands) throws ParseException {
        String token;
        String userName=HttpAuth.getRemoteUser(sp);
        EditState state = stateMngr.getEditState(userName);

        token=commands.nextToken();
        if (token.equals("GETEDITFIELDS")) {
            return getEditFields(state,args);
        } else if (token.equals("GETPOSRELATIONS")) {
            return getPosRelations(state,args);
        } else if (token.equals("GETTYPES")) {
            return getTypes(state,args);
        } else if (token.equals("GETRELTYPES")) {
            return getRelTypes(state,args);
        } else if (token.equals("GETDATATYPES")) {
            return getDataTypes(state,args);
        } else if (token.equals("GETRELDEFS")) {
            return getRelDefs(state,args);
        }
        return null;
    }

    Vector getRelTypes(EditState ed,StringTagger args) {
        Vector results=new Vector();
        for (Enumeration h=Collections.enumeration(stateMngr.mmBase.getBuilders());h.hasMoreElements();) {
            MMObjectBuilder bul=(MMObjectBuilder)h.nextElement();
            if (bul instanceof InsRel) {
                results.addElement(""+bul.getNumber());
                results.addElement(bul.getTableName());
                results.addElement(bul.description);
            }
        }
        args.setValue("ITEMS","3");
        return results;
    }

    Vector getDataTypes(EditState ed,StringTagger args) {
        Vector results=new Vector();
        for (Enumeration h=Collections.enumeration(stateMngr.mmBase.getBuilders());h.hasMoreElements();) {
            MMObjectBuilder bul=(MMObjectBuilder)h.nextElement();
            if (!(bul instanceof InsRel)) {
                results.addElement(""+bul.getNumber());
                results.addElement(bul.getTableName());
                results.addElement(bul.description);
            }
        }
        args.setValue("ITEMS","3");
        return results;
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
        return results;
    }


    Vector getRelDefs(EditState ed,StringTagger args) {
        Vector results=new Vector();
        Enumeration h=stateMngr.mmBase.getRelDef().search("");
        results=new Vector();
        for (;h.hasMoreElements();) {
            MMObjectNode node=(MMObjectNode)h.nextElement();
            results.addElement(""+node.getIntValue("number"));
            // *** changed
            if (node.getIntValue("dir")==2) {
                results.addElement(node.getStringValue("sguiname")+"/"+node.getStringValue("dguiname"));
            } else {
                results.addElement(node.getStringValue("dguiname"));
            }
        }
        args.setValue("ITEMS","2");
        return results;
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
        return results;
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
        String language=ed.getLanguage();
        MMObjectBuilder obj=ed.getBuilder();
        String key,val;
        Vector tempresults = new Vector(obj.getFields(org.mmbase.bridge.NodeManager.ORDER_EDIT));
        FieldDefs def;
        for (Enumeration h=tempresults.elements();h.hasMoreElements();) {
            def=(FieldDefs)h.nextElement();
            results.addElement(def.getGUIName(language));
            results.addElement(def.getGUIType());
            key=def.getDBName();
            val=ed.getSearchValue(key);
            if (val==null) val="";
            results.addElement(key);
            results.addElement(val);
        }
        args.setValue("ITEMS","4");
        return results;
    }

    /**
     * Replace/Trigger commands
     */
    public String replace(PageInfo sp, StringTokenizer cmds) {
        return "Command not defined (FieldSelector)";
    }

    /**
     * The hook that passes all form related pages to the correct handler
     */
    public boolean process(PageInfo sp, StringTokenizer command, Hashtable cmds, Hashtable vars) {
        return false;
    }

}
