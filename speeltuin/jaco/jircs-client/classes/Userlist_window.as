/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Userlist_window
{
	var focus:String;
	var clicked:String;
	var totalUsers:String;
	var settings:Settings;
	var functions:Functions;
	var debug:Debug;
	var chat:ChatBox;
	
	function Userlist_window(settings:Settings, functions:Functions, chat:ChatBox)
	{
		this.settings = settings;
		this.functions = functions;
		this.debug = new Debug(settings);
				
		this.focus = "noChannel"; 
		this.totalUsers = "(0)";	
		this.chat = chat;
	}

	public function doPrivate() {
		if (settings.privateChatEnabled || settings.operatorMode || settings.channels.getChannel(settings.activeChannel).operator) {
			var myItem:String = _root.chat.userlist_window.myList.getSelectedItem().data;
			if (myItem != "" && myItem != undefined) {
				if (myItem != settings.myUsername) {
					if (settings.privateChannels.getChannel(myItem) == undefined) {
						functions.createPrivateChat(myItem,true);	
					} else {
						chat.showPrivate(myItem);
					}
				}
			}
		}	
	}

	public function update() {		
		if (_root.operatorMode) {
			_root.chat.userlist_window.buttonKick._visible = true;
			_root.chat.userlist_window.buttonBan._visible = true;
		} 
		else {
			_root.chat.userlist_window.buttonKick._visible = false;	
			_root.chat.userlist_window.buttonBan._visible = false;	
		}
		
		clicked = undefined;
		if (settings.activeChannel == undefined) {
			focus = "noChannel";
			totalUsers = "(0)";		
			_root.chat.userlist_window.gotoAndStop("noChannel");
		} else {
			focus = "chatters";
			_root.chat.userlist_window.gotoAndStop("chatters");
			this.updateUserList();
		}		
	}

	public function updateUserList() {
		_root.chat.userlist_window.myList.removeAll();
	
		totalUsers = "(" + settings.channels.getChannel(settings.activeChannel).users.count() + ")";
		if (!settings.channels.getChannel(settings.activeChannel).kicked) {
			for(var item:String in settings.channels.getChannel(settings.activeChannel).users.items) {
				_root.chat.userlist_window.myList.addItem(item,item);
			}
		}
		_root.chat.userlist_window.totalUsers.text = this.totalUsers;
	}
	
	public function myChangeHandler(component:Object) {
		var myItem:String = _root.chat.userlist_window.myList.getSelectedItem().data;
		if (clicked == myItem)  {
			clicked = undefined;
			this.doPrivate();
		} else {
			clicked = myItem;
		}			
	}
	
	public function setVars()
	{
		_root.chat.userlist_window.privateMessageNotice._visible = false;
		if (!chat.settings.privateChatEnabled && !chat.settings.operatorMode) {
			_root.chat.userlist_window.button_private._visible = false;
			_root.chat.userlist_window.privateMessageNotice._visible = true;
		}	
	}	
}