package nl.didactor.component.redspider.test;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

import java.util.HashMap;
import java.util.Calendar;

import org.mmbase.bridge.*;

import nl.didactor.component.redspider.dataobjects.*;

import nl.didactor.component.redspider.Processor;



public class Run
{

   public static void main(String[] args)
   {

      HashMap user = new HashMap();
      user.put("username", "admin");
      user.put("password", "admin2k");

      CloudContext cloudContext = null;
      Cloud cloud = null;
      try
      {
         cloudContext = ContextProvider.getCloudContext("rmi://127.0.0.1:1111/didactor");
         cloud = cloudContext.getCloud("mmbase");
      }
      catch(Exception ex)
      {
         System.out.println("Failed to connect to the cloud");
         System.out.println(ex.toString());
         System.exit(1);
      }

      Processor processor = new Processor(cloud);

      Participant participant = new Participant();
      participant.setCity("New Orlean");
      participant.setAddress("trash");
      participant.setDayofbirth(Calendar.getInstance());
      participant.setFirstname("Vasia");
      participant.setInitials("Ibn");
      participant.setLastname("Pupkin");
      participant.setCountry("Russia");
      participant.setWorkgroups_name("test2");
      participant.setRoles_name("doorkeeper");
      participant.setExternid("998");
      participant.setSuffix("ibn");

      ParticipantClasses classes = new ParticipantClasses();
      String[] arrstrClasses = {"A12345B12", "BBBBBBBBBBBB", "D54321A56"};
      classes.setName(arrstrClasses);
      participant.setClasses(classes);

      ParticipantStatusType status = ParticipantStatusType.fromString("enabled");
      participant.setStatus(status);

      processor.process(participant);
   }



}
