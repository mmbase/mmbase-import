package nl.didactor.component.competence.builders;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Vector;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.storage.search.implementation.NodeSearchQuery;


public class CompetencesBuilder extends MMObjectBuilder
{
   private static Logger log = Logging.getLoggerInstance(CompetencesBuilder.class);

   public boolean commit(MMObjectNode node)
   {
//      log.info("[CompetenceBuilder] Commiting of compitence node ID=" + node.getNumber());
      this.fillValueField(node);
      return super.commit(node);
   }

   public void commitAll()
   {//Commits all competence nodes which are stored in the base
//      log.info("[CompetenceBuilder] Commiting all competence nodes");

      Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
      NodeManager nodmanCompetence = cloud.getNodeManager("competencies");
      NodeList nodlistPeople = nodmanCompetence.getList(null, null, null);
      for(Iterator it = nodlistPeople.iterator(); it.hasNext(); )
      {
         Node node = (Node) it.next();
         node.setValue("name", node.getValue("name"));
         node.commit();
      }
/*
      try
      {
         for(Iterator it = this.getNodes(new NodeSearchQuery(this)).iterator(); it.hasNext(); )
         {
            MMObjectNode nodeCompetence = (MMObjectNode) it.next();
            System.out.println(nodeCompetence.getValue("name"));
            nodeCompetence.commit();
         }
      }
      catch (Exception e)
      {
         log.error("Commiting of all competence nodes failed. The error is:" + e.toString());
      }
*/
   }

   public void fillValueField(MMObjectNode node)
   {//Fills value field for the Node
      String sValue = (String) node.getValue("name");

      //Competence types
      Vector vectCompentenceTypesNodes = node.getRelatedNodes("competencetypes");
      boolean bFirst = true;
      for(Iterator it = vectCompentenceTypesNodes.iterator(); it.hasNext(); )
      {
         MMObjectNode nodeCompetenceType = (MMObjectNode) it.next();

         if(bFirst)
         {
            sValue += "; type=" + (String) nodeCompetenceType.getValue("name");
            bFirst = false;
         }
         else
         {
            sValue += ", " + (String) nodeCompetenceType.getValue("name");
         }
      }

      Vector vectProfileNodes = node.getRelatedNodes("profiles");
      HashSet hsetProfilesIDs = new HashSet();
      for(Iterator it = vectProfileNodes.iterator(); it.hasNext(); )
      {
         MMObjectNode nodeProfile = (MMObjectNode) it.next();
         hsetProfilesIDs.add("" + nodeProfile.getNumber());
      }

      //Core tasks
      Enumeration enumAllRelations = node.getAllRelations();
      bFirst = true;
      while(enumAllRelations.hasMoreElements())
      {
         MMObjectNode nodeRelation = (MMObjectNode) enumAllRelations.nextElement();
         if(hsetProfilesIDs.contains("" + nodeRelation.getValue("snumber")))
         {
            Vector vectCoreTasks = nodeRelation.getRelatedNodes("coretasks");
            for(Iterator it = vectCoreTasks.iterator(); it.hasNext(); )
            {
               MMObjectNode nodeCoreTask = (MMObjectNode) it.next();
               if(bFirst)
               {
                  sValue += "; kerntaak=";
                  bFirst = false;
               }
               else
               {
                  sValue += ", ";
               }
               sValue += (String) nodeCoreTask.getValue("name");
            }
         }
      }


      //Core assignments
      enumAllRelations = node.getAllRelations();
      bFirst = true;
      while(enumAllRelations.hasMoreElements())
      {
         MMObjectNode nodeRelation = (MMObjectNode) enumAllRelations.nextElement();
         if(hsetProfilesIDs.contains("" + nodeRelation.getValue("snumber")))
         {
            Vector vectCoreAssignments = nodeRelation.getRelatedNodes("coreassignments");
            for(Iterator it = vectCoreAssignments.iterator(); it.hasNext(); )
            {
               MMObjectNode nodeCoreAssignments = (MMObjectNode) it.next();
               if(bFirst)
               {
                  sValue += "; kernopgave=";
                  bFirst = false;
               }
               else
               {
                  sValue += ", ";
               }
               sValue += (String) nodeCoreAssignments.getValue("name");
            }
         }
      }


      //Profiles
      bFirst = true;
      for(Iterator it = vectProfileNodes.iterator(); it.hasNext(); )
      {
         MMObjectNode nodeProfile = (MMObjectNode) it.next();

         if(bFirst)
         {
            sValue += "; profiel=";
            bFirst = false;
         }
         else
         {
            sValue += "; ";
         }
         sValue += (String) nodeProfile.getValue("name");

         //Profile types
         Vector vectProfileTypeNodes = nodeProfile.getRelatedNodes("profiletypes");
         for(Iterator itType = vectProfileTypeNodes.iterator(); itType.hasNext(); )
         {
            MMObjectNode nodeProfileType = (MMObjectNode) itType.next();
            sValue += ", " + (String) nodeProfileType.getValue("name");
         }

         //Profile levels
         Vector vectProfileLevelNodes = nodeProfile.getRelatedNodes("profilelevels");
         for(Iterator itLevel = vectProfileLevelNodes.iterator(); itLevel.hasNext(); )
         {
            MMObjectNode nodeProfileLevel = (MMObjectNode) itLevel.next();
            sValue += ", " + (String) nodeProfileLevel.getValue("name");
         }
      }

      node.setValue("value", sValue);
      return;
   }
}
