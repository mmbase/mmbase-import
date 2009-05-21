/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

/**
 * @javadoc
 *
 * @author Rico Jansen
 * @version $Id$
 */
public interface TemporaryNodeManagerInterface {
    public String createTmpNode(String type, String owner, String key);
    public String createTmpRelationNode(String type, String owner, String key, String source, String destination) throws Exception;
    public String createTmpAlias(String name, String owner, String key, String destination);
    public String deleteTmpNode(String owner, String key);
    public MMObjectNode getNode(String owner, String key);
    public String getObject(String owner, String key, String dbkey);
    public String setObjectField(String owner, String key, String field, Object value);
    public String getObjectFieldAsString(String owner, String key, String field);
    public Object getObjectField(String owner, String key, String field);
}
