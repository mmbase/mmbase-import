package org.mmbase.applications.editwizard;

import org.mmbase.util.logging.*;

/**
 * The wizardcommands are used to store information received from the clients about commands. Eg.: add-item, delete commands are stored here.
 *
 */
public class WizardCommand {

    private static Logger log = Logging.getLoggerInstance(WizardCommand.class.getName());
  public String type;
  public String name;
  public String value;
  public String fid;
  public String did;
  public String otherdid;

  public WizardCommand() {
    type = "none";
    name = "";
    value = "";
    fid = "";
    did = "";
  }

  /**
   * Sets a wizardcommand with the given name and value. It decodes the name into all stored properties.
   *
   * @param     aname   The name of the command
   * @param     avalue  The value of the command
   */
  public WizardCommand(String aname, String avalue) {

    name = aname.toLowerCase();
    value = avalue;
    log.info("command: "+name + " : "+value);

    // retrieve the fid and did again
    int nr0=name.indexOf("/");
    int nr1=name.indexOf("/", nr0+1);
    int nr2=name.indexOf("/", nr1+1);
    int nr3=name.indexOf("/", nr2+1);
    int nr4=name.indexOf("/", nr3+1);

    if (nr4<1) {
    return;
    }

    type = name.substring(nr0+1, nr1);
    fid = name.substring(nr1+1, nr2);
    did = name.substring(nr2+1, nr3);
    otherdid = name.substring(nr3+1, nr4);
  }

  /**
   * This method returns true if there is need to store all values passed by the client.
   *
   * @returns   True if it is needed to store the values, False if not.
   */
  public boolean needToStoreValues() {
    return (type!="cancel");
  }
}
