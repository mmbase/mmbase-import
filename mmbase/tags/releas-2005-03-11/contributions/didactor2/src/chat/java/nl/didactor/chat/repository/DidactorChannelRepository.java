package nl.didactor.chat.repository;

import java.net.Socket;
import java.util.Collection;
import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationManager;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import nl.eo.chat.repository.*;
import nl.eo.chat.repository.irc.*;

/**
 * ChannelRepository for channels within Didactor. Channels are related to classes:
 * people may only join channels that are related to classes they are in.
 * @author Johannes Verelst &lt;johannes@mediatorgroup.com&gt;
 */
public class DidactorChannelRepository extends IrcChannelRepository {
    Cloud cloud;
    private Logger log = Logging.getLoggerInstance(DidactorChannelRepository.class.getName());
    
    protected DidactorChannelRepository(Cloud cloud) {
        this.cloud = cloud;
        NodeManager classes = cloud.getNodeManager("classes");
        NodeList channellist = cloud.getList("", "classes,chatchannels", "classes.number,chatchannels.number", null, null, null, null, true);
        for (int i=0; i<channellist.size(); i++) {
            Node klas = channellist.getNode(i);
//            channels.put("#" + klas.getIntValue("chatchannels.number"), 
	      channels.put("#" + klas.getStringValue("chatchannels.name"),
//                         new DidactorChannel("#" + klas.getIntValue("chatchannels.number"), 
                         new DidactorChannel("#" + klas.getStringValue("chatchannels.name"), 
                                             cloud, 
                                             cloud.getNode(klas.getIntValue("chatchannels.number"))));
        }
    }
    
    public Channel createChannel(String name) {
        return null;
    }
    
    public void removeChannel(String name) {
        super.removeChannel(name);
    }

    public Collection getAllowedChannels(User user) {
        log.debug("Getting allowed channels from " + user.getNick() +"-"+ user.getUsername());
        DidactorUser duser = (DidactorUser)user;
        Node usernode = duser.getUsersNode();
        log.debug("UserNode: (" +duser.getUsersNode());
        NodeList userChannels = cloud.getList("" + usernode.getNumber(), "people,classes,chatchannels", "chatchannels.number", null, null, null, null, true);
        log.debug("aantal channels: " + userChannels.size());
        Vector v = new Vector();
        for (int i=0; i<userChannels.size(); i++) {
            Node channel = userChannels.getNode(i);
            log.debug("Found channel: " + channel);
            v.add(channels.get("#" + channel.getStringValue("chatchannels.name")));
//            v.add(channels.get("#" + channel.getIntValue("chatchannels.number")));
        }

        return v;
    }
}
