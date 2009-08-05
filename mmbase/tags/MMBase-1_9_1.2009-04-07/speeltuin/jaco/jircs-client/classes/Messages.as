/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Messages
{
	var NEWLINE:String;
	
	var ERROR_NOCONNECTION:String;
	var ERROR_CONNECTIONLOST:String;
	var ERROR_NICKEXISTS:String;
	var ERROR_NICKNOTVALID:String; 
	var ERROR_WRONG_PASSWORD:String; 
	var ERROR_MSGFILTERED:String;
	var ERROR_CHANNEL_FULL:String;
	var MESSAGE_USER_SAYS:String;
	
	var TOPIC_PRIVATE:String;
	
	var MESSAGE_CHANGENICK:String; 
	var MESSAGE_ENTERUSER:String; 
	var MESSAGE_JOINCHANNEL:String; 
	var MESSAGE_JOINCHANNEL2:String; 
	var MESSAGE_LEAVEUSER:String;
	var MESSAGE_SETTOPIC:String; 
	var MESSAGE_BANNEDFROMCHANNEL:String; 
	var MESSAGE_YOUAREOPERATOR:String; 
	var MESSAGE_YOUARENOTOPERATOR:String;
	var MESSAGE_USERISOPERATOR:String;
	var MESSAGE_USERISNOTOPERATOR:String; 
	var MESSAGE_USERBANNED1:String; 
	var MESSAGE_USERBANNED2:String; 
	var MESSAGE_USERUNBANNED1:String;
	var MESSAGE_USERUNBANNED2:String; 
	var MESSAGE_KICKED_NOREASON:String; 
	var MESSAGE_KICKED:String; 
	var MESSAGE_KICKED_THISUSER:String;
	var MESSAGE_YOUAREKICKED:String;
	var MESSAGE_NOSEND:String; 
	var MESSAGE_CHANNELCLOSED:String; 
	var MESSAGE_CHANNELMODERATED:String;
	var MESSAGE_CHANNELDEMODERATED:String; 
	var MESSAGE_CHANNELLIMIT1:String; 
	var MESSAGE_CHANNELLIMIT2:String; 
	var MESSAGE_CHANNELNOLIMIT:String; 
	var MESSAGE_PRIVATECHAT_DISABLED:String; 
	
	
	var MESSAGE_MODERATORMODE:String; 
	var MESSAGE_LASTMESSAGE:String;
	
	var NOTICE_PREFIX:String; 
	var NOTICE_ENDFIX:String; 
	
	var MY_OPEN_CHANNELS_TITLE:String; 
	var MY_OTHER_CHANNELS_TITLE:String; 
	
	var NO_OPEN_CHANNELS:String; 
	var MY_OPEN_CHANNELS:String; 
	var MY_OTHER_CHANNELS:String; 
	var MY_PRIVATE_CHATS:String;
	var NO_OTHER_CHANNELS:String;
	var CHANNEL_CHOOSE:String; 
	
	var CREATE_CHANNEL:String;
	var TOPIC_CHANNEL:String;
	var CLOSE_CHANNEL:String;
	var SETLIMIT_CHANNEL:String; 
	var MODERATED_CHANNEL:String; 
	var DEMODERATED_CHANNEL:String; 
	
	public function Messages()
	{
		this.NEWLINE = "<br>";
		
		this.ERROR_NOCONNECTION = "Kan geen verbinding maken met de server.";
		this.ERROR_CONNECTIONLOST = "De verbinding met de server is verbroken.";
		this.ERROR_NICKEXISTS = "Deze nickname bestaat al. Kies een andere.";
		this.ERROR_NICKNOTVALID = "Deze nickname is niet geldig. Kies een andere.";
		this.ERROR_WRONG_PASSWORD = "Naam en/of wachtwoord verkeerd";
		this.ERROR_MSGFILTERED = "Je bericht is gefiltered en kon niet verzonden worden";
		this.ERROR_CHANNEL_FULL = "Je kan niet naar dit kanaal toe. Het kanaal is vol."
		
		this.TOPIC_PRIVATE = "prive chat";
		
		this.MESSAGE_CHANGENICK = " heet nu: ";
		this.MESSAGE_USER_SAYS = "zegt:";
		this.MESSAGE_ENTERUSER = "komt binnen";
		this.MESSAGE_JOINCHANNEL = "Welkom in ";
		this.MESSAGE_JOINCHANNEL2 = " Alle gesprekken op deze server worden gelogd.";
		this.MESSAGE_LEAVEUSER = " is weggegaan";
		this.MESSAGE_SETTOPIC = "Het onderwerp van dit kanaal is: ";
		this.MESSAGE_BANNEDFROMCHANNEL = "Je hebt geen toegang tot dit kanaal.";
		this.MESSAGE_YOUAREOPERATOR = "Je bent moderator van dit kanaal geworden.";this.MESSAGE_YOUAREOPERATOR = "Je bent moderator van dit kanaal geworden.";
		this.MESSAGE_YOUARENOTOPERATOR = "Je bent geen moderator meer van dit kanaal.";
		this.MESSAGE_USERISOPERATOR = " is moderator van dit kanaal geworden.";
		this.MESSAGE_USERISNOTOPERATOR = " is niet langer moderator van dit kanaal.";
		this.MESSAGE_USERBANNED1 = "Moderators: gebruiker met ipadres: ";
		this.MESSAGE_USERBANNED2 = " mag niet meer op dit kanaal komen.";
		this.MESSAGE_USERUNBANNED1 = "Moderators: gebruiker met ipadres: ";
		this.MESSAGE_USERUNBANNED2 = " mag weer op dit kanaal komen.";
		this.MESSAGE_KICKED_NOREASON = " er is geen reden opgegeven.";
		this.MESSAGE_KICKED = " is van het kanaal verwijderd."
		this.MESSAGE_KICKED_THISUSER = "Je bent uit [channel] verwijderd.";
		this.MESSAGE_YOUAREKICKED = MESSAGE_KICKED_THISUSER;
		this.MESSAGE_NOSEND = "Het bericht kan niet naar het kanaal verzonden worden.";
		this.MESSAGE_CHANNELCLOSED = "Dit kanaal wordt gesloten.";
		this.MESSAGE_CHANNELMODERATED = "Er kunnen geen berichten meer naar dit kanaal gestuurd worden.";
		this.MESSAGE_CHANNELDEMODERATED = "Er kunnen weer berichten naar dit kanaal gestuurd worden.";
		this.MESSAGE_CHANNELLIMIT1 = "Er is een chatters-limiet op dit kanaal gezet. Er mogen maximaal ";
		this.MESSAGE_CHANNELLIMIT2 = " chatters op dit kanaal komen.";
		this.MESSAGE_CHANNELNOLIMIT = "Er zit geen chatters-limiet meer op dit kanaal.";
		this.MESSAGE_PRIVATECHAT_DISABLED = "Deze gebruiker kan geen prive berichten ontvangen.";
		
		
		this.MESSAGE_MODERATORMODE = "Moderator modus geactiveerd";
		this.MESSAGE_LASTMESSAGE = "laatste bericht: ";
		
		this.NOTICE_PREFIX = "*** ";
		this.NOTICE_ENDFIX = " ***";
		
		this.MY_OPEN_CHANNELS_TITLE = "mijn\n kanalen";
		this.MY_OTHER_CHANNELS_TITLE = "overige\n kanalen";
		
		this.NO_OPEN_CHANNELS = "--- je hebt geen open kanalen ---";
		this.MY_OPEN_CHANNELS = "--- mijn kanalen ---";
		this.MY_OTHER_CHANNELS = "--- overige kanalen ---";
		this.MY_PRIVATE_CHATS = "--- mijn prive gesprekken ---";
		this.NO_OTHER_CHANNELS = "--- er zijn geen overige kanalen ---";
		this.CHANNEL_CHOOSE = "--- kies een kanaal ---";
		
		this.CREATE_CHANNEL = "kanaal aanmaken";
		this.TOPIC_CHANNEL = "onderwerp veranderen";
		this.CLOSE_CHANNEL = "kanaal sluiten";
		this.SETLIMIT_CHANNEL = "max. gebruikers instellen";
		this.MODERATED_CHANNEL = "kanaal sluiten voor berichten";
		this.DEMODERATED_CHANNEL = "kanaal openen voor berichten";
	}
}