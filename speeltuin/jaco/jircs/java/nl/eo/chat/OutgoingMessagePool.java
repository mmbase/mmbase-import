/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat;

import java.util.Hashtable;
import java.util.Vector;
import java.net.Socket;

/**
 * The message pool for outgoing messages. The chat engine will put messages
 * into this pool and the outgoing translator will retrieve messages from
 * this pool.
 *
 * @author Jaco de Groot
 */
public class OutgoingMessagePool extends MessagePool {
    // Contains vectors with messages (values) related to sockets (keys).
    private Hashtable sockets = new Hashtable();
    private Object watcher = new Object();
    
    protected void putMessage(ServerMessage message) {
        synchronized(watcher) {
            log.debug("outgoing servermessage: "+message.getCommand(message.getCommand()));
            Vector recipients = message.getRecipients();
            for (int i = 0; i < recipients.size(); i++) {
                Socket socket = (Socket)recipients.elementAt(i);
                Vector messages = (Vector)sockets.get(socket);
                if (messages == null) {
                    messages = new Vector();
                    sockets.put(socket, messages);
                }
                messages.add(message);
            }

        }
    }

    protected Message getMessage(Socket socket) {
        synchronized(watcher) {
            Vector messages = (Vector)sockets.get(socket);
            if (messages != null && messages.size() > 0) {
                return (Message)messages.remove(0);
            } else {
                return null;
            }
        }
    }
}

