/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import javax.servlet.jsp.JspTagException;


import org.mmbase.util.logging.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: CommitTag.java 35335 2009-05-21 08:14:41Z michiel $
 * @since MMBase-1.9.2
 */

public class RefreshTag extends TransactionReferrerTag {
    private static final Logger log = Logging.getLoggerInstance(CommitTag.class);

    @Override
    public int doStartTag() throws JspTagException {
        TransactionTag tag = findTransactionTag(true);
        tag.refreshTransaction();
        return SKIP_BODY;
    }

}
