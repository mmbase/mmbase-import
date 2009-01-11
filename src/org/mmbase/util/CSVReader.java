package org.mmbase.util;

import java.io.*;
import java.util.*;
import java.nio.charset.Charset;

import org.mmbase.util.ResourceLoader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Reads Comma Separated Values (CSV). Uses utf-8 as the default characterset. 
 * The csv should live somewhere in the webroot.
 * The actual csv parsing is done with examplecode from O'Reilly's Java Cookbook 
 * written by Ian F. Darwin: "\"([^\"]+?)\",?|([^,]+),?|,".
 * 
 * @author Andr\U00e9 vanToly &lt;andre@toly.nl&gt;
 * @version $Rev$
 */
public class CSVReader {
	
    private static Logger log = Logging.getLoggerInstance(CSVReader.class);
	
	private static String filename;
	private static String delimiter = ",";
    private static String charset = "UTF-8";
	private static Pattern csv_pattern = Pattern.compile("\"([^\"]+?)\",?|([^,]+),?|,");
	public List lines = new ArrayList(); // list with rows as strings
	public List header = new ArrayList();
	public Map<Integer, ArrayList> rows = new HashMap();   // contains rows as arrays

    /**
     * Constructor
     */
	public CSVReader(String filename, String delimiter, String charset) {
       this.filename = filename;
       this.delimiter = delimiter;
       this.charset = charset;
	   readCSV(filename, delimiter, charset);
	}
    
	public CSVReader(String filename, String delimiter) {
        this(filename, delimiter, charset);
	}
    
    public CSVReader(String filename) {
        this(filename, delimiter, charset);
    }    
    
	public CSVReader() {
       // empty, needed for functionset?
    }    
    
    /**
     * Reads the contents of the CSV file. The values are stored in arrays.
     * @param filename  CSV file
     * @param delimiter the komma or something else TODO!
     * @param charset   by default UTF-8
     */
    public void readCSV(String filename, String delimiter, String charset) {
		log.debug("filename: " + filename + ", delimiter: " + delimiter + ", charset: " + charset);
        try {
            java.net.URI fileuri = ResourceLoader.getWebRoot().getResource(filename).toURI();
			Charset cs = Charset.forName(charset);
            log.info("Trying to parse CSV-file: " + fileuri);
			if (!",".equals(delimiter))	this.csv_pattern = compilePattern(delimiter);
            InputStream is = new FileInputStream(new File(fileuri));
            InputStreamReader isr = new InputStreamReader(is, cs);
            BufferedReader in = new BufferedReader(isr);
            String line;
            lines.clear();   // make sure they are empty
            while((line = in.readLine()) != null) {
                log.debug("line: " + line);
                lines.add(line);
            }
            in.close();
            if (lines.size() > 0) {
				header.clear();
				rows.clear();
				header = parse( (String) lines.get(0) );	// first could be header
                for (int i = 0; i < lines.size(); i++) {
                    rows.put(i, parse( (String) lines.get(i)) );
                }
            }
        } catch(java.nio.charset.IllegalCharsetNameException ice) {
            log.error("Illegal charset name: " + ice);
        } catch(java.nio.charset.UnsupportedCharsetException uce) {
            log.error("Unsupported charset: " + uce);
        } catch(java.net.URISyntaxException ue) {
            log.error("Error in file or path syntax: " + ue);
        } catch (IOException ioe) {
            log.error("IOException, probably file '" + filename + "' not found: " + ioe);
        }
    }
	
    /**
     * Returns the element at the given row and column.
     * @param row the element row
     * @param col the element column
     * @return the element as a String.
     */
    public String getElement(int r, int c) {
        ArrayList row = (ArrayList) rows.get(r);
        String value = (String) row.get(c);
        
        return value;
    }

    public Map getValues(String filename) {
        return getValues(filename, null, null);
    }
    
    public Map getValues(String filename, String delimiter) {
        return getValues(filename, delimiter, null);
    }

    /**
     * Map to use in a taglib function. Calls {@link #readCSV} and returns csv-file rows.
     * @param filename  CSV file
     * @return map with an array per row with values
     */
    public Map getValues(String filename, String delimiter, String charset) {
		if (delimiter == null || "".equals(delimiter)) delimiter = this.delimiter;
		if (charset == null || "".equals(charset)) charset = this.charset;
        readCSV(filename, delimiter, charset);
        return rows;
    }

    /**
     * Returns the number of rows in the CVS file.
     */
    public int size() {
        return rows.size();
    }

	/**
     * Parse one line - a csv row - at the time.
     * This method's logic is derived from O'Reilly's Java Cookbook written by 
     * Ian F. Darwin.
     *
     * @param  line row in a csv file
     * @return List of Strings, minus their double quotes
     */
    private ArrayList parse(String line) {
        ArrayList list = new ArrayList();
        //Pattern p = Pattern.compile(CSV_PATTERN);
        Matcher m = csv_pattern.matcher(line);
        while (m.find()) {
            //log.debug("found" + m.groupCount());
            String match = m.group();
            if (match == null) {
                break;
            }
            if (match.endsWith(",")) {
                match = match.substring(0, match.length() - 1);
            }
            if (match.startsWith("\"")) {
                match = match.substring(1, match.length() -1);
            }
            if (match.length() == 0)
                match = null;
            log.debug("Found match: " + match);
            list.add(match);
        }
        return list;
    }
    
	/**
	 * Compiles the pattern with a different delimiter
	 *
	 * @param  
	 * @return 
	 */
	private Pattern compilePattern(String delimiter)  {
		StringBuilder sb = new StringBuilder();
		sb.append("\"([^\"]+?)\"").append(delimiter).append("?|([^").append(delimiter);
		sb.append("]+)").append(delimiter).append("?|").append(delimiter);
		
		try {
 		    Pattern p = Pattern.compile(sb.toString());
			return p;
		} catch (PatternSyntaxException pse) {
			log.error("Can not use this delimiter '" + delimiter + "', it causes an exception: " + pse);
			return null;
		}
	}
	
	/**
	 * Description of the CSVReader, reports its configuration f.e.
	 *
	 * @return some information about CSVReader, like config etc.
	 */
	public String getDescription() {
		StringBuilder msg = new StringBuilder("Hi, I'm the CSVReader. ");
		msg.append("My configuration is as follows:");
		msg.append("\nfile to import: " + filename);
		msg.append("\ndelimiter: " + delimiter);
		msg.append("\ncharset: " + charset);
		return msg.toString();
	}

}
