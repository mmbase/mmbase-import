//package nl.vpro.mmbase.util;
package org.mmbase.util;

import java.lang.*;
import java.util.*;
import org.mmbase.module.Module;
//import nl.vpro.mmbase.util.RandomPool;
import org.mmbase.util.RandomPool;

//import vpro.james.coreserver.*;

/**
	Password Generator module, based on the code of Arnold G. Reinhold, Cambridge, MA, USA
	This code is under the GNU License as specified by the original author
	@author Rico Jansen
	@version 9 Jan 1997
*/

public class PasswordGenerator extends Module implements PasswordGeneratorInterface {

	private static PasswordGenerator PG; // For testing only

	// Pool of random numbers;
	RandomPool ranPool;

	String defaulttemplate=new String("SSSSSS");

	private static String consonants[] = {
			"b","c","d","f","g","h","j","k","l","m",
			"n","p","qu","r","s","t","v","w","x","z",
			"ch","cr","fr","nd","ng","nk","nt","ph","pr","rd",
			"sh","sl","sp","st","th","tr"
		};
	private static String vowels[] = { "a","e","i","o","u","y" };


	public PasswordGenerator() {
		ranPool=new RandomPool();
	}

	public void onload() {
	}

	public void reload() {
		defaulttemplate=getInitParameter("template");
		if (defaulttemplate==null) defaulttemplate=new String("SSSSSS");
	}

	public void init() {
		defaulttemplate=getInitParameter("template");
		if (defaulttemplate==null) defaulttemplate=new String("SSSSSS");
	}

	public void unload() {
	}
	public void shutdown() {
	}

	public static void main(String args[]) {
		PG=new PasswordGenerator();
		System.out.println("Password "+PG.getPassword());
		System.out.println("Password "+PG.getPassword("SSS 9 SSS"));
		System.out.println("Password "+PG.getPassword("SSSS"));
		System.out.println("Password "+PG.getPassword("SSSSS"));
		System.out.println("Password "+PG.getPassword("SSSSSS"));
		System.out.println("Password "+PG.getPassword("CCC CCC CCC"));
		System.out.println("Password "+PG.getPassword("HHHH HHHH HHHH"));
		System.out.println("Password "+PG.getPassword("AAAAA AAAAA AAAAA"));
		System.out.println("Password "+PG.getPassword("99999 99999 99999"));
		System.out.println("Password "+PG.getPassword("66666 66666 66666"));
	}

	/** x mod y function. Returns positive modulus only. agr */
	private int mod (long x, long y) {
		if (x<0) x=-x;
		if (y<0) y=-y;
		return (int) (x % y);
	}

	public String getPassword() {
		return(getPassword(defaulttemplate));
	}

	public String getPassword(String template) {
		int len;
		boolean next=true;
		StringBuffer pwd=new StringBuffer();

		len=template.length();
		for (int i=0;i<len;i++) {
			ranPool.stir(i*93762+49104); // Increasing value
			next=addChar(pwd,template,i,next);
		}
		return(pwd.toString());
	}

	/** Add a random character to the textArea as specified by the template */
	private boolean addChar(StringBuffer password,String template, int ntmpl,boolean consonantNext) {
		int ch = 0;
		char tmplChar;
		String charsOut=null;

		if (ntmpl >= template.length()) {
			consonantNext = true;
			return(consonantNext);
		}
		tmplChar = template.charAt(ntmpl);

		if (tmplChar == ' ') {
			ch = ' ';

		} else if (tmplChar == 'A') {		//random letter
			ch = mod(ranPool.value(), 26) + (int) 'a';

		} else if (tmplChar == 'C') {		//random alphanumeric [0-9,A-Z]
			ch =  mod(ranPool.value(), 36);
			if (ch <10) ch = ch + (int) '0';
			else ch = ch + (int) 'a' - 10;

		} else if (tmplChar == 'H') {		//random hex digit
			ch =  mod(ranPool.value(), 16);
			if (ch <10) ch = ch + (int) '0';
			else ch = ch + (int) 'a' - 10;

		} else if (tmplChar == 'S') {		//random syllable
			if (consonantNext) {
				charsOut = consonants[mod(ranPool.value(), consonants.length)];
				if (charsOut != "qu") consonantNext = false;
			} else {
				charsOut = vowels[mod(ranPool.value(), vowels.length)];
				consonantNext = true;
			}

		} else if (tmplChar == '6') {		//random dice throw
			ch = mod(ranPool.value(), 6) + (int) '1';

		} else if (tmplChar == '9') {		//random digit
			ch = mod(ranPool.value(), 10) + (int) '0';

		} else {
			return(consonantNext);
		}
		
		if (charsOut==null) {
			charsOut = String.valueOf((char)ch);
			consonantNext = true;
		}
		password.append(charsOut);
		return(consonantNext);
	}

	public String getModuleInfo() {
		return("Password Generator module, based on the code of Arnold G. Reinhold, Cambridge, MA, USA. Author Rico Jansen");
	}
}

