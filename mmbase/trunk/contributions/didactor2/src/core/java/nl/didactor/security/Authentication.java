package nl.didactor.security;

import nl.didactor.builders.*;
//import org.mmbase.security.*;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.security.Rank;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.bridge.Node;
import java.util.Map;

/**
 * Didactor authentication routines. This class authenticates users
 * against the cloud, and returns their rank based on the builder
 * they belong to.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class Authentication extends org.mmbase.security.Authentication {
    private static Logger log = Logging.getLoggerInstance(Authentication.class.getName());
    private PeopleBuilder users;

    static {
        Rank.createRank(200, "people");
    }

    /**
     * Login method: it tests the given credentials against MMBase.
     * @param application The application identifier
     * @param loginInfo A Map containing the login credentials
     * @param parameters A list of optional parameters
     */
    public org.mmbase.security.UserContext login(String application, Map loginInfo, Object[] parameters) throws org.mmbase.security.SecurityException {
        if ("anonymous".equals(application)) {
            return new UserContext("anonymous", "anonymous", Rank.ANONYMOUS);
        }
        
        if (loginInfo == null || application == null)
            throw new org.mmbase.security.SecurityException("Incorrect parameters: application = '" + application + "', loginInfo = '" + loginInfo + "'");


        String username = (String)loginInfo.get("username");
        String password = (String)loginInfo.get("password");
        if (username == null || password == null)
            return null;

        checkBuilder();
        MMObjectNode user = users.getUser(username, password);
        if (user == null)
            return null;

        return (org.mmbase.security.UserContext)(new UserContext(user));
    } 

    public boolean isValid(org.mmbase.security.UserContext usercontext) throws org.mmbase.security.SecurityException {
        return true;
    }

    protected void load() {
    }

    private void checkBuilder() throws org.mmbase.security.SecurityException {
        if (users == null) {
            org.mmbase.module.core.MMBase mmb = (org.mmbase.module.core.MMBase) org.mmbase.module.Module.getModule("mmbaseroot");
            users = (PeopleBuilder) mmb.getMMObject("people");
            if (users == null) {
                String msg = "builder people not found";
                log.error(msg);
                throw new org.mmbase.security.SecurityException(msg);
            }
        }
    }
}
