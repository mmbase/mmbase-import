package org.mmbase.module;

import java.util.*;
import java.awt.*;

import org.mmbase.util.*;

public interface PlaylistsInterface {
	public void init();
	public byte[] getRAMfile(boolean isInternal, Vector params);
}
