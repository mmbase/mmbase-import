/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Functions
{	
	var socket:Socket;
	var extra:Extras;
	var settings:Settings;
	var debug:Debug;
	var command:Command;
	var messages:Messages;
	var chat:ChatBox;
	
	public function Functions(socket:Socket, settings:Settings, command:Command, chat:ChatBox)
	{
		this.extra = new Extras();
		this.debug = new Debug();
		this.messages = new Messages();
		
		this.command = command;
		this.socket = socket;		
		this.settings = settings;
		this.chat = chat;	
	}
	
	//handle the login
	 public function login() {
		//settings.myUsername = extra.replace(settings.myUsername," ","_");
		command.sendLogin(settings.myUsername,settings.myPassword);
	}

	//handle the logout
	 public function logout(){
		command.sendQuit();
		socket.closeSocket();	
		settings.channels = new Hashtable();
		settings.availableChannels = new Hashtable();
		settings.activeChannel = "";
		settings.lastChannel = -1;
		chat.myLogout();
	}

	//add a user to a channel
	public function addUser(channel:String,user:String) {
		var username:String = command.getUserFromPrefix(user);
		var host:String = command.getHostFromPrefix(user);
		settings.channels.getChannel(channel).addUser(username,host);
		chat.myAddUser(channel,user);
		debug.sendDebug("addUser: adding user " + user + " to " + channel);	
	}

	//remove a user from a channel
	public function removeUser(channel:String,user:String,message:String) {
		settings.channels.getChannel(channel).removeUser(user);	
		settings.channels.getChannel(channel).removeUser("@" + user);
		this.updateDialog(channel,message,"");
		chat.myRemoveUser(channel,user,message);
	}

	//set the topic of a channel
	public function setTopic(user:String,channel:String,topic:String) {
		settings.channels.getChannel(channel).topic = topic;	
		this.updateDialog(channel,messages.NOTICE_PREFIX + messages.MESSAGE_SETTOPIC + topic + messages.NOTICE_ENDFIX,"");
		chat.mySetTopic(channel,topic);
	}

	//create a new channel
	public function createChannel(id:String,type:String) {
		if ((settings.channels.getChannel(id) == undefined) && (settings.lastChannel < settings.maxChannels)) {
			settings.lastChannel++;
			settings.activeChannel = id;
			var channel2:Channel = new Channel(id);
	
			settings.channels.putChannel(id,channel2);
			debug.sendDebug("createChannel: creating " + id + "(" + type + ")");			
			chat.myCreateChannel(id,type);
		}
	}

	//close a channel
	public function closeChannel(id:String) {
		if (id != undefined && settings.channels.getChannel(id) != undefined) {
			if (!settings.channels.getChannel(id).kicked) {
				command.sendPart(id);
			}
			settings.channels.remove(id);
			var newChannel:Channel = settings.channels.peek();		
			if (newChannel != undefined) {
				settings.activeChannel = newChannel.ID;
			} else {
				settings.activeChannel = undefined;
			}
			settings.lastChannel--;
			chat.myCloseChannel(id);
			debug.sendDebug("removeChannel: removing " + id);
		}
	}

	//create a private chat
	public function createPrivateChat(id:String,visible:Boolean) {
		//see if it doesn't allready exists
		if (id.substr(0,1) == "@") {
			id = id.substring(1,id.length);	
		}
		if (settings.privateChannels.getChannel(id) == undefined) {	
			settings.lastChannel++;
			var newChannel:Channel = new Channel(id,settings.lastChannel,"private");
			newChannel.topic = messages.TOPIC_PRIVATE;
			newChannel.addUser(id,"");
			newChannel.addUser(settings.myUsername,"");	
			settings.privateChannels.putChannel(id,newChannel);
			chat.myCreatePrivateChat(id,visible);
			debug.sendDebug("createPrivateChat: creating " + id);			
		}
	}

	//close a private chat
	public function closePrivateChat(id:String) {
		if (id != undefined && settings.privateChannels.getChannel(id) != undefined) {
			settings.privateChannels.remove(id);
			settings.lastChannel--;	
			chat.myClosePrivateChat(id);
		}
	}
	//update the dialog window
	public function updateDialog(id:String, message:String, user:String) {
		//message = convertTags(message);
		//var message = addLinks(message);
		if (!settings.channels.getChannel(id).kicked) {
			message += messages.NEWLINE;
		//change the color of the text
			message = this.addColor(message);
			if (settings.privateChannels.getChannel(id) != undefined) {
				message = "<b><i>" + user + " " + messages.MESSAGE_USER_SAYS + "</i></b>" + messages.NEWLINE + message;
				settings.privateChannels.getChannel(id).addMessage(message);
				if (user != settings.myUsername) {
					settings.privateChannels.getChannel(id).lastMessage = extra.getTimeString();
					_root.chat.channel_window.txt_lastmessage.text = settings.channels.getChannel(id).lastMessage;
				}
			} else {
				if (user != "") {
					message = user + ": " + message;
				} else {}
				settings.channels.getChannel(id).addMessage(message);
				if (user != settings.myUsername) {
					settings.channels.getChannel(id).lastMessage = extra.getTimeString();
					_root.chat.channel_window.txt_lastmessage.text = settings.channels.getChannel(id).lastMessage;
				}	
			}
		} else {
			message = messages.NOTICE_PREFIX + messages.MESSAGE_YOUAREKICKED + messages.NOTICE_ENDFIX;
			settings.channels.getChannel(id).addMessage(message + messages.NEWLINE);
		}
		debug.sendDebug("updateDialog: updating " + id + " with: " + message);	
		chat.myUpdateDialog(id,message,user);
	}

	//overloaded function of updateDialog(id,message,user) but takes array as parameters
	public function updateDialog2(params:Array) {
		this.updateDialog(params[0],params[1],params[2]);
	}
	
	public function setTextColor(id:String,color:String) {
		debug.sendDebug("setting color of " + id + " to " + color);
		if (settings.privateChannels.getChannel(id) != undefined) {
			settings.privateChannels.getChannel(id).textColor = color;
		} else {
			settings.channels.getChannel(id).textColor = color;
		}
	}

	public function addColor(message:String) {
		if (settings.textColorEnabled) {
			if (message.indexOf("&#x3;") > -1)
			{
				var start:Number = message.indexOf("&#x3;") + 5;
				var colorCode:String = message.substring(start, start+1);
				      switch (colorCode) {
					//black
					case "1": message = extra.replace(message,"&#x3;1",""); message = "<font color='#" + settings.defaultColors['black'][1] + "'>" + message; break;
					//blue
					case "2": message = extra.replace(message,"&#x3;2",""); message = "<font color='#" + settings.defaultColors['blue'][1] + "'>" + message; break;
					//green
					case "3": message = extra.replace(message,"&#x3;3",""); message = "<font color='#" + settings.defaultColors['green'][1] + "'>" + message; break;
					//red
					case "4": message = extra.replace(message,"&#x3;4",""); message = "<font color='#" + settings.defaultColors['red'][1] + "'>" + message; break;
					//brown
					case "5": message = extra.replace(message,"&#x3;5",""); message = "<font color='#" + settings.defaultColors['brown'][1] + "'>" + message; break;
					//purple
					case "6": message = extra.replace(message,"&#x3;6",""); message = "<font color='#" + settings.defaultColors['purple'][1] + "'>" + message; break;
					//orange
					case "7": message = extra.replace(message,"&#x3;7",""); message = "<font color='#" + settings.defaultColors['orange'][1] + "'>" + message; break;
					//yellow
					case "8": message = extra.replace(message,"&#x3;8",""); message = "<font color='#" + settings.defaultColors['yellow'][1] + "'>" + message; break;
					//lightgreen
					case "9": message = extra.replace(message,"&#x3;9",""); message = "<font color='#" + settings.defaultColors['lightgreen'][1] + "'>" + message; break;
				}
				message = message + "</font>";
			}		
		}	
		return message;
	}

	public function sendMessage(channel:String,message:String) {
		if (channel == undefined) 
		{
			channel = settings.activeChannel;	
		}
		if (settings.commandsEnabled) 
		{
			if (message.substring(0,1) == "/") 
			{
				var startP:Number = message.indexOf(" ",1);
				var endP:Number = message.length;
				var commandString:String = message.substring(1,startP);
				commandString = commandString.toUpperCase();
				var att:String = message.substring(startP + 1,endP);
				command.sendCommand(commandString,att.split(" "));
			} 
			else 
			{
				if (settings.privateChannels.getChannel(channel) != undefined) 
				{
					if (settings.textColorEnabled) 
					{
						if (settings.privateChannels.getChannel(channel).textColor != "" || settings.privateChannels.getChannel(channel).textColor == "black") 
						{
						      message = "&#x3;" + settings.defaultColors[settings.privateChannels.getChannel(channel).textColor][0] + message;
						}
					}
				} 
				else 
				{
					if (settings.textColorEnabled) 
					{
						if (settings.channels.getChannel(channel).textColor != "" || settings.channels.getChannel(channel).textColor == "black") 
						{
						      message = "&#x3;" + settings.defaultColors[settings.channels.getChannel(channel).textColor][0] + message;
						}
					}
				}

				if (!this.checkHiddenUser(settings.myUsername)) 
				{
					if (channel.substr(0,1) == "@") 
					{
						command.sendPrivmsg(channel.substring(1,channel.length),message);
					}
					else 
					{
						command.sendPrivmsg(channel,message);
					}
					//message = addLinks(message);
					this.updateDialog(channel,message,settings.myUsername);
				}	
			}
		} 
		else
		{
			if (settings.privateChannels.getChannel(channel) != undefined) 
			{
				if (settings.textColorEnabled) 
				{
					if (settings.privateChannels.getChannel(channel).textColor != "" || settings.privateChannels.getChannel(channel).textColor == "black") 
					{
						message = "&#x3;" + settings.defaultColors[settings.privateChannels.getChannel(channel).textColor][0] + message;
					}
				}
			} 
			else
			{
				if (settings.textColorEnabled) 
				{
					if (settings.channels.getChannel(channel).textColor != "" || settings.channels.getChannel(channel).textColor == "black")
					{
						message = "&#x3;" + settings.defaultColors[settings.channels.getChannel(channel).textColor][0] + message;
					}
				}
			}
			if (!this.checkHiddenUser(settings.myUsername))
			{
				if (channel.substr(0,1) == "@") 
				{
					command.sendPrivmsg(channel.substring(1,channel.length),message);
				} 
				else
				{
					command.sendPrivmsg(channel,message);
				}
				//message = addLinks(message);
				this.updateDialog(channel,message,settings.myUsername);
			}
		}
	}

	public function checkHiddenUser(user:String):Boolean {
		if (settings.hideSpecialUsers)
		{
			for (var item:String in settings.hiddenUsers) 
			{
				if (settings.hiddenUsers[item] == user) 
				{ 
					return true;
				}
			}
		}
		return false;
	}

	public function ignore(userid:String,truefalse:Boolean) {
		if (truefalse) 
		{
			settings.ignoreList.put(userid,true);
		}
		else 
		{
			settings.ignoreList.remove(userid);
		}
	}
}