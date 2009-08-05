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
 * Translator for incoming IRC messages.
 *
 * @author Jaco de Groot
 */
public class IncomingIrcTranslator extends IncomingTranslator {


    protected boolean messageSeparatorFound(int i) {
        // Officially we should only check for \r\n. But ksirc only
        // sends \n
        if (i > 0 && ((char)buffer[i]) == '\n') {
            return true;
        } else {
            return false;
        }
    }
    
    protected void parseMessage(int i) {
        if (((char)buffer[i - 1]) == '\r') {
            // Remove '\r'.
            s = s.substring(0, s.length() - 1);
        }
        ClientMessage message = new ClientMessage();
        int i1;
        int i2;
        i1 = 0;
        i2 = s.indexOf(' ');
        if (i2 == -1) {
            setCommand(message, s);
        } else {
            if (s.startsWith(":")) {
                message.setPrefix(s.substring(1, i2));
                // X-Chat 1.8.8 adds an extra space in "MODE #dynasol +b  *!*@*.localdomain".
                // Skip extra spaces.
                while (i2 + 1 < s.length() && s.charAt(i2 + 1) == ' ') {
                    i2++;
                }
                if (i2 == s.length() - 1) {
                    i2 = -1;
                } else {
                    i1 = i2 + 1;
                    if (s.charAt(i1) == ':') {
                        i1++;
                        i2 = s.length();
                        if (i1 == i2) {
                            i2 = -1;
                        }
                    } else {
                        i2 = s.indexOf(' ', i1);
                        if (i2 == -1) {
                            i2 = s.length();
                        }
                    }
                }
            }
            if (i2 != -1) {
                String command = s.substring(i1, i2);
                setCommand(message, command);
                // Skip extra spaces.
                while (i2 + 1 < s.length() && s.charAt(i2 + 1) == ' ') {
                    i2++;
                }
                if (i2 >= s.length() - 1) {
                    i2 = -1;
                } else {
                    i1 = i2 + 1;
                    if (s.charAt(i1) == ':') {
                        i1++;
                        i2 = s.length();
                        if (i1 == i2) {
                            i2 = -1;
                        }
                    } else {
                        i2 = s.indexOf(' ', i1);
                        if (i2 == -1) {
                            i2 = s.length();
                        }
                    }
                }
                while (i2 != -1) {
                    message.addParameter(s.substring(i1, i2));
                    // Skip extra spaces.
                    while (i2 + 1 < s.length() && s.charAt(i2 + 1) == ' ') {
                        i2++;
                    }
                    if (i2 >= s.length() - 1) {
                        i2 = -1;
                    } else {
                        i1 = i2 + 1;
                        if (s.charAt(i1) == ':') {
                            i1++;
                            i2 = s.length();
                            if (i1 == i2) {
                                message.addParameter("");
                                i2 = -1;
                            }
                        } else {
                            i2 = s.indexOf(' ', i1);
                            if (i2 == -1) {
                                i2 = s.length();
                            }
                        }
                    }
                }
            }
        }
        message.setSender(socket);
        incomingMessagePool.putMessage(message);
    }

}
