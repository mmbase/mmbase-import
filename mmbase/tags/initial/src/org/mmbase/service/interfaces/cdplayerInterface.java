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
public interface cdplayerInterface extends serviceInterface {
	public String getVersion();
	public boolean getTrack(int number,String filename);
	public String getListCD();
	public String getInfoCDtoString();
}
