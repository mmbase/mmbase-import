/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat.repository.mmbase;

import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.mmbase.bridge.*;
import org.mmbase.module.builders.Versions;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.storage.search.SearchQueryException;

import nl.eo.chat.ChatEngine;
import nl.eo.chat.InitializationException;
import nl.eo.chat.repository.*;
import nl.eo.chat.repository.irc.*;

/**
 * This implementation of a repository uses MMBase to retrieve and store
 * information.
 *
 * @author Jaco de Groot
 */
public class MMBaseRepository extends IrcRepository {
    private Cloud cloud;
    private NodeManager chatserversNodeManager;
    private NodeManager usersNodeManager;
    private NodeManager chatchannelsNodeManager;
    private RelationManager insrelRelationManager;
    private RelationManager rolerelRelationManager;
    private String userGroupNodeNumber;
    private String chatserversNodeNumber;
    private Node userGroupNode;
    private Node chatserversNode;
    private String usersNodeManagerName;
    private String usersAccountFieldName;
    private String usersSessionFieldName;
    private String mmbaseCloudContextUri;
    private String mmbaseUsername;
    private String mmbasePassword;
    private String mmbaseConfig;
    private String operatorUsername;
    private String operatorPassword;
    private String administratorUsername;
    private String  administratorPassword;

    public MMBaseRepository() {
    }

    public void setCloudContextUri( String u) {
        mmbaseCloudContextUri = u;
    }
    
    public void setUserGroupNode( String node ) {
        userGroupNodeNumber = node;
    }

    public void setChatServerNode( String node ) {
        chatserversNodeNumber = node;
    }

    public void setUsersNodeManagerName(String name) {
        usersNodeManagerName = name;
    }

    public void setUsersAccountFieldName(String name) {
         usersAccountFieldName = name;
    }

    public void setUsersSessionFieldName(String name) {
         usersSessionFieldName = name;
    }

    public void setMMBaseUsername(String name) {
        mmbaseUsername = name;
    }

    public void setMMBasePassword(String pass) {
        mmbasePassword = pass;
    }
    
    public void setMMBaseConfig(String conf) {
        mmbaseConfig = conf;
    }
  
    public void setOperatorUsername(String name) {
        operatorUsername = name;
    }

    public void setOperatorPassword(String pass) {
        operatorPassword = pass;
    }

    public void setAdministratorUsername(String name) {
        administratorUsername = name;
    }
    
    public void setAministratorPassword(String pass) {
        administratorPassword = pass;
    }

  
    public void init(ChatEngine engine) throws InitializationException {
        super.init(engine);
        engine.log.info("Initializing MMBaseRepository");
        Map loginInfo = new HashMap();
        loginInfo.put("username", mmbaseUsername);
        loginInfo.put("password", mmbasePassword);
        
        engine.log.debug("mmbase username/password = "+mmbaseUsername+"/"+mmbasePassword);
        if (mmbaseCloudContextUri == null) {
            throw new InitializationException("cloud-context-uri is null");
        }
        if (mmbaseCloudContextUri.equals("local")) {
            engine.log.info("Initializing with local context uri");
            if (mmbaseConfig != null) {
                // Init MMBase
                try{
                    MMBaseContext.init(mmbaseConfig, true);
                } catch(Exception e) {
                    throw new InitializationException("Could not initialize MMBase: " + e.getMessage());
                }
            }
            // Startup MMBase if not started already.
            MMBase mmb = (MMBase)org.mmbase.module.Module.getModule("MMBASEROOT");
            // Wait until MMBase is started.
            while (!mmb.getState()) {
                try{
                    engine.log.info("Wait a second for MMBase to start.");
                    Thread.currentThread().sleep(1000);
                } catch (Exception e) {
                }
            }
            // Wait until MMBase Chat application is deployed.
            Versions versions = (Versions)mmb.getMMObject("versions");
            try {
                while (versions.getInstalledVersion("Chat", "application") < 1) {
                    try{
                        engine.log.info("Wait a second for MMBase Chat application to be deployed.");
                        Thread.currentThread().sleep(1000);
                    } catch (Exception e) {
                    }
                }
            } catch (SearchQueryException e) {
                throw new InitializationException("Could not get MMBase Chat application version: "
                                                  + e.getMessage());
            }
            // Get a cloud object using LocalContext.
            cloud = LocalContext.getCloudContext().getCloud("mmbase", "name/password", loginInfo);
        } else {
            // Get a cloud object using ContextProvider.
            engine.log.info("Initializing with contextprovider "+mmbaseCloudContextUri);
            cloud = ContextProvider.getCloudContext(mmbaseCloudContextUri).getCloud("mmbase", "name/password", loginInfo);
        }
        // Get the needed node managers.
        try {
            chatserversNodeManager = cloud.getNodeManager("chatservers");
        } catch(BridgeException e) {
            throw new InitializationException("Builder chatservers not found.");
        }
        try {
            NodeManager usersNodeManager = cloud.getNodeManager(usersNodeManagerName);
        } catch(BridgeException e) {
            throw new InitializationException("Builder " + usersNodeManagerName + " not found.");
        }
        try {
            chatchannelsNodeManager = cloud.getNodeManager("chatchannels");
        } catch(BridgeException e) {
            throw new InitializationException("Builder chatchannels not found.");
        }
        try {
            insrelRelationManager = cloud.getRelationManager("related");
        } catch(BridgeException e) {
            throw new InitializationException("Builder insrel not found.");
        }
        try {
            rolerelRelationManager = cloud.getRelationManager("rolerel");
        } catch(BridgeException e) {
            throw new InitializationException("Builder rolerel not found.");
        }
        // Get the most important nodes from the cloud.
        try {
            userGroupNode = cloud.getNode(userGroupNodeNumber);
        } catch(BridgeException e) {
            throw new InitializationException("Could not find UserGroupNode: " + userGroupNodeNumber);
        }
        try {
            chatserversNode = cloud.getNode(chatserversNodeNumber);
        } catch(BridgeException e) {
            throw new InitializationException("Could not find chatservers node: " + chatserversNodeNumber);
        }
        if (!"chatservers".equals(chatserversNode.getNodeManager().getName())) {
            throw new InitializationException("Node " + chatserversNodeNumber + " is not a chatservers node.");
        }
        engine.log.debug("Usergroup node: " + userGroupNode.getNumber() + ".");
        engine.log.debug("Chatservers node: " + chatserversNode.getNumber() + ".");
        userRepository = new MMBaseUserRepository(cloud, usersNodeManager, rolerelRelationManager, userGroupNode, chatserversNode, usersNodeManagerName, usersAccountFieldName, usersSessionFieldName);
        userRepository.setOperatorUsername(operatorUsername);
        userRepository.setOperatorPassword(operatorPassword);
        userRepository.setAdministratorUsername(administratorUsername);
        userRepository.setAdministratorPassword(administratorPassword);
        userRepository.init(engine);
        channelRepository = new MMBaseChannelRepository(cloud, chatserversNode, chatchannelsNodeManager, insrelRelationManager, rolerelRelationManager, engine);
    }

    public Filter getFilter() {
        // Use getNode to prevent getting back an old value.
        Node node = cloud.getNode(chatserversNode.getNumber());
        return new MMBaseFilter(node.getStringValue("blackwordlist"));
    }

    public long open(Date currentDate) {
        Node node = cloud.getNode(chatserversNode.getNumber());
        WorkingHours workingHours = new WorkingHours(node.getStringValue("workinghours"));
        return workingHours.open(currentDate);
    }
    
    public long close(Date currentDate) {
        Node node = cloud.getNode(chatserversNode.getNumber());
        WorkingHours workingHours = new WorkingHours(node.getStringValue("workinghours"));
        return workingHours.close(currentDate);
    }

}

