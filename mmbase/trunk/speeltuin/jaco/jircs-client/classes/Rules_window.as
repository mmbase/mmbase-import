/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Rules_window
{
	public function Rules_window()
	{
	
	}
	/*
button_close.onRelease = function() {
	_parent.hide();
}
*/
	public function display() {
		_parent.lastDepth++;
		_root.rules_window._visible = true;
		_root.rules_window.gotoAndPlay("show");	
	}


	public function hide() {
		_parent.lastDepth--;
		_root.rules_window.gotoAndPlay("hide");	
	}
}