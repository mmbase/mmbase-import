/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class ChatBox
{
	var lastPrivateWindow:MovieClip; 
	var newPrivateChat:String;
	var lastDepth:Number;
	var firstTime:Boolean;
	
	var debug:Debug;
	var functions:Functions;
	var userlist:Userlist_window;
	var channellist:Channellist_window;
	var messages:Messages;
	var settings:Settings;
	var command:Command;
	var rules_window:Rules_window;
	var help_window:Help_window;
	var moderator_window:Moderator_window;
	var channel_win:Channel_window;
	var extras:Extras;
	var channel:Channel;
	var socket:Socket;
	var commandQueue:CommandQueue;
	
	var lastPrivID:String;
	var activePrivID:String;;

	public function ChatBox(login:Login)
	{
		this.firstTime = true;
		
		socket = login.socket;
		settings = login.settings;
		debug = new Debug();
		messages = new Messages();
		commandQueue = new CommandQueue();
		command = new Command(null,null,null,null,null, settings, socket, this, commandQueue);
		channellist = new Channellist_window(settings, command,this);
		functions = new Functions(socket, settings, command, this);
		channel_win = new Channel_window(settings, this, command, functions);
		extras = new Extras(settings, this);
		moderator_window = new Moderator_window(settings, command, extras, commandQueue);
		channel = new Channel();
		userlist = new Userlist_window(settings, functions, this);
	
		this.lastDepth = 1;
		_root.chat.button_moderator._visible = false;
		_root.chat.moderator_window._visible = false;
		_root.chat.check_moderator._visible = false;
		_root.chat.check_moderator.setStyleProperty("textColor", 0xFFFFFF);
		
		lastPrivateWindow._x = 0;
		lastPrivateWindow._y = 0;
		
		if (firstTime) 
		{
			channel_win.showWindow = false;
			channel_win.show();
			_root.chat.channel_window._visible = false;
		
			_root.chat.help_window._visible = false;
			_root.chat.rules_window._visible = true;
			_root.chat.moderator_window._visible = false;
		}
		
		_root.chat.my_Version.text = settings.chat_version;
		delete login;
	}
/*

button_closechat.onRelease = function() {
	logout();
}
*/
	function moderatorVisible(component) {
		 if (!component.getValue()) {
			command.sendCommand("MODE",[settings.activeChannel,"-o",settings.myUsername]);
		}
		else {
			command.sendCommand("MODE",[settings.activeChannel,"+o",settings.myUsername]);
		}
	}


	public function myProcessNick(olduser:String,newuser:String) {
		if (this["private_" + olduser] != undefined) {
			this["private_" + olduser]._visible = false;
			this.myCreatePrivateChat(newuser,this["private_" + olduser]._visible);
			this.myClosePrivateChat(olduser);		
		}
		userlist.update();
		channellist.update();
	}

	public function myProcessOperator() {
		this.showNoticeMessage(messages.MESSAGE_MODERATORMODE);
		_root.chat.button_moderator._visible = true;
		_root.chat.check_moderator._visible = true;	
		_root.chat.userlist_window.button_private._visible = true;
		_root.chat.userlist_window.privateMessageNotice._visible = false;
		_root.chatbox.moderator_window.show("users");
	}

	public function myProcessModerator(channelid:String) {
		if (channelid == settings.activeChannel) {
			_root.button_moderator._visible = true;
		//	  check_moderator.setChangeHandler("");
		//	  check_moderator.setValue(true);
		//	  check_moderator.setChangeHandler("moderatorVisible");	  
			  if (!settings.privateChatEnabled) {
				_root.userlist_window.button_private._visible = true;
				_root.userlist_window.privateMessageNotice._visible = false;
			}
		}
	}

	public function myProcessNoModerator(channelid:String) {
		  if (channelid == settings.activeChannel && !settings.operatorMode) {
			_root.button_moderator._visible = false;
			if (!settings.privateChatEnabled) {
				_root.serlist_window.button_private._visible = false;
				_root.userlist_window.privateMessageNotice._visible = true;
			}
		}
	//  check_moderator.setChangeHandler("");
	//  check_moderator.setValue(false);
	//  check_moderator.setChangeHandler("moderatorVisible");
	}

	public function myUpdateChannelList(channelid:String,users:Array) {
		//channellist.addChannel(channelid,users);
	}

	public function myProcessEndChannelList() {
		settings.channelsUpdated = true;
		if (this.firstTime) {
			if (settings.myFirstChannel != "" && settings.myFirstChannel != undefined && settings.myFirstChannel != "null") {
				command.sendCommand("JOIN",["#" + settings.myFirstChannel]);	
				
			} else {
				_root.rules_window.display();
			}
			this.firstTime = false;
		}
	}

	public function myProcessBanList(channelid:String,user:String) {
		moderator_window.addBannedUser(user,user);
	}

	public function myLogout() {
		for (var item:String in settings.channels.items) {
			functions.closeChannel(item);	
			debug.sendDebug("closing:" + item);	
		}
		for (var item:String in settings.privateChannels.items) {
			this["private_" + 	item].close();
			this.lastDepth--;
		}
		_root.help_window.hide();	
		_root.rules_window.hide();	
		_root.channel_window._visible = false;
		
		_root.channellist._visible = false;
		_root.userlist_window._visible = false;
	}

	public function myAddUser(channel:String,user:String) {
		if (channel == settings.activeChannel) {
			userlist.updateUserList()
		}
		channellist.update();	

	}

	public function myRemoveUser(channel:String,user:String,message:String) {
		if (channel == settings.activeChannel) {
			userlist.updateUserList();
		}
		channellist.update();
	}

	public function mySetTopic(id:String,topic:String) {
		if (settings.activeChannel == id) {
			channel_win.topic = topic;
		}
	}

	public function showPrivate(id:String) {
		var privated:String = "private_" + id;
		this.activePrivID = id;

		if (privated == undefined) {
			this.myCreatePrivateChat(id,true);  
		  
		} else {
			if (id == this.newPrivateChat) {
				this.newPrivateChat = undefined;
				channellist.update();
			}
			_root[privated].dialog_window.update(settings.privateChannels.getChannel(id).getDialog());			
			_root[privated].swapDepths(lastDepth);
			_root[privated].lastMessage = "";//MESSAGE_LASTMESSAGE + privateChannels.get(id).lastMessage;	
			_root[privated]._visible = true;
			lastPrivateWindow = _root[privated];
			lastDepth++;
		}
	}

	//creates a private chat (called by createPrivateChat in functions.as)
	public function myCreatePrivateChat(id:String,visible:Boolean) {
		this.newPrivateChat = id;
		var privated:String;
		privated = "private_" + id;
		//create the movieclip
		this.lastPrivID = id;
		_root.attachMovie("private_chat",privated,this.lastDepth);
		this.lastDepth++;
		
		_root[privated]._visible = visible;
		_root[privated]._x = this.lastPrivateWindow._x + 10;
		_root[privated]._y = this.lastPrivateWindow._y + 10;
		_root[privated].txt_id.text = id;

		if (visible) {
			this.showPrivate(id);		
		} 
		//don't show the window, just blink in the channelListWindow
		if (!visible) {
			extras.blink(id);
		}
		_root[privated].setButtonColor(settings.privateChannels.getChannel(id).textColor);	
		channellist.setFocus("channels");
		this.lastPrivateWindow = _root[privated];	
	}

	public function myCreateChannel(id:String,type:String) {
		this.setFocus(id);	
	}

	public function setFocus(id:String) {
		//status_messages.clear();
		_root.chat.rules_window.hide();
		channel_win.showHide = true;
		channel_win.hide();
		settings.activeChannel = id;
		channel_win.id = id;
					
		_root.chat.channel_window.txt_id.text = id;
		_root.chat.channel_window.txt_topic.text = channel_win.topic = settings.channels.getChannel(id).topic;
		
		channel.lastMessage = messages.MESSAGE_LASTMESSAGE + settings.channels.getChannel(id).lastMessage;
		channel_win.dialog_window.update(settings.channels.getChannel(id).getDialog());
		channel_win.setButtonColor(settings.channels.getChannel(id).textColor);
		channellist.setFocus("channels");
		channellist.update();
		userlist.update();


		if (settings.channels.getChannel(id).operator || settings.operatorMode) {
			_root.button_moderator._visible = true;
			if (!settings.privateChatEnabled) {
				_root.userlist_window.button_private._visible = true;
				_root.userlist_window.privateMessageNotice._visible = false;
			}
			else {
				_root.button_moderator._visible = false;
				if (!settings.privateChatEnabled) {
					_root.userlist_window.button_private._visible = false;
					_root.userlist_window.privateMessageNotice._visible = true;
				}
			}
		
			if (settings.operatorMode) {
				_root.check_moderator.setChangeHandler("");
				_root.check_moderator.setValue(settings.channels.getChannel(id).operator);
				_root.check_moderator.setChangeHandler("moderatorVisible");
			}
		}
	}

	public function myUpdateUserlist() {
		userlist.update();
		channellist.update();
	}

	public function myClosePrivateChat(id:String) {
		var privated:String = "private_" + id;
		if (privated != undefined) {
			channellist.update();		
			this.lastDepth--;
			this.removeMovie(privated);		
			var priv:Channel = settings.privateChannels.peek();
			if (priv != undefined && this["private_" + priv.ID] != undefined) {
				lastPrivateWindow = this["private_" + priv.ID];
			} else {
				var lastPrivateWindow = this["private_" + priv.ID];
			}
		}
		
	}

	public function myCloseChannel(id:String) {
		channel_win.hide();
		channel_win.dialog_window.update("");
		if (settings.activeChannel != undefined) {
			this.setFocus(settings.activeChannel);
		} 
		channellist.update();
		userlist.update();	
	}

	public function myUpdateDialog(id:String,message:String,user:String) {
		debug.sendDebug("myUpdateDialog")
		var privated = "private_" + id;
		if (_root[privated] != undefined) {
			if (_root[privated]._visible) {
				_root[privated].priv.dialog_window.addMessage(message);
				_root[privated].priv.lastMessage = "laatste ber.:" + settings.privateChannels.getChannel(id).lastMessage;
			} else {
				//channellist.setSelected(undefined);
				extras.blink(id);			
			}
		} else {
			if (id == settings.activeChannel) {
				channel_win.dialog_window.addMessage(message);
				channel.lastMessage = messages.MESSAGE_LASTMESSAGE + settings.channels.getChannel(id).lastMessage;
			} else {
				//channellist.setSelected(undefined);			
				extras.blink(id);	
			}
		}
	}

	public function removeMovie(id:String) {
		_root[id].removeMovieClip();	
	}

	public function showErrorMessage(message:String) {
		_root.chat.status_messages.addMessage(message,"error");    
	}

	public function showNoticeMessage(message:String) {
		_root.chat.status_messages.addMessage(message,"notice");        
	}
	
	public function setColor(colorcl:String, parent:String)
	{
		if(parent == "channel_window")
			channel_win.colorsel.switchColor(colorcl);
		else
		{
			var privated:String;
			privated = "private_" + this.activePrivID;
			_root[privated].priv.colorsel.switchColor(colorcl);	
		}
	}
}