/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;


import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * RawVideosProbe, encodes wav files
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @version 14-Jan-1998
 */
public class RawVideosProbe implements Runnable {

	Thread kicker = null;
	RawVideos parent=null;
	int trackNr=-1;
	String filename=null;
	int sleepTime=30000;
	float maxdiff=(float)0.10; // maximum of 10% difference between calc en found

	public RawVideosProbe(RawVideos parent) {
		this.parent=parent;
		init();
	}

	public void init() {
		this.start();	
	}


	/**
	 * Starts the main Thread.
	 */
	public void start() {
		/* Start up the main thread */
		if (kicker == null) {
			kicker = new Thread(this,"videosprobe");
			kicker.start();
		}
	}
	
	/**
	 * Stops the main Thread.
	 */
	public void stop() {
		/* Stop thread */
		kicker.setPriority(Thread.MIN_PRIORITY);  
		kicker.suspend();
		kicker.stop();
		kicker = null;
	}

	/**
	 * Main loop, exception protected
	 */
	public void run () {
		kicker.setPriority(Thread.MIN_PRIORITY+1);  
		while (kicker!=null) {
			try {
				try {Thread.sleep(sleepTime);} catch (InterruptedException e){}
				checkInProgress();
				doWork();
			} catch(Exception e) {
				System.out.println("RawVideos -> Exception in rawvideos thread");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Main work loop
	 */
	public void doWork() {
		kicker.setPriority(Thread.MIN_PRIORITY+1);  
		MMObjectNode node,node2;
		boolean needbreak=false;
		int id;

		while (kicker!=null) {
			try {Thread.sleep(sleepTime);} catch (InterruptedException e){}
			Enumeration e=parent.search("where status=1");
			while (e.hasMoreElements()) {
				needbreak=false;
				node=(MMObjectNode)e.nextElement();
				id=node.getIntValue("id");
				if (id!=-1) {
					node2=parent.getNode(id); // sneaky we get VideoPart node here
//					System.out.println("RawVideos -> encode node : "+node2+"\n"+node);
					if (node2!=null) {
						needbreak=encodeVideo(node2,node);
					}
				}
				if (needbreak) {
					System.out.println("RawVideos -> Encode Need break");
					break;
				}
			}
		}
	}

	private boolean encodeVideo(MMObjectNode anode,MMObjectNode rnode) {
		int type;
		boolean needbreak=false;
		int format;

		type=anode.getIntValue("otype");
		format=rnode.getIntValue("format");
		if (type==1147) {
			// CDtracks
			needbreak=encodeCDtrack(rnode,format);
		} else if (type==1483) {
			// videoparts
			needbreak=encodeVideoPart(rnode,format);
		} else {
			System.out.println("RawVideos -> unknown otype "+type);
		}
		return(needbreak);
	}


	private boolean encodeCDtrack(MMObjectNode node,int format) {
		boolean needbreak;

		needbreak=false;

//		System.out.println("RawVideos -> CDtrack "+format);
		switch(format) {
			case 1:
				needbreak=encodeMPG(node);
				break;
			case 2:
				needbreak=encodeRA(node);
				break;
			case 3:
				if (parent.getMachineName().equals("station")) {
					int id=node.getIntValue("id");
					// we want to scan a cdtrack
					CDTracks bul=(CDTracks)node.parent;	
					bul.getTrack(id,node);
				}
				break;
			default:
				System.out.println("RawVideos -> Unknown format "+format);
				break;

		}
		return(needbreak);
	}

	private boolean encodeVideoPart(MMObjectNode node,int format) {
		boolean needbreak;

		needbreak=false;
//		System.out.println("RawVideos -> VideoPart "+format);
		switch(format) {
			case 1:
				needbreak=encodeMPG(node);
				break;
			case 2:
				needbreak=encodeRA(node);
				break;
			case 3:
				System.out.println("RawVideos -> format 3 not supported");
				break;
			default:
				System.out.println("RawVideos -> Unknown format "+format);
				break;

		}
		return(needbreak);
	}


	boolean fileExists(String filename) {
		File sfile = new File(filename);
		if (sfile.isFile()) {
			return(true);
		}
		return(false);
	}

	long fileSize(String filename) {
		File sfile = new File(filename);
		if (sfile.isFile()) {
			return(sfile.length());
		}
		return(-1);
	}

	private boolean encodeMPG(MMObjectNode node) {
		boolean needbreak=false;
		int id;

		try {
			// we want to create a mp3 file
			mpegl3Interface mpeg=parent.mmb.mpegl3.getFree();
			System.out.println("RawVideos -> Found MP3 Encoder ="+mpeg);
			
			id=node.getIntValue("id");
			node.setValue("status",2);
			node.setValue("cpu",parent.getMachineName());
			node.setValue("url","http://station.vpro.nl/video/mpg/"+id+"/"+(node.getIntValue("speed")/1000)+"_"+node.getIntValue("channels")+".mp3");
			node.commit();
	
			System.out.println("Starting encode videopart on : /data/video/mov/"+id+".wav /data/video/mpg/"+id+" "+node.getIntValue("speed")+" "+node.getIntValue("channels"));
			mpeg.doEncode("/data/video/mov/"+id+".mov","/data/video/mpg/"+id,node.getIntValue("speed"),node.getIntValue("channels"));
	
			node.setValue("status",3);
			node.commit();
			needbreak=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return(needbreak);
	}

	private boolean encodeRA(MMObjectNode node) {
		boolean needbreak=false;
		boolean encoded=false;
		int id,rt;

		try {
			// we want to create a RA file
			raencoderInterface ra=parent.mmb.raenc.getFree();
//			System.out.println("RawVideos -> Found RA Encoder ="+ra);
			id=node.getIntValue("id");

			// should check if it can find the wanted file
			// local or remote.
			if (fileExists("/data/video/mov/"+id+".mov")) {
				node.setValue("status",2);
				node.setValue("cpu",parent.getMachineName());
				node.setValue("url","http://station.vpro.nl/video/rm/"+id+"/"+(node.getIntValue("speed")/1000)+"_"+node.getIntValue("channels")+".rm");
				node.commit();

				System.out.println("RawVideos -> Starting encode on : /data/video/mov/"+id+".mov /data/video/rm/"+id+" "+node.getIntValue("speed")+" "+node.getIntValue("channels"));
				ra.doEncode("/data/video/mov/"+id+".mov","/data/video/rm/"+id,node.getIntValue("speed"),node.getIntValue("channels"));
				encoded=true;
			} else if (fileExists("/video/mov/"+id+".mov")) {
				node.setValue("status",2);
				node.setValue("cpu",parent.getMachineName());
				node.setValue("url","http://station.vpro.nl/video/rm/"+id+"/"+(node.getIntValue("speed")/1000)+"_"+node.getIntValue("channels")+".rm");
				node.commit();

				System.out.println("RawVideos -> Starting encode on : /video/mov/"+id+".mov /data/video/rm/"+id+" "+node.getIntValue("speed")+" "+node.getIntValue("channels"));
				ra.doEncode("/video/mov/"+id+".mov","/data/video/rm/"+id,node.getIntValue("speed"),node.getIntValue("channels"));
				encoded=true;
			}
			if (encoded) {
				System.out.println("RawVideos -> Encode done");
				rt=verifyEncode(node,id);
				if (rt==3) {
					node.setValue("status",3);
				} else {
					System.out.println("RawVideos -> Encode Failed !!!!!");
					node.setValue("status",2);
				}
				node.commit();
				needbreak=true;
			} else {
				System.out.println("RawVideos -> Encode file not found (maybe not local?) "+id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return(needbreak);
	}

	private void checkInProgress() {
		int rt;
		try {
			String machine=parent.getMachineName();
			MMObjectNode node;
			int id;
	
			System.out.println("RawVideos -> checkInProgress");
			Enumeration e=parent.search("where status=2 and cpu='"+machine+"'");
			while (e.hasMoreElements()) {
				node=(MMObjectNode)e.nextElement();
				id=node.getIntValue("id");
				if (id!=-1) {
					System.out.println("RawVideos -> encode in progress on : "+id);
					rt=verifyEncode(node,id);
					if (rt==3) {
						node.setValue("status",3);
					} else if (rt==1) {
						node.setValue("status",1);
					} else {
						node.setValue("status",2);
					}
					node.commit();
				}
			}
		} catch (Exception e) {
			System.out.println("checkInProgress exception (will continue) "+e);
			e.printStackTrace();
		}
	}

	private int verifyEncode(MMObjectNode node,int id) {
		// Dependency on Linux is HERE
		int rtn=2;
		VideoObject movje=null;
		Execute exec=new Execute();
		String res,name;

		System.out.println("verifyEncode on node : "+node);

		// check if rmenc process is running
		// ---------------------------------
		System.out.println("verifyEncode : Checking for rmenc with id "+id);
		res=exec.execute("/root/bin/checkmov "+id+".wav");
		System.out.println("verifyEncode("+id+") : found "+res);
		while (res.length()>1) {

			// wait while being executed
			// -------------------------

			try {
				Thread.sleep(4*60*1000);
			} catch(InterruptedException e) {}
			res=exec.execute("/root/bin/checkmov "+id+".mov");
			System.out.println("verifyEncode("+id+") : found "+res);
		}

		// check if size is sane
		// ---------------------
		System.out.println("verifyEncode : Checking size");
		// find original wav
		name="/data/video/mov/"+id+".mov";
		if (fileExists(name)) {
			movje=VideoObject.getInfo(name);
		} else {
			name="/video/mov/"+id+".mov";
			if (fileExists(name)) {
				movje=VideoObject.getInfo(name);
			}
		}

		if (movje!=null) {
			float csize;
			float osize;
			float diff;
			System.out.println("verifyEncode found movje : "+movje);
			csize=movje.getLength()*node.getIntValue("speed")/8;
			name=parent.getFullName(node);
			osize=fileSize(name);
			System.out.println("verifyEncode("+id+") calc size "+csize+" found size "+osize);
			diff=Math.abs(csize-osize);
			if (diff>(maxdiff*(osize+csize)/2)) {
				System.out.println("verifyEncode("+id+") diff to large "+diff);
				rtn=1;
			} else {
				System.out.println("verifyEncode("+id+") OK ");
				rtn=3;
			}
//			System.out.println("verifyEncode("+id+") changes NOT commited");
		} else {
			// WAV not found ??
			System.out.println("verifyEncode : MOV not found "+id+" !!!!!!!!");
		}
		return(rtn);
	}

}
