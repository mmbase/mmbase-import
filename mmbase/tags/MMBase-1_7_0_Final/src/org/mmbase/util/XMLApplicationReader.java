/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;

import org.w3c.dom.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Case Roole
 * @author Rico Jansen
 * @author Pierre van Rooden
 * @version $Id: XMLApplicationReader.java,v 1.19 2003-04-10 13:51:37 pierre Exp $
 */
public class XMLApplicationReader extends XMLBasicReader {

    // logger
    private static Logger log = Logging.getLoggerInstance(XMLApplicationReader.class.getName());

    /** Public ID of the Application DTD version 1.0 */
    public static final String PUBLIC_ID_APPLICATION_1_0 = "-//MMBase//DTD application config 1.0//EN";
    private static final String PUBLIC_ID_APPLICATION_1_0_FAULT = "-//MMBase/DTD application config 1.0//EN";
    /** Public ID of the Application DTD version 1.1 */
    public static final String PUBLIC_ID_APPLICATION_1_1 = "-//MMBase//DTD application config 1.1//EN";

    /** DTD resource filename of the Application DTD version 1.0 */
    public static final String DTD_APPLICATION_1_0 = "application_1_0.dtd";
    /** DTD resource filename of the Application DTD version 1.1 */
    public static final String DTD_APPLICATION_1_1 = "application_1_1.dtd";

    /** Public ID of the most recent Application DTD */
    public static final String PUBLIC_ID_APPLICATION = PUBLIC_ID_APPLICATION_1_1;
    /** DTD resource filename of the most Application DTD */
    public static final String DTD_APPLICATION = DTD_APPLICATION_1_1;

    /**
     * Register the Public Ids for DTDs used by XMLApplicationReader
     * This method is called by XMLEntityResolver.
     */
    public static void registerPublicIDs() {
        // various builder dtd versions
        XMLEntityResolver.registerPublicID(PUBLIC_ID_APPLICATION_1_0, DTD_APPLICATION_1_0, XMLApplicationReader.class);
        XMLEntityResolver.registerPublicID(PUBLIC_ID_APPLICATION_1_1, DTD_APPLICATION_1_1, XMLApplicationReader.class);

        // legacy public IDs (wrong, don't use these)
        XMLEntityResolver.registerPublicID(PUBLIC_ID_APPLICATION_1_0_FAULT, DTD_APPLICATION_1_0, XMLApplicationReader.class);
    }

    private Element root;

    public XMLApplicationReader(String filename) {
        super(filename, XMLApplicationReader.class);
        root=getElementByPath("application");
    }

    /**
     * Get the name of this application
     */
    public String getApplicationName() {
        return getElementAttributeValue(root,"name");
    }


    /**
     * Get the version of this application
     */
    public int getApplicationVersion() {
        String ver=getElementAttributeValue(root,"version");
        if (!ver.equals(""))
            try {
                return Integer.parseInt(ver);
            } catch (Exception e) {
                return -1;
            }
        else
            return -1;
    }

    /**
     * Get the auto-deploy value of this application
     */
    public boolean getApplicationAutoDeploy() {
        return getElementAttributeValue(root,"auto-deploy").equals("true");
    }

    /**
     * Get the maintainer of this application
     */
    public String getApplicationMaintainer() {
        return getElementAttributeValue(root,"maintainer");
    }

    /**
     * Get the applicationlist required by this application
     */
    public List getRequirements() {
        List results=new Vector();
        for(Enumeration ns=getChildElements("application.requirements","requires");
            ns.hasMoreElements(); ) {
            Element n3=(Element)ns.nextElement();
            Map bset=new HashMap();
            bset.put("name",getElementAttributeValue(n3,"name"));
            addAttribute(bset,n3,"maintainer");
            addAttribute(bset,n3,"version");
            addAttribute(bset,n3,"type");
            results.add(bset);
        }
        return results;
    }

    private void addAttribute(Map bset, Element n, String attribute) {
        String val=n.getAttribute(attribute);
        if (!val.equals("")) {
            bset.put(attribute,val);
        }
    }

    /**
     * Get the Builders needed for this application
     */
    public Vector getNeededBuilders() {
        Vector results=new Vector();
        for(Enumeration ns=getChildElements("application.neededbuilderlist","builder");
            ns.hasMoreElements(); ) {
            Element n3=(Element)ns.nextElement();
            Hashtable bset=new Hashtable();
            bset.put("name",getElementValue(n3));
            addAttribute(bset,n3,"maintainer");
            addAttribute(bset,n3,"version");
            results.addElement(bset);
        }
        return results;
    }


    /**
     * Get the RelDefs needed for this application
     */
    public Vector getNeededRelDefs() {
        Vector results=new Vector();
        for(Enumeration ns=getChildElements("application.neededreldeflist","reldef");
            ns.hasMoreElements(); ) {
            Element n3=(Element)ns.nextElement();
            Hashtable bset=new Hashtable();
            addAttribute(bset,n3,"source");
            addAttribute(bset,n3,"target");
            addAttribute(bset,n3,"direction");
            addAttribute(bset,n3,"guisourcename");
            addAttribute(bset,n3,"guitargetname");
            addAttribute(bset,n3,"builder");
            results.addElement(bset);
        }
        return results;
    }


    /**
     * Get allowed relations for this application
     */
    public Vector getAllowedRelations() {
        Vector results=new Vector();
        for(Enumeration ns=getChildElements("application.allowedrelationlist","relation");
            ns.hasMoreElements(); ) {
            Element n3=(Element)ns.nextElement();
            Hashtable bset=new Hashtable();
            addAttribute(bset,n3,"from");
            addAttribute(bset,n3,"to");
            addAttribute(bset,n3,"type");
            results.addElement(bset);
        }
        return results;
    }

    /**
     * Get datasources attached to this application
     */
    public Vector getDataSources() {
        Vector results=new Vector();
        for(Enumeration ns=getChildElements("application.datasourcelist","datasource");
            ns.hasMoreElements(); ) {
            Element n3=(Element)ns.nextElement();
            Hashtable bset=new Hashtable();
            addAttribute(bset,n3,"path");
            addAttribute(bset,n3,"builder");
            results.addElement(bset);
        }
        return results;
    }


    /**
     * Get relationsources attached to this application
     */
    public Vector getRelationSources() {
        Vector results=new Vector();
        for(Enumeration ns=getChildElements("application.relationsourcelist","relationsource");
            ns.hasMoreElements(); ) {
            Element n3=(Element)ns.nextElement();
            Hashtable bset=new Hashtable();
            addAttribute(bset,n3,"path");
            addAttribute(bset,n3,"builder");
            results.addElement(bset);
        }
        return results;
    }

    /**
     * contextsources attached to this application
     */
    public Vector getContextSources() {
        Vector results=new Vector();
        for(Enumeration ns=getChildElements("application.contextsourcelist","contextsource"); ns.hasMoreElements(); ) {
            Element n3=(Element)ns.nextElement();
            Hashtable bset=new Hashtable();
            addAttribute(bset,n3,"path");
            addAttribute(bset,n3,"type");
            addAttribute(bset,n3,"goal");
            results.addElement(bset);
        }
        return results;
    }

    /**
     * Get the installation notices for this application
     */
    public String getInstallNotice() {
        return getElementValue("application.install-notice");
    }


    /**
     * Get the description for this application
     */
    public String getDescription() {
        return getElementValue("application.description");
    }

}
