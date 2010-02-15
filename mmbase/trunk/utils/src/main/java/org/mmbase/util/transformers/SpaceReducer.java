/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mmbase.util.logging.*;

/**
 * Replace 1 or more spaces by 1 space, and 1 or more newlines by 1
 * newline. Any other combination of newlines and spaces is replaced
 * by one newline.
 *
 * Except if they are in between "&lt;pre&gt;" and "&lt;/pre&gt;". (Note: perhaps this last behaviour should be made
 * configurable).
 *
 * @author Michiel Meeuwissen
 * @author Ernst Bunders
 * @since MMBase-1.7
 * @version $Id$
 */

public class SpaceReducer extends BufferedReaderTransformer implements CharTransformer {

    private static final Logger log = Logging.getLoggerInstance(SpaceReducer.class);

    @Override
    protected boolean transform(PrintWriter bw, String line, Status status) {

        SpaceReducerStatus srStatus = (SpaceReducerStatus)status;
        List<Tag> tagsToPass = srStatus.getTagsToPass();
        boolean result = false;

        if(!line.trim().equals("") || srStatus.getCurrentlyOpen() != null){
            bw.write(line);
            result = true;
        }
        if(srStatus.getCurrentlyOpen() != null){
            //look for a closing tag.
            srStatus.getCurrentlyOpen().setLine(line);
            if(srStatus.getCurrentlyOpen().hasClosed()){
                srStatus.setCurrentlyOpen(null);
            }
        }else{
            //look for an opening tag
            for (Tag tag : tagsToPass) {
                tag.setLine(line);
                if(tag.hasOpened()){
                    srStatus.setCurrentlyOpen(tag);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * This was the original, now unused implementation (not efficient enough)
     */
    protected Writer transform2(Reader r, Writer w) {

        int space = 1;  // 'open' spaces (on this line)
        int nl    = 1;  // 'open' newlines
        // we start at 1, rather then 0, because in that way, all leading space is deleted too

        StringBuilder indent = new StringBuilder();  // 'open' indentation of white-space
        int l = 0; // number of non-white-space (letter) on the current line

        int lines = 0; // for debug: the total number of lines read.
        try {
            log.debug("Starting spacereducing");
            int c = r.read();
            while (c != -1) {
                if (c == '\n' || c == '\r' ) {
                    if (nl == 0) w.write('\n');
                    nl++;
                    l = 0;
                    space = 0; indent.setLength(0);
                } else if (Character.isWhitespace((char) c)) {
                    if (space == 0 && l > 0) w.write(' ');
                    if (l == 0) indent.append((char) c);
                    space++;
                } else {
                    if (l == 0 && space > 0) {
                        w.write(indent.toString());
                        indent.setLength(0);
                    }
                    space = 0; lines += nl; nl = 0; l++;
                    w.write(c);
                }
                c = r.read();
            }
            log.debug("Finished: read " + lines + " lines");
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
    }

    @Override
    public String toString() {
        return "SPACEREDUCER";
    }

    /**
     * this is a helper class that can check if a tag was opened or closed in a line of text
     * It first removes all bodyless versions of the tag from the line, and then counts all opening and
     * closing occurrences of the tag.
     * This will not work if an opening or closing tag is partly written on the next line, so it's not perfect.
     * <ul>
     * <li>have no body
     * <li>can be opened and closed multiple times in one line.
     * </ul>
     * @author ebunders
     *
     */
    protected static class Tag{
        private boolean hasOpened = false;
        private boolean hasClosed = false;
        private Pattern openingPattern;
        private Pattern closingPattern;
        private Pattern noBodyPattern;
        private String name;

        public Tag(String name){
            openingPattern = Pattern.compile("<[\\s]*"+name+"(\\s+[a-zA-Z]+\\=\"[\\S]+\")*\\s*>", Pattern.CASE_INSENSITIVE);
            closingPattern = Pattern.compile("<[\\s]*/\\s*"+name+"\\s*>", Pattern.CASE_INSENSITIVE);
            noBodyPattern = Pattern.compile("<[\\s]*"+name+"\\s+([a-zA-Z]+\\=\"[\\S]+\")*\\s*/\\s*>", Pattern.CASE_INSENSITIVE);
            this.name=name;
        }

        public void setLine(String line){
            //remove the bodyless versions of the tag from this line (if they exist, which they should not)
            line = removeTagsWithoutBody(line);

            //count the opening and closing versions of the tag
            int opening = countOccurences(openingPattern, line);
            int closing = countOccurences(closingPattern, line);
            hasOpened = opening > closing;
            hasClosed = closing > opening;
        }

        private int countOccurences(Pattern pattern, String line) {
            Matcher m = pattern.matcher(line);
            int counter = 0;
            while(m.find()){
                counter ++;
                line = line.substring(m.end(), line.length());
                m = pattern.matcher(line);
            }
            return counter;
        }

        /**
         * remove all the occurrences of bodyless versions of the tag
         * they should not be there, but for safety
         *
         * @param line
         * @return
         */
        private String removeTagsWithoutBody(String line) {
            Matcher m = noBodyPattern.matcher(line);
            while(m.find()){
                line = line.substring(0, m.start()) + line.substring(m.end(), line.length());
                m = noBodyPattern.matcher(line);
            }
            return line;
        }

        public boolean hasOpened(){
            return hasOpened;
        }

        public boolean hasClosed(){
            return hasClosed;
        }
        public String toString() {
            return name;
        }
    }

    @Override public Status createNewStatus(){
        return new SpaceReducerStatus();
    }

    protected static class SpaceReducerStatus extends Status {
        private final List<Tag> tagsToPass = new ArrayList<Tag>();
        private Tag currentlyOpen = null;

        public SpaceReducerStatus(){
            tagsToPass.add(new Tag("pre"));
            tagsToPass.add(new Tag("textarea"));
        }

        public List<Tag> getTagsToPass() {
            return tagsToPass;
        }

        public Tag getCurrentlyOpen() {
            return currentlyOpen;
        }
        public void setCurrentlyOpen(Tag currentlyOpen) {
            this.currentlyOpen = currentlyOpen;
        }
    }


}
