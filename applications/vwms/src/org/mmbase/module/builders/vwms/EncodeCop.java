/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.builders.vwms;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;

import org.mmbase.util.media.audio.*;
import org.mmbase.util.media.audio.audioparts.*;
import org.mmbase.util.StringTagger;
import org.mmbase.util.logging.*;

/**
 * The EncodeCop vwm observes changes on a certain type of nodes and activates
 * services by starting EncodeHandlers for that particular node.
 * 
 * If a certain audio file has to be ripped, then the EncodeCop is programmed 
 * to check for newly AudioPart changes of ctype 'n' where the 
 * audiopart.source is equal to the CD audiosource. 
 * If this is true then it reacts by starting the EncodeHandler for ripping CD's.
 *
 * The same goes for the encoding of ripped audio. For this, the EncodeCop checks 
 * for RawAudio changes of type 'c' or 'n' where the rawaudio.state is equals 
 * to 1 and where the RawAudio represents encoded audio (eg. type=Real G2).  
 * 
 * The last type of node thats being checked is the g2encoder service. 
 * Here we check if the state is waiting and its info field is empty.
 * If this is true, we make this g2encoder available for usage by adding it to 
 * a free services list.
 *
 * When an EncodeHandler is created, it is added to the EncoderHandlers list.
 * The EncodeHandlers are removed from this list when they finish or when an error occurs 
 * during handling.
 *
 * Recovery :
 * When the mmbase running this EncodeCop is being reset recovery is needed.
 * Why?, cause there could be services that are still busy or services 
 * that just signalled that they're finished. In both cases, after mmbase reboot 
 * there are no more EncodeHandlers active for those services, thus the related 
 * rawaudios representing ripped/encoded audio won't be updated anymore. 
 * 
 * Thus, after mmbase reset recovery is written for the followin 2 cases:
 * 1) Service was busy (eg. ripping or encoding)
 *    In this case the servicebuilder state is still 'busy' and the info field 
 *    should contain the 'id' tag, where the value represents the audiopart 
 *    being encoded.
 *
 * 2) Service just finished (eg. file ripped or encoded)
 *    In this case the servicebuilder state is 'waiting' and the info field 
 *    should contain the 'result' tag and the 'id' tag.
 * 
 * When all can be found an EncodeHandler is started that will actually handle 
 * the type of recovery needed for that particular service.
 *
 *
 * The following node changed types exist in mmbase (stored in ctype variable: 
 * passed ctype:
 * d: node deleted
 * c: node changed
 * n: new node
 * f: node field changed
 * r: node relation changed
 * x: some xml notify?
 * g: ? anyone...anyone...
 * s: ? anyone...anyone...
 * 
 * 
 * @author Daniel Ockeloen, David van Zeventer
 * @version $Revision: 1.20 $ $Date: 2001-05-11 07:57:49 $
 */
public class EncodeCop extends Vwm implements MMBaseObserver {
    private static Logger log = Logging.getLoggerInstance(EncodeCop.class.getName());

	Vector EncoderHandlers		= new Vector();

	// A list of free services from which EncodeHandlers will receive a service.
	private Vector freeservices = new Vector();	

	private boolean firstProbeCall = true;

	public EncodeCop() {
		log.info("EncodeCop started...");
	}

	/**
	 * Adds observers to audiopart, rawaudio and g2encoder changes.
	 * @return true, always.
	 */	
	public boolean probeCall() {
		if (firstProbeCall) {
			firstProbeCall = false;	
            log.info("Adding types: audioparts, rawaudios, g2encoders to observer list.");

			Vwms.mmb.addLocalObserver("audioparts",this);
			Vwms.mmb.addRemoteObserver("audioparts",this);

			Vwms.mmb.addLocalObserver("rawaudios",this);
			Vwms.mmb.addRemoteObserver("rawaudios",this);

			Vwms.mmb.addLocalObserver("g2encoders",this);
			Vwms.mmb.addRemoteObserver("g2encoders",this);
	
			g2encoders g2bul=(g2encoders)Vwms.mmb.getMMObject("g2encoders");
			cdplayers cdpbul=(cdplayers)Vwms.mmb.getMMObject("cdplayers");

			// Add all free g2encoder services to freeservices list.	
			addFreeServices(g2bul);

			// Recover for cdplayer en g2encoder services that were busy or that just finished during reset. 
			recoverForBusyServices(cdpbul);
			recoverForFinishedServices(cdpbul);
			recoverForBusyServices(g2bul);
			recoverForFinishedServices(g2bul);
		}
		return true;
	}


	/**
	 * Called when node was changed by remote mmbase, inturn this method calls nodeChanged method.
	 * @param machine Name of the machine that changed the node.
	 * @param number object number of node who's state has been changed remotely.
	 * @param builder a String with the buildername of the node that was changed remotely.
	 * @param ctype a String with the node change type.
	 * @return result value of nodeChanged call.
	 */
	public boolean nodeRemoteChanged(String machine,String number,String builder,String ctype) {
		log.debug("("+machine+","+number+","+builder+","+ctype+"): Calling nodeChanged to evaluate change.");
		return(nodeChanged(machine,number,builder,ctype));
	}

	/**
	 * Called when node was changed on local mmbase, inturn this method calls nodeChanged method.
	 * @param machine Name of the machine that changed the node.
	 * @param number object number of node who's state has been changed.
	 * @param builder a String with the buildername of the node that was changed.
	 * @param ctype a String with the node change type.
	 * @return result value of nodeChanged call.
	 */
	public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
		log.debug("("+machine+","+number+","+builder+","+ctype+"): Calling nodeChanged to evaluate change.");
		return(nodeChanged(machine,number,builder,ctype));
	}

	/**
	 * Checks node changetype and buildertype to decide which type of Changed method should be called.
	 * @param machine Name of the machine that changed the node.
	 * @param number object number of node who's state has been changed.
	 * @param builder a String with the buildername of the node that was changed.
	 * @param ctype a String with the node change type.
	 * @return true, always.
	 */
	public boolean nodeChanged(String machine,String number,String builder, String ctype) {
		log.debug("("+machine+","+number+","+builder+","+ctype+"): Checking for changes of type 'c' OR 'n' and buildertype.");
		if (ctype.equals("c") || ctype.equals("n")) {
			boolean result = false;
			if (builder.equals("audioparts"))
				result=audiopartChanged(machine,number,ctype);	
			else if (builder.equals("rawaudios"))
				result=rawaudioChanged(machine,number,ctype);
			else if (builder.equals("g2encoders"))
				result=g2encoderChanged(machine,number,ctype);
			if (!result)
				log.error("Couldn't check the audiopart for changes");
		}
		return true;
	}

	/**
	 * Checks the audiopart ctype and source value and when ok, it creates an 
	 * EncodeHandler with task 'cdrip' to rip for this new audiopart.
	 * @param machine Name of the machine that changed the node.
	 * @param number - a String containing the object nr of this audioparts node.
	 * @param ctype - a String with the node changed type.
	 * @return false when we can't the builder to get & check the audiopart, otherwise true .
	 * @param number
	 */
	public boolean audiopartChanged(String machine,String number,String ctype) {
		log.info("("+machine+","+number+","+ctype+"): Getting node and checking ctype and audiosource.");
		if (ctype.equals("n")) {
			AudioParts bul=(AudioParts)Vwms.mmb.getMMObject("audioparts");
			if (bul!=null) {
				try {
					MMObjectNode apNode=bul.getHardNode(Integer.parseInt(number));
					if(apNode.getIntValue("source") == AudioParts.AUDIOSOURCE_CD) {
						log.info("New audiopart source:CD, adding new EncodeHandler with task 'cdrip'");
						EncoderHandlers.addElement(new EncodeHandler(this,"cdrip",apNode));
					}
				} catch (NumberFormatException nfe) {
					log.error("Can't get node cause number:"+number+" is not an integer.");
					nfe.printStackTrace();
					return false;
				}
			} else {
				log.error("Can't get the AudioParts builder.");
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks the rawaudio ctype and state & format value and when ok, it creates an 
	 * an EncodeHandler with task 'g2encode' to encode for this rawaudio. 
	 * @param machine Name of the machine that changed the node.
	 * @param number - a String containing the object nr of this rawaudios node.
	 * @param ctype - a String with the node changed type.
	 * @return false when we can't the builder to get & check the rawaudio, otherwise true.
	 */
	public boolean rawaudioChanged(String machine,String number,String ctype) {
		log.debug("("+machine+","+number+","+ctype+"): Getting raNode and checking state & format.");
		RawAudios bul=(RawAudios)Vwms.mmb.getMMObject("rawaudios");		
		if (bul!=null) {
			try {
				//log.info("cached  NODE: "+bul.nodeCache.get(new Integer(number)));
				MMObjectNode raNode=bul.getHardNode(Integer.parseInt(number));
				//log.info("getHardNode NODE: "+raNode);
				int state=raNode.getIntValue("status"); //state is called status !?#
				int format=raNode.getIntValue("format");

				if (state==RawAudioDef.STATUS_VERZOEK && format==RawAudioDef.FORMAT_G2) {
					log.info("("+number+","+ctype+"): raNode state:"+state+" format:"+format+", adding new EncodeHandler, task 'g2encode'.");
					EncoderHandlers.addElement(new EncodeHandler(this,"g2encode",raNode));
				}
			} catch (NumberFormatException nfe) {
				log.error("Can't get node cause number:"+number+" is not an integer.");
				nfe.printStackTrace();
				return false;
			}
		} else {
			log.error("Can't get the RawAudios builder!");
			return false;
		}
		return true;
	}

	/**
	 * Checks if the g2encoder state is waiting and has an empty info field.
	 * If this is true that the g2encoder is added made avaiable again by adding it to 
	 * the freeservices list.
	 *
	 * @param machine Name of the machine that changed the node.
	 * @param number - a String containing the object nr of this g2encoders node.
	 * @param ctype - a String with the node changed type.
	 * @return false when we can't the builder or get & check the g2encoder, otherwise true.
	 */
	public boolean g2encoderChanged(String machine,String number,String ctype) {
		g2encoders g2bul=(g2encoders)Vwms.mmb.getMMObject("g2encoders");
		if (g2bul!=null) {
			try {
				MMObjectNode g2encnode = g2bul.getHardNode(Integer.parseInt(number));
				String state = g2encnode.getStringValue("state");
				String info  = g2encnode.getStringValue("info");
				String name  = g2encnode.getStringValue("name");
				// A service is available when state is waiting and the info field is empty.
				if (state.equals("waiting") && info.equals("")) {
					log.info("("+machine+","+number+","+ctype+"): Adding service "+name+" available for usage"); 
					addService(g2encnode.getIntValue("number"));	
				} else
					log.info("("+machine+","+number+","+ctype+"): Can't put service "+name+" available, state!=waiting ("+state+") info!='' ("+info+")");
			} catch (NumberFormatException nfe) {
				log.error("("+machine+","+number+","+ctype+"): Can't check service state & info cause number("+number+") isn't an int!");
				nfe.printStackTrace();
				return false;
			}
		} else {
			log.error("Can't get the g2encoders builder!");
			return false;
		}
		return true;
	}

	/**
	 * Removes EncodeHandler reference from the EncoderHandlers vector.
	 * @param h EncodeHandlers reference.
	 * @return a boolean result of the Vector remove. 
	 */
	public boolean removeEncodeHandler(EncodeHandler eh) {
		return EncoderHandlers.remove(eh);
	}

	/**
	 * Searches all services that are 'busy'. For every service found we check for the idtag in info field.
	 * The idtag contains the reference (objnr) to the audiopart for which service is busy.
	 * If the id can be found and has valid value, we search the related RawAudio and start a recovery 
	 * EncodeHandler.
	 * @param servicebul currently either a reference to the cdplayers or g2encoders builder.
	 */
	private void recoverForBusyServices(ServiceBuilder servicebul) {
        RawAudios rabul = (RawAudios) Vwms.mmb.getMMObject("rawaudios");
        int number=0; // service number
		int apnumber=0;
        Enumeration raenum = null;
		MMObjectNode servicenode = null;
		StringTagger infoTagger = null;
		// Initializing service vars.
		String task=null, busyWith=null;
		int raformat= 0;
		int rastate = RawAudioDef.STATUS_ONDERWEG; // busy 
		String idname = "id";

		// Defining service and audio vars.	
		if ((servicebul.getTableName()).equals("cdplayers")) {
			raformat = RawAudioDef.FORMAT_WAV;
			task = "recoverbusycdrip";
			busyWith = "recording";
		} else if ((servicebul.getTableName()).equals("g2encoders")) {
			raformat = RawAudioDef.FORMAT_G2;
			task = "recoverbusyg2encoder";
			busyWith = "encoding";
		}
		// Search for busy services and start recovery. 
		Enumeration e = servicebul.search("WHERE state='busy'");
		while (e.hasMoreElements()) {
			servicenode = (MMObjectNode)e.nextElement();
			number = servicenode.getIntValue("number");
			infoTagger = new StringTagger(servicenode.getStringValue("info"));
			if (infoTagger.containsKey(idname)) {
				try {
					apnumber = Integer.parseInt((String) infoTagger.Value(idname));
					log.info("Service: "+servicenode.getStringValue("name")+" is "+busyWith+" audiopart "+apnumber);
					log.info("Adding new EncodeHandler, task '"+task+"' for it.");
					raenum = rabul.search("WHERE id="+apnumber+" AND format="+raformat+" AND status="+rastate);
					if (raenum.hasMoreElements())
						EncoderHandlers.addElement(new EncodeHandler(this,task+":"+number,(MMObjectNode)raenum.nextElement()));
				} catch (NumberFormatException nfe) {
					log.error(idname+":"+infoTagger.Value(idname)+" is no int");
					nfe.printStackTrace();
				}
			} else
				log.error("Can't find "+idname+" tag in infofield for service:"+servicenode);
		}
	}

	/**
	 * Searches all services that are 'waiting'. For every service found we check for the 
	 * result tag and id tag in info field.
	 * The idtag contains the reference (objnr) to the audiopart for which service just finished.
	 * If the result and id can be found and has valid value, we search the related RawAudio 
	 * and start a recovery EncodeHandler.
	 * @param servicebul currently either a reference to the cdplayers or g2encoders builder.
	 */
	private void recoverForFinishedServices(ServiceBuilder servicebul) {
        RawAudios rabul = (RawAudios) Vwms.mmb.getMMObject("rawaudios");
        int number=0; // service number
		int apnumber=0;
        Enumeration raenum = null;

		MMObjectNode servicenode = null;
		StringTagger infoTagger = null;
		// Initializing service vars.
		String task=null, finishedWith=null, result=null;
		int raformat=0;
		int rastate = RawAudioDef.STATUS_ONDERWEG; // busy 
		String idname = "id";

		// Defining service and audio vars.	
		if ((servicebul.getTableName()).equals("cdplayers")) {
			raformat = RawAudioDef.FORMAT_WAV;
			task = "recoverfinishedcdrip";
			finishedWith = "recorded";
			result = "result";	
		} else if ((servicebul.getTableName()).equals("g2encoders")) {
			raformat = RawAudioDef.FORMAT_G2;
			task = "recoverfinishedg2encoder";
			finishedWith = "encoded";
			result = "result";	// info should also contain id but since subdir == id we now use subdir.
		}
		// Search for waiting services that just finished and start recovery. 
		Enumeration e = servicebul.search("WHERE state='waiting'");
		while (e.hasMoreElements()) {
			servicenode = (MMObjectNode)e.nextElement();
			number = servicenode.getIntValue("number");
			infoTagger = new StringTagger(servicenode.getStringValue("info"));
			if (infoTagger.containsKey(result) && infoTagger.containsKey(idname)) {
				try {
					apnumber = Integer.parseInt((String) infoTagger.Value(idname));
					log.info("Service: "+servicenode.getStringValue("name")+" just "+finishedWith+" audiopart "+apnumber);
					log.info("Adding new EncodeHandler, task '"+task+"' for it.");
					raenum = rabul.search("WHERE id="+apnumber+" AND format="+raformat+" AND status="+rastate);
					if (raenum.hasMoreElements())
						EncoderHandlers.addElement(new EncodeHandler(this,task+":"+number,(MMObjectNode)raenum.nextElement()));
				} catch (NumberFormatException nfe) {
					log.error(idname+":"+infoTagger.Value(idname)+" is no int");
					nfe.printStackTrace();
				}
			} else if ((!infoTagger.containsKey(result)) && (!infoTagger.containsKey(idname)))
				log.info("Service "+servicenode.getStringValue("name")+" "+finishedWith+" correctly.");
			else
				log.error("Info field misses either result or id key in service: "+servicenode);
		}
	}

	/**
	 * Gets the first mmserver connected to this Vwm.
	 * @return mmservername or null if it can't be found.
	 */
	public String getRelatedMMServerName() {
		log.debug("Get the mmserver that runs this code.");
		Enumeration e=Vwms.mmb.getInsRel().getRelated(wvmnode.getIntValue("number"),"mmservers");
		if (e.hasMoreElements()) {
			MMObjectNode mmserverNode = (MMObjectNode) e.nextElement();
			String name = mmserverNode.getStringValue("name");
			log.debug("Found mmserver: "+name);
			return name;
		} else {
			log.error("No mmserver found!");
			return null;
		}
	}


	/**
	 * Retrieves a service from a list of free services. 	
	 * If there aren't any, we wait until we are notified that there is one available, 
	 * which will then be retrieved.
	 * @return a free service.
	 */
    public synchronized int retrieveService() {
        if (freeservices.size()==0) {
            try {
                wait();
            } catch (InterruptedException ie) {
				ie.printStackTrace();
			}
        }
        return ((Integer)freeservices.remove(0)).intValue();
    }

	/**
	 * Adds service to the list of free services. 
	 * @param servicenumber that is added.
	 * @return true if service was deleted, false if service couldn't be found.
	 */
    public synchronized void addService(int servicenumber) {
        freeservices.add(new Integer(servicenumber));
        notify();
    }

	/**
	 * Removes the first occurence of this services from the list of free services. 
	 * This situaion occurs when servicenode is deleted by someone.
	 * @param servicenumber  that has to be removed.
	 * @return true if service was deleted, false if service couldn't be found.
	 */
	public synchronized boolean removeService(int servicenumber) {
		return freeservices.remove(new Integer(servicenumber));
	}

	/**
	 * Searches for free services, (these are services having a waiting state and an 
	 * empty info field), and add them to the list of free services.
	 * @param servicebul reference to servicebuilder type.
	 */
	private void addFreeServices(MMObjectBuilder servicebul) {
		Enumeration e = servicebul.search("WHERE state='waiting' AND info=''");
		MMObjectNode service = null;
		while (e.hasMoreElements()) {
			service = (MMObjectNode) e.nextElement();
			log.info("Adding service "+service.getStringValue("name")+" available for usage"); 
			addService(service.getIntValue("number"));
		}
	} 

	/**
	 * Returns contents of freeservices list as a String.
	 * @return contents of freeservices list as a String.
	 */
	public synchronized String getContentsOfFreeServicesList() {
		return freeservices.toString();
	}
}
