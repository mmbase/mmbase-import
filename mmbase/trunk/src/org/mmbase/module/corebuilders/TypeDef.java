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
    Hashtable nameCache; // object number -> typedef name

    /**
     * Name-to-number cache.
     * @duplicate should be moved to org.mmbase.cache
     */
    Hashtable numberCache=new Hashtable(); // typedef name -> object number

    /**
     * Number-to-description cache.
     * @deprecated not used.
     */
    Hashtable descriptionCache;
    public boolean broadcastChanges=false;
    public Vector typedefsLoaded=new Vector();	// Contains the names of all active builders

    public TypeDef() {
    }

    public boolean init() {
        super.init();
        mmb.mmobjs.put(tableName,this);
        readCache(); // read type info into the caches
        return true;
    }

    /**
     * Fill the typedef caches with the initial values.
     * Caches filled are a number-to-name, name-to-number, and
     * number-to-description cache
     * @duplicate should be moved to org.mmbase.cache
     */
    public boolean readCache() {
        Integer number;
        String name,desc;
        nameCache=new Hashtable();
        descriptionCache=new Hashtable();
        for (Enumeration e=search(null);e.hasMoreElements();) {
            MMObjectNode n= (MMObjectNode)e.nextElement();
            number=n.getIntegerValue("number");
            name=n.getStringValue("name");
            desc=n.getStringValue("description");
            numberCache.put(name,number);
            nameCache.put(number,name);
            descriptionCache.put(number,desc);
         }
        return true;
    }

    /**
     * Obtain the type value of the requested type, returns -1 if not defined.
     */
    public int getIntValue(String value) {
        Integer result=(Integer)numberCache.get(value);
        if (result!=null) {
            return result.intValue();
        } else {
            readCache();
            result=(Integer)numberCache.get(value);
            if (result!=null) {
                return result.intValue();
            }
            return -1;
        }
    }

    /**
     * obtain the type value of the requested type, returns -1 if not defined.
     */
    public String getValue(int type) {
        return (String)nameCache.get(new Integer(type));
    }


    /**
     * obtain the type value of the requested type, returns -1 if not defined.
     */
    public String getValue(String type) {
        try {
            return (String)nameCache.get(new Integer(Integer.parseInt(type)));
        } catch(Exception e) {
            return "unknown";
        }
    }

    public String getDutchSName(String name) {
        if (name==null) return "ERROR";
        MMObjectBuilder bul=(MMObjectBuilder)mmb.mmobjs.get(name);
        if (bul!=null) {
            return bul.getDutchSName();
        } else {
            return "inactive ("+name+".xml)";
        }
    }


    public String getEnglishName(String dutchname) {
        Enumeration enum = mmb.mmobjs.elements();
        while (enum.hasMoreElements()){
            MMObjectBuilder bul=(MMObjectBuilder)enum.nextElement();
            if (bul.getDutchSName().equals(dutchname)) {
                return bul.tableName;
            }
        }
        return "inactive ("+dutchname+".xml)";
    }

    public boolean isRelationTable(String name) {
        return mmb.getRelDef().isRelationTable(name);
    }

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
            String name=node.getStringValue("name");
            return getDutchSName(name);
        }
        return super.getValue(node,field);
    }

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

    public boolean reloadBuilder(String objectname) {
        log.service("MMBASE -> Trying to reload builder : "+objectname);
        // first get all the info we need from the builder allready running
        MMObjectBuilder oldbul=mmb.getMMObject(objectname);
        String classname=oldbul.getClassName();
        String description=oldbul.getDescription();
        String dutchsname=oldbul.getDutchSName();

        try {
            Class newclass=Class.forName("org.mmbase.module.builders."+classname);
            log.debug("TypeDef -> Loaded load class : "+newclass);

            MMObjectBuilder bul = (MMObjectBuilder)newclass.newInstance();
            log.debug("TypeDef -> started : "+newclass);

            bul.setMMBase(mmb);
            bul.setTableName(objectname);
            bul.setDescription(description);
            bul.setDutchSName(dutchsname);
            bul.setClassName(classname);
            bul.init();
            mmb.mmobjs.put(objectname,bul);
            } catch (Exception e) {
                log.error(Logging.stackTrace(e));
                return false;
            }
            return true;
        }


    public String getGUIIndicator(MMObjectNode node) {
        if (node!=null) {
            String name=node.getStringValue("name");
            if (name==null) {
                log.error("TypeDef-> problem node "+node);
                return "problem";
            } else {
                return name;
            }
        } else {
            log.error("TypeDef-> problem node empty");
            return "problem";
        }
    }

    /**
     *	Handle a $MOD command
     */
    public String replace(scanpage sp, StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (cmd.equals("D2E")) {
                if (tok.hasMoreTokens()) {
                    return getEnglishName(tok.nextToken());
                }
            }
        }
        return "";
    }

    /**
     * Return the database type of the objecttype
     * @deprecated-now use default behavior
     */
    public int getDBType(String fieldName) {
        if (fieldName.equals("owner")) return FieldDefs.TYPE_STRING;
        if (fieldName.equals("otype")) return FieldDefs.TYPE_INTEGER;
        if (fieldName.equals("number")) return FieldDefs.TYPE_INTEGER;
        if (fieldName.equals("name")) return FieldDefs.TYPE_STRING;
        if (fieldName.equals("description")) return FieldDefs.TYPE_STRING;
        return -1;
    }

    public void loadTypeDef(String name) {
        if(!typedefsLoaded.contains(name)) {
            typedefsLoaded.add(name);
        } else {
            log.debug("Builder "+name+" is already loaded!");
        }
    }

    public void unloadTypeDef(String name) {
        if(typedefsLoaded.contains(name)) {
            typedefsLoaded.remove(name);
        } else {
            log.debug("Builder "+name+" is not loaded!");
        }
    }

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
