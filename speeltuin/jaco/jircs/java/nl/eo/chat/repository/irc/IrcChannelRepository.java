/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat.repository.irc;

import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import nl.eo.chat.repository.*;

/**
 * Basic IRC implementation of a channel repository.
 *
 * @author Jaco de Groot
 */
public class IrcChannelRepository implements ChannelRepository {
    protected HashMap channels = new HashMap(); // channel name (in lower case) -> channel
    
    public IrcChannelRepository() {
    }

    public Channel createChannel(String name) {
        Channel channel = new IrcChannel(name);
        channels.put(name.toLowerCase(), channel);
        return channel;
    }
    
    public void removeChannel(String name) {
        channels.remove(name.toLowerCase());
    }

    public Channel getChannel(String name) {
        return (Channel)channels.get(name.toLowerCase());
    }

    public Collection getChannels() {
        return channels.values();
    }

    public Collection getChannels(User user) {
        Vector channels = new Vector();
        Iterator iterator = getChannels().iterator();
        while (iterator.hasNext()) {
            Channel channel = (Channel)iterator.next();
            if (channel.containsUser(user)) {
                channels.add(channel);
            }
        }
        return channels;
    }

}

