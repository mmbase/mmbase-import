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
    
    public MMBaseRepository() {
    }

    public void init() throws InitializationException {
        String mmbaseCloudContextUri = properties.getProperty("mmbase.cloudcontext.uri");
        if (mmbaseCloudContextUri == null) {
            throw new InitializationException("Could not find property mmbase.cloudcontext.uri.");
        }
        userGroupNodeNumber = properties.getProperty("UserGroupNode");
        if (userGroupNodeNumber == null) {
            throw new InitializationException("Could not find property UserGroupNode.");
        }
        chatserversNodeNumber = properties.getProperty("ChatServerNode");
        if (chatserversNodeNumber == null) {
            throw new InitializationException("Could not find property ChatServerNode.");
        }
        if (mmbaseCloudContextUri.equals("local")) {
            String mmbaseConfig = properties.getProperty("mmbase.config");
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
                    ChatEngine.log.info("Wait a second for MMBase to start.");
                    Thread.currentThread().sleep(1000);
                } catch (Exception e) {
                }
            }
            // Wait until MMBase Chat application is deployed.
            Versions versions = (Versions)mmb.getMMObject("versions");
            try {
                while (versions.getInstalledVersion("Chat", "application") < 1) {
                    try{
                        ChatEngine.log.info("Wait a second for MMBase Chat application to be deployed.");
                        Thread.currentThread().sleep(1000);
                    } catch (Exception e) {
                    }
                }
            } catch (SearchQueryException e) {
                throw new InitializationException("Could not get MMBase Chat application version: "
                                                  + e.getMessage());
            }
            // Get a cloud object using LocalContext.
            cloud = LocalContext.getCloudContext().getCloud("mmbase");
        } else {
            // Get a cloud object using ContextProvider.
            cloud = ContextProvider.getCloudContext(mmbaseCloudContextUri).getCloud("mmbase");
        }
        // Get the needed node managers.
        try {
            chatserversNodeManager = cloud.getNodeManager("chatservers");
        } catch(BridgeException e) {
            throw new InitializationException("Builder chatservers not found.");
        }
        try {
            usersNodeManager = cloud.getNodeManager("users");
        } catch(BridgeException e) {
            throw new InitializationException("Builder users not found.");
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
        ChatEngine.log.debug("Usergroup node: " + userGroupNode.getNumber() + ".");
        ChatEngine.log.debug("Chatservers node: " + chatserversNode.getNumber() + ".");
        userRepository = new MMBaseUserRepository(cloud, usersNodeManager, rolerelRelationManager, userGroupNode, chatserversNode);
        userRepository.setOperatorUsername((String)properties.get("operator.username"));
        userRepository.setOperatorPassword((String)properties.get("operator.password"));
        userRepository.setAdministratorUsername((String)properties.get("administrator.username"));
        userRepository.setAdministratorPassword((String)properties.get("administrator.password"));
        channelRepository = new MMBaseChannelRepository(cloud, chatserversNode, chatchannelsNodeManager, insrelRelationManager, rolerelRelationManager);
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

