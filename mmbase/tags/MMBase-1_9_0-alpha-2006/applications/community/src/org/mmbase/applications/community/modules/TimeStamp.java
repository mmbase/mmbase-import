/* -*- tab-width: 4; -*-
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/

package org.mmbase.applications.community.modules;

import java.util.Date;

/**
 * Creates a timestamp value out of two integer values.
 * Supposedly needed because the Informix setup had no configuration
 * for long values (in which the timestamps are expressed), which
 * means they need be store as two integers instead.
 * @deprecated Do not use this class. Store timestamps as Long or Date instead.
 *
 * @author Dirk-Jan Hoekstra
 * @author Pierre van Rooden
 * @version $Id: TimeStamp.java,v 1.8 2005-01-30 16:46:35 nico Exp $
 */

public class TimeStamp extends Date
{

    /**
     * the 16 least significant bits of the timestamp value
     */
    private int low = 0;
    /**
    * the 16 most significant bits of the timestamp value
    */
	private int high = 0;

    /**
    * Creates a TimeStamp based on the current time.
    */
    public TimeStamp()
	{ /* POST: Creates a TimeStamp with the current time.
	   */ 
		this(System.currentTimeMillis());
	}

    /**
    * Creates a TimeStamp based on a specified time.
    * @param time the time in milliseconds since 1/1/1970
    */
	public TimeStamp(long time)
	{
		setTime(time);
		low  = (int)(time & 0xFFFFFFFFL); 
		high = (int)(time >>> 32);
	}

     /**
     * Creates a TimeStamp based on a specified time.
     * @param low the 16 least significant bits of a time value (a long
     *      representing milliseconds since 1/1/1970
     * @param high the 16 most significant bits of the time value
     */
	public TimeStamp(Integer low, Integer high)
	{
		this(); // Create this with currenttime
		if ((low!=null) && (high!=null))
			setTimeLowHigh(low.intValue(), high.intValue());
	}

	public TimeStamp(int low, int high)
	{
		setTimeLowHigh(low, high);
	}
	
    /**
    * Creates a TimeStamp based on a specified time.
    * @param low the 16 least significant bits of a time value (a long
    *      representing milliseconds since 1/1/1970
    * @param high the 16 most significant bits of the time value
    */
	private void setTimeLowHigh(int low, int high) 
	{ /* PRE:  Low has to contain the 16 least significant bits and high the 16 most significant bits of a long value.
	   *       The long value is interpeted as the milliseconds passed since January 1, 1970, 00:00:00 GMT.\
	   * POST: Take the two int values together and merge them into a long value.
	   */

		long highlong = high;
		highlong <<= 32;
		long time;
		if (low<0) { // sign bit is up
			long lowlong = low;
			lowlong &= 0xFFFFFFFFL;
			time = highlong + lowlong;
		}
		else {
			time = highlong + low;
		}
		setTime(time);
	}

    /**
    * Retrieve the 16 least significant bits of a time value (a long
    * representing milliseconds since 1/1/1970.
    */
	public int lowIntegerValue()
	{
        return low;
	}

    /**
    * Retrieve the 16 most significant bits of a time value (a long
    * representing milliseconds since 1/1/1970.
    */
	public int highIntegerValue()
	{ 
        return high;
	}
}
