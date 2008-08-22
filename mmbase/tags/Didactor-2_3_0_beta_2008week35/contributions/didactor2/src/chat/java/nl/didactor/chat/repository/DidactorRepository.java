package nl.didactor.chat.repository;

import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.mmbase.bridge.*;
import org.mmbase.module.builders.Versions;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.logging.Logging;

import nl.eo.chat.ChatEngine;
import nl.eo.chat.InitializationException;
import nl.eo.chat.repository.*;
import nl.eo.chat.*;
import nl.eo.chat.repository.irc.*;

/**
 * This implementation of a repository uses Didactor to retrieve and store
 * information.
 *
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class DidactorRepository implements Repository {
    private Cloud cloud;
    private UserRepository userRepository;
    private ChannelRepository channelRepository;
    private org.mmbase.util.logging.Logger log = Logging.getLoggerInstance(DidactorRepository.class.getName());
    
    public DidactorRepository() {
        log.debug("DidactorRepository() called");
    }

    public void init() {
        cloud = LocalContext.getCloudContext().getCloud("mmbase");
        userRepository = new DidactorUserRepository(cloud);
        channelRepository = new DidactorChannelRepository(cloud);
        log.service("Chatlogger using cloud '" + cloud + "'");
        log.service("Chatlogger using userrepository '" + userRepository + "'");
    }

    public void init(Logger log) {
        init();
    }

    public Filter getFilter() {
        return null;
    }

    public Filter getNickFilter() {
        return null;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public ChannelRepository getChannelRepository() {
        return channelRepository;
    }

    public long open(Date currentDate) {
        return 0;
    }
    
    public long close(Date currentDate) {
        return -1;
    }

    public void setProperties(java.util.Properties prop) {

    }

}
