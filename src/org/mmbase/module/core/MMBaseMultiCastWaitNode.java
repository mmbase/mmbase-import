/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.core;

/**
* MMBaseMultiCastWaitNode is a wrapper class for MMObjectNode we want to
* put into a 'waiting for a change' mode we don't block on the object
* itself because we deed to check its number before we nofity it again.
*
*/
public class MMBaseMultiCastWaitNode {

	MMObjectNode node;
	int number;

	public MMBaseMultiCastWaitNode(MMObjectNode node) {
		this.node=node;
		this.number=node.getIntValue("number");
	}

	public synchronized void doWait(int time) {
		try {
			wait(time);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public boolean doNotifyCheck(int wantednumber) {
		if (number==wantednumber) {
			doNotify();
			return(true);
		} else {
			return(false);
		}
	}

	public synchronized void doNotify() {
		notify();
	}

}

