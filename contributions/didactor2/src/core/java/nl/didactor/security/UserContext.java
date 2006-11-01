package nl.didactor.security;

import org.mmbase.security.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.MMObjectNode;
import java.util.Map;
import java.util.Collection;
import java.util.Vector;
import java.util.HashSet;

/**
 * This security-related class wraps around a user node. This object
 * contains all information about a user, it can also report back
 * the roles based on a given context.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class UserContext extends org.mmbase.security.BasicUser {
    private static final Logger log = Logging.getLoggerInstance(UserContext.class);

    private final MMObjectNode wrappedNode;
    private final String identifier ;
    private final String owner;
    private final Rank rank;

    /**
     * Copy constructor which only resets the application. This is needed because 'asis' is
     * considered an application here.
     */
    public UserContext(UserContext uc, String application) {
        super(application);
        wrappedNode = uc.wrappedNode;
        identifier = uc.identifier;
        owner = uc.owner;
        rank = uc.rank;
    }

    /**
     * From the org.mmbase.security.UserContext interface
     */
    public UserContext() {
        super("name/password");
        identifier = "";
        owner = "";
        rank = null;
        wrappedNode = null;
    }

    /**
     * From the org.mmbase.security.UserContext interface
     */
    public UserContext(String identifier, String owner, Rank rank) {
        super("name/password");
        this.identifier = identifier;
        this.owner = owner;
        this.rank = rank;
        wrappedNode = null;
    }

    /**
     * Initialize the usercontext for a given user node. This checks
     * the rank of the user by looking at the node type
     */
    public UserContext(MMObjectNode node) {
        super("name/password");
        //wrappedNode = node.getCloud().getNode(node.getNumber());
        owner = node.getStringValue("username");
        identifier = owner;
        String rankstring = "people";
        this.wrappedNode = node;

        if ("admin".equals(owner)) {
            rank = Rank.ADMIN;
        } else {
            rank = Rank.getRank(rankstring);
        }
    } 

    /**
     * From the org.mmbase.security.UserContext interface
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * From the org.mmbase.security.UserContext interface
     */
    public String getOwnerField() {
        return owner;
    }

    /**
     * From the org.mmbase.security.UserContext interface
     */
    public Rank getRank() throws org.mmbase.security.SecurityException {
        return rank;
    }
    
    public Integer getUserNumber() {
        try {
            if ( wrappedNode != null )
                return (Integer)wrappedNode.getValue("number");
        } catch (Exception e) {}
        return new Integer(0);
    }
}
