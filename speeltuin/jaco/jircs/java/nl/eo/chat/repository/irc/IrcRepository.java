/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat.repository.irc;

import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import nl.eo.chat.ChatEngine;
import nl.eo.chat.repository.*;
import nl.eo.chat.InitializationException;
import nl.eo.chat.Logger;

/**
 * Basic IRC implementation of the repository.
 *
 * @author Jaco de Groot
 */
public class IrcRepository implements Repository {
    protected IrcUserRepository userRepository;
    protected IrcChannelRepository channelRepository;
    protected Properties properties;

    public IrcRepository() {
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void init() throws InitializationException {
        userRepository = new IrcUserRepository();
        userRepository.setOperatorUsername((String)properties.get("operator.username"));
        userRepository.setOperatorPassword((String)properties.get("operator.password"));
        userRepository.setAdministratorUsername((String)properties.get("administrator.username"));
        userRepository.setAdministratorPassword((String)properties.get("administrator.password"));
        channelRepository = new IrcChannelRepository();
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public ChannelRepository getChannelRepository() {
        return channelRepository;
    }

    public Filter getFilter() {
        String badwords = "";
        String filterFile = (String)properties.get("filterFile");
        if (filterFile != null) {
            try {
                FileReader f = new FileReader(filterFile);
                try {
                    int charInt = f.read();
                    while (charInt != -1) {
                        badwords = badwords + (char) charInt;
                        charInt = f.read();
                    }
                } catch (IOException e1) {
                    ChatEngine.log.error("Error reading filterfile: " + filterFile);
                }
            } catch (FileNotFoundException e) {
                ChatEngine.log.error("Cannot find filterfile: " + filterFile);
                return null;
            }
            ChatEngine.log.info("Using filterfile: " + filterFile);
            ChatEngine.log.debug("The following words are being filtered:" + badwords);
        }
        return new IrcFilter(badwords);
    }

    public Filter getNickFilter() {
        String badNicks = "";
        String filterFile = (String)properties.get("nickNameFilterFile");
        if (filterFile != null) {
            try {
                FileReader f = new FileReader(filterFile);
                try {
                    int charInt = f.read();
                    while (charInt != -1) {
                        badNicks = badNicks + (char) charInt;
                        charInt = f.read();
                    }
                } catch (IOException e1) {
                    ChatEngine.log.error("Error reading nickNameFilterFile: " + filterFile);
                }
            } catch (FileNotFoundException e) {
                ChatEngine.log.error("Cannot find nickNameFilterFile: " + filterFile);
                return null;
            }
            ChatEngine.log.info("Using nickNameFilterfile: " + filterFile);
            ChatEngine.log.debug("The following nicknames are not allowed:" + badNicks);
        }
        return new IrcFilter(badNicks);
    }


    public long open(Date currentDate) {
        return 0;
    }

    public long close(Date currentDate) {
        return -1;
    }

}

