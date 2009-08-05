/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat.repository;

import java.util.Date;
import java.util.Collection;
import nl.eo.chat.InitializationException;
import nl.eo.chat.Logger;
import nl.eo.chat.ChatEngine;

/**
 * The repository holds the user repository and the channel repository.
 *
 * @author Jaco de Groot
 */
public interface Repository {
    
    public void init(ChatEngine engine) throws InitializationException;
    
    public UserRepository getUserRepository();
    
    public ChannelRepository getChannelRepository();
    
    /**
     * Returns the nummber of milliseconds before the chat server should open.
     *
     * @return  The nummber of milliseconds before the chat server should open,
     *          0 if the chat server should be open or -1 if the server is not
     *          scheduled to be open.
     */
    public long open(Date currentDate);
    
    /**
     * Returns the nummber of milliseconds before the chat server should close.
     *
     * @return  The nummber of milliseconds before the chat server should close,
     *           0 if the chat server should be closed or -1 if the server is not
     *          scheduled to be closed.
     */
    public long close(Date currentDate);
    
    /**
     * Returns a filter for all channels and private messages.
     *
     * @return  a filter for all channels and private messages or null if no
     *          filter should be active.
     */
    public Filter getFilter();
    
    /**
     * Returns a filter for nicknames.
     *
     * @return  a filter for nicknames or null if no
     *          filter should be active.
     */
    public Filter getNickFilter();    
    
}

