/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package	org.mmbase.module.irc.communication;

import	org.mmbase.module.irc.communication.*;
import	org.mmbase.module.irc.communication.irc.*;

/**
 * Class Communication
 * 
 * @javadoc
 */

public	class 		Communication
		extends		IrcConnection
		implements	CommunicationInterface	
{
	public Communication( CommunicationUserInterface com  )
	{
		super( com );
	}
}
		
