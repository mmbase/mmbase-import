package org.mmbase.module;

import org.mmbase.util.scanpage;
import org.mmbase.module.sessionInfo;

public interface CounterImplementationInterface
{
	public String getTag( String part, sessionInfo session, scanpage sp );
}
