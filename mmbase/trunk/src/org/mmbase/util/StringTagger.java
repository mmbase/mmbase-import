/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;

/**
* StringTagger, Creates a object with tags and fields from a String
* its ideal for name value pairs and name value pairs with multi
* values. It also provides support for quoted values.
*/
public class StringTagger {

	private Hashtable tokens;
	private Hashtable multitokens;
	private char TagStart;
	private char TagSeperator;
	private char FieldSeperator;
	private char QuoteChar;
	private String startline="";

	/**
	* Creates a StringTag for the given line.
	*
	* @param line : to be tagged line
	* @param TagStart : Seperator for the Tags
	* @param TagSeperator : Seperator inside the Tag (between name and value)
	* @param FieldSeperator : Seperator inside the value 
	* @param QuoteChar : Char used if a quoted value
	*
	* Example : StringTagger("cmd=lookup names='Daniel Ockeloen, Rico Jansen'",' ','=',','\'')
	*/
	public StringTagger(String line, char TagStart, char TagSeperator,char FieldSeperator, char QuoteChar) {
		this.TagStart=TagStart;
		this.startline=line;
		this.TagSeperator=TagSeperator;
		this.FieldSeperator=FieldSeperator;
		this.QuoteChar=QuoteChar;
		tokens = new Hashtable();
		multitokens = new Hashtable();
		createTagger(line);
	}

	/**
	* creates a default tagger, with ' ','=',',','"'
	* as inputs.
	*/
	public StringTagger(String line) {
		this(line,' ','=',',','"');
	}

	void createTagger(String line) {
		StringTokenizer tok2=new StringTokenizer(line+TagStart,""+TagSeperator+TagStart,true);
		String part,tag,prevtok,tok;
		boolean isTag,isPart,isQuoted;

		isTag=true;
		isPart=false;
		isQuoted=false;
		prevtok="";
		tag=part="";
//		System.out.println("Tagger -> |"+TagStart+"|"+TagSeperator+"|"+QuoteChar+"|");
		while(tok2.hasMoreTokens()) {
			tok=tok2.nextToken();
//			System.out.println("tagger tok ("+isTag+","+isPart+","+isQuoted+") |"+tok+"|"+prevtok+"|");
			if (tok.equals("=")) {
				if (isTag) {
					tag=prevtok;
					isTag=false;
				} else {
					if (!isQuoted) {
						splitTag(tag+"="+part);
						isTag=true;
						isPart=false;
						part="";
					} else {
						part+=tok;
					}
				}
			} else if (tok.equals(" ")) {
				if (isPart) {
					if (isQuoted) {
						part+=tok;
					} else {
						if (!prevtok.equals(" ")) {
							splitTag(tag+"="+part);
							isTag=true;
							isPart=false;
							part="";
						}
					}
					prevtok=tok;
				}
			} else {
				if (!isTag) isPart=true;
//				System.out.println("isTag "+isTag+" "+isPart);
				if (isPart) {
					if (isQuoted) {
						// Check end quote
						if (tok.charAt(tok.length()-1)==QuoteChar) {
							isQuoted=false;
						}
						part+=tok;
					} else {
						if (tok.charAt(0)==QuoteChar && !(tok.charAt(tok.length()-1)==QuoteChar)) {
							isQuoted=true;
						}
						part+=tok;
					}
				}
//				System.out.println("isTag "+isTag+" "+isPart+" "+isQuoted);
				prevtok=tok;
			}
		}
	}

	/**
	* handles and splits the tokens up and if needed create multivalues
	*/
	void splitTag(String Tag) { 
		int	tagPos=Tag.indexOf(TagSeperator);
		String name=Tag.substring(0,tagPos);
		String result=Tag.substring(tagPos+1);
//		System.out.println("SplitTag |"+name+"|"+result+"|");

		if (result.length()>1 && result.charAt(0)==QuoteChar && result.charAt(result.length()-1)==QuoteChar) {
			result=result.substring(1,result.length()-1);
		}
		tokens.put(name,result);
		
		StringTokenizer tok = new StringTokenizer(result,""+FieldSeperator);
		Vector Multi = new Vector();
		// If quoted, strip the " " from beginning and end ?
		while (tok.hasMoreTokens()) {
			Multi.addElement(tok.nextToken());
		}

		multitokens.put(name,Multi);
	}

	/**
	* returns a Enumeration of the name keys
	*/
	public Enumeration keys() {
		return(tokens.keys());
	}

	/** 
	 * toString
	 */
	public String toString() {
		String content="[";
		String key="";
		for (Enumeration e = keys();e.hasMoreElements();) {
			key=(String)e.nextElement();
			content+="<"+key;
			content+="="+Values(key);
			content+=">";	
		}
		content+="]";
		return content;
	}

	/**
	* returns a Enumeration of the values without multisplit
	*/
	public Enumeration elements() {
		return(tokens.elements());
	}

	/**
	* returns a Enumeration of the values as Vectors that contain
	* the seperated values
	*/
	public Enumeration multiElements(String token) {
		Vector tmp=(Vector)multitokens.get(token);
		if (tmp!=null) {
			return(tmp.elements());
		} else {
			return(null);	
		}	
	}

	/**
	 */
	public boolean containsKey (Object ob) {
		return tokens.containsKey(ob);
	}

	/**
	 */
	public Object get(Object ob) {
		return tokens.get(ob);	
	}

	/**
	* returns the values as Vectors that contain
	* the seperated values
	*/
	public Vector Values(String token) {
		Vector tmp=(Vector)multitokens.get(token);
		if (tmp!=null) {
			return(tmp);
		} else {
			return(null);	
		}	
	}


	/**
	* returns the values as String that contain
	* the orig. String
	*/
	public String ValuesString(String token) {
		/*
		Vector tmp=(Vector)multitokens.get(token);
		if (tmp!=null) {
			String tmp2="";
			for (Enumeration e = tmp.elements();e.hasMoreElements();) {
				if (tmp2.equals("")) {
					tmp2+=(String)e.nextElement();
				} else {
					tmp2+=","+(String)e.nextElement();
				}
			}
    		tmp2 = Strip.DoubleQuote(tmp2,Strip.BOTH); 
			return(tmp2);
		} else {
			return(null);	
		}	
		*/
		return(startline);
	}

	/**
	* returns the value (first) as string
	*/
	public String Value(String token) {
		String val;
		Vector tmp=(Vector)multitokens.get(token);
		if (tmp!=null && tmp.size()>0) {
			val=(String)tmp.elementAt(0);
			if (val!=null) {
    			val = Strip.DoubleQuote(val,Strip.BOTH); // added stripping daniel
				return(val);
			} else {
				return(null);	
			}
		} else {
			return(null);	
		}	
	}


	public void setValue(String token,String val) {
		Vector newval=new Vector();
		newval.addElement(val);
		multitokens.put(token,newval);
	}

	public void setValues(String token,Vector values) {
		multitokens.put(token,values);
	}

	public static void main(String args[]) {
		StringTagger tag=new StringTagger(args[0]);
	}

	public int hashCode() {
		return(multitokens.hashCode());
	}
}
