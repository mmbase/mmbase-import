package org.mmbase.module;

import org.mmbase.module.sessionInfo;
import org.mmbase.util.scanpage;

public interface CounterInterface
{
	public String getTag( String part, sessionInfo session, scanpage sp );
}
