/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
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
