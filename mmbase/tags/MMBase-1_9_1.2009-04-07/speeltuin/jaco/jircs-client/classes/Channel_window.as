/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Channel_window
{
	var id:String;
	var topic:String;
	var showWindow:Boolean;
	var hideWindow:Boolean;
	var showHide:Boolean;	
	var settings:Settings;
	var functions:Functions;
	var dialog_window:Dialog_window;
	var colorsel:Colorselector;
	var chat:ChatBox;
	
	public function Channel_window(settings:Settings, chat:ChatBox, command:Command,functions:Functions)
	{
		colorsel = new Colorselector(this, null);
		showWindow = false;
		hideWindow = false;
		showHide =true;
		this.settings = settings;
		
		this.functions = functions;
		this.chat = chat;
		
		this.dialog_window = new Dialog_window(settings, chat, command, "channel", null);
		
		if (settings.textColorEnabled) {
			_root.chat.channel_window.default_selection_button._visible = true;
			_root.chat.channel_window.text_select_color._visible = true;
			_root.chat.channel_window.options_bg._visible = true;
		} else {
			_root.chat.channel_window.default_selection_button._visible = false;
			_root.chat.channel_window.text_select_color._visible = false;
			_root.chat.channel_window.colorselector._visible = false;
			_root.chat.channel_window.options_bg._visible = false;
		}
	}
/*
button_ok.onRelease = function () {
	dialog_window.doInput();
}
*/
	public function setButtonColor(color:String) {
		if (settings.textColorEnabled) {
			_root.chat.channel_window.dialog_window.myInput.textColor = "0x" + settings.defaultColors[color][1];
			_root.chat.channel_window.dialog_window.myInput.textBold = true;
			functions.setTextColor(id,color);
			_root.chat.channel_window.default_selection_button.assignScript(color);
		}
	}

	public function show() {
		_root.chat.channel_window._visible = true;
		_root.chat.channel_window.txt_id._visible = true;
		_root.chat.channel_window.txt_topic._visible = true;
		_root.chat.channel_window.txt_lastmessage._visible = true;
		_root.chat.channel_window.dialog_window._visible = true;
		this.showWindow = true;	
		this.hideWindow = false;
		_root.chat.channel_window.play();
	}

	public function hide() {
		_root.chat.channel_window.txt_id._visible = false;
		_root.chat.channel_window.txt_topic._visible = false;
		_root.chat.channel_window.txt_lastmessage._visible = false;
		_root.chat.channel_window.dialog_window._visible = false;
		this.showWindow = false;
		this.hideWindow = true;	
		_root.chat.channel_window.play();
	}
	
	public function enterFrame()
	{
		if (_root.chatbox.channel_win.showWindow) {
			if (_root.chat.channel_window._alpha < 100) {
				_root.chat.channel_window._alpha += 10;
			}
			if (_root.chat.channel_window._alpha == 100) {
				_root.chatbox.channel_win.stop();
				Selection.setFocus("_root.channel_window.dialog_window.myInput");
			}
		}
		
		if (_root.chatbox.channel_win.hideWindow) {
			if (_root.chat.channel_window._alpha > 0) {
				_root.chat.channel_window._alpha -= 10;
			}
			if (_root.chat.channel_window._alpha <= 0) {
				_root.chat.channel_window._visible = false;
				if (_root.chatbox.channel_win.showHide) {
					_root.chatbox.channel_win.showHide = false;
					_root.chatbox.channel_win.show();			
				} else {
					_root.chatbox.channel_win.stop();	
				}
			}
		}	
	}
}