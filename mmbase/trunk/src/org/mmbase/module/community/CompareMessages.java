package org.mmbase.module.community;
import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import java.util.Vector;

/**
 * @author Dirk-Jan Hoekstra
 * @version 5 Jan 2001
 *
 * CompareMessages implemtnes the CompareInterface used by SortedVector.
 * At forhand you've to specifie on which fields the message nodes should be compared,
 * these fields may not have a null value.
 */
public class CompareMessages implements CompareInterface
{ 
	private Vector fields;
	private Vector sortDirs;

	public CompareMessages(Vector fields)
	{	this.fields = fields;
		sortDirs = new Vector(fields.size());
		for (int i = 0; i < fields.size(); i++)	sortDirs.add((String)"UP");
	}

	/**
	 * Fields are the fields on which the message nodes get compared.
	 * Use UP and DOWN in the sortDirs vector to specifie the sort directions.
	 */
	public CompareMessages(Vector fields, Vector sortDirs)
	{	this.fields = fields;
		this.sortDirs = sortDirs;
		for (int i = sortDirs.size(); i < fields.size(); i++) sortDirs.add((String)"UP");
	}

	/**
	 * The two message nodes will be compared using the compare function of the values out of the fields.
	 * Only String and Integer values can be used, in other cases it's assumed that the values are equal.
	 */
	public int compare(Object thisone, Object other)
	{	MMObjectNode n1 = (MMObjectNode)thisone;
		MMObjectNode n2 = (MMObjectNode)other;
		Object o1, o2;
		int result;
		int fieldnr = 0;
		do
		{	o1 = n1.getValue((String)fields.elementAt(fieldnr));
			o2 = n2.getValue((String)fields.elementAt(fieldnr));
			if (o1 instanceof Integer)
			{	if (((String)sortDirs.elementAt(fieldnr)).equals("UP"))
					result = (((Integer)o1).compareTo((Integer)o2));
				else
					result = -(((Integer)o1).compareTo((Integer)o2));
			} else
			if (o1 instanceof String)
			{	if (((String)sortDirs.elementAt(fieldnr)).equals("UP"))
					result = (((String)o1).compareToIgnoreCase((String)o2));
				 else
					result = -(((String)o1).compareToIgnoreCase((String)o2));
			}
			else
				result = 0;
			fieldnr++;
		} while ((result == 0) && (fieldnr < fields.size()));
		return result;
	}
}
