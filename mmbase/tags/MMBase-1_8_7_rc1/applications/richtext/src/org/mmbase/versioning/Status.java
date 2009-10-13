package org.mmbase.versioning;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.datatypes.processors.CommitProcessor;
import org.mmbase.storage.search.FieldCompareConstraint;

import org.mmbase.util.Casting;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Michiel Meeuwissen
 */

public class Status  {

    public static final int INVISIBLE = -1;
    public static final int NEW       = 0;
    public static final int ONLINE    = 1;
    public static final int LOCKED    = 2;

}
