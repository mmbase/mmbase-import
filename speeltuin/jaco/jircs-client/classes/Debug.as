/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Debug
{
	var settings:Settings;
	
	public function Debug()
	{
		this.settings = new Settings();
	}

	public function sendDebug(message:String) {
		if (settings.debug) {
			trace(message);
		}
	}
}