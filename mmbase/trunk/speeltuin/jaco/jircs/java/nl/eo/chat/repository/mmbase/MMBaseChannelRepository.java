/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat.repository.mmbase;

import java.net.Socket;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationManager;

import nl.eo.chat.ChatEngine;
import nl.eo.chat.repository.*;
import nl.eo.chat.repository.irc.*;

/**
 * This implementation of a channel repository uses MMBase to retrieve and store
 * information about channels.
 *
 * @author Jaco de Groot
 */
public class MMBaseChannelRepository extends IrcChannelRepository {
    Cloud cloud;
    Node chatserversNode;
    NodeManager chatchannelsNodeManager;
    RelationManager insrelRelationManager;
    RelationManager rolerelRelationManager;
    ChatEngine engine;
    long lastUpdate = 0;
    
    protected MMBaseChannelRepository(Cloud cloud, Node chatserversNode, NodeManager chatchannelsNodeManager, RelationManager insrelRelationManager, RelationManager rolerelRelationManager, ChatEngine engine) {
        this.cloud = cloud;
        this.chatserversNode = chatserversNode;
        this.chatchannelsNodeManager = chatchannelsNodeManager;
        this.insrelRelationManager = insrelRelationManager;
        this.rolerelRelationManager = rolerelRelationManager;
        this.engine = engine;
        updateChannelList();
    }

    protected void updateChannelList() {
        NodeList nodeList;
        // TODO: Fix this to really use the configured nodemanagers!
        nodeList = cloud.getList(chatserversNode.getStringValue("number"),
                                 "chatservers,chatchannels",
                                 "chatchannels.number,chatchannels.name",
                                 null, null, null, null, false);
        NodeIterator iterator = nodeList.nodeIterator();
        HashMap updatedChannels = new HashMap();
        while (iterator.hasNext()) {
            Node node = iterator.nextNode();
            String name = node.getStringValue("chatchannels.name");
            Node channelNode =  cloud.getNode(node.getIntValue("chatchannels.number"));
            MMBaseChannel channel = (MMBaseChannel) channels.get(name.toLowerCase());
            if (channel == null) {
                System.err.println("Got node with number "+channelNode.getNumber()+" / type "+channelNode.getNodeManager().getName());
                channel = new MMBaseChannel(name, cloud, rolerelRelationManager,
                        chatserversNode, channelNode);
            } else {
                String topic = channel.getTopic();
                if (topic == null) {
                    topic = "";
                }
                channel.updateChannel(channelNode);

                String topic2 = channel.getTopic();
                if (topic2 == null) {
                    topic2 = "";
                }
                if (!topic.equals(topic2)) {
                    engine.channelTopicChanged(channel);
                }
                channels.remove(name);
            }
            updatedChannels.put(name.toLowerCase(), channel);
        }
        // these channels were removed
        Iterator i = channels.values().iterator();
        while (i.hasNext()) {
            engine.channelRemoved((Channel) i.next());
        }
        channels = updatedChannels;
    }

    
    public Channel createChannel(String name) {
        // Create a channel node in MMBase.
        Node chatchannelsNode = chatchannelsNodeManager.createNode();
        chatchannelsNode.setValue("name", name);
        chatchannelsNode.setIntValue("topicprotection", 0);
        chatchannelsNode.setIntValue("moderated", 0);
        chatchannelsNode.setIntValue("nooutsidemessages", 0);
        chatchannelsNode.setIntValue("userlimit", -1);
        chatchannelsNode.commit();
        // Relate this node to the chat server node.
        Relation relation = insrelRelationManager.createRelation(chatserversNode, chatchannelsNode);
        relation.commit();
        Channel channel = new MMBaseChannel(name, cloud, rolerelRelationManager,
                                            chatserversNode, chatchannelsNode);
        channels.put(name.toLowerCase(), channel);
        return channel;
    }
    
    public void removeChannel(String name) {
        MMBaseChannel channel = (MMBaseChannel)channels.get(name);
        Node chatchannelsNode = channel.getChatchannelsNode();
        chatchannelsNode.delete(true);
        super.removeChannel(name);
    }

    public Channel getChannel(String name) {
        updateCheck();
        return super.getChannel(name);
    }

    public Collection getChannels() {
        updateCheck();
        return super.getChannels();
    }
    
    protected synchronized void updateCheck()  {
        long time = System.currentTimeMillis();
        if (time - lastUpdate > 1000*60) {
            updateChannelList();
            lastUpdate = time;
        }
    }
}

