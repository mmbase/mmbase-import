/**
 * DidactorBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */


package nl.redspider.www.Didactor.definitions;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import nl.didactor.component.redspider.dataobjects.*;


public class DidactorBindingImpl implements nl.redspider.www.Didactor.definitions.DidactorPortType
{

   private static Logger log = Logging.getLoggerInstance(DidactorBindingImpl.class.getName());


   public ParticipantResponse publishParticipant(Participant participant) throws java.rmi.RemoteException
   {
      try
      {

         System.out.println(participant);

         log.info("------------- A request has been got ------------");
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
         log.error("An error during parsing requset=" + e.toString());
      }


      try
      {
         ParticipantResponse respParticipant = new ParticipantResponse();
         respParticipant.setMessage("RULEZ");
         respParticipant.setExternid("555");
         respParticipant.setAction(DidactorActionType.fromString("add"));
         respParticipant.setResultcode(DidactorResultType.fromValue("success"));

         return respParticipant;

      }
      catch (IllegalArgumentException e)
      {
         System.out.println(e);
      }

      return null;
   }
}
