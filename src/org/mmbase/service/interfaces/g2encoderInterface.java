package org.mmbase.service.interfaces;

import org.mmbase.service.*;

/**
 * @author Daniel Ockeloen
 */
public interface g2encoderInterface extends serviceInterface {
	public String getVersion();
	public String doEncode(String cmds);
}
