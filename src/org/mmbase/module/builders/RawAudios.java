/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;


/**
 * @author Daniel Ockeloen
 * @version 12 Mar 1997
 * 17Dec1999 Added static method getFileName, davzev
 */
public class RawAudios extends MMObjectBuilder {
 	public boolean replaceCache=true;

	// These contstants are used by the new AudioParts.getUrl() method.
	public final static int MP3_FORMAT         = 1;
	public final static int RA_FORMAT          = 2;
	public final static int WAV_FORMAT         = 3;
	public final static int PCM_FORMAT         = 4;
	public final static int MP2_FORMAT         = 5;
	public final static int SURESTREAM_FORMAT  = 6; 
	public final static int GEDAAN = 3;

	public RawAudios() {
	}

	public String getGUIIndicator(MMObjectNode node) {
		String str=node.getStringValue("number");
		if (str.length()>15) {
			return(str.substring(0,12)+"...");
		} else {
			return(str);
		}
	}

	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("status")) {
			int val=node.getIntValue("status");
			switch(val) {
				case 1: return("Verzoek");
				case 2: return("Onderweg");
				case 3: return("Gedaan");
				case 4: return("Bron");
				default: return("Onbepaald");
			}
		} else if (field.equals("format")) {
			int val=node.getIntValue("format");
			switch(val) {
				case 1: return("mp3");
				case 2: return("ra");
				case 3: return("wav");
				case 4: return("pcm");
				case 5: return("mp2");
				case 6: return("g2/sure");
				default: return("Onbepaald");
			}
		} else if (field.equals("channels")) {
			int val=node.getIntValue("channels");
			switch(val) {
				case 1: return("mono");
				case 2: return("stereo");
				default: return("Onbepaald");
			}
		}
		return(null);
	}

	/**
	* get new node
	*/
	public MMObjectNode getNewNode(String owner) {
		MMObjectNode node=super.getNewNode(owner);
		// readCDInfo();
		// if (diskid!=null) node.setValue("discId",diskid);
		// if (playtime!=-1) node.setValue("playtime",playtime);
		return(node);
	}


	public Object getValue(MMObjectNode node,String field) {
		if (field.equals("str(status)")) {
			int val=node.getIntValue("status");
			switch(val) {
				case 1: return("Verzoek");
				case 2: return("Onderweg");
				case 3: return("Gedaan");
				case 4: return("Bron");
				default: return("Onbepaald");
			}
		} else if (field.equals("str(channels)")) {
			int val=node.getIntValue("channels");
			switch(val) {
				case 1: return("Mono");
				case 2: return("Stereo");
				default: return("Onbepaald");
			}
		} else if (field.equals("str(format)")) {
			int val=node.getIntValue("format");
			switch(val) {
				case 1: return("mp3");
				case 2: return("ra");
				case 3: return("wav");
				case 6: return("g2/sure");
				default: return("Onbepaald");
			}
		}
		return(null);
	}


	public boolean removeAudio(int id) {
		Connection con;
		boolean rtn=false;
		MMObjectNode node;
		Enumeration audios;

		audios=search("WHERE id="+id);
		while(audios.hasMoreElements()) {
			node=(MMObjectNode)audios.nextElement();
			System.out.println("RawAudios -> Zapping "+node.getIntValue("number")+","+node.getStringValue("url"));
			removeRelations(node);
			removeNode(node);
			zapPhysical(node);
			rtn=true;
		}
		if (true) {
			// For every format check the directory
			// MP3 
			// Nothing yet
			// RA
			removeRA(id);
			// WAV
			// Nothing yet
		}
		return(rtn);
	}

	private void zapPhysical(MMObjectNode node) {
		int id,iformat;
		int speed,channels;
		String path;
		String name;

		id=node.getIntValue("id");
		iformat=node.getIntValue("format");
		speed=node.getIntValue("speed")/1000;
		channels=node.getIntValue("channels");
		switch(iformat) {
			case 1: // mp3
				// Nothing for now
				path="/data/audio/mp3/"+id;
				break;
			case 2: // ra
				// Decode .ra file name
				path="/data/audio/ra/"+id;
				name=speed+"_"+channels+".ra";
				removeFile(path,name);
				break;
			case 3: // wav
				// Nothing for now
				path="/data/audio/wav/"+id;
				break;
			case 6: // G2
				path="/data/audio/ra/"+id;
				name="surestream.rm";
				removeFile(path,name);
				break;
			default: // Unknown
				break;
		}
	}

	public String getFullName(MMObjectNode node) {
		int id,iformat;
		int speed,channels;
		String path;
		String name;

		id=node.getIntValue("id");
		iformat=node.getIntValue("format");
		speed=node.getIntValue("speed")/1000;
		channels=node.getIntValue("channels");
		switch(iformat) {
			case 1: // mp3
				path="/data/audio/mp3/"+id+"/"+speed+"_"+channels+".ra";
				break;
			case 2: // ra
				path="/data/audio/ra/"+id+"/"+speed+"_"+channels+".ra";
				break;
			case 3: // wav
				path="/data/audio/wav/"+id+".wav";
				break;
			case 6: // G2
				path="/data/audio/ra/"+id+"/"+"surestream.rm";
				break;
			default: // Unknown
				path=null;
				break;
		}
		return(path);
	}

	private void removeRA(int id) {
		String path="/data/audio/ra";
		String name="real.txt";
		removeFile(path,id+"/"+name);
		removeFile(path,""+id);
	}

	private void removeFile(String path,String name) {
		File f;

		f=new File(path,name);
		if (f.isDirectory()) {
			System.out.println("Removing dir "+f.getPath());
			if (!f.delete()) {
				System.out.println("Can't delete directory "+f.getPath());
			}
		} else {
			System.out.println("Removing file "+f.getPath());
			if (!f.delete()) {
				System.out.println("Can't delete file "+f.getPath());
			}
		}
	}

	/**
	 * getFileName: Gets the right audio filename using the format speed and channels values.
	 * @param format The audio format used.
	 * @param speed The speed value.
	 * @param channels The channels value.
	 * @returns The audio fileName
	 */
	public static String getFileName(int format, int speed, int channels) {	
		String fileName = new String();
		String SURESTREAM_FILENAME = "surestream.rm"; 
	
		if (format == 2) {
			fileName = ""+(speed/1000)+"_"+channels; 
		} else if (format == 3) {
			System.out.println("RawAudios::getFilename: Yeah right!! I'm NOT giving you the wav filename!"); 
		} else if (format == 6) {
			fileName = SURESTREAM_FILENAME;
		}
		
		return fileName;
	}

	/**
	 * getHostName: Gets the right hostname using String containing a rawaudios.url field.
	 * @param url A String containing the contents of the rawaudios.url field.
	 * @returns The hostName
	 */
	public static String getHostName(String url) {	
		String FLIPSYMBOL = "F"; 
		String HOSTSYMBOL = "H"; 
		String hostName = new String();

		if (url.startsWith(FLIPSYMBOL)) {
			// Substring starting at H2=here ,thus 3 chars further.
			hostName = url.substring(3 + url.indexOf(HOSTSYMBOL+"2")); 
		} else {
			int h1Index = url.indexOf(HOSTSYMBOL+"2"); 
			int h2Index = url.indexOf(HOSTSYMBOL+"1"); 
			// Substring starting at H1=here ,thus 3 chars further. and 1 char before H2.
			hostName = url.substring(3 + h1Index, h2Index - 1); 
		}
		
		return hostName;
	}

}
