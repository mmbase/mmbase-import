/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.corebuilders;

import java.util.*;
import java.sql.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
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

    /**
    * Logger routine
    */
    private static Logger log = Logging.getLoggerInstance(TypeDef.class.getName());

    Hashtable nameCache; 						// object number -> typedef name
    Hashtable numberCache=new Hashtable(); 		// typedef name -> object number
    Hashtable descriptionCache; 				// object number -> typedef description
    public boolean broadcastChanges=false;
    public Vector typedefsLoaded=new Vector();	// Contains the names of all active builders

    public TypeDef() {
    }

    public boolean init() {
        super.init();
        mmb.mmobjs.put(tableName,this);
        readCache(); // read type info into the caches
        return(true);
    }


    public boolean readCache() {
        try {
            MultiConnection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            //System.out.println("SELECT * FROM "+mmb.baseName+"_"+tableName);
            ResultSet rs=stmt.executeQuery("SELECT * FROM "+mmb.baseName+"_"+tableName);
            nameCache=new Hashtable();
            //numberCache=new Hashtable();
            descriptionCache=new Hashtable();
            Integer number;String name,desc;
            while(rs.next()) {
                number=new Integer(rs.getInt(1));
                name=rs.getString(4);
                desc=rs.getString(5);
                // System.out.println("NUMBER="+number+" NAME="+name+" DESC="+desc);
                numberCache.put(name,number);
                nameCache.put(number,name);
                if (desc==null) desc="";
                descriptionCache.put(number,desc);
            }
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return(true);
    }


    boolean checkRootNode() {
        try {
            MultiConnection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT number FROM "+mmb.baseName+"_object where number=0");
            if (rs.next()) {
                stmt.close();
                con.close();
                return(true);
            } else {
                stmt.close();
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return(false);
    }


    /**
    * obtain the type value of the requested type, returns -1 if not defined.
    */
    public int getIntValue(String value) {
        Integer result=(Integer)numberCache.get(value);
        if (result!=null) {
            return(result.intValue());
        } else {
            readCache();
            result=(Integer)numberCache.get(value);
            if (result!=null) {
                return(result.intValue());
            }
            return(-1);
        }
    }

    /**
    * obtain the type value of the requested type, returns -1 if not defined.
    */
    public String getValue(int type) {
        return((String)nameCache.get(new Integer(type)));
    }


    /**
    * obtain the type value of the requested type, returns -1 if not defined.
    */
    public String getValue(String type) {
        try {
            return((String)nameCache.get(new Integer(Integer.parseInt(type))));
        } catch(Exception e) {
            return("unknown");
        }
    }

    public String getDutchSName(String name) {
        if (name==null) return("ERROR");
        MMObjectBuilder bul=(MMObjectBuilder)mmb.mmobjs.get(name);
        if (bul!=null) {
            return(bul.getDutchSName());
        } else {
            return("inactive ("+name+".xml)");
        }
    }


    public String getEnglishName(String dutchname) {
        Enumeration enum = mmb.mmobjs.elements();
        while (enum.hasMoreElements()){
            MMObjectBuilder bul=(MMObjectBuilder)enum.nextElement();
            if (bul.getDutchSName().equals(dutchname)) {
                return(bul.tableName);
            }
        }
        return("inactive ("+dutchname+".xml)");
    }

    public boolean isRelationTable(String name) {
        return(mmb.getRelDef().isRelationTable(name));
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
            //System.out.println("STATE="+val+" "+node);
            return(""+val);
        } else if (field.equals("dutchs(name)")) {
            String name=node.getStringValue("name");
            return(getDutchSName(name));
        }
        return super.getValue(node,field);
    }


    public boolean fieldLocalChanged(String number,String builder,String field,String value) {
        if (field.equals("state")) {
            if (value.equals("4")) {
                // reload request
                System.out.println("Reload wanted on : "+builder);
                // perform reload
                MMObjectNode node=getNode(number);
                String objectname=node.getStringValue("name");
                reloadBuilder(objectname);
                if (node!=null) {
                    node.setValue("state",1);
                }
            }
        }
        return(true);
    }



    public boolean reloadBuilder(String objectname) {
        System.out.println("MMBASE -> Trying to reload builder : "+objectname);
        // first get all the info we need from the builder allready running
        MMObjectBuilder oldbul=mmb.getMMObject(objectname);
        String classname=oldbul.getClassName();
        String description=oldbul.getDescription();
        String dutchsname=oldbul.getDutchSName();

        try {
            Class newclass=Class.forName("org.mmbase.module.builders."+classname);
                System.out.println("TypeDef -> Loaded load class : "+newclass);

            MMObjectBuilder bul = (MMObjectBuilder)newclass.newInstance();
            System.out.println("TypeDef -> started : "+newclass);

            bul.setMMBase(mmb);
            bul.setTableName(objectname);
            bul.setDescription(description);
            bul.setDutchSName(dutchsname);
            bul.setClassName(classname);
            bul.init();
            mmb.mmobjs.put(objectname,bul);
            } catch (Exception e) {
                e.printStackTrace();
                return(false);
            }
            return(true);
        }


    public String getGUIIndicator(MMObjectNode node) {
        if (node!=null) {
            String name=node.getStringValue("name");
            if (name==null) {
                System.out.println("TypeDef-> problem node "+node);
                return("problem");
            } else {
                return(name);
            }
        } else {
            System.out.println("TypeDef-> problem node empty");
            return("problem");
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
                    return(getEnglishName(tok.nextToken()));
                }
            }
        }
        return("");
    }


    /**
    * return the database type of the objecttype
    */
    public int getDBType(String fieldName) {
        if (fieldName.equals("owner")) return(FieldDefs.TYPE_STRING);
        if (fieldName.equals("otype")) return(FieldDefs.TYPE_INTEGER);
        if (fieldName.equals("number")) return(FieldDefs.TYPE_INTEGER);
        if (fieldName.equals("name")) return(FieldDefs.TYPE_STRING);
        if (fieldName.equals("description")) return(FieldDefs.TYPE_STRING);
        return(-1);
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

    public Vector  getList(scanpage sp,StringTagger tagger, StringTokenizer tok) throws ParseException {
        System.out.println("Tataaaaa");
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (cmd.equals("builders")) {
                return typedefsLoaded;
            }
        }
        return null;
    }

}
