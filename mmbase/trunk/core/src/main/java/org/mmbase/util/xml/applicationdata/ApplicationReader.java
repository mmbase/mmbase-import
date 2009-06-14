/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml.applicationdata;

import java.util.*;
import org.w3c.dom.*;
import org.mmbase.util.xml.EntityResolver;

/**
 * @javadoc
 * @author Case Roole
 * @author Rico Jansen
 * @author Pierre van Rooden
 * @version $Id$
 */
public class ApplicationReader extends org.mmbase.util.xml.DocumentReader {

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
     * Register the Public Ids for DTDs used by ApplicationReader
     * This method is called by XMLEntityResolve
     * @since MMBase-1.7
     */
    public static void registerPublicIDs() {
        // various builder dtd versions
        EntityResolver.registerPublicID(PUBLIC_ID_APPLICATION_1_0, DTD_APPLICATION_1_0, ApplicationReader.class);
        EntityResolver.registerPublicID(PUBLIC_ID_APPLICATION_1_1, DTD_APPLICATION_1_1, ApplicationReader.class);

        // legacy public IDs (wrong, don't use these)
        EntityResolver.registerPublicID(PUBLIC_ID_APPLICATION_1_0_FAULT, DTD_APPLICATION_1_0, ApplicationReader.class);
    }

    private Element root;

    public ApplicationReader(org.xml.sax.InputSource is) {
        super(is, ApplicationReader.class);
        root = getElementByPath("application");
    }

    /**
     * @since MMBase-1.8
     */
    public ApplicationReader(Document doc) {
        super(doc);
    }

    /**
     * Get the name of this application
     */
    public String getName() {
        return getElementAttributeValue(root,"name");
    }

    /**
     * Get the version of this application
     */
    public int getVersion() {
        String ver = getElementAttributeValue(root, "version");
        if (!ver.equals("")) {
            try {
                return Integer.parseInt(ver);
            } catch (Exception e) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    /**
     * Get the auto-deploy value of this application
     */
    public boolean hasAutoDeploy() {
        return getElementAttributeValue(root,"auto-deploy").equals("true");
    }

    /**
     * Get the maintainer of this application
     */
    public String getMaintainer() {
        return getElementAttributeValue(root,"maintainer");
    }

    /**
     * Get the applicationlist required by this application
     * @since MMBase-1.7
     */
    public List<Map<String,String>> getRequirements() {
        List<Map<String,String>> results = new ArrayList<Map<String,String>>();
        for (Element n3: getChildElements("application.requirements","requires")) {
            Map<String,String> bset = new HashMap<String,String>();
            bset.put("name",getElementAttributeValue(n3,"name"));
            addAttribute(bset,n3,"maintainer");
            addAttribute(bset,n3,"version");
            addAttribute(bset,n3,"type");
            results.add(bset);
        }
        return results;
    }

    private void addAttribute(Map<String,String> bset, Element n, String attribute) {
        String val = n.getAttribute(attribute);
        if (!val.equals("")) {
            bset.put(attribute,val);
        }
    }

    /**
     * Get the Builders needed for this application
     */
    public List<Map<String,String>> getNeededBuilders() {
        List<Map<String,String>> results = new ArrayList<Map<String,String>>();
        for (Element n3: getChildElements("application.neededbuilderlist","builder")) {
            Map<String,String> bset = new HashMap<String,String>();
            bset.put("name",getElementValue(n3));
            addAttribute(bset,n3,"maintainer");
            addAttribute(bset,n3,"version");
            results.add(bset);
        }
        return results;
    }

    /**
     * Get the RelDefs needed for this application
     */
    public List<Map<String,String>> getNeededRelDefs() {
        List<Map<String,String>> results = new ArrayList<Map<String,String>>();
        for (Element n3: getChildElements("application.neededreldeflist","reldef")) {
            Map<String,String> bset = new HashMap<String,String>();
            addAttribute(bset,n3,"source");
            addAttribute(bset,n3,"target");
            addAttribute(bset,n3,"direction");
            addAttribute(bset,n3,"guisourcename");
            addAttribute(bset,n3,"guitargetname");
            addAttribute(bset,n3,"builder");
            results.add(bset);
        }
        return results;
    }

    /**
     * Get allowed relations for this application
     */
    public List<Map<String,String>> getAllowedRelations() {
        List<Map<String,String>> results=new ArrayList<Map<String,String>>();
        for (Element n3: getChildElements("application.allowedrelationlist","relation")) {
            Map<String,String> bset=new HashMap<String,String>();
            addAttribute(bset,n3,"from");
            addAttribute(bset,n3,"to");
            addAttribute(bset,n3,"type");
            results.add(bset);
        }
        return results;
    }

    /**
     * Get datasources attached to this application
     */
    public List<Map<String,String>> getDataSources() {
        List<Map<String,String>> results = new ArrayList<Map<String,String>>();
        for (Element n3: getChildElements("application.datasourcelist","datasource")) {
            Map<String,String> bset = new HashMap<String,String>();
            addAttribute(bset,n3,"path");
            addAttribute(bset,n3,"builder");
            results.add(bset);
        }
        return results;
    }


    /**
     * Get relationsources attached to this application
     */
    public List<Map<String,String>> getRelationSources() {
        List<Map<String,String>> results = new ArrayList<Map<String,String>>();
        for (Element n3: getChildElements("application.relationsourcelist","relationsource")) {
            Map<String,String> bset = new HashMap<String,String>();
            addAttribute(bset,n3,"path");
            addAttribute(bset,n3,"builder");
            results.add(bset);
        }
        return results;
    }

    /**
     * contextsources attached to this application
     */
    public List<Map<String,String>> getContextSources() {
        List<Map<String,String>> results=new ArrayList<Map<String,String>>();
        for (Element n3: getChildElements("application.contextsourcelist", "contextsource")) {
            Map<String, String> bset = new HashMap<String, String>();
            addAttribute(bset,n3,"path");
            addAttribute(bset,n3,"type");
            addAttribute(bset,n3,"goal");
            results.add(bset);
        }
        return results;
    }

    /**
     * @since MMBase-1.9.2
     */
    public Map<Integer, Runnable> getAfterDeployment() {
        final Map<Integer, Runnable> result = new TreeMap<Integer, Runnable>();
        for (Element element: getChildElements("application.afterdeployment", "runnable")) {
            String v = element.getAttribute("version");
            int version = "".equals(v) ? Integer.MAX_VALUE : Integer.parseInt(v);
            try {
                Runnable runnable = (Runnable) org.mmbase.util.xml.Instantiator.getInstance(element);
                result.put(version, runnable);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
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
