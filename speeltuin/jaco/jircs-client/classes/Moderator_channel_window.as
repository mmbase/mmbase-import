/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Moderator_channel_window
{
	var text_channelfunctions:String;
	var text_channel:String;
	var text_action:String;
	var text_optional:String;
	var text_ok:String;
	var array_info:Array;
	var settings:Settings;
	var command:Command;
	
	var action:String;
	
	var topic:String;
	var create:String;
	var close:String;
	var limit:String;
	var nolimit:String;
	var moderated:String;
	var demoderated:String;
	var onderwerp:String;
	var onderwerpOpt:String;
	var limiet:String;
	
	public function Moderator_channel_window(settings:Settings, command:Command)
	{
		this.text_channelfunctions = "Kanaal functies";
		this.text_channel = "Kanaal:";
		this.text_action = "Actie:";
		this.text_optional = "*=optioneel";
		this.text_ok = "ok";
		
		this.action = "";
		
		this.settings = settings;
		this.command = command;
		
		this.topic ="Verandert het onderwerp van het kanaal.";
		this.create ="Maakt een nieuw kanaal aan.";
		this.close ="Sluit het kanaal als de laatste gebruiker weg gaat.";
		this.limit ="Zet een limiet op het aantal gebruikers in een kanaal.";
		this.nolimit ="Verwijder de limiet op het aantal gebruikers in een kanaal.";
		this.moderated ="Zorgt ervoor dat er geen berichten meer naar het kanaal gestuurd kunnen worden.";
		this.demoderated ="Zorgt ervoor dat er weer berichten naar het kanaal gestuurd kunnen worden.";		
		this.onderwerp = "Onderwerp:";
		this.onderwerpOpt = "Onderwerp*:"
		this.limiet = "Limiet:"
		
	//	_root.chat.moderator_window.mod_functions.channel_functions.action_kick.setLabel(this.text_kickuser);
	//	_root.chat.moderator_window.mod_functions.channel_functions.action_ban.setLabel(this.text_banuser);
	//	_root.chat.moderator_window.mod_functions.channel_functions.action_unban.setLabel(this.text_unbanuser);
		_root.chat.moderator_window.mod_functions.channel_functions.button_ok.setLabel(this.text_ok);
		
		var myTextFormat:TextFormat = new TextFormat();
		myTextFormat.font = "Verdana";
		
		_root.chat.moderator_window.mod_functions.channel_functions.list_actions.setAutoHideScrollBar(true);
		
		_root.chat.moderator_window.mod_functions.channel_functions.field_channel.setTextFormat(myTextFormat);
		_root.chat.moderator_window.mod_functions.channel_functions.field_users.setTextFormat(myTextFormat);
		_root.chat.moderator_window.mod_functions.channel_functions.field_userfunctions.setTextFormat(myTextFormat);
		_root.chat.moderator_window.mod_functions.channel_functions.field_reason.setTextFormat(myTextFormat);
		_root.chat.moderator_window.mod_functions.channel_functions.field_optional.setTextFormat(myTextFormat);
		_root.chat.moderator_window.mod_functions.channel_functions.field_action.setTextFormat(myTextFormat);
		_root.chat.moderator_window.mod_functions.channel_functions.field_info.setTextFormat(myTextFormat);
		_root.chat.moderator_window.mod_functions.channel_functions.field_title.setTextFormat(myTextFormat);
		_root.chat.moderator_window.mod_functions.channel_functions.input_reason.setTextFormat(myTextFormat);	
		
		_root.chat.moderator_window.mod_functions.channel_functions.input_channel.text = "";
		_root.chat.moderator_window.mod_functions.channel_functions.input_channel.type = "dynamic";
		_root.chat.moderator_window.mod_functions.channel_functions.input_channel.border = false;
		_root.chat.moderator_window.mod_functions.channel_functions.input_channel.background = false;
		_root.chat.moderator_window.mod_functions.channel_functions.input_channel.backgroundColor = "";
		_root.chat.moderator_window.mod_functions.channel_functions.input_channel._visible = false;
		
		_root.chat.moderator_window.mod_functions.channel_functions.sign_3._visible = false;
		_root.chat.moderator_window.mod_functions.channel_functions.sign_4._visible = false;
		_root.chat.moderator_window.mod_functions.channel_functions.sign_3_4._visible = true;
		
		_root.chat.moderator_window.mod_functions.channel_functions.field_optional.text = text_optional;
	}
	
	public function show() {
		_root.chat.moderator_window.mod_functions.channel_functions.list_actions.removeAll()
		_root.chat.moderator_window.mod_functions.channel_functions.list_actions.addItem("onderwerp veranderen","topic");
		if (settings.operatorMode) {
			_root.chat.moderator_window.mod_functions.channel_functions.list_actions.addItem("kanaal aanmaken","create");
		}
		_root.chat.moderator_window.mod_functions.channel_functions.list_actions.addItem("kanaal sluiten","close");
		_root.chat.moderator_window.mod_functions.channel_functions.list_actions.addItem("kanaal limiet","limit");
		_root.chat.moderator_window.mod_functions.channel_functions.list_actions.addItem("geen kanaal limiet","nolimit");
		_root.chat.moderator_window.mod_functions.channel_functions.list_actions.addItem("geen berichten","moderated");
		_root.chat.moderator_window.mod_functions.channel_functions.list_actions.addItem("wel berichten","demoderated");	
		_root.chat.moderator_window.mod_functions.channel_functions._visible = true;
		this.updateChannels();
	}
	
	public function hide() {
		_root.chat.moderator_window.mod_functions.channel_functions._visible = false;	
	}
	
	
	public function chooseAction() {
	    this.action = _root.chat.moderator_window.mod_functions.channel_functions.list_actions.getSelectedItem().data;
	    
	    switch (this.action) {
		case "topic":
		    _root.chat.moderator_window.mod_functions.channel_functions.field_extra.text = this.onderwerp;
		    _root.chat.moderator_window.mod_functions.channel_functions.field_info.text = this.topic;
		    _root.chat.moderator_window.mod_functions.channel_functions.sign_3._visible = true;
		    _root.chat.moderator_window.mod_functions.channel_functions.sign_4._visible = true;
		    _root.chat.moderator_window.mod_functions.channel_functions.sign_3_4._visible = false;
		    _root.chat.moderator_window.mod_functions.channel_functions.input_extra.type = "input";
		    _root.chat.moderator_window.mod_functions.channel_functions.input_extra.border = true;
				_root.chat.moderator_window.mod_functions.channel_functions.input_extra.background = true;
				_root.chat.moderator_window.mod_functions.channel_functions.input_extra.backgroundColor = 0xFFFFFF;
				_root.chat.moderator_window.mod_functions.channel_functions.input_channel._visible = false;
				_root.chat.moderator_window.mod_functions.channel_functions.list_channels._visible = true;

		    _root.chat.moderator_window.mod_functions.channel_functions.input_extra.restrict = "a-z A-Z 0-9 !?.,@*():;";
		    break;
		case "create":
		    _root.chat.moderator_window.mod_functions.channel_functions.field_extra.text = this.onderwerpOpt;
		    _root.chat.moderator_window.mod_functions.channel_functions.field_info.text = this.create;
		    _root.chat.moderator_window.mod_functions.channel_functions.sign_3._visible = true;
		    _root.chat.moderator_window.mod_functions.channel_functions.sign_4._visible = true;
		    _root.chat.moderator_window.mod_functions.channel_functions.sign_3_4._visible = false;        
		    _root.chat.moderator_window.mod_functions.channel_functions.input_extra.type = "input";
		    _root.chat.moderator_window.mod_functions.channel_functions.input_extra.border = true;
				_root.chat.moderator_window.mod_functions.channel_functions.input_extra.background = true;
				_root.chat.moderator_window.mod_functions.channel_functions.input_extra.backgroundColor = 0xFFFFFF;
		    _root.chat.moderator_window.mod_functions.channel_functions.input_extra.restrict = "a-z A-Z 0-9 !?.,@*():;";      
	
				_root.chat.moderator_window.mod_functions.channel_functions.input_channel.type = "input";
				_root.chat.moderator_window.mod_functions.channel_functions.input_channel.border = true;
				_root.chat.moderator_window.mod_functions.channel_functions.input_channel.background = true;
				_root.chat.moderator_window.mod_functions.channel_functions.input_channel.backgroundColor = 0xFFFFFF;			
				_root.chat.moderator_window.mod_functions.channel_functions.input_channel._visible = true;
				_root.chat.moderator_window.mod_functions.channel_functions.list_channels._visible = false;
				

		    break;    
		case "limit":
			_root.chat.moderator_window.mod_functions.channel_functions.field_extra.text = this.limiet;
			_root.chat.moderator_window.mod_functions.channel_functions.field_info.text = this.limit;
		    _root.chat.moderator_window.mod_functions.channel_functions.sign_3._visible = true;
		    _root.chat.moderator_window.mod_functions.channel_functions.sign_4._visible = true;
		    _root.chat.moderator_window.mod_functions.channel_functions.sign_3_4._visible = false;        
		    _root.chat.moderator_window.mod_functions.channel_functions.input_extra.type = "input";
		    _root.chat.moderator_window.mod_functions.channel_functions.input_extra.border = true;
				_root.chat.moderator_window.mod_functions.channel_functions.input_extra.background = true;
				_root.chat.moderator_window.mod_functions.channel_functions.input_extra.backgroundColor = 0xFFFFFF;			
		    _root.chat.moderator_window.mod_functions.channel_functions.input_extra.restrict = "0-9";
				_root.chat.moderator_window.mod_functions.channel_functions.input_channel._visible = false;
				_root.chat.moderator_window.mod_functions.channel_functions.list_channels._visible = true;

		    break;   
		case "nolimit":
		_root.chat.moderator_window.mod_functions.channel_functions.field_extra.text = "";
		_root.chat.moderator_window.mod_functions.channel_functions.field_info.text = this.nolimit;
		    _root.chat.moderator_window.mod_functions.channel_functions.sign_3._visible = false;
		    _root.chat.moderator_window.mod_functions.channel_functions.sign_4._visible = false;
		    _root.chat.moderator_window.mod_functions.channel_functions.sign_3_4._visible = true;        
		    _root.chat.moderator_window.mod_functions.channel_functions.input_extra.type = "dynamic";
		    _root.chat.moderator_window.mod_functions.channel_functions.input_extra.border = false;
				_root.chat.moderator_window.mod_functions.channel_functions.input_extra.background = false;
				_root.chat.moderator_window.mod_functions.channel_functions.input_extra.backgroundColor = "";			
				_root.chat.moderator_window.mod_functions.channel_functions.input_channel._visible = false;
				_root.chat.moderator_window.mod_functions.channel_functions.list_channels._visible = true;

		    break;   
		case "close":
		_root.chat.moderator_window.mod_functions.channel_functions.field_extra.text = "";
		_root.chat.moderator_window.mod_functions.channel_functions.field_info.text = this.close;
		_root.chat.moderator_window.mod_functions.channel_functions.sign_3._visible = false;
		_root.chat.moderator_window.mod_functions.channel_functions.sign_4._visible = false;
		_root.chat.moderator_window.mod_functions.channel_functions.sign_3_4._visible = true;        
		_root.chat.moderator_window.mod_functions.channel_functions.input_extra.type = "dynamic";
		_root.chat.moderator_window.mod_functions.channel_functions.input_extra.border = false;
			_root.chat.moderator_window.mod_functions.channel_functions.input_extra.background = false;
			_root.chat.moderator_window.mod_functions.channel_functions.input_extra.backgroundColor = "";		
			_root.chat.moderator_window.mod_functions.channel_functions.input_channel._visible = false;
			_root.chat.moderator_window.mod_functions.channel_functions.list_channels._visible = true;
			
		break;
		case "moderated":
		_root.chat.moderator_window.mod_functions.channel_functions.field_info.text = this.moderated;
		break;
		case "demoderated":
		_root.chat.moderator_window.mod_functions.channel_functions.field_info.text = this.demoderated;
		break;
		default:
		_root.chat.moderator_window.mod_functions.channel_functions.field_info.text = this.topic;
		    _root.chat.moderator_window.mod_functions.channel_functions.sign_3._visible = false;
		    _root.chat.moderator_window.mod_functions.channel_functions.sign_4._visible = false;
		    _root.chat.moderator_window.mod_functions.channel_functions.sign_3_4._visible = true;        
		    _root.chat.moderator_window.mod_functions.channel_functions.input_extra.type = "dynamic";
		    _root.chat.moderator_window.mod_functions.channel_functions.input_extra.border = false;
				_root.chat.moderator_window.mod_functions.channel_functions.input_extra.background = false;
				_root.chat.moderator_window.mod_functions.channel_functions.input_extra.backgroundColor = "";				
		    _root.chat.moderator_window.mod_functions.channel_functions.input_extra.text = "";
		
				_root.chat.moderator_window.mod_functions.channel_functions.input_channel._visible = false;
				_root.chat.moderator_window.mod_functions.channel_functions.list_channels._visible = true;
		    break;             
		
	    }        
	}
	
	function updateChannels() {
		_root.chat.moderator_window.mod_functions.channel_functions.list_channels.removeAll();
		for(var item:String in settings.channels.items) {
			if (settings.operatorMode || settings.channels.getChannel(item).operator) {
				_root.chat.moderator_window.mod_functions.channel_functions.list_channels.addItem(item,item);
			}
		}
		for (var i:Number=0;i<_root.chat.moderator_window.mod_functions.channel_functions.list_channels.getRowCount();i++) {
			if (_root.chat.moderator_window.mod_functions.channel_functions.list_channels.getItemAt(i).data == settings.activeChannel) {
				_root.chat.moderator_window.mod_functions.channel_functions.list_channels.setSelectedIndex(i);
			}
		}
	}
	
	function doAction() {
		var selected_channel = _root.chat.moderator_window.mod_functions.channel_functions.list_channels.getSelectedItem().data;
		switch (this.action) {
			case "create":
				if (_root.chat.moderator_window.mod_functions.channel_functions.input_channel.text != "") {
					var channel = "#" + _root.chat.moderator_window.mod_functions.channel_functions.input_channel.text;
					_root.chat.moderator_window.mod_functions.channel_functions.input_channel.text = "";				
					command.sendCommand("JOIN",[channel]);
					if (_root.chat.moderator_window.mod_functions.channel_functions.input_extra.text != "") {
						command.sendCommand("TOPIC",[channel,_root.chat.moderator_window.mod_functions.channel_functions.input_extra.text]);				
					_root.chat.moderator_window.mod_functions.channel_functions.input_extra.text = "";
					   
					} 
					_root.chat.moderator_window.hide();
				}
				break;
			case "topic":
				if (selected_channel != "" && selected_channel != undefined) {
					command.sendCommand("TOPIC",[selected_channel,_root.chat.moderator_window.mod_functions.channel_functions.input_extra.text]);
					_root.chat.moderator_window.mod_functions.channel_functions.input_extra.text = "";
					_root.chat.moderator_window.hide();				
				}
				break;
		    case "close":
		    if (selected_channel != "" && selected_channel != undefined) {
			command.sendCommand("MODE",[selected_channel,"+d"]);
			_root.chat.moderator_window.hide();
		    }
			break;
		    case "limit":
		    if (selected_channel != "" && selected_channel != undefined) {
			command.sendCommand("MODE",[selected_channel,"+l",_root.chat.moderator_window.mod_functions.channel_functions.input_extra.text]);
			_root.chat.moderator_window.mod_functions.channel_functions.input_extra.text = "";
			_root.chat.moderator_window.hide();
			}
			break;
		    case "nolimit":
		    if (selected_channel != "" && selected_channel != undefined) {
			command.sendCommand("MODE",[selected_channel,"-l"]);
			_root.chat.moderator_window.hide();
			}
			break;			
		    case "moderated":
		    if (selected_channel != "" && selected_channel != undefined) {
			command.sendCommand("MODE",[selected_channel,"+m"]);
		       _root.chat.moderator_window.hide();
		    }
			break;
		    case "demoderated":
		    if (selected_channel != "" && selected_channel != undefined) {
			command.sendCommand("MODE",[selected_channel,"-m"]);
			_root.chat.moderator_window.hide();
		    }
			break;	        
			default:
				break;
		} 	
	}
}