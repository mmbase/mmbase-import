package org.mmbase.util;

import java.io.*;
import java.util.*;
import java.nio.charset.Charset;


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
 * @todo I don't think it supports multiple line records
 * @todo The entire CSV is stored in memory twice (in public structures)
 * @todo The CSV is stored in a Map with List values, it could more logically be stored in a List of List
 * @todo It would also be possible to parse a CSV without storing it at all, so that you can also
 * parse really huge ones.
 * @todo The 'header' member is unused and public.
 * @todo Test-cases?
 *
 * @author Andr\U00e9 vanToly &lt;andre@toly.nl&gt;
 * @version $Id$
 */
public class CSVReader {

    private static final Logger log = Logging.getLoggerInstance(CSVReader.class);

    private final String filename;
    private final String delimiter;
    private String charset = "UTF-8";
    private Pattern csv_pattern = Pattern.compile("\"([^\"]+?)\",?|([^,]+),?|,");
    public List<String> lines              = new ArrayList<String>();    // list with rows as strings
    public List<String> header             = new ArrayList<String>();
    public Map<Integer, List<String>> rows = new HashMap<Integer, List<String>>();    // contains rows as arrays

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
        this(filename, delimiter, "UTF-8");
    }

    public CSVReader(String filename) {
        this(filename, ",", "UTF-8");
    }


    /**
     * Reads the contents of the CSV file. The values are stored in arrays.
     * @param filename  CSV file
     * @param delimiter the comma or something else TODO!
     * @param charset   by default UTF-8
     * @todo the params are members too, which is a bit confusing for a non-static method.
     *
     */
    public final void readCSV(String filename, String delimiter, String charset) {
        if (log.isDebugEnabled()) {
            log.debug("filename: " + filename + ", delimiter: " + delimiter + ", charset: " + charset);
        }
        try {
            Charset cs = Charset.forName(charset);
            log.info("Trying to parse CSV-file: " + filename);
            if (!",".equals(delimiter)) this.csv_pattern = compilePattern(delimiter);
            InputStream is = ResourceLoader.getWebRoot().getResourceAsStream(filename);
            InputStreamReader isr = new InputStreamReader(is, cs);
            BufferedReader in = new BufferedReader(isr);
            String line;
            lines.clear();   // make sure they are empty
            while((line = in.readLine()) != null) {
                if (log.isDebugEnabled()) log.debug("line: " + line);
                lines.add(line);
            }
            in.close();
            if (lines.size() > 0) {
                header.clear();
                rows.clear();
                header = parse( lines.get(0) );    // first could be header
                int i = 0;
                for (String l : lines) {
                    rows.put(i++, parse(l));
                }
            }
        } catch(java.nio.charset.IllegalCharsetNameException ice) {
            log.error("Illegal charset name: " + ice);
        } catch(java.nio.charset.UnsupportedCharsetException uce) {
            log.error("Unsupported charset: " + uce);
        } catch (IOException ioe) {
            log.error("IOException, probably file '" + filename + "' not found: " + ioe);
        }
    }

    /**
     * Returns the element at the given row and column.
     * @param r the element row
     * @param c the element column
     * @return the element as a String.
     */
    public String getElement(int r, int c) {
        List<String> row = rows.get(r);
        String value = row.get(c);

        return value;
    }

    public Map<Integer, List<String>> getValues(String filename) {
        return getValues(filename, null, null);
    }

    public Map<Integer, List<String>> getValues(String filename, String delimiter) {
        return getValues(filename, delimiter, null);
    }

    /**
     * Map to use in a taglib function. Calls {@link #readCSV} and returns csv-file rows.
     * @param filename  CSV file
     * @return map with an array per row with values
     * @todo   I don't understand why it does not return List<List<String>> in stead.
     */
    public Map<Integer, List<String>> getValues(String filename, String delimiter, String charset) {
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
    private List<String> parse(String line) {
        List<String> list = new ArrayList<String>();
        Matcher m = csv_pattern.matcher(line);
        while (m.find()) {
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
            if (log.isDebugEnabled()) log.debug("Found match: " + match);
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
            return Pattern.compile(sb.toString());
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
        msg.append("\nfile to import: ").append(filename);
        msg.append("\ndelimiter: ").append(delimiter);
        msg.append("\ncharset: ").append(charset);
        return msg.toString();
    }

}
