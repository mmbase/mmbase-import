/* -*- tab-width: 4; -*-
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/

package org.mmbase.module.community;

import java.util.Date;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class TimeStamp extends Date
{

    private static Logger log = Logging.getLoggerInstance(TimeStamp.class.getName()); 

	private int low = 0;
	private int high = 0;

	public TimeStamp()
	{ /* POST: Creates a TimeStamp with the current time.
	   */ 
		this(System.currentTimeMillis());
	}

	public TimeStamp(long time)
	{
		setTime(time);
		low  = (int)(time & 0xFFFFFFFFL); 
		high = (int)(time >>> 32);
	}

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


	public int lowIntegerValue()
	{
        return low;
	}

	public int highIntegerValue()
	{ 
        return high;
	}
}
