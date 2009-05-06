/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.xmlimporter;

import java.util.List;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A Consultant object serves as the intermediary for communication between
 * the thread that processes a transaction on one hand, and threads that
 * handle interaction with the user on the other hand, to resolve ambiguity
 * when more than one similar object is found to merge an object with.
 *
 * The processing thread should call: <ul>
 * <li>consultUser() - first, to request the user to be donsulted.
 * <li>duplicatesResolved() - after a call to consultUser() , to find out of it
 * successfully resolved the ambiguity.
 * <li>getChoice() - to get the index of the mergeResult, chosen by the user
 * during the last succesfull call of consultUser().</ul>
 *
 * @author Erik Visser (Finalist IT Group)
 * @author Rob van Maris (Finalist IT Group)
 * @since MMBase-1.5
 * @version $Id: Consultant.java,v 1.4 2007-06-21 15:50:20 nklasens Exp $
 */
public class Consultant {

   /** Logger instance. */
   private static Logger log
   = Logging.getLoggerInstance(Consultant.class.getName());

   /** When import finished.
    */
   public final static int IMPORT_STARTING = 0;

   /** When import running.
    */
   public final static int IMPORT_RUNNING = 1;

   /** When import finished.
    */
   public final static int IMPORT_FINISHED = 2;

   /** When transaction time out.
    */
   public final static int IMPORT_TIMED_OUT = 3;

   /** When transaction time out.
    */
   public final static int IMPORT_EXCEPTION = 4;

   /** the original object */
   private TmpObject originalObject;

   /** the collection of mergeresults */
   private List<TmpObject> mergeResults;

   /** the index of the user chosen mergeresult */
   private int choice;

   /** the index of the user chosen mergeresult */
   private int importStatus;

   /** switch that indicates the import mode chosen by the user
    * true = interactive ; false = batch
    */
   private boolean interactive;

   /** switch to detect if duplicates are found during a BATCH import */
   private boolean duplicatesFound;

   /** Creates new Consultant */
   public Consultant() {
      init();
   }

   /** init all fields */
   public void init() {
      originalObject = null;
      mergeResults = null;
      choice = 0;
      importStatus = 0;
      interactive = false;
      duplicatesFound = false;
   }

   /** Consult the user for a mergeresult choice.
    * After a call to this method, call duplicatesResolved() to examine
    * if duplicates are resolved. If not the thread was interrupted.
    * @param originalObject The original object.
    * @param mergeResults The merge results to choose from.
    */
   public synchronized void consultUser(TmpObject originalObject,
   List<TmpObject> mergeResults) {

      this.originalObject = originalObject;
      this.mergeResults = mergeResults;
      try {
         while(!Thread.interrupted() && !duplicatesResolved()) {
            wait();
         }
      } catch (InterruptedException e) {}
   }

   /** sets the user chosen import mode
    * true if interactive false otherwise
    * @param duplicatesFound Set true if duplicates are found. False otherwise.
    */
   public synchronized void setDuplicatesFound(boolean duplicatesFound) {
      this.duplicatesFound = duplicatesFound;
   }

   /** Returns true if in a transaction duplicates are found.
    * @return True if duplicates are found. False otherwise.
    */
   public synchronized boolean duplicatesFound() {
      if (interactive) {
         return mergeResults != null;
      } else {
         return duplicatesFound;
      }
   }

   /** Returns true if the user has chosen a mergeresult.
    * @return True if duplicates are resolved. False otherwise.
    */
   public synchronized boolean duplicatesResolved() {
      return mergeResults == null;
   }

   /** Returns the index of the mergeresult the user has chosen.
    * Call this method after method duplicatesResolved() returns true and
    * before the next call to method consultUser(). Nou ok tis goed.
    * @return The index of the mergeresult the user has chosen.
    */
   public synchronized int getChoice() {
      return choice;
   }

   /** Sets the index of the mergeresult the user has chosen.
    * Call this method after method duplicatesResolved() returns true and
    * before the next call to method consultUser(). Nou ok tis goed.
    * @param choice Sets the index of the mergeresult the user has chosen.
    */
   public synchronized void setChoice(int choice) {
      this.choice = choice;
      // to indicact ea choice is made
      mergeResults = null;
      originalObject = null;
      // to wake up the consulting Thread
      notifyAll();
   }

   /** Returns the original object.
    * The consultant object holds an original object and a list of merge results.
    *
    * @return The original object.
    */
   public synchronized TmpObject getOriginalObject() {
      return originalObject;
   }

   /** Returns the a list of merge results.
    * The consultant object holds an original object and a list of merge results.
    *
    * @return The list of merge results.
    */
   public synchronized List<TmpObject> getMergeResults() {
      return mergeResults;
   }

   /** Sets the import status.
    * Import status can be:
    * <ul>
    * <li>starting
    * <li>running
    * <li>finished
    * <li>transaction timed out
    * <li>exception occured
    * </ul>
    * See public static final int class variables.
    * @param importStatus Set to a known import status.
    * See public static final int class variables.
    */
   public synchronized void setImportStatus(int importStatus) {
      this.importStatus = importStatus;
   }

   /** Returns true if import has finished.
    * False otherwise.
    * @return The actual import status.
    */
   public synchronized int getImportStatus() {
      return importStatus;
   }

   /** Sets the user chosen import mode.
    * True if interactive. False otherwise.
    *
    * @param interactive Set to true if user has chosen interactive importmode. Set false otherwise.
    */
   public synchronized void setInteractive(boolean interactive) {
      this.interactive = interactive;
   }

   /** Returns true if user has chosen interactive import mode.
    * False otherwise.
    * @return True if import mode is interactive. False otherwise.
    */
   public synchronized boolean interactive() {
      return interactive;
   }

   /**
    * Add to log using info. Can be used from a jsp.
    * @param source Name of the message source.
    * @param message Text to add to log.
    */
   public static void logInfo(String source, String message) {
      log.info("source: " + source + "message: " + message);
   }

   /**
    * Add to log using error. Can be used from a jsp.
    * @param source Name of the message source.
    * @param message Text to add to log.
    */
   public static void logError(String source, String message) {
      log.error("source: " + source + "message: " + message);
   }

   /**
    * Add to log using debug. Can be used from a jsp.
    * @param source Name of the message source.
    * @param message Text to add to log.
    */
   public static void logDebug(String source, String message) {
      if (log.isDebugEnabled()) {
         log.debug("source: " + source + "message: " + message);
      }
   }

   /**
    * Add to log using trace. Can be used from a jsp.
    * @param source Name of the message source.
    * @param message Text to add to log.
    */
   public static void logTrace(String source, String message) {
      if (log.isDebugEnabled()) {
         log.trace("source: " + source + "message: " + message);
      }
   }
}