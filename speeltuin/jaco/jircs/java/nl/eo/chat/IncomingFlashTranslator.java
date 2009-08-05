/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat;

import java.io.*;
import java.net.*;
import java.util.Vector;
import nanoxml.XMLElement;

/**
 * Translator for incoming Flash messages.
 *
 * @author Jaco de Groot
 */
public class IncomingFlashTranslator extends IncomingTranslator {


    protected boolean messageSeparatorFound(int i) {
        if (((char)buffer[i]) == '\0') {
            return true;
        } else {
            return false;
        }
    }
    
    /*
    <message command="JOIN">
      <parameter value="#test"/>
    </message>
    
    <message prefix="irc.test.com" command="366">
      <parameter value="nick"/>
      <parameter value="#test"/>
      <parameter value="End of NAMES list."/>
    </message>
    */
    protected void parseMessage(int i) {
        XMLElement xml = getXML(s);
        if ("message".equals(xml.getName())) {
            ClientMessage message = new ClientMessage();
            String prefix = xml.getStringAttribute("prefix", null);
            if (prefix != null) {
                message.setPrefix(prefix);
            }
            String command = xml.getStringAttribute("command", null);
            int commandId = ClientMessage.getCommandId(command);
            if (commandId != -1) {
                message.setCommand(commandId);
            } else {
                log.debug("UNKNOWN COMMAND '" + command + "'.");
            }
            Vector children = xml.getChildren();
            for (int j = 0; j < children.size(); j++) {
                XMLElement child = (XMLElement)children.elementAt(j);
                message.addParameter(child.getStringAttribute("value",""));
            }
            message.setSender(socket);
            incomingMessagePool.putMessage(message);
        }
    }

    private XMLElement getXML(String message) {
        XMLElement element = new XMLElement();
        try {
            element.parseString(message);
        } catch(Exception e) {
        }
        return element;
    }
    
}
