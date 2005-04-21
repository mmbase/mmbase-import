/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module;

import java.util.*;
import java.awt.*;

import org.mmbase.util.*;

public interface PlaylistsInterface {
	public void init();
	public byte[] getRAMfile(boolean isInternal, Vector params);
}
