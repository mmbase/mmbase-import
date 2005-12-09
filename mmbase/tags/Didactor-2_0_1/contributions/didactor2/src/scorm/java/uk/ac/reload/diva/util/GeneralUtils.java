/**
 *  RELOAD TOOLS
 *
 *  Copyright (c) 2003 Oleg Liber, Bill Olivier, Phillip Beauvoir
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *  Project Management Contact:
 *
 *  Oleg Liber
 *  Bolton Institute of Higher Education
 *  Deane Road
 *  Bolton BL3 5AB
 *  UK
 *
 *  e-mail:   o.liber@bolton.ac.uk
 *
 *
 *  Technical Contact:
 *
 *  Phillip Beauvoir
 *  e-mail:   p.beauvoir@bolton.ac.uk
 *
 *  Web:      http://www.reload.ac.uk
 *
 */

package uk.ac.reload.diva.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Some useful General Utilities
 *
 * @author Phillip Beauvoir
 * @author Paul Sharples
 * @version $Id: GeneralUtils.java,v 1.2 2005-08-29 00:06:51 azemskov Exp $
 */
public final class GeneralUtils  {
	
	private GeneralUtils() {}
	
	/**
	 * Macintosh OS
	 */
	public static final int MACINTOSH = 0;
	
	/**
	 * Windows XP
	 */
	public static final int WINDOWS_XP = 1;
	
	/**
	 * Windows 98
	 */
	public static final int WINDOWS_9x = 2;
	
	/**
	 * Windows NT
	 */
	public static final int WINDOWS_NT = 3;
	
	/**
	 * Windows 2000
	 */
	public static final int WINDOWS_2000 = 4;
	
	/**
	 * Unix OS
	 */
	public static final int UNIX = 99;
	
	
	/**
	 * Determine the OS we are running on.
	 * @return The OS we are running on:-<br>
	 * 0 - MACINTOSH<br>
	 * 1 - WINDOWS<br>
	 * 2 - UNIX<br>
	 * 3 - NT<br>
	 */
	public static int getOS() {
		String osName = System.getProperty("os.name");
		osName = osName.toLowerCase();
		if(osName.equalsIgnoreCase("windows xp")) return WINDOWS_XP;
		if(osName.equalsIgnoreCase("windows 2000")) return WINDOWS_2000;
		if(osName.equalsIgnoreCase("windows nt")) return WINDOWS_NT;
		if(osName.startsWith("windows")) return WINDOWS_9x;
		if(osName.startsWith("mac os")) return MACINTOSH;
		else return UNIX;
	}
	
    /**
     * @param href The href to parse
     * @return true if href is an external URL
     */
    public static boolean isExternalURL(String href) {
    	if(href != null) {
    		href = href.toLowerCase();
    		return 	href.startsWith("http") ||
					href.startsWith("www") ||
					href.startsWith("ftp:"
				);}
    	return false;
    }


    /**
	 * Get the version of Java we are on.
	 * @return The version
	 */
	public static String getJavaVersion() {
		return System.getProperty("java.version");
	}
	
	/**
	 * Parse a date to a short date string.
	 * @param date The date to parse
	 * @return A String representation of the date such as "25-12-2002"
	 */
	public static String getShortDate(Date date) {
		if(date == null) return "";
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		return df.format(date);
	}
	
	/**
	 * Get the time as it is now.
	 * @return The Time Now
	 */
	public static Date getNow() {
		Calendar c = Calendar.getInstance();
		return c.getTime();
	}

	/**
	 * Returns a class from a jar file that exists in the given folder
	 * @param className The name of the class required
	 * @param jarFile The file of the jar
	 * @return The class or null if could not be created
	 */
	public static Class getClassFromJar(String className, File jarFile) {
		try {
			URL url = jarFile.toURL();
			ClassLoader loader = new URLClassLoader(new URL[] { url });
			return loader.loadClass(className);
		}
		catch(Exception ex) {
			System.out.println("Could not create class: "  + className + " " + ex);
			return null;
		}
	}
	
	/**
	 * Returns a class New Instance from a jar file that exists in the given jar
	 * @param className The name of the class required
	 * @param jarFile The file of the jar
	 * @return An instance of the class or null if it could not be created
	 */
	public static Object getClassInstanceFromJar(String className, File jarFile) {
		Object o = null;
		
		try {
			Class c = getClassFromJar(className, jarFile);
			if(c != null) o = c.newInstance();
		}
		catch(Exception ex) {
			System.out.println("Could not create class instance: "  + className + " " + ex);
			return null;
		}
		return o;
	}
	
}

