/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
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
