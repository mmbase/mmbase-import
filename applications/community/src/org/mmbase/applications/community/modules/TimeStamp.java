package org.mmbase.module.community;

import java.util.Date;

public class TimeStamp extends Date
{
	private String classname = getClass().getName();
	private int low = 0;
	private int high = 0;

	protected void debug(String msg)
	{ /* PRE:  A meaningfull debugmessage.
	   * POST: Writes the name of the class generating the message and the debug message to System.out.
	   */
		System.out.println(classname + ":" + msg);
	}

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
		//debug("TimeStamp(long): time=" + Long.toString(time,2));
		//debug("TimeStamp(long):  low=" + Long.toString(low,2));
		//debug("TimeStamp(long): high=" + Long.toString(high,2));
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

		//debug("TimeStamp(int low, int high): high=" + high+" "+Integer.toString(high,2));
		//debug("TimeStamp(int low, int high):  low=" + low+" "+Integer.toString(low,2));

		long highlong = high;
		highlong <<= 32;
		//debug("highlong: "+Long.toString(highlong,2));
		long time;
		if (low<0) { // sign bit is up
			long lowlong = low;
			lowlong &= 0xFFFFFFFFL;
			//debug("low<0, lowlong after AND: "+Long.toString(lowlong,2));
			time = highlong + lowlong;
		}
		else {
			//debug("low > 0");
			time = highlong + low;
		}
				
		//debug("TimeStamp(int low, int high): time=" + Long.toString(time,2));

		setTime(time);
	}

	public int lowIntegerValue()
	{ return low;
	}

	public int highIntegerValue()
	{ return high;
	}
}
