package nl.eo.chat;

import org.apache.commons.digester.*;
import java.io.*;

public class ServerConstructor {

    public static Server createServer ( String configfile ) throws Exception {
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.addObjectCreate("server","nl.eo.chat.Server");
        digester.addSetProperties("server");

        digester.addObjectCreate("server/logger","nl.eo.chat.SynchronizedLogger");
        digester.addSetProperties("server/logger", 
                new String[]{ "file","level","log-per-channel" } ,
                new String[]{ "file","logLevel","logPerChannel" }
        );
        digester.addSetNext("server/logger","setLogger","nl.eo.chat.Logger");
        

        digester.addObjectCreate("server/engine","nl.eo.chat.ChatEngine");
        digester.addSetProperties("server/engine");
        digester.addSetProperties("server/engine",
                new String[]{
                    "default-channel-modes","default-channel-userlimit",
                    "allow-channel-creation-by-user","allow-channel-operation-when-not-on-channel",
                    "allow-kick-operator","ignore-ban-operator",
                    "allow-operator-set-operatorstatus"
                },
                new String[]{
                    "defaultChannelModes","defaultChannelUserlimit",
                    "allowChannelCreationByUser","allowChannelOperationWhenNotOnChannel",
                    "kickOperatorAllowed","ignoreBanForOperator",
                    "setChannelOperatorMaySetRemoveChannelOperatorStatus"
                    
                }
        );
        digester.addSetNext("server/engine","addChatEngine","nl.eo.chat.ChatEngine");
        
        digester.addCallMethod("server/engine/flashport","addFlashPort",0);
        digester.addCallMethod("server/engine/ircport","addIrcPort",0);

        digester.addObjectCreate("server/engine/ircrepository","nl.eo.chat.repository.irc.IrcRepository");
        digester.addSetProperties("server/engine/ircrepository");
        digester.addSetProperties("server/engine/ircrepository",
                new String[]{ 
                    "operator-password","operator-username",
                    "administrator-password","administrator-username",
                    "filter-file","nick-filter-file",
                },
                new String[]{ 
                    "operatorPassword","operatorUsername",
                    "administratorPassword","administratorUsername",
                    "filterFile","nickFilterFile",
                }
        );
 
        
        digester.addSetNext("server/engine/ircrepository","setRepository");
 
        digester.addObjectCreate("server/engine/mmbaserepository","nl.eo.chat.repository.mmbase.MMBaseRepository");
        digester.addSetProperties("server/engine/mmbaserepository");
        digester.addSetProperties("server/engine/mmbaserepository",
                new String[]{ 
                    "mmbase-username","mmbase-password","cloud-context-uri",
                    "users-nodemanager","users-account-field","users-session-field",
                    "user-group-node","chat-server-node","mmbase-config",
                    "operator-password","operator-username",
                    "administrator-password","administrator-username"
                },
                new String[]{ 
                    "MMBaseUsername","MMBasePassword","cloudContextUri",
                    "usersNodeManagerName","usersAccountFieldName","usersSessionFieldName",
                    "userGroupNode","chatServerNode","MMBaseConfig",
                    "operatorPassword","operatorUsername",
                    "administratorPassword","administratorUsername"
                    
                    
                }
        );
        digester.addSetNext("server/engine/mmbaserepository","setRepository");
        
        return (Server) digester.parse(new File(configfile));
    }

}

