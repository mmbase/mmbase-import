package org.mmbase.module.builders.vwms;

import java.util.*;
import java.net.*;
import java.sql.*;

import org.mmbase.module.core.*;

public interface VwmProbeInterface {
	public boolean probeCall();
	public String getName();
	public boolean performTask(MMObjectNode node);
}
