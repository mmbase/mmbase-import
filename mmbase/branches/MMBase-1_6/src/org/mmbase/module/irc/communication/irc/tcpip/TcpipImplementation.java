/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.irc.communication.irc.tcpip;

import org.mmbase.module.irc.communication.*;
import org.mmbase.module.irc.communication.irc.tcpip.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Class TcpipImplementation
 * 
 * @javadoc
 */

public	class TcpipImplementation implements CommunicationUserInterface {
    private static Logger log = Logging.getLoggerInstance(TcpipImplementation.class.getName());

	public void receive( String msg ) {
		log.info("receive("+msg+")");
	}
}
