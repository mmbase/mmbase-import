/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Detect
{
	var playerVersion:Number;
	var myLength:Number;
	var majorVersion:String;
	var minorVersion:String;

	function Detect()
	{
		playerVersion = eval("$version"); 
		myLength = length(playerVersion);
		majorVersion = " ";
		minorVersion = " ";
		
		while (i<=myLength) 
		{
			var i:Number = i+1; 
			var temp:String = substring(playerVersion, i, 1); 
			 if (temp eq " ") 
			{ 
				var platform:String = substring(playerVersion, 1, i-1); 
				 majorVersion = substring(playerVersion, i+1, 1);
				var secondHalf:String = substring(playerVersion, i+1, myLength-i);
				minorVersion = substring(secondHalf, 5, 2);
			 } 
		} 
	
	// Here are some example statements to determine 
	// specific major/minor version information.
		
		if ((majorVersion >= 7) ||((majorVersion >= 6) && (minorVersion >=21))) {
			play();
		}
		else {
			stop();
		}
	}
}