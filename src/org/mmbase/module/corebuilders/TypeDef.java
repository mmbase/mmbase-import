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
 * @version 12 Mar 1997
 */
public class TypeDef extends MMObjectBuilder {

    // Logger routine
    private static Logger log = Logging.getLoggerInstance(TypeDef.class.getName());

    /**
     * Number-to-name cache.
     * @duplicate should be moved to org.mmbase.cache
     */
    private Map nameCache = new Hashtable(); // object number -> typedef name

    /**
     * Name-to-number cache.
     * @duplicate should be moved to org.mmbase.cache
     */
    private Map numberCache= new Hashtable(); // typedef name -> object number

    /**
     * List of known builders.
     */
    private Vector typedefsLoaded = new Vector();	// Contains the names of all active builders

    /**
     * Constructor
     */
    public TypeDef() {
        // set broadcasting of changes to false
        broadcastChanges=false;
    }

    /**
     * Initializes the typedef builder.
     * Loads a name-to-type and type-to-name cache of active builders.
     * @return true if init was completed, false if uncompleted.
     */
    public boolean init() {
        boolean result = super.init();
        readCache(); // read type info into the caches
        return result;
    }

    /**
     * Fill the typedef caches with the initial values.
     * Caches filled are a number-to-name, name-to-number, and
     * number-to-description cache
     * @duplicate should be moved to org.mmbase.cache
     * @return always true
     */
    public boolean readCache() {
        log.service("Reading typedef caches");
        Integer number;
        String name;
        for (Enumeration e = search(null); e.hasMoreElements();) {
            MMObjectNode n= (MMObjectNode) e.nextElement();
            number= n.getIntegerValue("number");
            name  = n.getStringValue("name");
            if (number != null && name != null) {
                numberCache.put(name,number);
                nameCache.put(number,name);
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
        Integer result=(Integer)numberCache.get(builderName);
        if (result!=null) {
            return result.intValue();
        } else {
            // XXX: a bit ugly to do this every time a match fails...
            readCache();
            result = (Integer) numberCache.get(builderName);
            if (result != null) {
                return result.intValue();
            }
            return -1;
        }
    }

    /**
     * Obtain the buildername of the requested type
     * @param type the object type
     * @return the name of the builder as a string, null if not found
     */
    public String getValue(int type) {
        String result = (String) nameCache.get(new Integer(type));
        if (result == null) {
            // XXX: it is ugly here too (see getIntValue()).
            // but sometimes necessary (when starting Versions)
            readCache();
            result = (String) nameCache.get(new Integer(type));
            if (result == null) {
                log.error("Could not find builder name for typdef number " + type);
            }
        }
        return result;
    }


    /**
     * Obtain the buildername of the requested type
     * @param type the object type
     * @return the name of the builder as a string, "unknown" if not found
     */
    public String getValue(String type) {
        try {
            return (String) nameCache.get(new Integer(Integer.parseInt(type)));
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
    public String getGUIIndicator(String field,MMObjectNode node) {
        if (field.equals("name")) {
            String name=node.getStringValue("name");
            String guiname= getSingularName(name,null);
            return guiname;
        }
        return null;
    }

    /**
     * @javadoc
     */
    public void loadTypeDef(String name) {
        if(!typedefsLoaded.contains(name)) {
            typedefsLoaded.add(name);
        } else {
            log.debug("Builder "+name+" is already loaded!");
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

}
