/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.xmlimporter;

import java.io.*;
import java.util.*;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.TemporaryNodeManager;
import org.mmbase.module.core.TransactionManager;
import org.mmbase.module.core.TransactionManagerException;
import org.mmbase.applications.xmlimporter.SimilarObjectFinder;
import org.mmbase.applications.xmlimporter.ObjectMerger;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class models a Temporary Cloud Transaction. The class:
 * <ul>
 * <li> is used for MMBase xml import (org.mmbase.applications.xmlimporter.TransactionParser).
 * <li> can be used by java classes by directly calling methods
 * </ul>
 * <br />See also the xml description in the tcp 2.0 project.
 *
 * @author Rob van Maris: Finalist IT Group
 * @since MMBase-1.5
 * @version $Id$
 */
public class Transaction implements Runnable {

    /** Logger instance. */
    private static Logger log = Logging.getLoggerInstance(Transaction.class);

    /** The temporary node manager. */
    private static TemporaryNodeManager tmpNodeManager;

    /** The transaction manager. */
    private static TransactionManager transactionManager;

    /** Base for unique id generation. */
    private static long uniqueId = System.currentTimeMillis();

    /** All user-related data. */
    private UserTransactionInfo uti;

    /** Transaction information for current user. */
    protected Consultant consultant;

    /** Key used by TransactionManager. */
    private String key;

    /** All non-anonymous object contexts in this transaction, mapped by id. */
    private HashMap<String, TmpObject> namedObjectContexts = new HashMap<String, TmpObject>();

    /** All object contexts in this transaction, mapped by key. */
    private HashMap<String, TmpObject> tmpObjects = new HashMap<String, TmpObject>();

    /** Ordered list of all object contexts in this transaction.
     * It reflects the order in which the objects are created
     * in the transaction. */
    private List<TmpObject> lstTmpObjects = new LinkedList<TmpObject>();

    /** User specified id. */
    private String id;

    /** Commit-on-close setting. */
    private boolean commitOnClose;

    /** Transaction timeout in seconds. */
    private long timeOut;

    /** Report file. */
    private File reportFile;

    /** Buffer to hold duplicates of this transaction. */
    private StringBuffer reportBuffer = new StringBuffer();

    /** Map for holding merge result objects. Key is the deleted object. */
    private Map<TmpObject, TmpObject> mergedObjects = new HashMap<TmpObject, TmpObject>();


    /** Flag, to be set when MergeExceptions occur.
     * Committing this transaction will not proceed when this flag is set,
     * but instead the contents of the reportBuffer will be written to a file.
     */
    private boolean resolvedDuplicates = true;

    /** Thread to monitor timeout. */
    private Thread kicker;

    /** Is the transaction finished or timedout */
    private boolean finished = false;

    /**
     * Creates new Transaction.
     * @param timeOut if the transactions is not finished after the timeout
     * (in seconds) the transaction is cancelled.
     * @param uti transaction info for current user.
     * @param key TransactionManager key for this transaction.
     * @param id TransactionHandler id for this transactions.
     * @param commitOnClose -  The user-specified commit-on-close setting.
     * True if this transaction is to be committed
     * when the user leaves it's context, false otherwise.
     * @param reportFile The file to use as reportfile.
     * @param consultant The intermediate import object. Used to set and get status from and set and get objects to and from.
     */
    protected Transaction(UserTransactionInfo uti, String key, String id,
    boolean commitOnClose, long timeOut, File reportFile, Consultant consultant) {

        this.uti = uti;
        this.key = key;
        this.id = id;
        this.commitOnClose = commitOnClose;
        this.timeOut = timeOut;
        this.reportFile = reportFile;
        this.consultant = consultant;
        start();
    }

    /**
     * Creates new Transaction.
     * @param timeOut if the transactions is not finished after the timeout
     *  (in seconds) the transaction is cancelled.
     * @param uti transaction info for current user.
     * @param key TransactionManager key for this transaction.
     * @param id TransactionHandler id for this transactions.
     * @param commitOnClose -  The user-specified commit-on-close setting.
     *  True if this transaction is to be committed
     *  when the user leaves it's context, false otherwise.
     */
    protected Transaction(UserTransactionInfo uti, String key, String id,
    boolean commitOnClose, long timeOut) {

        this.uti = uti;
        this.key = key;
        this.id = id;
        this.commitOnClose = commitOnClose;
        this.timeOut = timeOut;
        start();
    }

    /**
     * Finds the TransactionManager module.
     * The first time this method is called it looks up the
     * modules that transactions might use.
     * @return the TransactionManager module.
     */
    private static synchronized TransactionManager getTransactionManager() {
        if (transactionManager == null) {
            transactionManager = TransactionManager.getInstance();
            tmpNodeManager = transactionManager.getTemporaryNodeManager();

        }
        return transactionManager;
    }

    /**
     * Create a Transaction.
     * @param uti -  The UserTransactionInfo it belongs to.
     * @param id -  The user-specified id, null for anonymous transaction.
     * @param commitOnClose -  The user-specified commit-on-close setting.
     *  True if this transaction is to be committed
     *  when the user leaves it's context, false otherwise.
     * @param timeOut -  The user-specified time-out setting.
     * @return -  New transaction.
     * @throws TransactionHandlerException -  When failing to create the new transaction.
     */
    public static Transaction createTransaction(UserTransactionInfo uti,
    String id, boolean commitOnClose, long timeOut)
    throws TransactionHandlerException {

        // If no id is specified, it's an "anonymous" transaction.
        // Generate a unique id wich will be hidden for the user.
        boolean anonymous = (id == null);
        if (anonymous) {
            id = "AnTr" + uniqueId++; // Anonymous Transaction.
        }

        // Check transaction does not exist already.
        if (uti.knownTransactionContexts.get(id) != null) {
            throw new TransactionHandlerException(
            "Transaction id already exists: id = \"" + id + "\"");
        }

        // Create new transaction.
        String key = null;
        try {
            key = id; 
            getTransactionManager().createTransaction(id);
        } catch (TransactionManagerException e) {
            throw new TransactionHandlerException(e.getMessage());
        }

        Transaction transaction
        =  new Transaction(uti, key, id, commitOnClose, timeOut);

        // If not anonymous transaction,
        // register it in the list of all transaction of the user.
        if (!anonymous) {
            uti.knownTransactionContexts.put(id, transaction);
        }

        if (log.isDebugEnabled()) {
            log.debug("Transaction created: " + key);
        }
        return transaction;
    }

    /**
     * Create a Transaction.
     * @param uti the UserTransactionInfo it belongs to.
     * @param id the user-specified id, null for anonymous transaction.
     * @param commitOnClose -  The user-specified commit-on-close setting.
     * True if this transaction is to be committed
     * when the user leaves it's context, false otherwise.
     * @param timeOut the user-specified time-out setting.
     * @param reportFile The reportfile.
     * @param consultant The intermediate import object. Used to set and get status from and set and get objects to and from.
     * @return new transaction.
     * @throws TransactionHandlerException When failing to create
     * the new transaction.
     */
    public static Transaction createTransaction(UserTransactionInfo uti,
    String id, boolean commitOnClose, long timeOut, File reportFile,
    Consultant consultant)
    throws TransactionHandlerException {

        // If no id is specified, it's an "anonymous" transaction.
        // Generate a unique id wich will be hidden for the user.
        boolean anonymous = (id == null);
        if (anonymous) {
            id = "AnTr" + uniqueId++; // Anonymous Transaction.
        }

        // Check transaction does not exist already.
        if (uti.knownTransactionContexts.get(id) != null) {
            throw new TransactionHandlerException(
            "Transaction id already exists: id = \"" + id + "\"");
        }

        // Create new transaction.
        String key = null;
        try {
            getTransactionManager().createTransaction(id);
            key = id;
        } catch (TransactionManagerException e) {
            throw new TransactionHandlerException(e.getMessage());
        }

        Transaction transaction;
        if (consultant != null && consultant.interactive()) {
            transaction = new InteractiveTransaction(uti, key, id, commitOnClose,
            timeOut, reportFile, consultant);
        } else {
            transaction = new Transaction(uti, key, id, commitOnClose,
            timeOut, reportFile, consultant);
        }

        // If not anonymous transaction,
        // register it in the list of all transaction of the user.
        if (!anonymous) {
            uti.knownTransactionContexts.put(id, transaction);
        }

        if (log.isDebugEnabled()) {
            log.debug("Transaction created: " + key);
        }
        return transaction;
    }

    /**
     * Open previously created transaction.
     * This assumes that the transaction is not anonymous.
     * @return the transaction.
     * @param commitOnClose -  The user-specified commit-on-close setting.
     *  True if this transaction is to be committed
     *  when the user leaves it's context, false otherwise.
     * @param uti the UserTransactionInfo it belongs to.
     * @param id the user-specified id (not null).
     * @throws TransactionHandlerException if the transaction does not exist.
     */
    public static Transaction openTransaction(UserTransactionInfo uti,
    String id, boolean commitOnClose) throws TransactionHandlerException {

        // Check transaction does exist.
        if (!uti.knownTransactionContexts.containsKey(id)) {
            throw new TransactionHandlerException(
            "Transaction id does not exist: id = \"" + id + "\"");
        }

        // Retreive transaction, adjust commitOnClose setting.
        Transaction tr = uti.knownTransactionContexts.get(id);
        tr.commitOnClose = commitOnClose;
        return tr;
    }

    /**
     * This method should be called when leaving this transaction's context.
     * This will commit the transaction, when commitOnClose is set.
     * @throws TransactionHandlerException when a failure occured while
     *  committing the transaction.
     */
    public void leave() throws TransactionHandlerException {

        if (log.isDebugEnabled()) {
            log.debug("About to leave transaction: " + key);
        }

        if (commitOnClose) {
            commit();
        }
    }

    /**
     * Commit this transaction.
     * @throws TransactionHandlerException when a failure occured while
     *  committing the transaction:
     * <UL>
     * <LI> an exception thrown by MMBase TransactionManager.
     * <LI> an error while accessing the reportfile.
     * <LI> a transaction has timed out
     * </UL>
     */
    public void commit() throws TransactionHandlerException {
        if (consultant != null
        && consultant.getImportStatus() == Consultant.IMPORT_TIMED_OUT) {
            throw new TransactionHandlerException("Transaction with id="
            + id + " is timed out after "  + timeOut + " seconds.");
        }

        // Commit the transaction and stop thread.
        try {
            if (resolvedDuplicates) {
                // No MergeExceptions occurred: transaction can be committed.
                transactionManager.commit(uti.user, id);
                if (log.isDebugEnabled()) {
                    log.debug("transaction committed: " + key
                    + "\n" + reportBuffer.toString());
                }
            } else {
                // MergeExceptions occurred: don't commit, cancel transacton
                // write duplicates to reportBuffer instead

                log.warn("transaction not committed: " + key
                + "\n" + reportBuffer.toString());

                // Append reportfile.
                if (reportFile != null) {
                    Writer out = null;
                    try {
                        out = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(reportFile.getPath(),true)));
                        out.write(reportBuffer.toString());
                    } catch (Exception e) {
                        throw new TransactionHandlerException(
                        "Failed to append reportfile "+reportFile+": "+e);
                    } finally {
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e) {
                                throw new TransactionHandlerException(
                                "Failed to close reportfile "+reportFile+": "+e);
                            }
                        }
                    }
                }
                else {
                    if (log.isDebugEnabled()) {
                        log.debug("reportFile NULL");
                    }
                }

            }
        } catch (TransactionManagerException e) {
            throw new TransactionHandlerException(e.getMessage());
        } finally {
            stop();
        }

        // Remove from user's transaction contexts.
        uti.knownTransactionContexts.remove(id);
    }

    /**
     * Delete this transaction.
     * @throws TransactionHandlerException when:
     * <UL>
     * <LI>- a failure occured while deleting the transaction
     * <LI>- transaction has timed out
     * </UL>
     */
    public void delete() throws TransactionHandlerException {
        if (consultant != null
        && consultant.getImportStatus() == Consultant.IMPORT_TIMED_OUT) {
            throw new TransactionHandlerException("Transaction with id="
            + id + " is timed out after "  + timeOut + " seconds.");
        }
        // Cancel the transaction and stop thread.
        try {
            transactionManager.cancel(uti.user, id);
        } catch (TransactionManagerException e) {
            throw new TransactionHandlerException(e.getMessage());
        } finally {
            stop();
        }

        // Remove from user's transaction contexts.
        uti.knownTransactionContexts.remove(id);

        if (log.isDebugEnabled()) {
            log.debug("Transaction deleted: " + key);
        }
    }

    /**
     * Create object in the context of this transaction.
     *
     * @param objectId user-specified id for the new object
     *  (must be unique in this transaction context),
     *  or null for anonymous object.
     * @param type type of the new object.
     * @param disposeWhenNotReferenced flag: true if this object is
     * to be dropped when it has no relations on commit, false otherwise.
     * @return the new object in the temporary cloud.
     * @throws TransactionHandlerException When failing to create
     *  the new object.
     */
    public TmpObject createObject(String objectId, String type,
    boolean disposeWhenNotReferenced)
    throws TransactionHandlerException {

        // If no id is specified, it's an "anonymous" object.
        // Generate a unique id wich will be hidden for the user.
        boolean anonymous = (objectId == null);
        if (anonymous) {
            objectId = "AnOb" + uniqueId++; // Anonymous Object.
        }

        // Check object does not exist already.
        if (namedObjectContexts.get(objectId) != null) {
            throw new TransactionHandlerException(
            "Object id already exists: id = \"" + objectId + "\"");
        }

        // Create new object, and add to temporary cloud.
        tmpNodeManager.createTmpNode(type, uti.user.getName(), objectId);
        TmpObject tmpObject =
        new TmpObject(uti, objectId, false, disposeWhenNotReferenced);

        // Add to transaction.
        try {
            transactionManager.addNode(key, uti.user.getName(), objectId);
        } catch (TransactionManagerException e) {
            tmpNodeManager.deleteTmpNode(uti.user.getName(), objectId);
            throw new TransactionHandlerException(e.getMessage());
        }
        tmpObjects.put(tmpObject.getKey(), tmpObject);
        lstTmpObjects.add(tmpObject);

        // If not anonymous object,
        // register it in the list of named objects in the transaction.
        if (!anonymous) {
            namedObjectContexts.put(objectId, tmpObject);
        }

        if (log.isDebugEnabled()) {
            log.debug("Object created: " + tmpObject);
        }
        return tmpObject;
    }

    /**
     * Create relation in the context of this transaction.
     * @return the new relation object in the temporary cloud.
     * @param objectId user-specified id for the new object
     *  (must be unique in this transaction context),
     *  or null for anonymous object.
     * @param type type of the new relation.
     * @param source the user-specified id of the source object.
     * @param destination the user-specified id of the destination object.
     * @throws TransactionHandlerException when
     *  <UL>
     *  <LI>an object with this id already exists in this transaction context
     *  <LI>a relation object can't be created
     *  <LI>an object can't be added to a transaction
     *  </UL>
     */
    public TmpObject createRelation(String objectId, String type, String source,
    String destination) throws TransactionHandlerException {

        // If no id is specified, it's an "anonymous" object.
        // Generate a unique id wich will be hidden for the user.
        boolean anonymous = (objectId == null);
        if (anonymous) {
            objectId = "AnRel" + uniqueId++; // Anonymous Relation.
        }

        // Check object does not exist already.
        if (namedObjectContexts.get(objectId) != null) {
            throw new TransactionHandlerException(
            "Object id already exists: id = \"" + objectId + "\"");
        }

        // check if  source and destination objects exist
        if (namedObjectContexts.get(source) == null) {
            throw new TransactionHandlerException(
            "Unknown source object id: id = \"" + source + "\"");
        }

        if (namedObjectContexts.get(destination) == null) {
            throw new TransactionHandlerException(
            "Unknown destination object id: id = \"" + destination + "\"");
        }

        // Create new object, and add to temporary cloud.
        try {
            tmpNodeManager.createTmpRelationNode(
            type, uti.user.getName(), objectId, source, destination);
        } catch (Exception e) {
            throw new TransactionHandlerException(
            "Type \"" + type + "\" is not a proper relation.");
        }
        TmpObject tmpObject = new TmpObject(uti, objectId, true, false);

        // Add to transaction.
        try {
            transactionManager.addNode(key, uti.user.getName(), objectId);
        } catch (TransactionManagerException e) {
            tmpNodeManager.deleteTmpNode(uti.user.getName(), objectId);
            throw new TransactionHandlerException(e.getMessage());
        }
        tmpObjects.put(tmpObject.getKey(), tmpObject);
        lstTmpObjects.add(tmpObject);

        // If not anonymous object,
        // register it in the list of named objects in the transaction.
        if (!anonymous) {
            namedObjectContexts.put(objectId, tmpObject);
        }

        if (log.isDebugEnabled()) {
            log.debug("Relation created: " + tmpObject);
        }
        return tmpObject;
    }

    /**
     * Open object in the context of this transaction.
     * @param objectId the user-specified id  of the object.
     * @return the object in the temporary cloud.
     * @throws TransactionHandlerException when the id does not exist
     *  in the context of this transactions.
     */
    public TmpObject openObject(String objectId)
    throws TransactionHandlerException {

        // Check object does exist.
        if (namedObjectContexts.get(objectId) == null) {
            throw new TransactionHandlerException(
            "Object id does not exist: id = \"" + objectId + "\"");
        }

        TmpObject tmpObj =  namedObjectContexts.get(objectId);

        if (log.isDebugEnabled()) {
            log.debug("Opened object: " + tmpObj);
        }

        return tmpObj;
    }

    /**
     * Create access object for an object in the persistent cloud,
     * in the context of this transaction.
     *
     * If an accessObjects with the same objectId and mmbaseId then the already
     * existing accessObject is returned. This because in a stylesheet it's hard
     * to establish if an object already exists.
     *
     * @param objectId user-specified id of the new object
     *  (must be unique in this transaction context).
     * @param mmbaseId the mmbase id for the persistent object.
     * @return the access object in the temporary cloud.
     * @throws TransactionHandlerException When failing to create
     *  the access object.
     */
    public TmpObject accessObject(String objectId, int mmbaseId)
    throws TransactionHandlerException {
        // If no id is specified, it's an "anonymous" object.
        // Generate a unique id wich will be hidden for the user.
        boolean anonymous = (objectId == null);
        if (anonymous) {
            objectId = "AnOb_" + uniqueId++; // Anonymous Object.
        }

        // Check object does not exist already. If an accessObjects with the
        // same objectId and mmbaseId then the already existing accessObject is
        // returned. This because in a stylesheet it's hard to establish if an
        // object already exists.
        if (namedObjectContexts.get(objectId) != null) {
            TmpObject namedObject
            = namedObjectContexts.get(objectId);
            if (namedObject.getMMBaseId() == mmbaseId) {
                // Already accessed with this id,
                // return existing access object.
                return namedObject;
            }

            // Id already in use for another object.
            throw new TransactionHandlerException(
            "Object id already used for another object: id = \""
            + objectId + "\"");
        }

        // Get persistent object, and add to temporary cloud.
        TmpObject tmpObject = null;
        try {
            tmpNodeManager.getObject(
            uti.user.getName(), objectId, Integer.toString(mmbaseId));
            tmpObject = new TmpObject(uti, objectId, mmbaseId);
        } catch (Exception e) {
            // Exception occurs when object not found in persistent cloud.
            throw new TransactionHandlerException(
            "Can't find object with mmbase id " + mmbaseId + " - "
            + e.getMessage());
        }

        // Add to transaction.
        try {
            transactionManager.addNode(key, uti.user.getName(), objectId);
        } catch (TransactionManagerException e) {
            tmpNodeManager.deleteTmpNode(uti.user.getName(), objectId);
            throw new TransactionHandlerException(e.getMessage());
        }
        tmpObjects.put(tmpObject.getKey(), tmpObject);
        lstTmpObjects.add(tmpObject);

        // If not anonymous object,
        // register it in the list of named objects in the transaction.
        if (!anonymous) {
            namedObjectContexts.put(objectId, tmpObject);
        }

        if (log.isDebugEnabled()) {
            log.debug("Access object created: " + tmpObject);
        }
        return tmpObject;
    }

    /**
     * Deletes object from the context of this transaction, as
     * well as its relations.
     * Note that, when the object is an relation, this may
     * affect its source and/or destination object as well: if it
     * (the source/destination) is an input object that has its
     * disposedWhenNotReferenced flag set and this is the last
     * relation that references it, it is deleted as well.
     * @param tmpObject the object.
     * @throws TransactionHandlerException When failing to delete
     *  the object.
     */
    public void deleteObject(TmpObject tmpObject)
    throws TransactionHandlerException {

        // Remove from tmp cloud.
        try {
            transactionManager.removeNode(
            key, uti.user.getName(), tmpObject.getId());
        } catch (TransactionManagerException e) {
            throw new TransactionHandlerException(e.getMessage());
        }
        tmpNodeManager.deleteTmpNode(uti.user.getName(), tmpObject.getId());
        tmpObjects.remove(tmpObject.getKey());
        lstTmpObjects.remove(tmpObject);

        // Remove from list of named objects in the transaction.
        namedObjectContexts.remove(tmpObject.getId());

        // Delete its relations as well.
        Iterator<TmpObject> i = getRelations(tmpObject).iterator();
        while (i.hasNext()) {
            TmpObject relation = i.next();
            if (stillExists(relation)) {
                if (log.isDebugEnabled()) {
                    log.debug("About to delete relation " + relation
                    + " because the referenced object "
                    + tmpObject.getKey() + " is deleted.");
                }
                deleteObject(relation);
            }
        }

        // If this is a relation, take care of source and destination as well.
        if (tmpObject.isRelation()) {
            // Delete source when this is required, based on its
            // disposeWhenNotReferenced flag.
            Object _snumber = tmpObject.getField(TmpObject._SNUMBER);
            if (!_snumber.equals("")) {
                TmpObject source = tmpObjects.get(_snumber);
                dropIfRequested(source);
            }

            // Delete destination when this is required, based on
            // its disposeWhenNotReferenced flag.
            Object _dnumber = tmpObject.getField(TmpObject._DNUMBER);
            if (!_dnumber.equals("")) {
                TmpObject destination = tmpObjects.get(_dnumber);
                dropIfRequested(destination);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Object deleted: " + tmpObject);
        }
    }

    /**
     * Mark object in the context of this transaction for deletion.
     * @param deleteRelations -  Set to true if all relations are to be deleted too, set to false otherwise.
     * @param tmpObject -  The object.
     * @throws TransactionHandlerException -  When a failure occurred.
     */
    public void markDeleteObject(TmpObject tmpObject, boolean deleteRelations)
    throws TransactionHandlerException {
        // Mark accessed mmbase object for deletion from persistent cloud.
        try {
            transactionManager.deleteObject(
            key, uti.user.getName(), tmpObject.getId());
        } catch (TransactionManagerException e) {
            throw new TransactionHandlerException(e.getMessage());
        }

        // Check its relations.
        Iterator<TmpObject> iRelations = getRelations(tmpObject).iterator();
        while (iRelations.hasNext()) {
            TmpObject relation = iRelations.next();
            if (relation.isAccessObject()) {
                // Relation in persistent cloud.
                if (!deleteRelations) {
                    // The object cannot be deleted, because it has relations
                    // attached, wich are not allowed to be deleted.
                    throw new TransactionHandlerException(
                    "Object has relation(s) attached to it "
                    + "(use deleteRelations=\"true\").");
                } else {
                    // Mark relation for delete as well.
                    markDeleteObject(relation, true);
                }
            }
        }

        // Remove from temporary cloud.
        tmpNodeManager.deleteTmpNode(uti.user.getName(), tmpObject.getId());
        tmpObjects.remove(tmpObject.getKey());
        lstTmpObjects.remove(tmpObject);

        // Remove from list of named objects in the transaction.
        namedObjectContexts.remove(tmpObject.getId());

        if (log.isDebugEnabled()) {
            log.debug("Access object marked for deletion: "
            + tmpObject);
        }
    }

    /**
     * Merges all objects in this transaction of a given type.
     * @param objectType The type of the objects to merge.
     * @param finder SimilarObjectFinder instance that prescribes the
     *  actions necessary to find similar objects.
     * @param merger ObjectMerger instance that prescribes the actions
     *  necessary to merge similar objects.
     * @throws TransactionHandlerException When a failure occurred.
     */
    public void mergeObjects(String objectType, SimilarObjectFinder finder,
    ObjectMerger merger) throws TransactionHandlerException {

        // Create snapshot of all objects in this transaction.
        TmpObject[] objects = lstTmpObjects.toArray(new TmpObject[0]);

        // For all objects in the snapshot of this transaction.
        for (TmpObject tempObj1 : objects) {
            // Check object still exists and is the specified type.
            if ( stillExists(tempObj1) &&
            tempObj1.getNode().getName().equals(objectType)) {

                // Search for similar object.
                List<TmpObject> similarObjects = finder.findSimilarObject(this, tempObj1);

                if (similarObjects.size() == 1) {
                    // One object found, merge the objects.
                    TmpObject tempObj2 = similarObjects.get(0);
                    // Merge objects (deletes one of both as well).
                    // Note the order: tempObj2 comes from the part of the
                    // transaction that is already merged or is an access object.
                    merge(tempObj2, tempObj1, merger);
                } else if (similarObjects.size() > 1) {
                    // More than one object found: ambiguity.
                    log.warn("More than one similar object found: " + similarObjects);

                    resolvedDuplicates = resolvedDuplicates && handleDuplicates(
                    tempObj1, similarObjects, merger);
                    if (!resolvedDuplicates) {
                        break;
                    }
                } else if (similarObjects.size() == 0) {
                    // No object found: delete current object when not allowed to add.
                    if (!merger.isAllowedToAdd(tempObj1)) {
                        deleteObject(tempObj1);
                    }
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("All objects of type " + objectType + " merged.");
        }
    }

    /**
     * Handles sitiuations where more then one similar objects are found to
     * merge with. This implementation writes the transaction to a file
     * includng comments on the duplicates found. The transaction is cancelled.
     * The file can be used to examine the duplicates and process the
     * transaction later on.
     * @param tempObj the original object.
     * @param similarObjects the similar objects.
     * @param merger the merger.
     * @return True if duplicates are resolved.
     * This implementation always returns false because duplicates are not resolved.
     * Throws TransactionHandlerException When failing to handle the duplicates
     * as desired.
     * @throws TransactionHandlerException When a failure occurred.
     */
    protected boolean handleDuplicates(TmpObject tempObj, List<TmpObject> similarObjects,
    ObjectMerger merger) throws TransactionHandlerException {
        // set duplicates flag
        if (consultant != null ) {
            consultant.setDuplicatesFound(true);
        }
      appendReportBuffer("\n"+"<!-- *** DUPLICATES FOUND START *** -->\n");
      appendReportBuffer("what follows:\n");
      appendReportBuffer("- the original object\n");
      appendReportBuffer("- similar object blocks\n");
      appendReportBuffer("where a similar object block holds: " +
                         "a mergecandidate and a mergeresult\n");
      appendReportBuffer("and a mergeresult is thew result of a merge of " +
                         "original object and mergecandidate -->\n\n");

      appendReportBuffer("<!-- *** original object *** -->\n");
      appendReportBuffer(tempObj.toXML()+"\n");

      Iterator<TmpObject> iter = similarObjects.iterator();
      while (iter.hasNext() ) {
         TmpObject similarObject = iter.next();
         appendReportBuffer("<!-- *** start similar object block *** -->\n\n");
         appendReportBuffer("<!-- *** mergeCandidate *** -->\n");
         appendReportBuffer(similarObject.toXML() + "\n");
         appendReportBuffer("<!-- *** mergeResult *** -->\n");
         appendReportBuffer(caculateMerge(similarObject, tempObj, merger).toXML() + "\n");
         appendReportBuffer("<!-- *** end similar object block *** -->\n\n");
      }
      appendReportBuffer("<!-- *** DUPLICATES FOUND END *** -->\n\n");
      return false;
    }

    /**
     * Calculates the fields that would result from merging two
     * temporary objects in this transaction. This does not affect the
     * actual objects in the transaction. A new TmpObject is created
     * outside this transaction, to hold the result.
     * @param merger ObjectMerger instance that prescribes the actions
     * necessary to merge similar objects.
     * @param tempObj1 First object.
     * @param tempObj2 Second object.
     * @return A TmpObject instance that holds the calculated fields. This
     * object exists outside this transaction.
     */
    protected TmpObject caculateMerge(TmpObject tempObj1, TmpObject tempObj2, ObjectMerger merger){
        // If the only the second object is an access object, swap the objects.
        // After that we can be certain that if only one of the objects is
        // an access object, it is the first one.  The first object will be
        // the merge target, the second will be deleted afterward.
        if (!tempObj1.isAccessObject() && tempObj2.isAccessObject()) {
            TmpObject to = tempObj1;
            tempObj1 = tempObj2;
            tempObj2 = to;
        }

        if (log.isDebugEnabled()) {
            log.debug("About to calculate merge objects: " + tempObj1.getKey()
            + "(target) and " + tempObj2.getKey());
        }

        // Merge fields.
        Iterator<String> fieldNames = tempObj1.getNode().getBuilder().getFieldNames().iterator();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();

            // Merge field for all fields except "number" and "owner".
            if (!fieldName.equals("number") && !fieldName.equals("owner")) {
                merger.mergeField(tempObj1, tempObj2, fieldName);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Calculate merge finished for objects:\n target: " + tempObj1.getKey()
            + " with object: " + tempObj2.getKey());
        }
        return tempObj1;
    }

    /**
     * Merges two temporary objects in this transaction.
     * Afterwards one of these will contain the merged object and the other
     * one will have been deleted.
     * If neither of the objects is an access object, the first one will
     * become the merged object, and the second will be deleted.
     * @param merger ObjectMerger instance that prescribes the actions
     * necessary to merge similar objects.
     * @param tempObj1 First object.
     * @param tempObj2 Second object.
     * @throws TransactionHandlerException When a failure occurred.
     */
    public void merge(TmpObject tempObj1, TmpObject tempObj2, ObjectMerger merger) throws TransactionHandlerException {
        // If the only the second object is an access object, swap the objects.
        // After that we can be certain that if only one of the objects is
        // an access object, it is the first one.
        // The first object will be the merge target, the second will
        // be deleted afterward.
        if (!tempObj1.isAccessObject() && tempObj2.isAccessObject()) {
            TmpObject to = tempObj1;
            tempObj1 = tempObj2;
            tempObj2 = to;
        }

        if (log.isDebugEnabled()) {
            log.debug("About to merge objects: " + tempObj1.getKey()
            + "(target) and " + tempObj2.getKey());
        }

        // Merge fields.
        Iterator<String> fieldNames  = tempObj1.getNode().getBuilder().getFieldNames().iterator();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();

            // Merge field for all fields except "number" and "owner".
            if (!fieldName.equals("number") && !fieldName.equals("owner")) {
                merger.mergeField(tempObj1, tempObj2, fieldName);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Fields merged of objects: " + tempObj1.getKey()
            + "(target) and " + tempObj2.getKey());
        }

        // Merge relations.
        merger.mergeRelations(tempObj1, tempObj2,
        getRelations(tempObj1), getRelations(tempObj2));

        if (log.isDebugEnabled()) {
            log.debug("Relations merged of objects: " + tempObj1.getKey()
            + "(target) and " + tempObj2.getKey());
        }
        // Remove duplicate relations from the merged object.
        List<TmpObject> relations = getRelations(tempObj1);
        for (int i = 0; i < relations.size(); i++) {

            // All relations of tempObj1.
            TmpObject relation1 = relations.get(i);

            // Check if this relation still exists.
            if (!stillExists(relation1)) {
                continue;
            }

            // If it duplicates an existing relation, drop one of them.
            for (int i2 = i + 1; i2 < relations.size(); i2++) {
                TmpObject relation2 = relations.get(i2);

                if (log.isDebugEnabled()) {
                    log.debug("Relation1: " + relation1);
                    log.debug("Relation2: " + relation2);
                    log.debug("equalRelations: " + equalRelations(relation1, relation2));
                }

                if (stillExists(relation2)
                && equalRelations(relation1, relation2)
                && merger.areDuplicates(relation1, relation2)) {
                    // Equal relation found, drop this one if it is
                    // not an access object. If it is, drop the other one.
                    if (!relation1.isAccessObject()) {
                        if (log.isDebugEnabled()) {
                            log.debug("About to delete duplicate relation: "
                            + relation1.getKey());
                        }
                        deleteObject(relation1);
                    } else {
                        if (relation2.isAccessObject()) {
                            if (log.isDebugEnabled()) {
                                log.debug("About to mark for deletion "
                                + "duplicate relation: "
                                + relation2.getKey());
                            }
                            markDeleteObject(relation2, false);
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("About to delete duplicate relation: "
                                + relation2.getKey());
                            }
                            deleteObject(relation2);
                        }
                    }
                }
            }
        }

        // Dispose of tempObj2.
        if (tempObj2.isAccessObject()) {
            // Delete tempObj2 from persistent cloud.
            if (log.isDebugEnabled()) {
                log.debug("About to mark object " + tempObj2.getKey()
                + "for deletion, having merged it with "
                + tempObj1.getKey());
            }
            markDeleteObject(tempObj2, true);
        } else {
            // Delete tempObj2 from temporary cloud.
            if (log.isDebugEnabled()) {
                log.debug("About to delete object " + tempObj2.getKey()
                + ", having merged it with " + tempObj1.getKey());
            }
            deleteObject(tempObj2);
        }

        if (log.isDebugEnabled()) {
            log.debug("Objects merged: " + tempObj1.getKey()
            + "(target) and " + tempObj2.getKey());
        }

        // Put merge result in map. Key is the deleted object. Used for tracking
        // the relation between a deleted original object and the object tha
        // the object that contains the merge result.
        mergedObjects.put(tempObj2, tempObj1);
    }

    /**
     * Gets an access object for a specified node in the persistent
     * cloud. If such an access object already exists in this transaction,
     * this object is returned, otherwise a new access object is created.
     * @param mmbaseId MMBase number of the specified node.
     * @return the access object in this transaction context,
     * or null if such an access object does not exist.
     * @throws TransactionHandlerException When unable to create
     * the access object.
     */
    public TmpObject getAccessObject(int mmbaseId)
    throws TransactionHandlerException {
        // No need to search if mmbaseId is not a valid MMBase Id.
        if (mmbaseId == -1) {
            return null;
        }
        // Search through the objects in this transaction.
        Iterator<TmpObject> i = lstTmpObjects.iterator();
        while (i.hasNext()) {
            TmpObject tmpObject = i.next();
            if (tmpObject.getMMBaseId() == mmbaseId) {
                // Found, return it.
                return tmpObject;
            }
        }

        // Not found, create new access object.
        return accessObject(null, mmbaseId);
    }

    /**
     * For an object in this transaction, looks up all its relations,
     * and returns access objects for these.
     * @param tmpObject An object in the temporary cloud (can
     * be an access objects).
     * @return List of access objects for the found relations.
     * @exception TransactionHandlerException When a failure occurred.
     */
    public List<TmpObject> getRelations(TmpObject tmpObject)
    throws TransactionHandlerException {

        List<TmpObject> accessObjects = new ArrayList<TmpObject>();

        // If it's an access object, access all its relations in persistent cloud.
        if (tmpObject.isAccessObject()) {
            List<MMObjectNode> relations = tmpObject.getRelationsInPersistentCloud();
            Iterator<MMObjectNode> i = relations.iterator();
            while (i.hasNext()) {
                MMObjectNode relation = i.next();

                // Get access object for relation.
                // It may have been accessed before in this transaction, and
                // have its source/destination changed in the process.
                // For this reason source/destination have to be tested again
                // for this object. This will be done in the next step.
                getAccessObject(relation.getIntValue("number"));
            }
        }

        // Visit all its relations in temporary cloud, add them to the list.
        // This includes the access objects that were created for all
        // relations found in the persistent cloud.
        Iterator<TmpObject> i2 = lstTmpObjects.iterator();
        while (i2.hasNext()) {
            TmpObject tmpObj2 = i2.next();
            if (tmpObj2.isRelation()
            && (tmpObject.isSourceOf(tmpObj2)
            || tmpObject.isDestinationOf(tmpObj2))) {

                // Relation: add
                accessObjects.add(tmpObj2);
            }
        }
        return accessObjects;
    }

    /**
     * Drops an object from the temporary cloud, based on its
     * disposeWhenNotReferenced flag - i.e. drop it only when it
     * is not an access object, is has no relations and the flag
     * is set.
     * @param tmpObject An object in the temporary cloud.
     * @throws TransactionHandlerException When a failure occurred.
     */
    void dropIfRequested(TmpObject tmpObject)
    throws TransactionHandlerException {
        if (stillExists(tmpObject) // not deleted already
        && !tmpObject.isAccessObject() // is not access object
        && getRelations(tmpObject).size() == 0 // is unreferenced
        && tmpObject.getDisposeWhenNotReferenced()) { // flag is set

            // Delete.
            if (log.isDebugEnabled()) {
                log.debug("About to delete object " + tmpObject.getKey()
                + " because it has become unreferenced.");
            }
            deleteObject(tmpObject);
        }
    }

    /**
     * Test if two objects in the temporary cloud represent the same relation
     * (are of same relation type and have the same source and destination objects).
     * This takes into account that an (access)
     * object in the temporary cloud may represent an object in the
     * persistent cloud.
     * @param tmpObj1 The first object.
     * @param tmpObj2 The second object.
     * @return True if both objects represent the same relation,
     * false otherwise.
     */
    protected boolean equalRelations(TmpObject tmpObj1, TmpObject tmpObj2) {
        // Test if they're both relations.
        if (!tmpObj1.isRelation() || !tmpObj2.isRelation()) {
            return false;
        }

        // Test same relationtype.
        if (tmpObj1.getNode().getIntValue(TmpObject.RNUMBER)
        != tmpObj2.getNode().getIntValue(TmpObject.RNUMBER)) {
            return false;
        }

        // Test same source.
        String sourceId = tmpObj1.getNode().getStringValue(TmpObject._SNUMBER);
        if (!sourceId.equals("")) {
            // Source of tmpObj1 is temporary node.
            TmpObject source = tmpObjects.get(sourceId);
            if (!source.isSourceOf(tmpObj2)) {
                return false;
            }
        } else {
            sourceId = tmpObj2.getNode().getStringValue(TmpObject._SNUMBER);
            if (!sourceId.equals("")) {
                // Source of tmpObj2 is temporary node.
                TmpObject source = tmpObjects.get(sourceId);
                if (!source.isSourceOf(tmpObj1)) {
                    return false;
                }
            } else {
                // Sources of both is persistent node.
                if (!tmpObj1.getNode().getStringValue(TmpObject.SNUMBER).equals(
                tmpObj2.getNode().getStringValue(TmpObject.SNUMBER))) {
                    return false;
                }
            }
        }

        // Test same destination.
        String destinationId
        = tmpObj1.getNode().getStringValue(TmpObject._DNUMBER);
        if (!destinationId.equals("")) {
            // Destination of tmpObj1 is temporary node.
            TmpObject destination = tmpObjects.get(destinationId);
            if (!destination.isDestinationOf(tmpObj2)) {
                return false;
            }
        } else {
            destinationId = tmpObj2.getNode().getStringValue(TmpObject._DNUMBER);
            if (!destinationId.equals("")) {
                // Destination of tmpObj2 is temporary node.
                TmpObject destination = tmpObjects.get(destinationId);
                if (!destination.isDestinationOf(tmpObj1)) {
                    return false;
                }
            } else {
                // Destinations of both is persistent node.
                if (!tmpObj1.getNode().getStringValue(TmpObject.DNUMBER).equals(
                tmpObj2.getNode().getStringValue(TmpObject.DNUMBER))) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Tests if this object still exists in the temporary cloud.
     * @param tmpObject A temporary object.
     * @return True if the object still exists in the temporary
     *  cloud, false otherwise.
     */
    protected boolean stillExists(TmpObject tmpObject) {
        return lstTmpObjects.contains(tmpObject);
    }

    /**
     * Key accessor.
     * @return TransactionManager key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets HashMap of all non-anonymous object contexts, mapped by their id.
     * @return the object context map.
     */
    HashMap<String, TmpObject> getObjectContexts() {
        return namedObjectContexts;
    }

    /**
     * Gets (unmodifiable) list of all temporary objects in the transaction.
     * @return List of all temporary objects in the transaction.
     */
    public List<TmpObject> getTmpObjects() {
        return Collections.unmodifiableList(lstTmpObjects);
    }

    /**
     * Gets merged object, resulting from previous merge operations.
     * After a merge the result is put in a map with the deleted object as key
     * so it can be looked up by this method.
     * @param tempObj1 The original object.
     * @return The object that contains the result of the previous merge operation.
     * This can be tempObj1 itself or the object tempObj1 is merged with.
     */
    public TmpObject getMergedObject(TmpObject tempObj1) {
        TmpObject tempObj2 = mergedObjects.get(tempObj1);
        if (tempObj2 == null) {
            tempObj2 = tempObj1;
        }
        return tempObj2;
    }

    /**
     * Add text to reportBufferFile of this transaction.
     * @param str Text to add to reportBuffer.
     */
    public void appendReportBuffer(String str) {
        reportBuffer.append(str);
    }

    /**
     * Start the Transaction. If it is not stopped explicitly
     * (commit or delete), it will timeout eventually.
     */
    protected void start() {
        if (kicker == null) {
            kicker = new Thread(this, "TR " + key);
            kicker.start();
        }
    }

    /**
     * Stop the Transaction.
     */
    protected synchronized void stop() {

        kicker = null;
        finished = true;
        this.notify();

        log.info("Stop transaction: " + new Date().toString());
    }

    /**
     * Wait assynchronously for the transaction to time out.
     * This can be ended by invoking stop().
     */
    public void run() {
        synchronized(this) {
            try {
                wait(timeOut*1000);
            } catch (InterruptedException e) {
            }
            uti.knownTransactionContexts.remove(id);

            if (!finished) {
                if (consultant != null) {
                    consultant.setImportStatus(Consultant.IMPORT_TIMED_OUT);
                }
                log.warn("Transaction with id=" + id + " is timed out after "
                + timeOut + " seconds.");
            }
        }
    }
}
