/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.externalprocess;

/**
 * Basic implementation of {@link IProgressMonitor} that implements every method in the most
 * straight forward or empty way.
 *
 * @author Michiel Meeuwissen
 * @version $Id: IProgressMonitor.java 34900 2009-05-01 16:29:42Z michiel $
 * @since MMBase-1.9.1
 */
public abstract class AbstractMonitor implements IProgressMonitor {

    private boolean canceled = false;

    public void begin() {
    }

    public void done() {
    }
    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean value) {
        canceled = value;
    }

    public void worked() {
    }
}
