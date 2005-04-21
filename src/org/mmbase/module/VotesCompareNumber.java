package org.mmbase.module;

import org.mmbase.util.CompareInterface;

public 	class		VotesCompareNumber
		implements	CompareInterface
{
	public int compare( Object thisObject, Object otherObject )
    {
		Vote thisVote  = (Vote) thisObject;
		Vote otherVote = (Vote) otherObject;
	
        int result = 0;

        if( thisVote.number >  otherVote.number )
            result = 01;
        else
        if( thisVote.number == otherVote.number )
            result = 00;
        else
        if( thisVote.number < otherVote.number )
            result = -1;

        return result;
    }
}
