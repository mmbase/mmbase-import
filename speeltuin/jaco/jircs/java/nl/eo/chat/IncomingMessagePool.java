/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat;

import java.util.Vector;

/**
 * The message pool for incoming messages. The incoming translator will put
 * messages into this pool and the chat engine will retrieve messages from
 * this pool.
 *
 * @author Jaco de Groot
 */
public class IncomingMessagePool extends MessagePool {
    private Vector messages = new Vector();
    private Object msgWatcher = new Object();
    
    protected void putMessage(Message message) {
        synchronized(msgWatcher) {
            log.debug("incoming: putMessage "+message.getCommand());
            messages.add(message);
        }
    }

    protected Message getMessage() {
        synchronized(msgWatcher) {
            if (messages.size() > 0) {
                return (Message)messages.remove(0);
            } else {
                return null;
            }
        }
    }
}


