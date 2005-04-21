/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module;

import java.util.*;
import java.sql.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 */

public class MusicProgram {

	private String classname = getClass().getName();

	public Vector tracks;
	public Vector groups;
	public Vector interviews;
	public Vector nieuws;
	public Vector programs;
	public Vector episodes;
	public Vector playlists;
	public LRUHashtable cache=new LRUHashtable(10);

	public String toString()
	{
		String result = "";
			result += "tracks["+makeSet(tracks)+"], ";
			result += "groups["+makeSet(groups)+"], ";
			result += "interviews["+makeSet(interviews)+"], ";
			result += "nieuws["+makeSet(nieuws)+"], ";
			result += "programs["+makeSet(programs)+"], ";
			result += "episodes["+makeSet(episodes)+"], ";
			result += "playlists["+makeSet(playlists)+"], ";
		return result;
	}

	// ----------------------------------------------------------------------

    public static String makeSet(Vector lst) {
        return(makeSet(lst," {","",",","","} "));
    }

    public static String makeSet(Vector lst,String pre,String post) {
        return(makeSet(lst,pre,"",",","",post));
    }

    public static String makeSet(Vector lst,String pre,String preobj,String between,String postobj,String post) {
		String result = null;
		if( lst != null )
		{
   	     	 boolean first=true;
		     StringBuffer b=new StringBuffer();
   		     String obj;
   		     b.append(pre);
   		     for (Enumeration t = lst.elements();t.hasMoreElements();) {
   		         obj=(String)t.nextElement();
   		         if (obj.length()>0) {
   		             if (first) {
   		                 b.append(preobj+obj+postobj);
   		                 first=false;
   		             } else {
   		                 b.append(between+preobj+obj+postobj);
   		             }
   		         } else {
   		             System.out.println("Filter-> makeSet : Invalid filmkey");
   		         }
   		     }
   		     b.append(post);
   		     result = b.toString();
   		 }
		return result;
	}

    private void debug( String msg )
    {
        System.out.println( classname +":"+ msg );
    }

// ----------------------------------------------------------------------
}
