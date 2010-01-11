/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.externalprocess;

/**
 * The <code>IProgressMonitor</code> interface is implemented
 * by objects that monitor the progress of a external process;
 * the methods in this interface are invoked by code that performs the external
 * process handling.
 * <p>
 * A request to cancel the external process can be signaled using the
 * <code>setCanceled</code> method.  Operations taking a progress monitor are
 * expected to poll the monitor (using <code>isCanceled</code>) periodically and
 * abort at their earliest convenience.  Operation can however choose to ignore
 * cancelation requests.
 * </p>
 * <p>
 * Since notification is synchronous with the external process itself, the
 * listener should provide a fast and robust implementation. If the handling of
 * notifications would involve blocking operations, or operations which might
 * throw uncaught exceptions, the notifications should be queued, and the actual
 * processing deferred (or perhaps delegated to a separate thread).
 * </p>
 * <p>
 * Clients may implement this interface.
 * </p>
 *
 * @author Nico Klasens (Finalist IT Group)
 * @version $Id$
 * @since MMBase-1.6
 */
public interface IProgressMonitor {

    /**
     * Notifies that the processing is beginning.  This must only be called once
     * on a given progress monitor instance.
     */
    void begin();

    /**
     * Notifies that the work is done; that is, either the external process is
     * completed or the user canceled it.
     */
    void done();

    /**
     * Returns whether cancelation of current operation has been requested.
     * Long-running operations should poll to see if cancelation
     * has been requested.
     *
     * @return <code>true</code> if cancellation has been requested,
     *    and <code>false</code> otherwise
     * @see #setCanceled
     */
    boolean isCanceled();

    /**
     * Sets the cancel state to the given value.
     *
     * @param value <code>true</code> indicates that cancelation has
     *     been requested (but not necessarily acknowledged);
     *     <code>false</code> clears this flag
     *
     * @see #isCanceled
     */
    void setCanceled(boolean value);

    /**
     * Notifies that some work of the external process has been completed.
     */
    void worked();
}
