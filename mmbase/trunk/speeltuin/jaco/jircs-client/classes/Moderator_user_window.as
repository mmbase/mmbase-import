/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Moderator_user_window 
{
	var text_userfunctions:String;
	var text_channel:String;
	var text_user:String;
	var text_action:String;
	var text_reason:String;
	var text_optional:String;
	var text_kickuser:String;
	var text_banuser:String;
	var text_unbanuser:String;
	var text_oper:String;
	var text_deoper:String;
	var text_info:String;
	var text_info_title:String;
	var text_ok:String;
	
	var kick:Boolean;
	var ban:Boolean;
	var unban:Boolean;
	var oper:Boolean;
	var deoper:Boolean;
	
	var kickStr:String;
	var banStr:String;
	var unbanStr:String;
	var operStr:String
	var deoperStr:String

	var settings:Settings;
	var command:Command;
	var extras:Extras;
	var commandQueue:CommandQueue;
	
	public function Moderator_user_window(settings:Settings, command:Command, extras:Extras, commandQueue:CommandQueue) 
	{
		this.settings = settings;
		this.command = command;
		this.extras = extras;
		this.commandQueue = commandQueue;
		
		 this.text_userfunctions = "Gebruikers functies";
		this.text_channel = "Kanaal:";
		this.text_user = "Gebruiker:";
		this.text_action = "Actie:";
		this.text_reason = "Geef een reden op*:";
		this.text_optional = "*=optioneel";
		this.text_kickuser = "Gebruiker verwijderen";
		this.text_banuser = "Gebruiker blokkeren";
		this.text_unbanuser = "Gebruiker de-blokkeren";
		this.text_oper = "Moderatorstatus toekennen";
		this.text_deoper = "Moderatorstatus verwijderen";
		this.text_info_title = "Info:";
		this.text_ok = "ok";
		
		this.kickStr="Verwijder een gebruiker direct van het kanaal.";
		this.banStr="Blokkeer de toegang van het kanaal voor de gebruiker.";
		this.unbanStr="Laat een gebruiker weer toe op een kanaal.";
		this.operStr="Maak een gebruiker moderator van een kanaal.";
		this.deoperStr="Maak een gebruiker geen moderator meer van een kanaal.";
		
		_root.chat.moderator_window.mod_functions.user_functions.action_kick.setLabel(text_kickuser);
		_root.chat.moderator_window.mod_functions.user_functions.action_ban.setLabel(text_banuser);
		_root.chat.moderator_window.mod_functions.user_functions.action_unban.setLabel(text_unbanuser);
		_root.chat.moderator_window.mod_functions.user_functions.action_oper.setLabel(text_oper);
		_root.chat.moderator_window.mod_functions.user_functions.action_deoper.setLabel(text_deoper);
		_root.chat.moderator_window.mod_functions.user_functions.button_ok.setLabel(text_ok);
		
		var myTextFormat:TextFormat = new TextFormat();
		myTextFormat.font = "Verdana";
		
		_root.chat.moderator_window.mod_functions.user_functions.field_channel.setTextFormat(myTextFormat);
		_root.chat.moderator_window.mod_functions.user_functions.field_users.setTextFormat(myTextFormat);
		_root.chat.moderator_window.mod_functions.user_functions.field_userfunctions.setTextFormat(myTextFormat);
		_root.chat.moderator_window.mod_functions.user_functions.field_reason.setTextFormat(myTextFormat);
		_root.chat.moderator_window.mod_functions.user_functions.field_optional.setTextFormat(myTextFormat);
		_root.chat.moderator_window.mod_functions.user_functions.field_action.setTextFormat(myTextFormat);
		_root.chat.moderator_window.mod_functions.user_functions.input_reason.setTextFormat(myTextFormat);
		
		this.kick = false;
		this.ban = false;
		this.unban = false;
		this.oper = false;
		this.deoper = false;
	}
	/*
	public function button_channelfunctions.onRelease = function() {
	    _parent.show();
		hide();
	}
	*/
	public function show() {
		_root.chat.moderator_window.mod_functions.user_functions._visible = true;
		this.updateChannels();
		
		//set the selected channel in the list to the active channel
		for (var i:Number=0;i<_root.chat.moderator_window.mod_functions.user_functions.list_channels.getLength();i++) {
			if (_root.chat.moderator_window.mod_functions.user_functions.list_channels.getItemAt(i).label ==settings.activeChannel) {
				_root.chat.moderator_window.mod_functions.user_functions.list_channels.setSelectedIndex(i);
				break;
			}
		}
		//set the selected user to the user selected in the main window
		if (_root.chat.userlist_window.myList.getSelectedItem() != undefined) {
			for (var i:Number=0;i<_root.chat.moderator_window.mod_functions.user_functions.list_users.getLength();i++) {
				if (_root.chat.moderator_window.mod_functions.user_functions.list_users.getItemAt(i).label == _root.chat.userlist_window.myList.getSelectedItem().label) {
					_root.chat.moderator_window.mod_functions.user_functions.list_users.setSelectedIndex(i);
					break;
				}
			}
		}
	}
	
	public function hide() {
		_root.chat.moderator_window.mod_functions.user_functions._visible = false;	
	}
	
	function channelChange(component) {
		if (settings.channels.getChannel(_root.chat.moderator_window.mod_functions.user_functions.list_channels.getSelectedItem().label) != undefined) {
			this.updateUsers();
		}
	}
	
	public function getBanList() {
		if (_root.chat.moderator_window.mod_functions.user_functions.list_channels.getSelectedItem().data != undefined || _root.chat.moderator_window.mod_functions.user_functions.list_channels.getSelectedItem().data != "")  {
			_root.chat.moderator_window.mod_functions.user_functions.list_users.removeAll();
		    command.sendCommand("MODE",[_root.chat.moderator_window.mod_functions.user_functions.list_channels.getSelectedItem().data,"+b"]);
		}
	}
	
	public function addBannedUser(user) {
		_root.chat.moderator_window.mod_functions.user_functions.list_users.addItem(user.substring(user.indexOf("@"),user.length),user);
	}
	
	public function kickUser(component) {
		this.kick = component.getValue();
	    if (this.kick) {
		if (_root.chat.moderator_window.mod_functions.user_functions.action_unban.getValue()) {
				unban = !_root.chat.moderator_window.mod_functions.user_functions.action_unban.getValue();
				_root.chat.moderator_window.mod_functions.user_functions.action_unban.setValue(false);
			} 
		if (_root.chat.moderator_window.mod_functions.user_functions.action_deoper.getValue()) {
		    _root.chat.moderator_window.mod_functions.user_functions.action_deoper.setValue(false);
		}
		if (_root.chat.moderator_window.mod_functions.user_functions.action_oper.getValue()) {
		    _root.chat.moderator_window.mod_functions.user_functions.action_oper.setValue(false);
		}		
		 _root.chat.moderator_window.mod_functions.user_functions.field_info.text = this.kickStr;
		}       
	}
	
	public function banUser(component) {
		 this.ban = component.getValue();
	    if (this.ban) {
		if (!_root.chat.moderator_window.mod_functions.user_functions.action_kick.getValue()) {
				var kick:Boolean = true;
				_root.chat.moderator_window.mod_functions.user_functions.action_kick.setValue(true);
			}
		if (_root.chat.moderator_window.mod_functions.user_functions.action_unban.getValue()) {
				unban = !_root.chat.moderator_window.mod_functions.user_functions.action_unban.getValue();
				_root.chat.moderator_window.mod_functions.user_functions.action_unban.setValue(false);
		}
		if (_root.chat.moderator_window.mod_functions.user_functions.action_deoper.getValue()) {
		    _root.chat.moderator_window.mod_functions.user_functions.action_deoper.setValue(false);
		}
		if (_root.chat.moderator_window.mod_functions.user_functions.action_oper.getValue()) {
		    _root.chat.moderator_window.mod_functions.user_functions.action_oper.setValue(false);
		}		
		 _root.chat.moderator_window.mod_functions.user_functions.field_info.text = this.banStr;
		}
	}
	
	public function unBanUser(component) {
		this.unban = component.getValue();
		if (this.unban) {
		 _root.chat.moderator_window.mod_functions.user_functions.field_info.text = this.unbanStr;
		if (_root.chat.moderator_window.mod_functions.user_functions.action_kick.getValue() || _root.chat.moderator_window.mod_functions.user_functions.action_ban.getValue()) {
		    _root.chat.moderator_window.mod_functions.user_functions.action_kick.setValue(false);
				kick = _root.chat.moderator_window.mod_functions.user_functions.action_kick.getValue();
		    _root.chat.moderator_window.mod_functions.user_functions.action_ban.setValue(false);
				this.ban = _root.chat.moderator_window.mod_functions.user_functions.action_ban.getValue();
		}
		if (_root.chat.moderator_window.mod_functions.user_functions.action_deoper.getValue()) {
		    _root.chat.moderator_window.mod_functions.user_functions.action_deoper.setValue(false);
		}
		if (_root.chat.moderator_window.mod_functions.user_functions.action_oper.getValue()) {
		    _root.chat.moderator_window.mod_functions.user_functions.action_oper.setValue(false);
		}		
			this.getBanList();  
	    } else {
			this.updateUsers();		
		}
	}
	
	public function operUser(component) {
	    this.oper = !this.oper; 
	    if (this.oper) {
		 _root.chat.moderator_window.mod_functions.user_functions.field_info.text = this.operStr;
		if (_root.chat.moderator_window.mod_functions.user_functions.action_unban.getValue()) {
		   _root.chat.moderator_window.mod_functions.user_functions. action_unban.setValue(false);
		} 
		if (_root.chat.moderator_window.mod_functions.user_functions.action_ban.getValue()) {
		   _root.chat.moderator_window.mod_functions.user_functions. action_ban.setValue(false);
		}         
		if (_root.chat.moderator_window.mod_functions.user_functions.action_kick.getValue()) {
		   _root.chat.moderator_window.mod_functions.user_functions. action_kick.setValue(false);
		}         
		if (_root.chat.moderator_window.mod_functions.user_functions.action_deoper.getValue()) {
		    _root.chat.moderator_window.mod_functions.user_functions.action_deoper.setValue(false);
		}         
	
	    }  
	}
	
	public function deOperUser(component) {
	   this.deoper = !this.deoper; 
	    if (this.deoper) {
		 _root.chat.moderator_window.mod_functions.user_functions.field_info.text = this.deoperStr;
		if (_root.chat.moderator_window.mod_functions.user_functions.action_unban.getValue()) {
		   _root.chat.moderator_window.mod_functions.user_functions. action_unban.setValue(false);
		} 
		if (_root.chat.moderator_window.mod_functions.user_functions.action_ban.getValue()) {
		    _root.chat.moderator_window.mod_functions.user_functions.action_ban.setValue(false);
		}         
		if (_root.chat.moderator_window.mod_functions.user_functions.action_kick.getValue()) {
		    _root.chat.moderator_window.mod_functions.user_functions.action_kick.setValue(false);
		}         
		if (_root.chat.moderator_window.mod_functions.user_functions.action_oper.getValue()) {
		    _root.chat.moderator_window.mod_functions.user_functions.action_oper.setValue(false);
		}         
	    }
	}  
	
	public function updateChannels() {
		_root.chat.moderator_window.mod_functions.user_functions.list_channels.removeAll();
		for (var item:String  in settings.channels.items) {
			if (settings.operatorMode || settings.channels.getChannel(item).operator) {
				_root.chat.moderator_window.mod_functions.user_functions.list_channels.addItem(item,item);
			}
		}
		for (var i:Number=0;i<_root.chat.moderator_window.mod_functions.user_functions.list_channels.getRowCount();i++) {
			if (_root.chat.moderator_window.mod_functions.user_functions.list_channels.getItemAt(i).data == settings.activeChannel) {
				_root.chat.moderator_window.mod_functions.user_functions.list_channels.setSelectedIndex(i);
			}
		}
		this.updateUsers();
	}
	
	public function updateUsers() {
		_root.chat.moderator_window.mod_functions.user_functions.list_users.removeAll();
		for (var item:String in settings.channels.getChannel(_root.chat.moderator_window.mod_functions.user_functions.list_channels.getSelectedItem().data).users.items) {
			if (item != settings.myUsername && item.substr(1, item.length) != settings.myUsername) {
				if (item.substr(0, 1) == "@") {
					_root.chat.moderator_window.mod_functions.user_functions.list_users.addItem(item,"");
				} else {
					_root.chat.moderator_window.mod_functions.user_functions.list_users.addItem(item,"");
				}
			}
		}
	}
	
	public function doAction(component) {
		var selected_channel:String = _root.chat.moderator_window.mod_functions.user_functions.list_channels.getSelectedItem().data;
		var users:Array = _root.chat.moderator_window.mod_functions.user_functions.list_users.getSelectedIndices();
		if (selected_channel != undefined || selected_channel != "")  {
			if (users != undefined) {
				if (this.kick)  {
					for (var item:String in users) {
						var user:String = extras.replace(_root.chat.moderator_window.mod_functions.user_functions.list_users.getItemAt(users[item]).label,"@","");
						command.sendCommand("KICK",[selected_channel,user,_root.chat.moderator_window.mod_functions.user_functions.input_reason.text]);
					}
					this.updateUsers();
					_root.chat.moderator_window.mod_functions.user_functions.action_kick.setValue(false);
					this.kick = _root.chat.moderator_window.mod_functions.user_functions.action_kick.getValue();
					_root.chat.moderator_window._visible = false;				
		    }
				if (this.ban) {
					for (var item:String in users) {
						user = extras.replace(_root.chat.moderator_window.mod_functions.user_functions.list_users.getItemAt(users[item]).label,"@","");
						if (_root.chat.moderator_window.mod_functions.user_functions.list_users.getItemAt(users[item]).data == "") {
							command.sendCommand("USERHOST",[user]);
							var cmd:Command = new Command("USERHOST",user,"MODE",Array(selected_channel,"+b","*!*@" + _root.chat.moderator_window.mod_functions.user_functions.list_users.getItemAt(users[item]).data,_root.chat.moderator_window.mod_functions.user_functions.input_reason.text),0, null, command.socket, null, null);
							commandQueue.addCommand(cmd);												
						} else {
						    command.sendCommand("MODE",[selected_channel,"+b","*!*@" + _root.chat.moderator_window.mod_functions.user_functions.list_users.getItemAt(users[item]).data,_root.chat.moderator_window.mod_functions.user_functions.input_reason.text]);
						}
					}
					_root.chat.moderator_window.mod_functions.user_functions.action_ban.setValue(false);
					this.ban = _root.chat.moderator_window.mod_functions.user_functions.action_ban.getValue();
					this.updateUsers();
					_root.chat.moderator_window._visible = false;				
		    }
				if (this.unban) {
					for (var item:String in users) {
						command.sendCommand("MODE",[selected_channel,"-b",_root.chat.moderator_window.mod_functions.user_functions.list_users.getItemAt(users[item]).data,_root.chat.moderator_window.mod_functions.user_functions.input_reason.text]);
					}
					this.getBanList();
					_root.chat.moderator_window.mod_functions.user_functions.action_unban.setValue(false);
					this.unban = _root.chat.moderator_window.mod_functions.user_functions.action_unban.getValue();
					_root.chat.moderator_window._visible = false;				
			    }
		    if (this.oper) {
			  		for (var item:String in users) {
						user = this.extras.replace(_root.chat.moderator_window.mod_functions.user_functions.list_users.getItemAt(users[item]).label,"@","");
						command.sendCommand("MODE",[selected_channel,"+o",user]);
					}
					_root.chat.moderator_window.mod_functions.user_functions.action_oper.setValue(false);
					this.oper = _root.chat.moderator_window.mod_functions.user_functions.action_oper.getValue();
					this.updateUsers();
					_root.chat.moderator_window._visible = false;			
		    }			
		    if (this.deoper) {
					for (var item:String in users) {
						user = extras.replace(_root.chat.moderator_window.mod_functions.user_functions.list_users.getItemAt(users[item]).label,"@","");
						command.sendCommand("MODE",[selected_channel,"-o",user]);
					}
					_root.chat.moderator_window.mod_functions.user_functions.action_deoper.setValue(false);
					this.deoper = _root.chat.moderator_window.mod_functions.user_functions.action_deoper.getValue();
					this.updateUsers();
					_root.chat.moderator_window._visible = false;				
			}					
		    }
	    } 	
	}
}