/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.io.File;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * FileWatcher with public onChange. It can be used in the implementation (an initialisations) of other FileWatches
 *
 * @deprecated-now this code exists only to enable the onChange method to be public.
 *      in the original FileWatcher class, this method is protected. The method in that
 *      class need to be changed in scope, and this class needs to be removed.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id: WrappedFileWatcher.java,v 1.2 2004-09-30 17:19:50 pierre Exp $
 */
public abstract class WrappedFileWatcher extends FileWatcher {
    private static Logger log = Logging.getLoggerInstance(WrappedFileWatcher.class);

    /**
     * @javadoc
     */
    abstract public void onChange(File file);
}
