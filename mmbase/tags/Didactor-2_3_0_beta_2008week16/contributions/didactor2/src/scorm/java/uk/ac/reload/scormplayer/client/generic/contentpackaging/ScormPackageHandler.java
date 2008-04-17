/**
 *  RELOAD TOOLS
 *
 *  Copyright (c) 2003 Oleg Liber, Bill Olivier, Phillip Beauvoir, Paul Sharples
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *  Project Management Contact:
 *
 *  Oleg Liber
 *  Bolton Institute of Higher Education
 *  Deane Road
 *  Bolton BL3 5AB
 *  UK
 *
 *  e-mail:   o.liber@bolton.ac.uk
 *
 *
 *  Technical Contact:
 *
 *  Phillip Beauvoir
 *  e-mail:   p.beauvoir@bolton.ac.uk
 *
 *  Paul Sharples
 *  e-mail:   p.sharples@bolton.ac.uk
 *
 *  Web:      http://www.reload.ac.uk

 * @version $Id: ScormPackageHandler.java,v 1.6 2007-03-14 13:56:46 michiel Exp $
 *
 */



package uk.ac.reload.scormplayer.client.generic.contentpackaging;

import java.io.*;
import java.net.*;
import java.util.Iterator;


import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import uk.ac.reload.diva.util.GeneralUtils;
import uk.ac.reload.jdom.XMLDocument;
import uk.ac.reload.moonunit.contentpackaging.CP_Core;
import uk.ac.reload.moonunit.contentpackaging.SCORM12_Core;


import org.mmbase.bridge.*;


import nl.didactor.utils.zip.Unpack;
import nl.didactor.utils.files.CommonUtils;
import nl.didactor.utils.http.FileDownloader;
import nl.didactor.utils.http.exceptions.*;
import nl.didactor.utils.debug.LogController;
import nl.didactor.component.scorm.exceptions.*;
import nl.didactor.component.scorm.metastandart.schema.Importer;
import nl.didactor.component.scorm.metastandart.schema.Filter;
import nl.didactor.component.scorm.metastandart.MetaDataImporter;


/**
 * The ScormPackageHandler Class.  A class used to parse a scorm imsmanifest.xml
 * file and build a xml file for each sco encountered.
 * Each item is examined and a decision is made about whether or not to generate
 * a sco cmi data model.
 *
 * @author Paul Sharples
 * @version $Id: ScormPackageHandler.java,v 1.6 2007-03-14 13:56:46 michiel Exp $
 */
public class ScormPackageHandler extends XMLDocument {

    /**
     * A var to flag if any item were found in the manifest.  If there are none
     * found then this package cannot be played and/or is a resource package.
     */
    public boolean _hasItemsToPlay = false;

    /**
     *  Default org id
     */
    protected String _currentOrgId;

    /**
     * Our instance of core scorm methods
     */
    protected SCORM12_Core _scormCore;

    /**
     * Name of the project
     */
    protected String _projectName;

    /**
     * Default Constructor
     */

   private Cloud cloud;
   NodeManager nmMetaStandart;
   NodeManager nmMetaDefinition;
   NodeManager nmPosrel;

   private Node nodePackage;
   private Node nodeEducation;
   private int iCurrentLevel = 0;
   private int iCounter = 0;

   private Filter filter;


   private boolean bDebugMode = LogController.showLogs("az");
   private String  sDebugIndo = "Scorm Package Handler: ";


   public ScormPackageHandler(File manifest, String sNodePackageID) throws JDOMException, IOException
   {
      // Load the Document
      loadDocument(manifest);
      _projectName = manifest.getParentFile().getName();
      _scormCore = new SCORM12_Core(this);
      cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
      nmMetaStandart   = cloud.getNodeManager("metastandard");
      nmMetaDefinition = cloud.getNodeManager("metadefinition");
      nmPosrel         = cloud.getNodeManager("posrel");

      filter = new Filter(cloud);

      nodePackage = cloud.getNode(sNodePackageID);
   }


    /**
     * A class to actually do the bulk of the work. It creates an xml file
     * representing the organizations, similar to the imsmanifest, but also
     * modelling the sco/asset attributes needed by the runtime system - ie
     * order of sequence, launch URL...
     */
    public void buildSettings() throws Exception
    {
       // get the root element of the manifest
       // NOTE: CLONE IT first- must work on a copy of the original JDOM doc.
       Element manifestRoot = (Element)getDocument().getRootElement().clone();
       // now get the organizations node
       Element orgs = manifestRoot.getChild(SCORM12_Core.ORGANIZATIONS, manifestRoot.getNamespace());
       // get the identifier for the default organization
       Element defaultOrgNode = getDefaultOrganization(orgs);

      if (defaultOrgNode != null)
      {
         //Creating of education node
         nodeEducation = cloud.getNodeManager("educations").createNode();
         nodeEducation.setValue("name", defaultOrgNode.getChild("title", SCORM12_DocumentHandler.IMSCP_NAMESPACE_112).getText());
         nodeEducation.commit();

         //Relation to package node
         nodePackage.createRelation(nodeEducation, cloud.getRelationManager("related")).commit();


         // and store the default identifier
         String defaultOrgIdentifier = defaultOrgNode.getAttributeValue(SCORM12_Core.IDENTIFIER);
         // set the default organization
         iterateThruManifest(manifestRoot, nodeEducation);
      }

       // *** FINISHED ***
       //System.out.println("Finished");
       //throw an exception if no items were found in the manifest
       if (!_hasItemsToPlay)
       {
            throw new NoItemFoundException(NoItemFoundException.NO_ITEM_FOUND_MSG);
       }
    }


    /**
     * A method to read through the imsmanifest and build our JDOM model in
     * memory representing our navigation file.
     * @param element
     * @param settings
     * @param indentCount
     */
    public void iterateThruManifest(Element element, Node nodeParent) throws Exception
    {
        String name = element.getName();
        if(name.equals(CP_Core.ORGANIZATION) && isDocumentNamespace(element))
        {
         _currentOrgId = element.getAttributeValue(CP_Core.IDENTIFIER);
        }
        if (name.equals(SCORM12_Core.ITEM) && isDocumentNamespace(element))
        {
           String id = element.getAttributeValue(SCORM12_Core.IDENTIFIER);
           // next see if this item references a resource
           // If it does, then look for the href
           // and also scotype...
           String thestat = "";
           String url = "";
           String scoType = "";
           Element ref_element = getReferencedElement(element);
           if (ref_element != null)
           {
              String ref_name = ref_element.getName();
              // A RESOURCE
              if (ref_name.equals(SCORM12_Core.RESOURCE))
              {
                 // get the sco type
                 String theScoType = ref_element.getAttributeValue("scormtype", SCORM12_DocumentHandler.ADLCP_NAMESPACE_12);
                 if (theScoType != null)
                 {
                    scoType = theScoType;
                 }
                 boolean isVisible = true;
                 // check that the item is not hidden
                 String isVisibleAttrib = element.getAttributeValue(CP_Core.ISVISIBLE);
                 if (isVisibleAttrib != null)
                 {
                    if (isVisibleAttrib.equals("false"))
                    {
                       isVisible = false;
                    }
                 }

                 url = getAbsoluteURL(element);
                 // an item that references somthing has been found..
//                 System.out.println("url=" + url);

                 _hasItemsToPlay = true;
                 if (url.startsWith("file:///"))
                 {
                    String tempHref;
                    if (GeneralUtils.getOS() == GeneralUtils.MACINTOSH ||
                        GeneralUtils.getOS() == GeneralUtils.UNIX)
                    {
                       tempHref = url.substring(7, url.length()); //mac & linux
                    }
                    else
                    {
                       tempHref = url.substring(8, url.length()); // windows
                    }
                    tempHref = tempHref.replaceAll("%20", " ");


                    if(bDebugMode) System.out.println(sDebugIndo + "resource href=" + tempHref);


                    //New htmlpage node
                    Node nodeHtmlPage = cloud.getNodeManager("htmlpages").createNode();
                    nodeHtmlPage.setValue("path", tempHref);
                    nodeHtmlPage.setValue("name", nodeParent.getValue("name"));
                    nodeHtmlPage.commit();


                    Element elemMetadata = null;
                    Element elemMetadataLocation = null;
                    elemMetadata = ref_element.getChild("metadata", SCORM12_DocumentHandler.IMSCP_NAMESPACE_112);
                    if(elemMetadata != null)
                    {
                       elemMetadataLocation = elemMetadata.getChild("location", SCORM12_DocumentHandler.ADLCP_NAMESPACE_12);
                    }



                    if(bDebugMode) System.out.println(sDebugIndo +  "metadatalocation = " + elemMetadataLocation);



                    if(elemMetadataLocation != null)
                    {

                       //Check if the metastandart with such name already exists
                       XMLDocument xmlDocument = new XMLDocument();
                       File fileMetaDataLocation = new File(CommonUtils.fixPath(this.getFile().getParentFile() + File.separator + elemMetadataLocation.getText()));
                       xmlDocument.loadDocument(fileMetaDataLocation);
                       Element elemDataRoot = xmlDocument.getRootElement();

                       NodeList nlMetaStandartsWithSuchName = nmMetaStandart.getList("name='" + elemDataRoot.getName() + "'", null, null);
  //                    System.out.println( ((Node) nlMetaStandartsWithSuchName.get(0)).getNumber());
                       if (nlMetaStandartsWithSuchName.size() == 0)
                       {
                          try
                          {
                             //Getting schema from server
                             String sSchemaContent = FileDownloader.getTextFile("http://www.imsglobal.org/xsd/imsmd_rootv1p2p1.xsd", 100);

                             //Writing it to the temp file
                             //Unfortunately XMLDocument doesn't understand String as input
                             File fileScema = File.createTempFile("schema", null);
                             RandomAccessFile rafileSchema = new RandomAccessFile(fileScema, "rw");
                             rafileSchema.writeBytes(sSchemaContent);
                             rafileSchema.close();

                             //MetaStnadart parcer
                             Importer importer = new Importer(cloud, fileScema);
                             Node nodeRootMetastandart = importer.importScheme(elemDataRoot.getName());
                             filter.process(nodeRootMetastandart);

                             fileScema.delete();
                          }
                          catch (Exception e)
                          {
                             throw new ImportMetaStandartsException(e);
                          }
                       }

                       try
                       {
                          //Get metadata and connect it to the page
                          MetaDataImporter metaDataImporter = new MetaDataImporter(cloud, (Node) nlMetaStandartsWithSuchName.get(0));
                          metaDataImporter.process(fileMetaDataLocation, nodeHtmlPage);
                       }
                       catch (Exception e)
                       {
                          throw new ImportMetaDataException(e);
                       }
                    }



                    //read html from disk file
                    RandomAccessFile fileHtmlPage = new RandomAccessFile(CommonUtils.fixPath(tempHref), "r");
                    byte[] arrbytesHtmlPage = new byte[ (new Long(fileHtmlPage.length())).intValue() ];
                    fileHtmlPage.readFully(arrbytesHtmlPage);
                    fileHtmlPage.close();


                    String sFileContent = new String(arrbytesHtmlPage);


/*                 *** JS Script cleaner ***

                    String sMarker = "";
                    sFileContent = sFileContent.replaceAll("<[sS][cC][rR][iI][pP][tT][\\d\\D]*[sS][cC][rR][iI][pP][tT]\\s*>", sMarker);
                    sFileContent = sFileContent.replaceAll("[oO][nN][cC][lL][iI][cC][kK]=\"[^\"]*\\)\"", sMarker);
                    sFileContent = sFileContent.replaceAll("[oO][nN][lL][oO][aA][dD]=\"[^\"]*\\)\"", sMarker);
                    sFileContent = sFileContent.replaceAll("[oO][nN][uU][nN][lL][oO][aA][dD]=\"[^\"]*\\)\"", sMarker);
*/

                    nodeHtmlPage.setValue("content", sFileContent);
                    nodeHtmlPage.commit();

                    //Relation to learnblock
                    nodeParent.createRelation(nodeHtmlPage, cloud.getRelationManager("posrel")).commit();


                    /*
                                            String testHref = ScormTomcatHandler.getSharedInstance().getScormWebAppPath().toString().replace('\\', '/');
                                            testHref = testHref.replaceAll("%20", " ");
                                            if (tempHref.startsWith(testHref)) {
                                                String localUrlMinusPath = tempHref.substring(
                                                        ScormTomcatHandler.getSharedInstance().getScormWebAppPath().toString().length()+1,
                                                    tempHref.length()
                                                    );
                                                String correctLocalUrl = localUrlMinusPath.replace(
                                                    '\\', '/');
                                                url = correctLocalUrl;
                                            }
                     */
                 }
              }

              // A sub-MANIFEST
              else
              {
                 if (ref_name.equals(CP_Core.MANIFEST))
                 {
                    // Get ORGANIZATIONS Element
                    Element orgsElement = ref_element.getChild(SCORM12_Core.ORGANIZATIONS, ref_element.getNamespace());
                    // Now we have to get the default ORGANIZATION
                    if (orgsElement != null)
                       ref_element = getDefaultOrganization(orgsElement);

                       // Get the children of the referenced <organization> element and graft clones
                    if (ref_element != null)
                    {
                       Iterator it = ref_element.getChildren().iterator();
                       while (it.hasNext())
                       {
                          Element ref_child = (Element) it.next();
                          element.addContent( (Element) ref_child.clone());
                       }
                    }
                 }
              }

              // next we need to find any MAXTIMEALLOWED entries
              String maxTimeText = "";
              Element maxTime = element.getChild(SCORM12_Core.MAXTIMEALLOWED, SCORM12_DocumentHandler.ADLCP_NAMESPACE_12);
              if (maxTime != null)
              {
                 maxTimeText = maxTime.getText();
              }

              // next find any TIMELIMITACTION entries
              String timeLimitText = "";
              Element timeLimit = element.getChild(SCORM12_Core.TIMELIMITACTION, SCORM12_DocumentHandler.ADLCP_NAMESPACE_12);
              if (timeLimit != null)
              {
                 timeLimitText = timeLimit.getText();
              }

              // next find any DATAFROMLMS entries
              String datafromLmsText = "";
              Element dataFromLms = element.getChild(SCORM12_Core.DATAFROMLMS, SCORM12_DocumentHandler.ADLCP_NAMESPACE_12);
              if (dataFromLms != null)
              {
                 datafromLmsText = dataFromLms.getText();
              }

              // next find any MASTERYSCORE entries
              String masteryScoreText = "";
              Element masteryScore = element.getChild(SCORM12_Core.MASTERYSCORE, SCORM12_DocumentHandler.ADLCP_NAMESPACE_12);
              if (masteryScore != null)
              {
                 masteryScoreText = masteryScore.getText();
              }

              // if the scoType is "sco", then we have to generate our CMI model
              // for it...
              if (scoType.equals(SCORM12_Core.SCO))
              {
              }
           }
        }
        Iterator it = element.getChildren().iterator();

        int iPosrelCounter = 0;
        while (it.hasNext())
        {
            Element child = (Element) it.next();

            String sChildName = child.getName();

            if (sChildName.equals(SCORM12_Core.ITEM) && isDocumentNamespace(element))
            {
               //System.out.println("---Begin of level---");
               iCurrentLevel++;

               //New learnblock node
               Node nodeLearnblock = cloud.getNodeManager("learnblocks").createNode();
               nodeLearnblock.setValue("name", "" + child.getChild("title", SCORM12_DocumentHandler.IMSCP_NAMESPACE_112).getText());
               nodeLearnblock.setValue("path", "" + nodePackage.getNumber() + "-" + iPosrelCounter);
//Deep level
//               nodeLearnblock.setValue("intro", "deep level=" + iCurrentLevel);
               nodeLearnblock.commit();

               //Relation to parent node (learnblok or education)
               Relation relPosrel = nodeParent.createRelation(nodeLearnblock, cloud.getRelationManager("posrel"));
               relPosrel.setValue("pos", "" + iPosrelCounter);
               relPosrel.commit();

               iPosrelCounter++;

               //System.out.println(sChildName);
               //System.out.println("now level is " + iCurrentLevel);

               iterateThruManifest(child, nodeLearnblock);


               if ((nodeParent.getNodeManager().getName().equals("learnblocks")) && (nodeLearnblock.countRelatedNodes(cloud.getNodeManager("learnblocks"), "posrel", "destination") == 0) && (nodeLearnblock.countRelatedNodes(cloud.getNodeManager("htmlpages"), "posrel", "destination") == 1))
               {//There are no children learnblock's
                //So removing our temporal learnblock

                  NodeList nlHtmlPages = nodeLearnblock.getRelatedNodes (cloud.getNodeManager("htmlpages"), "posrel", "destination");
                  Node nodeHtmlPage = (Node) nlHtmlPages.get(0);

                  nodeHtmlPage.setValue("name", nodeLearnblock.getValue("name"));
                  nodeHtmlPage.commit();
                  Relation relation = nodeParent.createRelation(nodeHtmlPage, cloud.getRelationManager("posrel"));
                  relation.setValue("pos", this.getPosrelNode(nodeParent.getNumber(), nodeLearnblock.getNumber()).getValue("pos"));
                  relation.commit();


                  nodeLearnblock.delete(true);
               }

            }
            else
            {
               iterateThruManifest(child, nodeParent);
            }


            if (sChildName.equals(SCORM12_Core.ITEM) && isDocumentNamespace(element))
            {
               //System.out.println(sChildName);
               //System.out.println("---End of level---");
               iCurrentLevel--;
               //System.out.println("now level is " + iCurrentLevel);
            }
        }
    }


    /**
     * getDefaultOrganization - wraps the same method
     * found in SCORM1_2Core.
     * @param orgs
     * @return - the JDOM element representing the default organization
     */
    public Element getDefaultOrganization(Element orgs) {
        return _scormCore.getDefaultOrganization(orgs);
    }

    /**
     * getReferencedElement - wraps the same method
     * found in SCORM1_2Core.
     * @param sourceElement
     * @return - the JDOM element representing the resource
     */
    public Element getReferencedElement(Element sourceElement) {
        return _scormCore.getReferencedElement(sourceElement);
    }

    /**
     * @return The Absolute URL string that an Element references
     */
    public String getAbsoluteURL(Element element) {
        return _scormCore.getAbsoluteURL(element);
    }

    /**
     * @return True if this is a Manifest that we can handle
     */
    public boolean isSCORM12Manifest() throws DocumentHandlerException {
      // Has to be a CP Package with a SCORM 1.2 Namespace in there
        return SCORM12_DocumentHandler.canHandle(getDocument());
    }




   private Node getPosrelNode(String sSource, String sDestination)
   {
      NodeList nlPosrel = nmPosrel.getList("snumber='" + sSource + "' AND dnumber='" + sDestination + "'", null, null);
      return (Node) nlPosrel.get(0);
   }
   private Node getPosrelNode(int sSource, int sDestination)
   {
      return this.getPosrelNode("" + sSource, "" + sDestination);
   }



   private boolean isMetaStandartAlreadyExists(File fileXML)
   {
      return true;
   }


}
