/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.util.Vector;
import java.util.Enumeration;
import org.mmbase.util.SortedVector;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public 	class Votes {

    private static Logger log = Logging.getLoggerInstance(Votes.class.getName()); 

	private	Vector votes;

	public Votes()
	{
		votes = new Vector();
	}

	public void addElement( Vote vote )
	{
		votes.addElement( vote );
	}

	public Enumeration elements()
	{
		return votes.elements();
	}

	public Vector sort( String what )
	{
		Vector result;

		what = what.toLowerCase().trim();

				if (what.equals("number"))		result = sortNumber();
		else	if (what.equals("otype"))		result = sortNumber();
		else	if (what.equals("votes"))		result = sortVotes();
		else	if (what.equals("percentage"))	result = sortPercentage();
		else	if (what.equals("scaled"))		result = sortPercentage();
		else
		{
			log.error("sort("+what+"): This is not a valid sort-criteria (sorting on number)");
			result = sortNumber();
		}
		return result;
	}

	public Vector sortNumber()
	{
		Vector result = null; 
		result = SortedVector.SortVector( votes, new VotesCompareNumber() );
		return result;
	}

	
	public Vector sortVotes()
	{
		Vector result = null;
		result = SortedVector.SortVector( votes, new VotesCompareVote() );
		return result;
	}

	public Vector sortPercentage()
	{
		Vector result = null;
		result = SortedVector.SortVector( votes, new VotesComparePercentage() );
		return result;
	}

	public Vector sortScaled()
	{
		Vector result = null;
		result = SortedVector.SortVector( votes, new VotesCompareScaled() );
		return result;
	}
}
