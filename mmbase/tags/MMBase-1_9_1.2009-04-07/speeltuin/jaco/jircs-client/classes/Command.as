/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Command
{
	var key:String
	var command:String;
	var params:Array;
	var trigger:String
	var messages:Messages;
	var settings:Settings;
	var functions:Functions;
	var type:Number;
	var socket:Socket;
	var debug:Debug;
	var extras:Extras;
	var chat:ChatBox;
	var commandQueue: CommandQueue;
	
	public function Command(trigger:String,key:String,cmd:String,params:Array,type:Number, settings:Settings, socket:Socket, chat:ChatBox, commandQueue:CommandQueue)
	{
		this.debug = new Debug();
		this.messages = new Messages();
		
		this.functions = new Functions(socket, settings, this, chat);
		
		this.socket = socket;
		this.settings = settings;
		this.extras = new Extras();
		
		this.type = type;
		this.command = cmd;       
		this.params = params;
		this.key = key;
		this.trigger = trigger;
		
		this.chat = chat;
		this.commandQueue = commandQueue;
	}
    
	public function execute() {
		debug.sendDebug("Command: executing: " + command + "(" + params + ")");
		//if (type == 0) {
			this.sendCommand(command,params);    
	//	}
		if (type == 1) {
		//	eval(command)(params); 
		}
	}
		
	/**
	  processes the input xml object received from the xml socket
	*/
	public function processInput(xml:XML) {
		debug.sendDebug("onXML is receiving " + xml.toString());   
		if (xml.firstChild.nodeName == "message") {
			var prefix:String = xml.firstChild.attributes.prefix;	
			var command:String = xml.firstChild.attributes.command;
			switch (command) {
				case "PRIVMSG":
					this.processPrivmsg(xml);
					break;
				case "JOIN":
					this.processJoin(xml);
					break;
				case "PART":
					this.processPart(xml);
					break;
				case "QUIT":
					this.processQuit(xml);
					break;
				case "PING":
					this.processPing(xml);
					break;
				case "TOPIC":
					this.processChannelTopic(xml);
					break;
				case "KICK":
					this.processKickUser(xml);
					break;
				case "MODE":
					this.processMode(xml);
					break;
				case "NICK":
					this.processNick(xml);
					break;
				case "ERROR":
					this.processError(xml);
					break;			
	
				//Number cases
				case "001":
					this.processLogon(command,xml);
					break;
				  case "302":
					this.processUserhost(xml);
					break;
				  case "313":
					this.processWhois(xml);
					break;
				case "322":
					this.processRecieveChannelList(command,xml)				
					break;				
				case "323":
					this.processEndChannelList(command,xml)				
					break;				
				case "332":
					this.processChannelTopic(xml);
					break;		
				case "353":
					this.processUserList(xml);
					break;
				case "366":
					break;	
				case "367":
					this.processBanList(xml);
					break;
				case "368":
					this.processEndBanList(xml);
					break;	
				case "381":
					this.processOperator(xml);
					break;
				case "401":
					this.processNoSend(xml);
					break;
				case "404":
					this.processNoSend(xml);
					break;
				case "432":
					this.processNickNotValid(xml);
					break;
				case "433":
					this.processNickExists(xml);
					break;
				case "464":
					this.processWrongModeratorPassword(xml);
					break;
				case "471":
					this.processChannelFull(xml);
					break;
				case "474":
					this.processBannedFromChannel(xml);
					break;
				case "475":
					this.processBannedFromChannel(xml);
					break;
				case "4003":
					this.processMessageFiltered(xml);
					break;
				default:
					this.processCommand(command,xml);
			}
		}
	}
	
	/**
	  returns the username in the prefix
	*/
	public function getUserFromPrefix(prefix:String):String {
		var nick:String = prefix.substring(0,prefix.indexOf("!"));
		if (nick == "") {
			nick = prefix;	
		}		
		return extras.replace(nick,":","");
	}
	
	/**
	  returns the hostname in the prefix
	*/
	public function getHostFromPrefix(prefix:String):String {
		var host:String = prefix.substring((prefix.indexOf("@") + 1),prefix.length);
		return host;
	}
	
	
	/**
	  function called when a 'channel full' message is received
	*/
	public function processChannelFull(xml:XML) {
	    chat.showErrorMessage(messages.ERROR_CHANNEL_FULL);    
	}
	
	/**
	  activates the operator mode, enabling the user to creat channels and such
	*/
	public function processOperator(xml:XML) {
		settings.operatorMode = true;
		settings.commandsEnabled = true;
		chat.myProcessOperator();
	}
	
	/**
	  shows an error message when a wrong moderator/operator password is entered
	*/
	public function processWrongModeratorPassword(xml:XML) {
		settings.moderatorLoginTries++;	
		chat.showErrorMessage(messages.ERROR_WRONG_PASSWORD);
	}
	
	/**
	  processes the 'WHOIS' response, adds users who have operator/moderator rights to a list of know moderators/operators
	*/
	public function processWhois(xml:XML) {
		var user:String = xml.firstChild.childNodes[1].attributes.value;
		settings.knownOperators.put(user,true);
		  //execute the updateDialog that's stored in the commandQueue
		var cmd:Object = commandQueue.getCommand("313",user);
		if (cmd != undefined) {
			if (settings.privateChannels.get(user) == undefined && settings.privateChannels.get("@" + user) == undefined) {
				//newPrivateChat = user;
				functions.createPrivateChat(user,false);
			}   
			this.execute();    
		}
	}
	
	/**
	  processes the 'MODE' command. used to set and unset bans, set and unset moderator/operator modes,
	  delete channel, moderated and de-moderated channels and user limits on channels
	*/
	public function processMode(xml:XML) {
	  //process bans and unbans  
		if (xml.firstChild.childNodes[1].attributes.value == "+b") {
			if (settings.operatorMode) {
				functions.updateDialog(xml.firstChild.childNodes[0].attributes.value,messages.NOTICE_PREFIX + messages.MESSAGE_USERBANNED1 + this.getHostFromPrefix(xml.firstChild.childNodes[2].attributes.value) + messages.MESSAGE_USERBANNED2 + messages.NOTICE_ENDFIX,"");				
			}
		}
		if (xml.firstChild.childNodes[1].attributes.value == "-b") {
			if (settings.operatorMode) {
				functions.updateDialog(xml.firstChild.childNodes[0].attributes.value,messages.NOTICE_PREFIX + messages.MESSAGE_USERUNBANNED1 + this.getHostFromPrefix(xml.firstChild.childNodes[2].attributes.value) + messages.MESSAGE_USERUNBANNED2 + messages.NOTICE_ENDFIX,"");	
			}
		}
		
		//process operator and de-operator
		if (xml.firstChild.childNodes[1].attributes.value == "+o") {
			var user:String = xml.firstChild.childNodes[2].attributes.value;
			var channel:String = xml.firstChild.childNodes[0].attributes.value;
			settings.channels.get(channel).changeUser(user,"@" + user);
			if (user == settings.myUsername) {
				settings.channels.get(channel).operator = true;
		      chat.myProcessModerator(channel);
				functions.updateDialog(channel,messages.NOTICE_PREFIX + messages.MESSAGE_YOUAREOPERATOR + messages.NOTICE_ENDFIX,"");
			} else {
			functions.updateDialog(channel,messages.NOTICE_PREFIX + user + messages.MESSAGE_USERISOPERATOR + messages.NOTICE_ENDFIX,"");
			}
			chat.myUpdateUserlist();	
		}
		if (xml.firstChild.childNodes[1].attributes.value == "-o") {
			var user:String = xml.firstChild.childNodes[2].attributes.value;
			var channel:String = xml.firstChild.childNodes[0].attributes.value;
			settings.channels.get(channel).changeUser("@" + user,user);
			if (user == settings.myUsername) {
				functions.updateDialog(channel,messages.NOTICE_PREFIX + messages.MESSAGE_YOUARENOTOPERATOR + messages.NOTICE_ENDFIX,"");
				settings.channels.get(channel).operator = false;
				chat.myProcessNoModerator(channel);
			} 
			else {
				functions.updateDialog(channel,messages.NOTICE_PREFIX + user + messages.MESSAGE_USERISNOTOPERATOR + messages.NOTICE_ENDFIX,"");	
			}
			chat.myUpdateUserlist();
		}
	
	  //process channel closed	
		if (xml.firstChild.childNodes[1].attributes.value == "+d") {
			//var user = xml.firstChild.childNodes[2].attributes.value;
			var channel:String = xml.firstChild.childNodes[0].attributes.value;
			functions.updateDialog(channel,messages.NOTICE_PREFIX + messages.MESSAGE_CHANNELCLOSED + messages.NOTICE_ENDFIX,"");	
		}
		
		//proccess channel moderated (no messages can be sent to the channel) de-moderated
		if (xml.firstChild.childNodes[1].attributes.value == "+m") {
			//var user = xml.firstChild.childNodes[2].attributes.value;
			var channel:String = xml.firstChild.childNodes[0].attributes.value;
			functions.updateDialog(channel,messages.NOTICE_PREFIX + messages.MESSAGE_CHANNELMODERATED + messages.NOTICE_ENDFIX,"");	
		}	
		
		if (xml.firstChild.childNodes[1].attributes.value == "-m") {
			//var user = xml.firstChild.childNodes[2].attributes.value;
			var channel:String = xml.firstChild.childNodes[0].attributes.value;
			functions.updateDialog(channel,messages.NOTICE_PREFIX + messages.MESSAGE_CHANNELDEMODERATED + messages.NOTICE_ENDFIX,"");	
		}	
		
		//process user limit
		if (xml.firstChild.childNodes[1].attributes.value == "+l") {
			var limit:String = xml.firstChild.childNodes[2].attributes.value;
			var channel:String = xml.firstChild.childNodes[0].attributes.value;
			functions.updateDialog(channel,messages.NOTICE_PREFIX + messages.MESSAGE_CHANNELLIMIT1 + limit + messages.MESSAGE_CHANNELLIMIT2 + messages.NOTICE_ENDFIX,"");	
		}	
		
		if (xml.firstChild.childNodes[1].attributes.value == "-l") {
			var limit:String = xml.firstChild.childNodes[2].attributes.value;
			var channel:String = xml.firstChild.childNodes[0].attributes.value;
			functions.updateDialog(channel,messages.NOTICE_PREFIX + messages.MESSAGE_CHANNELNOLIMIT + messages.NOTICE_ENDFIX,"");	
		}		
		
	}
	
	/**
	  processes the 'USERHOST' command
	*/
	public function processUserhost(xml:XML) { 
		var host:String = getHostFromPrefix(xml.firstChild.lastChild.attributes.value);
		var user:String = xml.firstChild.lastChild.attributes.value;
		user = user.substring(1,user.indexOf('='));    

		var cmd:Command = commandQueue.getCommand("USERHOST",user);
		if (cmd != undefined) {
			cmd.params[2] = "*!*@" + host;
			cmd.execute();    
		}
	}
	
	/**
	  updates the dialog window with an error message
	*/
	public function processError(xml:XML) {
		functions.updateDialog("",xml.firstChild.childNodes[0].attributes.value,"");
	}
	
	
	/**
	  updated the dialog window with the 'cannot send to channel' message
	*/
	public function processNoSend(xml:XML) {
		functions.updateDialog(xml.firstChild.childNodes[1].attributes.value,messages.NOTICE_PREFIX + messages.MESSAGE_NOSEND + messages.NOTICE_ENDFIX,"");	
	}
	
	/**
	  processes the 'KICK' command
	*/
	public function processKickUser(xml:XML) { 	
		var user:String = this.getUserFromPrefix(xml.firstChild.attributes.prefix);	
		var reason:String = xml.firstChild.childNodes[2].attributes.value;
		if (reason == undefined) {
			reason = messages.MESSAGE_KICKED_NOREASON;	
		}
		var channel:String = xml.firstChild.childNodes[0].attributes.value;
		var thisUser:String = xml.firstChild.childNodes[1].attributes.value;
		
		if (thisUser == settings.myUsername) {
			settings.channels.get(channel).kicked = true;
			functions.closeChannel(channel);
			chat.showErrorMessage(messages.NOTICE_PREFIX + extras.replace(messages.MESSAGE_KICKED_THISUSER,"[channel]",channel) + " (" + reason + ")" + messages.NOTICE_ENDFIX,"");
		} else {
			functions.updateDialog(channel,messages.NOTICE_PREFIX + thisUser + messages.MESSAGE_KICKED + " (" + reason + ")" + messages.NOTICE_ENDFIX,"");
			functions.removeUser(channel,thisUser,"");	
		}		
	}
	
	/**
	  processes the list of banned users for a certain channel
	*/
	public function processBanList(xml:XML) {
		var channelid:String = xml.firstChild.childNodes[1].attributes.value;
		var user:String = xml.firstChild.childNodes[2].attributes.value;
		chat.myProcessBanList(channelid,user);	
	}
	
	/**
	  processes the end of the list of banned users for a certain channel
	*/
	public function processEndBanList(xml:XML) {
	//	this.myProcessEndBanList(xml.firstChild.childNodes[1].attributes.value);
	}
	
	/**
	  shows an errormessage if the current users is banned from the channel
	*/
	public function processBannedFromChannel(xml:XML) {
		chat.showErrorMessage(messages.MESSAGE_BANNEDFROMCHANNEL);
	}
	
	/**
	  logs on the current user
	*/
	public function processLogon(command:String,xml:XML) {
		settings.loggedOn = true;	
		//??????????????????????????chat.myOnLogon();
		this.processCommand(command,xml);	
	}
	
	/**
	  processes the 'PART' message when a user leaves a channel
	*/
	public function processPart(xml:XML) {
		var user:String = getUserFromPrefix(xml.firstChild.attributes.prefix);
		var message:String = messages.NOTICE_PREFIX + user + messages.MESSAGE_LEAVEUSER + messages.NOTICE_ENDFIX;
		var channel:String = xml.firstChild.firstChild.attributes.value;
		functions.removeUser(channel,user,message);
	}
	
	/**
	  processes the 'QUIT' message when a user leaves a channel
	*/
	public function processQuit(xml:XML) {
		var user = getUserFromPrefix(xml.firstChild.attributes.prefix);
		var message =messages.NOTICE_PREFIX + user +  messages.MESSAGE_LEAVEUSER + messages.NOTICE_ENDFIX;
		var channel = xml.firstChild.firstChild.attributes.value;
		functions.removeUser(channel,user,message);
	}
	
	/**
	  process incoming private messages (from users to a channel, or users to this user)
	*/
	public function processPrivmsg(xml:XML) {
		var user:String = getUserFromPrefix(xml.firstChild.attributes.prefix);
		var message:String = extras.convertTags(xml.firstChild.lastChild.attributes.value);
		var channel:String = xml.firstChild.firstChild.attributes.value;  	
		if (!functions.checkHiddenUser(user)) {
		    //check to see if it's a private chat
			if (channel == settings.myUsername) {
				if (settings.privateChatEnabled || (settings.knownOperators.get(user) != undefined) || settings.operatorMode) {
					if (settings.privateChannels.get(channel) == undefined && settings.privateChannels.get("@" + channel) == undefined) {
						channel = user;	
						chat.newPrivateChat = channel;
						functions.createPrivateChat(user,false);
						channel = user;
						functions.updateDialog(channel,message,user);        			
					}       		  
				} 
				else {
					//send a WHOIS to see if the user is a moderator/operator and therefor allow the incoming message
					this.sendCommand("WHOIS",[user]);
					var cmd:Command = new Command("313",user,"updateDialog2",[user,message,user],1,null,null);
					commandQueue.addCommand(cmd);		  
				}
			}
			else {
			functions.updateDialog(channel,message,user);
			}
		}	
	}
	
	/**
	  processes the list of channels generated by the sending of the 'LIST' command
	*/
	public function processRecieveChannelList(command:String,xml:XML){
		var id:String = xml.firstChild.childNodes[1].attributes.value
		var users:Number = xml.firstChild.childNodes[2].attributes.value;
		if (settings.channels.getChannel(id) == undefined) {		
			var channel2:Channel = new Channel(id);
			for(var i:Number=0;i<users;i++) {
				channel2.addUser("user" + i,"");
			}
			//update the list of available channels
			settings.availableChannels.putChannel(id,channel2);
			debug.sendDebug("addChannel: adding channel " + id + " to list");	
		}		
	}
	
	/**
	  processes the end of the list of channels generated by the sending of the 'LIST' command
	*/
	public function processEndChannelList(command:String,xml:XML) {
		chat.myProcessEndChannelList();
	}
	
	
	/**
	  process a general command message not defined in processInput
	*/
	public function processCommand(command:String,xml:XML) {
		if (settings.channels.getAll() != undefined) {
			//updateDialog(activeChannel,xml.firstChild.lastChild.attributes.value + NEWLINE,"");	
		} else {
		    chat.showNoticeMessage(xml.firstChild.lastChild.attributes.value);
		}
	}
	
	/**
	  process a change of topic on a channel
	*/
	public function processChannelTopic(xml:XML) {
		var user:String = getUserFromPrefix(xml.firstChild.attributes.prefix);
		var channel:String = xml.firstChild.lastChild.previousSibling.attributes.value;
		functions.setTopic(user,channel,xml.firstChild.lastChild.attributes.value);
	}
	
	/**
	  process a user joining a channel
	*/
	public function processJoin(xml:XML) {
		var channel:String = xml.firstChild.lastChild.attributes.value;
		if (settings.channels.getChannel(channel) == undefined) { 
			functions.createChannel(channel,"public");
			var message:String = messages.NOTICE_PREFIX + messages.MESSAGE_JOINCHANNEL + channel + "." + messages.MESSAGE_JOINCHANNEL2 + messages.NOTICE_ENDFIX;		
			functions.updateDialog(channel,message,"");
		} else {
			var user:String = xml.firstChild.attributes.prefix;
			if (!functions.checkHiddenUser(this.getUserFromPrefix(user))) {
			var message:String = messages.NOTICE_PREFIX + this.getUserFromPrefix(user) + " " + messages.MESSAGE_ENTERUSER + messages.NOTICE_ENDFIX;		
			functions.updateDialog(channel,message,"");
				functions.addUser(channel,user);
			}
		}
	}
	
	/**
	  process the list of users on a channel
	*/
	public function processUserList(xml:XML) {
		var users:String = xml.firstChild.lastChild.attributes.value;
		var channel:String = xml.firstChild.firstChild.nextSibling.nextSibling.attributes.value;
		var checkPos:Number = users.indexOf(" ");
		while (checkPos > 0) {
			var user:String = users.substring(0,checkPos);
			if (!functions.checkHiddenUser(user)) {
			functions.addUser(channel,user);
		}
		users = users.substring((checkPos + 1),users.length);
		checkPos = users.indexOf(" ");
		
		}
		if (!functions.checkHiddenUser(users.substring(0,users.length))) {
			functions.addUser(channel,users.substring(0,users.length));
		}
	}
	
	/**
	  process the the error message if the nickname used already exists
	*/
	public function processNickExists(xml:XML) {
		//debug(xml.firstChild.lastChild.attributes.value);
		//errors = xml.firstChild.lastChild.attributes.value;
		settings.errors = messages.ERROR_NICKEXISTS;
		if (settings.activeChannel != undefined) {
			functions.updateDialog(settings.activeChannel,messages.NOTICE_PREFIX + messages.ERROR_NICKEXISTS + messages.NOTICE_ENDFIX,"");	
		} else {
		    chat.showErrorMessage(messages.ERROR_NICKEXISTS); 
		}
	}
	
	/**
	  process a non-valid nickname
	*/
	public function processNickNotValid(xml:XML) {
		settings.errors = messages.ERROR_NICKNOTVALID;
		settings.connected = false;
		settings.loggedOn = false;
		settings.myUsername = "";
		socket.XMLsocket.close();
	}
	
	/**
	  process the response when a message that has been sent is filtered
	*/
	public function processMessageFiltered(xml:XML) {
		functions.updateDialog(settings.activeChannel,messages.NOTICE_PREFIX + messages.ERROR_MSGFILTERED + messages.NOTICE_ENDFIX,"");		
	}
	
	/**
	  process a change nickname from a user
	*/
	public function processNick(xml:XML) {
		var olduser:String = getUserFromPrefix(xml.firstChild.attributes.prefix);
		var newuser:String = xml.firstChild.childNodes[0].attributes.value;
		
		//check all channels to see if the user is on that channel
		for(var item:String in settings.channels.items) {
			var channel:Channel = settings.channels.getChannel(item);
			for(var user:String in settings.channels.getChannel(item).users.items) {
				var user:String = user;
				//do a check to see if the user is a moderator
				if (user.substr(0,1) == "@") {
					if (user.substring(1,user.length) == olduser) {
						channel.changeUser("@" + olduser,"@" + newuser);
						functions.updateDialog(channel.ID,messages.NOTICE_PREFIX + "@" + olduser + messages.MESSAGE_CHANGENICK + "@" + newuser + messages.NOTICE_ENDFIX,"");
					}
					
				} else {
					if (user == olduser) {
						channel.changeUser(olduser,newuser);
						functions.updateDialog(channel.ID,messages.NOTICE_PREFIX + olduser + messages.MESSAGE_CHANGENICK + newuser + messages.NOTICE_ENDFIX,"");
					}
				}	
			}	
		}
		//see if there are any private chats with this user
		if (settings.privateChannels.get("@" + olduser) != undefined) {
			settings.privateChannels.get("@" + olduser).changeUser("@" + olduser,"@" + newuser);
			settings.privateChannels.get("@" + olduser).ID = "@" + newuser;
			settings.privateChannels.changeKey("@" + olduser,"@" + newuser);
		} else	if (settings.privateChannels.get(olduser) != undefined) {
			settings.privateChannels.get(olduser).changeUser(olduser,newuser);
			settings.privateChannels.get(olduser).ID = newuser;
			settings.privateChannels.changeKey(olduser,newuser);
		}
		chat.myProcessNick(olduser,newuser);			
	}
	
	
	/**
	  process the response to the PING message
	*/
	public function processPing(xml:XML) {
	debug.sendDebug("processPing: recieving ping");
		this.sendCommand("PONG",[settings.myHost]);
	}
	
	/**
	  send the KICK command (kicks a user from a channel)
	*/
	public function sendKick(channel:String,user:String,message:String) {
		if (message == "") {
			message = settings.myUsername;
		}
		this.sendCommand("KICK",[channel,user,message]);	
	}
	
	/**
	  send the JOIN (channel) command
	*/
	public function sendJoin(channel:String) {
		if (settings.channels.get(channel) == undefined) {
			this.sendCommand("JOIN",[channel]);
		}
	}
	
	/**
	  send the PRIVMSG message (messages to channels and users)
	*/
	public function sendPrivmsg(channel:String,message:String) {
		this.sendCommand("PRIVMSG",[channel,message]);
	}
	
	/**
	  send the PART command (to leave a channel)
	*/
	public function sendLeaveChannel(channel:String) {
		this.sendCommand("PART",[channel]);
	}
	
	/**
	  send the login data
	*/
	public function sendLogin(username:String,password:String) {
		this.sendCommand("PASS",[password]);
		this.sendCommand("NICK",[username]);
		this.sendCommand("USER",[username,"flashClient",settings.myHost,username]);
	}
	
	/**
	  send the QUIT command (to quit the chat)
	*/
	public function sendQuit() {
		this.sendCommand("QUIT",[" "]);
	}
	
	/**
	  same as sendLeaveChannel, don't know why this one is here :S
	*/
	public function sendPart(channel:String) {
		this.sendLeaveChannel(channel)
	}
	
	
	/**
	  send the the specified command with optional parameters
	*/
	public function sendCommand(command:String, parameters:Array) {
		var message:XML = new XML();
		message.nodeName = "message";
		message.attributes.command = command;
		//add parameter to the command
		for (var i:Number=0;i < parameters.length;i++) {
			var param:XML = new XML();		
			param.nodeName = "parameter";
			param.attributes.value = parameters[i];
			message.appendChild(param);
		}
		socket.sendXML(message);	
	}		
}