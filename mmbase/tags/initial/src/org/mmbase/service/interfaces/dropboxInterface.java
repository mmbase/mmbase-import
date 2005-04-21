/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.service.interfaces;

import org.mmbase.service.*;

/**
 * @author Daniel Ockeloen
 */
public interface dropboxInterface extends serviceInterface {
	public String getVersion();
	public void setCmd(String cmd);
	public void setDir(String dir);
	public void setWWWPath(String path);
	public String doDir(String cmds);
}
