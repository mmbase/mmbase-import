/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.StringTokenizer;

public class Matcher {
	public static void main(String args[]) {
		
		System.out.println("Threshold = "+Matcher.match("Wat is MMBase in helemsnaam?","Wat is MMBase?"));
		System.out.println("Threshold = "+Matcher.match("Wat is Xalan?","Wat is MMBase?"));
	}


	/**
	 * This method will match two string. It will return a treshold,
	 * 0 indicates absolutely no match, 1 means perfect match.
	 */
	static public float match(String firstString, String secondString) {
		StringTokenizer st1 = new StringTokenizer(firstString,"\".,:!? \n\t");
		StringTokenizer st2 = new StringTokenizer(secondString,"\".,:!? \n\t");
		int numberOffWordsFirstString=st1.countTokens();
		int numberOffWordsSecondString=st2.countTokens();
		int matchedWords=0;
 
		while (st1.hasMoreElements()) {
			String wordFirstString = (String)st1.nextToken();
			st2 = new StringTokenizer(secondString,"\".,:!? \n\t");

			while (st2.hasMoreTokens()) {
				String wordSecondString = (String)st2.nextToken();
				//System.out.println("matching "+wordFirstString+" ?= "+wordSecondString);
				if (wordFirstString.equals(wordSecondString)) {
					matchedWords++;
					break;
				}
				
			}
		}	
		System.out.println("firstString = "+firstString);
		System.out.println("secondString = "+secondString);
		System.out.println("matchedWords = "+matchedWords);
		return (float)matchedWords/(((float)numberOffWordsFirstString+(float)numberOffWordsSecondString)/2);
	}
}
