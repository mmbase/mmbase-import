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
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 */
public abstract class WrappedFileWatcher extends FileWatcher{
    private static Logger log = Logging.getLoggerInstance(WrappedFileWatcher.class);

    abstract public void onChange(File file);

}
