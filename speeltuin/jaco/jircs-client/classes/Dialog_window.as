/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Dialog_window
{
	var lines:Number;
	var lastInput:Array;
	var linesInDialog:Number;
	var lineCounter:Number;
	var settings:Settings;
	var extras:Extras;
	var functions:Functions;
	var chat:ChatBox;
	
	var parent:String;
	var privated:String;
	var id:String;
		
	public function Dialog_window(settings:Settings, chat:ChatBox, command:Command, parent:String, id:String)
	{
		this.chat = chat;
		this.lineCounter = 0;
		this.lines = 5;
		this.lastInput = new Array();
		this.linesInDialog = 0;
		this.settings = settings;
		this.extras = new Extras();
		this.functions = new Functions(null, settings, command, chat);
		
		this.parent = parent;
		
		if(this.parent == "channel")
			_root.chat.channel_window.dialog_window.myDialog.html = true;	
		else  {
			if (this.parent == "private") {
				this.id = id;
				this.privated = "private_" +id
				_root[privated].dialog_window.myDialog.html = true;
			}
		}
		
	}

	public function addMessage(message:String) {
		if(this.parent == "channel")
		{
			if (_root.chat.channel_window.dialog_window.myDialog.scroll == _root.chat.channel_window.dialog_window.myDialog.maxscroll) {
			_root.chat.channel_window.dialog_window.myDialog.htmlText += message;
			_root.chat.channel_window.dialog_window.myDialog.scroll = _root.chat.channel_window.dialog_window.myDialog.maxscroll;
			} else {
				_root.chat.channel_window.dialog_window.myDialog.htmlText += message;
			}
	
			lineCounter++;
			if (lineCounter >= (settings.screenBuffer * 2)) {
				update(settings.channels.getChannel(settings.activeChannel).getDialog());
				lineCounter = 0;    
			}
		}
		else
		{
			if (_root[privated].dialog_window.myDialog.scroll == _root[privated].dialog_window.myDialog.maxscroll) {
			_root[privated].dialog_window.myDialog.htmlText += message;
			_root[privated].dialog_window.myDialog.scroll = _root[privated].dialog_window.myDialog.maxscroll;
			} else {
				_root[privated].dialog_window.myDialog.htmlText += message;
			}
	
			lineCounter++;
			if (lineCounter >= (settings.screenBuffer * 2)) {
				update(settings.privateChannels.getChannel(this.id).getDialog());
				lineCounter = 0;    			
			}
		}
	}

	public function update(messages:String) {
		_root.chat.channel_window.dialog_window.myDialog.htmlText = messages;
		_root.chat.channel_window.dialog_window.myDialog.scroll = _root.chat.channel_window.dialog_window.myDialog.maxscroll;
	}

	function addLine(line:String) {
		this.lastInput.push(line);
		if (this.lastInput.length >= this.lines) {
			this.lastInput.shift();
		}
	}

	public function getPreviousLine() {
	var line:Object = this.lastInput.pop();
		_root.chat.channel_window.dialog_window.myInput.text = line;
		this.lastInput.unshift(line);
	}
	
	public function getNextLine() {
	var line:Object = this.lastInput.shift();
		_root.chat.channel_window.dialog_window.myInput.text = line;
		this.lastInput.push(line);
	}

	public function doInput() {
		if(chat.activePrivID == null)
		{
			if (_root.chat.channel_window.dialog_window.myInput.text != "") {
				var message:String = extras.convertTags(_root.chat.channel_window.dialog_window.myInput.text);
				this.addLine(message);			
				functions.sendMessage(_root.chat.channel_window.txt_id.text,message);
				_root.chat.channel_window.dialog_window.myInput.text = "";
			}
		}
		
		if(chat.activePrivID != null)
		{
			var privated:String = "private_" + chat.activePrivID;
			if (_root[privated].dialog_window.myInput.text != "") {
				var message:String = extras.convertTags(_root[privated].dialog_window.myInput.text);
				this.addLine(message);			
				functions.sendMessage(_root[privated].txt_id.text,message);
				_root[privated].dialog_window.myInput.text = "";
			}
		}
	}
}