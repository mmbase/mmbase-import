/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;


/**
 * Returns the path to use for TREEPART, TREEFILE, LEAFPART and LEAFFILE.
 * The system searches in a provided base path for a filename that matches the supplied number/alias of
 * a node (possibly extended with a version number). See the documentation on the TREEPART SCAN command for more info.
 *
 * This class can be overriden to make an even smarter search possible.
 *
 * @since MMBase-1.8.5
 * @version $Id$
 * @deprecated Use org.mmbase.util.functions.SmartPathFunction
 */
public class SmartPathFunction extends org.mmbase.util.functions.SmartPathFunction {

}


