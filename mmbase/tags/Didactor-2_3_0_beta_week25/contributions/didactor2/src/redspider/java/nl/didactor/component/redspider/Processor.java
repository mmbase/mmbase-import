package nl.didactor.component.redspider;

/**
 * <p>Title: Main request processror</p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: didactor.nl</p>
 * @author Alexey Zemskov
 * @version 1.0
 */

import java.util.Date;
import java.util.Iterator;
import java.text.SimpleDateFormat;

import nl.didactor.component.redspider.dataobjects.*;
import org.mmbase.bridge.*;
import java.text.*;



public class Processor
{
   private Cloud cloud;
   private NodeManager     nmPeople = null;
   private NodeManager     nmClasses  = null;
   private NodeManager     nmWorkgroups  = null;
   private NodeManager     nmRoles  = null;
   private NodeManager     nmProfiles = null;
   private NodeManager     nmPEPs = null;

   private NodeManager     nmClassRel  = null;
   private RelationManager rmClassRel = null;

   private NodeManager     nmRelated = null;
   private RelationManager rmRelated = null;



   public Processor(Cloud cloud)
   {
      this.cloud = cloud;
      this.nmPeople     = cloud.getNodeManager("people");
      this.nmClasses    = cloud.getNodeManager("classes");
      this.nmWorkgroups = cloud.getNodeManager("workgroups");
      this.nmRoles      = cloud.getNodeManager("roles");
      this.nmProfiles   = cloud.getNodeManager("profiles");
      this.nmPEPs       = cloud.getNodeManager("pop");


      this.nmClassRel   = cloud.getNodeManager("classrel");
      this.rmClassRel   = cloud.getRelationManager("classrel");

      this.rmRelated    = cloud.getRelationManager("related");
      this.nmRelated    = cloud.getNodeManager("insrel");
   }



   public String process(Participant participant)
   {
      NodeList nlParticipant = nmPeople.getList("people.externid='" + participant.getExternid() + "'", null, null);
      String  sDidactorActionType;
      Node nodeParticipant;

      if (nlParticipant.size() > 0)
      {//This externid is already present
         nodeParticipant = nlParticipant.getNode(0);
         sDidactorActionType = "modify";
      }
      else
      {//If the people not found we will create a new one
         nodeParticipant = this.createNewPerson();
         sDidactorActionType = "add";
      }


      if(participant.getStatus().equals(ParticipantStatusType.fromString("disabled")))
      {//The order is to disable the person
         nodeParticipant.setValue("person_status", "0");
         nodeParticipant.commit();

         this.deleteClassesRelations(nodeParticipant);
         this.deleteWorkgroupRelations(nodeParticipant);

         sDidactorActionType = "modify";
      }
      else
      {
         //Enable person
         nodeParticipant.setValue("person_status", "1");
         nodeParticipant.commit();

         //Store fields
         this.storeParticipantFields(nodeParticipant, participant);

         //Store classes
         this.storeClasses(nodeParticipant, participant.getClasses());

         //Store workgoup
         this.storeWorkgroup(nodeParticipant, (String) participant.getWorkgroups_name());

         //Store Role
         this.storeRole(nodeParticipant, (String) participant.getRoles_name());

         //Store POP
         this.storePEP(nodeParticipant, (String) participant.getFirstname() + " " + participant.getSuffix() + " " + participant.getLastname() + " (" + participant.getExternid() + ")");
      }

      return sDidactorActionType;
   }





   private Node createNewPerson()
   {
      Node nodePeople = nmPeople.createNode();
      nodePeople.commit();
      return nodePeople;
   }




   private void storeParticipantFields(Node nodePerson, Participant participant)
   {
      nodePerson.setValue("firstname", participant.getFirstname());
      nodePerson.setValue("initials", participant.getInitials());
      nodePerson.setValue("suffix", participant.getSuffix());
      nodePerson.setValue("lastname", participant.getLastname());
      nodePerson.setValue("address", participant.getAddress());
      nodePerson.setValue("zipcode", participant.getZipcode());
      nodePerson.setValue("city", participant.getCity());
      nodePerson.setValue("country", participant.getCountry());
      nodePerson.setValue("email", participant.getEmail());
      nodePerson.setValue("username", participant.getExternid());


      SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
      try
      {
         Date dateDayOfBirth = df.parse(participant.getDayofbirth());
         nodePerson.setValue("dayofbirth", "" + dateDayOfBirth.getTime()/1000);
      }
      catch (ParseException ex)
      {
         nodePerson.setValue("dayofbirth", "-1");
      }

      nodePerson.setValue("externid", participant.getExternid());
//      nodePerson.setValue("", participant.);

      nodePerson.commit();
   }





   private void storeClasses(Node nodePerson, ParticipantClasses participantClasses)
   {
      RelationList rlClassRelToDelete = nodePerson.getRelations("classrel", nmClasses);
      RelationList rlProfilesToDelete = nodePerson.getRelations("related", nmProfiles);

      Object[] arrstrClasses;
      if ((participantClasses != null) && (participantClasses.getName() != null))
      {
         arrstrClasses = participantClasses.getName();

         for(int f = 0; f < arrstrClasses.length; f++)
         {
            String sClassName = (String) arrstrClasses[f];
            NodeList nlClasses = nmClasses.getList("name='" + sClassName + "'", null, null);

            Node nodeClass;
            if(nlClasses.size() == 0)
            {
               nodeClass = this.createNewClass(sClassName);
               nodePerson.createRelation(nodeClass, rmClassRel).commit();
            }
            else
            {
               nodeClass = nlClasses.getNode(0);

               //Do we have a relation to this class already?
               NodeList nlClassRel = nmClassRel.getList("snumber='" + nodePerson.getNumber() + "' AND dnumber='" + nodeClass.getNumber() + "'", null, null);
               if(nlClassRel.size() == 0)
               {//there is no relation yet
                  nodePerson.createRelation(nodeClass, rmClassRel).commit();
               }
               else
               {
                  rlClassRelToDelete.remove(nlClassRel.get(0));
               }
            }

            //profile for the class
            Node nodeProfileRelation = null;
            try
            {
               nodeProfileRelation = this.storeProfile(nodePerson, sClassName.split("#")[2]);
               rlProfilesToDelete.remove(nodeProfileRelation);
            }
            catch (Exception ex)
            {
            }
         }
      }


      //Delete all old unneeded relations
      for(Iterator it = rlClassRelToDelete.iterator(); it.hasNext();)
      {
         ((Node) it.next()).delete(true);
      }
      //Delete all old unneeded profiles
      for(Iterator it = rlProfilesToDelete.iterator(); it.hasNext();)
      {
         ((Node) it.next()).delete(true);
      }
   }

   private Node createNewClass(String sClassName)
   {
      Node nodeClass = nmClasses.createNode();
      nodeClass.setValue("name", sClassName);
      nodeClass.commit();
//      hmClasses.put(sClassName, new Integer(nodeClass.getNumber()));
      return nodeClass;
   }

   private void deleteClassesRelations(Node nodePerson)
   {
      RelationList rellistClasses = nodePerson.getRelations("classrel", nmClasses);
      for(Iterator it = rellistClasses.iterator(); it.hasNext();)
      {
         Node nodeClassRelation = (Node) it.next();
         nodeClassRelation.delete(true);
      }
   }







   private void storeWorkgroup(Node nodePerson, String sWorkgroupName)
   {
      RelationList rlRelatedToDelete = nodePerson.getRelations("related", nmWorkgroups);

      if ((sWorkgroupName != null) && (!"".equals(sWorkgroupName)))
      {
         NodeList nlWorkgroups = nmWorkgroups.getList("name='" + sWorkgroupName + "'", null, null);

         Node nodeWorkgroup;
         if (nlWorkgroups.size() == 0)
         {
            nodeWorkgroup = this.createNewWorkgroup(sWorkgroupName);
            nodeWorkgroup.createRelation(nodePerson, rmRelated).commit();
         }
         else
         {
            nodeWorkgroup = nlWorkgroups.getNode(0);
            //Do we have a relation to this workgroup already?
            NodeList nlRelated = nmRelated.getList("snumber='" +
               nodeWorkgroup.getNumber() + "' AND dnumber='" +
               nodePerson.getNumber() + "'", null, null);
            if (nlRelated.size() == 0)
            { //there is no relation yet

               nodeWorkgroup.createRelation(nodePerson, rmRelated).commit();
            }
            else
            {
               rlRelatedToDelete.remove(nlRelated.get(0));
            }
         }
      }

      //Delete all old unneeded relations
      for(Iterator it = rlRelatedToDelete.iterator(); it.hasNext();)
      {
         ((Node) it.next()).delete(true);
      }
   }

   private Node createNewWorkgroup(String sWorkgroupName)
   {
      Node nodeWorkgroup = nmWorkgroups.createNode();
      nodeWorkgroup.setValue("name", sWorkgroupName);
      nodeWorkgroup.commit();
      return nodeWorkgroup;
   }

   private void deleteWorkgroupRelations(Node nodePerson)
   {
      RelationList rlWorkgroupsRelations = nodePerson.getRelations("related", nmWorkgroups);
      for(Iterator it = rlWorkgroupsRelations.iterator(); it.hasNext();)
      {
         Node nodeRelation = (Node) it.next();
         nodeRelation.delete(true);
      }
   }





   private void storeRole(Node nodePerson, String sRoleName)
   {
      RelationList rlRelatedToDelete = nodePerson.getRelations("related", nmRoles);

      if((sRoleName != null) &&(!"".equals(sRoleName)))
      {
         NodeList nlRoles = nmRoles.getList("name='" + sRoleName + "'", null, null);

         Node nodeRole;
         if (nlRoles.size() == 0)
         {
            nodeRole = this.createNewRole(sRoleName);
            nodePerson.createRelation(nodeRole, rmRelated).commit();
         }
         else
         {
            nodeRole = nlRoles.getNode(0);
            //Do we have a relation to this Role already?
            NodeList nlRelated = nmRelated.getList("snumber='" +
               nodePerson.getNumber() + "' AND dnumber='" + nodeRole.getNumber() +
               "'", null, null);
            if (nlRelated.size() == 0)
            { //there is no relation yet

               nodePerson.createRelation(nodeRole, rmRelated).commit();
            }
            else
            {
               rlRelatedToDelete.remove(nlRelated.get(0));
            }
         }
      }

      //Delete all old unneeded relations
      for(Iterator it = rlRelatedToDelete.iterator(); it.hasNext();)
      {
         ((Node) it.next()).delete(true);
      }
   }

   private Node createNewRole(String sRoleName)
   {
      Node nodeRole = nmRoles.createNode();
      nodeRole.setValue("name", sRoleName);
      nodeRole.commit();
      return nodeRole;
   }

   private void deleteRolesRelations(Node nodePerson)
   {
      RelationList rlRolesRelations = nodePerson.getRelations("related", nmRoles);
      for(Iterator it = rlRolesRelations.iterator(); it.hasNext();)
      {
         Node nodeRelation = (Node) it.next();
         nodeRelation.delete(true);
      }
   }







   private Node storeProfile(Node nodePerson, String sProfileName)
   {
      NodeList nlProfiles = nmProfiles.getList("name='" + sProfileName + "'", null, null);

      Node nodeProfile;
      if(nlProfiles.size() == 0)
      {
         nodeProfile = this.createNewProfile(sProfileName);
         Node relation = nodePerson.createRelation(nodeProfile, rmRelated);
         relation.commit();
         return relation;
      }
      else
      {
         nodeProfile = nlProfiles.getNode(0);
         //Do we have a relation to this Profile already?
         NodeList nlRelated = nmRelated.getList("snumber='" + nodePerson.getNumber() + "' AND dnumber='" + nodeProfile.getNumber() + "'", null, null);
         if(nlRelated.size() == 0)
         {//there is no relation yet

            Node relation = nodePerson.createRelation(nodeProfile, rmRelated);
            relation.commit();
            return relation;
         }
         else
         {
            return nlRelated.getNode(0);
         }
      }
   }

   private Node createNewProfile(String sProfileName)
   {
      Node nodeProfile = nmProfiles.createNode();
      nodeProfile.setValue("name", sProfileName);
      nodeProfile.commit();
      return nodeProfile;
   }












   private void storePEP(Node nodePerson, String sPEPName)
   {
      RelationList rlRelatedToDelete = nodePerson.getRelations("related", nmPEPs);

      if((sPEPName !=null) && (!"".equals(sPEPName)))
      {
         NodeList nlPEPs = nmPEPs.getList("name='" + sPEPName + "'", null, null);

         Node nodePEP;
         if (nlPEPs.size() == 0)
         {
            nodePEP = this.createNewPEP(sPEPName);
            nodePerson.createRelation(nodePEP, rmRelated).commit();
         }
         else
         {
            nodePEP = nlPEPs.getNode(0);
            //Do we have a relation to this PEP already?
            NodeList nlRelated = nmRelated.getList("snumber='" +
               nodePerson.getNumber() + "' AND dnumber='" + nodePEP.getNumber() +
               "'", null, null);
            if (nlRelated.size() == 0)
            { //there is no relation yet

               nodePerson.createRelation(nodePEP, rmRelated).commit();
            }
            else
            {
               rlRelatedToDelete.remove(nlRelated.get(0));
            }
         }
      }

      //Delete all old unneeded relations
      for(Iterator it = rlRelatedToDelete.iterator(); it.hasNext();)
      {
         ((Node) it.next()).delete(true);
      }
   }

   private Node createNewPEP(String sPEPName)
   {
      Node nodePEP = nmPEPs.createNode();
      nodePEP.setValue("name", sPEPName);
      nodePEP.commit();
      return nodePEP;
   }



   public boolean check_fields_dimension(Participant participant)
   {
      return
      (nmPeople.getField("externid").getMaxLength()  > participant.getExternid().length()) &&
      (nmPeople.getField("initials").getMaxLength()  > participant.getInitials().length()) &&
      (nmPeople.getField("firstname").getMaxLength() > participant.getFirstname().length()) &&
      (nmPeople.getField("suffix").getMaxLength()    > participant.getSuffix().length()) &&
      (nmPeople.getField("lastname").getMaxLength()  > participant.getLastname().length()) &&
      (nmPeople.getField("email").getMaxLength()     > participant.getEmail().length()) &&
      (nmPeople.getField("address").getMaxLength()   > participant.getAddress().length()) &&
      (nmPeople.getField("zipcode").getMaxLength()   > participant.getZipcode().length()) &&
      (nmPeople.getField("city").getMaxLength()      > participant.getCity().length()) &&
      (nmPeople.getField("country").getMaxLength()   > participant.getCountry().length()) &&
      true;
   }
}
