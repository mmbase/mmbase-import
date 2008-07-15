package nl.vpro.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StringUtil {
	
	private static final Log log = LogFactory.getLog(StringUtil.class.getName());

	/**
	 * This method is like {@link String#startsWith(String)} but it takes a
	 * regular expression pattern as attribute
	 * 
	 * @param string
	 *            The string to test
	 * @param regularExpression
	 *            the expression to match the string to
	 * @return true when the pattern is found in the string and the match starts
	 *         at position 0;
	 */
	public static boolean startsWith(String string, String regularExpression) {
		IllegalParameterException.check(string, String.class, "string");
		IllegalParameterException.check(regularExpression, String.class, "regularExpression");
		Pattern p = Pattern.compile(regularExpression);
		Matcher m = p.matcher(string);
		if (m.find() == true) {
			if (m.start() == 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method is like {@link String#endsWith(String)} but it takes a
	 * regular expression pattern as attribute
	 * 
	 * @param string
	 *            The string to test
	 * @param regularExpression
	 *            the expression to match the string to
	 * @return true when the pattern is found in the string and the match starts
	 *         at position 0;
	 */
	public static boolean endsWith(String string, String regularExpression) {
		IllegalParameterException.check(string, String.class, "string");
		IllegalParameterException.check(regularExpression, String.class, "regularExpression");
		Pattern p = Pattern.compile(regularExpression);
		Matcher m = p.matcher(string);
		boolean found = m.find();
		log.info(""+found);
		if (found) {
			log.info(""+m.end());
			if (m.end() == string.length()) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isValidBoolean (String booleanValue, boolean nullAllowed){
		if(! nullAllowed && booleanValue == null){
			return false;
		}
		if(booleanValue.toLowerCase().equals("true") || booleanValue.toLowerCase().equals("false")){
			return true;
		}
		return false;
	}

}
