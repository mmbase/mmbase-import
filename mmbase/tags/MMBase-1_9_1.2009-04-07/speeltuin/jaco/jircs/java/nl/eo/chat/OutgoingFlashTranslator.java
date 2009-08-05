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
 * Translator for outgoing Flash messages.
 *
 * @author Jaco de Groot
 */
public class OutgoingFlashTranslator extends OutgoingTranslator {

    protected boolean writeMessage(Writer writer, ServerMessage message) throws IOException {
        boolean quit = false;
        XMLElement messageElement = new XMLElement();
        messageElement.setName("message");
        String prefix = message.getPrefix();
        if (prefix != null) {
            messageElement.setAttribute("prefix", prefix);
        }
        String command;
        int c = message.getCommand();
        if (c == 1000) {
            command = "ERROR";
            quit = true;
        } else {
            command = ServerMessage.getCommand(message.getCommand());
        }
        messageElement.setAttribute("command", command);
        Vector parameters = (Vector)message.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            String parameter = (String)parameters.elementAt(i);
            XMLElement parameterElement = new XMLElement();
            parameterElement.setName("parameter");
            parameterElement.setAttribute("value", parameter);
            messageElement.addChild(parameterElement);
            if (parameter.indexOf(' ') != -1) {
                i = parameters.size();
            }
        }
        writer.write(messageElement.toString());
        writer.write('\0');
        writer.flush();
        return quit;
    }

}
