/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard;

import java.util.*;
import org.mmbase.util.logging.*;

/**
 * The wizardcommands are used to store information received from the clients about commands.
 * Eg.: add-item, delete commands are stored here.
 *
 * @javadoc
 * @author Kars Veling
 * @author Pierre van Rooden
 * @since MMBase-1.6
 * @version $Id: WizardCommand.java,v 1.14 2008-02-12 17:41:14 michiel Exp $
 */
public class WizardCommand implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    public final static short UNKNOWN_COMMAND = -1;
    public final static short ADD_ITEM = 0;
    public final static short CANCEL = 1;
    public final static short COMMIT = 2;
    public final static short DELETE_ITEM = 3;
    public final static short GOTO_FORM = 4;
    public final static short MOVE_DOWN = 5;
    public final static short MOVE_UP = 6;
    public final static short SAVE       = 7;
    public final static short START_WIZARD = 8;
    public final static short UPDATE_ITEM = 9;

    /**
     * Array with the command strings, as they are parsed.
     * The accompanying command constant can be derived by using the
     * index in the array.
     */
    private final static String[] COMMANDS =
    {"add-item", "cancel", "commit", "delete-item", "goto-form", "move-down", "move-up", "save", "start-wizard", "update-item"};

    private static final Logger log = Logging.getLoggerInstance(WizardCommand.class);

    private String commandName="unknown";
    private int type = UNKNOWN_COMMAND;
    private List<String> params = null;

    private String value = null;

    // the original command as it was passed
    private String command;

    /**
     * Creates  a wizard command object with the given command and value.
     * The command parsed should be of the format:
     * <code>
     * cmd/type/fid/did/otherdid/
     * </code>
     * 'type' is the command itself (i.e. 'add-item'), fid, did, and otherdid are possible
     * parameters to the command.
     *
     * @todo should use StringTokenizer here
     * @param acommand The full command
     * @param avalue The value of the command
     */
    public WizardCommand(String acommand, String avalue) {
        command = acommand.toLowerCase();
        value = avalue;
        if (log.isDebugEnabled()) {
            log.debug("command: " + command + " : "+value);
        }

        StringTokenizer st= new StringTokenizer(command,"/",true);
        // skip first token ('cmd') and delimiter
        st.nextToken();
        st.nextToken();

        // second token is command name (aka type)
        commandName= st.nextToken();
        st.nextToken(); // delimiter

        // attempt to determine type from the (ordered) array of known commands.
        type = Arrays.binarySearch(COMMANDS, commandName);
        if (type<0) type =UNKNOWN_COMMAND;

        int paramcount=st.countTokens();
        if (paramcount>0) {
            params = new ArrayList<String>(paramcount);
            // get optional other parameters: fid, did, and otherdid, possible others...
            while (st.hasMoreTokens()) {
                String tok=st.nextToken();
                if (!tok.equals("/")) {
                    params.add(tok);
                    st.nextToken();
                } else {
                    params.add("");
                }
            }
        }
    }

    /**
     * Returns the type of the parsed command.
     * @return one of the WizardCommand constants, or UNKNOWN_COMMAND if the type cannot be determined
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the value passed to the parsed command.
     * @return the value as a string
     */
    public String getValue() {
        return value;
    }
   /**
     * Returns the parameter with the indicated index of the parsed command.
     * @return teh parameter as string, or an empty string if it doesn't exist.
     */
    public String getParameter(int i) {
        if ((params==null) || (i>=params.size()))
            return "";
        else
            return params.get(i);
    }

    /**
     * Returns the 'fid' (field id) parameter of the parsed command.
     * This is always the parameter with index 0
     * @return the fid as string, or an empty string if it doesn't exist.
     */
    public String getFid() {
        return getParameter(0);
    }

    /**
     * Returns the 'did' (data id) parameter of the parsed command.
     * This is always the parameter with index 1
     * @return the did as string, or an empty string if it doesn't exist.
     */
    public String getDid() {
        return getParameter(1);
    }

    public String toString() {
        return command;
    }

}
