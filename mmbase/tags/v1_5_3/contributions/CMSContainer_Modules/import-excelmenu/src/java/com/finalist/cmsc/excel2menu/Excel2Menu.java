package com.finalist.cmsc.excel2menu;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.mmbase.bridge.*;

import com.finalist.cmsc.excel2menu.ExcelConfig.*;
import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.repository.RepositoryUtil;

/**
 * Convert a excel file to CMSC specific content
 * 
 * @author Wouter Heijke
 */
public class Excel2Menu {

   private static Log log = LogFactory.getLog(Excel2Menu.class);

   private Cloud cloud;
   private ExcelConfig config;


   public Excel2Menu(Cloud cloud, ExcelConfig config) {
      this.cloud = cloud;
      this.config = config;
   }


   public void convert(String name) {
      try {
         log.info("Convert xls file:" + name);
         InputStream inputStream = new FileInputStream(name);
         convert(inputStream);
      }
      catch (IOException e) {
         log.error(e.getMessage(), e);
      }
   }


   private void checkConfig() {
      for (View view : config.getViews()) {
         checkView(view);
      }
      for (Portlet portlet : config.getPortlets()) {
         checkPortlet(portlet);
      }
      for (Layout layout : config.getLayouts()) {
         checklayout(layout);
      }
      checkSite(config.getSite());
   }


   private void checkView(View view) {
      Node viewNode = PortletUtil.findViewWithResource(cloud, view.resource);
      if (viewNode == null) {
         viewNode = PortletUtil.createView(cloud, view.title, view.resource);
      }
      view.nodeNumber = viewNode.getNumber();
   }


   private void checkPortlet(ExcelConfig.Portlet portlet) {
      Node defNode = PortletUtil.findDefinitionWithTitle(cloud, portlet.title);
      if (defNode == null) {
         defNode = PortletUtil.createDefinition(cloud, portlet.title, portlet.definition, PortletUtil.SINGLE);
      }
      Node portletNode = PortletUtil.getPortletForDefinition(defNode);
      if (portletNode == null) {
         View view = config.getView(portlet.view);
         if (view == null) {
            throw new IllegalStateException("View " + portlet.view + " is not found");
         }
         Node viewNode = cloud.getNode(view.nodeNumber);
         PortletUtil.createPortlet(cloud, portlet.title, defNode, viewNode);
      }
      portlet.nodeNumber = defNode.getNumber();
   }


   private void checklayout(Layout layout) {
      Node layoutNode = PagesUtil.findLayoutWithResource(cloud, layout.resource);
      if (layoutNode == null) {
         layoutNode = PagesUtil.createLayout(cloud, layout.title, layout.resource);
         for (Map.Entry<String, String> position : layout.positions.entrySet()) {
            Portlet portlet = config.getPortlet(position.getValue());
            if (portlet == null) {
               throw new IllegalStateException("Portlet " + position.getValue() + " is not found");
            }
            Node definitionNode = cloud.getNode(portlet.nodeNumber);
            PagesUtil.addAllowedNamedRelation(layoutNode, definitionNode, position.getKey());
         }
      }
      layout.nodeNumber = layoutNode.getNumber();
   }


   private void checkSite(Site site) {
      Node siteNode = SiteUtil.getSite(cloud, site.path);
      if (siteNode == null) {
         Layout layout = config.findLayout(0);
         Node layoutNode = cloud.getNode(layout.nodeNumber);
         siteNode = SiteUtil.createSite(cloud, site.title, site.path, layoutNode);
      }
      site.nodeNumber = siteNode.getNumber();
   }


   public void convert(InputStream inputStream) {
      Node siteNode = SiteUtil.getSite(cloud, config.getSite().path);
      if (siteNode == null) {
         checkConfig();
         siteNode = cloud.getNode(config.getSite().nodeNumber);

         try {
            Map<String, Node> parents = new HashMap<String, Node>();

            POIFSFileSystem file = new POIFSFileSystem(inputStream);

            HSSFWorkbook workbook = new HSSFWorkbook(file);
            int numSheets = workbook.getNumberOfSheets();

            for (int i = 0; i < numSheets; i++) {
               HSSFSheet sheet = workbook.getSheetAt(i);
               int last = sheet.getLastRowNum();

               for (int rowNum = sheet.getFirstRowNum(); rowNum < last; rowNum++) {
                  HSSFRow menuRow = sheet.getRow(rowNum);
                  if (menuRow != null) {
                     short firstCellNum = menuRow.getFirstCellNum();
                     short lastCellNum = menuRow.getLastCellNum();
                     int maxCell = Math.min(lastCellNum, config.getMaxLevel());

                     for (short cellNum = firstCellNum; cellNum < maxCell; cellNum++) {
                        if (cellNum == 0) {
                           HSSFCell menuCell = findMenu(menuRow, cellNum);
                           ;
                           if (menuCell != null) {
                              String cellValue = getCellValue(menuCell);
                              Node menu = createMenu(cellValue, 1, siteNode);
                              registerMenu(parents, menuCell.getCellNum(), menu);
                           }
                        }
                        else {
                           HSSFCell cell = findMenu(menuRow, cellNum);
                           if (cell != null) {
                              String cellValue = getCellValue(cell);
                              Node parentMenu = requestMenu(parents, cellNum - 1);

                              Node submenu = createMenu(cellValue, cellNum + 1, parentMenu);
                              registerMenu(parents, cellNum, submenu);
                           }
                        }
                     }
                  }
               }
            }
         }
         catch (IOException e) {
            log.error(e.getMessage(), e);
         }
      }

      if (config.isCreateChannels()) {
         Node rootNode = RepositoryUtil.getRootNode(cloud);
         if (!RepositoryUtil.hasChild(rootNode, config.getSite().path)) {
            Node contentChannel = createContentChannelForSite(cloud, siteNode);
            createContentChannelChildren(cloud, siteNode, contentChannel);
         }
      }
   }


   /**
    * Request a parent for a certain menu level
    * 
    * @param parents
    * @param id
    *           Menu level
    * @return Node for a menu level
    */
   private Node requestMenu(Map<String, Node> parents, int id) {
      return parents.get(Integer.toString(id));
   }


   /**
    * Keep track of parent menu's for all menu levels
    * 
    * @param parents
    * @param id
    *           Menu level
    * @param menu
    *           Node of menu to register
    */
   private void registerMenu(Map<String, Node> parents, int id, Node menu) {
      parents.put(Integer.toString(id), menu);
   }


   /**
    * Find a submenu in this row in a specific cell
    * 
    * @param row
    *           Row to look into
    * @param s
    *           Cell # to look into
    * @return Cell
    */
   private HSSFCell findMenu(HSSFRow row, short s) {
      HSSFCell result = null;
      HSSFCell menuCell = row.getCell(s);
      if (menuCell != null) {
         String cellValue = getCellValue(menuCell);
         if (cellValue != null) {
            result = menuCell;
         }
      }
      return result;
   }


   private Node createMenu(String pageTitle, int level, Node parentMenu) {
      String fragment = TreeUtil.convertToFragment(pageTitle);

      if (!NavigationUtil.hasChild(parentMenu, fragment)) {
         Layout layout = config.findLayout(level);
         Node layoutNode = cloud.getNode(layout.nodeNumber);
         Node newPage = PagesUtil.createPage(cloud, pageTitle, layoutNode);
         NavigationUtil.appendChild(parentMenu, newPage);
         return newPage;
      }
      else {
         return NavigationUtil.getChild(parentMenu, fragment);
      }
   }


   private String getCellValue(HSSFCell menuCell) {
      String result = null;
      if (menuCell.getCellType() != HSSFCell.CELL_TYPE_BLANK) {
         if (menuCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
            String cellValue = menuCell.getStringCellValue().trim();
            cellValue = cellValue.replaceAll("-", "");
            cellValue = cellValue.replaceAll("\"", "");
            if (cellValue != null && cellValue.length() > 0) {
               result = cellValue;
            }
         }
      }
      return result;
   }


   private Node createContentChannelForSite(Cloud cloud, Node parent) {
      Node rootNode = RepositoryUtil.getRootNode(cloud);
      return createContentChannelForPage(cloud, parent, rootNode);
   }


   private Node createContentChannelForPage(Cloud cloud, Node parent, Node parentChannel) {
      String fragmentFieldname = NavigationUtil.getFragmentFieldname(parent);
      String fragment = parent.getStringValue(fragmentFieldname);
      if (!RepositoryUtil.hasChild(parentChannel, fragment)) {
         Node contentChannel = RepositoryUtil.createChannel(cloud, parent.getStringValue(PagesUtil.TITLE_FIELD),
               fragment);

         RepositoryUtil.appendChild(parentChannel, contentChannel);
         return contentChannel;
      }
      else {
         return RepositoryUtil.getChild(parentChannel, fragment);
      }
   }


   private void createContentChannelChildren(Cloud cloud, Node parent, Node parentChannel) {
      NodeList pages = NavigationUtil.getChildren(parent);
      for (Iterator<Node> pagesiter = pages.iterator(); pagesiter.hasNext();) {
         Node page = pagesiter.next();
         Node contentChannel = createContentChannelForPage(cloud, page, parentChannel);

         createContentChannelChildren(cloud, page, contentChannel);
      }
   }

}
