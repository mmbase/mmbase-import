/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module;

import java.util.*;
import java.util.Date;
import java.io.*;
import java.sql.*;

import javax.servlet.http.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;
import org.mmbase.util.*;
	

/**
 * @author Daniel Ockeloen
 * @author Rob Vermeulen
 * @version 29 Feb 1998
 */
public class Music extends ProcessorModule implements MusicInterface {
	private String classname = getClass().getName();

    //private MMBaseInterface mmbase;
	private MMBase mmbase;
	private LRUHashtable sqlcache = new LRUHashtable(20);
	private Hashtable programs = new Hashtable(10);

	public void init() {
    		mmbase=(MMBase)getModule("MMBASEROOT");
	}
	
	public void reload() {
			init();
	}

	public void onload() {
	}

	public void unload() {
	}

	public void shutdown() {
	}
 
	public String replace(HttpServletRequest req, String cmds) {
		StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			//if (cmd.equals("EXTRACT")) return(getVars(req,tok.nextToken()));
		}
		return("No command defined");
	}

	 public Vector getList(HttpServletRequest requestInfo,StringTagger tagger, String value) {
			//System.out.println("Playlist->"+tagger);
			//System.out.println("Playlist->"+value);
    		String line = Strip.DoubleQuote(value,Strip.BOTH);

		StringTokenizer tok = new StringTokenizer(line,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			if (cmd.equals("BANDLIST")) return(doBandList(tok,tagger));
			if (cmd.equals("TRACKLIST")) return(doTrackList(tok,tagger));
			if (cmd.equals("OBJECTS")) return(doObjects(requestInfo,tagger));
			if (cmd.equals("OBJECTSSIZE")) return(doObjectsSize(requestInfo,tagger));
		}
		return(null);
	}

	/**
	 * show Objects
	 */
	public Vector doObjectsSize(HttpServletRequest req, StringTagger tagger) {
		doObjects(req,tagger);
		String type=tagger.Value("TYPE");
		String where=tagger.Value("WHERE");
		String prg=tagger.Value("PROGRAM");
		Vector prgs=tagger.Values("PROGRAM");
		System.out.println("Music -> prgs="+prg);
		String fields=tagger.ValuesString("FIELDS");
		// get the contains list for this type and prg
		Vector checkv=null;
		if (prgs!=null && prgs.size()>1) {
		 checkv=getAreaVector(prgs,type);
		} else {
		 checkv=getAreaVector(prg,type);
		}
		MusicProgram p=null;
		if (prgs.size()>1) {
			p=(MusicProgram)programs.get(prgs);
		} else {
			p=(MusicProgram)programs.get(prg);
		}
		MusicSearchCache res=(MusicSearchCache)p.cache.get(fields+" "+where);
		if (res!=null) {
			Vector results=new Vector();
			results.addElement(""+res.size);
			results.addElement("0");
			return(results);
		} 
		return(new Vector());	 
	}

	/**
	 * show Objects
	 */
	public Vector doObjects2(HttpServletRequest req, StringTagger tagger) {
		Object tmp;
		String result=null;
		MMObjectNode node;
		int i=0;
		Vector results=new Vector(); 
		String type=tagger.Value("TYPE");
		String where=tagger.Value("WHERE");
		String dbsort=tagger.Value("DBSORT");
		String dbdir=tagger.Value("DBDIR");
		String prg=tagger.Value("PROGRAM");
		Vector prgs=tagger.Values("PROGRAM");
		String fields=tagger.ValuesString("FIELDS");
		String age=tagger.Value("AGE");
		System.out.println("Music -> AGE="+age);
		int agenr=1000;
		int agecount=1;
		if (age!=null) {
			try {
				agenr=Integer.parseInt(age);
				agecount=((DayMarkers)mmbase.getMMObject("daymarks")).getDayCountAge(agenr);
			} catch (Exception e) {
				System.out.println("MUSIC-> Not a valid AGE");
			}
		}
		System.out.println("Music -> REROUTED CALL");


		Vector checkv=null;
		if (prgs!=null && prgs.size()>1) {
		 checkv=getAreaVector(prgs,type);
		} else {
		 checkv=getAreaVector(prg,type);
		}

		// get the contains list for this type and prg
		MusicProgram p=null;
		if (prgs!=null && prgs.size()>1) {
			p=(MusicProgram)programs.get(prgs);
		} else {
			p=(MusicProgram)programs.get(prg);
		}


		MusicSearchCache res=(MusicSearchCache)p.cache.get(fields+" "+where);
		if (res!=null) {
			tagger.setValue("ITEMS",""+res.rows);
			System.out.println("Music -> FROM CACHE");
			return(res.results);
		} else {
		res=new MusicSearchCache();

		
		MMObjectBuilder bul=mmbase.getMMObject(type);
		long begin=(long)System.currentTimeMillis();
		Enumeration e=null;
		if (dbsort==null) {
			e=bul.search(where);
		} else {
			if (dbdir==null) {
				e=bul.search(where,dbsort);
			} else {
				if (dbdir.equals("DOWN")) {
					e=bul.search(where,dbsort,false);
				} else {
					e=bul.search(where,dbsort,true);
				}
			}
		}


		for (;e.hasMoreElements();) {
			node=(MMObjectNode)e.nextElement();
			int id=node.getIntValue("number");
			if (checkv.contains(""+id) && id>agecount) {
				i++;
				Enumeration f=tagger.Values("FIELDS").elements();
				for (;f.hasMoreElements();) {
					// hack hack this is way silly Strip needs to be fixed
					String fieldname=Strip.DoubleQuote((String)f.nextElement(),Strip.BOTH);
					results.addElement( node.getValueAsString( fieldname ) );
				}
			}
		}
		}
		tagger.setValue("ITEMS",""+tagger.Values("FIELDS").size());
		long end=(long)System.currentTimeMillis();
		//System.out.println("doObject ("+type+")="+(end-begin)+" ms");
		res.results=results;	
		res.size=i;	
		res.rows=tagger.Values("FIELDS").size();
		p.cache.put(fields+" "+where,res);
		return(results);
	}


	/**
	 * show Objects
	 */
	public Vector doObjects(HttpServletRequest req, StringTagger tagger) {
		Object tmp;
		MMObjectNode node;
		int i=0;
		Vector results=new Vector(); 
		String type=tagger.Value("TYPE");
		String where=tagger.Value("WHERE");
		
		// where escape 
		//where=Escape.singlequote(where);

		String dbsort=tagger.Value("DBSORT");
		String dbdir=tagger.Value("DBDIR");
		String prg=tagger.Value("PROGRAM");
		Vector prgs=tagger.Values("PROGRAM");
		String fields=tagger.ValuesString("FIELDS");
		String age=tagger.Value("AGE");
		System.out.println("Music -> AGE="+age);
		int agenr=1000;
		int agecount=1;		
		if (age!=null) {
			try {
				agenr=Integer.parseInt(age);
				agecount=((DayMarkers)mmbase.getMMObject("daymarks")).getDayCountAge(agenr);
				System.out.println("Music -> AGECOUNT="+agecount);
			} catch (Exception e) {
				System.out.println("MUSIC-> Not a valid AGE");
			}
		}
		Vector checkv=null;
		if (prgs!=null && prgs.size()>1) {
		 checkv=getAreaVector(prgs,type);
		} else {
		 checkv=getAreaVector(prg,type);
		}

		// get the contains list for this type and prg
		MusicProgram p=null;
		if (prgs!=null && prgs.size()>1) {
			p=(MusicProgram)programs.get(prgs);
		} else {
			p=(MusicProgram)programs.get(prg);
		}


		MusicSearchCache res=(MusicSearchCache)p.cache.get(fields+" "+where);
		if (res!=null) {
			tagger.setValue("ITEMS",""+res.rows);
			System.out.println("Music -> FROM CACHE");
			return(res.results);
		} else {
		res=new MusicSearchCache();

		
		MMObjectBuilder bul=mmbase.getMMObject(type);
		long begin=(long)System.currentTimeMillis();
		Enumeration e=null;
		if (dbsort==null) {
			System.out.println("Music -> XXXXXXXXXXXX");
			e=bul.searchIn(where,toStr(checkv));
		} else {
			if (dbdir==null) {
				e=bul.searchIn(where,dbsort,toStr(checkv));
			} else {
				if (dbdir.equals("DOWN")) {
					e=bul.searchIn(where,dbsort,false,toStr(checkv));
				} else {
					e=bul.searchIn(where,dbsort,true,toStr(checkv));
				}
			}
		}


		for (;e.hasMoreElements();) {
			node=(MMObjectNode)e.nextElement();
			int id=node.getIntValue("number");
			if (id>agecount) {
				i++;
				Enumeration f=tagger.Values("FIELDS").elements();
				for (;f.hasMoreElements();) {
					// hack hack this is way silly Strip needs to be fixed
					String fieldname=Strip.DoubleQuote((String)f.nextElement(),Strip.BOTH);
					results.addElement( node.getValueAsString( fieldname ) );
				}
			}
		}
		}
		tagger.setValue("ITEMS",""+tagger.Values("FIELDS").size());
		long end=(long)System.currentTimeMillis();
		//System.out.println("doObject ("+type+")="+(end-begin)+" ms");
		res.results=results;	
		res.size=i;	
		res.rows=tagger.Values("FIELDS").size();
		p.cache.put(fields+" "+where,res);
		return(results);
	}

	/**
	* obtain the Vector for the fromtend command of 'PLAYLIST'
	*/
	public Vector doBandList(StringTokenizer tok,StringTagger tagger) {
		Vector results;
		Vector bannum=new Vector();
		String prg=tagger.Value("PROGRAM");
		results=(Vector)sqlcache.get("BANDS-"+prg);
		if (results!=null) {
			System.out.println("Music -> Cached bands +"+prg);
		} else {	
			System.out.println("Music -> NOT Cached bands +"+prg);
		results=new Vector();
		MultiConnection con=mmbase.getConnection();
		try {
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select a.number,a.name from vpro4_groups a where a.number in (select b.snumber from vpro4_insRel b where b.dnumber="+prg+") union select a.number,a.name from vpro4_groups a where a.number in (select b.dnumber from vpro4_insRel b where b.snumber="+prg+") ORDER BY 2");
			while(rs.next()) {
				String tmp=rs.getString(2);
				if (tmp.length()>18) tmp=tmp.substring(0,16)+"...";
				results.addElement(rs.getString(1));
				results.addElement(tmp);
				bannum.addElement(rs.getString(1));
			}	
			stmt.close();
			con.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
			sqlcache.put("BANDS-"+prg,results);
		// get the program cache node
		MusicProgram p= (MusicProgram)programs.get(prg);
		if (p==null) {
			p = new MusicProgram();
			programs.put(prg,p);
		}
		p.groups=bannum;	
		}
		tagger.setValue("ITEMS","2");
		return (results);
	}


	/**
	* obtain the Vector for the fromtend command of 'PLAYLIST'
	*/
	public Vector doTrackList(StringTokenizer tok,StringTagger tagger) {
		Vector results;
		String prg=tagger.Value("PROGRAM");
		results=(Vector)sqlcache.get("TRACKS-"+prg);
		if (results!=null) {
			System.out.println("Music -> Cached tracks +"+prg);
		} else {	
			System.out.println("Music -> NOT Cached tracks +"+prg);
		results=new Vector();	
		Vector trknum=new Vector();
		String groups="";
		MultiConnection con=mmbase.getConnection();
		try {
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select a.number,a.name from vpro4_groups a where a.number in (select b.snumber from vpro4_insRel b where b.dnumber="+prg+") union select a.number,a.name from vpro4_groups a where a.number in (select b.dnumber from vpro4_insRel b where b.snumber="+prg+") ORDER BY 2");
			while(rs.next()) {
				String tmp=rs.getString(1);
				if (groups.equals("")) {
					groups+=tmp;
				} else {
					groups+=","+tmp;
				}
			}	
			stmt.close();
			con.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}

		con=mmbase.getConnection();
		try {
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select a.number,a.title from vpro4_cdtracks a where a.number in (select snumber from vpro4_funrel where dnumber in ("+groups+")) union select a.number,a.title from vpro4_cdtracks a where a.number in (select dnumber from vpro4_funrel where snumber in ("+groups+")) ORDER BY 2");
			while(rs.next()) {
				String tmp=rs.getString(2);
				if (tmp.length()>18) tmp=tmp.substring(0,16)+"...";
				results.addElement(rs.getString(1));
				results.addElement(tmp);
				
				trknum.addElement(rs.getString(1));
			}	
			stmt.close();
			con.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		sqlcache.put("Tracks-"+prg,results);
		// get the program cache node
		MusicProgram p= (MusicProgram)programs.get(prg);
		if (p==null) {
			p = new MusicProgram();
			programs.put(prg,p);
		}
		p.tracks=trknum;	
		}
		tagger.setValue("ITEMS","2");
		return (results);
	}


	public boolean process(HttpServletRequest req, Hashtable cmds,Hashtable vars, HttpPost poster) {
		return(true);
	}

	public Vector getAreaVector(String prg,String type) {
		Vector results=new Vector();

		if (type.equals("cdtracks")) results=getTracksVector(prg);
		if (type.equals("groups")) results=getGroupsVector(prg);
		if (type.equals("interviews")) results=getInterviewsVector(prg);
		if (type.equals("news")) results=getNieuwsVector(prg);
		if (type.equals("programs")) results=getProgramsVector(prg);
		if (type.equals("episodes")) results=getEpisodesVector(prg);
		if (type.equals("playlsts")) results=getPlaylstsVector(prg);

		if( results == null )
			debug("getAreaVector("+prg+","+type+") (String): ERROR: result is null!");
		else
			if( results.size() == 0 )
				debug("getAreaVector("+prg+","+type+") (String): ERROR: result is empty!");

		return(results);	
	}

	public Vector getAreaVector(Vector prgs,String type) {
		Vector results=new Vector();
		Vector res=new Vector();
		MusicProgram p=null;

		if (type.equals("cdtracks")) {
			p=(MusicProgram)programs.get(prgs);
			if (p==null) {
				//debug("getAreaVector("+makeSet(prgs)+","+type+"): programs is null, getting new MusicProgram!");
				p=new MusicProgram();
		  	    programs.put(prgs,p);
			} else {
				//debug("getAreaVector("+makeSet(prgs)+","+type+"): got programs("+p.toString()+")");
				//p.tracks=results;
				results=p.tracks; // fix daniel

				checkVector( prgs, type, results );
				return(results);
			}
		} else if (type.equals("groups")) {
			p=(MusicProgram)programs.get(prgs);
			if (p==null) {
				p=new MusicProgram();
		  	    programs.put(prgs,p);
			} else {
				// p.groups=results;
				results=p.groups; // fix daniel

				checkVector( prgs, type, results );
				return(results);
			}
		} else if (type.equals("interviews")) {
			p=(MusicProgram)programs.get(prgs);
			if (p==null) {
				p=new MusicProgram();
		  	    programs.put(prgs,p);
			} else {
				// p.interviews=results;
				results=p.interviews; // fix daniel

				checkVector( prgs, type, results );
				return(results);
			}
		} else if (type.equals("news")) {
			p=(MusicProgram)programs.get(prgs);
			if (p==null) {
				p=new MusicProgram();
		  	    programs.put(prgs,p);
			} else {
				//p.nieuws=results;
				results=p.nieuws; // fix daniel
				checkVector( prgs, type, results );
				return(results);
			}
		} else if (type.equals("programs")) {
			p=(MusicProgram)programs.get(prgs);
			if (p==null) {
				p=new MusicProgram();
		  	    programs.put(prgs,p);
			} else {
				//p.programs=results;
				results=p.programs; // fix daniel
				checkVector( prgs, type, results );
				return(results);
			}
		} else if (type.equals("episodes")) {
			p=(MusicProgram)programs.get(prgs);
			if (p==null) {
				p=new MusicProgram();
		  	    programs.put(prgs,p);
			} else {
				//p.episodes=results;
				results=p.episodes; // fix daniel

				checkVector( prgs, type, results );
				return(results);
			}
		} else if (type.equals("playlsts")) {
			p=(MusicProgram)programs.get(prgs);
			if (p==null) {
				p=new MusicProgram();
		  	    programs.put(prgs,p);
			} else {
				//p.playlists=results;
				results=p.playlists; // fix daniel

				checkVector( prgs, type, results );
				return(results);
			}
		}

		Enumeration f=prgs.elements();
		for (;f.hasMoreElements();) {
			String prg=(String)f.nextElement();
    		prg = Strip.DoubleQuote(prg,Strip.BOTH);
			if (type.equals("cdtracks")) res=getTracksVector(prg);
			if (type.equals("groups"))  res=getGroupsVector(prg);
			if (type.equals("interviews"))  res=getInterviewsVector(prg);
			if (type.equals("news"))  res=getNieuwsVector(prg);
			if (type.equals("programs"))  res=getProgramsVector(prg);
			if (type.equals("episodes"))  res=getEpisodesVector(prg);
			if (type.equals("playlsts"))  res=getPlaylstsVector(prg);
			if (res!=null) {
				Enumeration h=res.elements();
				for (;h.hasMoreElements();) {
					prg=(String)h.nextElement();
					results.addElement(prg);
				}
			}
		}
		if (type.equals("cdtracks")) {
			p.tracks=results;
		} else if (type.equals("groups")) {
			p.groups=results;
		} else if (type.equals("interviews")) {
			p.interviews=results;
		} else if (type.equals("news")) {
			p.nieuws=results;
		} else if (type.equals("programs")) {
			p.programs=results;
		} else if (type.equals("episodes")) {
			p.episodes=results;
		} else if (type.equals("playlsts")) {
			p.playlists=results;
		}

		checkVector( prgs, type, results );
		//debug("getAreaVector("+makeSet(prgs)+","+type+"): result("+results+"), musicProgram("+p.toString()+")");
		return(results);	
	}

	private void checkVector( Vector prgs, String type, Vector results )
	{	
		if ( results == null )
			debug("getAreaVector("+makeSet(prgs)+","+type+") (vector): ERROR: results is null!");		
		else
			if (results.size() == 0)
				debug("getAreaVector("+makeSet(prgs)+","+type+") (vector): ERROR: results is empty!");
	}

	public Vector getGroupsVector(String prg) {
		MusicProgram p=(MusicProgram)programs.get(prg);
		if (p==null) {
			p=new MusicProgram();
			programs.put(prg,p);
		}
		Vector results=p.groups;
		if (results==null) {
		results=new Vector();
		MultiConnection con=mmbase.getConnection();
		try {
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select a.number,a.name from vpro4_groups a where a.number in (select b.snumber from vpro4_insRel b where b.dnumber="+prg+") union select a.number,a.name from vpro4_groups a where a.number in (select b.dnumber from vpro4_insRel b where b.snumber="+prg+") ORDER BY 2");
			while(rs.next()) {
				results.addElement(rs.getString(1));
			}	
			stmt.close();
			con.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		p.groups=results;
		}
		return(results);
	}

	public Vector getTracksVector(String prg) {
		MusicProgram p=(MusicProgram)programs.get(prg);
		if (p==null) {
			p=new MusicProgram();
			programs.put(prg,p);
		}
		Vector results=p.tracks;
		if (results==null) {

		results=new Vector();
		String groups="";
		MultiConnection con=mmbase.getConnection();
		try {
			Statement stmt=con.createStatement();
//			System.out.println("select a.number,a.name from vpro4_groups a where a.number in (select b.snumber from vpro4_insRel b where b.dnumber="+prg+") union select a.number,a.name from vpro4_groups a where a.number in (select b.dnumber from vpro4_insRel b where b.snumber="+prg+") ORDER BY 2");
			ResultSet rs=stmt.executeQuery("select a.number,a.name from vpro4_groups a where a.number in (select b.snumber from vpro4_insRel b where b.dnumber="+prg+") union select a.number,a.name from vpro4_groups a where a.number in (select b.dnumber from vpro4_insRel b where b.snumber="+prg+") ORDER BY 2");
			while(rs.next()) {
				String tmp=rs.getString(1);
				if (groups.equals("")) {
					groups+=tmp;
				} else {
					groups+=","+tmp;
				}
			}	
			stmt.close();
			con.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (groups.equals("")) return(new Vector()); // new new
		con=mmbase.getConnection();
		try {
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select a.number,a.title from vpro4_cdtracks a where a.number in (select snumber from vpro4_funrel where dnumber in ("+groups+")) union select a.number,a.title from vpro4_cdtracks a where a.number in (select dnumber from vpro4_funrel where snumber in ("+groups+")) ORDER BY 2");
			while(rs.next()) {
				results.addElement(rs.getString(1));
			}	
			stmt.close();
			con.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		p.tracks=results;	
		}

		return(results);
	}


	public Vector getInterviewsVector(String prg) {
		MusicProgram p=(MusicProgram)programs.get(prg);
		if (p==null) {
			p=new MusicProgram();
			programs.put(prg,p);
		}
		Vector results=p.interviews;
		if (results==null) {

		results=new Vector();
		String groups="";
		MultiConnection con=mmbase.getConnection();
		try {
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select a.number,a.name from vpro4_groups a where a.number in (select b.snumber from vpro4_insrel b where b.dnumber="+prg+") union select a.number,a.name from vpro4_groups a where a.number in (select b.dnumber from vpro4_insrel b where b.snumber="+prg+") ORDER BY 2");
			while(rs.next()) {
				String tmp=rs.getString(1);
				if (groups.equals("")) {
					groups+=tmp;
				} else {
					groups+=","+tmp;
				}
			}	
			stmt.close();
			con.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (groups.equals("")) return(new Vector()); // new new
		con=mmbase.getConnection();
		try {
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select a.number,a.title from vpro4_interviews a where a.number in (select snumber from vpro4_insrel where dnumber in ("+groups+")) union select a.number,a.title from vpro4_interviews a where a.number in (select dnumber from vpro4_insrel where snumber in ("+groups+")) ORDER BY 2");
			while(rs.next()) {
				results.addElement(rs.getString(1));
			}	
			stmt.close();
			con.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		p.interviews=results;	
		}

		return(results);
	}


	public Vector getNieuwsVector(String prg) {
		MusicProgram p=(MusicProgram)programs.get(prg);
		if (p==null) {
			p=new MusicProgram();
			programs.put(prg,p);
		}
		Vector results=p.nieuws;
		if (results==null) {

		results=new Vector();
		String groups="";
		MultiConnection con=mmbase.getConnection();
		try {
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select a.number,a.name from vpro4_groups a where a.number in (select b.snumber from vpro4_insrel b where b.dnumber="+prg+") union select a.number,a.name from vpro4_groups a where a.number in (select b.dnumber from vpro4_insrel b where b.snumber="+prg+") ORDER BY 2");
			while(rs.next()) {
				String tmp=rs.getString(1);
				if (groups.equals("")) {
					groups+=tmp;
				} else {
					groups+=","+tmp;
				}
			}	
			stmt.close();
			con.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (groups.equals("")) return(new Vector()); // new new
		con=mmbase.getConnection();
		try {
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select a.number,a.title from vpro4_news a where a.number in (select snumber from vpro4_insrel where dnumber in ("+groups+")) union select a.number,a.title from vpro4_news a where a.number in (select dnumber from vpro4_insrel where snumber in ("+groups+")) ORDER BY 2");
			while(rs.next()) {
				results.addElement(rs.getString(1));
			}	
			stmt.close();
			con.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		p.nieuws=results;	
		}

		return(results);
	}


	public Vector getPlaylstsVector(String prg) {
		MusicProgram p=(MusicProgram)programs.get(prg);
		if (p==null) {
			p=new MusicProgram();
			programs.put(prg,p);
		}
		Vector results=p.playlists;
		if (results==null) {

		results=new Vector();

		MultiConnection con=mmbase.getConnection();
		try {
			Statement stmt=con.createStatement();

			ResultSet rs=stmt.executeQuery("select a.number, a.title from vpro4_playlsts a,vpro4_insrel b where a.number=b.snumber and b.dnumber="+prg+" UNION select a.number, a.title from vpro4_playlsts a,vpro4_insrel b where a.number=b.dnumber and b.snumber="+prg);
			while(rs.next()) {
				results.addElement(rs.getString(1));
			}	
			stmt.close();
			con.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		p.playlists=results;	
		}

		return(results);
	}


	public Vector getEpisodesVector(String prg) {
		MusicProgram p=(MusicProgram)programs.get(prg);
		if (p==null) {
			p=new MusicProgram();
			programs.put(prg,p);
		}
		Vector results=p.episodes;
		if (results==null) {

		results=new Vector();
		String groups="";
		MultiConnection con=mmbase.getConnection();
		try {
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select a.number,a.title from vpro4_programs a where a.number in (select b.snumber from vpro4_insrel b where b.dnumber="+prg+") union select a.number,a.title from vpro4_programs a where a.number in (select b.dnumber from vpro4_insrel b where b.snumber="+prg+") ORDER BY 2");
			while(rs.next()) {
				String tmp=rs.getString(1);
				if (groups.equals("")) {
					groups+="0,"+tmp;
				} else {
					groups+=","+tmp;
				}
			}	
			stmt.close();
			con.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (!groups.equals("")) {
		con=mmbase.getConnection();
		try {
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select a.number,a.title from vpro4_episodes a where a.number in (select snumber from vpro4_insrel where dnumber in ("+groups+")) union select a.number,a.title from vpro4_episodes a where a.number in (select dnumber from vpro4_insrel where snumber in ("+groups+")) ORDER BY 2");
			while(rs.next()) {
				results.addElement(rs.getString(1));
			}	
			stmt.close();
			con.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		}
		p.episodes=results;	
		}

		return(results);
	}


	public Vector getProgramsVector(String prg) {
		MusicProgram p=(MusicProgram)programs.get(prg);
		if (p==null) {
			p=new MusicProgram();
			programs.put(prg,p);
		}
		Vector results=p.programs;
		if (results==null) {

		results=new Vector();
		MultiConnection con=mmbase.getConnection();
		try {
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select a.number,a.title from vpro4_programs a where a.number in (select b.snumber from vpro4_insrel b where b.dnumber="+prg+") union select a.number,a.title from vpro4_programs a where a.number in (select b.dnumber from vpro4_insrel b where b.snumber="+prg+") ORDER BY 2");
			while(rs.next()) {
				results.addElement(rs.getString(1));
			}	
			stmt.close();
			con.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}

		p.programs=results;	
		}
		return(results);
	}
	
	String toStr(Vector results) {
		String result=null;
		Enumeration f=results.elements();
		for (;f.hasMoreElements();) {
			String tmp=(String)f.nextElement();
			if (result==null) {
				result=tmp;
			} else {
				result+=","+tmp;
			}
		}
		return(result);
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
		if (lst != null)
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
   	    	 result = (b.toString());
   	 	}
		return result;
	}

	private void debug( String msg )
	{
		System.out.println( classname +":"+ msg );
	}

// ----------------------------------------------------------------------

}
