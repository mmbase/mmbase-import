/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
/**
* Escapes and Unescapes undesirable characters using % (URLEncoding) but
* keeps param makers alive (needs to be checked if used and if it can't be
 * combined in URLEscape).
*/
public class URLParamEscape {
	
	static boolean isacceptable[] =
	{
		false, false, false, false, false, false, false, false,	//  !"#$%&' 
		false, false, true, false, true, true, true, false,		// ()*+,-./
		true, true, true, true, true, true, true, true,			// 01234567
		true, true, true, false, false, false, false, false,	// 89:;<=>? 
		true, true, true, true, true, true, true, true,			// @ABCDEFG 
		true, true, true, true, true, true, true, true,			// HIJKLMNO 
		true, true, true, true, true, true, true, true,			// PQRSTUVW 
		true, true, true, false, false, false, false, true,		// XYZ[\]^_ 
		false, true, true, true, true, true, true, true,		// `abcdefg 
		true, true, true, true, true, true, true, true,			// hijklmno 
		true, true, true, true, true, true, true, true,			// pqrstuvw 
		true, true, true, false, false, false, false, false		// xyz{|}~  
	};
	static char hex[] = {
		'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
	};
	
	static char HEX_ESCAPE='%';
	
	public static String escapeurl(String str) {
		byte buf[];
		int i,a;
		StringBuffer esc=new StringBuffer();
	
		buf=new byte[str.length()];
		str.getBytes(0,str.length(),buf,0);

		for (i = 0; i<str.length();i++) {
			a = (int)buf[i] & 0xff;
			if (a>=32 && a<128 && isacceptable[a-32]) {
				esc.append((char)a);
			} else {
				esc.append(HEX_ESCAPE);
				esc.append(hex[a >> 4]);
				esc.append(hex[a & 15]);
			}
		}
		return(esc.toString());
	}
	
	private static char from_hex(char c)
	{
		return (char)(c >= '0' && c <= '9' ? c - '0'
			: c >= 'A' && c <= 'F' ? c - 'A' + 10
			: c - 'a' + 10);			/* accept small letters just in case */
	}
	
	public static String unescapeurl(String str)
	{
		int i;
		char j,t;
		StringBuffer esc=new StringBuffer();

		if (str!=null) {
			for (i=0;i<str.length();i++) {
				t=str.charAt(i);
				if (t==HEX_ESCAPE) {
					t=str.charAt(++i);
					j=(char)(from_hex(t)*16);
					t=str.charAt(++i);
					j+=from_hex(t);
					esc.append(j);
				} else {
					esc.append(t);
				}
			}	
		} else {
			System.out.println("Unescapeurl -> Bogus parameter");
		}
		return (esc.toString());
	}

	public static void main(String args[]) {
		for (int i=0;i<args.length;i++) {
			System.out.println("Original : '"+args[i]+"'");
			System.out.println("Escaped : '"+escapeurl(args[i])+"'");
			System.out.println("Unescaped again : '"+unescapeurl(escapeurl(args[i]))+"'");
		}

	}
}
