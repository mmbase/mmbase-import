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
package nl.leocms.module;

import org.mmbase.module.Module;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import nl.leocms.util.PropertiesUtil;
import nl.leocms.util.ServerUtil;
import nl.leocms.emailservice.EmailServiceTimerTask;

/**
 * 
 * @author Ronald Kramp
 * @version $Revision  $
 */
public class EmailServiceModule extends Module implements Runnable {
   /** MMBase logging system */
	private static Logger log = Logging.getLoggerInstance(EmailServiceModule.class.getName());
	
   //Object sync;
   private static Thread thread = null;
   
	/** The mmbase instance */
	private MMBase mmb = null;
   private String webserviceURL = "http://localhost:8080/ggg/UserDataService.jws";
   
   /**
	 * @see org.mmbase.module.Module#onload()
	 */
	public void onload() {
	}
   
   /**
	 * @see org.mmbase.module.Module#init()
	 * Also read for the config parameters expiretime and signaleringperiod from de module.xml
	 */
	public void init() {
		mmb = (MMBase) Module.getModule("MMBASEROOT");
		// Start thread to wait for mmbase to be up and running.
		thread = new Thread(this);
      thread.setDaemon(true);
      thread.start();
	}
   
   /*
    * The thread in which the singaleringen will be added and removed
    */
   private void sendMailLijst() {
      try {
			Thread.sleep(60000);
		} 
		catch (InterruptedException e) {}
      log.info("sendMailLijst thread started");
      Calendar currentTaskCalendar = getDatumTijd();
      Timer emailServiceTimer = new Timer();
      EmailServiceTimerTask emailServiceTimerTask = new EmailServiceTimerTask(webserviceURL);
      
      if (currentTaskCalendar != null) {
         emailServiceTimer.schedule(emailServiceTimerTask, new Date(currentTaskCalendar.getTimeInMillis()));
      }

      webserviceURL = ServerUtil.getWebserviceUrl();
      log.info("Using webserviceURL: " + webserviceURL);

      while (true) {
         try {
            // wait for mmbase to put in default data
            Thread.sleep(60 * 60 * 1000); // om het uur kijken of de tijd is aangepast.
         } 
         catch (InterruptedException e) {
            log.warn("Interupted while sleeping , continuing");
         }
         Calendar cal = getDatumTijd();

         if ((cal != null) && (currentTaskCalendar != null)) {
            if (!cal.equals(currentTaskCalendar)) {
               // this will cancel the scheduled task, if it's currently running it will finish the task
               boolean hasNotRun = emailServiceTimerTask.cancel();
               log.info("emailServiceTimerTask has run: " + !hasNotRun);
               currentTaskCalendar = cal;
               emailServiceTimerTask = new EmailServiceTimerTask(webserviceURL);
               emailServiceTimer.schedule(emailServiceTimerTask,  new Date(currentTaskCalendar.getTimeInMillis()));
            }
         }
      }
   }
   
   public static Calendar getDatumTijd() {
      String datumtijd = PropertiesUtil.getProperty("emailservice.datumtijd");
      log.debug("datumtijd in property = " + datumtijd);         
      
      int dag = 0;
      int maand = 0;
      int jaar = 0;
      int uur = 0;
      int minuut = 0;
      
      try {
         dag = Integer.parseInt(datumtijd.substring(0, 2));
         maand = Integer.parseInt(datumtijd.substring(3, 5));
         jaar = Integer.parseInt(datumtijd.substring(6, 10));
         uur = Integer.parseInt(datumtijd.substring(11, 13));
         minuut = Integer.parseInt(datumtijd.substring(14, 16));
      }
      catch (NumberFormatException nfe) {
         log.error("Cannot parse datumtijd: " + nfe);
      }

      if (dag > 0) {
         // if dag 
         Calendar cal = Calendar.getInstance();
         
         cal.set(Calendar.DATE, dag);
         cal.set(Calendar.MONTH, maand - 1);
         cal.set(Calendar.YEAR, jaar);
         cal.set(Calendar.HOUR_OF_DAY, uur);
         cal.set(Calendar.MINUTE, minuut);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);
         return cal;
      }
      return null;
   }
   
   /**
	 * Wait for mmbase to be up and running,
	 * then execute the tests.
	 */
	public void run() {
		// Wait for mmbase to be up & running.
		while (!mmb.getState()) {
			try {
				Thread.sleep(1000);
			} 
			catch (InterruptedException e) {}
		}
		webserviceURL = ServerUtil.getWebserviceUrl();
      log.info("Using webserviceURL: " + webserviceURL);
		sendMailLijst();
	}
}