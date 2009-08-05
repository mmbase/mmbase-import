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
    protected String filterFile;
    protected String nickFilterFile;
    protected ChatEngine engine;

    public void init (ChatEngine engine) throws InitializationException{
        this.engine = engine;
        engine.log.debug("Initializing IrcRepository");
        userRepository.init(engine);
    }
    
    public void setFilterFile(String file) {
        filterFile = file;
    }
 
    public void setNickFilterFile(String file) {
        nickFilterFile = file;
    }
    
    public IrcRepository() {
        userRepository = new IrcUserRepository();
        channelRepository = new IrcChannelRepository();
    }
    
    public void setOperatorUsername(String name) {
        userRepository.setOperatorUsername(name);
    }

    public void setOperatorPassword(String pass) {
        userRepository.setOperatorPassword(pass);
    }

    public void setAdministratorUsername(String name) {
        userRepository.setAdministratorUsername(name);
    }
    
    public void setAministratorPassword(String pass) {
        userRepository.setAdministratorPassword(pass);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public ChannelRepository getChannelRepository() {
        return channelRepository;
    }

    public Filter getFilter() {
        String badwords = "";
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
                    engine.log.error("Error reading filterfile: " + filterFile);
                }
            } catch (FileNotFoundException e) {
                engine.log.error("Cannot find filterfile: " + filterFile);
                return null;
            }
            engine.log.info("Using filterfile: " + filterFile);
            engine.log.debug("The following words are being filtered:" + badwords);
        }
        return new IrcFilter(badwords);
    }

    public Filter getNickFilter() {
        String badNicks = "";
        if (nickFilterFile != null) {
            try {
                FileReader f = new FileReader(nickFilterFile);
                try {
                    int charInt = f.read();
                    while (charInt != -1) {
                        badNicks = badNicks + (char) charInt;
                        charInt = f.read();
                    }
                } catch (IOException e1) {
                    engine.log.error("Error reading nickNameFilterFile: " + nickFilterFile);
                }
            } catch (FileNotFoundException e) {
                engine.log.error("Cannot find nickNameFilterFile: " + nickFilterFile);
                return null;
            }
            engine.log.info("Using nickNameFilterfile: " + nickFilterFile);
            engine.log.debug("The following nicknames are not allowed:" + badNicks);
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

