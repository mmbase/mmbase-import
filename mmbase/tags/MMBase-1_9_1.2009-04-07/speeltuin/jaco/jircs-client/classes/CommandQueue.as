/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

class CommandQueue
{
	var queue:Hashtable;
	var debug:Debug;
	
	public function CommandQueue()
	{
		queue = new Hashtable();
		debug = new Debug();
	}
    /**
     * puts a command on the queue (to be processed later)
     *
     */
	public function addCommand(cmd:Command) 
	{
		debug.sendDebug("CommandQueue: adding command to queue: command:" + cmd.command + ", params:" + cmd.params + ", trigger:" + cmd.trigger + ", key:" + cmd.key);
		this.queue.putCommand(cmd.trigger,cmd); 
	}

    /**
     * gets a command and removes it from the queue. 
     * returns 'undefined' if there is no command with the specified trigger and key
     */
	public function getCommand(trigger:String,key:String):Command
	{
		var returnValue:Command;
		if (this.queue.getCommand(trigger) != undefined && this.queue.getCommand(trigger).key == key) 
		{
			returnValue = this.queue.getCommand(trigger);
			this.queue.remove(trigger);
			{
				return returnValue;
			}
		}
	}
}