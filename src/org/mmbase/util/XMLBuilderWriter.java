/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.*;
import java.util.*;
import org.mmbase.module.core.*;

import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;

/**
 * Class for creating builder configuration files.
 *
 * @deprecated-now use org.mmbase.xml.BuilderWriter instead
 * @author Daniel Ockeloen
 * @version $Id: XMLBuilderWriter.java,v 1.16 2002-03-26 13:16:32 pierre Exp $
 */
public class XMLBuilderWriter  {

    // logger
    private static Logger log = Logging.getLoggerInstance(XMLBuilderWriter.class.getName());

    /**
     * Creates a builder configuration file.
     * @param filename the filename where the configuration is to be stored
     * @param bul the builder to store
     * @return always <code>true</code>
     */
    public static boolean writeXMLFile(String filename,MMObjectBuilder bul) {
        String header = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+
                        "<!DOCTYPE builder PUBLIC \"//MMBase - builder//\" \"http://www.mmbase.org/dtd/builder.dtd\">\n";

        StringBuffer body = new StringBuffer(500);
        body.append(header);
        body.append("<builder maintainer=\"");
        body.append(bul.getMaintainer());
        body.append("\" version=\"");
        body.append(bul.getVersion());
        body.append("\">\n\n");

        // status
        body.append("<!-- <status>\n");
        body.append("\twhat is the status of this builder options : active or inactive\n");
        body.append("-->\n");
        body.append("<status>active</status>\n\n");


        // classfile
        body.append("<!-- <classfile>\n");
        body.append("\tThis is a optional tag, it names the classfile that allows you to add\n");
        body.append("\tsupport routines or changes them on a per builder base. For example\n");
        body.append("\timages has routines to image support etc etc.\n\n");
        body.append("\tpossible values : Dummy or disabled tag makes mmbase use the default setup\n\n");
        body.append("\t\tshort name : Urls will start class : org.mmbase.module.builders.Urls.class\n\n");
        body.append("\t\tlong name : nl.vpro.mmbase.module.builders.Urls will start that class\n");
        body.append("-->\n");
        body.append("<classfile>"+bul.getClassName()+"</classfile>\n\n");

        // searchage
        body.append("<searchage>"+bul.getSearchAge()+"</searchage>\n\n");

        // names
        body.append("<!-- <names>\n");
        body.append("\tnames defines the different names used in user visible parts, singular for\n");
        body.append("\tfor talking about 1 object (Url,Person) and plurar for talking about multiple\n");
        body.append("\t<Urls, people).\n>");
        body.append("-->\n");
        body.append("<names>\n");
        body.append("\t<!-- singles per language as defined by ISO 639 -->\n");

        Hashtable names=bul.getSingularNames();
        if (names!=null) {
            for (Enumeration e=names.keys();e.hasMoreElements();) {
                String country=(String)e.nextElement();
                String name=(String)names.get(country);
                body.append("\t<singular xml:lang=\""+country+"\">"+name+"</singular>\n");
            }
        } else {
            body.append("\t<singular xml:lang=\"us\">"+bul.getDutchSName()+"</singular>\n");
        }

        body.append("\t<!-- multiple per language as defined by ISO 639 -->\n");


        names=bul.getPluralNames();
        if (names!=null) {
            for (Enumeration e=names.keys();e.hasMoreElements();) {
                String country=(String)e.nextElement();
                String name=(String)names.get(country);
                body.append("\t<plural xml:lang=\""+country+"\">"+name+"</plural>\n");
            }
        } else {
            body.append("\t<plural xml:lang=\"us\">"+bul.getDutchSName()+"</plural>\n");
        }
        body.append("</names>\n\n");


        // descriptions
        body.append("<!-- <descriptions>\n");
        body.append("\tsmall description of the builder for human reading\n");
        body.append("-->\n");
        body.append("<descriptions>\n");
        body.append("<!-- descriptions per language as defined by ISO 639  -->\n");

        // XXX:escape descriptions and store them as CDATA

        names=bul.getDescriptions();
        if (names!=null) {
            for (Enumeration e=names.keys();e.hasMoreElements();) {
                String country=(String)e.nextElement();
                String name=(String)names.get(country);
                body.append("\t<description xml:lang=\""+country+"\">"+name+"</description>\n");
            }
        } else {
            body.append("\t<description xml:lang=\"us\">"+bul.getDescription()+"</description>\n");
        }

        body.append("</descriptions>\n\n");

        // properties
        body.append("<!-- <properties>\n");
        body.append("you can define properties to be used by the classfile (if used) it uses\n");
        body.append("a key/value system. Its a optional tag.\n");
        body.append("-->\n");
        body.append("<properties>\n");
        Hashtable props=bul.getInitParameters();
        if (props!=null) {
            for (Enumeration e=props.keys();e.hasMoreElements();) {
                String name=(String)e.nextElement();
                String value=(String)props.get(name);
                body.append("\t<property name=\""+name+"\">"+value+"</property>\n");
            }
        }
        body.append("</properties>\n\n");

        // fieldlists
        body.append("<!-- <fieldlist>\n");
        body.append("\t defines the different fields in this object, be carefull the order is important\n");
        body.append("\tonce defined keep them in the same order.\n");
        body.append("\tIf you use number and owner (the 2 default fields, please check the docs for this)\n");
        body.append("-->\n");
        body.append("<fieldlist>\n");

        Vector fields=bul.sortedDBLayout;
        if (fields!=null) {
            body.append("\t<!-- POS 1 : <field> 'number' is a default field (see docs) -->\n");
            body.append("\t<field>\n");
            body.append("\t\t<!-- gui related -->\n");
            body.append("\t\t<gui>\n");

            FieldDefs def=bul.getField("number");
            names=def.getGUINames();
            if (names!=null) {
                for (Enumeration ee=names.keys();ee.hasMoreElements();) {
                    String country=(String)ee.nextElement();
                    String name=(String)names.get(country);
                    body.append("\t\t\t<guiname xml:lang=\""+country+"\">"+name+"</guiname>\n");
                }
            } else {
                body.append("\t\t\t<guiname xml:lang=\"us\">"+def.getGUIName()+"</guiname>\n");
            }
            //	body.append("\t\t\t<guiname xml:lang=\"us\">Object</guiname>\n";
            body.append("\t\t\t<guitype>integer</guitype>\n");
            body.append("\t\t</gui>\n");
            body.append("\t\t<!-- editor related  -->\n");
            body.append("\t\t<editor>\n");
            body.append("\t\t\t<positions>\n");
            body.append("\t\t\t\t<!-- position in the input area of the editor -->\n");
            body.append("\t\t\t\t<input>-1</input>\n");
            body.append("\t\t\t\t<!-- position in list area of the editor -->\n");
            body.append("\t\t\t\t<list>10</list>\n");
            body.append("\t\t\t\t<!-- position in search area of the editor -->\n");
            body.append("\t\t\t\t<search>10</search>\n");
            body.append("\t\t\t</positions>\n");
            body.append("\t\t</editor>\n");
            body.append("\t\t<!-- database related  -->\n");
            body.append("\t\t<db>\n");
            body.append("\t\t\t<!-- name of the field in the database -->\n");
            body.append("\t\t\t<name>number</name>\n");
            body.append("\t\t\t<!-- MMBase datatype and demands on it -->\n");
            body.append("\t\t\t<type state=\"persistent\" notnull=\"true\" key=\"true\">INTEGER</type>\n");
            body.append("\t\t</db>\n");
            body.append("\t</field>\n\n");

            int i=2;
            for (Enumeration e=fields.elements();e.hasMoreElements();) {
                String fieldn=(String)e.nextElement();
                if (!fieldn.equals("otype")) {
                    def=bul.getField(fieldn);
                    if (def.getDBName().equals("owner")) {
                        body.append("\t<!-- POS "+(i++)+" : <field> '"+def.getDBName()+"' is a default field (see docs) -->\n");
                    } else {
                        body.append("\t<!-- POS "+(i++)+" : <field> '"+def.getDBName()+"'  -->\n");
                    }
                    body.append("\t<field>\n");
                    body.append("\t\t<!-- gui related -->\n");
                    body.append("\t\t<gui>\n");
                    names=def.getGUINames();
                    if (names!=null) {
                        for (Enumeration ee=names.keys();ee.hasMoreElements();) {
                            String country=(String)ee.nextElement();
                            String name=(String)names.get(country);
                            body.append("\t\t\t<guiname xml:lang=\""+country+"\">"+name+"</guiname>\n");
                        }
                    } else {
                        body.append("\t\t\t<guiname xml:lang=\"us\">"+def.getGUIName()+"</guiname>\n");
                    }
                    body.append("\t\t\t<guitype>"+def.getGUIType()+"</guitype>\n");
                    body.append("\t\t</gui>\n");
                    body.append("\t\t<!-- editor related  -->\n");
                    body.append("\t\t<editor>\n");
                    body.append("\t\t\t<positions>\n");
                    body.append("\t\t\t\t<!-- position in the input area of the editor -->\n");
                    body.append("\t\t\t\t<input>"+def.getGUIPos()+"</input>\n");
                    body.append("\t\t\t\t<!-- position in list area of the editor -->\n");
                    body.append("\t\t\t\t<list>"+def.getGUIList()+"</list>\n");
                    body.append("\t\t\t\t<!-- position in search area of the editor -->\n");
                    body.append("\t\t\t\t<search>"+def.getGUISearch()+"</search>\n");
                    body.append("\t\t\t</positions>\n");
                    body.append("\t\t</editor>\n");
                    body.append("\t\t<!-- database related  -->\n");
                    body.append("\t\t<db>\n");
                    body.append("\t\t\t<!-- name of the field in the database -->\n");
                    body.append("\t\t\t<name>"+def.getDBName()+"</name>\n");
                    body.append("\t\t\t<!-- MMBase datatype and demands on it -->\n");

                    String tmps = FieldDefs.getDBStateDescription(def.getDBState());
                    String tmpt = FieldDefs.getDBTypeDescription(def.getDBType());
                    int size=def.getDBSize();

                    if (size==-1) {
                        body.append("\t\t\t<type state=\""+tmps+"\" notnull=\""+def.getDBNotNull()+"\" key=\""+def.isKey()+"\">"+tmpt+"</type>\n");
                    } else {
                        body.append("\t\t\t<type state=\""+tmps+"\" size=\""+size+"\" notnull=\""+def.getDBNotNull()+"\" key=\""+def.isKey()+"\">"+tmpt+"</type>\n");
                    }

                    body.append("\t\t</db>\n");
                    body.append("\t</field>\n\n");
                }
            }
        }
        body.append("</fieldlist>\n\n");

        // the end of the builder file
        body.append("</builder>");

        // print it  XXX: should be :  return saveFile(filename,body);  (?)
        saveFile(filename,body.toString());
        return true;
    }

    /**
     * Writes a text to a file.
     * @param filename the filename where the text is to be stored
     * @param value the text to write
     * @return always <code>true</code>
     */
    static boolean saveFile(String filename,String value) {
        PrintWriter writer = null;
        boolean retVal = true;
        try {
            writer = new PrintWriter(new FileWriter(new File(filename)));
            writer.print(value);
            writer.flush();
        }
        catch(IOException e) {
            log.error(e.getMessage());
            log.error(Logging.stackTrace(e));
            retVal = false;
        }
        finally {
            if(writer != null) {
                if(writer.checkError()) {
                    log.error("saving file -"+filename+"- failed");
                    retVal = false;
                }
                writer.close();
            }
        }
        return retVal;
    }
}
