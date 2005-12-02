package nl.didactor.component.scorm.player;

import java.io.File;

import java.util.Vector;
import java.util.Iterator;

import org.jdom.Element;
import org.jdom.Namespace;

import uk.ac.reload.diva.util.GeneralUtils;
import uk.ac.reload.moonunit.contentpackaging.CP_Core;
import uk.ac.reload.moonunit.contentpackaging.SCORM12_Core;

import uk.ac.reload.jdom.XMLDocument;

import uk.ac.reload.scormplayer.client.generic.contentpackaging.SCORM12_DocumentHandler;

import nl.didactor.utils.debug.LogController;



public class MenuCreator extends XMLDocument
{

   /**
    * A count of organizations (should really only be one for the tree widget)
    */
   private int _orgCount = -1;

   /**
    * Used to keep track of how many items there are in a organization
    * (this is array based, so initially set to -1)
    */
   private int _itemCount = -1;


   private SCORM12_Core scormCore;
   private Element _defaultorg;
   private String sWebAppsPath;
   private String sPackageHref;

   private boolean bDebugMode = LogController.showLogs("az");
   private String  sDebugIndo = "Scorm menu creater: ";


   private String sSubManifest = "";

   public MenuCreator(File fileManifest, String sWebAppsPath, String sPackageHref) throws Exception
   {
      super.loadDocument(fileManifest);
      scormCore = new SCORM12_Core(this);
      this.sWebAppsPath = sWebAppsPath;
      this.sPackageHref = sPackageHref;
   }





   public String[] parse(boolean useRelativePaths, String sPackageName, String sSubPath)
   {
      // New Vector
      if(bDebugMode) System.out.println(sDebugIndo + "--------------------- Start of JS Parser ---------------------");

      Vector v = new Vector();
      writePackageSettings(v, "packageName", 0);


      Element manifestRoot = (Element)getDocument().getRootElement().clone();
      Element orgs = manifestRoot.getChild(SCORM12_Core.ORGANIZATIONS, manifestRoot.getNamespace());
      // get the identifier for the default organization
      _defaultorg = scormCore. getDefaultOrganization(orgs);

      //Selecting the submanifest element
      if(bDebugMode) System.out.println(sDebugIndo + "Selecting the submanifest element");
      try
      {
         Element elemCurrent = _defaultorg;
         String[] arrstrOffsets = sSubPath.split(",");
         for(int f = 0; f < arrstrOffsets.length; f++)
         {
            int iOffsetAtThisLevel = (new Integer(arrstrOffsets[f])).intValue();
            elemCurrent = (Element) elemCurrent.getChildren("item", null).get(iOffsetAtThisLevel);
         }

         createNavLinks(v, elemCurrent, "menu", useRelativePaths);
      }
      catch(Exception e)
      {//Let's start from the root then
         createNavLinks(v, _defaultorg, "menu", useRelativePaths);
      }


      // now call createNavLinks() which should interrogate the org/item structure
      if(bDebugMode) System.out.println(sDebugIndo + "Creating Links");

//      createNavLinks(v, ((Element)((Element) _defaultorg.getChildren().get(1)).getChildren().get(1)), "menu", useRelativePaths);
//      createNavLinks(v, _defaultorg, "menu", useRelativePaths);


      // Convert Vector to String array
      String[] javascriptStrings = new String[v.size()];

      if(bDebugMode) System.out.println(sDebugIndo + "--------------------- End of JS Parser ---------------------");
      v.copyInto(javascriptStrings);
      return javascriptStrings;
  }




  protected void writePackageSettings(Vector javascriptStrings, String name, int value) {
     javascriptStrings.add("CPAPI." + name + " = " + value + ";");
  }



  protected void createNavLinks(Vector javascriptStrings, Element element, String menuParent, boolean useRelativePaths)
  {
    String name = element.getName();
    if(bDebugMode) System.out.println(sDebugIndo + "*** name:" + name);
    if(bDebugMode) System.out.println(sDebugIndo + "*** menu:" + menuParent);
    if(bDebugMode) System.out.println(sDebugIndo + "*** value:" + element.getText());

    // ORGANIZATION
    if(name.equals(CP_Core.ORGANIZATION) && this.isDocumentNamespace(element))
    {
       ++_orgCount;
       _itemCount = -1;
       String orgId = element.getAttributeValue(CP_Core.IDENTIFIER);
       menuParent = "menu";
       String title = "Organization";
       // Display Title if there is one
       Element titleElement = element.getChild(CP_Core.TITLE, element.getNamespace());
       if(titleElement != null)
       {
          if(!titleElement.getText().equals(""))
          {
             title = titleElement.getText();
          }
       }
       // find out if this the default organization...
       String _defaultOrganization = _defaultorg.getAttributeValue(CP_Core.IDENTIFIER);
       if (_defaultOrganization != null)
       {
          if (_defaultOrganization.equals(orgId))
          {
             writePackageSettings(javascriptStrings, "_defaultOrg", _orgCount);
          }
       }
       writeOrganization(javascriptStrings, title, orgId);
    }
    // ITEM
    else if(name.equals(CP_Core.ITEM) && this.isDocumentNamespace(element))
    {
       ++_itemCount;
       String itemId = element.getAttributeValue(CP_Core.IDENTIFIER);
       String hyperLink = "";
       String url = "";
       String scoType = "";
       // Display Title if there is one
       String title = "Item";
       Element titleElement = element.getChild(CP_Core.TITLE, element.getNamespace());
       if(titleElement != null)
       {
          if(!titleElement.getText().equals("")) title = titleElement.getText();
       }
       // check to see that the isvisible attribute is not set to false...
       String isVisibleAttrib = element.getAttributeValue(CP_Core.ISVISIBLE);
       if(isVisibleAttrib != null)
       {
          if(isVisibleAttrib.equals("false"))
          {
              title = "* hidden";
          }
       }
       // What does this Item reference?
       Element ref_element = scormCore.getReferencedElement(element);
       String prerequisites = "";
       if(ref_element != null)
       {
          String ref_name = ref_element.getName();
          // A RESOURCE
          if(ref_name.equals(CP_Core.RESOURCE))
          {
/*AZ
Unknown
              scoType = _navViewer.findScoType(element);
*/
              // Relative path for export - Note the "../" is relative to where the Nav file is!
              if(useRelativePaths)
              {
                  url = scormCore.getRelativeURL(element);
                  // Only if local path add relative bit
                  if(GeneralUtils.isExternalURL(url) == false)
                  {
//AZ                    url = "../" + url;
                     url = sPackageHref + url;
                  }
             }
              // Absolute Paths for Previewing in-situ
             else
             {
                 String turl = this.getLaunch(element);
                 url = turl;
             }
             if(url != null)
             {
                 hyperLink = url;
                 if(!title.equals("* hidden"))
                 {
/*AZ
                       prerequisites = _navViewer.getPrerequisites(element);
                       if (prerequisites == null)
                       {
                           prerequisites = "";
                       }
*/
                 }
             }
          }
          // A sub-MANIFEST
          else if(ref_name.equals(CP_Core.MANIFEST))
          {
              hyperLink = "javascript:void(0)";
             // Get ORGANIZATIONS Element
             Element orgsElement = ref_element.getChild(CP_Core.ORGANIZATIONS, ref_element.getNamespace());
             // Now we have to get the default ORGANIZATION
             if(orgsElement != null) ref_element = scormCore.getDefaultOrganization(orgsElement);
             // Get the children of the referenced <organization> element and graft clones
             if(ref_element != null)
             {
                Iterator it = ref_element.getChildren().iterator();
                while(it.hasNext())
                {
                   Element ref_child = (Element) it.next();
                   element.addContent((Element) ref_child.clone());
                }
             }
          }
       }
       else
       {
           hyperLink = "javascript:void(0)";
       }
       if(bDebugMode) System.out.println(sDebugIndo + "adding to sequencer:"+ itemId + " " + hyperLink+ " " + _itemCount+ " " + scoType+ " " +title+ " " + prerequisites);
//        _sequence.addNewItem(itemId, hyperLink, _itemCount, scoType, title, prerequisites);


       if(_orgCount == -1)
       {// It is "SubManifestMode"
          _orgCount++;
          writeOrganization(javascriptStrings, title, "");
       }
       writeItem(javascriptStrings, title, hyperLink, itemId, menuParent);
       menuParent = itemId;
    }


    // round we go again...
    Iterator it = element.getChildren().iterator();
    while(it.hasNext())
    {
       Element child = (Element) it.next();
       createNavLinks(javascriptStrings, child, menuParent, useRelativePaths);
    }
 }






 protected void writeItem(Vector javascriptStrings, String title, String url, String itemId, String parentMenu) {
    // the javscript tree widget doesn't like hyphens, so replace them with underscores...
    parentMenu = parentMenu.replace('-', '_');
    // add the item...
    javascriptStrings.add("CPAPI.orgArray(" + _orgCount + ").itemArray(" + _itemCount + ").itemTitle = \"" + escapeQuotes(title) + "\";");
    javascriptStrings.add("CPAPI.orgArray(" + _orgCount + ").itemArray(" + _itemCount + ").itemIdentifier = \"" + itemId + "\";");
    javascriptStrings.add("CPAPI.orgArray(" + _orgCount + ").itemArray(" + _itemCount + ").itemParent = \"" + parentMenu + "\";");
    javascriptStrings.add("CPAPI.orgArray(" + _orgCount + ").itemArray(" + _itemCount + ").itemHyper = \"" + url + "\";");
 }





 protected void writeOrganization(Vector javascriptStrings, String title, String orgId) {
    javascriptStrings.add("CPAPI.orgArray(" + _orgCount + ").organizationName = \"" + escapeQuotes(title) + "\";");
    javascriptStrings.add("CPAPI.orgArray(" + _orgCount + ").organizationIdentifier = \"" + orgId + "\";");
 }








   /**
    * Utility method to escape all string delimiters found with a string
    * @param inputString - the string to escape
    * @return - the string with escaped quotes (in javascript format)
    */
   protected String escapeQuotes(String inputString) {
      inputString = inputString.replaceAll("'", "\\\\\\\\'"); // replace a single quote with \\'
      inputString = inputString.replaceAll("\"","\\\\\""); // replace a double quote with \"
      return inputString;
   }



   public String getLaunch(Element element)
   {
       String url = scormCore.getAbsoluteURL(element);
       // an item that references somthing has been found..
       if (url.startsWith("file:///"))
       {
           String tempHref;
           if (GeneralUtils.getOS()== GeneralUtils.MACINTOSH || GeneralUtils.getOS()== GeneralUtils.UNIX){
             tempHref = url.substring(7, url.length());//mac & linux
           }
           else
           {
             tempHref = url.substring(8, url.length()); // windows
           }
           tempHref = tempHref.replaceAll("%20", " ");

//            String testHref = ScormTomcatHandler.getSharedInstance().getScormWebAppPath().toString().replace('\\', '/');
           String testHref = sWebAppsPath;

           testHref = testHref.replaceAll("%20", " ");
           if (tempHref.startsWith(testHref))
           {
               String localUrlMinusPath = tempHref.substring(sWebAppsPath.length(),  tempHref.length());
               String correctLocalUrl = localUrlMinusPath.replace('\\', '/');
               url = "../.." + correctLocalUrl;
           }
       }
       return url;
   }


}
