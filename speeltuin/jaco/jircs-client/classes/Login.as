/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Login
{
	var settings:Settings;
	var socket:Socket;
	var functions:Functions;
	var messages:Messages;
	var debug:Debug;
	var command:Command;
	
	public function Login()	
	{
		this.debug = new Debug();
		this.messages = new Messages();
		this.settings = new Settings();
		this.socket = new Socket(settings);
		this.command = new Command(null,null,null,null,null,settings,socket,null);
		this.functions = new Functions(socket, settings, command, null);
		
		_root.background_chat.my_Version.text = settings.chat_version;
	}

	function checkSettings()
	{
		if (settings.myPassword != "") {
			_root.login_window.myPasswordField._visible = false;
			_root.login_window.myPasswordInput._visible = false;
		}
		if (settings.myHost != "") {
			_root.login_window.myHostField._visible = false;
			_root.login_window.myHostInput._visible = false;
		}
		
		_root.login_window.myNickInput.tabIndex = 1;
		_root.login_window.myPasswordInput.tabIndex = 2;
		_root.login_window.myHostInput.tabIndex = 3;
		
		_root.login_window.myNickInput.maxChars = 12;
		
		_root.login_window.myNickInput.restrict = "A-Z a-z 0-9";
	}
	
	public function doLogin() {
		if ((_root.login_window.myNickInput.text != "")  ||  (settings.myUsername != "")){
			if(settings.myUsername == "")
				settings.myUsername = _root.login_window.myNickInput.text;
			if (_root.login_window.myPasswordInput._visible) {
				settings.myPassword = _root.login_window.myPasswordInput.text;
			}
			if (_root.login_window.myHostInput._visible) {
				settings.myHost = _root.login_window.myHostInput.text;
			}
			this.socket.connect();	
		}
		_root.gotoAndPlay("check");
	}

	public function checkUserFill()
	{
		if (settings.myUsername != "") {	
			this.doLogin();
		}
	}
	
	public function checkData()
	{
		if (settings.loggedOn) {
			Key.removeListener(_root.myListener1);
			_root.gotoAndPlay("gotochat");
		} else if (settings.errors != "" ) {
			_root.gotoAndStop("logout");
		} else {
			if (settings.tries < 20) {
				_root.gotoAndPlay("check");		
				settings.tries++;
			} else {
				settings.tries = 0;
				settings.errors = messages.ERROR_NOCONNECTION;
			}
		}	
	}
	
	public function setError()
	{
		_root.logout_window.myError.text = settings.errors;
	}
}