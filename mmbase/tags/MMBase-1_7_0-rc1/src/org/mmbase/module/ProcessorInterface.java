/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 */
public interface  ProcessorInterface  {

    /**
     * Returns a virtual builder used to create node lists from the results
     * returned by getList().
     * The default method does not associate the builder with a cloud (mmbase module),
     * so processormodules that need this association need to override this method.
     * Note that different lists may return different builders.
     * @param command the LIST command for which to retrieve the builder
     * @param params contains the attributes for the list
     */
    public MMObjectBuilder getListBuilder(String command,Map params);

    /**
     * Generate a list of values from a command to the processor.
     * The values are grouped into nodes.
     * @param context the context of the page or calling application (currently, this should be a scanpage object)
     * @param command the list command to execute.
     * @param params contains the attributes for the list
     * @return a <code>Vector</code> that contains the list values contained in MMObjectNode objects
     */
    public Vector getNodeList(Object context,String command, Map params) throws ParseException;

    /**
     * Generate a list of values from a command to the processor
     * @param sp the page context
     * @param tagger contains the attributes for the list
     * @param command the list command to execute.
     */
    abstract public Vector getList(scanpage sp,StringTagger tagger, String value) throws ParseException;

    /**
     * Execute the commands provided in the form values
     */
    abstract public boolean process(scanpage sp, Hashtable cmds,Hashtable vars);

    /**
     *    Replace a command by a string
     */
    abstract public String replace (scanpage sp, String command);

    /**
     *    Replace a command by a string
     */
    abstract public String replace (scanpage sp, StringTagger command);

    /**
     * Do a cache check (304) for this request
     */
    abstract public boolean cacheCheck(scanpage sp,String cmd);
}
