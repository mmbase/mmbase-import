/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat.repository;

import java.net.Socket;
import java.util.Collection;
import java.util.Date;

/**
 * The user interface represents a registered user.
 *
 * @author Jaco de Groot
 */
public interface User {

    public String getUsername();

    public String getRealname();
    
    public String getNick();

    public String getHostname();
    
    public Collection getChannels();

    public Socket getSocket();

    public boolean isOperator();

    public boolean isAdministrator();
    
    /**
     * Checks if the user is allowed to change his nick.
     *
     * @return  true if the user is allowed to change his nick, false
     *          otherwise.
     */
    public boolean isRestricted();

    // Will only be called if the current state differs from the new state.
    public void setOperator(boolean operator);

    // Will only be called if the current state differs from the new state.
    public void setAdministrator(boolean operator);

    public void setLastCommandRecieved(Date date);

    public Date getLastCommandRecieved();

}

