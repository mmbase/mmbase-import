/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard;

import org.mmbase.util.logging.*;

/**
 * The wizardcommands are used to store information received from the clients about commands.
 * Eg.: add-item, delete commands are stored here.
 *
 * @javadoc
 * @author Kars Veling
 * @since   MMBase-1.6
 * @version $Id: WizardCommand.java,v 1.3 2002-02-25 16:18:23 pierre Exp $
 */
public class WizardCommand {

    // logging
    private static Logger log = Logging.getLoggerInstance(WizardCommand.class.getName());

    // the original command as it was passed
    private String command;

    public String type;
    public String value;
    public String fid;
    public String did;
    public String otherdid;

    public WizardCommand() {
        type = "none";
        command = "";
        value = "";
        fid = "";
        did = "";
    }

    /**
     * Creates  a wizard command object with the given command and value.
     * The command parsed should be of the format:
     * <code>
     * /type/fid/did/otherdid/
     * </code>
     * 'type' is the command itself (i.e. 'add-item'), fid, did, and otherdid are possible
     * parameters to the command.
     *
     * @todo should use StringTokenizer here
     * @param command The full command
     * @param avalue The value of the command
     */
    public WizardCommand(String acommand, String avalue) {

        command = acommand.toLowerCase();
        value = avalue;
        log.debug("command: "+command + " : "+value);

        // retrieve the fid and did again
        int nr0=command.indexOf("/");
        int nr1=command.indexOf("/", nr0+1);
        int nr2=command.indexOf("/", nr1+1);
        int nr3=command.indexOf("/", nr2+1);
        int nr4=command.indexOf("/", nr3+1);

        if (nr4<1) {
            return;
        }

        type = command.substring(nr0+1, nr1);
        fid = command.substring(nr1+1, nr2);
        did = command.substring(nr2+1, nr3);
        otherdid = command.substring(nr3+1, nr4);
    }

    /**
     * This method returns true if there is need to store all values passed by the client.
     *
     * @returns   True if it is needed to store the values, False if not.
     */
    public boolean needToStoreValues() {
        return !type.equals("cancel");
    }
}
