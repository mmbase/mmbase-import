/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Moderator_window
{
	var moderator_login:Moderator_login;
	var mod_chan_win:Moderator_channel_window;
	var mod_user_win:Moderator_user_window;
	var focus:String;
	var mode:String;
	var settings:Settings;
	var debug:Debug;
	var commandQueue:CommandQueue;
	
	public function Moderator_window(settings:Settings, command:Command, extras:Extras, commandQueue:CommandQueue)
	{
		this.debug = new Debug(settings)
		this.moderator_login = new Moderator_login(command);
		this.settings = settings;
		this.focus = "users";
		_root.chat.moderator_window.mod_functions._visible = false;
		_root.chat.moderator_window.moderator_login._visible = false;
		
		this.mod_chan_win = new Moderator_channel_window(settings, command);
		this.mod_user_win = new Moderator_user_window(settings, command, extras, commandQueue);
	}
/*
button_close.onRelease = function() {
 	hide();
}
*/
	function addBannedUser(user:String) {
		mod_user_win.addBannedUser(user); 
	}


	function show(mode:String) {
		if (settings.operatorMode) 
		{
			_root.chat.moderator_window.moderator_login._visible = false;
			_root.chat.moderator_window.mod_functions._visible = true;
			this.showWindow(mode);        
		} 
		else
		{
			if (settings.channels.get(settings.activeChannel).operator) 
			{
				_root.chat.moderator_login._visible = false;
				_root.chat.mod_functions._visible = true;
				this.showWindow(mode);        
			} 
			else
			{
				_root.chat.moderator_window.moderator_login._visible = true;
				_root.chat.moderator_window.mod_functions._visible = false;
				moderator_login.listen();  
				Selection.setFocus("_root.chat.moderator_window.moderator_login.input_id");
			}
		}
		_root.chat.moderator_window.swapDepths(900);
		_root.chat.moderator_window._visible = true;
	}

	public function showWindow(target:String) {
		if (target == "") {
			target = _root.chat.moderator_window.mod_functions.select_choosefunction.getSelectedItem().data; 	
		} else {
			var len:Number = _root.chat.moderator_window.mod_functions.select_choosefunction.getLength();
			for (var i =0;i<len;i++) {
				if (_root.chat.moderator_window.mod_functions.select_choosefunction.getSelectedItem().data == target) {
					_root.chat.moderator_window.mod_functions.select_choosefunction.setSelectedIndex(i);		
				}
			}
		}
		this.doShow(target);
	}

	public function doShow(target:String) {
		if (target == 'users') {
			this.mod_user_win.show();
			_root.chat.moderator_window.mod_functions.channel_functions._visible = false;
			_root.chat.moderator_window.mod_functions.user_functions._visible = true;
		}
		if (target =='channels') {
			this.mod_chan_win.show();
			_root.chat.moderator_window.mod_functions.channel_functions._visible = true;
			_root.chat.moderator_window.mod_functions.user_functions._visible = false;
		}
	}
}