/*

    Copyright (C) 1996 Daniel Ockeloen , Rico Jansen

*/

package org.mmbase.module;

import java.util.*;
import nl.vpro.mmbase.module.builders.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;


/**
 * The SearchHandler handles all requests to the frontend that deal 
 * with the 'zoeken' section of the VPRO webserver.
 *
 * @author Hans Speijer
 */

public class SearchHandler implements CommandHandlerInterface {
	FrontendState state;
	MMBase mmBase;
	TeaserSearcher searcher;  
	
	public SearchHandler(MMBase mmBase, FrontendState state) {
		this.mmBase = mmBase;
		this.state = state;
		searcher = new TeaserSearcher(state,mmBase);
  	}
	
	public Vector getList(scanpage sp, StringTagger args, StringTokenizer commands) {
		String token;

		token=commands.nextToken();
		if (token.equals("GETSEARCHRESULT")) {
			return(getSearchResult(sp.getSessionName(),args));
		}
		if (token.equals("GETPROGRAMS")) {
			return(getPrograms(sp.getSessionName(),args));
		}
		if (token.equals("GETGUIDE")) {
			return(getGuide(sp.getSessionName(),args));
		}
		if (token.equals("GETSHOP")) {
			return(getShop(sp.getSessionName(),args));
		}
		return(null);
	}

	public Vector getSearchResult(String userName, StringTagger tagger) {
		Vector result = new Vector();
	    Vector nodes; 
		MMObjectNode node;
		String identifier;
		Enumeration enum;
		Object item;
		int pos = 0;
		int batch = 6;
		FrontendUserState userState = state.getUserState(userName);
 
   		identifier = Strip.DoubleQuote((String)tagger.get("RESULT"),Strip.BOTH);
		nodes = userState.getSearchResult().getResult(identifier);
		try {
			pos = Integer.parseInt((String)tagger.get("POS"));
			batch = Integer.parseInt((String)tagger.get("BATCH"));
		}
		catch (NumberFormatException e) {pos =0; batch=10;}

		if(nodes != null) {
			for(int index = pos; /*(index < pos+batch) &&*/ (index < nodes.size());index++) {
				node = (MMObjectNode)nodes.elementAt(index);
				
				item = node.getValue("title");
				if (item == null) item = "";
				result.addElement(item);
				
				item = node.getValue("url");
				if (item == null) item = "";
				result.addElement(item);
				
				item = node.getValue("body");
				if (item == null) item = "";
				result.addElement(item);
				
				item = node.getValue("handle");
				if (item == null) item = "";
				result.addElement(item);

				item = node.getValue("totype");
				if (item == null) item = "";
				result.addElement(item);

				switch (((Integer)item).intValue()) {
				case 5:
					result.addElement("000033");
					break;
				case 6:
					result.addElement("000066");
					break;
				case 2:
				case 3:
				case 4:
					result.addElement("000099");
					break;
				case 1:
					result.addElement("0000cc");
					break;
				default: 
					result.addElement("0000ff");
				}

			   
			userState.currentListPos = index + 1;

			}

		}
	
		tagger.setValue("ITEMS","6");
	  	
		return (result);
	} 

	public Vector getPrograms(String userName, StringTagger tagger) {
		Vector result = new Vector();
		Programs programBuilder = null;
	    SortedVector nodes;
		Object item; 
		MMObjectNode node;
		Enumeration enum;
		int medium;

		try {
			medium = Integer.parseInt((String)tagger.get("MEDIUM"));
		}
		catch (NumberFormatException e) {medium = 1;}

		if (programBuilder == null) 
			programBuilder = (Programs)mmBase.getMMObject("programs");   

		nodes = programBuilder.getPrograms(medium);
		if (nodes != null) {
			nodes.setCompare(new MMObjectCompare("title"));
			enum = nodes.elements();
			
			while(enum.hasMoreElements()) {
				node = (MMObjectNode)enum.nextElement();
				
				item = node.getValue("title");
				if (item == null) item = "";
				result.addElement(item);
				
				item = node.getValue("url");
				if (item == null) item = "";
				result.addElement(item);
				
				item = node.getValue("body");
				if (item == null) item = "";
				result.addElement(item);
			}
		}

		tagger.setValue("ITEMS","3");
			
		return (result);
	}

	public Vector getGuide(String userName, StringTagger tagger) {
		Vector result = new Vector();

		return result;		
	}

	public Vector getShop(String userName, StringTagger tagger) {
		Vector result = new Vector();
	    Vector nodes; 
		MMObjectNode node;
		String identifier;
		Enumeration enum;
		Object item;
		int pos = 0;
		int batch = 6;
		FrontendUserState userState = state.getUserState(userName);
 

   		identifier = Strip.DoubleQuote((String)tagger.get("TYPE"),Strip.BOTH);

		nodes = searcher.getShopTeasers(identifier);

		if(nodes != null) {
			for(int index = pos; /*(index < pos+batch) &&*/ (index < nodes.size());index++) {
				node = (MMObjectNode)nodes.elementAt(index);
				
				item = node.getValue("title");
				if (item == null) item = "";
				result.addElement(item);
				
				item = node.getValue("url");
				if (item == null) item = "";
				result.addElement(item);
				
				item = node.getValue("body");
				if (item == null) item = "";
				result.addElement(item);
				
				item = node.getValue("handle");
				if (item == null) item = "";
				result.addElement(item);
			}

		}
	
		tagger.setValue("ITEMS","4");
	  	
		return (result);
	}

	/**
	 * Replace/Trigger commands
	 */
	public String replace(scanpage sp, StringTokenizer commands) {
		String token;
		String user = sp.getSessionName();
		FrontendUserState userState = state.getUserState(user);

		if (commands.hasMoreTokens()) {
			token = commands.nextToken();
			if (token.equals("RESULTCOUNT")) {
				return (getResultCount(userState, commands));
			}
			if (token.equals("QUERY")) {
				return (getQuery(userState));
			}
		}
 

		return "Command not defined (SearchHandler)";
	}
	
	public String getResultCount(FrontendUserState state, StringTokenizer commands) {
		String token;
		FrontendSearchResult result = state.getSearchResult();
		Vector resultVector;

		if (commands.hasMoreTokens()) {
			token = commands.nextToken();
			resultVector = result.getResult(token);
			if (resultVector != null)
				return (""+resultVector.size());
		}

		return ("0");
	}
	
	public String getQuery(FrontendUserState state) {
		FrontendSearchResult searchResult = state.getSearchResult();
		
		if (searchResult != null){
			return (searchResult.getQuery());
		}
		else return ("");   
	}

	/**
	 * The hook that passes all form related requests to the correct handler
	 */
	public boolean process(scanpage sp, StringTokenizer command, Hashtable cmds, Hashtable vars) {
		String token;

		token=command.nextToken();
		if (token.equals("QUERY")) {
			return(processQuery(sp, vars));
			}

		return (false);
	}

	boolean processQuery(scanpage sp,  Hashtable vars) {
		
		searcher.search((String)vars.get("QUERY"), sp.getSessionName());

		return (true);
	}
	 
}

