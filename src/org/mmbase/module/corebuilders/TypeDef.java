/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.corebuilders;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.util.scanpage;
import org.mmbase.util.StringTagger;
import org.mmbase.module.ParseException;
import org.mmbase.util.logging.*;

/**
 * TypeDef, one of the meta stucture nodes it is used to define the
 * object types (builders)
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @version $Id: TypeDef.java,v 1.38 2003-12-17 21:09:03 michiel Exp $
 */
public class TypeDef extends MMObjectBuilder {

    // Logger routine
    private static final Logger log = Logging.getLoggerInstance(TypeDef.class);

    /**
     * Number-to-name cache.
     * @duplicate should be moved to org.mmbase.cache
     */
    private Map numberToNameCache = null; // object number -> typedef name

    /**
     * Name-to-number cache.
     * @duplicate should be moved to org.mmbase.cache
     */
    private Map nameToNumberCache = null; // typedef name -> object number

    /**
     * List of known builders.
     */
    private Vector typedefsLoaded = new Vector();     // Contains the names of all active builders

    /**
     * Constructor
     */
    public TypeDef() {
        // set broadcasting of changes to false
        broadcastChanges = false;
    }

    protected Map getNumberToNameCache() {
        if (numberToNameCache == null) readCache();
        return numberToNameCache;
    }

    protected Map getNameToNumberCache() {
        if (nameToNumberCache == null) readCache();
        return nameToNumberCache;
    }

    /**
     * Insert a new object (content provided) in the cloud, including an entry for the object alias (if provided).
     * This method indirectly calls {@link #preCommit}.
     * @param owner The administrator creating the node
     * @param node The object to insert. The object need be of the same type as the current builder.
     * @return An <code>int</code> value which is the new object's unique number, -1 if the insert failed.
     */
    public int insert(String owner, MMObjectNode node) {
        int result=super.insert(owner, node);
        if (result!=-1) {
            Integer number=node.getIntegerValue("number");
            String name=node.getStringValue("name");
            getNameToNumberCache().put(name,number);
            getNumberToNameCache().put(number,name);
        }
        return result;
    }

    /**
     * Commit changes to this node to the database. This method indirectly calls {@link #preCommit}.
     * Use only to commit changes - for adding node, use {@link #insert}.
     * @param node The node to be committed
     * @return true if commit successful
     */
    public boolean commit(MMObjectNode node) {
        Integer number=node.getIntegerValue("number");
        String oldname=node.getStringValue("name");
        boolean result=super.commit(node);
        if (result) {
            String newname=node.getStringValue("name");
            if (!oldname.equals(newname)) {
                getNameToNumberCache().remove(oldname);
                getNameToNumberCache().put(newname,number);
                getNumberToNameCache().put(number,newname);
            }
        }
        return result;
    }

    /**
     * Remove a node from the cloud, when the represented builder was active
     * it will also be unloaded
     * @param node The node to remove.
     * @throws RuntimeException When the operation could not be performed
     */
    public void removeNode(MMObjectNode node) {
        Integer number=node.getIntegerValue("number");
        String name=node.getStringValue("name");
        super.removeNode(node);
        getNameToNumberCache().remove(name);
        getNumberToNameCache().remove(number);
    }

    /**
     * Fill the typedef caches with the initial values.
     * Caches filled are a number-to-name, name-to-number, and
     * number-to-description cache
     * @duplicate should be moved to org.mmbase.cache
     * @return always true
     */
    private boolean readCache() {
        // at least fill in typedef
        log.service("Reading typedef caches");
        Integer number;
        String name;
        numberToNameCache = new Hashtable();
        nameToNumberCache = new Hashtable();
        for (Enumeration e = search(null); e.hasMoreElements();) {
            MMObjectNode n= (MMObjectNode) e.nextElement();
            number= n.getIntegerValue("number");
            name  = n.getStringValue("name");
            if (number != null && name != null) {
                nameToNumberCache.put(name,number);
                numberToNameCache.put(number,name);
            } else {
                log.error("Could not add typedef cache-entry number/name= " + number + "/" + name);
            }
         }
        return true;
    }

    /**
     * Obtain the type value of the requested builder
     * @todo smarter cache update
     * @param builderName name of the builder
     * @return the object type as an int, -1 if not defined.
     */
    public int getIntValue(String builderName) {
        Integer result=(Integer)getNameToNumberCache().get(builderName);
        if (result!=null) {
            return result.intValue();
        } else {
            return -1;
        }
    }

    /**
     * Obtain the buildername of the requested type
     * @param type the object type
     * @return the name of the builder as a string, null if not found
     */
    public String getValue(int type) {
        String result = (String) getNumberToNameCache().get(new Integer(type));
        if (result == null) {
            log.error("Could not find builder name for typedef number " + type);
        }
        return result;
    }

    /**
     * Obtain the buildername of the requested type
     * @param type the object type
     * @return the name of the builder as a string, "unknown" if not found
     * @deprecated use getValue(int)
     */
    public String getValue(String type) {
        try {
            return (String) getNumberToNameCache().get(new Integer(Integer.parseInt(type)));
        } catch(Exception e) {
            return "unknown";
        }
    }

    /**
     * @javadoc
     */
    public String getSingularName(String builderName, String language) {
        if (builderName == null) return "unknown";
        MMObjectBuilder bul=(MMObjectBuilder)mmb.mmobjs.get(builderName);
        if (bul!=null) {
            if (language == null) {
                return bul.getSingularName();
            } else {
                return bul.getSingularName(language);
            }
        } else {
            return "inactive ("+builderName+")";
        }
    }

    /**
     * @javadoc
     */
    public boolean isRelationTable(String name) {
        return mmb.getRelDef().isRelationTable(name);
    }

    /**
     * @javadoc
     */
    public Object getValue(MMObjectNode node,String field) {
        if (field.equals("state")) {
            int val=node.getIntValue("state");

            // is it set allready ? if not set it, this code should be
            // removed ones the autoreloader/state code is done.
            if (val==-1) {
                // state 1 is up and running
                node.setValue("state",1);
            }
            return ""+val;
        } else if (field.equals("dutchs(name)")) {
            // replace this function with gui(name) ?
            // but have to change admin pages first
            return getGUIIndicator("name",node);
        }
        return super.getValue(node,field);
    }

    /**
     * @javadoc
     */
    public boolean fieldLocalChanged(String number,String builder,String field,String value) {
        if (field.equals("state")) {
            if (value.equals("4")) {
                // reload request
                log.service("Reload wanted on : "+builder);
                // perform reload
                MMObjectNode node=getNode(number);
                String objectname=node.getStringValue("name");
                reloadBuilder(objectname);
                if (node!=null) {
                    node.setValue("state",1);
                }
            }
        }
        return true;
    }

    /**
     * Returns the MMObjectBuilder which is represented by the node.
     * @param   node The node, from which we want to know its MMObjectBuilder
     * @return  The builder which is represented by the node, or <code>null</code>
     *          if the builder was not loaded.
     */
    public MMObjectBuilder getBuilder(MMObjectNode node) {
        String builderName = node.getStringValue("name");
        return mmb.getMMObject(builderName);
    }

    /**
     * @javadoc
     */
    public boolean reloadBuilder(String objectname) {
        log.service("MMBASE -> Trying to reload builder : "+objectname);
        // first get all the info we need from the builder allready running
        MMObjectBuilder oldbul=mmb.getMMObject(objectname);
        String classname=oldbul.getClass().getName();
        String description=oldbul.getDescription();

        try {
            Class newclass=Class.forName("org.mmbase.module.builders."+classname);
            log.debug("TypeDef -> Loaded load class : "+newclass);

            MMObjectBuilder bul = (MMObjectBuilder)newclass.newInstance();
            log.debug("TypeDef -> started : "+newclass);

            bul.setMMBase(mmb);
            bul.setTableName(objectname);
            bul.setDescription(description);
            bul.init();
            mmb.mmobjs.put(objectname,bul);
        } catch (Exception e) {
            log.error(Logging.stackTrace(e));
            return false;
        }
        return true;
    }

    /**
     * What should a GUI display for this node.
     * This method returns the gui name (singular name) of the builder that goes with this node.
     * @param node The node to display
     * @return the display of the node as a <code>String</code>
     */
    public String getGUIIndicator(MMObjectNode node) {
        if (node!=null) {
            return getGUIIndicator("name",node);
        } else {
            log.error("TypeDef-> problem node empty");
            return "problem";
        }
    }

    /**
     * What should a GUI display for this node/field combo.
     * If the field specified is 'name', this method returns the gui name (singular name) of the
     * builder that goes with this node.
     * @param field the name field of the field to display
     * @param node The node to display
     * @return the display of the node's field as a <code>String</code>, null if not specified
     */
    public String getGUIIndicator(String field, MMObjectNode node) {
        if (field.equals("name")) {
            String name = node.getStringValue("name");
            String guiname= getSingularName(name, null);
            return guiname;
        }
        return null;
    }

    /**
     * The GUIIndicator can depend on the locale. Override this function
     * @since MMBase-1.6
     */
    protected String getLocaleGUIIndicator(Locale locale, String field, MMObjectNode node) {
        if (field == null || "".equals(field) || "name".equals(field)){
            return getLocaleGUIIndicator(locale, node);
        }
        return null;
    }

    protected String getLocaleGUIIndicator(Locale locale, MMObjectNode node) {
        String rtn =  getSingularName(node.getStringValue("name"), locale.getLanguage());
        if (rtn == null) return node.getStringValue("name");
        return rtn;
    }

    /**
     * @javadoc
     */
    public void loadTypeDef(String name) {
        if(!typedefsLoaded.contains(name)) {
            typedefsLoaded.add(name);
        } else {
            if (log.isDebugEnabled()) log.debug("Builder "+name+" is already loaded!");
        }
    }

    /**
     * @javadoc
     */
    public void unloadTypeDef(String name) {
        if(typedefsLoaded.contains(name)) {
            typedefsLoaded.remove(name);
        } else {
            log.debug("Builder "+name+" is not loaded!");
        }
    }

    /**
     * @javadoc
     */
    public Vector getList(scanpage sp,StringTagger tagger, StringTokenizer tok) throws ParseException {
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (cmd.equals("builders")) {
                return typedefsLoaded;
            }
        }
        return null;
    }

    protected Object executeFunction(MMObjectNode node, String function, List args) {
        log.debug("executefunction of typedef");
        if (function.equals("info")) {
            List empty = new ArrayList();
            java.util.Map info = (java.util.Map) super.executeFunction(node, function, empty);
            info.put("gui", info.get("info") + " (localized)");
            if (args == null || args.size() == 0) {
                return info;
            } else {
                return info.get(args.get(0));
            }
        } else if (function.equals("gui")) {
            log.debug("GUI of servlet builder with " + args);
            if (args == null || args.size() ==0) {
                return getGUIIndicator(node);
            } else {
                String rtn;
                if (args.size() <= 1) {
                    rtn = getGUIIndicator((String) args.get(0), node);
                } else {
                    String language = (String) args.get(1);
                    if (language == null) language = mmb.getLanguage();
                    Locale locale = new Locale(language, "");
                    rtn = getLocaleGUIIndicator(locale, (String) args.get(0), node);
                }
                if (rtn == null) return super.executeFunction(node, function, args);
                return rtn;
            }
        } else if (function.equals("defaultsearchage")) {
            return new Integer(getBuilder(node).getSearchAge());
        } else {
            return super.executeFunction(node, function, args);
        }
    }
}
