package org.mmbase.module;

import org.mmbase.util.CompareInterface;

public 	class		VotesComparePercentage
		implements	CompareInterface
{
	public int compare( Object thisObject, Object otherObject )
    {
		Vote thisVote  = (Vote) thisObject;
		Vote otherVote = (Vote) otherObject;
	
        int result = 0;

        if( thisVote.percentage >  otherVote.percentage )
            result = 01;
        else
        if( thisVote.percentage == otherVote.percentage )
            result = 00;
        else
        if( thisVote.percentage < otherVote.percentage )
            result = -1;

        return result;
    }
}
