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

public class ConnectorCommandGetNewRelation extends ConnectorCommand {

  String objecttype;

  public ConnectorCommandGetNewRelation(String role, String sourceobjectnumber, String destinationobjectnumber) {
    super("getnewrelation");
    addCommandAttr("role", role);
    addCommandAttr("source", sourceobjectnumber);
    addCommandAttr("destination", destinationobjectnumber);
  }

}
