/*
$Id: LStreams.java,v 1.4 2000-03-29 10:59:23 wwwtech Exp $

$Log: not supported by cvs2svn $
Revision 1.3  2000/02/24 14:37:31  wwwtech
Davzev added: Server restarted debug in init() method.

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author David V van Zeventer
 * @version 4 nov 1998
 * @Revision@ $Date: 2000-03-29 10:59:23 $
 */
/**
 * NOTE 29 JAN 1999: The key in the lstreams hashtable consists of nodes' channelname only, instead of
 *                   the nodes' channelname AND machinename.
 *                   With this code it's not possible to use 2 different machines broadcast the same channel.
 *
 * 		     CHange all lstreams hashtable calls with key "channelname" to key "channelname+machinename". 
 */
public class LStreams extends MMObjectBuilder {

	public final static int ON_AIR  = 2;	// Broadcast is now on the air.
	public final static int OFF_AIR = 1;	// Broadcast is now off the air.
	Hashtable lstreams_ht = new Hashtable(); // Create a LStreams hashtable
	public final static String buildername = "-LStreams";	
	
	 
	public boolean init() {	//This method is called when the server has been restarted.
		String methodname = "init";
		if (super.init()) {
			System.out.println(buildername+": "+methodname+": Server has been restarted.");	

			MMObjectNode lstreamsnode=null;  	//Temporary lstreamsnode containing al the DB fields.
			String channelname=null;
			String streamsoptions=null;
			Hashtable RALEobjects_ht= null;	//See if there are any objnodes in DB, if so then fill lstreams hashtable with their RALEobjects hashtable.
			int broadcaststate;
			boolean reset_while_on_air = false;	//initial value = false.

			// not all but only the ones for this machine like search('where machine='noise');
			Enumeration e=search("WHERE machine='"+getMachineName()+"'");	
			while (e.hasMoreElements()){
				System.out.println(buildername+": "+methodname+": Machine "+getMachineName()+" receives new LStreams hashtable.");

				lstreamsnode = (MMObjectNode)e.nextElement();	//Retrieve lstreamsnode.
				channelname = lstreamsnode.getStringValue("channel");
				RALEobjects_ht = new Hashtable();	//Create new empty RALEobjects hashtable.
				lstreams_ht.put(channelname,RALEobjects_ht); //29 JAN 1999 SEE NOTE above class definition. 

				broadcaststate = lstreamsnode.getIntValue("state");
				if (broadcaststate == ON_AIR) {
					reset_while_on_air = true;
					System.out.println(buildername+": "+methodname+": WARNING: The LStreamsnode for this machine for channel: "+channelname+" got a broadcaststate value: " +broadcaststate+" -> THIS MEANS THAT THE LIVE ENCODERS MUST BE STARTED AGAIN.");
					streamsoptions=lstreamsnode.getStringValue("streamsoptions");	//Get StreamsOptions from DB
					if (streamsoptions == null) {	//There are no streamsoptions given.
						System.out.println(buildername+": "+methodname+": No streamsoptions found for "+channelname);
					}
					try {
						parseStreamsOptions(streamsoptions,RALEobjects_ht,reset_while_on_air);//Parse StreamsOptions to fill the RALEObjects hashtable ,
					}catch (InvalidStreamsOptions isoe) { 			 
					 	//Error occured in method parseStreamsOptions()
						System.out.println(buildername+": "+methodname+": ERROR: parseStreamsOptions failed " +isoe);
					}
				}else {
					if (broadcaststate == OFF_AIR) {
						System.out.println(buildername+": "+methodname+": The LStreamsnode for this machine for channel: "+channelname+" got a broadcaststate value: " +broadcaststate+".");
					} else {	//invalid broadcaststate
						System.out.println(buildername+": "+methodname+": ERROR: The LStreamsnode for this machine for channel: "+channelname+" got a invalid broadcaststate value: " +broadcaststate+".");
						return false;
					}
				}
			}
			return true;
		} else {
			System.out.println(buildername+": "+methodname+": Cannot create MMObjectBuilder LStreams (init() method failed).");
			return false;
		}
	}

	/**
	* insert a new object, normally not used (only subtables are used)
	*/
	/*
	public int insert(String owner,MMObjectNode node) {
		String channel=node.getStringValue("channel");
		String machine=node.getStringValue("machine");
		int state=node.getIntValue("state");
		String url=node.getStringValue("url");
		String suser=node.getStringValue("suser");
		String spassword=node.getStringValue("spassword");
		String streamsoptions=node.getStringValue("streamsoptions");


		if (channel==null) channel="";
		if (machine==null) machine="";

		int number=getDBKey();
		if (number==-1) return(-1);
		try {
			MultiConnection con=mmb.getConnection();
			PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_"+tableName+" values(?,?,?,?,?,?,?,?,?,?)");
				stmt.setInt(1,number);
				stmt.setInt(2,oType);
				stmt.setString(3,owner);
				stmt.setString(4,channel);
				stmt.setString(5,machine);
				stmt.setInt(6,state);
				stmt.setString(7,url);
				stmt.setString(8,suser);
				stmt.setString(9,spassword);
				stmt.setString(10,streamsoptions);

				stmt.executeUpdate();
				stmt.close();
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error on : "+number+" "+owner+" fake");
			return(-1);
		}
		signalNewObject(tableName,number);
		return(number);	
	}
	*/

	public String getGUIIndicator(MMObjectNode node) {
        	String str=node.getStringValue("channel");
	        if (str.length()>31) {
	            return(str.substring(0,12)+"...");
	        } else {
	            return(str);
	        }
    	}

	/**
	* insert a new object, normally not used (only subtables are used)
	*/
	public boolean commit(MMObjectNode node) {
		if (super.commit(node)) {	
			if(checkBroadcastState(node)){
				return true;
			}else {
				return false;
			}
		}else {	//Insert RALiveobject failed.
			System.out.println(buildername+": Committing new LStreamsnode in DB failed");
			return false; //Let the commit fail.
		}
	}		
	
	
	/**
	 * checkBroadcastState: Checks current nodes' broadcaststate and starts/stops their RALEencoder threads.
	 */			         
	boolean checkBroadcastState(MMObjectNode node) {
		String methodname = "checkBroadcastState";
		String streamsoptions=null;
		Hashtable RALEobjects_ht=null;
		boolean reset_while_on_air=false;	//initial value = false;

		System.out.println(buildername+": "+methodname+": lstreams hashtable contains: " + lstreams_ht);
		String channelname = node.getStringValue("channel");	//Get channelname
		if(lstreams_ht.containsKey(channelname)) {
			System.out.println(buildername+": "+methodname+": Key channelname: "+channelname+" already in lstreams hashtable -> Getting value (a RALEobjects hashtable)."); 
			RALEobjects_ht = (Hashtable) lstreams_ht.get(channelname);
		}else{
			RALEobjects_ht = new Hashtable();  	//Create a RALEobjects hashtable 
			System.out.println(buildername+": "+methodname+": Key Channelname: "+channelname+" not in lstreams hashtable -> Adding entry to LStreams hashtable."); 
			lstreams_ht.put(channelname, RALEobjects_ht);	//Add the RALEobjects hashtable to lstreams_ht.
		}

		int broadcaststate=node.getIntValue("state");
		if (broadcaststate == ON_AIR) {	//Start liveencoders.
			//System.out.println(buildername+": "+methodname+": broadcaststate set to :"+broadcaststate+".");
			streamsoptions=node.getStringValue("streamsoptions");	//Get StreamsOptions from DB
			if (streamsoptions == null) {	//There are no streamsoptions given.
				return false;
			}
			try {
				this.parseStreamsOptions(streamsoptions,RALEobjects_ht,reset_while_on_air);//Parse StreamsOptions-> will fill RALEObjectsHashtable 
			}catch (InvalidStreamsOptions isoe) {
			 	//Error occured in method parseStreamsOptions()
				System.out.println(buildername+": "+methodname+": ERROR: parseStreamsOptions failed " +isoe);
				return false;
			}								  
			return true;
		}else {
			if (broadcaststate == OFF_AIR) {	//Stop the LiveEncoder(S).
				//System.out.println(buildername+": "+methodname+": broadcaststate set to: "+broadcaststate+".");
				RALiveEncoder tmpRALE=null;
			  	String tmpchannelname;		//Get all RALEobjects from and stop them. 
		  		//System.out.println(buildername+": "+methodname+": For lstreamsnode: "+channelname+" I am now removing following entries from his RALEobjects hashtable: "+RALEobjects_ht);
				for (Enumeration e=RALEobjects_ht.keys() ; e.hasMoreElements();) {
					tmpchannelname = (String)e.nextElement();
						
					//The enc_on semaphore and the small_ep_distance is also stored in RALEobjects_ht.
					if ( !(tmpchannelname.equals("enc_on") || tmpchannelname.equals("small_ep_distance")) ){

						//System.out.println(buildername+": "+methodname+": Stopping stream: "+ tmpchannelname +".");
						tmpRALE = (RALiveEncoder)RALEobjects_ht.get(tmpchannelname);
						tmpRALE.stop();
					}
        			}
				RALEobjects_ht.clear();	//Remove all keys and elements from his RALEobjects hashtable.
				lstreams_ht.put(channelname, RALEobjects_ht);	//Adding empty RALEobjects hashtable for this objectnode to lstreams hashtable.
			}else {		// Invalid broadcaststate.
				System.out.println(buildername+": "+methodname+": ERROR in objectnode: "+channelname+" -> state field :" + broadcaststate +" is an invalid broadcaststate value.");
				return false;
			}
			return true;
		}
	}
		
		
	
	/**
	* parseStreamsOptions : This method parses streamsoptions. After a streamsoption is parsed, 
	*			a RALiveobject is created and RALiveobjects hashtable is updated.
	*			A streamsoptions example: BEGIN TR_20			<-streamname
	*						  scriptname=/root/huidig/rmenc-live
	*						  //encodername=/usr/bin/RALenc.exe
	*						  //configname=/etc/RALE_20.cfg
	*						  END
	*						  BEGIN TR_40
	*						  ...
	*/	
	public void parseStreamsOptions(String streamsoptions,Hashtable RALEobjects_ht, boolean reset_while_on_air) throws InvalidStreamsOptions{
		String methodname = "parseStreamsOptions";
		StringTokenizer lines_st=null;			//Will tokenize text into seperate lines.
		StringTokenizer words_st=null;			//Will tokenize line into seperate words.

		String beginprefix	= "BEGIN";		//Compare strings used during parsing process.
		String beginsuffix	= "STR_";
		String endprefix	= "END";

		String startscriptprefix= "startscriptname";
		String startscriptsuffix= "/";			//rmenc.start
		String stopscriptprefix	= "stopscriptname"; 
		String stopscriptsuffix= "/";			//rmenc.stop 	
		String line=null;				//will hold 1 streamsoptions textline.
		String streamname=null;				//Means the name of the stream being encoded-> TR_40 40kb/s stream

		String startscriptname=null;
		String stopscriptname=null;
	
		Hashtable soptions_ht= new Hashtable();		//temporary hashtable holding encoder+config name.

		lines_st = new StringTokenizer(streamsoptions, "\n\r");	//tokenize textinto seperate lines
	       	while (lines_st.hasMoreTokens()) {

			line = lines_st.nextToken();
			//System.out.println(buildername+": "+methodname+": Now parsing line:" +line);
			if (line.startsWith(beginprefix)) {
				//System.out.println(buildername+": "+methodname+": beginprefix found.");
				words_st = new StringTokenizer(line, " ");	//tokenize BEGIN line.
				streamname = words_st.nextToken();			
				streamname = words_st.nextToken();			
				if (streamname.startsWith(beginsuffix)) {
					//System.out.println(buildername+": "+methodname+": beginsuffix found.");
				}else { //There's no|invalid beginsuffix.
					System.out.println(buildername+": "+methodname+": No/Invalid streamname entered in beginstatement.");
					throw new InvalidStreamsOptions();
				}					
			} else {
				if(line.startsWith(startscriptprefix)){
					//System.out.println(buildername+": "+methodname+": startscriptprefix found.");
					words_st = new StringTokenizer(line, "=");	//tokenize scriptline
					startscriptname = words_st.nextToken();
					startscriptname = words_st.nextToken();
					if (startscriptname.startsWith(startscriptsuffix)) {
						//System.out.println(buildername+": "+methodname+": startscriptsuffix found.");
						//System.out.println(buildername+": "+methodname+": Adding startscriptname: "+startscriptname+" to soptions hashtable.");
						soptions_ht.put(startscriptprefix, startscriptname);
					} else {// There's no|invalid startscriptsuffix
						System.out.println(buildername+": "+methodname+": No/Invalid startscriptname entered in startscriptname statement.");
						throw new InvalidStreamsOptions();
					}
				} else {
					if (line.startsWith(stopscriptprefix)){
						//System.out.println(buildername+": "+methodname+": stopscriptprefix found.");
						words_st = new StringTokenizer(line, "=");	//tokenize scriptline
						stopscriptname = words_st.nextToken();
						stopscriptname = words_st.nextToken();
						if (stopscriptname.startsWith(stopscriptsuffix)) {
							//System.out.println(buildername+": "+methodname+": stopscriptsufix found.");
							//System.out.println(buildername+": "+methodname+": Adding stopscriptname: "+stopscriptname+" to soptions hashtable.");
							soptions_ht.put(stopscriptprefix, stopscriptname);
						} else { // There's no|invalid stopscriptsuffix
							System.out.println(buildername+": "+methodname+": No/Invalid stopscriptname entered in stopscriptname statement.");
							throw new InvalidStreamsOptions();
						} 
					} else {
						if (line.startsWith(endprefix)){ //Now you can Create a RALEobject and Update the Hashtable
							//System.out.println(buildername+": "+methodname+": endprefix found.");
							//Checking if object already exists
							if (RALEobjects_ht.containsKey(streamname)) {
									System.out.println(buildername+": "+methodname+": Warning stream "+streamname+" already exists --> Replacing old stream with new streamsettings.");
									RALEobjects_ht.remove(streamname);
							}
							//Creating RALiveobject
							RALiveEncoder tempRALEobj = new RALiveEncoder(streamname,soptions_ht,reset_while_on_air);
							//Putting object in RALEobject hashtable
							System.out.println(buildername+": "+methodname+": Adding RALEobject: "+streamname+" to the RALEobjects hashtable of lstreamsnode.");
							RALEobjects_ht.put(streamname,tempRALEobj);
							//Creating new soptions hashtable for if soptions contains more streams.
							soptions_ht = new Hashtable();
						}else { //There's no|invalid statement prefix
							System.out.println(buildername+": "+methodname+": Streamsoptions contains incorrect prefix statement, statement used: "+line+" .");
							throw new InvalidStreamsOptions();
						}
					}
				}
			}	
		}//while
	}
	
	
	/** 
	 * updateEnconSemaphore: This method will update enc_on semaphore, to tell that an episode has started.
	 * 			  The semaphore value also means how many episodes/encoders are running.
	 *			  This situation happens when an episode begins when another episode hasn't ended yet.
	 *			  From the encoders viewpoint since theres only 1 per LStreamsnode this means that the
	 *			  encoders just stays on.
	 */	
	public synchronized Integer updateEnconSemaphore(int direction, MMObjectNode node){
		String methodname = "updateEnconSemaphore";
		int enc_on=0;
		Integer enc_on_iobj=null;
		Hashtable RALEobjects_ht=null;

		String channelname = node.getStringValue("channel");	//Retrieve lstreamsnodes' channelname. 
		
		if(lstreams_ht.containsKey(channelname)) {
 		 	System.out.println(buildername+": "+methodname+": Key "+channelname+" found in lstreams hashtable.");
			RALEobjects_ht = (Hashtable) lstreams_ht.get(channelname);	//Get the right RALEobjects hashtable.
			//System.out.println(buildername+": "+methodname+": Value for key "+channelname+" = "+RALEobjects_ht);

			if (direction==1) {	//increase semaphore.

				if (RALEobjects_ht.containsKey("enc_on")) {	//Semaphore already exists
					System.out.println(buildername+": "+methodname+": enc_on semaphore found.");
					enc_on_iobj = (Integer) RALEobjects_ht.get("enc_on");
					enc_on= enc_on_iobj.intValue();
					enc_on++;	//Increasing semaphore.
				} else {	//Semaphore doesn't exist in RALEobjects_ht.
					System.out.println(buildername+": "+methodname+": enc_on semaphore doesn't exist, now entering it with value=1.");
					enc_on=1;	//Set enc_on on 1.
				}

				enc_on_iobj = new Integer(enc_on);	//Create new Integerobject named enc_on_iobj. 
				RALEobjects_ht.put("enc_on", enc_on_iobj);
			} else {		//decrease semaphore.

				if (RALEobjects_ht.containsKey("enc_on")) {	//Semaphore already exists.
					enc_on_iobj = (Integer) RALEobjects_ht.get("enc_on");	//Get semaphore iobj.
					enc_on= enc_on_iobj.intValue();
					if (enc_on>0) {
						enc_on--;	//Decreasing semaphore.
					}else {
						System.out.println(buildername+": "+methodname+": Warning, decreasing enc_on semaphore will cause semvalue to become negative!!! i'm not doing that ...now way--> instead setting enc_on value back to 0.");
						enc_on=0;	//semaphore value back to initial value 0.
					}	
				} else {	//Semaphore doesn't exist in RALEobjects_ht.
					System.out.println(buildername+": "+methodname+": Warning: updateEnconSemaphore method detects that enc_on semaphore variable doesn't exist yet."); 
					//System.out.println(buildername+": "+methodname+": This means 1:server has been reset right before stopbroadcasttask occured.");
					//System.out.println(buildername+": "+methodname+": OR         2:A stopbroadcasttask occured before a startbroadcast task has started.");
					System.out.println(buildername+": "+methodname+": Now entering enc_on semaphore with initial value set to: 0.");
					enc_on=0;	//Set enc_on on 1.
				}
				
				enc_on_iobj = new Integer(enc_on);	//Create new Integerobject named enc_on_iobj. 
				RALEobjects_ht.put("enc_on", enc_on_iobj);
			}
		} else{
			System.out.println(buildername+": "+methodname+": ERROR Can't find Channelname: "+channelname+" in lstreams hashtable, semaphore can't be entered for an unknown channel.");
		}

		return enc_on_iobj;	//return semaphore object.	
	}
	
	/**
	 * getEnconSemaphore: This method returns the enc_on semaphore intvalue
	 */
	public Integer getEnconSemaphore(MMObjectNode node) {
		String methodname = "getEnconSemaphore";
		int enc_on=0;
		Integer enc_on_iobj=null;
		Hashtable RALEobjects_ht=null;

		System.out.println(buildername+": "+methodname+": Getting enc_on semaphore from RALEobjects hashtable.");
		String channelname = node.getStringValue("channel");	//Retrieve lstreamsnodes' channelname. 
		if(lstreams_ht.containsKey(channelname)) {
			enc_on_iobj = (Integer) RALEobjects_ht.get("enc_on");	//Get semaphore iobj from RALEobjects_ht
			enc_on= enc_on_iobj.intValue();
		} else{
			System.out.println(buildername+": "+methodname+": ERROR Can't find Channelname: "+channelname+" in lstreams hashtable, can't retrieve semaphore for an unknown channel!"); 
		}
		return enc_on_iobj;
	}

	/** 
	 * putSmallEpDistance: stores booleanvalue in the RALEobjects hashtable which is stored in the
	 * 		       LStreams hashtable.
	 * 		       Returns 1 when there wasn't any small_ep_distance object in RALEobjects hashtable
 	 *		       where it creates a small_ep_distance with the value determined.
	 *		       Returns 0 when no problems occur. 
	 */
	public synchronized int putSmallEpDistance(Boolean small_ep_distance_bobj, MMObjectNode node) {
		String methodname = "putSmallEpDistance";
		Hashtable RALEobjects_ht=null;

		String channelname = node.getStringValue("channel");	//Retrieve lstreamsnodes' channelname. 

		if(lstreams_ht.containsKey(channelname)) {
			System.out.println(buildername+": "+methodname+": Channelname "+channelname+" found in lstreams hashtable.");
			RALEobjects_ht = (Hashtable) lstreams_ht.get(channelname);	//Get the right RALEobjects hashtable.
			if (RALEobjects_ht.containsKey("small_ep_distance")) {	//See if small_ep_distance already exists in RALEobjects_ht.
				System.out.println(buildername+": "+methodname+": small_ep_distance found, value: "+ ((Boolean) RALEobjects_ht.get("small_ep_distance")).booleanValue()); // get small_ep_distance object from hashtable and return boolean value. 
			} else {
				System.out.println(buildername+": "+methodname+": Warning: putSmallEpDistance method detects that small_ep_distance_bobj variable doesn't exist yet."); 
				System.out.println(buildername+": "+methodname+": This means 1:server has been reset right before stopbroadcasttask occured.");
				System.out.println(buildername+": "+methodname+": OR         2:A stopbroadcasttask occured before a startbroadcast task has started.");
				System.out.println(buildername+": "+methodname+": Now entering small_ep_distance variable with the value determined: "+small_ep_distance_bobj.booleanValue()+" in RALEobjects hashtable.");
				RALEobjects_ht.put("small_ep_distance", small_ep_distance_bobj);
				return 1;	//1 meaning that the server has been reset during a broadcast or a stoptask occured before a starttask.
			} 

			RALEobjects_ht.put("small_ep_distance", small_ep_distance_bobj);
			System.out.println(buildername+": "+methodname+": New value for small_ep_distance: "+small_ep_distance_bobj.booleanValue());
			return 0;	//0 no problem has occured.
		} else{
			System.out.println("-LStreams> putSmallEpDistance: ERROR Can't find Channelname: "+channelname+" in lstreams hashtable, small_ep_distance in RALEobjects hashtable retains original value.");
			return 0;	//Although a problem did occured i'm returning 0 small_ep_distance retains original value. 
		}
	}

	/**
	 * getSmallEpDistance: Retrieves booleanvalue from the RALEobjects hashtable which is stored in the
	 *		       LStreams hashtable.
	 */
	public Boolean getSmallEpDistance(MMObjectNode node) {
		String methodname = "getSmallEpDistance";
		Hashtable RALEobjects_ht=null;
		Boolean small_ep_distance_bobj= null;

		String channelname = node.getStringValue("channel");	//Retrieve lstreamsnodes' channelname. 
		System.out.println(buildername+": "+methodname+": Performing for channel "+channelname+".");
		
		if(lstreams_ht.containsKey(channelname)) {
			//System.out.println(buildername+": "+methodname+": Channelname "+channelname+" found in lstreams hashtable.");
			RALEobjects_ht = (Hashtable) lstreams_ht.get(channelname);	//Get the right RALEobjects hashtable.
			small_ep_distance_bobj = (Boolean) RALEobjects_ht.get("small_ep_distance");
			if(small_ep_distance_bobj == null) {
				System.out.println(buildername+": "+methodname+": This is the first starttask using getSmallEpDistance, now adding to RALEobjects hashtable with initialvalue set to: false.");
				small_ep_distance_bobj= new Boolean(false);	//Create new Booleanobject named small_ep_distance_bobj.
				RALEobjects_ht.put("small_ep_distance", small_ep_distance_bobj);
			} else {
				System.out.println(buildername+": "+methodname+": small_ep_distance found, value: "+small_ep_distance_bobj.booleanValue());
			}
		} else{
			System.out.println(buildername+": "+methodname+": ERROR Can't find Channelname: "+channelname+" in lstreams hashtable, can't retrieve small_ep_distance from an unknown channel.");
		}
		return small_ep_distance_bobj;
	}

}


class InvalidStreamsOptions extends Exception {
}
