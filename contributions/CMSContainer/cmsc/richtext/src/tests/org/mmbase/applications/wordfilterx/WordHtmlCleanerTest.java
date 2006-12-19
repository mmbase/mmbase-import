package org.mmbase.applications.wordfilterx;

import junit.framework.TestCase;

import java.util.regex.Pattern;

import org.mmbase.applications.wordfilter.WordHtmlCleaner;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class WordHtmlCleanerTest extends TestCase {

    /**
     * Test for jira issue NIJ-385. Bold tags with only spaces in it where reduced to a self closing tag, which will
     * render al following text bold in IE.<br/>
     * E.g. The following html <code>&lt;b&gt; &lt;/b&gt;</code> would be cleaned as <code>&lt;b/&gt;</code>. 
     */
    public void testEmptyTagsCleaner() {
        String html = "<b> </b><i> </i><u> </u> <B> </B> <U> </U> <I> </I> ";

        String cleanedHtml = WordHtmlCleaner.cleanHtml(html);
        Pattern p = Pattern.compile("<[bBiIuU]\\s*/>");
        assertFalse("Cleaned html still has some empty tags left!" + cleanedHtml, p.matcher(cleanedHtml).find());
    }
    
    /**
     * As specified in NIJ-780, only a single <br/> should be the result of a replaced <p></p>
     */
    public void testReplaceParagraphSingleBr() {
    	doTestReplace("<p>This is paragraph</p><p>paragraph 2</p>", "This is paragraph<br/>paragraph 2");
    }
	/**
     * As specified in NIJ-780, h1 till h7 should be replaced by a <b>...</b><br/>
     */
    public void testReplaceH1() {
    	for(int count = 1; count <= 7; count++) {
    		doTestReplace("<h"+count+">Header</h"+count+"><p>paragraph 2</p>", "<b>Header</b><br/>paragraph 2");
    	}
    }

    public void testReplaceHeaderEnd() {
    	for(int count = 1; count <= 7; count++) {
    		doTestReplace("<h"+count+">Header</h"+count+">", "<b>Header</b>");
    	}
    }



    private void doTestReplace(String input, String expectedOutput) {

    	String cleanedHtml = WordHtmlCleaner.cleanHtml(input);
        assertEquals(expectedOutput,cleanedHtml);
	}

}
