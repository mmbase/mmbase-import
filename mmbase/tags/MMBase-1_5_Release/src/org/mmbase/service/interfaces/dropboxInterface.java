/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.service.interfaces;

import org.mmbase.service.*;

/**
 * @rename DropboxInterface
  * @author Daniel Ockeloen
 */
public interface dropboxInterface extends serviceInterface {
	public String getVersion();
	public void setCmd(String cmd);
	public void setDir(String dir);
	public void setWWWPath(String path);
	public String doDir(String cmds);
}
