/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class Blinker
{
	var targetObject:Object;
	var blinkid:String;	
	var blinknr:Number;
	var maxblink:Number;
	var intervalid:Number;
	var blinkon:Boolean;
	var finished:Boolean;	
	var extras:Extras;

	public function Blinker2(id:String,targetObject:Object)
	{
		extras = new Extras()
		this.targetObject = targetObject;
		this.blinkid = id;	
		this.blinknr = 0;
		this.maxblink = 4;
		this.intervalid = setInterval(extras.blinkerCallback, 500,this);
		this.blinkon = true;
		this.finished = false;	
	}
}