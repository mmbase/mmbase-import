/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
button_close.onRelease = function() {
	_parent.hide();
}
*/

class Help_window{
	
	var lastDepth:Number;
	
	public function Help_window()
	{
		this.lastDepth = 0;
	}

	public function display() {
		_root.chat.help_window._visible = true;
		_root.chat.help_window.gotoAndPlay("show");	
	}

	public function hide() {
		this.lastDepth--;
		_root.chat.help_window.gotoAndPlay("hide");	
	}
}