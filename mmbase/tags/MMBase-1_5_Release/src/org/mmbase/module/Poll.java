/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Enumeration;
import java.sql.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.database.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


//import org.mmbase.module.SMTP.*;

/**
 * @author Arjan Houtman
 * @author Rico Jansen
 */
public class Poll extends ProcessorModule  {

    private static Logger log = Logging.getLoggerInstance(Poll.class.getName()); 

	private sessionsInterface	_sessions;
	private MMBase 	_mmbase;
	//private SMTPServerInterface _smtpserver;

	// Database-tables:
	private MMObjectBuilder		_answers;
	private MMObjectBuilder		_questions;
	private PosRel				_posrels;

	private LRUHashtable		_cache		= new LRUHashtable (53);
	private int					_saveDelay	= 0;
	private Vector				_updates	= new Vector ();
	private boolean				_saving		= false;


	public void init () {
		super.init ();

		// Get the modules and the database-tables...
		_sessions	= (sessionsInterface)getModule("SESSION");
		_mmbase		= (MMBase)getModule ("MMBASEROOT");
		_answers	= (MMObjectBuilder)_mmbase.getMMObject ("answers");
		_questions	= (MMObjectBuilder)_mmbase.getMMObject ("questions");
		_posrels	= (PosRel)_mmbase.getMMObject ("posrel");
		//_smtpserver = (SMTPServerInterface)getModule ("SMTPServer");

		//RecipientFilter recipf = (RecipientFilter)_smtpserver.getFilter ("RECIPIENT");
		//recipf.subscribe (this, RecipientFilter.EQUALS, "poll");

		//BodyFilter bodyf = (BodyFilter)_smtpserver.getFilter ("BODY");
		//bodyf.subscribe (this, BodyFilter.CONTAINS, "poll");
	}


	private String error (String msg) {
		return "Error-> " + msg;
	}



	/**
	 */
	public Poll () {
	}





	/*
	 * Test-method to receive e-mail... no real functionality yet... just to test.
	 */
	/*
    public void receivedEMail (EMail email) {
        log.debug("How nice, I received e-mail from '" + email.getHeader ("From") + "'");

		EMail reply = new EMail ();
		reply.setSenderHost ("twohigh.vpro.nl");
		reply.setReversePath ("poll@twohigh.vpro.nl");
		reply.addRecipient (email.getReversePath ());
		reply.setHeader ("From", "poll@twohigh.vpro.nl");

        if (email.isMultiPart ()) {
            log.debug("Unable to handle this email 'cause it has multiple parts... :(");

			reply.setHeader ("Subject", "Couldn't handle your E-Mail.");
			reply.setBody ("Sorry, but I was unable to handle your e-mail since it consisted of multiple parts.");
        }
        else {
			String subject = ((email.getHeader ("Subject")).trim ()).toLowerCase ();
			
			if (subject.startsWith ("vraag ")) {
				String qid = (subject.substring (6)).trim ();
	
				MMObjectNode question = getQuestion (qid);
				Vector relations = getRelations (qid);
				int totalVotes = getTotalVotes (qid);

				String body = "Vraag: " + question.getStringValue ("title") + "\n\n";

		        for (Enumeration answers = relations.elements (); answers.hasMoreElements (); ) {
       		    	MMObjectNode relNode = getRelation ((String)(answers.nextElement ()));
					String answerId=getAnswerId(relNode);

            		int votes = relNode.getIntValue ("pos");
            		MMObjectNode data = getAnswer (answerId);

					body += "Antwoord: '" + data.getStringValue ("title") + "' ("+ votes + ", " + calculateIntPercentage (votes, totalVotes, 1.0) + "%)\n";
				}

				reply.setHeader ("Subject", "Re: " + subject);
				reply.setBody (body);
			}
        }

		//if (_smtpserver == null) _smtpserver = (SMTPServerInterface)getModule ("SMTPServer");
		//_smtpserver.sendMail (reply);
    }
	*/



	/* =====================================================================================
	 * METHODS TO HANDLE LISTS:
	 * -------------------------------------------------------------------------------------
	 */

	/**
	 * <LIST [what] PROCESSOR="POLL">
	 * </LIST>
	 *
	 * [what]	-	ANSWERS-[question-id]
	 *				example: ANSWERS-903200
	 */
	 public Vector getList (scanpage sp, StringTagger tagger, String value) throws ParseException {
		Vector result = null;

		String          line = Strip.DoubleQuote (value, Strip.BOTH);
		StringTokenizer tok	 = new StringTokenizer (line, "-\n\r");
		
		if (tok.hasMoreTokens ()) {
			String cmd = tok.nextToken ();

					if (cmd.equals ("ANSWERS")) 	result = listAnswers (tok, tagger);
			else	if (cmd.equals ("ANSWERS2")) 	result = listAnswers2(tok, tagger);
			else 	log.error ("LIST could not be parsed correctly.");
		}
		return result;
	}



	private double getScale (String s) {
		if (s == null) return 1.0;

		Double d;

		try {
			d = new Double (s);
		}
		catch (NumberFormatException e) {
			// Can't change string to Double, use 1.0 as scale.
			d = new Double (1.0);
		}

		return d.doubleValue ();
	}



	/*
	 */
	private Vector listAnswers (StringTokenizer tok, StringTagger tagger) {
		if (!tok.hasMoreTokens ()) return null;

		Vector	results		= new Vector ();
		Vector	select		= tagger.Values ("SELECT");
		String	questionId	= tok.nextToken ();
		int		totalVotes	= getTotalVotes (questionId);
		Vector	relations	= getRelations2 (questionId);
		double  scale       = getScale (tagger.Value ("SCALE"));

		for (Enumeration e1 = relations.elements (); e1.hasMoreElements (); ) {
			MMObjectNode 	relNode		= getRelation ((String)(e1.nextElement ()));
			String 			answerId	= getAnswerId(relNode);
			int				votes		= relNode.getIntValue ("pos");
			MMObjectNode	data		= getAnswer (answerId);

			for (Enumeration e2 = select.elements (); e2.hasMoreElements (); ) {
				String key = (String)(e2.nextElement ());

				if      (key.equals ("votes")) results.addElement ("" + votes);
				else if (key.equals ("percentage")) results.addElement ("" + calculateIntPercentage (votes, totalVotes, 1.0));
				else if (key.equals ("scaled")) results.addElement ("" + calculateIntPercentage (votes, totalVotes, scale));
				else if (key.equals ("number") || key.equals ("otype"))	results.addElement ("" + data.getIntValue (key));
				else results.addElement (data.getStringValue (key));
			}
		}
		tagger.setValue ("ITEMS", "" + select.size ());
		return results;
	}


    private Vector listAnswers2 (StringTokenizer tok, StringTagger tagger) 
	{
        if (!tok.hasMoreTokens ()) return null;

		long	oldtime		= System.currentTimeMillis();

        Vector  results     = new Vector ();
        Vector  select      = tagger.Values ("SELECT");
        String  questionId  = tok.nextToken ();
        int     totalVotes  = getTotalVotes (questionId);
        Vector  relations   = getRelations2 (questionId);
        double  scale       = getScale (tagger.Value ("SCALE"));

		String	sort		= tagger.Value("SORT");
		if ( sort!=null && !sort.equals(""))
			sort = sort.toLowerCase();

		Votes	votesv 		= new Votes();
		Vote	vote		= null;

		// Fill vector
		// -----------

        for (Enumeration e1 = relations.elements (); e1.hasMoreElements (); ) {
            MMObjectNode    relNode     = getRelation ((String)(e1.nextElement ()));
			String 			answerId	= getAnswerId(relNode);
            int             votes       = relNode.getIntValue ("pos");
            MMObjectNode    data        = getAnswer (answerId);

			vote = new Vote();
            for (Enumeration e2 = select.elements (); e2.hasMoreElements (); ) {
				
                String key = (String)(e2.nextElement ());

                if      (key.equals ("votes")) 							vote.vote 		= votes;
                else if (key.equals ("percentage")) 					vote.percentage	= calculateIntPercentage (votes, totalVotes, 1.0);
                else if (key.equals ("scaled")) 						vote.scaled		= calculateIntPercentage (votes, totalVotes, scale);
                else if (key.equals ("number") || key.equals ("otype")) vote.number		= data.getIntValue (key);
				else if (key.equals ("title"))							vote.title		= data.getStringValue( key );
				else if (key.equals ("subtitle"))						vote.subtitle	= data.getStringValue( key );
                else 													
				{
					log.warn("listAnswers2(): While doing(" + questionId + "): This key(" + key + ") is not implemented for sorting!");
					vote.number		= data.getIntValue(key);
				}
            }
			votesv.addElement( vote );
        }

		// Sort vector
		// -----------
		
		
		Vector sorted = null;
		if( sort != null && !sort.equals(""))
		{
					if (sort.equals("number") || sort.equals("otype")) 	sorted = votesv.sortNumber();
			else	if (sort.equals("votes"))							sorted = votesv.sortVotes();
			else	if (sort.equals("percentage"))						sorted = votesv.sortPercentage();
			else	if (sort.equals("scaled"))							sorted = votesv.sortScaled();
		}
		else
			sorted = votesv.sortNumber();

		// Fill result with sorted
		// -----------------------
	
		for( Enumeration ee = sorted.elements(); ee.hasMoreElements(); )
		{
			vote = (Vote) ee.nextElement();
			for( Enumeration ee2 = select.elements(); ee2.hasMoreElements(); )
			{
                String key = (String)(ee2.nextElement ());

                if      (key.equals ("votes"))                          results.addElement( "" + vote.vote       );
                else if (key.equals ("percentage"))                     results.addElement( "" + vote.percentage );
                else if (key.equals ("scaled"))                         results.addElement( "" + vote.scaled     );
                else if (key.equals ("number") || key.equals ("otype")) results.addElement( "" + vote.number     );
                else if (key.equals ("title"))                          results.addElement( "" + vote.title      );
                else if (key.equals ("subtitle"))                       results.addElement( "" + vote.subtitle   );
                else
                {
					// not implemented key for sorting
                    results.addElement( "" + vote.number );
                }
			}	
		}

        tagger.setValue ("ITEMS", "" + select.size ());

        log.info("listAnswers2(): time("+(System.currentTimeMillis() - oldtime)+") ms. for items("+sorted.size()+")");
        return results;
    }


	/* =================================================================================
	 * METHODS TO HANDLE PROCESS COMMANDS (FORMS):
	 * ---------------------------------------------------------------------------------
	 */

	/**
	 * Execute the commands provided in the form values
	 */
	public boolean process (scanpage sp, Hashtable cmds, Hashtable vars) {

        String cmdline,token;

		for (Enumeration h = cmds.keys();h.hasMoreElements();) {
			cmdline=(String)h.nextElement();	
			StringTokenizer tok = new StringTokenizer(cmdline,"-\n\r");
			token = tok.nextToken();
			if (token.equals("VOTE")) {
				String voteFor =(String)cmds.get(cmdline);
				if (voteFor != null) processVote (sp, voteFor);
			} else log.warn("unknown command : "+cmdline);
        }

		
		return false;
	}




	/*
	 */
	private void processVote (scanpage sp, String args) {
		StringTokenizer tokens = new StringTokenizer (args, "-\n\r");

		if (tokens.hasMoreTokens ()) {
			String qid = tokens.nextToken ();
            if (log.isDebugEnabled()) {
                log.debug("qid=" + qid);
            }

			if (tokens.hasMoreTokens ()) {
				String			aid		= tokens.nextToken ();
                log.debug("aid=" + aid);
				MMObjectNode	relNode	= getRelation (qid, aid);

				if (relNode != null) {
					int votes = relNode.getIntValue ("pos");

					relNode.setValue ("pos", votes + 1);
                    relNode.commit();

					if (!_updates.contains (relNode)) _updates.addElement (relNode);

					voted (sp, qid);
				}
			}
			else {
				log.warn("PRC-CMD-VOTE needs an Answer-number to do a vote.");
			}
		}
		else {
			log.warn("PRC-CMD-VOTE needs a Question-number and an Answer-number to do a vote.");
		}
	}



	private void voted (scanpage sp, String qid) { 
		if (_sessions == null) _sessions = (sessionsInterface)getModule("SESSION");

		String			user		= sp.getSessionName ();
		sessionInfo		info		= sp.session;
		String			votedFor	= _sessions.getValue (info, "POLLVOTES");
		Hashtable		h			= makeHashtable (votedFor, ";", "=");
		String			votes		= (String)(h.get (qid));
		if (votes == null) votes = "0";

		int times = Integer.parseInt (votes) + 1;

		h.put (qid, "" + times);

		_sessions.setValue (info, "POLLVOTES", makeString (h, ";", "="));
		_sessions.saveValue(info, "POLLVOTES");
	}





	/* ===============================================================================
	 * METHODS TO HANDLE $MOD COMMANDS:
	 * -------------------------------------------------------------------------------
	 */

	/**
	 *	Handle a $MOD command
	 */
	public String replace (scanpage sp, String mod) {
		StringTokenizer tokens = new StringTokenizer (mod, "-\n\r");

		if (!tokens.hasMoreTokens ()) return error ("POLL needs a command following");

		String cmd		= tokens.nextToken ();
		String result	= "unknown command: " + cmd;

		if      (cmd.equals ("QUESTION")) result = replaceQuestion (tokens);
		else if (cmd.equals ("ANSWER"))   result = replaceAnswer (tokens);
		else if (cmd.equals ("VOTES"))    result = replaceVotes (sp, tokens);
		else if (cmd.equals ("VOTE"))
		{
			if (tokens.hasMoreTokens())
			{	
				String question = tokens.nextToken();
				String answer	= tokens.nextToken();
				result = "";
				processVote( sp, question + "-" + answer );
			}
			else
			{

			}
		} 

		return result;
	}



	/*
	 */
	private String replaceQuestion (StringTokenizer tokens) {
		if (!tokens.hasMoreTokens ()) return error ("POLL-QUESTION needs a question-number following");
		String id = tokens.nextToken ();

		MMObjectNode question = getQuestion (id);

		if (question == null) return error ("Illegal question number \"" + id + "\" following POLL-QUESTION");

		String key = (tokens.hasMoreTokens ())? tokens.nextToken () : "title";

		return question.getStringValue (key);
	}




	/*
	 */
	private String replaceAnswer (StringTokenizer tokens) {
		if (!tokens.hasMoreTokens ()) return error ("POLL-ANSWER needs an answer-number following");
		String id = tokens.nextToken ();

		MMObjectNode answer = getAnswer (id);

		if (answer == null) return error ("Illegal answer number \"" + id + "\" following POLL-ANSWER");

		String key = (tokens.hasMoreTokens ())? tokens.nextToken () : "title";

		return answer.getStringValue (key);
	}



	/*
	 */
	private String replaceVotes (scanpage sp, StringTokenizer tokens) {
		String res = "";

		if (!tokens.hasMoreTokens ()) return error ("POLL-VOTES needs at least a question-number following");
		String qid = tokens.nextToken ();

		if (!tokens.hasMoreTokens ()) {
			// No more tokens, so we want the total number of votes for this question...
			res = "" + getTotalVotes (qid);
		}
		else {
			String aid	= tokens.nextToken ();
			res			= (aid.equals ("USER"))? userNrOfVotes (sp, qid) : "" + getVotes (qid, aid);
		}

		return res;
	}



	/*
	 * Returns the number of times that this user voted for question qid.
	 */
	private String userNrOfVotes (scanpage sp, String qid) {
        if (_sessions == null) _sessions = (sessionsInterface)getModule("SESSION");

        String          user        = sp.getSessionName ();
        sessionInfo     info        = sp.session;
        String          votedFor    = _sessions.getValue (info, "POLLVOTES");
        Hashtable       h           = makeHashtable (votedFor, ";", "=");
        String          votes	    = (String)(h.get (qid));
        if (votes == null) votes = "0";

		return votes;
	}


	/* =============================================================================
	 * METHODS FOR THE ACTUAL DATA RETRIEVAL AND STORAGE:
	 * -----------------------------------------------------------------------------
	 */

	/*
	 */
	private int getTotalVotes (String questionId) {
		Vector	relations	= getRelations2 (questionId);
		int		total		= 0;

		for (Enumeration e = relations.elements (); e.hasMoreElements (); ) {
			MMObjectNode node = getRelation ((String)(e.nextElement ()));

			total += node.getIntValue ("pos");
		}

		return total;
	}




	/*
 	 */
	private int getVotes (String questionId, String answerId) {
		int				votes		= -1;
		MMObjectNode	relation	= getRelation (questionId, answerId);

		if (relation != null) votes = relation.getIntValue ("pos");

		return votes;
	}




	private Vector getRelations2(String questionId) {
		MMObjectNode question,relnode;
		Vector rtn=new Vector();
		int otype=_mmbase.getTypeDef().getIntValue("posrel");
		int snum,dnum,q;
        if (_questions==null) _questions = (MMObjectBuilder)_mmbase.getMMObject("questions");
		question=_questions.getNode(questionId);
		q=Integer.parseInt(questionId);
		if (question!=null) {
			Enumeration e=question.getRelations(otype);
			while(e.hasMoreElements()) {
				relnode=(MMObjectNode)e.nextElement();
                rtn.addElement(((Integer)relnode.getValue("number")).toString());
            }
        }
		return(rtn);
	}

    /*
     */
	private Vector getRelations (String questionId) {
		// Hopefully we can get the data from the cache...
		Vector res = (Vector)_cache.get (questionId + "_rels");

		if (res == null) {
			// Oops, the data wasn't in the cache... now we have to work...
			//log.debug("Retrieving relation-nodes of question " + questionId + " from database.");

			res					= new Vector ();
			MultiConnection	con	= _mmbase.getConnection ();

			try {
				Statement	stmt	= con.createStatement ();
				// Only select question <-> answer combos
				ResultSet rs =stmt.executeQuery("select b.* from install_questions a ,  install_posrel b , install_answers c where a.number=b.snumber AND c.number=b.dnumber AND a.number="+questionId+" UNION select b.* from install_questions a ,  install_posrel b , install_answers c where a.number=b.dnumber AND c.number=b.snumber AND a.number="+questionId); 

				while (rs.next ()) {
					res.addElement (rs.getString (1));
				}

				stmt.close ();
				con.close ();

				_cache.put (questionId + "_rels", res);
            }
            catch (SQLException e) {
                log.error("SQLException while getting answers of question " + questionId);
                log.error(Logging.stackTrace(e));
            }
        }
        else {
            //log.debug("Found relation-nodes of question " + questionId + " in cache.");
        }

        return res;
    }



	/*
	 */
	private MMObjectNode getRelation (String questionId, String answerId) {
		MMObjectNode	node	= null;
		boolean			found	= false;
		Vector			v 		= getRelations2 (questionId);

		for (Enumeration e = v.elements (); e.hasMoreElements () && !found; ) {
			String			relid	= (String)(e.nextElement ());
			MMObjectNode	relNode	= getRelation (relid);
			String 			aid	= getAnswerId(relNode);

			if (aid.equals (answerId)) {
				found	= true;
				node	= relNode;
			}
		}

		return node;
	}


    private MMObjectNode getRelation (String id) {
        if (_posrels == null) _posrels = (PosRel)_mmbase.getMMObject ("posrel");

        MMObjectNode posrelNode = (MMObjectNode)_posrels.getNode (id);

        return posrelNode;
    }

	/* Given an Answer node-nr, returns the MMObjectNode with that Answer's data.
	 * This method uses the general MMObjectNode cache.
	 */
	private MMObjectNode getAnswer (String id) {
		if (_answers == null) _answers = (MMObjectBuilder)_mmbase.getMMObject ("answers");

		MMObjectNode answernode = (MMObjectNode)_answers.getNode (id);

		return answernode;
	}

    /* Given an Question node-nr, returns the MMObjectNode with that Question's data.
     * This method uses the general MMObjectNode cache.
     */
	private MMObjectNode getQuestion (String id) {
		if (_questions == null) _questions = (Question)_mmbase.getMMObject ("questions");

		MMObjectNode questionnode = (MMObjectNode)_questions.getNode (id);

		return questionnode;
	}




	/* ============================================================================
	 * MISCELLANEOUS METHODS:
	 */

    public static int calculateIntPercentage (int votes, int total, double scale) {
		// Why return 100 on a result of 0 ?
		double perc = (total == 0) 
		            ? 100.0
		            : ((double)votes / (double)total) * 100.0;

        int res = (int)(perc * scale);

        if ((perc - (double)res) > 0.5) res++;

        return res;
    }


	/*
	 * Makes a hashtable from the given string. The string is first split into substrings at d1, and then those
	 * substring are split at d2. The left side of the d2 is the key in the hashtable, the right side of the d2
	 * is the value.
	 *
	 * Example:
	 *		String s = "NAME=Pipo;NOSECOLOR=red;SHOENSIZE=58"
	 * 		String d1 = ";"
	 *		String d2 = "=";
	 */
    public static Hashtable makeHashtable (String s, String d1, String d2) {
        Hashtable h = new Hashtable (13);

        if (s == null) return h;

        StringTokenizer tokens = new StringTokenizer (s, d1);

        while (tokens.hasMoreTokens ()) {
            String  token   = tokens.nextToken ();
            int     sep     = token.indexOf (d2);

            if (sep != -1) {
                String  key     = token.substring (0, sep);
                String  value   = token.substring (sep + 1);

                h.put (key, value);
            }
        }

        return h;
    }



	/*
	 */
    public static String makeString (Hashtable h, String d1, String d2) {
        String s = "";

        for (Enumeration e = h.keys (); e.hasMoreElements (); ) {
            String  key     = (String)e.nextElement ();
            String  value   = (String)h.get (key);

            if (s.length () > 0) s += d1;

            s += (key + d2 + value);
        }

        return s;
    }





	/**
	 */
	public String getModuleInfo () {
		return "Support routines for polls, Arjan Houtman";
	}





	/*
	 */
	private void saveUpdates () {
		//log.debug("Storing " + _updates.size () + " nodes in database.");

		while (_updates.size () > 0) {
			MMObjectNode n = (MMObjectNode)_updates.elementAt (0);

			n.commit ();

			_updates.removeElementAt (0);

			try {
				Thread.sleep (5000);
			}
			catch (InterruptedException exp) {
			}
		}

		//log.debug("Finished storing updates in database.");
	}





	/**
	 * maintainance call, will be called by the admin to perform managment
	 * tasks. This can be used instead of its own thread.
 	 */
	public void maintainance () {
		_saveDelay++;

		if (_saveDelay > 10) {
			_saveDelay = 0;

			if (!_saving) {
				_saving = true;

				saveUpdates ();

				_saving = false;
			}
        }
    }

	public String getAnswerId(MMObjectNode relNode) {
      	String answerId = "" + relNode.getIntValue ("snumber");
		int answerint=relNode.getIntValue("snumber");

		TypeDef typedef = (TypeDef)_mmbase.getMMObject ("typedef");
		MMObjectNode tmpNode=(MMObjectNode)typedef.getNode(answerint);
		if (!typedef.getValue(tmpNode.getIntValue("otype")).equals("answers")) {
        	answerId = "" + relNode.getIntValue ("dnumber");
//			System.out.println("Poll -> relnode swap "+relNode.getIntValue("number")+" answer "+answerId);
		} else {
//			System.out.println("Poll -> relnode no swap "+relNode.getIntValue("number")+" answer "+answerId);
		}
		return(answerId);
	}
}

