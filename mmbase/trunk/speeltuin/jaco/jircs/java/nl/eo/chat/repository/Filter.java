/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat.repository;

/**
 * A filter can be used to filter out messages that aren't allowed.
 *
 * @author Jaco de Groot
 */
public interface Filter {

    public boolean allow(String text);

}

