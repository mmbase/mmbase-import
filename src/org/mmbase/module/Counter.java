package org.mmbase.module;

import org.mmbase.module.CounterImplementationInterface;

import org.mmbase.module.ProcessorModule;
import org.mmbase.module.CounterInterface;
import org.mmbase.module.sessionInfo;
import org.mmbase.util.scanpage;

public 
	class 		Counter
	extends		ProcessorModule
	implements	CounterInterface
{
//  ----------------------------------------------------------------------------------	
	private String 	classname = getClass().getName();
	private boolean debug 	= true;
	private	void 	debug( String msg ) { System.out.println( classname +":"+ msg ); }
//  ----------------------------------------------------------------------------------	


//  ----------------------------------------------------------------------------------	
	public Counter()
	{

	}
//  ----------------------------------------------------------------------------------	

	public void init()
	{
	}

//  ----------------------------------------------------------------------------------	

	public String getTag( String part, sessionInfo session, scanpage sp )
	{
		CounterImplementationInterface counter;
		String result = null;
		int i;
		String params="";

		if( part != null && !part.equals(""))
			part = part.trim();

		if( debug ) debug("getTag("+part+")");

		// check what counter this tag tags to
		// -----------------------------------

		part = part.trim();

		i=part.indexOf(' ');
		if (i!=-1) {
			params=part.substring(i+1);
			part=part.substring(0,i);
		}
		debug("getTag() : module="+part+" params="+params);
		counter = (CounterImplementationInterface) getModule(part);

		if( counter != null ) {
			result = counter.getTag( params, session, sp );
		} else {
			debug("getTag(): ERROR: module "+part+" is not found and loaded!");
		}
		// debug("getTag(): result:"+result);
		return result;
	}
}
