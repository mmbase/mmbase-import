/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import java.util.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.builders.Properties;

/**
 * @author Daniel Ockeloen
 * @author Rico Jansen
 */

public class Judas extends Vwm implements MMBaseObserver {
protected Vector urls=new Vector();
private JudasURLpusher pusher=null;


	public Judas() {
		System.out.println("Yo Judas and im alive");
	}

	public boolean performTask(MMObjectNode node) {
		boolean rtn=false;
		return(rtn);
	}

	public boolean probeCall() {
		if (pusher==null) {
			pusher=new JudasURLpusher(this);
			System.out.println("Judas -> Starting URL pusher");
		}
		
		System.out.println("Judas -> Adding observers");

		// add news for 3voor12 
		Vwms.mmb.addLocalObserver("news",this);
		Vwms.mmb.addRemoteObserver("news",this);

		// add audioparts
		Vwms.mmb.addLocalObserver("audioparts",this);
		Vwms.mmb.addRemoteObserver("audioparts",this);

		// add episodes
		Vwms.mmb.addLocalObserver("episodes",this);
		Vwms.mmb.addRemoteObserver("episodes",this);

		// add groups
		Vwms.mmb.addLocalObserver("groups",this);
		Vwms.mmb.addRemoteObserver("groups",this);

		// add groups
		Vwms.mmb.addLocalObserver("mmevents",this);
		Vwms.mmb.addRemoteObserver("mmevents",this);

		// add items
		Vwms.mmb.addLocalObserver("items",this);
		Vwms.mmb.addRemoteObserver("items",this);

		// add people
		Vwms.mmb.addLocalObserver("people",this);
		Vwms.mmb.addRemoteObserver("people",this);

		// add guideart
		Vwms.mmb.addLocalObserver("guideart",this);
		Vwms.mmb.addRemoteObserver("guideart",this);

		return(true);
	}

	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeChanged(String number,String builder, String ctype) {
		System.out.println("Judas -> sees that a "+builder+" "+number+" has changed type="+ctype);

		// check if its a valid track in the cache 
		int inumber=-1;
		try {
			inumber=Integer.parseInt(number);
		} catch(Exception e) {}

	
		// checks for 3voor12
		if (builder.equals("news")) {
			get3voor12news(inumber);
		} else if (builder.equals("episodes")) {
			get3voor12episodes(inumber);
		} else if (builder.equals("audioparts")) {
			get3voor12audioparts(inumber,ctype);
			get3voor12cdtracks(inumber);
		} else if (builder.equals("groups")) {
			get3voor12groups(inumber);
		} else if (builder.equals("mmevents")) {
			get3voor12mmeventsAgenda(inumber);
			get3voor12mmeventsMagazine(inumber);
		}

		// checks for programs/episodes
		if (builder.equals("episodes")) {
			getEpisodes(inumber);
		} else if (builder.equals("items")) {
			getItems(inumber);
		} else if (builder.equals("people")) {
			getPeople(inumber);
		}
	
		return(true);
	}


	public void get3voor12cdtracks(int number) {
		Vector tables=new Vector();
		tables.addElement("programs");
		tables.addElement("groups");
		tables.addElement("insrel");
		tables.addElement("audioparts");
		Vector fields=new Vector();
		fields.addElement("programs.number");
		fields.addElement("groups.number");
		fields.addElement("audioparts.number");
		Vector ordervec=new Vector();
		Vector dirvec=new Vector();


		MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");		
		
		Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"",ordervec,dirvec);
		for (Enumeration e=vec.elements();e.hasMoreElements();) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			System.out.println("Judas -> found nodes "+node);
			int prog=node.getIntValue("programs.number");
			int group=node.getIntValue("groups.number");
			int area=prog-62752; // huge hack possible because programs range from 62753 to 62758
			if (area>0 && area<7) {
				String url="/data/3voor12/layout/tracks/track.shtml?"+area+"+"+number;
				System.out.println("Judas -> url "+url);
				addURL(url);
				url="/data/3voor12/layout/tracks/track-list.shtml?0+0+100+number+down";
				addURL(url);
 				url="/data/3voor12/layout/tracks/track-list.shtml?0+1+100+number+down";
				addURL(url);
 				url="/data/3voor12/layout/tracks/track-list.shtml?0+2+100+number+down";
 				addURL(url);
 				url="/data/3voor12/layout/tracks/track-list.shtml?0+3+100+number+down";
 				addURL(url);
 				url="/data/3voor12/layout/tracks/track-list.shtml?0+0+100+title+up";
 				addURL(url);
 				url="/data/3voor12/layout/tracks/track-list.shtml?0+1+100+title+up";
 				addURL(url);
 				url="/data/3voor12/layout/tracks/track-list.shtml?0+2+100+title+up";
 				addURL(url);
 				url="/data/3voor12/layout/tracks/track-list.shtml?0+3+100+title+up";
 				addURL(url);

				// now the area
				url="/data/3voor12/layout/tracks/track-list.shtml?"+area+"+0+100+number+down";
				addURL(url);

 				url="/data/3voor12/layout/tracks/track-list.shtml?"+area+"+1+100+number+down";
 				addURL(url);
 				url="/data/3voor12/layout/tracks/track-list.shtml?"+area+"+2+100+number+down";
 				addURL(url);
 				url="/data/3voor12/layout/tracks/track-list.shtml?"+area+"+3+100+number+down";
 				addURL(url);
 				url="/data/3voor12/layout/tracks/track-list.shtml?"+area+"+0+100+title+up";
 				addURL(url);
 				url="/data/3voor12/layout/tracks/track-list.shtml?"+area+"+1+100+title+up";
 				addURL(url);
 				url="/data/3voor12/layout/tracks/track-list.shtml?"+area+"+2+100+title+up";
 				addURL(url);
 				url="/data/3voor12/layout/tracks/track-list.shtml?"+area+"+3+100+title+up";
 				addURL(url);
 
 				// and the band page itself	
				url="/data/3voor12/layout/bands/band.shtml?"+area+"+"+group;
				addURL(url);
			}
		}
	}


	public void get3voor12news(int number) {
		Vector tables=new Vector();
		tables.addElement("programs");
		tables.addElement("news");
		Vector fields=new Vector();
		fields.addElement("programs.number");
		fields.addElement("news.number");
		Vector ordervec=new Vector();
		Vector dirvec=new Vector();
		String url;


		MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");		
		
		Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"",ordervec,dirvec);
		for (Enumeration e=vec.elements();e.hasMoreElements();) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			System.out.println("Judas -> found nodes "+node);
			int prog=node.getIntValue("programs.number");
			int news=node.getIntValue("news.number");
			int area=prog-62752; // huge hack possible because programs range from 62753 to 62758
			if (prog==128877) area=0;
			if (area>-1 && area<7) {
				url="/data/3voor12/layout/nieuws/nieuws.shtml?"+area+"+"+number;
				addURL(url);
				url="/data/3voor12/layout/nieuws/nieuws-list.shtml?0+0+100+number+down";

				addURL(url);
 				url="/data/3voor12/layout/nieuws/nieuws-list.shtml?0+1+100+number+down";
 				addURL(url);
 				url="/data/3voor12/layout/nieuws/nieuws-list.shtml?0+2+100+number+down";
 				addURL(url);
 				url="/data/3voor12/layout/nieuws/nieuws-list.shtml?0+0+100+title+up";
 				addURL(url);
 				url="/data/3voor12/layout/nieuws/nieuws-list.shtml?0+1+100+title+up";
 				addURL(url);
 				url="/data/3voor12/layout/nieuws/nieuws-list.shtml?0+2+100+title+up";
 				addURL(url);
				url="/data/3voor12/layout/nieuws/magazine/magazine.shtml?0";
				addURL(url);
				url="/data/3voor12/layout/nieuws/magazine/magazine.shtml?1";
				addURL(url);
				url="/data/3voor12/layout/nieuws/magazine/magazine.shtml?2";
				addURL(url);
				url="/data/3voor12/layout/nieuws/magazine/magazine.shtml?3";
				addURL(url);
				url="/data/3voor12/layout/nieuws/magazine/magazine.shtml?4";
				addURL(url);
				url="/data/3voor12/layout/nieuws/magazine/magazine.shtml?5";
				addURL(url);
				url="/data/3voor12/layout/nieuws/magazine/magazine.shtml?6";
				addURL(url);
			}
			// Sportpaleis
			if (prog==2142557) {
				url="/data/"+prog+"/agenda.shtml?"+news;
				addURL(url);
			}
		}
	}



	public void getEpisodes(int number) {
		Vector tables=new Vector();
		tables.addElement("programs");
		tables.addElement("episodes");
		Vector fields=new Vector();
		fields.addElement("programs.number");
		fields.addElement("episodes.number");
		Vector ordervec=new Vector();
		Vector dirvec=new Vector();


		MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");		
		
		Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"",ordervec,dirvec);
		for (Enumeration e=vec.elements();e.hasMoreElements();) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			System.out.println("Judas -> found nodes "+node);
			int prog=node.getIntValue("programs.number");
			int episode=node.getIntValue("episodes.number");

			// extra check for 5jaar site !!
			if (prog==1615807) {
				System.out.println("5 jaar check="+prog);
				String url="/data/projecten/5JAAR2HOOG/1.1/aflevering/aflevering-txt.shtml?"+episode;
				addURL(url);
			} else if (false && prog==2215511) {
				// Kaft toer
				System.out.println("Judas -> Kaft toer check");
				String url="/data/kaft/kafttoer/1.0/episode/one_episode.shtml?"+prog+"+"+episode;
				addURL(url);

				url="/data/kaft/kafttoer/1.0/episode/list_items.shtml?"+prog+"+"+episode;
				addURL(url);

				url="/data/kaft/kafttoer/1.0/episode/intro.shtml?"+prog;
				addURL(url);

				url="/data/kaft/kafttoer/1.0/episode/episode.shtml?"+prog+"+"+episode;
				addURL(url);

				url="/data/kaft/kafttoer/1.0/episode/parts/list_episodes.shtml?"+prog+"+"+episode;
				addURL(url);

				url="/data/kaft/kafttoer/1.0/program/description.shtml?"+prog;
				addURL(url);
			} else {
				String url="/data/"+prog+"/aflevering.shtml?"+episode;
				addURL(url);
				url="/data/"+prog+"/aflevering-txt.shtml?"+episode;
				addURL(url);
				url="/data/"+prog+"/aflevering_txt.shtml?"+episode;
				addURL(url);

				url="/data/"+prog+"/archief.shtml?"+prog;
				addURL(url);
				url="/data/"+prog+"/archief-txt.shtml?"+prog;
				addURL(url);
				url="/data/"+prog+"/archief_txt.shtml?"+prog;
				addURL(url);

				url="/data/"+prog+"/gasten.shtml?"+prog;
				addURL(url);
				url="/data/"+prog+"/gasten-txt.shtml?"+prog;
				addURL(url);
				url="/data/"+prog+"/gasten_txt.shtml?"+prog;
				addURL(url);
				url="/data/"+prog+"/program.shtml?"+prog;
				addURL(url);
				url="/data/"+prog+"/program-txt.shtml?"+prog;
				addURL(url);
				url="/data/"+prog+"/program_txt.shtml?"+prog;
				addURL(url);
			}
		}
	}



	public void getItems(int number) {
		Vector tables=new Vector();
		tables.addElement("programs");
		tables.addElement("episodes");
		tables.addElement("items");
		Vector fields=new Vector();
		fields.addElement("programs.number");
		fields.addElement("episodes.number");
		fields.addElement("items.number");
		Vector ordervec=new Vector();
		Vector dirvec=new Vector();


		MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");		
		
		Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"",ordervec,dirvec);
		for (Enumeration e=vec.elements();e.hasMoreElements();) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			System.out.println("Judas -> found nodes "+node);
			int prog=node.getIntValue("programs.number");
			int episode=node.getIntValue("episodes.number");
			int item=node.getIntValue("items.number");

			// extra check for 5jaar site !!
			if (prog==1615807) {
				System.out.println("5 jaar check="+prog);
			
				InsRel bul=(InsRel)Vwms.mmb.getMMObject("insrel");		
				Enumeration g=bul.getRelated(item,5);
				if (g.hasMoreElements()) {
					MMObjectNode pnode=(MMObjectNode)g.nextElement();
				
					String url="/data/projecten/5JAAR2HOOG/1.1/persoon/persoon-item.shtml?"+pnode.getIntValue("number");
					addURL(url);
				}
				String url="/data/projecten/5JAAR2HOOG/1.1/aflevering/aflevering-item.shtml?"+episode;
				addURL(url);
			} else if (false && prog==2215511) {
				// Kaft toer
				System.out.println("Judas -> Kaft toer check");
				String url="/data/kaft/kafttoer/1.0/episode/one_episode.shtml?"+prog+"+"+episode;
				addURL(url);

				url="/data/kaft/kafttoer/1.0/episode/list_items.shtml?"+prog+"+"+episode;
				addURL(url);

				url="/data/kaft/kafttoer/1.0/program/description.shtml?"+prog;
				addURL(url);

				InsRel bul=(InsRel)Vwms.mmb.getMMObject("insrel");		
				Enumeration g=bul.getRelated(""+item,"people");
				MMObjectNode pnode;
				if (g.hasMoreElements()) {
					pnode=(MMObjectNode)g.nextElement();
					url="/data/kaft/kafttoer/1.0/item/item.shtml?"+prog+"+"+episode+"+"+pnode.getIntValue("number")+"+"+item;
					addURL(url);
				}
			} else {
				String url="/data/"+prog+"/item.shtml?"+item;
				addURL(url);
				url="/data/"+prog+"/item-txt.shtml?"+item;
				addURL(url);
				url="/data/"+prog+"/item_txt.shtml?"+item;
				addURL(url);
				url="/data/"+prog+"/aflevering.shtml?"+episode;
				addURL(url);
				url="/data/"+prog+"/aflevering-txt.shtml?"+episode;
				addURL(url);
			}

		}
	}


	public void getPeople(int number) {
		Vector tables=new Vector();
		tables.addElement("programs");
		tables.addElement("episodes");
		tables.addElement("items");
		tables.addElement("people");
		Vector fields=new Vector();
		fields.addElement("programs.number");
		fields.addElement("episodes.number");
		fields.addElement("items.number");
		fields.addElement("people.number");
		Vector ordervec=new Vector();
		Vector dirvec=new Vector();


		MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");		
		
		Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"",ordervec,dirvec);
		for (Enumeration e=vec.elements();e.hasMoreElements();) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			System.out.println("Judas -> found nodes "+node);
			int prog=node.getIntValue("programs.number");
			int episode=node.getIntValue("episodes.number");
			int item=node.getIntValue("items.number");
			int people=node.getIntValue("people.number");

			if (false && prog==2215511) {
				System.out.println("Judas -> Kaft toer check");
				// Kaft toer
				String url="/data/kaft/kafttoer/1.0/item/item.shtml?"+prog+"+"+episode+"+"+people+"+"+item;
				addURL(url);

				url="/data/kaft/kafttoer/1.0/people/list_people.shtml?"+prog+"+"+people;
				addURL(url);

				url="/data/kaft/kafttoer/1.0/people/one_people.shtml?"+prog+"+"+people;
				addURL(url);

				url="/data/kaft/kafttoer/1.0/people/intro.shtml?"+prog+"+"+people;
				addURL(url);

				url="/data/kaft/kafttoer/1.0/people/list_items.shtml?"+prog+"+"+people;
				addURL(url);
			}
		}
	}


	public void get3voor12groups(int number) {
		Vector tables=new Vector();
		tables.addElement("programs");
		tables.addElement("groups");
		Vector fields=new Vector();
		fields.addElement("programs.number");
		fields.addElement("groups.number");
		Vector ordervec=new Vector();
		Vector dirvec=new Vector();


		MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");		
		
		Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"",ordervec,dirvec);
		for (Enumeration e=vec.elements();e.hasMoreElements();) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			System.out.println("Judas -> found nodes "+node);
			int prog=node.getIntValue("programs.number");
			int news=node.getIntValue("groups.number");
			int area=prog-62752; // huge hack possible because programs range from 62753 to 62758
			if (prog==128877) area=0;
			if (area>-1 && area<7) {
				String url="/data/3voor12/layout/bands/band.shtml?"+area+"+"+number;
				System.out.println("Judas -> url "+url);
				addURL(url);

				url="/data/3voor12/layout/bands/band-list.shtml?0+0+100+number+down";
				addURL(url);

 				url="/data/3voor12/layout/bands/band-list.shtml?0+0+100+number+down";
 				addURL(url);
 				url="/data/3voor12/layout/bands/band-list.shtml?0+1+100+number+down";
 				addURL(url);
 				url="/data/3voor12/layout/bands/band-list.shtml?0+2+100+number+down";
 				addURL(url);
 				url="/data/3voor12/layout/bands/band-list.shtml?0+3+100+number+down";
 				addURL(url);
 				url="/data/3voor12/layout/bands/band-list.shtml?0+0+100+name+up";
 				addURL(url);
 				url="/data/3voor12/layout/bands/band-list.shtml?0+1+100+name+up";
 				addURL(url);
 				url="/data/3voor12/layout/bands/band-list.shtml?0+2+100+name+up";
 				addURL(url);
 				url="/data/3voor12/layout/bands/band-list.shtml?0+3+100+name+up";
 				addURL(url);
 
 				// area	
				url="/data/3voor12/layout/bands/band-list.shtml?"+area+"+0+100+number+down";
				addURL(url);
 				url="/data/3voor12/layout/bands/band-list.shtml?"+area+"+1+100+number+down";
 				addURL(url);
 				url="/data/3voor12/layout/bands/band-list.shtml?"+area+"+2+100+number+down";
 				addURL(url);
 				url="/data/3voor12/layout/bands/band-list.shtml?"+area+"+3+100+number+down";
 				addURL(url);
 				url="/data/3voor12/layout/bands/band-list.shtml?"+area+"+0+100+name+up";
 				addURL(url);
 				url="/data/3voor12/layout/bands/band-list.shtml?"+area+"+1+100+name+up";
 				addURL(url);
 				url="/data/3voor12/layout/bands/band-list.shtml?"+area+"+2+100+name+up";
 				addURL(url);
 				url="/data/3voor12/layout/bands/band-list.shtml?"+area+"+3+100+name+up";
 				addURL(url);
			}
		}
	}


	public void get3voor12episodes(int number) {
		Vector tables=new Vector();
		tables.addElement("pools");
		tables.addElement("programs");
		tables.addElement("episodes");
		Vector fields=new Vector();
		fields.addElement("pools.number");
		fields.addElement("programs.number");
		fields.addElement("episodes.number");
		Vector ordervec=new Vector();
		Vector dirvec=new Vector();


		MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");		
		
		Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"",ordervec,dirvec);
		for (Enumeration e=vec.elements();e.hasMoreElements();) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			System.out.println("Judas -> found nodes "+node);
			int pool=node.getIntValue("pools.number");
			int prog=node.getIntValue("programs.number");
			int area=-1;

			if (pool==1881762) area=0;
			if (pool==1882536) area=1;
			if (pool==1882541) area=2;
			if (pool==1882542) area=3;
			if (pool==1882543) area=4;
			if (pool==1882554) area=5;
			if (pool==1882555) area=6;

			if (area>-1 && area<7) {
				String url="/data/3voor12/layout/uitzendingen/uitzending.shtml?"+area+"+"+number;
				addURL(url);
				url="/data/3voor12/layout/programs/program.shtml?"+area+"+"+prog;
				addURL(url);

			}
		}
	}


	public void get3voor12audioparts(int number,String type) {
		Vector tables=new Vector();
		tables.addElement("pools");
		tables.addElement("programs");
		tables.addElement("episodes");
		tables.addElement("audioparts");
		Vector fields=new Vector();
		fields.addElement("pools.number");
		fields.addElement("programs.number");
		fields.addElement("episodes.number");
		fields.addElement("audioparts.number");
		Vector ordervec=new Vector();
		Vector dirvec=new Vector();

		
		// this is bad but i have NO idea what better way to use now when
		// a delete hits.
		if (type.equals("d")) {
			// i have NO idea how to solve it unless we know/check before a delete or
			// we keep deleted structures nodes somehow so we can still query them !
			// best bet is a delayed delete.
		} else {
			MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");		
		
			Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"",ordervec,dirvec);
			for	 (Enumeration e=vec.elements();e.hasMoreElements();) {
				MMObjectNode node=(MMObjectNode)e.nextElement();
				System.out.println("Judas -> found nodes "+node);
				int pool=node.getIntValue("pools.number");
				int prog=node.getIntValue("programs.number");
				int episode=node.getIntValue("episodes.number");
				int area=-1;

				if (pool==1881762) area=0;
				if (pool==1882536) area=1;
				if (pool==1882541) area=2;
				if (pool==1882542) area=3;
				if (pool==1882543) area=4;
				if (pool==1882554) area=5;
				if (pool==1882555) area=6;

				if (area>-1 && area<7) {
					String url="/data/3voor12/layout/uitzendingen/uitzending.shtml?"+area+"+"+episode;
					addURL(url);
					url="/data/3voor12/layout/programs/program.shtml?"+area+"+"+prog;
					addURL(url);

				}
			}
		}
	}


	public void get3voor12mmeventsAgenda(int number) {
		Vector tables=new Vector();
		tables.addElement("pools");
		tables.addElement("programs");
		tables.addElement("episodes");
		tables.addElement("mmevents");
		Vector fields=new Vector();
		fields.addElement("pools.number");
		fields.addElement("programs.number");
		fields.addElement("episodes.number");
		Vector ordervec=new Vector();
		Vector dirvec=new Vector();


		MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");		
		
		Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"",ordervec,dirvec);
		for (Enumeration e=vec.elements();e.hasMoreElements();) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			System.out.println("Judas -> found nodes "+node);
			int prog=node.getIntValue("pools.number");
			int news=node.getIntValue("programs.number");
			int area=-1;

			if (prog==1881762) area=0;
			if (prog==1882536) area=1;
			if (prog==1882541) area=2;
			if (prog==1882542) area=3;
			if (prog==1882543) area=4;
			if (prog==1882554) area=5;
			if (prog==1882555) area=6;

			if (area>-1 && area<7) {
				String url="/data/3voor12/layout/programs/agenda/agenda.shtml?0";
				addURL(url);
				url="/data/3voor12/layout/programs/agenda/agenda.shtml?1";
				addURL(url);
				url="/data/3voor12/layout/programs/agenda/agenda.shtml?2";
				addURL(url);
				url="/data/3voor12/layout/programs/agenda/agenda.shtml?3";
				addURL(url);
				url="/data/3voor12/layout/programs/agenda/agenda.shtml?4";
				addURL(url);
				url="/data/3voor12/layout/programs/agenda/agenda.shtml?5";
				addURL(url);
				url="/data/3voor12/layout/programs/agenda/agenda.shtml?6";
				addURL(url);
			}
		}
	}


	public void get3voor12mmeventsMagazine(int number) {
		Vector tables=new Vector();
		tables.addElement("programs");
		tables.addElement("news");
		tables.addElement("mmevents");
		Vector fields=new Vector();
		fields.addElement("programs.number");
		fields.addElement("news.number");
		Vector ordervec=new Vector();
		Vector dirvec=new Vector();


		MultiRelations multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");		
		
		Vector vec=multirelations.searchMultiLevelVector(number,fields,"YES",tables,"",ordervec,dirvec);
		for (Enumeration e=vec.elements();e.hasMoreElements();) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			System.out.println("Judas -> found nodes "+node);
			int prog=node.getIntValue("programs.number");
			int area=prog-62752; // huge hack possible because programs range from 62753 to 62758
			if (prog==128877) area=0;
			if (area>-1 && area<7) {
				String url="/data/3voor12/layout/nieuws/magazine/magazine.shtml?0";
				addURL(url);
				url="/data/3voor12/layout/nieuws/magazine/magazine.shtml?1";
				addURL(url);
				url="/data/3voor12/layout/nieuws/magazine/magazine.shtml?2";
				addURL(url);
				url="/data/3voor12/layout/nieuws/magazine/magazine.shtml?3";
				addURL(url);
				url="/data/3voor12/layout/nieuws/magazine/magazine.shtml?4";
				addURL(url);
				url="/data/3voor12/layout/nieuws/magazine/magazine.shtml?5";
				addURL(url);
				url="/data/3voor12/layout/nieuws/magazine/magazine.shtml?6";
				addURL(url);
			}
		}
	}

	public void addURL(String url) {
		urls.addElement(url);
	}


	public boolean pushReload(String url) {
		System.out.println("Judas -> pushing ="+url);
		url=url.replace('?',':')+".asis";
		Netfiles bul=(Netfiles)Vwms.mmb.getMMObject("netfiles");		
		Enumeration e=bul.search("WHERE filename='"+url+"' AND service='pages' AND subservice='main'");
		if (e.hasMoreElements()) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			node.setValue("status",4);
			node.commit();
		}
		return(true);
	}

}
