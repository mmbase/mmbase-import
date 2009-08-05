/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Settings
{
	var chat_version:String;
	
	var loggedOn:Boolean;
	var connected:Boolean;
	
	var errors:String;
	var ignoreList:Array;
	
	var myHost:String;
	var myPort:Number;
	var myUsername:String;
	var myPassword:String;
	var myFirstChannel:String;
	var operatorMode:Boolean;
	var privateChatEnabled:Boolean;
	var textColorEnabled:Boolean;
	
	var lastChannel:Number;
	var activeChannel:String;
	var channels:Hashtable;
	var privateChannels:Hashtable;
	var availableChannels:Hashtable;
	var channelsUpdated:Boolean;
	var maxChannels:Number;
	var maxPrivateChannels:Number;
	//var commandQueue:CommandQueue;
	var debug:Boolean;
	var commandsEnabled:Boolean;
	var knownOperators:Hashtable;
	var moderatorLoginTries:Number;
	
	var screenBuffer:Number;
	var NEWLINE:String;
	
	var hideSpecialUsers:Boolean;
	var hiddenUsers:Array;
	
	var defaultColors:Array;
	
	var tries:Number;
	
	public function Settings()
	{
		this.chat_version = "v2.0";
		
		this.loggedOn = false;
		this.connected = false;
		
		this.errors = "";
		this.ignoreList = new Array();
		
		this.myHost = "localhost";
		this.myPort = 5557;
		this.myUsername = "LightW";
		this.myPassword = "dasdf";
		this.myFirstChannel = undefined;
		this.operatorMode = false;
		this.privateChatEnabled = true;
		this.textColorEnabled = true;
		
		this.lastChannel = -1;
		this.activeChannel = undefined;
		this.channels = new Hashtable();
		this.privateChannels = new Hashtable();
		this.availableChannels = new Hashtable();
		this.channelsUpdated = false;
		this.maxChannels = 10;
		this.maxPrivateChannels = 10;
		//this.commandQueue = new CommandQueue();
		this.debug = false;
		this.commandsEnabled = false;
		this.knownOperators = new Hashtable();
		this.moderatorLoginTries = 0;
		
		this.screenBuffer = 500;
		
		this.NEWLINE = "<br>";
		
		this.hideSpecialUsers = true;
		this.hiddenUsers = new Array("reda_hid1","reda_hid2","reda_hid3","reda_hid4","reda_hid5","reda_hid6");
		
		this.defaultColors = new Array();
		this.defaultColors['black'] = new Array("1","000000");
		this.defaultColors['blue'] = new Array("2","3333FF");
		this.defaultColors['green'] = new Array("3","003300");
		this.defaultColors['red'] = new Array("4","FF0000");
		this.defaultColors['brown'] = new Array("5","993300");
		this.defaultColors['purple'] = new Array("6","CC0066");
		this.defaultColors['orange'] = new Array("7","FF6600");
		this.defaultColors['yellow'] = new Array("8","FFCC00");
		this.defaultColors['lightgreen'] = new Array("9","009933");		
		
		this.tries = 0;
	}
}