package org.mmbase.applications.editwizard;

import java.util.Vector;
import org.w3c.dom.*;

/**
 * Title:        VPRO EditWizard
 * Description:
 * Copyright:    Copyright (c) 1999
 * Company:      Q42
 * @author Kars Veling
 * @version
 */

public class ConnectorCommandGetConstraints extends ConnectorCommand {

  String objecttype, objectnumber;
  Document cmd;

  public ConnectorCommandGetConstraints(String objecttype) {
    super("getconstraints");
    addCommandAttr("type",objecttype);
  }


}
