/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Moderator_login
{
	var command:Command;
	
	public function Moderator_login(command:Command)
	{
		this.command = command;
	}
	
	function listen()
	{}
		
	function doLogin() {
		if (_root.chat.moderator_window.moderator_login.input_id.text != "" && _root.chat.moderator_window.moderator_login.input_pass.text != "") {
			command.sendCommand("OPER",[_root.chat.moderator_window.moderator_login.input_id.text,_root.chat.moderator_window.moderator_login.input_pass.text]);
			_root.chat.moderator_window.moderator_login.input_id.text = "";
			_root.chat.moderator_window.moderator_login.input_pass.text = "";
			_root.chat.moderator_window._visible = false;	
		}	
	}
}