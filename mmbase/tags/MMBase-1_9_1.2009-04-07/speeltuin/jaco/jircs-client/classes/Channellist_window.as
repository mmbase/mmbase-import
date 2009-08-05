/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Channellist_window
{
	var highlight:String;
	var focus:String; 
	var clicked:String;
	var mouseOver:Boolean;
	var tries:Number;
	var blinkid:String;
	
	var command:Command;
	var settings:Settings;
	var messages:Messages;
	var chat:ChatBox;
	var debug:Debug;
	
	function Channellist_window(settings:Settings, command:Command, chat:ChatBox)
	{
		mouseOver = false;
		tries = 0;
		
		this.debug = new Debug(settings);
		this.chat = chat;
		this.settings = settings;
		this.command = command;
		messages = new Messages();
	}

	public function onChoose() {
		var thisItem:String = _root.chat.channellist_window.myList.getSelectedItem().data;	
		if (thisItem != undefined && thisItem != "") {
			if (settings.privateChannels.getChannel(thisItem) != undefined) {
				chat.showPrivate(thisItem);
			} else {
				if (focus == "channels") {
					if (thisItem != settings.activeChannel) {
						chat.setFocus(thisItem);
					}
				}
				if (focus != "channels") {
					command.sendJoin(thisItem);
				}
				this.setFocus("channels");
			}
		}
	}

	public function myChangeHandler() {
		var thisItem:String = _root.chat.channellist_window.myList.getSelectedItem().data;	
		if (this.clicked == thisItem && this.blinkid != thisItem)  {
			this.clicked = undefined;
			this.onChoose();
		} else {
			this.clicked = thisItem;	
		}
	}

	public function setFocus(id:String) {
		clicked = undefined;
		if (focus == id) {
			this.updateChannelList();
		} else {
			if (!settings.channelsUpdated) {
				this.sendUpdateChannelList();
			} else {
				debug.sendDebug("setFocus.... go to " + id)
				focus = id;				
				_root.chat.channellist_window.gotoAndStop(id);
				this.updateChannelList();
			}
		}
	}
	
	public function setSelected(id) {
		if (!mouseOver) {
			highlight = id;
			if (id == undefined) {
				_root.myList.setSelectedIndex(undefined);
			} else {
				for (var i=0;i<_root.myList.getLength();i++) {
					if (_root.myList.getItemAt(i).data == id) {
						_root.myList.setSelectedIndex(i);
						_root.myList.setScrollPosition(i);
					}
				}
			}
		}
	}


	public function setBlink(id:String) {
		if (!mouseOver) {
			highlight = id;
			if (focus == "channels") {
				if (id == undefined) {
					_root.myList.setSelectedIndex(undefined);
				} else {
					for (var i=0;i<_root.myList.getLength();i++) {
						if (_root.myList.getItemAt(i).data == id) {
							_root.myList.setSelectedIndex(i);
							_root.myList.setScrollPosition(i);			
						}
					}
				}
			}
			clicked = undefined;
		}
	}


	public function update() {
		if (settings.activeChannel == undefined) {
			this.sendUpdateChannelList();	
		} else {
			this.updateChannelList();
		}
	}

	public function sendUpdateChannelList() {
		settings.availableChannels = new Hashtable();
		this.focus = undefined;
		settings.channelsUpdated = false;
		command.sendCommand("LIST",[]);
		_root.chat.channellist_window.gotoAndPlay("updating");
	}

	function updateChannelList() {
		_root.chat.channellist_window.myList.removeAll();
		if (focus == "channels") {
			if (settings.channels.count() < 1) {
				_root.chat.channellist_window.myList.addItem(messages.NO_OPEN_CHANNELS,"");			
			} else {			
				_root.chat.channellist_window.myList.addItem(messages.MY_OPEN_CHANNELS,"");
				for(var item:String in settings.channels.items) {
					var listItem:String = item;
					listItem += " (" + settings.channels.getChannel(item).users.count() + ")";	
					if (item == settings.activeChannel) {
						listItem += " *";	
					}
					_root.chat.channellist_window.myList.addItem(listItem,item)
				}			
			}
			
			if (settings.privateChannels.count() > 0) {
				_root.chat.channellist_window.myList.addItem(" ","");
				_root.chat.channellist_window.myList.addItem(messages.MY_PRIVATE_CHATS,"");
			}
			for(var item:String in settings.privateChannels.items) {
				var listItem:String = item;
				if (item == _parent.newPrivateChat) {
					listItem += " (nieuw)";
				}
				_root.chat.channellist_window.myList.addItem(listItem,item);
			}
		} 
		if (focus == "otherChannels") {	
			if (settings.availableChannels.count() == 0) {
				_root.chat.channellist_window.myList.addItem(messages.NO_OTHER_CHANNELS,"");
			} else {
				_root.chat.channellist_window.myList.addItem(messages.CHANNEL_CHOOSE,"");
				for (var item:String in settings.availableChannels.items) {
					var listItem:String = item + " (" + settings.availableChannels.getChannel(item).users.count() + ")";
					_root.chat.channellist_window.myList.addItem(listItem,item);
				}
			}
			if (highlight != undefined) {
				setSelected(highlight);
				highlight = undefined;
			}
		}
	}

	public function checkFirstTime()
	{
		if(chat.firstTime){
			chat.firstTime = false;
			this.setFocus("otherChannels");
		}	
	}
	
	public function doUpdateChannellist()
	{
		if (settings.channelsUpdated) {
			this.setFocus("otherChannels");	
		} else {
			if (this.tries < 30) {
				this.tries++;
				this.setFocus("updating");
			} else {
				this.tries = 0;
				settings.channelsUpdated = true;
				this.setFocus("otherChannels");	
			}
		}	
	}
}