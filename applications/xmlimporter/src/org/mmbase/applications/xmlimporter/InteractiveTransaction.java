/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.xmlimporter;

import java.util.*;
import java.io.File;
import org.mmbase.applications.xmlimporter.ObjectMerger;

/**
 * Extended from Transaction for interactive handling of dulpicates.
 * @since MMBase-1.5
 * @version $Id: InteractiveTransaction.java,v 1.4 2007-06-21 15:50:20 nklasens Exp $
 */
public class InteractiveTransaction extends Transaction {

   /**
    * Creates new InteractiveTransaction.
    * @param timeOut if the transactions is not finished after the timeout
    * (in seconds) the transaction is cancelled.
    * @param uti transaction info for current user.
    * @param key TransactionManager key for this transaction.
    * @param id TransactionHandler id for this transactions.
    * @param commitOnClose -  The user-specified commit-on-close setting.
    * True if this transaction is to be committed
    * when the user leaves it's context, false otherwise.
    * @param reportFile The file to use as duplicates file.
    * @param consultant The intermediate import object. Used to set and get status from and set and get objects to and from.
    */
   protected InteractiveTransaction(UserTransactionInfo uti, String key,
   String id, boolean commitOnClose, long timeOut, File reportFile,
   Consultant consultant) {
      super(uti, key, id, commitOnClose, timeOut, reportFile, consultant);
   }

   /**
    * Handles sitiuations where more then one similar objects are found to
    * merge with. This implementation confronts the user with the merge results.
    * The user can choose which result is preferred. The preferred result is
    * used to merge with.
    *
    * @param tempObj the original object
    * @param similarObjects the similar objects
    * @param merger the merger
    * @return True if duplicates are resolved.
    * Throws TransactionHandlerException When failing to resolve
    * the duplicates by consulting the user.
    * @throws TransactionHandlerException When a failure occurred.
    */
   protected boolean handleDuplicates(TmpObject tempObj, List<TmpObject> similarObjects,
   ObjectMerger merger) throws TransactionHandlerException {
      Iterator<TmpObject> iter = similarObjects.iterator();
      List<TmpObject> mergeResults = new ArrayList<TmpObject>();
      while (iter.hasNext() ) {
         TmpObject similarObject = iter.next();
         mergeResults.add(caculateMerge(similarObject, tempObj, merger));
      }

      int choice = consult(tempObj, mergeResults);

      merge(similarObjects.get(choice), tempObj, merger);
      return true;
   }

   /**
    * Handles sitiuations where more then one similar objects are found to
    * merge with. This implementation confronts the user with the merge results.
    * The user can choose which result is preferred. The preferred result is
    * used to merge with.
    *
    * @param tempObj1 the original object
    * @param mergeResults the mergeResults
    * @return the index number of the chosen mergeResult
    * @throws TransactionHandlerException If the situation could not be handled.
    */
   int consult(TmpObject originalObject, List<TmpObject> mergeResults)
   throws TransactionHandlerException {

      // Consult user.
      consultant.consultUser(originalObject, mergeResults);

      // Test if duplicates were actually resolved.
      if (!consultant.duplicatesResolved()) {
         throw new TransactionHandlerException(
         "Failed to resolve duplicates by consulting user.");
      }

      // Get users choice.
      return consultant.getChoice();
   }
}

