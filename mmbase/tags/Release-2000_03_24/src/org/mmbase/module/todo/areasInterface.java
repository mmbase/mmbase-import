/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module;

import java.util.*;
import java.awt.*;
import javax.servlet.http.*;

import org.mmbase.util.*;

public interface areasInterface {
	public String getState(String name);
	public String getValue(String name);
	public String setValue(String name, String value);
	public String setState(String name, String value);
}
