package nl.didactor.editwizards;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.mmbase.applications.editwizard.Config;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This filter is used to filter the searchlist results of the editwizard for learnobjects.
 * The filter can't filter out all the objects, because the data that is not saved
 * to MMBase can contain newly added or deleted relations between educations, modules and
 * learnblocks.  
 * 
 * @author Nico Klasens (Finalist IT Group), 10-feb-2004
 * @version $Id: LearnobjectsFilter.java,v 1.1 2004-11-01 12:52:42 jdiepenmaat Exp $
 */
public class LearnobjectsFilter {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(LearnobjectsFilter.class.getName());
   
   public static List filter(List results, Stack subObjects, Cloud cloud, boolean multilevel ) {
      if (!subObjects.empty()) {
         List relatedNodes = null;
         
         // A stack is just a list with the last element as the top element
         for (int i = subObjects.size() - 1; i >= 0; i--) {
            Config.SubConfig ewconfig = (Config.SubConfig) subObjects.get(i);
            if (ewconfig instanceof Config.WizardConfig) {
               Config.WizardConfig wizardConfig = (Config.WizardConfig) ewconfig;
               String objectnr = wizardConfig.objectNumber;
               if (objectnr != null && !"new".equals(objectnr)) {
                  Node wizardNode = cloud.getNode(objectnr);
                  if ("learnblocks".equals(wizardNode.getNodeManager().getName())) {
                     relatedNodes = getRelatedFromLearnBlock(wizardNode);
                     break;
                  }
                  if ("modules".equals(wizardNode.getNodeManager().getName())) {
                     relatedNodes = getRelatedFromModule(wizardNode);
                     break;
                  }
                  if ("educations".equals(wizardNode.getNodeManager().getName())) {
                     relatedNodes = getRelatedFromEducation(wizardNode);
                     break;
                  }
               }
            }
         }
         
         if (relatedNodes != null && !relatedNodes.isEmpty()) {
            log.debug("Learnobjects filtering out: " + relatedNodes.toString());
            Iterator resultsIter = results.iterator();
            while (resultsIter.hasNext()) {
               Node element = (Node) resultsIter.next();
               if (multilevel) {
                  element = cloud.getNode(element.getIntValue("learnobjects.number"));
               }
               if (relatedNodes.contains(element.getStringValue("number"))) {
                  resultsIter.remove();
               }
            }
         }
      }
      return results;
   }
   
   private static List getRelatedFromLearnBlock(Node node) {
      List result = new ArrayList();
      NodeList related = node.getRelatedNodes("modules", "posrel", "SOURCE");
      NodeIterator iter = related.nodeIterator();
      while (iter.hasNext()) {
         Node element = iter.nextNode();
         result.addAll(getRelatedFromModule(element));
      }
      return result;
   }

   private static List getRelatedFromModule(Node node) {
      List result = new ArrayList();
      NodeList related = node.getRelatedNodes("educations", "posrel", "SOURCE");
      NodeIterator iter = related.nodeIterator();
      while (iter.hasNext()) {
         Node element = iter.nextNode();
         result.addAll(getRelatedFromEducation(element));
      }
      return result;
   }

   private static List getRelatedFromEducation(Node node) {
      List numbers = new ArrayList();
      NodeList los = node.getCloud().getList(
            node.getStringValue("number"),
            "educations,posrel,modules,posrel,learnblocks,posrel,learnobjects",
            "learnobjects.number", null, null, null, "DESTINATION", true);
      NodeIterator iter = los.nodeIterator();
      while (iter.hasNext()) {
         Node element = iter.nextNode();
         numbers.add(element.getStringValue("learnobjects.number"));
      }
      return numbers;
   }
}
