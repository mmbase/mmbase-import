/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.applications.media.builders;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.core.MMObjectNode;

/**
 *
 * The AudioSource builder describes a specific type of audio that can be retrieved (real/mp3/etc). Information about
 * format, quality, and status will be maintained in this object. An AudioSource belongs
 * to a MediaFragement that describes the piece of media, the AudioSource is the
 * actually audio itself. An AudioSource is connected to provider objects that indicate
 * where the audio files can be found.
 *
 * @author Rob Vermeulen
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.7
 */
public class AudioSources extends MediaSources {    
    private static Logger log = Logging.getLoggerInstance(AudioSources.class);
    
    /**
     * Creates a new audiosource and relates it to an audiofragment.
     *
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
        insrel.setValue("rnumber", mmb.getInsRel().getNumber());
        int insrelnumber = insrel.insert(owner);
        if(insrelnumber<0) {
            log.error("Cannot create relation between audiosource and audiofragment.");
        } else {
            log.debug("New relation between audiosource and audiofragment is created.");
        }
        return node;
    }
}
