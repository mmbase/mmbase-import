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

/**
 * Translator for outgoing IRC messages.
 *
 * @author Jaco de Groot
 */
public class OutgoingIrcTranslator extends OutgoingTranslator {

    protected boolean writeMessage(Writer writer, ServerMessage message) throws IOException {
        boolean quit = false;
        String prefix = message.getPrefix();
        if (prefix != null) {
            writer.write(':');
            for (int i = 0; i < prefix.length(); i++) {
                writer.write(prefix.charAt(i));
            }
            writer.write(' ');
        }
        int c = message.getCommand();
        if (c == 1000) {
            writer.write("ERROR");
            quit = true;
        } else {
            String command = ServerMessage.getCommand(message.getCommand());
            for (int i = 0; i < command.length(); i++) {
                writer.write(command.charAt(i));
            }
        }
        Vector parameters = (Vector)message.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            String parameter = (String)parameters.elementAt(i);
            writer.write(' ');
            if (parameter.indexOf(' ') != -1) {
                writer.write(':');
                i = parameters.size();
            } else if (i == parameters.size() - 1 && message.getUseLastParameterColon()) {
                writer.write(':');
            }
            for (int j = 0; j < parameter.length(); j++) {
                writer.write(parameter.charAt(j));
            }
        }
        writer.write('\r');
        writer.write('\n');
        writer.flush();
        return quit;
    }

}
