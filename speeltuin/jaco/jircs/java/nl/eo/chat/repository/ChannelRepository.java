/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat.repository;

import java.util.Collection;

/**
 * The channel repository holds all active channels.
 *
 * @author Jaco de Groot
 */
public interface ChannelRepository {
    final static int MODE_IRC = 0;
    final static int MODE_STRICT = 1;
    
    /**
     * Creates a channel with the specified name.
     */
    public Channel createChannel(String name);
    
    /**
     * Removes the channel with the specified name.
     */
    public void removeChannel(String name);

    /**
     * Returns a channel with the specified name.
     */
    public Channel getChannel(String name);

    /**
     * Returns all channels.
     */
    public Collection getChannels();

    /**
     * Returns all channels the specified user is on.
     */
    public Collection getChannels(User user);

}

