/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Private
{
	var id:String;
	var privated:String;
	var settings:Settings;
	var functions:Functions;
	var chat:ChatBox;
	var dialog_window:Dialog_window;
	var colorsel:Colorselector;
	
	public function Private(id:String, chat:ChatBox)
	{
		this.chat = chat;
		
		this.settings = chat.settings;
		this.functions = chat.functions;
				
		this.id = id;
		this.privated = "private_" + this.id;
		
		this.colorsel = new Colorselector(null, this);
		
		this.dialog_window = new Dialog_window(settings, chat, chat.command, "private", this.id);
		
		if (settings.textColorEnabled) {
			_root[privated].default_selection_button._visible = true;
			_root[privated].text_select_color._visible = true;
			_root[privated].options_bg._visible = true;
		} 
		else {
			_root[privated].default_selection_button._visible = false;
			_root[privated].text_select_color._visible = false;
			_root[privated].colorselector._visible = false;
			_root[privated].options_bg._visible = false;
		}		
	}

	public function setButtonColor(color) {
		if (settings.textColorEnabled) {
			_root[privated].dialog_window.myInput.textColor = "0x" + settings.defaultColors[color][1];
			functions.setTextColor(id,color);
			_root[privated].default_selection_button.assignScript(color);
		}
	}

	public function button_optionsPress(){
		//colorselector.show();
	}

	public function hide() {
		_root[privated]._visible = false;
	}
	
	public function show() {
		_root[privated]._visible = true;
	}
	
	public function close() {
		_root[privated]._visible = false;
		functions.closePrivateChat(this.id);	
	}
	
	
	public function buttonTopPress()	{
		chat.showPrivate(id);
		startDrag(_root[privated],false,-116,-250,419,220);
	}
	
	public function buttonTopOnRelease() {
		chat.lastDepth--;
		stopDrag();	
	}
	
	public function buttonTopReleaseOut() {
		chat.lastDepth--;
		stopDrag();	
	}
}