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
		//log.debug("TimeStamp(long): time=" + Long.toString(time,2));
		//log.debug("TimeStamp(long):  low=" + Long.toString(low,2));
		//log.debug("TimeStamp(long): high=" + Long.toString(high,2));
	}

	public TimeStamp(Integer low, Integer high)
	{ /* PRE:  Read the constructor TimeStamp(int low, int high).
	   */
		this(low.intValue(), high.intValue());
	}

	public TimeStamp(int low, int high)
	{ /* PRE:  Low has to contain the 16 least significant bits and high the 16 most significant bits of a long value.
	   *       The long value is interpeted as the milliseconds passed since January 1, 1970, 00:00:00 GMT.\
	   * POST: Take the two int values together and merge them into a long value.
	   */

		//log.debug("TimeStamp(int low, int high): high=" + high+" "+Integer.toString(high,2));
		//log.debug("TimeStamp(int low, int high):  low=" + low+" "+Integer.toString(low,2));

		long highlong = high;
		highlong <<= 32;
		//log.debug("highlong: "+Long.toString(highlong,2));
		long time;
		if (low<0) { // sign bit is up
			long lowlong = low;
			lowlong &= 0xFFFFFFFFL;
			//log.debug("low<0, lowlong after AND: "+Long.toString(lowlong,2));
			time = highlong + lowlong;
		}
		else {
			//log.debug("low > 0");
			time = highlong + low;
		}
				
		//log.debug("TimeStamp(int low, int high): time=" + Long.toString(time,2));

		setTime(time);
	}

	public int lowIntegerValue()
	{ return low;
	}

	public int highIntegerValue()
	{ return high;
	}
}
