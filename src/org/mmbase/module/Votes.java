package org.mmbase.module;

import java.util.Vector;
import java.util.Enumeration;
import org.mmbase.util.SortedVector;

public 	class 		Votes
{
	private String classname = getClass().getName();
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
			debug("sort("+what+"): ERROR: This is not a valid sort-criteria (sorting on number)");
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

	public void debug( String msg )
	{
		System.out.println( classname + ":" + msg );
	}
}
