/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module;
 

import java.util.Date;
import org.mmbase.util.Sortable;

public class PlaylistItem implements Sortable {

	private	String	classname = getClass().getName();

	public int 		id;
	public int 		score;
	public int 		oldpos;
	public int 		startdate;
	public String 	title;
	public String 	group;
	public int 		groupId;
	public int 		relid;

	public int		episode;

	// startstop
	public int		speed;
	public int		channels;
	public int		format;
	public String	url;		
	public String	starttime = null;
	public String	stoptime  = null;

	public String toString() {
		//return("ID("+id+"), episode("+episode+"), score("+score+"), oldpos("+oldpos+"), title("+title+"), relid("+relid+"), speed("+speed+"), channels("+channels+"), starttime("+starttime+"), stoptime("+stoptime+")");
		return("ID("+id+"), episode("+episode+"), title("+title+"), speed("+speed+"), channels("+channels+"), starttime("+starttime+"), stoptime("+stoptime+")");
	}

	public int compare( Sortable otherItem )

	{
		PlaylistItem other = (PlaylistItem) otherItem;
		int result = 0;

		if (this.id == other.id )
		{
			if (this.speed == other.speed )
			{
				if (this.channels == other.channels )
				{
					debug("Compare( this("+ this.toString() +", other("+other.toString()+"): ERROR: Same items in list!");
					result = 0;
				}
				else
				{
					if( this.channels > other.channels )
						result = 1;
					else
						result = -1;
				}
			}
			else
			{
				if (this.speed > other.speed )
					result = 1;
				else
					result = -1;
			}
		}
		else
			if (this.id > other.id )
				result = 1;
			else
				result = -1;
		return result;
	}

	private void debug( String msg )
	{
		System.out.println( classname + ":" + msg );
	}
}

