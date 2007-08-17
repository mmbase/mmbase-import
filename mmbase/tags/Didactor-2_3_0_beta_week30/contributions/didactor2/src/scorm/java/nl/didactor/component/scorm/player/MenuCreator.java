package nl.didactor.component.scorm.player;

import java.io.File;
import java.util.*;

import org.jdom.Element;
import org.jdom.Namespace;


import uk.ac.reload.diva.util.GeneralUtils;
import uk.ac.reload.moonunit.contentpackaging.CP_Core;
import uk.ac.reload.moonunit.contentpackaging.SCORM12_Core;

import uk.ac.reload.jdom.XMLDocument;

import uk.ac.reload.scormplayer.client.generic.contentpackaging.SCORM12_DocumentHandler;

import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc
 * @version $Id: MenuCreator.java,v 1.11 2007-07-26 14:50:57 michiel Exp $
 */

public class MenuCreator extends XMLDocument  {

    private static final Logger log = Logging.getLoggerInstance(MenuCreator.class);
        
    /**
     * A count of organizations (should really only be one for the tree widget)
     */
    private int orgCount = -1;

    /**
     * Used to keep track of how many items there are in a organization
     * (this is array based, so initially set to -1)
     */
    private int itemCount = -1;

    private Element defaultOrg;

    private final SCORM12_Core scormCore;
    private final Node packageNode;

    public static File getManifest(Node packageNode) {
        File directory = org.mmbase.servlet.FileServlet.getFile("scorm", null);
        File file = new File(directory + File.separator + packageNode.getNumber() + "_" + File.separator + CP_Core.MANIFEST_NAME);
        return file;
    }

    public MenuCreator(Node packageNode) throws Exception {
        super.loadDocument(getManifest(packageNode));
        scormCore = new SCORM12_Core(this);
        this.packageNode = packageNode;
    }


    public String[] parse(boolean useRelativePaths, Integer... sSubPath) {

        if(log.isDebugEnabled()) {
            //log.debug("SCORM: MenuCreator.parse(" + sPackageName + ") with offset=" + sSubPath);
            log.debug("--------------------- Start of JS Parser ---------------------");
        }

        List<String> v = new ArrayList<String>();
        writePackageSettings(v, "packageName", 0);


        Element manifestRoot = (Element)getDocument().getRootElement().clone();
        Element orgs = manifestRoot.getChild(SCORM12_Core.ORGANIZATIONS, manifestRoot.getNamespace());
        // get the identifier for the default organization
        defaultOrg = scormCore. getDefaultOrganization(orgs);

        //Selecting the submanifest element
        try {
            Element elemCurrent = defaultOrg;
            for(int f = 0; f < sSubPath.length; f++) {
                int iOffsetAtThisLevel = sSubPath[f];
                elemCurrent = (Element) elemCurrent.getChildren("item", null).get(iOffsetAtThisLevel);
            }

            createNavLinks(v, elemCurrent, "menu", useRelativePaths);
        } catch(Exception e) {
            //Let's start from the root then
            createNavLinks(v, defaultOrg, "menu", useRelativePaths);
        }


        if(log.isDebugEnabled()){
            //log.debug("-----------------SCORM menu for offset=" + sSubPath + ":----------------");
            log.debug("" + v);
            log.debug("--------------------- End of JS Parser ---------------------");
        }
        return v.toArray(new String[] {});
    }




    protected static void writePackageSettings(List<String> javascriptStrings, String name, int value) {
        javascriptStrings.add("CPAPI." + name + " = " + value + ";");
    }



    protected void createNavLinks(List<String> javascriptStrings, Element element, String menuParent, boolean useRelativePaths) {
        String name = element.getName();
        if (log.isDebugEnabled()) {
            log.debug("*** name:" + name);
            log.debug("*** menu:" + menuParent);
            log.debug("*** value:" + element.getText());
        }
        // ORGANIZATION
        if(name.equals(CP_Core.ORGANIZATION) && this.isDocumentNamespace(element)) {
            ++orgCount;
            itemCount = -1;
            String orgId = element.getAttributeValue(CP_Core.IDENTIFIER);
            menuParent = "menu";
            String title = "Organization";
            // Display Title if there is one
            Element titleElement = element.getChild(CP_Core.TITLE, element.getNamespace());
            if(titleElement != null) {
                if(!titleElement.getText().equals("")) {
                    title = titleElement.getText();
                }
            }
            // find out if this the default organization...
            String defaultOrganization = defaultOrg.getAttributeValue(CP_Core.IDENTIFIER);
            if (defaultOrganization != null) {
                if (defaultOrganization.equals(orgId)) {
                    writePackageSettings(javascriptStrings, "_defaultorg", orgCount);
                }
            }
            writeOrganization(javascriptStrings, title, orgId);
        } else if(name.equals(CP_Core.ITEM) && this.isDocumentNamespace(element)) {
            // ITEM
            ++itemCount;
            String itemId = element.getAttributeValue(CP_Core.IDENTIFIER);
            String hyperLink = "";
            String url = "";
            String scoType = "";
            // Display Title if there is one
            String title = "Item";
            Element titleElement = element.getChild(CP_Core.TITLE, element.getNamespace());
            if(titleElement != null) {
                if(!titleElement.getText().equals("")) title = titleElement.getText();
            }
            // check to see that the isvisible attribute is not set to false...
            String isVisibleAttrib = element.getAttributeValue(CP_Core.ISVISIBLE);
            if(isVisibleAttrib != null) {
                if(isVisibleAttrib.equals("false")) {
                    title = "* hidden";
                }
            }
            // What does this Item reference?
            Element ref_element = scormCore.getReferencedElement(element);
            String prerequisites = "";
            if(ref_element != null) {
                String ref_name = ref_element.getName();
                // A RESOURCE
                if(ref_name.equals(CP_Core.RESOURCE)) {
                    /*AZ
                      Unknown
                      scoType = _navViewer.findScoType(element);
                    */
                    // Relative path for export - Note the "../" is relative to where the Nav file is!
                    if(useRelativePaths) {
                        url = scormCore.getRelativeURL(element);
                        // Only if local path add relative bit
                        if(GeneralUtils.isExternalURL(url) == false) {
                            url = "/files/scorm/" + packageNode.getNumber() + "_/" + url;
                            //url = packageHref + url;
                        }
                    } else {
                        // Absolute Paths for Previewing in-situ
                        String turl = this.getLaunch(element);
                        url = turl;
                    }
                    if(url != null) {
                        hyperLink = url;
                        if(!title.equals("* hidden")) {
                            /*AZ
                              prerequisites = _navViewer.getPrerequisites(element);
                              if (prerequisites == null)
                              {
                              prerequisites = "";
                              }
                            */
                        }
                    }
                } else if(ref_name.equals(CP_Core.MANIFEST)) {
                    // A sub-MANIFEST
                    hyperLink = "javascript:void(0)";
                    // Get ORGANIZATIONS Element
                    Element orgsElement = ref_element.getChild(CP_Core.ORGANIZATIONS, ref_element.getNamespace());
                    // Now we have to get the default ORGANIZATION
                    if(orgsElement != null) ref_element = scormCore.getDefaultOrganization(orgsElement);
                    // Get the children of the referenced <organization> element and graft clones
                    if(ref_element != null) {
                        Iterator it = ref_element.getChildren().iterator();
                        while(it.hasNext()) {
                            Element ref_child = (Element) it.next();
                            element.addContent((Element) ref_child.clone());
                        }
                    }
                }
            } else {
                hyperLink = "javascript:void(0)";
            }
            if (log.isDebugEnabled()) {
                log.debug("adding to sequencer:"+ itemId + " " + hyperLink+ " " + itemCount+ " " + scoType+ " " +title+ " " + prerequisites);
                //        _sequence.addNewItem(itemId, hyperLink, itemCount, scoType, title,  prerequisites);
            }


            if(orgCount == -1) {
                // It is "SubManifestMode"
                orgCount++;
                writeOrganization(javascriptStrings, title, "");
            }
            writeItem(javascriptStrings, title, hyperLink, itemId, menuParent);
            menuParent = itemId;
        }


        // round we go again...
        Iterator it = element.getChildren().iterator();
        while(it.hasNext()) {
            Element child = (Element) it.next();
            createNavLinks(javascriptStrings, child, menuParent, useRelativePaths);
        }
    }

    protected void writeItem(List<String> javascriptStrings, String title, String url, String itemId, String parentMenu) {
        // the javscript tree widget doesn't like hyphens, so replace them with underscores...
        parentMenu = parentMenu.replace('-', '_');
        // add the item...
        javascriptStrings.add("CPAPI.orgArray(" + orgCount + ").itemArray(" + itemCount + ").itemTitle = \"" + escapeQuotes(title) + "\";");
        javascriptStrings.add("CPAPI.orgArray(" + orgCount + ").itemArray(" + itemCount + ").itemIdentifier = \"" + itemId + "\";");
        javascriptStrings.add("CPAPI.orgArray(" + orgCount + ").itemArray(" + itemCount + ").itemParent = \"" + parentMenu + "\";");
        javascriptStrings.add("CPAPI.orgArray(" + orgCount + ").itemArray(" + itemCount + ").itemHyper = \"" + url + "\";");
    }


    protected void writeOrganization(List<String> javascriptStrings, String title, String orgId) {
        javascriptStrings.add("CPAPI.orgArray(" + orgCount + ").organizationName = \"" + escapeQuotes(title) + "\";");
        javascriptStrings.add("CPAPI.orgArray(" + orgCount + ").organizationIdentifier = \"" + orgId + "\";");
    }

    /**
     * Utility method to escape all string delimiters found with a string
     * @param inputString - the string to escape
     * @return - the string with escaped quotes (in javascript format)
     */
    protected static String escapeQuotes(String inputString) {
        inputString = inputString.replaceAll("'", "\\\\\\\\'"); // replace a single quote with \\'
        inputString = inputString.replaceAll("\"","\\\\\""); // replace a double quote with \"
        return inputString;
    }



    protected String getLaunch(Element element) {
        String url = scormCore.getAbsoluteURL(element);
        // an item that references somthing has been found..
        if (url.startsWith("file:///")) {
            String tempHref;
            if (GeneralUtils.getOS() == GeneralUtils.MACINTOSH || GeneralUtils.getOS() == GeneralUtils.UNIX){
                tempHref = url.substring(7, url.length());//mac & linux
            } else {
                tempHref = url.substring(8, url.length()); // windows
            }
            tempHref = tempHref.replaceAll("%20", " ");

            String testHref = "";//webAppsPath;

            testHref = testHref.replaceAll("%20", " ");
            if (tempHref.startsWith(testHref)) {
                String localUrlMinusPath = tempHref;//.substring(webAppsPath.length(),  tempHref.length());
                String correctLocalUrl = localUrlMinusPath.replace('\\', '/');
                url = "../.." + correctLocalUrl;
            }
        }
        return url;
    }


}
