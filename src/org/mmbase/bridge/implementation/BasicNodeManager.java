/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.security.*;
import java.util.*;
import javax.servlet.*;
import org.mmbase.util.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

/**
 * This class represents a node's type information object - what used to be the 'builder'.
 * It contains all the field and attribuut information, as well as GUI data for editors and
 * some information on deribed and deriving types. It also contains some maintenance code - code
 * to create new nodes, en code to query objects belonging to the same manager.
 * Since node types are normally maintained through use of config files (and not in the database),
 * as wel as for security issues, the data of a nodetype cannot be changed except through
 * the use of an administration module (which is why we do not include setXXX methods here).
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public class BasicNodeManager implements NodeManager, Comparable {

    // Cloud for this nodetype
    protected BasicCloud cloud=null;

    // builder on which the type is based
    protected MMObjectBuilder builder=null;

    // field types
    protected Hashtable fieldTypes = new Hashtable();

    // empty constructor for overriding constructors
    BasicNodeManager() {
    }

    // constructor for creating a Manager for specific cloud
    BasicNodeManager(MMObjectBuilder builder, Cloud cloud) {
        init(builder, cloud);
    }

    protected void init(MMObjectBuilder builder, Cloud cloud) {
        this.cloud=(BasicCloud)cloud;
        this.builder=builder;
        if (!builder.isVirtual()) {
            for(Iterator i=builder.getFields().iterator(); i.hasNext();){
                FieldDefs f=(FieldDefs)i.next();
                Field ft= new BasicField(f,this);
                fieldTypes.put(ft.getName(),ft);
            }
        }
    }

    public Node createNode() {
        cloud.assert(Operation.CREATE,builder.oType);
        // create object as a temporary node
        int id = cloud.uniqueId();
        String currentObjectContext = BasicCloudContext.tmpObjectManager.createTmpNode(builder.getTableName(), cloud.getAccount(), ""+id);
        // if we are in a transaction, add the node to the transaction;
        if (cloud instanceof BasicTransaction) {
            ((BasicTransaction)cloud).add(currentObjectContext);
        }
        MMObjectNode node = BasicCloudContext.tmpObjectManager.getNode(cloud.getAccount(), ""+id);
        // set the owner to userName instead of account
//        node.setValue("owner",cloud.getUserName());
        node.setValue("owner","bridge");
        if (builder instanceof InsRel) {
            return new BasicRelation(node, (RelationManager)this, id);
        } else {
            return new BasicNode(node, this, id);
        }
    }

    public Cloud getCloud() {
        return cloud;
    }

    public String getName() {
        return builder.getTableName();
    }

    public String getGUIName() {
        Hashtable singularNames=builder.getSingularNames();
        if (singularNames!=null) {
            String lang=cloud.language;
            String tmp=(String)singularNames.get(lang);
            if (tmp!=null) {
                return tmp;
            }
        }
        return builder.getTableName();
    }

    public String getDescription() {
        Hashtable descriptions=builder.getDescriptions();
        if (descriptions!=null) {
            String lang=cloud.language;
            String tmp=(String)descriptions.get(lang);
            if (tmp!=null) {
                return tmp;
            }
        }
        return builder.getDescription();
    }

    public FieldList getFields() {
        return new BasicFieldList(fieldTypes.values(),cloud,this);
    }

    public FieldList getFields(int order) {
        if (order == ORDER_EDIT) {
            return new BasicFieldList(builder.getSortedFields(),cloud,this);
        } else if (order == ORDER_LIST) {
            return new BasicFieldList(builder.getSortedListFields(),cloud,this);
        } else if (order == ORDER_SEARCH) {
            return new BasicFieldList(builder.getEditFields(),cloud,this);
        } else {
            return getFields() ;
        }
    }

    public Field getField(String fieldName) {
        Field f= (Field)fieldTypes.get(fieldName);
        return f;
    }

    public NodeList getList(String constraints, String orderby,
            String directions) {
        String where = null;
          if ((constraints != null) && (!constraints.trim().equals(""))) {
            where = "WHERE " + constraints;
          }
          Vector v;
          if (orderby != null) {
            v = builder.searchVector(where, orderby, directions);
        } else {
            v = builder.searchVector(where);
        }
        // remove all nodes that cannot be accessed
        for(int i=(v.size()-1); i>-1; i--) {
            if (!cloud.check(Operation.READ, ((MMObjectNode)v.get(i)).getIntValue("number"))) {
                v.remove(i);
            }
        }
        return new BasicNodeList(v,cloud,this);
    }

    public String getInfo(String command) {
        return getInfo(command, null,null);
    }

    public String getInfo(String command, ServletRequest req,  ServletResponse resp){
        StringTokenizer tokens= new StringTokenizer(command,"-");
        return builder.replace(BasicCloudContext.getScanPage(req, resp),tokens);
    }

    public NodeList getList(String command, Hashtable parameters){
        return getList(command,parameters,null,null);
    }

    public NodeList getList(String command, Hashtable parameters, ServletRequest req, ServletResponse resp){
        StringTagger params= new StringTagger("");
        if (parameters!=null) {
            for (Enumeration keys=parameters.keys(); keys.hasMoreElements(); ) {
                String key=(String)keys.nextElement();
                Object o = parameters.get(key);
                if (o instanceof Vector) {
                    params.setValues(key,(Vector)o);
                } else {
                    params.setValue(key,""+o);
                }
            }
        }
        try {
            StringTokenizer tokens= new StringTokenizer(command,"-");
            Vector v=builder.getList(BasicCloudContext.getScanPage(req, resp),params,tokens);
            if (v==null) { v=new Vector(); }
            int items=1;
            try { items=Integer.parseInt(params.Value("ITEMS")); } catch (Exception e) {}
            Vector fieldlist=params.Values("FIELDS");
            Vector res=new Vector(v.size() / items);
            for(int i= 0; i<v.size(); i+=items) {
                MMObjectNode node = new MMObjectNode(builder);
                for(int j= 0; (j<items) && (j<v.size()); j++) {
                    if ((fieldlist!=null) && (j<fieldlist.size())) {
                        node.setValue((String)fieldlist.get(j),v.get(i+j));
                    } else {
                        node.setValue("item"+(j+1),v.get(i+j));
                    }
                }
                res.add(node);
            }
               NodeManager tempNodeManager = null;
               if (res.size()>0) {
                  tempNodeManager = new VirtualNodeManager((MMObjectNode)res.get(0),cloud);
            }
            return new BasicNodeList(res,cloud,tempNodeManager);
        } catch (Exception e) {
            throw new BasicBridgeException(e);
        }
    }


    /**
     * Compares two nodemanagers, and returns true if they are equal.
     * This effectively means that both objects are nodemanagers, and they both use to the same builder type
     * @param o the object to compare it with
     */
    public boolean equals(Object o) {
        return (o instanceof NodeManager) && (o.hashCode()==hashCode());
    };

    /**
     * Compares two nodemanagers for sorting. It uses the GUIName for
     * sorting, which makes the sorting alphabetic. This is what you
     * need when you e.g. try to make an generic editor.
     *
     * @param o the object to compare with 
     */
    public int compareTo(Object o) {
	if (! (o instanceof NodeManager)) { return -1; }
	return getGUIName().compareToIgnoreCase(((NodeManager)o).getGUIName());
    }

    /**
     * Returns the nodemanager's hashCode.
     * This effectively returns the buidlers's object type number
     */
    public int hashCode() {
        return builder.oType;
    };
}
