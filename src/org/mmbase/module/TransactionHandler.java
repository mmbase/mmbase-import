/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import org.mmbase.util.*;
import org.mmbase.module.core.*;

import java.util.*;
import java.io.*;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.*;
import org.xml.sax.*;


/**
 * TransactionHandler Module
 *
 * @author  $Author: daniel $ 
 * @version $Revision: 1.5 $ $Date: 2000-10-22 09:57:29 $
 */
 
public class TransactionHandler 
	extends Module 
	implements TransactionHandlerInterface {
	
	/*
	 * Inner classes
	 *
	 * a work around for the user problem
	 *
	 */
	class User {
		private String name;
		public User(String name) {
			this.name= name;
		}
		
		String getName() { return name;}
	}

	/*
	 * own exception class
	 */
	class TransactionHandlerException extends Exception {
		TransactionHandlerException(String s) { super(s); }
	}
	
	/* 
	 *container class for info per user for thread safety
	 */
	class UserTransactionInfo {
		
		public boolean anonymousTransaction = true;
		public String currentTransactionContext = null;
		public Hashtable knownTransactionContexts = new Hashtable();
		
		public boolean anonymousObject = true;
		public String currentObjectContext = null;
		public Hashtable knownObjectContexts = new Hashtable();
	}
	
	private String	_classname = getClass().getName();
	private static boolean _debug=false;
	private void 	debug( String msg ) { System.out.println( _classname +":"+ msg ); }

	// hashtable used to cache per user for thread safety
	// the construct ((UserTransactionInfo) cashUser.get(currentUser))
	// is used to refer to the current info indexed per user
	private static Hashtable cashUser;
	private static Random rand;
	
	private static TransactionManagerInterface transactionManager;
	private static TemporaryNodeManagerInterface tmpObjectManager;

	private MMBase mmbase;
	
	public TransactionHandler() {
	}
	
	/**
	 * Inits the module (startup final step 2).
	 * This is called second on startup, the module is expected
	 * to read the environment variables it needs. Startup threads,
	 * open connections etc.
	 */
	public void init(){
		UserTransactionInfo user;

		if (_debug) debug(">> init TransactionHandler Module ");
		
		// initialize static hashtable if necc.
		if (cashUser == null) cashUser = new Hashtable();
		if (rand == null) rand = new Random();
		
		mmbase=(MMBase)getModule("MMBASEROOT");
		transactionManager = new TransactionManager(mmbase);
		tmpObjectManager = new TemporaryNodeManager(mmbase);
	}
	
	public void onload(){
		if (_debug) debug(">> onload TransactionHandler Module ");
	}

private final String xmlHeader =
	"<?xml version='1.0'?> <!DOCTYPE TRANSACTION SYSTEM \"Transactions.dtd\">";
	
	/*
	 * handleTransaction is the method that is called externally
	 * by scanparser. It is the start of the whole chain.
	 */
public void handleTransaction(String template, sessionInfo session, scanpage sp) {
			
		if (_debug) debug(" >>>>handleTransaction in TransactionHandler ");
		if (_debug) debug(template);
		if (_debug) debug(session.toString());
		if (_debug) debug(sp.toString());

		/*
			This SHOULD be retrieved from the USERS module which is not finished yet
		*/
		User currentUser = new User("U" + rand.nextInt(10000000));
		UserTransactionInfo user = new UserTransactionInfo();
		cashUser.put(currentUser, user);
		
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(template));
		parse(currentUser,null, is);

		/*
			needs better handling
		*/
		cashUser.remove(currentUser);
	}


	private void parse(User currentUser,String xFile, InputSource iSource) {
	
	
		DOMParser parser = new DOMParser();
		
		try {
		   if (xFile ==  null) {
		   	if (_debug) debug("parsing input: " + iSource.toString());
		   	parser.parse(iSource);
		   }
		   if (iSource ==  null) {
		   	if (_debug) debug("parsing file: " + xFile);
		   	parser.parse(xFile);
		   }
		} catch (SAXException se) {
		   se.printStackTrace();
			 return;
		   //System.exit(-1);
		} catch (IOException ioe) {
		   ioe.printStackTrace();
		   return;
		   //System.exit(-2);
		}
		
		
		try {  //catch TransactionHandlerException's here
			
		Document document = parser.getDocument();
		
		// get <Transactions> context
		Element docRootElement = document.getDocumentElement();
		
		// do for all transaction contexts (create-, open-, commit- and deleteTransaction)
		NodeList transactionContextList = docRootElement.getChildNodes();
		for (int i = 0; i < transactionContextList.getLength(); i++) {
			String id = null, commit = null, time = null;
			boolean noId = true;
			Node currentTransactionArgumentNode = null;

			Node transactionContext = transactionContextList.item(i);
			String tName = transactionContext.getNodeName();
			if (tName.equals("#text")) continue;
			
			//get attributes for transaction
			NamedNodeMap nm = transactionContext.getAttributes();
			if (nm != null) {
				//id
				currentTransactionArgumentNode = nm.getNamedItem("id");
				if (currentTransactionArgumentNode != null)
					id = currentTransactionArgumentNode.getNodeValue();
				if (id == null) {
					noId = true;
					id = uniqueId();
				} else {
					noId = false;
				}
				//commitOnClose
				currentTransactionArgumentNode = nm.getNamedItem("commitOnClose");
				if (currentTransactionArgumentNode != null)
					commit = currentTransactionArgumentNode.getNodeValue();
				if (commit==null) commit="true";
				//timeOut
				currentTransactionArgumentNode = nm.getNamedItem("timeOut");
				if (currentTransactionArgumentNode != null)
					time = currentTransactionArgumentNode.getNodeValue();
				if (time==null) time="6";
				
			}
			initTransactionContext(currentUser,tName, id, commit, time);
			
			//do for all object contexts (create-, open-, get- and deleteObject)
			NodeList objectContextList = transactionContext.getChildNodes();
			for (int j = 0; j < objectContextList.getLength(); j++) {
				String oId = null, oType = null, oMmbaseId = null;
				boolean noOId = true;
				Node currentObjectArgumentNode = null;
				
				Node objectContext = objectContextList.item(j);
				String oName = objectContext.getNodeName();
				if (oName.equals("#text")) continue;
				
				//get attributes
				NamedNodeMap nm2 = objectContext.getAttributes();
				if (nm2 != null) {
					//oId
					currentObjectArgumentNode = nm2.getNamedItem("id");
					if (currentObjectArgumentNode != null) oId = currentObjectArgumentNode.getNodeValue();
					if (oId == null) {
						noOId = true;
						oId = uniqueId();
					} else {
						noOId = false;
					}
					//type
					currentObjectArgumentNode = nm2.getNamedItem("type");
					if (currentObjectArgumentNode != null) oType = currentObjectArgumentNode.getNodeValue();
					//mmbaseId
					currentObjectArgumentNode = nm2.getNamedItem("mmbaseId");
					if (currentObjectArgumentNode != null) oMmbaseId = currentObjectArgumentNode.getNodeValue();
				}
				initObjectContext(currentUser,oName, oId, oType, oMmbaseId);
				
				//do for all field contexts (setField)
				NodeList fieldContextList = objectContext.getChildNodes();
				for (int k = 0; k < fieldContextList.getLength(); k++) {
					String fieldName = null, fieldValue = null;
					
					Node fieldContext = fieldContextList.item(k);
					if (fieldContext.getNodeName().equals("#text")) continue;
					//get attributes
					NamedNodeMap nm3 = fieldContext.getAttributes();
					if (nm3 != null) {
							currentObjectArgumentNode = nm3.getNamedItem("par");
							if (currentObjectArgumentNode != null)
								fieldName = currentObjectArgumentNode.getNodeValue();
							currentObjectArgumentNode = nm3.getNamedItem("val");
							if (currentObjectArgumentNode != null)
								fieldValue = currentObjectArgumentNode.getNodeValue();
							executeFieldContext(currentUser,oId, fieldName, fieldValue);
					}
				}
				
				exitObjectContext(currentUser,oName, oId, oType, oMmbaseId);
			}
			
			exitTransactionContext(currentUser,tName, id, commit, time);
		}
		
		} catch (TransactionHandlerException t) {
			System.out.println(t);
			System.out.println("parsing stopped");
		}
	}
	
	
	private void initTransactionContext(User currentUser,String tName, String id, String commit, String time)
		throws TransactionHandlerException {
			
		if (_debug) debug(" -> " + tName + " id(" + id + ") commit(" + commit + ") time(" + time + ")");
		
		// create transaction, if no Id create one, remember it's anonymous
		if (tName.equals("createTransaction")) {
			if (id == null) {
				id = uniqueId();
				((UserTransactionInfo) cashUser.get(currentUser)).anonymousTransaction = true;
			}
			else {
				((UserTransactionInfo) cashUser.get(currentUser)).anonymousTransaction = false;
			}
			// check for existence
			if (((UserTransactionInfo) cashUser.get(currentUser)).knownTransactionContexts.get(id) != null) {
				throw new TransactionHandlerException(tName + " transaction id already exists: " + id);
			}
			// actually create and administrate if not anonymous
			((UserTransactionInfo) cashUser.get(currentUser)).currentTransactionContext =
				transactionManager.create(currentUser, id);
			if (!((UserTransactionInfo) cashUser.get(currentUser)).anonymousTransaction)
				((UserTransactionInfo) cashUser.get(currentUser)).knownTransactionContexts.put(id, ((UserTransactionInfo) cashUser.get(currentUser)).currentTransactionContext);
			return;
		} // end createTransaction
		
		// except for create (above), all (open commit, cancel) need existing id
		if (id == null) throw 
				new TransactionHandlerException("id is null for " + tName);
		((UserTransactionInfo) cashUser.get(currentUser)).currentTransactionContext = (String) ((UserTransactionInfo) cashUser.get(currentUser)).knownTransactionContexts.get(id);
		if (((UserTransactionInfo) cashUser.get(currentUser)).currentTransactionContext == null) throw 
				new TransactionHandlerException(tName + "id is not known " + id);
				
		if (tName.equals("openTransaction")) {
			// no-op we only need ((UserTransactionInfo) cashUser.get(currentUser)).currentTransactionContext
		} // end openTransaction
		
		if (tName.equals("commitTransaction")) {
			//no-op, we do on exit
		} // end commitTransaction
		
		if (tName.equals("deleteTransaction")) {
			transactionManager.cancel(currentUser, id);
			((UserTransactionInfo) cashUser.get(currentUser)).currentTransactionContext = null;
			((UserTransactionInfo) cashUser.get(currentUser)).knownTransactionContexts.remove(id);
		} // end deleteTransaction
		
	} 
	
	private void initObjectContext(User currentUser,String oName, String id, String type, String oMmbaseId)
		throws TransactionHandlerException {
			
		if (_debug) debug("\t -> " + oName + " id(" + id + ") type(" + type + ") oMmbaseId(" + oMmbaseId + ")");
		// check if we are inside transaction context, if not error
		if (((UserTransactionInfo) cashUser.get(currentUser)).currentTransactionContext == null) throw
				new TransactionHandlerException(oName + " id " + id + " : not in tansaction context");
		// create object, if no Id create one, remember it's anonymous
		if (oName.equals("createObject")) {
			if (id == null) {
				id = uniqueId();
				((UserTransactionInfo) cashUser.get(currentUser)).anonymousObject = true;
			}
			else {
				((UserTransactionInfo) cashUser.get(currentUser)).anonymousObject = false;
			}
			// check for existence
			if (((UserTransactionInfo) cashUser.get(currentUser)).knownObjectContexts.get(id) != null) {
				throw new TransactionHandlerException(oName + " Object id already exists: " + id);
			}
			// actually create and administrate if not anonymous
			((UserTransactionInfo) cashUser.get(currentUser)).currentObjectContext =
				tmpObjectManager.createTmpNode(type, currentUser.getName(), id);
			if (!((UserTransactionInfo) cashUser.get(currentUser)).anonymousObject)
				((UserTransactionInfo) cashUser.get(currentUser)).knownObjectContexts.put(id, ((UserTransactionInfo) cashUser.get(currentUser)).currentObjectContext);
			// add to tmp cloud
			transactionManager.addNode(((UserTransactionInfo) cashUser.get(currentUser)).currentTransactionContext, ((UserTransactionInfo) cashUser.get(currentUser)).currentObjectContext);
			return;
		} // end createObject
		
		if (oName.equals("getObject")) {
			if (id == null) {
				id = uniqueId();
				((UserTransactionInfo) cashUser.get(currentUser)).anonymousObject = true;
			}
			else {
				((UserTransactionInfo) cashUser.get(currentUser)).anonymousObject = false;
			}
			// check for existence
			if (((UserTransactionInfo) cashUser.get(currentUser)).knownObjectContexts.get(id) != null) {
				throw new TransactionHandlerException(oName + " Object id already exists: " + id);
			}
			if (oMmbaseId == null) {
				throw new TransactionHandlerException(oName + " no MMbase id: ");
			}
			// actually get and administrate if not anonymous
			MMObjectNode m  = tmpObjectManager.getNode(oMmbaseId);
			((UserTransactionInfo) cashUser.get(currentUser)).currentObjectContext =
				tmpObjectManager.getObject(oMmbaseId);
			if (!((UserTransactionInfo) cashUser.get(currentUser)).anonymousObject)
				((UserTransactionInfo) cashUser.get(currentUser)).knownObjectContexts.put(id, ((UserTransactionInfo) cashUser.get(currentUser)).currentObjectContext);
			// add to tmp cloud
			transactionManager.addNode(((UserTransactionInfo) cashUser.get(currentUser)).currentTransactionContext, ((UserTransactionInfo) cashUser.get(currentUser)).currentObjectContext);
			return;
		} // end getObject
		
		// except for create, get (above), all (open, delete) need existing id
		if (id == null) throw 
				new TransactionHandlerException("id is null for " + oName);
		((UserTransactionInfo) cashUser.get(currentUser)).currentObjectContext = (String) ((UserTransactionInfo) cashUser.get(currentUser)).knownObjectContexts.get(id);
		if (((UserTransactionInfo) cashUser.get(currentUser)).currentObjectContext == null) throw 
				new TransactionHandlerException(oName + "id is not known " + id);
				
		if (oName.equals("openObject")) {
			// no-op we only need current object context
		} // end openObject
		
		if (oName.equals("deleteObject")) {
			//delete from temp cloud
			transactionManager.removeNode(((UserTransactionInfo) cashUser.get(currentUser)).currentTransactionContext, ((UserTransactionInfo) cashUser.get(currentUser)).currentObjectContext);
			// destroy
			tmpObjectManager.deleteTmpNode(((UserTransactionInfo) cashUser.get(currentUser)).currentObjectContext);
			((UserTransactionInfo) cashUser.get(currentUser)).currentObjectContext = null;
			((UserTransactionInfo) cashUser.get(currentUser)).knownObjectContexts.remove(id);
		} // end deleteObject
		
	}
	
	private void exitTransactionContext(User currentUser,String tName, String id, String commit, String time) 
		throws TransactionHandlerException {
			
		if (tName.equals("deleteTransaction")) {
			return;
		} // end deleteTransaction

		// we're supposed to have a '((UserTransactionInfo) cashUser.get(currentUser)).currentTransactionContext', so check
		if (((UserTransactionInfo) cashUser.get(currentUser)).currentTransactionContext == null) throw 
				new TransactionHandlerException(tName + " no current transaction for " + id);
			
		if ((tName.equals("createTransaction")) || (tName.equals("openTransaction"))) {
			if (commit.equals("true")) {
				transactionManager.commit(currentUser, ((UserTransactionInfo) cashUser.get(currentUser)).currentTransactionContext);
			}
		} // end createTransaction & openTransaction
		
		if (tName.equals("commitTransaction")) {
				transactionManager.commit(currentUser, ((UserTransactionInfo) cashUser.get(currentUser)).currentTransactionContext);
		} // end commitTransaction
		
		
		//out of context now
		if (_debug) debug(" <- " + tName + " id(" + id + ") commit(" + commit + ") time(" + time + ")");
		((UserTransactionInfo) cashUser.get(currentUser)).currentTransactionContext = null;
 }
	
	private void exitObjectContext(User currentUser,String oName, String id, String type, String oMmbaseId) 
		throws TransactionHandlerException {
		
		if (oName.equals("deleteObject")) {
			return;
		} // end deleteTransaction
		
		// we're supposed to have a '((UserTransactionInfo) cashUser.get(currentUser)).currentObjectContext' for the others, so check
		if (((UserTransactionInfo) cashUser.get(currentUser)).currentObjectContext == null) throw 
				new TransactionHandlerException(oName + " no current object context for " + id);
			
		if (oName.equals("createObject")) {
		} // end createObject
		if (oName.equals("openObject")) {
		} // end openObject
		
		if (oName.equals("getObject")) {
		} // end getObject
		
		//out of context now
		if (_debug) debug("\t <- " + oName + " id(" + id + ") type(" + type + ") oMmbaseId(" + oMmbaseId + ")\n");
		((UserTransactionInfo) cashUser.get(currentUser)).currentObjectContext = null;

	}
	
	private void executeFieldContext(User currentUser,String oId, String fieldName, String fieldValue) throws
		TransactionHandlerException {

		if (_debug) debug("\t\t -X Object " + oId + ": [" + fieldName + "] set to: " + fieldValue);
		
		//check that we are inside object context
		if (((UserTransactionInfo) cashUser.get(currentUser)).currentObjectContext == null) throw
				new TransactionHandlerException(oId + " set field " + fieldName + " to " + fieldValue);
		
		tmpObjectManager.setObjectField(((UserTransactionInfo) cashUser.get(currentUser)).currentObjectContext, fieldName, fieldValue);
	}
	
	private String uniqueId() {
		return "U" + System.currentTimeMillis();
	}
	
}
