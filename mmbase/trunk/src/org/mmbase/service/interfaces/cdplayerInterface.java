/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.service.interfaces;

import org.mmbase.service.*;

/**
 * @author Daniel Ockeloen
 */
public interface cdplayerInterface extends serviceInterface {
	public String getVersion();
	public boolean getTrack(int number,String filename);
	public String getListCD();
	public String getInfoCDtoString();
}
