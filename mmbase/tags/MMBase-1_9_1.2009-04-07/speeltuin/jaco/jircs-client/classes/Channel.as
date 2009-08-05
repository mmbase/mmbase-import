 /*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Channel
{
	var ID:String;
	var lastMessage:String;
	var topic:String;
	var dialog:Array;
	var users:Hashtable;
	var kicked:Boolean;
	var operator:Boolean;
	var textColor:String;
	
	var settings:Settings;
	
	public function Channel(ID)
	{
		this.ID = ID;
		this.lastMessage = "";
		this.topic = "";
		this.dialog = new Array();
		this.users = new Hashtable();
		this.kicked = false;
		this.operator = false;
		this.textColor = "black";		
		
		this.settings = new Settings();
	}
	

	public function getDialog():String{
		var returnValue:String = "";
		for(var i:Number = 0;i < this.dialog.length;i++) {
			returnValue += this.dialog[i];
		}
		return returnValue;
	}

	public function addMessage(message:String) {
		this.dialog.push(message);
		if (this.dialog.length >= settings.screenBuffer) {
		    this.dialog.shift();
		}
	}

	public function addUser(userID:String,host:String) {
		this.users.put(userID,host);	
	}

	public function changeUser(oldID:String,newID:String) {
		this.users.changeKey(oldID,newID);
	}	
	
	public function removeUser(user:String) {
		this.users.remove(user);
	}
}