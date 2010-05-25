/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * The module which provides access to a filesystem residing in a database.
 *
 * @application SCAN
 * @rename SessionInfo
 * @author Daniel Ockeloen
 */
public class sessionInfo {

    private static Logger log = Logging.getLoggerInstance(sessionInfo.class.getName());

    private String hostname;
    private String cookie;
    private MMObjectNode node;

    Hashtable<String, String> values = new Hashtable<String, String>();
    Hashtable<String, Vector<String>> setvalues = new Hashtable<String, Vector<String>>();

    public void setNode(MMObjectNode node) {
        this.node=node;
    }

    public MMObjectNode getNode() {
        return(node);
    }

    public String getCookie() {
        return(cookie);
    }

    public String getValue(String wanted) {
        return values.get(wanted);
    }

    public String setValue(String key,String value) {
        if (isSecure(value)) {
            return values.put(key,value);
        } else {
            log.error("ERROR: Illegal input, action blocked");
            return("illegal input,see error log");
        }
    }

    /**
     * Removes (clears) a value from a session.
     * @param key the key of the attribute to clear
     * @return the original value of the attribute
     */
    public String removeValue(String key) {
        return values.remove(key);
    }

    /**
    * adds a value to a set, no duplicates are allowed.
    */
    public void addSetValue(String key,String value) {

        log.debug("addSetValue("+key+","+value+")");

        Vector<String> v=setvalues.get(key);
        if (v==null) {
            // not found so create it
            v=new Vector<String>();
            if (isSecure(value)) {
                v.addElement(value);
                setvalues.put(key,v);
            } else {
                log.error("ERROR: Illegal input, action blocked");
            }
            log.debug("sessionset="+v.toString());
        } else {
            if (!v.contains(value)) {
                v.addElement(value);
                log.debug("sessionset="+v.toString());
            }
        }
        log.debug("addSetValue() -> getSetString("+key+"): " +getSetString(key));
    }


    /**
    * add a value to a set, duplicates are allowed.
    */
    public void putSetValue(String key,String value) {

        log.debug("putSetValue("+key+","+value+")");

        Vector<String> v=setvalues.get(key);
        if (v==null) {
            // not found so create it
            v=new Vector<String>();
            if (isSecure(value)) {
                v.addElement(value);
                setvalues.put(key,v);
            } else {
                log.error("ERROR: Illegal input, action blocked");
            }
            log.debug("sessionset="+v.toString());
        } else {
            if (isSecure(value)) {
                v.addElement(value);
            } else {
                log.error("ERROR: Illegal input, action blocked");
            }
            log.debug("sessionset="+v.toString());
        }
    }


    /**
    * deletes a value from the SESSION set.
    */
    public void delSetValue(String key,String value) {
        Vector v=setvalues.get(key);
        if (v!=null) {
            if (v.contains(value)) {
                v.removeElement(value);
                log.debug("sessionset="+v.toString());
            }
        }
    }


    /**
    * does this set contain the value ?
    */
    public String containsSetValue(String key,String value) {
        Vector v=setvalues.get(key);
        if (v!=null) {
            if (v.contains(value)) {
                return("YES");
            }
        }
        return("NO");
    }


    /**
    * delete the values belonging to the key
    */
    public String clearSet(String key) {
        log.debug("sessionset="+key);
        Vector<String> v=setvalues.get(key);
        if (v!=null) {
            v=new Vector<String>();
            setvalues.put(key,v);
            log.debug("sessionset="+v.toString());
        }
        return("");
    }


    /**
    * returns the session variable values comma separaterd
    * @param key the name of the session variable
    */
    public String getSetString(String key) {

        log.debug("getSetString("+key+")");

        Vector v=setvalues.get(key);
        if (v!=null) {
            String result="";
            Enumeration res=v.elements();
            while (res.hasMoreElements()) {
                String tmp=(String)res.nextElement();
                if (result.equals("")) {
                    result=tmp;
                } else {
                    result+=","+tmp;
                }
            }
            return(result);
        } else {
            log.error("getSetString("+key+"): ERROR: this key is non-existent!");
            return(null);
        }
    }

    /**
    * return the number of values contained by a session variable
    */
    public String getSetCount(String key) {

        Vector v=setvalues.get(key);
        if (v!=null) {
            return(""+v.size());
        } else {
            return(null);
        }
    }


    /**
    * return the average of a set of numbers
    */
    public String getAvgSet(String key) {
        Vector v=setvalues.get(key);
        if (v!=null) {
            int total=0;
            int count=0;
            Enumeration res=v.elements();
            while (res.hasMoreElements()) {
                try {
                    String tmp=(String)res.nextElement();
                    int tmpi=Integer.parseInt(tmp);
                    total+=tmpi;
                    count++;
                } catch(Exception e) {}
            }
            int res1=total/count;
            return(""+res1);
        } else {
            return(null);
        }
    }

    /**
     * returns the hostname of a user
     */
    public String getHostName() {
        return(hostname);
    }

    public sessionInfo(String hostname, String cookie) {
        this.hostname=hostname;
        this.cookie=cookie;
    }

    public sessionInfo(String hostname) {
        this.hostname=hostname;
    }

    public String toString() {
        return("sessionInfo="+values.toString());
    }


    private boolean isSecure(String value) {

        Vector words=new Vector();
        words.addElement("<transaction");
        words.addElement("<TRANSACTION");
        words.addElement("<create");
        words.addElement("<createObject");
        words.addElement("<delete");
        words.addElement("<mark");
        words.addElement("<setField");
        words.addElement("</setField>");
        words.addElement("<DO");
        Enumeration e = words.elements();
        while (e.hasMoreElements()) {
            String check=(String)e.nextElement();
            if (value.indexOf(check)!=-1) {
                log.error(check+" found in session variable");
                return(false);
            }
        }
        return(true);
    }
}

