/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.io.File;

/**
 * FileWatcher with public onChange. It can be used in the implementation (an initialisations) of other FileWatches
 *
 * @deprecated-now this code exists only to enable the onChange method to be public.
 *      in the original FileWatcher class, this method is protected. The method in that
 *      class need to be changed in scope, and this class needs to be removed.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id: WrappedFileWatcher.java,v 1.3 2005-01-30 16:46:35 nico Exp $
 */
public abstract class WrappedFileWatcher extends FileWatcher {

    /**
     * @javadoc
     */
    abstract public void onChange(File file);
}
