/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module;

import java.util.*;
import java.awt.*;


public interface MusicInterface {
	public void init();
	public Vector getAreaVector(String prog,String type);
	public Vector getAreaVector(Vector prog,String type);
}
