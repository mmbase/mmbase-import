/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.util;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a helper class to build paths for mmbase queries. You set the steps in the path, and then it checks if the
 * same builder occurs more than one time, and if so, the relevant step names are suffixed with an index number.
 * 
 * @author Ernst Bunders
 * 
 */
public class PathBuilder {
	private String[] steps;
	private String path="";

	public PathBuilder(String[] steps) {
		if(steps == null || steps.length == 0){
			throw new NullPointerException("the array of steps is null or empty");
		}
		this.steps = steps;

		// convert the steps. if a step occurs more than one time, the subsequent
		// occurrences are suffixed with an increasing number
		Map<String, Integer> m = new HashMap<String, Integer>();
		for (int i = 0; i < steps.length; i++) {
			if (m.containsKey(steps[i])) {
				m.put(steps[i], m.get(steps[i]) + 1);
				steps[i] = steps[i] + m.get(steps[i]);
			} else {
				m.put(steps[i], 0);
			}
		}
		
		
		//create the path
		for (int i = 0; i < steps.length; i++) {
			path = path + steps[i];
			if (i < (steps.length - 1)) {
				path = path + ",";
			}
		}
	}

	public String getPath() {
		return path;
	}

	/**
	 * retrieve the 'safe' name of a certain step. the index is zero based.
	 * @param index
	 * @return
	 */
	public String getStep(int index) {
		if (index < steps.length && index >= 0) {
			return steps[index];
		} else {
			throw new IndexOutOfBoundsException(String.format("there are only %s steps, so index %s is illegal",
					steps.length, index));
		}
	}

}
