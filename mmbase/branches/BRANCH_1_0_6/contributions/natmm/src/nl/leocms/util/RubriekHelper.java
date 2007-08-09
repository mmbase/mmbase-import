/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationList;
import org.mmbase.bridge.RelationManager;
import org.mmbase.util.logging.*;

import nl.leocms.util.tools.HtmlCleaner;

/**
 * @author Edwin van der Elst
 * Date :Oct 16, 2003
 *
 */
public class RubriekHelper {
   
   private static Logger log = Logging.getLoggerInstance(RubriekHelper.class);

   // defined in the default data rubriek.xml
   public static final String LEEUWARDEN_NL_RUBRIEK_ALIAS = "rubriek.leocms.nl";

   Cloud cloud;
   /**
    * @param cloud
    */
   public RubriekHelper(Cloud cloud) {
      super();
      this.cloud = cloud;
   }

   public Node createSubrubriek(Node parent) {
      NodeManager manager = cloud.getNodeManager("rubriek");
      Node newRubriek = manager.createNode();
      newRubriek.setStringValue("naam", "**Nieuw**");
      newRubriek.setStringValue("naam_eng", "**New**");
      newRubriek.setStringValue("naam_fra", "**Nieuw**");
      newRubriek.setStringValue("naam_de", "**Neu**");
      int parentLevel = parent.getIntValue("level");
      newRubriek.setIntValue("level", parentLevel + 1);
      newRubriek.commit();
      RelationManager relManager = cloud.getRelationManager("rubriek", "rubriek", "parent");
      Relation relation = relManager.createRelation( parent, newRubriek);
      RelationList existing = parent.getRelations("parent");
      int pos = -1;
      for (int i = 0; i < existing.size(); i++) {
         pos = Math.max(existing.getRelation(i).getIntValue("pos"), pos);
      }
      relation.setIntValue("pos", pos + 1);
      relation.commit();
      return newRubriek;
   }

   /**
    * Verander de volgorde van subrubrieken
    * @param parentNode - Node van de parent
    * @param childs - String met childnodenumbers bv. "170,173,178"
    */
   public void changeOrder(Node parentNode, String childs) {
//    Moved to RelationUtil:
//      StringTokenizer tokenizer = new StringTokenizer(childs,",");
//      List tokens=new ArrayList();
//      while (tokenizer.hasMoreTokens()) {
//         tokens.add(tokenizer.nextToken());
//      }
//      RelationList list = parentNode.getRelations("parent");
//      RelationIterator iter = list.relationIterator();
//      while (iter.hasNext()) {
//         Relation rel = iter.nextRelation();
//         int destination = rel.getDestination().getNumber();
//         rel.setIntValue("pos",tokens.indexOf(""+destination));
//         rel.commit();
//      }
      RelationUtil.reorderPosrel(parentNode,childs,"parent");
   }

   /**
    * Bepaal pad naar de root-rubriek (=rubriek zonder parents)
    * @param rubriek - start rubriek node
    * @return List met pad naar root, eerste node is de root, laatste is 'rubriek'
    */
   public List getPathToRoot(Node rubriek) {
      List ret = new ArrayList();

      NodeList parentList = rubriek.getRelatedNodes("rubriek", "parent", "SOURCE");
      while (parentList.size() != 0) {
         Node parent = (Node) parentList.get(0);
         ret.add(parent);
         parentList = parent.getRelatedNodes("rubriek", "parent", "SOURCE");
      }
      ret.add(0, rubriek);
      Collections.reverse(ret);
      return ret;
   }

   /**
    * Creates a string that represents the root path for the given rubriek.
    * @param rubriek
    * @return
    */
   public String getPathToRootString(String rubriek) {
      String ret="";
      Node pnode = cloud.getNode( rubriek );
      List path = getPathToRoot( pnode) ;
      for (Iterator i=path.iterator(); i.hasNext(); ) {
         Node n = (Node)i.next();
         ret += n.getStringValue("naam");
         if (i.hasNext()) {
            ret+=" - ";
         }
      }
      return ret;
   }

   /**
    * Is this path of rubrieken part of the first subsite?
    * @param path
    * @return
    */
   public boolean isFirstSubsite(List path) {
      boolean bIsFirstSubsite = false;
      if(path.size()>1) {
         String sRoot = ((Node) path.get(0)).getStringValue("number");
         String sSub = ((Node) path.get(1)).getStringValue("number");
         NodeList nodeList = cloud.getList(sRoot,"rubriek,parent,rubriek2","rubriek2.number", null, "parent.pos", "UP", "DESTINATION", true);
         if(nodeList.size()>0) {
            bIsFirstSubsite = nodeList.getNode(0).getStringValue("rubriek2.number").equals(sSub);
         }
      }
      return bIsFirstSubsite;
   }

   /**
    * Returns the subDir where the templates of this rubriek can be found
    * @param root the first rubriek of this subsite
    * @return subDir
    */
   public static String getSubDir(Node root) {
      String subDir = "";
      if(root!=null) {
         subDir = root.getStringValue("url_live");
         if(!subDir.equals("")) { subDir += "/"; }
      }
      return subDir;
   }

   /**
    * Creates a the url for the given rubriek starting with an '/'
    * @param rubriek
    * @return
    */
   public StringBuffer getUrlPathToRootString(Node rubriek, String contextPath) {
      StringBuffer url = new StringBuffer("");
      url.append(contextPath);
      List path = getPathToRoot(rubriek);
      if(path.size()>1) {
         int iFirstNode = (isFirstSubsite(path) ? 2 : 1);
         for(int i=iFirstNode; i<path.size(); i++) {
            Node n = (Node) path.get(i);
            url.append('/');
            url.append(HtmlCleaner.stripText(n.getStringValue("naam")));
         }
      }
      return url;
   }
   
   /**
    * Creates a the url for the given rubriek ending with an '/'
    * @param rubriek
    * @return
    */
   public String getSubsiteRubriek(String rubriekNumber) {
      Node rubriekNode = cloud.getNode(rubriekNumber);
      int rubriekLevel = rubriekNode.getIntValue("level");
      if (rubriekLevel == 0 || rubriekLevel == 1) {
         return rubriekNumber;
      } else {         
         return (new PaginaHelper(cloud)).getSubsiteRubriek(cloud,rubriekNumber);
      } 
   }

   /**
    * Returns the style to be used for this rubriek
    * @param rubriek
    * @return
    */
   public String getParentStyle(String rubriekNumber) {
      NodeList nodeList = cloud.getList(rubriekNumber, "rubriek,parent,rubriek2", "rubriek2.style", null, null, null, "SOURCE", true);
      if (nodeList.size() > 0) {
         return nodeList.getNode(0).getStringValue("rubriek2.style");
      }
      return "";
   }

   
   /**
    * Method that finds the Rubriek node using a rubriekpath as input.
    *
    *
    * @param path List with the urlPath decomposed in urlfragments starting with the root
    * @return Node that was found
    */
   public Node getRubriekWithRubriekenPath(List path, Node parentNode) {
      if( path.size()>0) {
         NodeList nl;
         if (parentNode == null) {
            String where = "rubriek.url = '" + (String)path.get(0) + "' AND rubriek.level = 2" ;
            nl = cloud.getList("", "rubriek", "rubriek.number", where, null, null, null, true);
         } else {
            String where = "rubriek.url = '" + (String)path.get(0) + "'";
            nl = cloud.getList(parentNode.getStringValue("rubriek.number"), "rubriek", "rubriek.number", where, null, null, "DESTINATION", true);
         }
         if (nl.size() > 0 ) {
            parentNode = (Node)nl.iterator().next();
            path.remove(0);
            return getRubriekWithRubriekenPath(path, parentNode);
         } else {
            return null;
         }
      } else {
         return parentNode;
      }

   }

   /**
    * Method that gets the rubrieknode with the given rubriek node number
    * @param rubriekNodeNumber node number of the rubriek
    * @return Node that was found
    */
   public Node getRubriekNode(String rubriekNodeNumber) {
      NodeManager manager = this.cloud.getNodeManager("rubriek");
      NodeList list = manager.getList("number='"+rubriekNodeNumber+"'",null,null);
      if (list.size()==1) {
         return list.getNode(0);
      }
      else {
         throw new RuntimeException("Rubriek "+rubriekNodeNumber+" not found");
      }
   }

   /**
    * Method that finds the Rubriek node using a pagina nodenumber as input.
    * @param path List with the urlPath decomposed in urlfragments starting with the root
    * @return Node that was found
    */
   public String getRubriekNodeNumberAsString(String paginaNodeNumber) {
      NodeList nodeList = cloud.getList(paginaNodeNumber, "pagina,posrel,rubriek", "rubriek.number", null, null, null, null, true);
      if (nodeList.size() > 0 ) {
         return ((Node)nodeList.iterator().next()).getStringValue("rubriek.number");
      }
      else {
         return null;
      }
   }

   /**
    * Method that finds the Rubriek node using a rubriek path as input.
    *
    * @param path String with the urlPath
    * @return list of nodes that were found
    */
    public ArrayList getRubriekWithRubriekenUrlPath(String path, Node parentNode) {
      NodeList nl = cloud.getNodeManager("rubriek").getList(null, "number", "DOWN");  
      Iterator iter = nl.listIterator();
      
      ArrayList rubriekNodeList = new ArrayList();
      Node rubriek = null;
      String rubriekUrl;
      while (iter.hasNext()) {
         rubriek = (Node) iter.next();
         rubriekUrl = getUrlPathToRootString(rubriek,"").toString();
         if (path.indexOf(rubriekUrl)>-1) {
            rubriekNodeList.add(rubriek);
         }
      }
      
      // FIX FOR NMCMS-206
      // matching rubrieks must be returned by reverse order of their length
      // the longest rubriek we can find that matches the passed url, will be the first in the returned list
      Collections.sort( rubriekNodeList, new Comparator()
      {
      public int compare( Object a, Object b )
         {
         Integer aLength = new Integer(getUrlPathToRootString((Node) a,"").toString().length());
         Integer bLength = new Integer(getUrlPathToRootString((Node) b,"").toString().length());
         return ( bLength ).compareTo( aLength );
         }
      } );
      
      return rubriekNodeList;
   }
   
   public List getSubRubrieken(String rubriekNodeNumber) {
      ArrayList rubriekNodeList = null;
      NodeList nodeList = cloud.getList(rubriekNodeNumber, "rubriek,parent,rubriek2", "rubriek2.number", null, "parent.pos", null, "DESTINATION", true);
      if ((nodeList != null) && (nodeList.size() > 0)) {
         rubriekNodeList = new ArrayList(nodeList.size());
         for (int i = 0; i < nodeList.size(); i++) {
            Node tempRubriekNode = nodeList.getNode(i);
            String tempRubriekNodeNumber = tempRubriekNode.getStringValue("rubriek2.number");
            rubriekNodeList.add(cloud.getNode(tempRubriekNodeNumber));
         }
      }
      return rubriekNodeList;
   }
   
   /**
    * Returns a sorted list of all visible rubrieken from the (sub)tree of rubriekNode
    * 
    * @param rubriekNodeNumber
    * @return NodeList nodeList
    */
   public NodeList getTreeNodes(String rubriekNodeNumber) {
      NodeList nodeList = cloud.getList(rubriekNodeNumber, 
          "rubriek1,parent,rubriek", "rubriek.number,rubriek.isvisible",null,"parent.pos","UP","DESTINATION",true);
      // determine the sublists
      NodeList [] subList = new NodeList[nodeList.size()];
      int i =0;
      int size = nodeList.size();
      while(i < size) {
          Node node = nodeList.getNode(i);
          boolean bIsVisible = !node.getStringValue("rubriek.isvisible").equals("0");
          if(bIsVisible) {
            subList[i] = getTreeNodes(node.getStringValue("rubriek.number"));
          }
          i++;
      }
      // merge the sublists in the nodelist
      i=0; int j=0;
      while(i < size) {
         if(subList[i]!=null) {
            nodeList.addAll(j+1,subList[i]);
            j=j+subList[i].size();
         }
         i++;       
         j++;
      }
      return nodeList;
   }
   
   /**
    * Returns the rubrieken and pages related to this rubriek. 
    * The key in the TreeMap is the position of the object
    * The value in the TreeMap is the object number
    * 
    * @param rubriekNode
    * @return TreeMap subObjects
    */
   public TreeMap getSubObjects(String rubriekNodeNumber, boolean showAll) {
      TreeMap subObjects = new TreeMap();
      // add sub-rubrieken (if any)
      NodeList nodeList = cloud.getList(rubriekNodeNumber,
          "rubriek,parent,rubriek2","rubriek2.number,rubriek2.isvisible,parent.pos",null,"parent.pos","up","DESTINATION",false);
      if ((nodeList != null) && (nodeList.size() > 0)) {
         for (int i = 0; i < nodeList.size(); i++) {
            Node tempRubriekNode = nodeList.getNode(i);
            boolean bIsVisible = !tempRubriekNode.getStringValue("rubriek2.isvisible").equals("0");
            if(showAll || bIsVisible){
               int iKey = tempRubriekNode.getIntValue("parent.pos");
               while(subObjects.containsKey(new Integer(iKey))) {
                  iKey++;
               }
               subObjects.put(new Integer(iKey), tempRubriekNode.getStringValue("rubriek2.number"));
            }
         }
      }
      // add pagina's
      String paginaConstraint = "";
      if(!showAll) {
         paginaConstraint = "pagina.embargo <= '" + System.currentTimeMillis() / 1000 + "' AND pagina.verloopdatum > '" + System.currentTimeMillis() / 1000 + "'";
      }
      nodeList = cloud.getList(rubriekNodeNumber,"rubriek,posrel,pagina","pagina.number,posrel.pos",paginaConstraint,"posrel.pos","up","DESTINATION",false);
      if ((nodeList != null) && (nodeList.size() > 0)) {
         for (int i = 0; i < nodeList.size(); i++) {
            Node tempRubriekNode = nodeList.getNode(i);
            int iKey = tempRubriekNode.getIntValue("posrel.pos");
            while(subObjects.containsKey(new Integer(iKey))) {
               iKey++;
            }
            subObjects.put(new Integer(iKey), tempRubriekNode.getStringValue("pagina.number"));
         }
      }
      return subObjects;
   }
   
   public TreeMap getSubObjects(String rubriekNodeNumber) {
      return getSubObjects(rubriekNodeNumber,false);
   }
   
   /**
    * Returns the first page related to this object
    * Return the objectNumber, if this object is a page
    * Descend into the tree, as long as the first subobject under the present rubriek is a rubriek
    * 
    * @param objectNodeNumber
    * @return pageID, -1 if no page found
    */
   public String getFirstPage(String objectNodeNumber) {
      
      String paginaNodeNumber = "-1";
      
      Node n = cloud.getNode(objectNodeNumber);
      String nType = n.getNodeManager().getName();
      if(nType.equals("pagina")){
      
         paginaNodeNumber = objectNodeNumber;
      
      } else {
           
         TreeMap subObjects = (TreeMap) getSubObjects(objectNodeNumber);
         while(subObjects.size()>0 && paginaNodeNumber.equals("-1")) {
            Integer nextKey = (Integer) subObjects.firstKey();
            String nextObject =  (String) subObjects.get(nextKey);
            
            n = cloud.getNode(nextObject);
            nType = n.getNodeManager().getName();
            if(nType.equals("pagina")){
               paginaNodeNumber = nextObject;
            } else { 
               subObjects = (TreeMap) getSubObjects(nextObject);
            }
         }
         
      }
      if(paginaNodeNumber.equals("-1")) { 
        log.error("there is no visible page related to object " + objectNodeNumber);
      }
      return paginaNodeNumber;
   }
   
   /**
    * Returns all visible pages for rubriek or pagina with number objectNumber
    * 
    * @param objectNumber
    * @return HashSet hm
    */
   public HashSet getAllPages(String objectNumber) {
      
      HashSet hm = new HashSet();
      
      Node n = cloud.getNode(objectNumber);
      String nType = n.getNodeManager().getName();
      if(nType.equals("pagina")){
      
         hm.add(objectNumber);
      
      } else {
           
         TreeSet subObjects = new TreeSet(getSubObjects(objectNumber).values());
         while(!subObjects.isEmpty()) {
            String nextObject =  (String) subObjects.first();
            subObjects.remove(nextObject);
            
            n = cloud.getNode(nextObject);
            nType = n.getNodeManager().getName();
            if(nType.equals("pagina")){
               hm.add(nextObject);
            } else { 
               subObjects.addAll(getSubObjects(nextObject).values());
            }
         }
         
      }
      return hm;
   }
   
   /**
    * Checks if the given rubriek object contains subrubrieken, pages or content elements.
    *
    * @param rubriekNode
    * @return true or false
    */
   public boolean isRubriekRemovable(Node rubriekNode) {
      /* nb: assert rubriekNode != null;
      assert "rubriek".equals(rubriekNode.getNodeManager().getName()); */
      NodeList hoofdRubrieken = rubriekNode.getRelatedNodes("rubriek", "hoofdrubriek", "DESTINATION");
      if (hoofdRubrieken.size() > 0) {
         return true;
      }
      NodeList subRubrieken = rubriekNode.getRelatedNodes("rubriek", "parent", "DESTINATION");
      if (subRubrieken.size() > 0) {
         return true;
      }
      NodeList pages = rubriekNode.getRelatedNodes("pagina", "posrel", "DESTINATION");
      if (pages.size() > 0) {
         return true;
      }
      NodeList contentElements = rubriekNode.getRelatedNodes("contentelement", "creatierubriek", "SOURCE");
      if (contentElements.size() > 0) {
         return true;
      }
      return false;
   }
    
    /**
    * Removes the rubriek
    *
    * @param rubriekNodeNumber
    */
   public void removeRubriek(String rubriekNodeNumber) {
      Node rubriekNode = cloud.getNode(rubriekNodeNumber);
      rubriekNode.deleteRelations();
      rubriekNode.delete(true);
   }
   
   /**
    * Returns the content elements associated with this rubriek by creatierubriek relation.
    *
    * @param rubriekNode
    * @return NodeList related content elements
    */
   public NodeList getContentElements(Node rubriekNode) {
	   return rubriekNode.getRelatedNodes("contentelement", "creatierubriek", "SOURCE");
   }
   
   /** gets the root rubriek number 
     */
   public String getRootRubriek() {
      return cloud.getNodeByAlias("root").getStringValue("number");
   }
}
