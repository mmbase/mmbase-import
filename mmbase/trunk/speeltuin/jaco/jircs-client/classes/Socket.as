/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Socket
{
	var XMLsocket:XMLSocket;
	var settings:Settings;
	var debug:Debug;
	var messages:Messages;
	
	public function Socket(settings:Settings)
	{ 
		this. XMLsocket = new XMLSocket();
		this.messages = new Messages();
		this.settings = settings;
		this.debug = new Debug();
	}
/*
socket.onClose = function() {
	_global.errors = ERROR_CONNECTIONLOST;
	updateDialog(activeChannel,errors,"");	
	_global.loggedOn = false;
	_global.connected = false;
	sendDebug("onSocketClose: socket closed by server");
}


*/

	public function connect() {
		debug.sendDebug("Establishing Connection... Host: " + settings.myHost + " Port: " + settings.myPort)
		if (!settings.connected) {
			if (!XMLsocket.connect(settings.myHost, settings.myPort)) {
				debug.sendDebug("Connection NOT established... Host: " + settings.myHost + " Port: " + settings.myPort)
				settings.errors = messages.ERROR_NOCONNECTION;
			}			
			else {
				debug.sendDebug("Connection established... Host: " + settings.myHost + " Port: " + settings.myPort)
			}
		}
	}

	public function sendXML(xml:XML) {
		XMLsocket.send(xml);
		debug.sendDebug("sendXml sending: " + xml.toString());	
	}

	public function closeSocket() {
		settings.connected = false;
		settings.loggedOn = false;
		 XMLsocket.close();	
		debug.sendDebug("closeSocket: closing socket");	
	}
}