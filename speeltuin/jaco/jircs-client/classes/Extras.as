/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Extras
{
	var debug:Debug;
	var myBlinkers:Array;
	var settings:Settings;
	var chat:ChatBox;
	
	public function Extras(settings:Settings, chat:ChatBox)
	{
		this.settings = settings;
		this.chat = chat;
		this.debug = new Debug();
		this.myBlinkers = new Array();
		
		if (this.getMovieParam("user") != "" && this.getMovieParam("user") != undefined) {
			settings.myUsername = this.getMovieParam("user");
		}
		if (this.getMovieParam("session") != "" && this.getMovieParam("session") != undefined) {
			settings.myPassword = this.getMovieParam("session");	
		}
		if (this.getMovieParam("host") != "" && this.getMovieParam("host") != undefined) {
			settings.myHost = this.getMovieParam("host");	
		}
		if (this.getMovieParam("port") != "" && this.getMovieParam("port") != undefined) {
			settings.myPort = Number( this.getMovieParam("port"));	
		}
		if (this.getMovieParam("enterchannel") != "" && this.getMovieParam("enterchannel") != undefined) {
			settings.myFirstChannel = this.getMovieParam("enterchannel");	
		}
		if (this.getMovieParam("private") != "" && this.getMovieParam("private") != undefined) {
			if (this.getMovieParam("private") == "false") {
				settings.privateChatEnabled = false;
			}
		}
	}

	
	public function addLinks(theString:String):String {
		var startIndex:Number = 0;
		var tempString:String = "";
		var linkString:String;
		var URL:String;
		var beginURL:Number;
		var endURL:Number;
		if(theString.indexOf("www") != -1) {
			while(theString.indexOf("www", startIndex) != -1) {
				tempString += theString.substring(startIndex, theString.indexOf("www", startIndex));
				beginURL = theString.indexOf("www", startIndex);
				endURL = (theString.indexOf(" ", beginURL) != -1) ? theString.indexOf(" ", beginURL) : theString.length;
				URL = theString.substring(beginURL, endURL);
				linkString = "<A HREF='http://" + URL + "' TARGET='_blank'><U>" + URL + "</U></A>";	
				tempString += linkString;
				startIndex = endURL;
			}
			tempString += theString.substring(endURL, theString.length);
			debug.sendDebug(tempString);
			return tempString;
		} else {
			debug.sendDebug(theString);
			return theString;
		}
	}

// *** changes < and > to &lt; and &gt;
	public function convertTags(theString:String):String {
		var tempString:String = "";
		for(var i:Number = 0; i < theString.length; i++) {
			if(theString.charAt(i) == "<") {
				tempString += "&lt;";
			} else if (theString.charAt(i) == ">") {
				tempString += "&gt;";
			} else {
				tempString += theString.charAt(i);
			}
		}
		return tempString;
	}

	public function getTimeString():String {
		var now:Date = new Date();
		var hours:Number = now.getHours();
		var minutes:Number = now.getMinutes();
		var seconds:Number = now.getSeconds();
		hours = (hours < 10 ? 0 : null) + hours;
		minutes = (minutes < 10 ? 0 : null) + minutes;
		seconds = (seconds < 10 ? 0 : null) + seconds;	
		return (hours + ":" + minutes + ":" + seconds);
	}

	public function blinkerCallback(blinker:Object) {
		if (blinker.blinknr < blinker.maxblink) {
			if (blinker.blinkon) {
				chat.channellist.setBlink(blinker.blinkid);
				blinker.blinkon = false;
				debug.sendDebug("blinkcb: " + blinker.blinkid);
			} else {
				chat.channellist.setBlink(undefined);
				blinker.blinkon = true;
			}
			blinker.blinknr++;
		} else {
			blinker.finished = true;
			clearInterval(blinker.intervalid);	
		}
	}

	public function blink(id:String) {
		debug.sendDebug("blink: " + id);
		//remove any finished blinkers
		for (var i:Number = 0;i < myBlinkers.length;i++) {
			if (myBlinkers[i].finished) {
				myBlinkers.splice(i,1);		
			}
		}	
			
		var blinkerExists:Boolean = false;
		//first see if there's allready a blinker for this id
		for (var i:Number = 0;i < myBlinkers.length;i++) {
			if (myBlinkers[i].blinkid == id) {
				blinkerExists = true;
				break;
			}
		}
	
		//create a new blinker
		if (!blinkerExists) {
			var myBlinker:Blinker = new Blinker(id,this);
			myBlinkers.push(myBlinker);
			debug.sendDebug("blink new blinker: " + id);
		}
	}

	public function getMovieParam(id:String):String {
		var returnValue:String = "";
		var url:String = _url;
		var startP:Number= url.indexOf(id,0);
		if (startP > 0) {
			var endP:Number = url.indexOf("&",startP);
			if (endP <= 0) {
				endP = url.length;	
			}
			returnValue = url.substr((startP + id.length + 1),(endP -(startP + id.length + 1)));
			debug.sendDebug("getMovieParam: " + returnValue);
		}
		return returnValue;	
	}

	public function replace(source:String,find:String,replaceString:String):String {
		var buf:String = "";
		var index:Number = source.indexOf(find);
		var len:Number = find.length;	// Length of substring
		var start:Number = 0;		// Records index of previously replaced substring
		while (index != -1) {
		    buf += (source.substring(start, index));		// From start rather than from zero
		    buf += replaceString;
		    start = index + len;			// Work out new start position in string
		    index = source.indexOf( find, start );		// Use indexOf(String,int)
		}
		buf += ( source.substring( start ) );
		return buf;
	}
}