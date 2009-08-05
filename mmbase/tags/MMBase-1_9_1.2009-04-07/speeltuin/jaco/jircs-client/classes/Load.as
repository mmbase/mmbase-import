/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Load
{
	var total_bytes:Number;
	var loaded_bytes:Number;
	var remaining_bytes:Number;
	var percent_done: Number;
	
	function Load()
	{
		total_bytes = _root.getBytesTotal();
		loaded_bytes = _root.getBytesLoaded();
		remaining_bytes = total_bytes-loaded_bytes;
		percent_done = int((loaded_bytes/total_bytes)*100);
		_root.bar.gotoAndStop(percent_done);
		_root.percent_done.text = percent_done;
		
		if (loaded_bytes >= total_bytes) {	
				play();
		}
	}
}


