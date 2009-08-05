package nl.leocms.util.tools;

import java.io.*;

import jxl.*;

import org.mmbase.bridge.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Convert a Excel file to LeoCMS navigation structure
 *
 * @author Alexey Zemskov
 * @version $Revision: 1.5 $
 */
public class Excel2Menu {

   private static final Logger log = Logging.getLoggerInstance(Excel2Menu.class);

   private Cloud cloud;
   private int iMaxLevel = 0;
   private String sSitePath;

   private NodeManager nmRubric;
   private NodeManager nmPage;
   private NodeManager nmPageTemplate;
   private RelationManager rmParent;
   private RelationManager rmPosrel;
   private RelationManager rmUseIt;


   /**
    * Constructor
    * @param cloud Cloud
    * @param sMaxLevel String
    * @param sSiteTitle String
    * @param sSitePath String
    * @throws Exception
    */
   public Excel2Menu(Cloud cloud, String sMaxLevel, String sSitePath) throws Exception{
      log.debug("Maxlevel=" + sMaxLevel);
      log.debug("SitePath=" + sSitePath);
      this.cloud = cloud;
      this.iMaxLevel = (new Integer(sMaxLevel)).intValue();
      this.sSitePath = sSitePath;

      nmRubric = cloud.getNodeManager("rubriek");
      nmPage = cloud.getNodeManager("pagina");
      nmPageTemplate = cloud.getNodeManager("paginatemplate");
      rmParent = cloud.getRelationManager("parent");
      rmPosrel = cloud.getRelationManager("posrel");
      rmUseIt = cloud.getRelationManager("gebruikt");
   }


   public void convert(InputStream inputStream) throws Exception{
      // creates a rubrieken tree from the Excel file and relates it to the rubriek with alias 'root'
      // only the first worksheet is used and the title of the worksheet is the naam of the new rubrieken tree
      // the leaves of the rubrieken tree are pagina's
      // the templates specified in the Excel are related to the paginas

      Workbook workbook = Workbook.getWorkbook(inputStream);

      Sheet sheet = workbook.getSheet(0);
      String sSheetName = sheet.getName();
      int iRows = sheet.getRows();
      int iColumns = sheet.getColumns();

      if (iRows < 3)
      {
         log.debug("Worksheet " + sSheetName + " has less than three rows, so it can't be proceed.");
      }
      else if (iColumns < 1)
      {
         log.debug("Worksheet " + sSheetName + " has less than 1 column, is it empty?");
      }
      else
      {
         log.debug("Worksheet " + sSheetName + " *********************************************************");

         Node nodeRootRubric = cloud.getNode("root");


         Node nodeNewRubric =  addRubric(sSheetName,1);
         nodeNewRubric.setStringValue("url_live", sSitePath);
         nodeNewRubric.commit();
         nodeRootRubric.createRelation(nodeNewRubric, rmParent).commit();

         int iCurrentLevel = 1;

         Node[] nodeTempRootTree = new Node[iMaxLevel + 1];

         int [] posAtLevel = new int[iMaxLevel];
         for(int i = 0; i<iMaxLevel; i++) { posAtLevel[i] = 0; }
         
         Node nodeTempRoot = nodeNewRubric;
         nodeTempRootTree[0] = nodeTempRoot;
         for(int f = 0; f < iRows; f++){
            String sTemplate = sheet.getCell(0, f).getContents().replace('\n', ' ');
            log.debug("template in row=" + f + " is \"" + sTemplate + "\"");


            log.debug("Current level=" +  iCurrentLevel + ", cell contents=" + sheet.getCell(iCurrentLevel, f).getContents());
            while((sheet.getCell(iCurrentLevel, f).getContents() == null) || ("".equals(sheet.getCell(iCurrentLevel, f).getContents()))){
               iCurrentLevel--;
                if(iCurrentLevel == 0){
                  throw new Exception ("Row with number=" + f + " has got invalid content. Is it empty?");
               }
               log.debug("Rolling back to level=" + iCurrentLevel + ", our new TempRoot=" + nodeTempRootTree[iCurrentLevel - 1].getNumber());
               log.debug("Current level=" +  iCurrentLevel + ", cell contents=" + sheet.getCell(iCurrentLevel, f).getContents());
              
               nodeTempRoot = nodeTempRootTree[iCurrentLevel - 1];
            }

            if(iCurrentLevel > iMaxLevel){
               //The have to obey MaxLevel limit
               log.debug("-----------We have reached the limit of levels!-----------");
               continue;
            }

            String sCurrentLevelContent;

            if((sTemplate == null) || ("".equals(sTemplate))){
               //rubric
               sCurrentLevelContent = sheet.getCell(iCurrentLevel, f).getContents().replace('\n', ' ');
               log.debug("Row=" + f + " is a rubric with name=" + sCurrentLevelContent);
               nodeNewRubric = this.addRubric(sCurrentLevelContent, iCurrentLevel+1);
               Relation newRelation = nodeTempRoot.createRelation(nodeNewRubric, rmParent);
               newRelation.setIntValue("pos",posAtLevel[iCurrentLevel]);
               newRelation.commit();
               posAtLevel[iCurrentLevel]++;
               nodeTempRoot = nodeNewRubric;
               nodeTempRootTree[iCurrentLevel] = nodeTempRoot;
               iCurrentLevel++;

            } else {

               //page
               sCurrentLevelContent = sheet.getCell(iCurrentLevel, f).getContents().replace('\n', ' ');
               log.debug("Row=" + f + " is a page with name=" + sCurrentLevelContent);
               Node nodeNewPage = this.addPage(sCurrentLevelContent);
               Relation newRelation = nodeTempRoot.createRelation(nodeNewPage, rmPosrel);
               newRelation.setIntValue("pos",posAtLevel[iCurrentLevel]);
               newRelation.commit();
               posAtLevel[iCurrentLevel]++;

               Node nodePageTemplate = getPageTemplate(sTemplate);
               nodeNewPage.createRelation(nodePageTemplate, rmUseIt).commit();
            }
         }
      }
   }

   /**
    * Adds a new rubric
    *
    * @param sName String
    * @return Node
    */
   private Node addRubric(String sName, int iLevel){
      Node nodeNewRubric = nmRubric.createNode();
      nodeNewRubric.setStringValue("naam", sName);
      nodeNewRubric.setIntValue("level", iLevel);
      nodeNewRubric.commit();
      return nodeNewRubric;
   }

   /**
    * Adds a new page
    *
    * @param sTitle String
    * @return Node
    */
   private Node addPage(String sTitle){
      Node nodeNewPage = nmPage.createNode();
      nodeNewPage.setStringValue("titel", sTitle);
      nodeNewPage.setLongValue("verloopdatum", 2145913199);
      nodeNewPage.setIntValue("verwijderbaar", 1);
      nodeNewPage.commit();
      return nodeNewPage;
   }



   /**
    * Looks for the template with name=@param sPagetemplateName
    * If it presents in db it returns the template otherwise it adds a new one to db
    *
    * @param sPagetemplateName String
    * @return Node
    */
   private Node getPageTemplate(String sPagetemplateUrl){
      NodeList nl = cloud.getList(null,
                                  "paginatemplate",
                                  "paginatemplate.number",
                                  "paginatemplate.url='" + sPagetemplateUrl + "'",
                                  null, null, null, true);

      if (nl.size() > 0) {

         return cloud.getNode(nl.getNode(0).getStringValue("paginatemplate.number"));

      } else {

         return addNewPageTemplate(sPagetemplateUrl);
      }
   }



   /**
    * Adds a new PageTemplate
    * @param sPagetemplateName String
    * @return Node
    */
   private Node addNewPageTemplate(String sPagetemplateUrl){
      Node nodeNewPageTemplate = nmPageTemplate.createNode();
      nodeNewPageTemplate.setStringValue("naam", sPagetemplateUrl);
      nodeNewPageTemplate.setStringValue("url", sPagetemplateUrl);
      nodeNewPageTemplate.setIntValue("systemtemplate",0);
      nodeNewPageTemplate.setIntValue("dynamiclinklijsten",0);
      nodeNewPageTemplate.setIntValue("dynamicmenu",0);
      nodeNewPageTemplate.setIntValue("contenttemplate",0); 
      nodeNewPageTemplate.commit();
      return nodeNewPageTemplate;
   }


}
