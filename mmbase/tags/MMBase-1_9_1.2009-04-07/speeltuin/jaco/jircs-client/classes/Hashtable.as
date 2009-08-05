/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Hashtable
{
	var items:Array;
	var lastItem:Object;
	var debug:Debug
	
	public function Hashtable()
	{
		this.items = new Array();
		this.debug = debug;
	}
	//puts the object in the Hashtable using the key
	public function put(key:String,object:Object) {
		this.items[key] = object;		
	}
	
	//puts the object in the Hashtable using the key
	public function putChannel(key:String,object:Channel) {
		this.items[key] = object;		
	}
	
	public function get(key:String):Object {
		return this.items[key];		
	}
	
	//returns the element identified bij the key, returns undefined if the key is nog present
	public function getChannel(key:String):Channel {
		return this.items[key];		
	}
	
	public function putCommand(key:String,object:Command) {
		this.items[key] = object;
	}
	
	public function getCommand(key:String):Command {
		return this.items[key];		
	}
	
	public function changeKey(oldkey:String,newkey:String) {
		var item:Object = this.items[oldkey];
		this.items[newkey] = item;
		delete this.items[oldkey];
	}
	
	//returns an array with all the objects in the array
	public function getAll():Array {
		return items;
	}
	
	public function getKeys():Array {
		var returnValue = new Array();
		for(var item:String in this.items) {
			returnValue.push(item);
		}
		return returnValue;				
	}

	//removes the element identified by the key, does nothing if the key is not present
	public function remove(key) {
		var succes:Boolean = delete this.items[key]
		debug.sendDebug("Hashtabel remove: " + key + " " + succes);
	}
	
	//returns the first element in the Hashtable
	public function peek():Channel {
		var returnValue:Channel = undefined;
		for (var item:String in this.items) {
			returnValue = this.items[item];	
		}
		return returnValue;
	}
	
	//count the items in the hashtable and return the value
	public function count():Number {
		var i:Number = 0;
		for (var item:String in this.items) {
			i++;
		}
		return i;
	}
	
	public function clear() {
		delete this.items;
		this.lastItem = undefined;
		this.items = new Array();
	}
};