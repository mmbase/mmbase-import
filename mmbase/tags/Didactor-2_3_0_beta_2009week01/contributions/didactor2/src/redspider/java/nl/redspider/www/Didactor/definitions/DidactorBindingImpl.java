/**
 * DidactorBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package nl.redspider.www.Didactor.definitions;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.bridge.*;


import nl.didactor.component.redspider.dataobjects.*;
import nl.didactor.component.redspider.Processor;



public class DidactorBindingImpl implements nl.redspider.www.Didactor.definitions.DidactorPortType
{
   private static Logger log = Logging.getLoggerInstance(DidactorBindingImpl.class.getName());
   private Processor processor;


   public DidactorBindingImpl() {
      super();
      Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
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
         log.info("workgroup=" + participant.getWorkgroups_name());
         log.info("country="   + participant.getCountry());
         log.info("zipcode="   + participant.getZipcode());
         log.info("role="      + participant.getRoles_name());

         //classes
         ParticipantClasses partisipantClasses = participant.getClasses();
         if((partisipantClasses == null) || (partisipantClasses.getName() == null) || (partisipantClasses.getName().length == 0))
         {
            log.info("there are no classes for the person");
         }
         else
         {
            Object[] arrobjClasses = partisipantClasses.getName();
            for (int f = 0; f < arrobjClasses.length; f++)
            {
               String sClass = (String) arrobjClasses[f];
               log.info("class=" + sClass);
            }
         }

         //status
         if (participant.getStatus() == null)
         {
            log.info("there is no status for the person");
         }
         else
         {
            log.info("status=" + participant.getStatus().getValue());
         }

         //

      }
      catch(Exception e)
      {
         String sErrorMessage = "An error during *PARCING* the requset:" + e.toString();
         log.error(sErrorMessage);

         respParticipant.setResultcode("error");
         respParticipant.setAction("disable");
         respParticipant.setExternid("null");
         respParticipant.setMessage(sErrorMessage);
         return respParticipant;
      }


      //Processor
      try
      {
         String sDidactorActionType = processor.process(participant);
         if(processor.check_fields_dimension(participant))
         {
            respParticipant.setResultcode(ParticipantResponse.sResultCodeSuccess);
            respParticipant.setMessage("");
         }
         else
         {
            respParticipant.setResultcode(ParticipantResponse.sResultCodeWarning);
            respParticipant.setMessage("At least one of fields is out of dimensions.");
         }

         respParticipant.setExternid(participant.getExternid());
         respParticipant.setAction(sDidactorActionType);

      }
      catch(Exception e)
      {
         respParticipant.setResultcode(ParticipantResponse.sResultCodeError);
         respParticipant.setAction("disable");
         respParticipant.setExternid(participant.getExternid());
         respParticipant.setMessage("A error occured during *PROCESSING* of the request: "  + e.toString());
      }

      return respParticipant;
   }
}
