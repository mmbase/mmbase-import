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
