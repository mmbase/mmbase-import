/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat.repository;

import java.util.Collection;
import java.util.Date;
import java.util.Vector;

/**
 * The channel interface represents a chat channel.
 *
 * @author Jaco de Groot
 */
public interface Channel {
    public static final int MODERATED_MODE_UNCHANGED = 0;
    public static final int MODERATED_MODE_ON = 1;
    public static final int MODERATED_MODE_OFF = 2;
    
    public Vector getUsers();

    /**
     * Adds a new user to the channel.
     */
    public void addUser(User user);
    
    /**
     * Removes a user from the channel.
     */
    public void removeUser(User user);

    public void addBan(String ban);

    public void removeBan(String ban);

    public boolean containsBan(String ban);

    public boolean isBanned(User user);

    public Collection getBans();
    
    public String getName();
    
    public boolean containsUser(User user);

    public int getNumberOfUsers();

    public int getNumberOfVisibleUsers();
    
    /**
     * Set a (new) topic for the channel.
     *
     * @param topic  the topic for the channel or null if the topic for the
     *               channel should be removed.
     */
    public void setTopic(String topic);

    /**
     * Returns the topic for the channel.
     *
     * @return  the topic for the channel or null if the channel has no topic.
     */
    public String getTopic();

    // Is het mogelijk true terug te geven als user niet op kanaal zit?
    // Is het mogelijk dat deze methode aangeroepen wordt terwijl gebruiker niet op kanaal?
    public boolean isOperator(User user);

    /**
     * Change the operator status of a user. This method will only be called in
     * case of a change and only for users who are on the channel.
     */
    public void setOperator(User user, boolean operator);

    /**
     * Check the moderated flag of the channel.
     */
    public boolean isModerated();

    /**
     * Set the moderated flag of the channel. For newly created
     * channels this flag should not be set. The chat engine will call this
     * method after creation if it is configured that new channels should
     * have the moderated flag set.
     */
    public void setModerated(boolean moderated);
    
    /**
     * This method is called by the chat engine at certain intervals to check
     * if the moderated mode should be changed. This gives a channel the
     * possibility to have "working hours".  If a channel should always be open
     * it should always return MODERATED_MODE_UNCHANGED. If a channel should
     * switch to moderated mode at 22:00 it should return MODERATED_MODE_ON the
     * first time it is called after 22:00. The channel must remember the last
     * time it was called and return MODERATED_MODE_ON or MODERATED_MODE_OFF
     * only one time for every switch from on to off or off to on.
     * The first time this method is called it should return MODERATED_MODE_ON
     * or MODERATED_MODE_OFF of it has "working hours" or
     * MODERATED_MODE_UNCHANGED if it should always be open.
     */
    public int moderatedChange(Date currentDate);

    /**
     * Check the mode n flag: no messages to channel from clients on the
     * outside.
     */
    public boolean noOutsideMessages();
    
    /**
     * Set the mode n flag: no messages to channel from clients on the
     * outside. For newly created channels this flag should not be set. The
     * chat engine will call this method after creation if it is configured that
     * new channels should have this flag set.
     */
    public void setNoOutsideMessages(boolean noOutsideMessages);

    /**
     * Check the mode t flag: topic settable by channel operator only.
     */
    public boolean topicProtection();

    /**
     * Set the mode t flag: topic settable by channel operator only. For newly
     * created channels this flag should not be set. The chat engine will call
     * this method after creation if it is configured that new channels should
     * have this flag set.
     */
    public void setTopicProtection(boolean protect);
    
    /**
     * Returns the maximum number of users for the channel.
     *
     * @return  the maximum number of users for the channel or -1 if no limit
     *          is set.
     */
    public int getUserLimit();
    
    /**
     * Set the maximum number of users for the channel. For newly created
     * channels this limit should not be set. The chat engine will call this
     * method after creation of the new channel if a default user limit for new
     * channels is configured.
     *
     * @param maximum  the new userlimit or -1 if the user limit should be
     *                 removed.
     */
    public void setUserLimit(int maximum);

    /**
     * Check the mode d flag: delete channel when last user leaves.
     */
    public boolean deleteWhenLastUserLeaves();

    /**
     * Set the mode d flag: delete channel when last user leaves. For newly
     * created channels this flag should not be set. The chat engine will call
     * this method after creation if it is configured that new channels should
     * have this flag set. 
     */
    public void setDeleteWhenLastUserLeaves(boolean delete);

}

