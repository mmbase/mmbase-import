/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.module.builders.media;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.core.MMObjectNode;

/**
 */
public class AudioSources extends MediaSources {
    
    private static Logger log = Logging.getLoggerInstance(AudioSources.class.getName());
    
    /**
     * creates a new audiosource and relates it to an audiofragment.
     * @param audiofragment the audiofragment to relate this new audiosource to.
     * @return The new created audiofragment.
     */
    public MMObjectNode addAudioSource(MMObjectNode audiofragment, int format, int codec, int bitrate, int channels, String url, int state, String owner) {
                
	MMObjectNode node=getNewNode(owner);		
	node.setValue("format",format);
	node.setValue("codec",codec);
	node.setValue("bitrate",bitrate);
	node.setValue("channels",channels);
	node.setValue("url",url);
        node.setValue("state",state);
	int audioSourceNumber = insert(owner,node);
        if(audioSourceNumber<0) {
            log.error("Cannot create a new audiosource.");
            return null;
        } else {
            log.debug("New audiosource created.");
        }
        
        // create relation between audiofragment and new audiosource
        MMObjectNode insrel = mmb.getInsRel().getNewNode(owner);
        insrel.setValue("snumber", audiofragment.getValue("number"));
        insrel.setValue("dnumber", audioSourceNumber);
        insrel.setValue("rnumber", mmb.getInsRel().oType);
        int insrelnumber = insrel.insert(owner);
        if(insrelnumber<0) {
            log.error("Cannot create relation between audiosource and audiofragment.");
        } else {
            log.debug("New relation between audiosource and audiofragment is created.");
        }
        return node;
    }
}