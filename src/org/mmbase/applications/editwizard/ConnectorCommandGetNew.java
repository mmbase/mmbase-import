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

public class ConnectorCommandGetNew extends ConnectorCommand {

  String objecttype;

  public ConnectorCommandGetNew(String aobjecttype) {
    super("getnew");
    objecttype = aobjecttype;
    addCommandAttr("type", objecttype);
  }

}
