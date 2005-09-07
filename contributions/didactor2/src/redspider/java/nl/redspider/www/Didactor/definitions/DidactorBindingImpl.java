/**
 * DidactorBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package nl.redspider.www.Didactor.definitions;

import java.util.Calendar;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.bridge.*;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;


import nl.didactor.component.redspider.dataobjects.*;
import nl.didactor.component.redspider.Processor;



public class DidactorBindingImpl implements nl.redspider.www.Didactor.definitions.DidactorPortType
{
   private static Logger log = Logging.getLoggerInstance(DidactorBindingImpl.class.getName());
   private Processor processor;


   public DidactorBindingImpl()
   {
      super();

      CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
      Cloud cloud = cloudProvider.getAdminCloud();
      processor = new Processor(cloud);
   }



   public ParticipantResponse publishParticipant(Participant participant) throws java.rmi.RemoteException
   {
/*
      participant = new Participant();
      participant.setCity("New Orlean");
      participant.setAddress("trash");
      participant.setDayofbirth(Calendar.getInstance());
      participant.setFirstname("Vasia");
      participant.setInitials("Ibn");
      participant.setLastname("Pupkin");
      participant.setCountry("Russia");
      participant.setWorkgroups_name("test2");
      participant.setRoles_name("fucker");
      participant.setExternid("998");

      ParticipantClasses classes = new ParticipantClasses();
      String[] arrstrClasses = {"A12345B12", "CCCCCCCCCCCC", "D54321A56"};
      classes.setName(arrstrClasses);
      participant.setClasses(classes);

      ParticipantStatusType status = ParticipantStatusType.fromString("enabled");
      participant.setStatus(status);
*/

      //Response
      ParticipantResponse respParticipant = new ParticipantResponse();

      try
      {
         log.info("------------- A request has been received ------------");
         log.info("id=" + participant.getExternid());
         log.info("initials="  + participant.getInitials());
         log.info("firstname=" + participant.getFirstname());
         log.info("suffix="    + participant.getSuffix());
         log.info("lastname="  + participant.getLastname());

         ParticipantClasses partisipantClasses = participant.getClasses();
         Object[] arrobjClasses = partisipantClasses.getName();
         for(int f = 0; f < arrobjClasses.length; f++)
         {
            String sClass = (String) arrobjClasses[f];
            log.info("class=" + sClass);
         }

      }
      catch(Exception e)
      {
         String sErrorMessage = "An error during parsing requset, some fields are null:" + e.toString();
         log.error(sErrorMessage);

         respParticipant.setResultcode(DidactorResultType.fromValue("error"));
         respParticipant.setAction(DidactorActionType.fromString("disable"));
         respParticipant.setExternid("null");
         respParticipant.setMessage(sErrorMessage);
         return respParticipant;
      }


      //Processor
      try
      {
         DidactorActionType didactorActionType = processor.process(participant);

         respParticipant.setMessage("");
         respParticipant.setExternid(participant.getExternid());
         respParticipant.setAction(didactorActionType);
         respParticipant.setResultcode(DidactorResultType.fromValue("success"));

      }
      catch(Exception e)
      {
         respParticipant.setResultcode(DidactorResultType.fromValue("error"));
         respParticipant.setAction(DidactorActionType.fromString("disable"));
         respParticipant.setExternid(participant.getExternid());
         respParticipant.setMessage(e.toString());
      }

      return respParticipant;
   }
}
