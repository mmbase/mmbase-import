package org.cmscontainer.tools.htmlcontainer;

public class MathUtil {

	public static int random(int max) {
		return (int)(Math.random()*max);
	}

	public static int random(int min, int max) {
		return (int)(Math.random()*(max-min)+min);
	}

}
